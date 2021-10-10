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
package io.radien.webapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.radien.api.Appframeable;
import io.radien.api.OAFProperties;

/**
 * Abstract Web Application constructor class
 *
 * @author Marco Weiland
 */
public abstract class AbstractWebapp implements Appframeable {

	private static final long serialVersionUID = 6930919846980181399L;
	protected static final Logger log = LoggerFactory.getLogger(AbstractWebapp.class);

	/**
	 * Gets the current OAF access
	 * @return the oaf object
	 */
	public String getProperty(OAFProperties cfg) {
		return getOAF().getProperty(cfg);
	}

	/**
	 * Gets the current Web app version
	 * @return the string value of the requested version
	 */
	public String getVersion() {
		return getProperty(OAFProperties.SYS_MF_APP_VERSION);
	}

	/**
	 * Web app version method getter
	 * @return the web app version
	 */
	public String getWebappVersion() {
		return getProperty(OAFProperties.SYS_MF_WEBAPP_VERSION);
	}

	/**
	 * Web app build number getter
	 * @return web app build number
	 */
	public String getBuildNumber() {
		return getProperty(OAFProperties.SYS_MF_BUILD_NUMBER);
	}

	/**
	 * Returns the current web app name getter method
	 * @return the web app name
	 */
	public String getAppName() {
		return getProperty(OAFProperties.SYS_APP_NAME);
	}

	/**
	 * Validates if for the current web app should be show the login or not
	 * @return true if login should be show
	 */
	public boolean isShowLogin() {
		return !Boolean.valueOf(getProperty(OAFProperties.SYS_AUTHENTICATION_OIDC_ENABLED));
	}

	/**
	 * Validates if dynamic app menu is to e displayed
	 * @return true if dynamic ap menu is to be show
	 */
	public boolean isDynamicAppmenuDisplayed() {
		return Boolean.valueOf(getProperty(OAFProperties.SYS_DYNAMIC_APPMENU_DISPLAY_ENABLED));
	}

}
