/**
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
 */
package io.rd.microservice.kernel.listener;

import io.rd.microservice.api.OAFAccess;
import io.rd.microservice.kernel.OpenAppframe;

import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.servlet.ServletContextEvent;

/**
 * @author Rajesh Gavvala
 * @author Marco Weiland
 */
public class OAFInitListener extends AbstractOAFInitListener {
	private static final long serialVersionUID = 6812608123262000015L;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		super.contextInitialized(sce);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		super.contextDestroyed(sce);
	}

	@Override
	protected Class<? extends OAFAccess> getAppframe() {
		return OpenAppframe.class;
	}

	@Override
	protected BeanManager getBeanManager() {
		return CDI.current().getBeanManager();
	}

}
