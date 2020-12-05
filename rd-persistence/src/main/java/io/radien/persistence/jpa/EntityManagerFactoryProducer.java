/*

	Copyright (c) 2016-present openappframe.org & its legal owners. All rights reserved.

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.

 */
package io.radien.persistence.jpa;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;

/**
 * Class responsible for creating the default EntityManagerFactory used by the
 * oaf application
 *
 * @author Marco Weiland
 */
public class EntityManagerFactoryProducer implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(EntityManagerFactoryProducer.class);

	private static final String CREATE_SUCCESS = "{} | ACTION: -createEntityManagerFactory | INIT COUNT: {}";
	private static final String CREATE_FAIL = "{} | ACTION: -createEntityManagerFactory FAILED! | Message : {}";
	private long initCount = 0;
	private long initCountCustom1 = 0;
	private long initCountCustom2 = 0;
//	@Inject
//	private OAFAccess app;
//
//	@Produces
//	@ApplicationScoped
//	@Default
//	public EntityManagerFactory create() {
//		boolean error = false;
//		String msg = "";
//		try {
//			return Persistence.createEntityManagerFactory(app.getProperty(OAFProperties.SYS_PERSISTENCE_UNIT));
//		} catch (Exception e) {
//
//			log.error("Error creating entity manager factory", e);
//
//			error = true;
//			msg = e.getMessage();
//		} finally {
//			if (!error) {
//				initCount++;
//				log.info(CREATE_SUCCESS, this, getInitCount());
//			} else {
//				log.error(CREATE_FAIL, this, msg);
//			}
//		}
//		return null;
//	}
//
//	@Produces
//	@ApplicationScoped
//	@EntityManagerQualifier(factory = EntityManagerFactories.INTEGRATION_1)
//	public EntityManagerFactory createIntegration1() {
//		boolean error = false;
//		String msg = "";
//		try {
//			return Persistence
//					.createEntityManagerFactory(app.getProperty(OAFProperties.SYS_PERSISTENCE_UNIT_CUSTOM1));
//		} catch (Exception e) {
//			log.error("Error creating integration 1", e);
//			error = true;
//			msg = e.getMessage();
//		} finally {
//			if (!error) {
//				initCountCustom1++;
//				log.info(CREATE_SUCCESS, this, getInitCountCustom1());
//			} else {
//				log.error(CREATE_FAIL, this, msg);
//			}
//		}
//		return null;
//	}
//
//	@Produces
//	@ApplicationScoped
//	@EntityManagerQualifier(factory = EntityManagerFactories.INTEGRATION_2)
//	public EntityManagerFactory createIntegration2() {
//		boolean error = false;
//		String msg = "";
//		try {
//			return Persistence
//					.createEntityManagerFactory(app.getProperty(OAFProperties.SYS_PERSISTENCE_UNIT_CUSTOM2));
//		} catch (Exception e) {
//			log.error("Error creating integration 2", e);
//			error = true;
//			msg = e.getMessage();
//		} finally {
//			if (!error) {
//				initCountCustom2++;
//				log.info(CREATE_SUCCESS, this, getInitCountCustom2());
//			} else {
//				log.error(CREATE_FAIL, this, msg);
//			}
//		}
//		return null;
//	}
//
//	public void destroy(@Disposes EntityManagerFactory factory) {
//		String detector = "";
//		try {
//			detector = (String) factory.getProperties().get("persistenceUnitName");
//			factory.close();
//		} catch (Exception e) {
//			log.error("Error destroying Entity manager", e);
//		} finally {
//			log.info("{} | ACTION: -closeEntityManager | INIT COUNT: {} [{}]", this, getInitCount(), detector);
//		}
//	}
//
//	private long getInitCount() {
//		return initCount;
//	}
//
//	private long getInitCountCustom1() {
//		return initCountCustom1;
//	}
//
//	private long getInitCountCustom2() {
//		return initCountCustom2;
//	}
}
