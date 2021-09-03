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

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Model;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;

/**
 * Redirection Util object class
 *
 * @author Marco Weiland
 */
public @Model @RequestScoped class RedirectUtil implements RedirectHandler {
	protected static final Logger log = LoggerFactory.getLogger(RedirectUtil.class);
	private static final long serialVersionUID = 162788969745175865L;

	@Inject
	private OAFAccess oaf;
	private String publicIndex;
	
	@PostConstruct
	private void init() {
		this.publicIndex = getOAF().getProperty(OAFProperties.SYS_CFG_DEFAULT_LANDING_PAGE);
	}

	/**
	 * Create and return a combination between a given url and a given segment
	 * @param url to be used as a prefix
	 * @param segment to be used as segmentation of the url
	 * @return new string value of the url+segment
	 */
	public static String combineURLSegment(String url, String segment) {
		if (url == null || url.isEmpty()) {
			return "/" + segment;
		}
		if (url.charAt(url.length() - 1) == '/' || segment.charAt(0) == '/' ) {
			return url + segment;
		}
		return url + "/" + segment;
	}

	/**
	 * Retrieves in the current OAF the active property
	 * @param cfg to be retrieved
	 * @return string value of the property
	 */
	public String getProperty(OAFProperties cfg) {
		return getOAF().getProperty(cfg);
	}

	/**
	 * Gets the current OAF access
	 * @return the oaf object
	 */
	@Override
	public OAFAccess getOAF() {
		return oaf;
	}

	/**
	 * Redirect user into given url path
	 * @param url to redirect the user
	 */
	@Override
	public void redirectTo(String url) {
		ExternalContext ec = JSFUtil.getExternalContext();
		String uri = ec.getRequestContextPath() + url;
		try {
			ec.redirect(uri);
			return;
		} catch (IOException e) {
			log.error("Problem with redirect to " + uri, e);
		}
		FacesContext fc = JSFUtil.getFacesContext();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "pretty:"+ url +"?faces-redirect=true");
	}

	/**
	 * Redirects user into index
	 * @param openDefaultApp
	 */
	@Override
	public void redirectToIndex(boolean openDefaultApp) {
		String url = openDefaultApp ? publicIndex+"?openApp=true" : publicIndex;
		redirectTo(url);
	}

	/**
	 * Redirects the user into home page/public index
	 */
	@Override
	public void redirectToPublicIndex() {
		redirectTo(publicIndex);
	}

	/**
	 * Redirects user into logout page
	 */
	@Override
	public void redirectToLogout() {
		redirectTo("/logout");
	}

	/**
	 * Redirects user into an error page
	 * @param exp to be throw/show
	 */
	@Override
	public void redirectToErrorPage(Exception exp) {
		redirectTo("/public/error");
	}
}
