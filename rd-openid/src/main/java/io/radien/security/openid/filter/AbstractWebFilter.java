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
package io.radien.security.openid.filter;

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
 * @author Marco Weiland
 */
public abstract class AbstractWebFilter implements Filter, Appframeable {
	protected static final Logger log = LoggerFactory.getLogger(AbstractWebFilter.class.getName());
	private static final long serialVersionUID = 1L;
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

	protected String getFullLoginPageName() {
		return getLoginPage() + getExtension();
	}

	protected String getFullLandingPageName() {
		return getLandingPage() + getExtension();
	}

	protected String getRedirect(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		return RedirectUtil.combineURLSegment(((HttpServletRequest) req).getContextPath(), getFullLandingPageName());
	}

	protected String getLandingPage() {
		return getOAF().getProperty(OAFProperties.SYS_CFG_DEFAULT_LANDING_PAGE);
	}

	protected String getLoginPage() {
		return getOAF().getProperty(OAFProperties.SYS_CFG_LOGIN_PAGE);
	}

	protected String getTermPage() {
		return getOAF().getProperty(OAFProperties.SYS_CFG_TERM_PAGE);
	}

	protected String getContextPage() {
		return getOAF().getProperty(OAFProperties.SYS_CFG_CONTEXT_PAGE);
	}

	protected String getExtension() {
		if (Boolean.valueOf(getOAF().getProperty(OAFProperties.SYS_PRETTY_FACES_ENABLED))) {
			return "";
		}
		return "." + getOAF().getProperty(OAFProperties.SYS_CFG_JSF_MAPPING);
	}

	protected String xmlPartialRedirectToPage(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		return "<?xml version='1.0' encoding='UTF-8'?>" + "<partial-response><redirect url=\""
				+ getRedirect(req, res, chain) + "\"/></partial-response>";
	}

	protected boolean isAjax(HttpServletRequest request) {
		return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
	}

	public FilterConfig getFilterConfig() {
		return filterConfig;
	}

	public void setFilterConfig(FilterConfig filterConfig) {
		this.filterConfig = filterConfig;
	}
}