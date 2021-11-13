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

import com.nimbusds.jose.JWSObject;
import com.nimbusds.oauth2.sdk.AccessTokenResponse;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.AuthorizationCodeGrant;
import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import com.nimbusds.oauth2.sdk.AuthorizationRequest;
import com.nimbusds.oauth2.sdk.AuthorizationResponse;
import com.nimbusds.oauth2.sdk.AuthorizationSuccessResponse;
import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.Scope;
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
import io.radien.security.openid.validation.TokenValidator;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
    @ConfigProperty(name = "auth.appBaseContext", defaultValue = "/web")
    private String appBaseContext;

    @Inject
    private SecurityContext securityContext;

    @Inject
    private TokenValidator tokenValidator;


    /**
     * Intercept the request and In case of being a trigger URI, It will start
     * an authentication based on Authorization Code Flow, compounded by the following steps:
     * 1 - Prepare an Authorization code request
     * 2 - Send it to Identity Server
     * 3 - Via callback URI (Return), process the status code and obtains the Authorization Code
     * 4 - With the obtained Authorization code, prepares an Access Code request.
     * 5 - Obtains an validate the Access Code
     *
     * @param servletRequest servlet request parameter (See {@link ServletRequest}
     * @param servletResponse servlet response parameter (See {@link ServletResponse}
     * @param chain filter chain parameter (See {@link FilterChain}
     * @throws IOException in case of any issue doing I/O processing
     * @throws ServletException in case of any issue processing servlet stuff
     */
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

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
            response.sendRedirect(getAppContextURL(request));
            chain.doFilter(request, response);
        }
        catch (URISyntaxException | AuthorizationCodeRequestException | TokenRequestException | InvalidAccessTokenException e) {
            log.error("An internal error occurred while trying to authenticate the user.", e);
            this.securityContext.clear();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Failed authentication..");
        }
    }

    /**
     * Performs the Authorization Code Request operation
     * @param servletResponse instance of servlet response used to redirect request to Identity Id server
     * @throws URISyntaxException in case of parsing error regarding userAuthorizationUri parameter
     * @throws IOException in case of error during redirection to the Identity Id server
     */
    protected void requestAuthzCode(HttpServletResponse servletResponse) throws URISyntaxException, IOException {
        URI authEndpoint = new URI(this.userAuthorizationUri);
        ClientID clientID = new ClientID(this.clientId);
        Scope scope = new Scope("openid", "email", "profile");

        URI callback = new URI(getCallbackURI());

        State state = new State();
        securityContext.setState(state);

        AuthorizationRequest request = new AuthorizationRequest.Builder(
                new ResponseType(ResponseType.Value.CODE), clientID)
                .scope(scope)
                .state(state)
                .redirectionURI(callback)
                .endpointURI(authEndpoint)
                .build();

        servletResponse.sendRedirect(request.toURI().toString());
    }

    /**
     * Corresponds to the final part of authorization code request operation.
     * OpenId has processed the operation and now is communicating the response
     * via callback URL, the status code and the code in self are store as
     * Query String parameters
     * @param request used as parameter to read/parse the callback URI
     * @return in case of success returns a instance of {@link AuthorizationCode} containing
     * the authorization code
     * @throws AuthorizationCodeRequestException in case of any issue regarding authorization code operation
     */
    protected AuthorizationCode processAuthCodeCallback(HttpServletRequest request) throws AuthorizationCodeRequestException {
        try {
            AuthorizationResponse response = AuthorizationResponse.parse(new URI(getCompleteURL(request)));

            if (!this.securityContext.getState().equals(response.getState())) {
                throw new AuthorizationCodeRequestException("Invalid State");
            }

            if (!response.indicatesSuccess()) {
                ErrorObject errorObject = response.toErrorResponse().getErrorObject();
                String msg = errorObject.getCode() + " " + errorObject.getDescription() + " " + errorObject.getHTTPStatusCode();
                throw new AuthorizationCodeRequestException(msg);
            }

            AuthorizationSuccessResponse successResponse = response.toSuccessResponse();
            return successResponse.getAuthorizationCode();
        }
        catch (URISyntaxException | ParseException e) {
            throw new AuthorizationCodeRequestException(e);
        }
    }

    /**
     * Given a previously obtained Authorization Code, performs the Access Token request operation
     * @param code authorization code obtained previously
     * @return in case of success returns a instance of {@link Tokens} containing
     * the access token and refresh token as well
     * @throws TokenRequestException in case of any occurred during request token operation
     */
    protected Tokens requestAccessToken(AuthorizationCode code) throws TokenRequestException {
        try {
            URI callback = new URI(this.getCallbackURI());
            AuthorizationGrant codeGrant = new AuthorizationCodeGrant(code, callback);

            ClientID clientID = new ClientID(this.clientId);
            Secret clientSecretObj = new Secret(this.clientSecret);
            ClientAuthentication clientAuth = new ClientSecretBasic(clientID, clientSecretObj);

            URI tokenEndpoint = new URI(this.accessTokenUri);
            TokenRequest request = new TokenRequest(tokenEndpoint, clientAuth, codeGrant);
            TokenResponse response = TokenResponse.parse(request.toHTTPRequest().send());

            if (!response.indicatesSuccess()) {
                ErrorObject errorObject = response.toErrorResponse().getErrorObject();
                String msg = errorObject.getCode() + " " + errorObject.getDescription() + " " + errorObject.getHTTPStatusCode();
                throw new TokenRequestException(msg);
            }

            AccessTokenResponse successResponse = response.toSuccessResponse();
            return successResponse.getTokens();
        }
        catch (URISyntaxException | IOException | ParseException e) {
            throw new TokenRequestException(e);
        }
    }

    /**
     * Check if access token is valid and assemblies a UserDetails bean (Object that encapsulates information regarding
     * the authenticated user)
     * @param accessToken OpenIDConnect bean that encapsulates Access Token information
     * @return instance of UserDetails bean assembled using data from access token payload
     * @throws InvalidAccessTokenException in case of any validation issue found regarding Access token
     */
    protected UserDetails validateAccessToken(AccessToken accessToken) throws InvalidAccessTokenException {
        try {
            JWSObject jwsObject = JWSObject.parse(accessToken.getValue());
            tokenValidator.validate(jwsObject);
            return assemblyUserDetails(jwsObject.getPayload().toJSONObject());
        } catch (java.text.ParseException p) {
            throw new InvalidAccessTokenException(p);
        }
    }

    /**
     * Given a map obtained from a Access token payload, mounts a User Details bean
     * @param mainMap map containing information regarding user
     * @return instance of UserDetails
     */
    protected UserDetails assemblyUserDetails(Map<String, Object> mainMap) {
        List<String> expectedKeys = Arrays.asList("sub", "email", "preferred_username",
                "given_name", "family_name");
        Map<String, String> subMap = mainMap.entrySet().stream().filter(m -> expectedKeys.contains(m.getKey())).
                collect(Collectors.toMap(Map.Entry::getKey, x -> x.getValue().toString()));
        return new OpenIdConnectUserDetails(subMap);
    }

    /**
     * Check if current request corresponds to the URI that triggers the OIDC authentication
     * process, or is related with
     * @param request Http servlet request to be used as parameter to check the URI
     * @return true if corresponds to triggering URI, otherwise false
     */
    protected boolean isTriggeringURI(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return requestURI.endsWith(LOGIN_URI) || requestURI.endsWith(AUTH_CALLBACK_URI);
    }

    /**
     * Checks if the current request (URL) corresponds to the authorization code callback
     * @param request Http servlet request to be used as parameter to check the URI
     * @return true if corresponds to callback, otherwise false
     */
    protected boolean isAuthorizationCodeCallbackURI(HttpServletRequest request) {
        return request.getRequestURI().endsWith(AUTH_CALLBACK_URI);
    }

    /**
     * Assemblies an URI to be used as callback during Authorization Code request
     * @return String that corresponds to Authorization Code Callback
     */
    protected String getCallbackURI() {
        return this.redirectUri + AUTH_CALLBACK_URI;
    }

    /**
     * Stores information to be referred in other contexts
     * @param userDetails Object that encapsulates information regarding the authenticated user
     * @param tokens Tokens obtained during OIDC authentication
     * @param servletRequest Http servlet request used as parameter to access session object
     */
    protected void saveInformation(UserDetails userDetails, Tokens tokens, HttpServletRequest servletRequest) {
        this.securityContext.setAccessToken(tokens.getAccessToken());
        this.securityContext.setRefreshToken(tokens.getRefreshToken());
        this.securityContext.setUserDetails(userDetails);
        servletRequest.getSession().setAttribute("accessToken", tokens.getAccessToken().getValue());
        servletRequest.getSession().setAttribute("refreshToken", tokens.getRefreshToken().getValue());
    }

    /**
     * Retrieve/assemblies the URL referred by a Http Request object, what includes
     * the Query String part
     * @param request Http servlet request used as parameter
     * @return String describing the complete URL
     */
    protected String getCompleteURL(HttpServletRequest request) {
        StringBuffer requestURL = request.getRequestURL();
        if (request.getQueryString() != null) {
            requestURL.append("?").append(request.getQueryString());
        }
        return requestURL.toString();
    }

    /**
     * Retrieves/assemblies application context URL
     * @param request Http servlet request used as parameter
     * @return String describing the Application context URL
     */
    protected String getAppContextURL(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append(request.getScheme()).append("://");
        sb.append(request.getServerName());
        if (request.getServerPort() > 0) {
            sb.append(":");
            sb.append(request.getServerPort());
        }
        if (!this.appBaseContext.startsWith("/")) {
            sb.append("/");
        }
        sb.append(this.appBaseContext);
        return sb.toString();
    }


}