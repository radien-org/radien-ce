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
package io.radien.webapp.security;

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

import io.radien.api.Appframeable;
import io.radien.api.OAFProperties;
import io.radien.webapp.RedirectUtil;

/**
 * Abstract web Filter object class implemntation
 *
 * @author Marco Weiland
 */
public abstract class AbstractWebFilter implements Filter, Appframeable {

	protected static final Logger log = LoggerFactory.getLogger(AbstractWebFilter.class.getName());
	private static final long serialVersionUID = 2521912193155542704L;
	private transient FilterConfig filterConfig;

	/**
	 * Abstract web filter initializer
	 * @param arg0 filter configuration to be used
	 @throws ServletException if the request could not be handled
	 */
	@Override
	public void init(FilterConfig arg0) throws ServletException {
		this.filterConfig = arg0;
	}

	/**
	 * Destroyer of the web filter
	 */
	@Override
	public void destroy() {
		filterConfig = null;
	}

	/**
	 * Creation of the web filtering process
	 * @param req the Servlet request
	 * @param res the servlet response
	 * @param chain the filter chain
	 * @throws ServletException if the request could not be handled
	 * @throws IOException if an input or output error is detected when the servlet handles the request
	 */
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		process(req, res, chain);
	}

	/**
	 * Processes all requests that have no user in session and are within the
	 * openappframe modules, redirecting the user to the index page in tis case
	 *
	 * @param req the Servlet request
	 * @param res the servlet response
	 * @param chain the filter chain
	 */
	protected abstract void process(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException;

	/**
	 * Redirects the user into the login page
	 * @return string value of the login page path
	 */
	protected String getFullLoginPageName() {
		return getLoginPage() + getExtension();
	}

	/**
	 * Redirects the user into the landing page
	 * @return string value of the landing page path
	 */
	protected String getFullLandingPageName() {
		return getLandingPage() + getExtension();
	}

	/**
	 * Redirects the user into the requested page
	 * @return string value of the requested page path
	 */
	protected String getRedirect(ServletRequest req, ServletResponse res, FilterChain chain) {
		return RedirectUtil.combineURLSegment(((HttpServletRequest) req).getContextPath(), getFullLandingPageName());
	}

	/**
	 * Landing page getter property value
	 * @return landing page url
	 */
	protected String getLandingPage() {
		return getOAF().getProperty(OAFProperties.SYS_CFG_DEFAULT_LANDING_PAGE);
	}

	/**
	 * Login page getter property value
	 * @return login page url
	 */
	protected String getLoginPage() {
		return getOAF().getProperty(OAFProperties.SYS_CFG_LOGIN_PAGE);
	}

	/**
	 * Term page getter property value
	 * @return term page url
	 */
	protected String getTermPage() {
		return getOAF().getProperty(OAFProperties.SYS_CFG_TERM_PAGE);
	}

	/**
	 * Context page getter property value
	 * @return context page url
	 */
	protected String getContextPage() {
		return getOAF().getProperty(OAFProperties.SYS_CFG_CONTEXT_PAGE);
	}

	/**
	 * Adds to the requested property the configuration for the jsf mapping
	 * @return string value of page
	 */
	protected String getExtension() {
		if (Boolean.parseBoolean(getOAF().getProperty(OAFProperties.SYS_PRETTY_FACES_ENABLED))) {
			return "";
		}
		return "." + getOAF().getProperty(OAFProperties.SYS_CFG_JSF_MAPPING);
	}

	/**
	 * Redirects user into partial xml page
	 *
	 * @param req the Servlet request
	 * @param res the servlet response
	 * @param chain the filter chain
	 * @throws ServletException if the request could not be handled
	 * @throws IOException if an input or output error is detected when the servlet handles the request
	 */
	protected String xmlPartialRedirectToPage(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		return "<?xml version='1.0' encoding='UTF-8'?>" + "<partial-response><redirect url=\""
				+ getRedirect(req, res, chain) + "\"/></partial-response>";
	}

	/**
	 * Validates if request is ajax or not
	 * @param request the Servlet request
	 * @return true if the requested request is an ajax one
	 */
	protected boolean isAjax(HttpServletRequest request) {
		return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
	}

	/**
	 * Filter configuration getter
	 * @return the filtered configuration
	 */
	public FilterConfig getFilterConfig() {
		return filterConfig;
	}

	/**
	 * Filtered configuration setter
	 * @param filterConfig to be set
	 */
	public void setFilterConfig(FilterConfig filterConfig) {
		this.filterConfig = filterConfig;
	}
}