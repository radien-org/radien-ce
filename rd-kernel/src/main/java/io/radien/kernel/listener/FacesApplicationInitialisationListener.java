/**
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
package io.radien.kernel.listener;

import javax.faces.application.Application;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Marco Weiland
 */
// @WebListener
public class FacesApplicationInitialisationListener implements SystemEventListener {
	private static final Logger log = LoggerFactory.getLogger(FacesApplicationInitialisationListener.class);

	@Override
	public void processEvent(SystemEvent event) throws AbortProcessingException {
		log.info("Process event triggered");
	}

	@Override
	public boolean isListenerForSource(Object source) {
		return source instanceof Application;
	}

	// @Override
	// public void contextInitialized(ServletContextEvent sce) {
	//

	//
	// BeanManager beanManager = lookUpBeanManager();
	// if (beanManager != null) {
	// beanManager.fireEvent(new AppStartupEvent());
	// log.info("beanManager fired AppStartupEvent.");
	// } else {
	// log.error("beanManager is null. Cannot fire startup event.");
	// }
	//
	// }
	//
	// public BeanManager lookUpBeanManager() {
	// try {
	// // See reference below about how I came up with this
	// InitialContext iniCtx = new InitialContext();
	// BeanManager result = (BeanManager)
	// iniCtx.lookup("java:comp/env/BeanManager");
	// return result;
	// } catch (NamingException e) {
	// log.error("Could not construct BeanManager.", e);
	// return null;
	// }
	// }
	//
	// public static class AppStartupEvent implements Serializable {
	//
	// private static final long serialVersionUID = 1L;
	// }
	//
	// @Override
	// public void contextDestroyed(ServletContextEvent sce) {
	// // TODO Auto-generated method stub
	//
	// }

}
