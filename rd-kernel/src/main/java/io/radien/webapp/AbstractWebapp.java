/**
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
package io.radien.webapp;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.radien.api.Appframeable;
import io.radien.api.OAFProperties;

/**
 * @author Marco Weiland
 */
public abstract class AbstractWebapp implements Serializable, Appframeable {
	private static final long serialVersionUID = 1L;
	protected static final Logger log = LoggerFactory.getLogger(AbstractWebapp.class);
	private static final String SNAPSHOT = "SNAPSHOT";

	public String getProperty(OAFProperties cfg) {
		return getOAF().getProperty(cfg);
	}

	public String getVersion() {
		String version = getProperty(OAFProperties.SYS_MF_APP_VERSION);
		if ( version != null &&  version.contains(SNAPSHOT)) {
			version += ("-"+getBuildNumber());
		}
		return version;
	}

	public String getWebappVersion() {
		String version = getProperty(OAFProperties.SYS_MF_WEBAPP_VERSION);
		if ( version != null && version.contains(SNAPSHOT)) {
			version += ("-"+getBuildNumber());
		}
		return version;
	}

	public String getBuildNumber() {
		return getProperty(OAFProperties.SYS_MF_BUILD_NUMBER);
	}

	public String getAppName() {
		return getProperty(OAFProperties.SYS_APP_NAME);
	}

	public boolean isShowLogin() {
		return !Boolean.valueOf(getProperty(OAFProperties.SYS_AUTHENTICATION_OIDC_ENABLED));
	}

	public boolean isDynamicAppmenuDisplayed() {
		return Boolean.valueOf(getProperty(OAFProperties.SYS_DYNAMIC_APPMENU_DISPLAY_ENABLED));
	}

}
