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

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import org.apache.jackrabbit.commons.JcrUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.jcr.Repository;
import java.io.Serializable;

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

    private Repository repository = null;

    @PostConstruct
    private void init() {

        log.info("[CMS] setting system properties for CMS System");
        String repositoryHomeDir = System.getProperty("catalina.base") + oaf.getProperty(OAFProperties.SYSTEM_CMS_REPO_HOME_DIR);

        String homeDirPath = OAFProperties.SYSTEM_CMS_REPO_HOME_DIR.propKey();
        System.setProperty(homeDirPath, repositoryHomeDir);
        log.info("[CMS] {} set to {} ", homeDirPath, repositoryHomeDir);

        try {
            if (repository == null) {

                repository = JcrUtils.getRepository();

                log.info("############################ CMS running on : ############################");
                log.info("## " + repository.getDescriptor(Repository.REP_VENDOR_DESC) + " / "
                        + repository.getDescriptor(Repository.REP_NAME_DESC));
                log.info("## " + repository.getDescriptor(Repository.SPEC_NAME_DESC) + " v"
                        + repository.getDescriptor(Repository.SPEC_VERSION_DESC));
                log.info("##########################################################################");
            }

        } catch (Exception e) {
            log.error("Error producing a JCR repository", e);
        }
    }

    @Produces
    @RequestScoped
    public Repository create() {

        return repository;
    }


}
