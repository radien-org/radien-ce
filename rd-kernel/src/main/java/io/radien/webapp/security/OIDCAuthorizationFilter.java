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
package io.radien.webapp.security;

import io.radien.security.openid.context.SecurityContextHolder;
import io.radien.security.openid.model.Authentication;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.radien.api.OAFProperties;
import io.radien.api.kernel.messages.SystemMessages;
import io.radien.security.openid.model.OpenIdConnectUserDetails;
import io.radien.webapp.RedirectUtil;

/**
 * OIDC filter that handles per request OIDC authentication
 *
 * @author Marco Weiland
 */
public class OIDCAuthorizationFilter extends AuthorizationFilter {

	private static final long serialVersionUID = -6910833814070691420L;
	private static final Logger log = LoggerFactory.getLogger(OIDCAuthorizationFilter.class);

	/**
	 * Processes all requests that have no user in session and are within the
	 * openappframe modules, redirecting the user to the index page in tis case
	 *
	 * @param req the Servlet request
	 * @param res the servlet response
	 * @param chain the filter chain
	 */
	@Override
	protected void process(ServletRequest req, ServletResponse res, FilterChain chain) {
		HttpServletRequest request =(HttpServletRequest) req;
		HttpSession httpSession = request.getSession();
		Authentication auth = (Authentication) httpSession.getAttribute("Authentication");

//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		boolean isAnonymous = auth == null
				|| "anonymousUser".equalsIgnoreCase(auth.getName());
		try {
			if (!isAnonymous && !session.isActive()) {
				String userName = auth.getName();
				OpenIdConnectUserDetails userDetails = (OpenIdConnectUserDetails) auth.getPrincipal();

				String accessToken = httpSession.getAttribute("accessToken").toString();
				String refreshToken = httpSession.getAttribute("refreshToken").toString();
				session.login(userDetails.getSub(),userDetails.getUserEmail(),userDetails.getUsername(),
						userDetails.getGivenname(),userDetails.getFamilyname(),accessToken,refreshToken);

				log.info("User has logged in via OIDC. {}", userName);
			}
			chain.doFilter(req, res);
		} catch (Exception e) {
			log.error("ERROR",e);
			log.error(SystemMessages.KERNEL_UNKNOWN.message(e.getMessage()));
		}

	}

	/**
	 * Redirecting the user to the login page in this case
	 *
	 * @param req the Servlet request
	 * @param res the servlet response
	 * @param chain the filter chain
	 */
	@Override
	protected String getRedirect(ServletRequest req, ServletResponse res, FilterChain chain) {

		if ("true".equalsIgnoreCase(getOAF().getProperty(OAFProperties.SYS_AUTHENTICATION_OIDC_PUBLIC_REQUIRED))) {
			return ((HttpServletRequest) req).getContextPath() + "/login";
		}
		return RedirectUtil.combineURLSegment(((HttpServletRequest) req).getContextPath(), getFullLandingPageName());
	}
}
