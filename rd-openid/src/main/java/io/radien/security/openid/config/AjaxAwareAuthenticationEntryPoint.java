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
package io.radien.security.openid.config;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

/**
 * @author Marco Weiland
 *
 */
public class AjaxAwareAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

	public AjaxAwareAuthenticationEntryPoint(final String loginFormUrl) {
		super(loginFormUrl);
	}

	/**
	 * Sends HTTP status 401 (Unauthorized) when the <b>X-Requested-With</b>
	 * header equals <b>XMLHttpRequest</b>. Otherwise redirects to the login
	 * page.
	 */
	@Override
	public void commence(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response,
			AuthenticationException authException) throws IOException, javax.servlet.ServletException {
		final String header = request.getHeader("X-Requested-With");
		final boolean isAjaxRequest = "XMLHttpRequest".equalsIgnoreCase(header);

		if (isAjaxRequest) {
			response.sendError(HttpStatus.UNAUTHORIZED.value());
		} else {
			super.commence(request, response, authException);
		}
	}

}
