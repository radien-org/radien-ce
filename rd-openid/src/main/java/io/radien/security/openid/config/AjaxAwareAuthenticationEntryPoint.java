/**
 *
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
