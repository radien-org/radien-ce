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

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import io.radien.api.OAFProperties;
import io.radien.kernel.messages.SystemMessages;
import io.radien.security.openid.model.OpenIdConnectUserDetails;
import io.radien.webapp.RedirectUtil;

/**
 * OIDC filter that handles per request OIDC authentication
 *
 * @author Marco Weiland
 */
public class OIDCAuthorizationFilter extends AuthorizationFilter {

	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(OIDCAuthorizationFilter.class);

//	@Inject
//	private SessionManager sessionManager;

	@Override
	protected void process(ServletRequest req, ServletResponse res, FilterChain chain) {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		boolean isAnonymous = auth == null
				|| "anonymousUser".equalsIgnoreCase(SecurityContextHolder.getContext().getAuthentication().getName());
		try {
			if (!isAnonymous && sessionHandler.getUser(request) == null) {
				String userName = SecurityContextHolder.getContext().getAuthentication().getName();
				OpenIdConnectUserDetails userDetails = (OpenIdConnectUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//				sessionManager.externalLogin(userName, request, response, userDetails);
				log.info("User has logged in via OIDC. {}", userName);
			}
			chain.doFilter(req, res);
		} catch (Exception e) {
			log.error("ERROR",e);
			log.error(SystemMessages.KERNEL_UNKNOWN.message(e.getMessage()));
		}

	}

	@Override
	protected String getRedirect(ServletRequest req, ServletResponse res, FilterChain chain) {

		if ("true".equalsIgnoreCase(getOAF().getProperty(OAFProperties.SYS_AUTHENTICATION_OIDC_PUBLIC_REQUIRED))) {
			return ((HttpServletRequest) req).getContextPath() + "/login";
		}
		return RedirectUtil.combineURLSegment(((HttpServletRequest) req).getContextPath(), getFullLandingPageName());
	}
}