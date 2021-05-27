/*
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
package io.radien.api;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * OAF Access interface class
 *
 * @author Marco Weiland
 */
public interface OAFAccess extends Serializable {

	/**
	 * OAF Access version getter
	 * @return the oaf version
	 */
	public String getVersion();

	/**
	 * OAF Access system administrator user id getter
	 * @return the oaf system administrator user id
	 */
	public Long getSystemAdminUserId();

	/**
	 * OAF Access resource bundle getter
	 * @return the oaf resource bundle
	 */
	public ResourceBundle getResourceBundle(String bundleName);

	/**
	 * OAF Access fire event caller
	 */
	public void fireEvent(Event event);

	/**
	 * OAF Access default locale getter
	 * @return the oaf default locale
	 */
	public Locale getDefaultLocale();

	/**
	 * OAF Access supported locales list getter
	 * @return a map of oaf supported locales
	 */
	public Map<String, Locale> getSupportedLocales();

	/**
	 * OAF Access find locale getter
	 * @return the oaf found locale for the required language
	 */
	public Locale findLocale(String language);

	/**
	 * OAF Access property endpoint getter
	 * @return the oaf property
	 */
	public String getProperty(SystemProperties cfg);

}
