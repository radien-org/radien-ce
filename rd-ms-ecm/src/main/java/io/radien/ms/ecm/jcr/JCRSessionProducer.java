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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import java.io.Serializable;

/**
 * Class responsible for creating a jcr session per request
 *
 * @author Bruno Gama
 */
public @RequestScoped class JCRSessionProducer implements Serializable {

	private static final Logger log = LoggerFactory.getLogger(JCRSessionProducer.class);
	private static final long serialVersionUID = 788230632040161445L;
	private long initCount = 0;

	@Inject
	private Repository repository;

	@Produces
	@RequestScoped
	public Session create() {
		boolean error = false;
		try {

			// TODO: CHANGE REPOSITORY ADMIN PASSWORD

			return repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
		} catch (Exception e) {
			log.error("Error creating new JCR session", e);
			error = true;
		} finally {
			if (!error) {
				initCount++;
			}
		}
		return null;
	}

	public void destroy(@Disposes Session session) {
		try {
			session.logout();
		} catch (Exception e) {
			log.error("Error destroying JCR session", e);
		} finally {
			log.debug("{} | ACTION: -closeJCRSession | INITCOUNT: {}", this, initCount);
		}
	}

}
