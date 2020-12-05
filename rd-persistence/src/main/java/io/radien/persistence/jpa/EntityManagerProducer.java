/*
 * Copyright (c) 2016-present openappframe.org & its legal owners. All rights reserved.
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
package io.radien.persistence.jpa;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class responsible for creating a {@link EntityManager} object
 *
 * @author Marco Weiland
 */
public class EntityManagerProducer implements Serializable {

	private static final long serialVersionUID = 1L;
	private static long initCount = 0;
	protected final Logger log = LoggerFactory.getLogger(EntityManagerProducer.class);

//	@Inject
//	@Default
//	private EntityManagerFactory emf;
//
//	@Produces
//	@RequestScoped
//	@Default
//	public EntityManager create() {
//		boolean error = false;
//		try {
//			return emf.createEntityManager();
//		} catch (Exception e) {
//			error = true;
//		} finally {
//			if (!error) {
//				initCount++;
//				log.debug(String.format("%s |ACTION: -createEntityManager | INIT COUNT: " + getInitCount(), this));
//			} else {
//				log.error(String.format("%s | ACTION: -createEntityManager FAILED! ", this));
//			}
//		}
//		return null;
//	}
//
//	public void destroy(@Disposes EntityManager em) {
//		String detector = "";
//		try {
//			detector = (String) em.getEntityManagerFactory().getProperties().get("persistenceUnitName");
//			em.clear();
//			em.close();
//		} catch (Exception e) {
//			log.error("Error destroying Entity manager", e);
//		} finally {
//			log.debug(String.format("%s | ACTION: -closeEntityManager | INIT COUNT: " + getInitCount(),
//					"[" + detector + "]" + this));
//		}
//	}
//
//	private static long getInitCount() {
//		return initCount;
//	}

}
