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
package io.radien.api.webapp.i18n;

import java.util.List;
import java.util.Locale;

import io.radien.api.Appframeable;

/**
 * Interface defining the methods used in locale and timezone management
 *
 * @author Marco Weiland
 */
public interface LocaleManagerAccess extends Appframeable {

	/**
	 * Active language getter method
	 * @return the current active language
	 */
	String getActiveLanguage();

	/**
	 * Active language setter method
	 * @param activeLocale to be set
	 */
	void setActiveLanguage(String activeLocale);

	/**
	 * Active locale getter method
	 * @return the current active locale
	 */
	Locale getActiveLocale();

	/**
	 * Active supported languages getter method
	 * @return a list of all the supported languages
	 */
	List<String> getSupportedLanguages();

	/**
	 * Active client time zone offset getter method
	 * @return the current active client time zone offset
	 */
	String getClientTzOffset();

	/**
	 * Active user session language getter method
	 * @return the current user language
	 */
	String getUserLanguage();
}
