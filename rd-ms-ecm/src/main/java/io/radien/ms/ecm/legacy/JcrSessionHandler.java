/*
 * Copyright (c) 2006-present openappframe.org & its legal owners. All rights reserved.
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
 */
package io.radien.ms.ecm.legacy;



import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.radien.api.service.ecm.exception.ContentRepositoryNotAvailableException;

/**
 * @author Marco Weiland
 */
@ApplicationScoped
public class JcrSessionHandler {
    private static final Logger log = LoggerFactory.getLogger(JcrSessionHandler.class);

    @Inject
    private Repository repository;

    private long initCount = 0;


    public Session createSession() throws ContentRepositoryNotAvailableException {
        boolean error = false;
        try {
            Session adminSession = repository.login(getAdminCredentials());
            adminSession.setNamespacePrefix("oaf", "http://www.jcp.org/jcr/mix/1.0");
            return adminSession;
        } catch (Exception e) {
            log.error("Error creating new JCR session", e);
            error = true;
            throw new ContentRepositoryNotAvailableException();
        } finally {
            if (!error) {
                initCount++;
                log.info("{} |ACTION: -createJCRSession | INIT COUNT: {}", this, initCount);
            } else {
                log.error("{} | ACTION: -createJCRSession FAILED!", this.getClass());
            }
        }
    }

    private static SimpleCredentials getAdminCredentials() {
        return new SimpleCredentials(CmsConstants.USER_ADMIN, "admin".toCharArray());
    }
}