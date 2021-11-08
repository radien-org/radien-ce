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
package io.radien.security.openid.filter;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.oauth2.sdk.AccessTokenResponse;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.AuthorizationCodeGrant;
import com.nimbusds.oauth2.sdk.AuthorizationErrorResponse;
import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import com.nimbusds.oauth2.sdk.AuthorizationRequest;
import com.nimbusds.oauth2.sdk.AuthorizationResponse;
import com.nimbusds.oauth2.sdk.AuthorizationSuccessResponse;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.TokenErrorResponse;
import com.nimbusds.oauth2.sdk.TokenRequest;
import com.nimbusds.oauth2.sdk.TokenResponse;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.Tokens;
import io.radien.exception.AuthorizationCodeRequestException;
import io.radien.exception.InvalidAccessTokenException;
import io.radien.exception.TokenRequestException;
import io.radien.security.openid.context.SecurityContextHolder;
import io.radien.security.openid.context.client.ClientContext;
import io.radien.security.openid.model.Authentication;
import io.radien.security.openid.model.OpenIdConnectUserDetails;
import io.radien.security.openid.model.SimpleGrantedAuthority;
import io.radien.security.openid.model.UsernamePasswordAuthenticationToken;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Marco Weiland
 */
@WebFilter("/*")
public class OpenIdConnectFilter implements Filter {

    private static final String LOGIN_URI = "/login";
    private static final String AUTH_CALLBACK_URI = "/auth";

    private static Logger log = LoggerFactory.getLogger(OpenIdConnectFilter.class);

    @Inject
    @ConfigProperty(name="SCRIPT_CLIENT_ID_VALUE")
    private String clientId;

    @Inject
    @ConfigProperty(name="SCRIPT_CLIENT_SECRET_VALUE")
    private String clientSecret;

    @Inject
    @ConfigProperty(name="AUTH_ACCESS_TOKEN_URI")
    private String accessTokenUri;

    @Inject
    @ConfigProperty(name="AUTH_USER_AUTHORIZATION_URI")
    private String userAuthorizationUri;

    @Inject
    @ConfigProperty(name="auth.redirectUri")
    private String redirectUri;

    @Inject
    @ConfigProperty(name="AUTH_LOGOUT_REDIRECT_URI_LOCAL")
    private String logoutRedirectUri;

    @Inject
    @ConfigProperty(name="api")
    private String apiPrefix;

    @Inject
    @ConfigProperty(name="auth.csrfEnabled")
    private boolean csrfEnabled;

    @Inject
    @ConfigProperty(name="auth.issuer")
    private String issuer;

    @Inject
    @ConfigProperty(name="auth.jwkUrl")
    private String jwkUrl;

    @Inject
    private ClientContext clientContext;

    // State string for pairing the response to the request
    private State state;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    protected void doFilter(HttpServletRequest request, HttpServletResponse response,
                            FilterChain chain) throws IOException, ServletException {

        if (!isTriggeringURI(request)) {
            chain.doFilter(request, response);
            return;
        }

        try {
            if (isAuthorizationCodeCallbackURI(request)) {
                AuthorizationCode code = processAuthCodeCallback(request);
                Tokens tokens = requestAccessToken(request, code);
                Authentication authentication = validateAccessToken(tokens.getAccessToken());
                saveInformation(authentication, tokens, request);
            }
            else {
                AccessToken accessToken = this.clientContext.getAccessToken();
                if (accessToken == null) {
                    requestAuthzCode(response);
                    return;
                }
            }
            response.sendRedirect(getBaseURL());
            chain.doFilter(request, response);
        }
        catch (URISyntaxException | AuthorizationCodeRequestException | TokenRequestException | InvalidAccessTokenException e) {
            SecurityContextHolder.clearContext();
            log.error("An internal error occurred while trying to authenticate the user.", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Failed authentication..");
            response.setContentType("application/json; charset=UTF-8");
        }
    }

    protected void requestAuthzCode(HttpServletResponse servletResponse) throws URISyntaxException, IOException {
        // The authorisation endpoint of the server
        URI authEndpoint = new URI(this.userAuthorizationUri);

        // The client identifier provisioned by the server
        ClientID clientID = new ClientID(this.clientId);

        // The requested scope values for the token
        Scope scope = new Scope("openid", "email", "profile");

        // The client callback URI, typically pre-registered with the server
        URI callback = new URI(getCallbackURI());

        // Generate random state string for pairing the response to the request
        this.state = new State();
        clientContext.setState(state);

        // Build the request
        AuthorizationRequest request = new AuthorizationRequest.Builder(
                new ResponseType(ResponseType.Value.CODE), clientID)
                .scope(scope)
                .state(state)
                .redirectionURI(callback)
                .endpointURI(authEndpoint)
                .build();

        // Use this URI to send the end-user's browser to the server
        URI requestURI = request.toURI();
        servletResponse.sendRedirect(requestURI.toString());
    }

    protected AuthorizationCode processAuthCodeCallback(HttpServletRequest request) throws AuthorizationCodeRequestException {
        try {
            // Parse the authorisation response from the callback URI
            AuthorizationResponse response = AuthorizationResponse.parse(new URI(getCompleteURL(request)));

            // Check the returned state parameter, must match the original
            if (!state.equals(response.getState())) {
                throw new AuthorizationCodeRequestException("Unexpected or tampered response, stop!!!");
            }

            if (!response.indicatesSuccess()) {
                // The request was denied or some error occurred
                AuthorizationErrorResponse errorResponse = response.toErrorResponse();
                throw new AuthorizationCodeRequestException(errorResponse.getErrorObject().toString());
            }

            AuthorizationSuccessResponse successResponse = response.toSuccessResponse();

            // Retrieve the authorisation code, to be used later to exchange the code for
            // an access token at the token endpoint of the server
            return successResponse.getAuthorizationCode();
        }
        catch (URISyntaxException | ParseException e) {
            throw new AuthorizationCodeRequestException(e);
        }
    }

    protected Tokens requestAccessToken(HttpServletRequest servletRequest, AuthorizationCode code) throws TokenRequestException {
        try {
            // Construct the code grant from the code obtained from the authz endpoint
            // and the original callback URI used at the auth endpoint
            URI callback = new URI(this.getCallbackURI());
            AuthorizationGrant codeGrant = new AuthorizationCodeGrant(code, callback);

            // The credentials to authenticate the client at the token endpoint
            ClientID clientID = new ClientID(this.clientId);
            Secret clientSecret = new Secret(this.clientSecret);
            ClientAuthentication clientAuth = new ClientSecretBasic(clientID, clientSecret);

            // The token endpoint
            URI tokenEndpoint = new URI(this.accessTokenUri);

            // Make the token request
            TokenRequest request = new TokenRequest(tokenEndpoint, clientAuth, codeGrant);

            TokenResponse response = TokenResponse.parse(request.toHTTPRequest().send());

            if (!response.indicatesSuccess()) {
                // We got an error response...
                TokenErrorResponse errorResponse = response.toErrorResponse();
                throw new TokenRequestException(errorResponse.toString());
            }

            AccessTokenResponse successResponse = response.toSuccessResponse();
            return successResponse.getTokens();
        }
        catch (URISyntaxException | IOException | ParseException e) {
            throw new TokenRequestException(e);
        }
    }

    protected Authentication validateAccessToken(AccessToken accessToken) throws InvalidAccessTokenException {
        Authentication authentication = null;
        try {
            JWSObject jwsObject = JWSObject.parse(accessToken.getValue());

            String issuer = ConfigProvider.getConfig().getValue("auth.issuer", String.class);
            String jwkUrl = ConfigProvider.getConfig().getValue("auth.jwkUrl", String.class);

            JWSHeader header = jwsObject.getHeader();
            String kid = header.getKeyID();

            final JwkProvider provider = new UrlJwkProvider(new URL(jwkUrl));
            final Jwk jwk = provider.get(kid);

            JWSVerifier verifier = new RSASSAVerifier((RSAPublicKey) jwk.getPublicKey());
            if (!jwsObject.verify(verifier)) {
                throw new InvalidAccessTokenException("Invalid verify");
            }

            Payload payload = jwsObject.getPayload();
            try (JsonReader reader = Json.createReader(new StringReader(payload.toString()))) {
                JsonObject jsonObject = reader.readObject();

                if (!issuer.equals(jsonObject.getString("iss"))) {
                    throw new InvalidAccessTokenException("Invalid iss");
                }

                if (!jsonObject.getString("typ").equals("Bearer")) {
                    throw new InvalidAccessTokenException("Invalid type");
                }

                LocalDateTime exp = LocalDateTime.ofInstant(Instant.ofEpochSecond(jsonObject.getJsonNumber("exp").longValue()), ZoneId.systemDefault());
                if (exp.isBefore(LocalDateTime.now())) {
                    throw new InvalidAccessTokenException("Token Expired");
                }

                OpenIdConnectUserDetails user = new OpenIdConnectUserDetails(jsonObject);
                authentication = new UsernamePasswordAuthenticationToken(user,
                        null, Collections.singletonList(new SimpleGrantedAuthority("USER")));

            }
        } catch (java.text.ParseException | MalformedURLException | JwkException | JOSEException e) {
            log.error("Unable to parse Access Token", e);
        }
        return authentication;
    }

    protected boolean isTriggeringURI(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return requestURI.endsWith(LOGIN_URI) || requestURI.endsWith(AUTH_CALLBACK_URI);
    }

    protected boolean isAuthorizationCodeCallbackURI(HttpServletRequest request) {
        return request.getRequestURI().endsWith(AUTH_CALLBACK_URI);
    }

    protected String getCallbackURI() {
        return this.redirectUri + AUTH_CALLBACK_URI;
    }

    protected void saveInformation(Authentication authentication, Tokens tokens, HttpServletRequest servletRequest) {
        this.clientContext.setAccessToken(tokens.getAccessToken());
        this.clientContext.setRefreshToken(tokens.getRefreshToken());
        servletRequest.getSession().setAttribute("accessToken", tokens.getAccessToken().getValue());
        servletRequest.getSession().setAttribute("refreshToken", tokens.getRefreshToken());
        servletRequest.getSession().setAttribute("Authentication", authentication);
    }

    protected String getCompleteURL(HttpServletRequest request) {
        StringBuffer requestURL = request.getRequestURL();
        if (request.getQueryString() != null) {
            requestURL.append("?").append(request.getQueryString());
        }
        return requestURL.toString();
    }

    protected String getBaseURL() {
        return redirectUri.substring(0, redirectUri.lastIndexOf("/"));
    }
}

//
//import io.radien.exception.GenericErrorCodeMessage;
//import java.net.URL;
//import java.security.interfaces.RSAPublicKey;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.Date;
//import java.util.Map;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.jwt.Jwt;
//import org.springframework.security.jwt.JwtHelper;
//import org.springframework.security.jwt.crypto.sign.RsaVerifier;
//import org.springframework.security.oauth2.client.OAuth2RestTemplate;
//import org.springframework.security.oauth2.common.OAuth2AccessToken;
//import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
//import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
//
//import com.auth0.jwk.Jwk;
//import com.auth0.jwk.JwkProvider;
//import com.auth0.jwk.UrlJwkProvider;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import io.radien.security.openid.model.OpenIdConnectUserDetails;
//
///**
// * @author Marco Weiland
// */
//public class OpenIdConnectFilter extends AbstractAuthenticationProcessingFilter {
//	@Value("${auth.clientId}")
//	private String clientId;
//
//	@Value("${auth.issuer}")
//	private String issuer;
//
//	@Value("${auth.jwkUrl}")
//	private String jwkUrl;
//
//	private OAuth2RestTemplate restTemplate;
//
//	public OpenIdConnectFilter(String defaultFilterProcessesUrl) {
//		super(defaultFilterProcessesUrl);
//	}
//
//	@Override
//	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
//			throws AuthenticationException {
//		OAuth2AccessToken token;
//
//		logger.info("Attempting auth");
//
//		try {
//			token = restTemplate.getAccessToken();
//		} catch (OAuth2Exception e) {
//			throw new BadCredentialsException(GenericErrorCodeMessage.AUTHORIZATION_ERROR.toString(), e);
//		}
//
//		try {
//			final String idToken = token.getAdditionalInformation().get("id_token").toString();
//			final String kid = JwtHelper.headers(idToken).get("kid");
//
//			final Jwt decodedToken = JwtHelper.decodeAndVerify(idToken, verifier(kid));
//
//			final Map<String, String> authInfo = new ObjectMapper().readValue(decodedToken.getClaims(),
//					new TypeReference<Map<String, String>>() {
//					});
//			verifyClaims(authInfo);
//
//			final OpenIdConnectUserDetails user = new OpenIdConnectUserDetails(authInfo);
//			logger.info("Authentication attempt finished");
//			HttpSession session = request.getSession(true);
//			session.setAttribute("accessToken",token.getValue());
//			session.setAttribute("refreshToken",token.getRefreshToken().getValue());
//			return new UsernamePasswordAuthenticationToken(user, null, Collections.singletonList(new SimpleGrantedAuthority("USER")));
//		} catch (Exception e) {
//			throw new BadCredentialsException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString(), e);
//		}
//	}
//
//	private RsaVerifier verifier(String kid) {
//		try{
//			final JwkProvider provider = new UrlJwkProvider(new URL(jwkUrl));
//			final Jwk jwk = provider.get(kid);
//
//			return new RsaVerifier((RSAPublicKey) jwk.getPublicKey());
//		}catch (Exception exception){
//			logger.error(GenericErrorCodeMessage.GENERIC_ERROR.toString(), exception);
//			return null;
//		}
//	}
//
//	private void verifyClaims(Map<String, String> claims) {
//		final Integer exp = Integer.valueOf(claims.get("exp"));
//		final Date expireDate = new Date(exp * 1000L);
//		final Date now = new Date();
//
//		if (expireDate.before(now) || !claims.get("iss").equals(issuer) || !claims.get("aud").equals(clientId)) {
//			logger.error(GenericErrorCodeMessage.GENERIC_ERROR.toString());
//		}
//	}
//
//	public void setRestTemplate(OAuth2RestTemplate restTemplate) {
//		this.restTemplate = restTemplate;
//	}
//
//}
