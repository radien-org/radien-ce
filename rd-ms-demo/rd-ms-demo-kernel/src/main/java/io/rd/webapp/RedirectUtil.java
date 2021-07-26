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

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Model;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.rd.api.OAFAccess;
import io.rd.api.OAFProperties;

/**
 * @author Rajesh Gavvala
 * @author Marco Weiland
 */
public @Model @RequestScoped class RedirectUtil implements RedirectHandler {
	protected static final Logger log = LoggerFactory.getLogger(RedirectUtil.class);
	private static final long serialVersionUID = 6812608123262000029L;

	@Inject
	private OAFAccess oaf;
	private String publicIndex;
	
	@PostConstruct
	private void init() {
		this.publicIndex = getOAF().getProperty(OAFProperties.SYS_CFG_DEFAULT_LANDING_PAGE);
	}

	public static String combineURLSegment(String url, String segment) {
		if (url == null || url.isEmpty()) {
			return "/" + segment;
		}
		if (url.charAt(url.length() - 1) == '/' || segment.charAt(0) == '/' ) {
			return url + segment;
		}
		return url + "/" + segment;
	}

	public String getProperty(OAFProperties cfg) {
		return getOAF().getProperty(cfg);
	}

	@Override
	public OAFAccess getOAF() {
		return oaf;
	}

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

	@Override
	public void redirectToIndex(boolean openDefaultApp) {
		String url = openDefaultApp ? publicIndex+"?openApp=true" : publicIndex;
		redirectTo(url);
	}

	@Override
	public void redirectToPublicIndex() {
		redirectTo(publicIndex);
	}

	@Override
	public void redirectToErrorPage(Exception exp) {
		redirectTo("/public/error");
	}
}
