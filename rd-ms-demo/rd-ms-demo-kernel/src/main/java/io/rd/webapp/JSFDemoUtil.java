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
package io.rd.webapp;

import java.text.MessageFormat;

import java.util.Optional;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.rd.util.StringFormatUtil;

/**
 * Utility class for JSF bean classes
 * @author Rajesh Gavvala
 * @author Marco Weiland
 */
public class JSFDemoUtil {
	private static final Logger log = LoggerFactory.getLogger(JSFDemoUtil.class);
	private JSFDemoUtil(){}

	/**
	 * returns the message in the default bundle by the key given by arguments
	 *
	 * @param key key of the message to return
	 * @return String value of the message identified by given key
	 */
	public static String getMessage(String key) {
		return getMessage("msg", key);
	}

	public static String getMessage(String bundleName, String key) {
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			ResourceBundle bundle = context.getApplication().getResourceBundle(context, bundleName);
			return bundle.getString(key);
		} catch (Exception e) {
			log.warn("Missing translation / key for key {} in bundle {}", key, bundleName);
		}
		return key;
	}

	public static String getString(AjaxBehaviorEvent event, String attribute, String defaultVal) {
		Optional<String> returnVal = getReturnVal(attribute);
		String result = returnVal.
				orElse((String) event.getComponent().getAttributes().get(attribute));
		return (result == null || result.equalsIgnoreCase("")) ? defaultVal : result;
	}

	private static Optional<String> getReturnVal(String attribute) {
		ExternalContext externalContext = JSFDemoUtil.getExternalContext();
		if(externalContext == null){
			log.error("Null External Context");
			return Optional.empty();
		}
		return Optional.of(externalContext.getRequestParameterMap()
				.get(attribute));
	}

	public static String getString(ActionEvent event, String attribute, String defaultVal) {
		Optional<String> returnVal = getReturnVal(attribute);

		String result = returnVal.orElse((String) event.getComponent().getAttributes().get(attribute));

		return (result == null || result.equalsIgnoreCase("")) ? defaultVal : result;
	}

	public static FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}

	public static ExternalContext getExternalContext() {
		FacesContext facesContext = getFacesContext();
		if(facesContext == null){
			log.warn("Faces Context is NULL");
			return null;
		}
		return facesContext.getExternalContext();
	}


	public static void addMessage( Severity severity,  String title,  String message) {
		FacesMessage facesMsg = new FacesMessage(severity, title, message);
		ExternalContext externalContext = JSFDemoUtil.getExternalContext();
		if(externalContext == null) {
			log.error("Null external context");
			return;
		}
		FacesContext.getCurrentInstance().addMessage(null, facesMsg);
	}

	public static void addMessage( Severity severity,  String title,  String pattern,  Object... arguments) {
		String message;

		if (arguments.length == 0) {
			message = pattern;
		} else {
			try {
				message = StringFormatUtil.format(pattern, arguments);
			} catch (IllegalArgumentException e) {
				log.error("Provided pattern \"{}\" is not correct, making the best of a bad situation. Reason: {}", pattern, e.getMessage());
				message = MessageFormat.format(pattern, arguments);
			}
		}
		addMessage(severity, title, message);
	}
}
