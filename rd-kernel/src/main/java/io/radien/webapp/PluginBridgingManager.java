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
package io.radien.webapp;

import java.io.Serializable;

import javax.inject.Inject;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;

/**
 * 
 * @author Marco Weiland
 * 
 */
public abstract class PluginBridgingManager implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	private OAFAccess oaf;

	@Inject
	private UserSession userSession;

	public OAFAccess getOAF() {
		return oaf;
	}

	public UserSession getUserSession() {
		return userSession;
	}

	public boolean isCmsDynamicMenuEnabled() {
		return Boolean.valueOf(getOAF().getProperty(OAFProperties.MODULE_CMS_DYNAMIC_CONTENT_MENU_ENABLED));
	}

	public abstract String getThemeCssClass();

}
