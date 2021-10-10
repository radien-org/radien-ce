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
package io.rd.microservice.webapp.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.rd.microservice.api.Appframeable;
import io.rd.microservice.api.OAFProperties;
import io.rd.microservice.webapp.RedirectUtil;

/**
 * @author Rajesh Gavvala
 * @author Marco Weiland
 */
public abstract class AbstractWebFilter implements Filter, Appframeable {
	protected static final Logger log = LoggerFactory.getLogger(AbstractWebFilter.class.getName());
	private static final long serialVersionUID = 6812608123262000020L;
	private transient FilterConfig filterConfig;

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		this.filterConfig = arg0;
	}

	@Override
	public void destroy() {
		filterConfig = null;
	}

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		process(req, res, chain);
	}

	protected abstract void process(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException;

	protected String getFullLandingPageName() {
		return getLandingPage() + getExtension();
	}

	protected String getRedirect(ServletRequest req, ServletResponse res, FilterChain chain) {
		return RedirectUtil.combineURLSegment(((HttpServletRequest) req).getContextPath(), getFullLandingPageName());
	}

	protected String getLandingPage() {
		return getOAF().getProperty(OAFProperties.SYS_CFG_DEFAULT_LANDING_PAGE);
	}


	protected String getTermPage() {
		return getOAF().getProperty(OAFProperties.SYS_CFG_TERM_PAGE);
	}

	protected String getExtension() {
		if (Boolean.valueOf(getOAF().getProperty(OAFProperties.SYS_PRETTY_FACES_ENABLED))) {
			return "";
		}
		return "." + getOAF().getProperty(OAFProperties.SYS_CFG_JSF_MAPPING);
	}

	public FilterConfig getFilterConfig() {
		return filterConfig;
	}

	public void setFilterConfig(FilterConfig filterConfig) {
		this.filterConfig = filterConfig;
	}
}