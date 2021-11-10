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
import io.radien.security.openid.context.SecurityContext;
import io.radien.security.openid.model.OpenIdConnectUserDetails;
import io.radien.security.openid.model.UserDetails;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Marco Weiland
 */
public class OpenIdConnectFilter implements Filter {

    private static final String LOGIN_URI = "/login";
    private static final String AUTH_CALLBACK_URI = "/auth";

    private static final Logger log = LoggerFactory.getLogger(OpenIdConnectFilter.class);

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
    @ConfigProperty(name = "AUTH_ISSUER")
    private String issuer;

    @Inject
    @ConfigProperty(name = "AUTH_JWKURL")
    private String jwkUrl;

    @Inject
    @ConfigProperty(name = "auth.appBaseContext", defaultValue = "/web")
    private String appBaseContext;

    @Inject
    private SecurityContext securityContext;

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
                Tokens tokens = requestAccessToken(code);
                UserDetails userDetails = validateAccessToken(tokens.getAccessToken());
                saveInformation(userDetails, tokens, request);
            }
            else {
                AccessToken accessToken = this.securityContext.getAccessToken();
                if (accessToken == null) {
                    requestAuthzCode(response);
                    return;
                }
            }
            response.sendRedirect(getAppBaseURL(request));
            chain.doFilter(request, response);
        }
        catch (URISyntaxException | AuthorizationCodeRequestException | TokenRequestException | InvalidAccessTokenException e) {
            log.error("An internal error occurred while trying to authenticate the user.", e);
            this.securityContext.clear();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Failed authentication..");
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
        State state = new State();
        securityContext.setState(state);

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
            if (!this.securityContext.getState().equals(response.getState())) {
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

    protected Tokens requestAccessToken(AuthorizationCode code) throws TokenRequestException {
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

    protected UserDetails validateAccessToken(AccessToken accessToken) throws InvalidAccessTokenException {
        try {
            JWSObject jwsObject = JWSObject.parse(accessToken.getValue());
            JWSHeader header = jwsObject.getHeader();

            String kid = header.getKeyID();
            final JwkProvider provider = new UrlJwkProvider(new URL(jwkUrl));
            final Jwk jwk = provider.get(kid);

            JWSVerifier verifier = new RSASSAVerifier((RSAPublicKey) jwk.getPublicKey());
            if (!jwsObject.verify(verifier)) {
                throw new InvalidAccessTokenException("Invalid verify");
            }

            Map<String, Object> payloadMap = jwsObject.getPayload().toJSONObject();

            if (!issuer.equals(payloadMap.get("iss"))) {
                throw new InvalidAccessTokenException("Invalid iss");
            }

            if (!payloadMap.get("typ").equals("Bearer")) {
                throw new InvalidAccessTokenException("Invalid type");
            }

            Long expAttr = (Long) payloadMap.get("exp");
            LocalDateTime exp = LocalDateTime.ofInstant(Instant.ofEpochSecond(expAttr), ZoneId.systemDefault());
            if (exp.isBefore(LocalDateTime.now())) {
                throw new InvalidAccessTokenException("Token Expired");
            }

            return assemblyUserDetails(payloadMap);

        } catch (java.text.ParseException | MalformedURLException | JwkException | JOSEException e) {
            throw new InvalidAccessTokenException(e);
        }
    }

    protected UserDetails assemblyUserDetails(Map<String, Object> mainMap) {
        List<String> expectedKeys = Arrays.asList("sub", "email", "preferred_username",
                "given_name", "family_name");
        Map<String, String> subMap = mainMap.entrySet().stream().filter(m -> expectedKeys.contains(m.getKey())).
                collect(Collectors.toMap(Map.Entry::getKey, x -> x.getValue().toString()));
        return new OpenIdConnectUserDetails(subMap);
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

    protected void saveInformation(UserDetails userDetails, Tokens tokens, HttpServletRequest servletRequest) {
        this.securityContext.setAccessToken(tokens.getAccessToken());
        this.securityContext.setRefreshToken(tokens.getRefreshToken());
        this.securityContext.setUserDetails(userDetails);
        servletRequest.getSession().setAttribute("accessToken", tokens.getAccessToken().getValue());
        servletRequest.getSession().setAttribute("refreshToken", tokens.getRefreshToken().getValue());
    }

    protected String getCompleteURL(HttpServletRequest request) {
        StringBuffer requestURL = request.getRequestURL();
        if (request.getQueryString() != null) {
            requestURL.append("?").append(request.getQueryString());
        }
        return requestURL.toString();
    }

    protected String getAppBaseURL(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();

        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int portNumber = request.getServerPort();
        String contextPath = request.getContextPath();
        String servletPath = request.getServletPath();
        String pathInfo = request.getPathInfo();
        String query = request.getQueryString();
        return redirectUri.substring(0, redirectUri.lastIndexOf("/"));
    }


}