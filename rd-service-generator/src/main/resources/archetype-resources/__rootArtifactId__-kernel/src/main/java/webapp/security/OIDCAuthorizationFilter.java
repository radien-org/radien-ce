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
package ${package}.webapp.security;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ${package}.api.OAFProperties;
import ${package}.webapp.RedirectUtil;

/**
 * OIDC filter that handles per request OIDC authentication
 * @author Rajesh Gavvala
 * @author Marco Weiland
 */
public class OIDCAuthorizationFilter extends AuthorizationFilter {

	private static final long serialVersionUID = 6812608123262000022L;
	private static final Logger log = LoggerFactory.getLogger(OIDCAuthorizationFilter.class);

	@Override
	protected void process(ServletRequest req, ServletResponse res, FilterChain chain) {

	}

	@Override
	protected String getRedirect(ServletRequest req, ServletResponse res, FilterChain chain) {
		return RedirectUtil.combineURLSegment(((HttpServletRequest) req).getContextPath(), getFullLandingPageName());
	}
}
