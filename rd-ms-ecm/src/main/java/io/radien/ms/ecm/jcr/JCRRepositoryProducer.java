/*
 * Copyright (c) 2021-present radien GmbH. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package io.radien.ms.ecm.jcr;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.ms.ecm.constants.JcrSourceEnum;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.oak.run.osgi.OakOSGiRepositoryFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.servlet.ServletContext;
import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * Class responsible for producing initializing the JCR repository
 *
 * @author Bruno Gama
 */
public @ApplicationScoped class JCRRepositoryProducer implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(JCRRepositoryProducer.class);
    private static final long serialVersionUID = 2126413751365548137L;
    @Inject
    private OAFAccess oaf;
    @Inject
    private ServletContext servletContext;

    private Repository repository = null;
    private String repoHome;
    private String repoSource;
    private String mongoDbName;
    private String mongoDbUri;


    @PostConstruct
    private void init() {
        log.info("[CMS] setting system properties for CMS System");
        repoHome = oaf.getProperty(OAFProperties.SYSTEM_CMS_REPO_HOME_DIR);
        repoSource = oaf.getProperty(OAFProperties.SYSTEM_CMS_REPO_SOURCE);
        mongoDbName = oaf.getProperty(OAFProperties.SYSTEM_CMS_REPO_MONGO_DB_NAME);
        mongoDbUri = oaf.getProperty(OAFProperties.SYSTEM_CMS_REPO_MONGO_DB_URI);
        try {
            initRepository();
        } catch (IOException e) {
            log.error("JCR repository configuration files not found!: {}", e.getMessage());
        } catch (RepositoryException e) {
            log.error("Could not start JCR repository", e);
        }
    }

    /**
     * Method invoke on bean initialization that creates the oak repository instance
     *
     * @throws IOException Exception thrown if error occurs while creating the system directories that will hold cms structure information/ content
     * @throws RepositoryException If Repository instantiation throws and error
     */
    private void initRepository() throws IOException, RepositoryException {
        File repoHomeDir = new File(repoHome);
        FileUtils.forceMkdir(repoHomeDir);

        List<String> configFileNames = determineConfigFileNamesToCopy();
        List<String> configFilePaths = copyConfigs(repoHomeDir, configFileNames);
        repository = createRepository(configFilePaths, repoHomeDir);
    }

    /**
     * Determines which repository configuration file should be used based on the repository source defined in 'application.properties'
     * @return A list with the jcr configuration files that should be used by this instance
     */
    private List<String> determineConfigFileNamesToCopy() {
        List<String> configNames = Lists.newArrayList();
        configNames.add("repository-config.json");

        if (JcrSourceEnum.MONGODB == JcrSourceEnum.valueOf(repoSource)) {
            configNames.add("mongomk-config.json");
            log.info("Using Mongo persistence");
        } else {
            configNames.add("segmentmk-config.json");
        }
        return configNames;
    }

    /**
     * Copies the configs from the 'resources/jcr' folder
     *
     * @param repoHomeDir the repository home directory
     * @param configFileNames the config file names to copy
     * @return The list of copied string
     * @throws IOException Exception thrown if an IO error occurs while operating on those files
     */
    private List<String> copyConfigs(File repoHomeDir, List<String> configFileNames)
            throws IOException {
        List<String> filePaths = Lists.newArrayList();
        for (String configName : configFileNames) {
            File dest = new File(repoHomeDir, configName);
            InputStream source = getClass().getClassLoader().getResourceAsStream("jcr/" + configName);
            copyDefaultConfig(dest, source);
            filePaths.add(dest.getAbsolutePath());
        }
        return filePaths;
    }

    /**
     * Similar to {@link RepositoryConfiguration#copyConfigs} method
     * @param repoConfig the repository configuration file
     * @param defaultRepoConfig the Resource with the default repository configuration
     * @throws IOException Exception thrown if an IO error occurs while operating on those files
     */
    private void copyDefaultConfig(File repoConfig, InputStream defaultRepoConfig)
            throws IOException {
        if (!repoConfig.exists()){
            log.info("Copying default repository config to {}", repoConfig.getAbsolutePath());
            try (FileOutputStream os = FileUtils.openOutputStream(repoConfig)) {
                IOUtils.copy(defaultRepoConfig, os);
            }
        }
    }

    /**
     * Creates a repository instance based on the properties that exist initially in this project resources/jcr/*-config.json files
     *
     * @param repoConfigs the ArrayList of repository configurations
     * @param repoHomeDir the repository physical home directory to store any index/cache data
     * @return the default JCR implementation of a {@link Repository} object
     * @throws RepositoryException Exception thrown if an ERROR occurs while trying to create the Repository object
     */
    private Repository createRepository(List<String> repoConfigs, File repoHomeDir) throws RepositoryException {
        Map<String,Object> config = Maps.newHashMap();
        config.put(OakOSGiRepositoryFactory.REPOSITORY_HOME, repoHomeDir.getAbsolutePath());
        config.put(OakOSGiRepositoryFactory.REPOSITORY_CONFIG_FILE, commaSepFilePaths(repoConfigs));
        config.put(OakOSGiRepositoryFactory.REPOSITORY_SHUTDOWN_ON_TIMEOUT, true);
        config.put(OakOSGiRepositoryFactory.REPOSITORY_ENV_SPRING_BOOT, true);
        config.put(OakOSGiRepositoryFactory.REPOSITORY_TIMEOUT_IN_SECS, 10);

        //Set of properties used to perform property substitution in
        //OSGi configs
        config.put(OAFProperties.SYSTEM_CMS_REPO_HOME_DIR.propKey(), repoHomeDir.getAbsolutePath());
        config.put("oak.mongo.db", mongoDbName);
        config.put("oak.mongo.uri", mongoDbUri);

        //Configures BundleActivator to get notified of
        //OSGi startup and shutdown
        configureActivator(config);

        return new OakOSGiRepositoryFactory().getRepository(config);
    }

    /**
     * OSGi bundle activator
     * @param config the configuration Map
     */
    private void configureActivator(Map<String, Object> config) {
        config.put(BundleActivator.class.getName(), new BundleActivator() {
            @Override
            public void start(BundleContext bundleContext) {
                servletContext.setAttribute(BundleContext.class.getName(), bundleContext);
            }

            @Override
            public void stop(BundleContext bundleContext) {
                servletContext.removeAttribute(BundleContext.class.getName());
            }
        });

    }

    /**
     * Joins a list of strings into a comma separated string
     * @param repoConfigs a List of string containing each repository configuration
     * @return an object with the comma separated configs
     */
    private Object commaSepFilePaths(List<String> repoConfigs) {
        return String.join(",", repoConfigs);
    }

    @Produces
    @RequestScoped
    public Repository create() {

        return repository;
    }


}
