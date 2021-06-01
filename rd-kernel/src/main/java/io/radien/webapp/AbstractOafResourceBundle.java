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
package io.radien.webapp;

import java.util.Enumeration;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.radien.kernel.UTF8Control;

/**
 * Resource bundles contain locale-specific objects. When your program needs a locale-specific resource,
 * a String for example, your program can load it from the resource bundle that is appropriate for the current
 * user's locale. In this way, you can write program code that is largely independent of the user's locale
 * isolating most, if not all, of the locale-specific information in resource bundles.
 * This allows you to write programs that can:
 * be easily localized, or translated, into different languages
 * handle multiple locales at once
 * be easily modified later to support even more locales
 *
 * @author Marco Weiland
 */
public abstract class AbstractOafResourceBundle extends ResourceBundle {
	private static final Logger log = LoggerFactory.getLogger(AbstractOafResourceBundle.class);
	protected static final String DEFAULT_BUNDLE_NAME = "language/Language";
	private static final Control UTF8_CONTROL = new UTF8Control();

	/**
	 * Abstract OAF resource Bundle empty param constructor
	 */
	public AbstractOafResourceBundle() {
		setParent(ResourceBundle.getBundle(getBundleName(), FacesContext.getCurrentInstance().getViewRoot().getLocale(), UTF8_CONTROL));
	}

	/**
	 * Gets an object for the given key from this resource bundle. Returns null if this resource bundle does not contain an object for the given key.
	 * @param key the key for the desired object
	 * @return the object for the given key, or null
	 */
	@Override
	protected Object handleGetObject(@NotNull String key) {
		Object o = null;
		try {
			o = parent.getObject(key);
			return o;
		} catch (Exception e) {
			log.warn("[AbstractOafResourceBundle] message property could not be retrieved: {}", key);
		}
		return key;
	}

	/**
	 * Returns an enumeration of the keys.
	 * @return an Enumeration of the keys contained in this ResourceBundle and its parent bundles.
	 */
	@NotNull
	@Override
	public Enumeration<String> getKeys() {
		return parent.getKeys();
	}

	public abstract String getBundleName();

}