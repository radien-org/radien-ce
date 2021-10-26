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
package io.rd.webapp.security;

import javax.faces.application.ResourceHandler;
import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.rd.webapp.RedirectUtil;

/**
 * Filter implementation that triggers if there is no user present on
 * sessionHandler bean
 * @author Rajesh Gavvala
 * @author Marco Weiland
 */
// @WebFilter(filterName = "AuthFilter", urlPatterns = { "/*" }, dispatcherTypes
// = { DispatcherType.REQUEST, DispatcherType.FORWARD })
public abstract class AbstractAuthorizationFilter extends AbstractWebFilter {
	private static final long serialVersionUID = 6812608123262000019L;

	private static final Logger log = LoggerFactory.getLogger(AbstractAuthorizationFilter.class);

	/** Bean responsible for utility redirect methods */
	@Inject
	protected RedirectUtil webapp; 

	/**
	 * Processes all requests that have no user in session and are within the
	 * openappframe modules, redirecting the user to the index page in tis case
	 *
	 * @param req
	 *                  the Servlet request
	 * @param res
	 *                  te servlet response
	 * @param chain
	 *                  the filter chain
	 */
	protected void process(ServletRequest req, ServletResponse res, FilterChain chain) {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		String reqURI = request.getRequestURI();

		try {

			if ( reqURI.contains("/module/")
					&& reqURI.contains(ResourceHandler.RESOURCE_IDENTIFIER) && !reqURI.contains("/module/ecm/display")
					&& !reqURI.contains("/saml") && !reqURI.contains("/oidc") && !reqURI.contains("/sps")) {
				response.sendRedirect(getRedirect(request, response, chain));
				webapp.redirectToIndex(false);
				return;
			}
			chain.doFilter(req, res);
		} catch (Exception e) {
			log.error("Error processing abstract authorization filter", e);
		}

	}

}
