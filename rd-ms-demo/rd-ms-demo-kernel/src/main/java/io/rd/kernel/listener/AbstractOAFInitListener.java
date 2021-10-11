/*
 * Copyright (c) 2021-present radien GmbH & its legal owners. All rights reserved.
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
package io.rd.kernel.listener;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.rd.api.OAFAccess;
import io.rd.api.OAFProperties;
import io.rd.kernel.eventbus.event.InitApplicationEvent;
import io.rd.kernel.interceptor.RuntimeIntercepted;

/**
 * @author Rajesh Gavvala
 * @author Marco Weiland
 */
public abstract class AbstractOAFInitListener implements ServletContextListener {
	private static final long serialVersionUID = 6812608123262000014L;
	protected static final Logger log = LoggerFactory.getLogger(AbstractOAFInitListener.class);

	@Override @RuntimeIntercepted
	public void contextInitialized(ServletContextEvent sce) {
		BeanManager bm = getBeanManager();

		if (bm != null) {
			@SuppressWarnings("unchecked")
			Bean<? extends OAFAccess> bean = (Bean<? extends OAFAccess>) bm.getBeans(getAppframe()).iterator().next();
			CreationalContext<? extends OAFAccess> ctx = bm.createCreationalContext(bean);
			OAFAccess appframe = (OAFAccess) bm.getReference(bean, getAppframe(), ctx);

			if (appframe.getProperty(OAFProperties.SYS_RUNTIME_MODE).equalsIgnoreCase("standalone")) {
				log.info("[APPINIT] : ENABLED : Application initializer");
				log.info("[MODE-Standalone] : ENABLED");
				log.info("[MODE-Plugin] : DISABLED");
				log.info("[DDL] : ENABLED : Tables/data will be created/updated");

				bm.fireEvent(new InitApplicationEvent());

			} else if (appframe.getProperty(OAFProperties.SYS_RUNTIME_MODE).equalsIgnoreCase("plugin")) {
				log.info("[APPINIT] : ENABLED : Application initializer");
				log.info("[MODE-Standalone] : DISABLED");
				log.info("[MODE-Plugin] : ENABLED");

				bm.fireEvent(new InitApplicationEvent());

			} else {
				log.error("[APPINIT] : FATAL : UNKNOWN RUNTIME MODE: "
						+ appframe.getProperty(OAFProperties.SYS_RUNTIME_MODE));
				log.error("[APPINIT] : FATAL : APPLICATION UNPREDICTABLE ");

				throw new RuntimeException();
			}

		} else {
			log.error("beanManager is null. Cannot fire startup event.");
			throw new RuntimeException();
		}
	}

	protected abstract BeanManager getBeanManager();

	protected abstract Class<? extends OAFAccess> getAppframe();

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		log.info("context destroyed");
	}

}
