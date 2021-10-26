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
package io.rd.microservice.webapp;

import java.util.Enumeration;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.rd.microservice.kernel.UTF8Control;


/**
 * @author Rajesh Gavvala
 * @author Marco Weiland
 */
public abstract class AbstractOafResourceBundle extends ResourceBundle {
	private static final Logger log = LoggerFactory.getLogger(AbstractOafResourceBundle.class);
	protected static final long serialVersionUID = 6812608123262000025L;
	protected static final String DEFAULT_BUNDLE_NAME = "language/Language";
	private static final Control UTF8_CONTROL = new UTF8Control();

	public AbstractOafResourceBundle() {
		setParent(ResourceBundle.getBundle(getBundleName(), FacesContext.getCurrentInstance().getViewRoot().getLocale(), UTF8_CONTROL));
	}

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

	@NotNull
	@Override
	public Enumeration<String> getKeys() {
		return parent.getKeys();
	}

	public abstract String getBundleName();

}