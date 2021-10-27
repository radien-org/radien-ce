/*
 * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
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

import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.servlet.ServletContextEvent;

import io.radien.api.OAFAccess;
import io.radien.kernel.OpenAppframe;

/**
 * Open Appframe initializer listener
 *
 * @author Marco Weiland
 */
public class OAFInitListener extends AbstractOAFInitListener {

	/**
	 * Requests to start the given context event
	 * @param sce context to be started/initialized
	 */
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		super.contextInitialized(sce);
	}

	/**
	 * Requests to delete/destroy the given context
	 * @param sce context to be discarded and destroyed
	 */
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		super.contextDestroyed(sce);
	}

	/**
	 * Request to return the current open appframe object
	 * @return the active open appframe
	 */
	@Override
	protected Class<? extends OAFAccess> getAppframe() {
		return OpenAppframe.class;
	}

	/**
	 * Requests the current CDI bean manager
	 * @return the current bean manager from the CDI
	 */
	@Override
	protected BeanManager getBeanManager() {
		return CDI.current().getBeanManager();
	}

}
