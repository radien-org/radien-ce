/**
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

import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.SystemException;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.radien.security.openid.model.OpenIdConnectUserDetails;

/**
 * @author Marco Weiland
 */
public class OpenIdConnectFilter extends AbstractAuthenticationProcessingFilter {
	@Value("${auth.clientId}")
	private String clientId;

	@Value("${auth.issuer}")
	private String issuer;

	@Value("${auth.jwkUrl}")
	private String jwkUrl;

	private OAuth2RestTemplate restTemplate;

	public OpenIdConnectFilter(String defaultFilterProcessesUrl) {
		super(defaultFilterProcessesUrl);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		OAuth2AccessToken token;

		logger.info("Attempting auth");

		try {
			token = restTemplate.getAccessToken();
		} catch (OAuth2Exception e) {
			throw new BadCredentialsException(GenericErrorCodeMessage.AUTHORIZATION_ERROR.toString(), e);
		}

		try {
			final String idToken = token.getAdditionalInformation().get("id_token").toString();
			final String kid = JwtHelper.headers(idToken).get("kid");

			final Jwt decodedToken = JwtHelper.decodeAndVerify(idToken, verifier(kid));

			final Map<String, String> authInfo = new ObjectMapper().readValue(decodedToken.getClaims(),
					new TypeReference<Map<String, String>>() {
					});
			verifyClaims(authInfo);

			final OpenIdConnectUserDetails user = new OpenIdConnectUserDetails(authInfo);
			logger.info("Authentication attempt finished");
			HttpSession session = request.getSession(true);
			session.setAttribute("accessToken",token.getValue());
			session.setAttribute("refreshToken",token.getRefreshToken().getValue());
			return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
		} catch (Exception e) {
			throw new BadCredentialsException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString(), e);
		}
	}

	private RsaVerifier verifier(String kid) {
		try{
			final JwkProvider provider = new UrlJwkProvider(new URL(jwkUrl));
			final Jwk jwk = provider.get(kid);

			return new RsaVerifier((RSAPublicKey) jwk.getPublicKey());
		}catch (Exception exception){
			logger.error(GenericErrorCodeMessage.GENERIC_ERROR.toString(), exception);
			return null;
		}
	}

	private void verifyClaims(Map<String, String> claims) {
		final Integer exp = Integer.valueOf(claims.get("exp"));
		final Date expireDate = new Date(exp * 1000L);
		final Date now = new Date();

		if (expireDate.before(now) || !claims.get("iss").equals(issuer) || !claims.get("aud").equals(clientId)) {
			logger.error(GenericErrorCodeMessage.GENERIC_ERROR.toString());
		}
	}

	public void setRestTemplate(OAuth2RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

}
