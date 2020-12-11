package io.radien.ms.ecm.legacy;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.servlet.ServletContext;

import org.apache.commons.io.FileUtils;
import org.apache.felix.connect.launch.PojoServiceRegistry;
import org.apache.jackrabbit.api.JackrabbitRepository;
import org.apache.jackrabbit.oak.run.osgi.OakOSGiRepositoryFactory;
import org.apache.jackrabbit.oak.run.osgi.ServiceRegistryProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import io.radien.api.OAFProperties;

@RequestScoped
class RepositoryConfiguration {

    private static final Logger log = LoggerFactory.getLogger(RepositoryConfiguration.class);

    @Inject
    private ServletContext servletContext;

    private Repository repository;

    @Inject
    @ConfigProperty(name = "system.jcr.home}")
    private String repoHome;

    @Inject
    @ConfigProperty(name = "system.jcr.source")
    private String repoSource;

    @Inject
    @ConfigProperty(name = "oak.mongo.db")
    private String mongoDbName;

    @Inject
    @ConfigProperty(name = "oak.mongo.uri")
    private String mongoDbUri;

    /**
     * Invoked on bean creation, attempts to create the JCR repository
     */
    @PostConstruct
    public void init() {
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
        //FIXME: fix this
//        List<String> configFilePaths = copyConfigs(repoHomeDir, configFileNames);
//        repository = createRepository(configFilePaths, repoHomeDir);
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
     * Gets the Repository bean to be injected elsewhere in this application
     * @return the created {@link Repository} implementation
     */
    public Repository repository() {
        return repository;
    }

    /**
     * Bean used for metrics support
     * @return {@link PojoServiceRegistry} object with helpful metrics
     */
    
    public PojoServiceRegistry getServiceRegistry(){
        return ((ServiceRegistryProvider) repository).getServiceRegistry();
    }

    /**
     * Invoked on bean destroy phase
     */
    @PreDestroy
    private void destroy() {
        if (repository instanceof JackrabbitRepository) {
            ((JackrabbitRepository) repository).shutdown();
            log.info("Repository shutdown complete");
            repository = null;
        }
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

    //FIXME: fix commented code
//    /**
//     * Copies the configs from the 'resources/jcr' folder
//     *
//     * @param repoHomeDir the repository home directory
//     * @param configFileNames the config file names to copy
//     * @return The list of copied string
//     * @throws IOException Exception thrown if an IO error occurs while operating on those files
//     */
//    private List<String> copyConfigs(File repoHomeDir, List<String> configFileNames)
//            throws IOException {
//        List<String> filePaths = Lists.newArrayList();
//        for (String configName : configFileNames) {
//            File dest = new File(repoHomeDir, configName);
//            Resource source = new ClassPathResource("jcr/" + configName);
//            copyDefaultConfig(dest, source);
//            filePaths.add(dest.getAbsolutePath());
//        }
//        return filePaths;
//    }
//
//
//    /**
//     * Similar to {@link RepositoryConfiguration#copyConfigs} method
//     * @param repoConfig the repository configuration file
//     * @param defaultRepoConfig the Resource with the default repository configuration
//     * @throws IOException Exception thrown if an IO error occurs while operating on those files
//     */
//    private void copyDefaultConfig(File repoConfig, Resource defaultRepoConfig)
//            throws IOException {
//        if (!repoConfig.exists()){
//            log.info("Copying default repository config to {}", repoConfig.getAbsolutePath());
//            InputStream in = defaultRepoConfig.getInputStream();
//            try (FileOutputStream os = FileUtils.openOutputStream(repoConfig)) {
//                IOUtils.copy(in, os);
//            }
//        }
//    }


    /**
     * Joins a list of strings into a comma separated string
     * @param repoConfigs a List of string containing each repository configuration
     * @return an object with the comma separated configs
     */
    private Object commaSepFilePaths(List<String> repoConfigs) {
        return String.join(",", repoConfigs);
    }


}
