/*
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
package io.radien.webapp;

import java.text.MessageFormat;

import java.util.Optional;
import java.util.ResourceBundle;

import javax.faces.FactoryFinder;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.context.Flash;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.radien.util.StringFormatUtil;

/**
 * Utility class for JSF bean classes
 *
 * @author Marco Weiland
 */
public class JSFUtil {
	private static final Logger log = LoggerFactory.getLogger(JSFUtil.class);
	private JSFUtil(){}

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
		ExternalContext externalContext = JSFUtil.getExternalContext();
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

	public static Long getLong(ActionEvent event, String attribute, Long defaultVal) {
		Long returnVal = (Long) event.getComponent().getAttributes().get(attribute);
		return returnVal == null ? defaultVal : returnVal;
	}

	public static Long getLong(AjaxBehaviorEvent event, String attribute) {
		Optional<String> returnValString = getReturnVal(attribute);
		String result = returnValString.orElse("" + event.getComponent().getAttributes().get(attribute));
		return Long.valueOf(result);
	}

	public static FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}

	public static FacesContext getFacesContext(HttpServletRequest request, HttpServletResponse response) {
		// Get current FacesContext.
		FacesContext facesContext = FacesContext.getCurrentInstance();

		// Check current FacesContext.
		if (facesContext == null) {

			// Create new Lifecycle.
			LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder
					.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
			Lifecycle lifecycle = lifecycleFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);

			// Create new FacesContext.
			FacesContextFactory contextFactory = (FacesContextFactory) FactoryFinder
					.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
			facesContext = contextFactory.getFacesContext(request.getSession(false).getServletContext(), request, response,
					lifecycle);

			// Create new View.
			UIViewRoot view = facesContext.getApplication().getViewHandler().createView(facesContext, "");
			facesContext.setViewRoot(view);

			// Set current FacesContext.
			FacesContextWrapper.setCurrentInstance(facesContext);
		}

		return facesContext;
	}

	public static ExternalContext getExternalContext() {
		FacesContext facesContext = getFacesContext();
		if(facesContext == null){
			log.warn("Faces Context is NULL");
			return null;
		}
		return facesContext.getExternalContext();
	}

	// region HELPERS

	public static String getFileNameFromPart(Part part) {
		final String partHeader = part.getHeader("content-disposition");
		for (String content : partHeader.split(";")) {
			if (content.trim().startsWith("filename")) {
				return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
			}
		}
		return null;
	}

	/**
	 * Wraps the protected FacesContext.setCurrentInstance() in a inner class.
	 */
	private abstract static class FacesContextWrapper extends FacesContext {
		protected static void setCurrentInstance(FacesContext facesContext) {
			FacesContext.setCurrentInstance(facesContext);
		}
	}

	public static HttpServletRequest getRequest(FacesContext context) {
		Object request = context.getExternalContext().getRequest();
		assert request instanceof HttpServletRequest;
		return (HttpServletRequest) request;
	}

	/**
	 * Gets the token parameter passed in the url
	 *
	 * @return {@link String} with the parameter token value
	 */
	public static Optional<String> getRequestParameter(String key) {
		return getReturnVal(key);
	}

	public static Optional<String> getBaseUrl() {
		ExternalContext externalContext = JSFUtil.getExternalContext();
		if(externalContext == null){
			return Optional.empty();
		}
		HttpServletRequest origRequest = (HttpServletRequest) externalContext
				.getRequest();
		return Optional.of(origRequest.toString().substring(0,
				origRequest.toString().length() - origRequest.getServletPath().length()));
	}

	public static void addSuccessMessage( String messageKey) {
		addSuccessMessage(null, messageKey);
	}

	public static void addSuccessMessage( String titleKey,  String messageKey) {
		addTranslatableMessage(FacesMessage.SEVERITY_INFO, titleKey, messageKey);
	}

	public static void addSuccessMessage( String titleKey,  String messageKey,  Object... arguments) {
		addTranslatableMessage(FacesMessage.SEVERITY_INFO, titleKey, messageKey, arguments);
	}

	public static void addErrorMessage( String messageKey) {
		addErrorMessage(null, messageKey);
	}

	public static void addErrorMessage( String titleKey,  String messageKey) {
		addTranslatableMessage(FacesMessage.SEVERITY_ERROR, titleKey, messageKey);
	}

	public static void addErrorMessage( String titleKey,  String messageKey,  Object... arguments) {
		addTranslatableMessage(FacesMessage.SEVERITY_ERROR, titleKey, messageKey, arguments);
	}

	public static void addErrorMessage( Exception e) {
		String msg = e.getLocalizedMessage();
		if (msg != null && msg.length() > 0) {
			addMessage(FacesMessage.SEVERITY_ERROR, "", msg);
		} else {
			addMessage(FacesMessage.SEVERITY_ERROR, "", e.getMessage());
		}
	}

	public static void addWarningMessage( String messageKey) {
		addWarningMessage(null, messageKey);
	}

	public static void addWarningMessage( String titleKey,  String messageKey) {
		addTranslatableMessage(FacesMessage.SEVERITY_WARN, titleKey, messageKey);
	}

	public static void addWarningMessage( String titleKey,  String messageKey,  Object... arguments) {
		addTranslatableMessage(FacesMessage.SEVERITY_WARN, titleKey, messageKey, arguments);
	}

	public static void addTranslatableMessage( Severity severity,  String titleKey,  String messageKey,  Object... arguments) {
		String title;
		if (titleKey == null || titleKey.isEmpty()) {
			title = "";
		} else {
			title = getMessage(titleKey);
		}

		String message = getMessage(messageKey);
		if (message.isEmpty() || message.equals(messageKey)) {
			if (!messageKey.contains(" ")) {
				message = "???" + messageKey + "???";
			} else {
				message = messageKey;
			}
			addMessage(severity, title, message);
		} else {
			addMessage(severity, title, message, arguments);
		}
	}

	public static void addMessage( Severity severity,  String title,  String message) {
		FacesMessage facesMsg = new FacesMessage(severity, title, message);
		ExternalContext externalContext = JSFUtil.getExternalContext();
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

	public void redirect(String destination){
		getFacesContext().getApplication().getNavigationHandler().handleNavigation(JSFUtil.getFacesContext(), null, destination);
	}
}
