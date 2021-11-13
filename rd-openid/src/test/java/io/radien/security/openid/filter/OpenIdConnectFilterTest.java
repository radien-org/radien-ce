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
import com.nimbusds.jose.Payload;
import com.nimbusds.oauth2.sdk.AccessTokenResponse;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.AuthorizationErrorResponse;
import com.nimbusds.oauth2.sdk.AuthorizationRequest;
import com.nimbusds.oauth2.sdk.AuthorizationResponse;
import com.nimbusds.oauth2.sdk.AuthorizationSuccessResponse;
import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.ResponseMode;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.TokenErrorResponse;
import com.nimbusds.oauth2.sdk.TokenResponse;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.oauth2.sdk.token.Tokens;
import io.radien.exception.AuthorizationCodeRequestException;
import io.radien.exception.InvalidAccessTokenException;
import io.radien.exception.TokenRequestException;
import io.radien.security.openid.context.OpenIdSecurityContext;
import io.radien.security.openid.context.SecurityContext;
import io.radien.security.openid.model.OpenIdConnectUserDetails;
import io.radien.security.openid.model.UserDetails;
import io.radien.security.openid.validation.TokenValidator;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static io.radien.api.SystemVariables.ACCESS_TOKEN;
import static io.radien.api.SystemVariables.OIDC_STATE;
import static io.radien.api.SystemVariables.REFRESH_TOKEN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.reflect.Whitebox.setInternalState;

/**
 * Test class for {@link OpenIdConnectFilter}
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ ClientID.class, Scope.class, State.class, AuthorizationRequest.class,
        TokenResponse.class, AuthorizationResponse.class, JWSObject.class})
public class OpenIdConnectFilterTest {

    @InjectMocks
    OpenIdConnectFilter openIdConnectFilter;
    private SecurityContext securityContext;

    @Before
    public void setUp() {
        openIdConnectFilter = new OpenIdConnectFilter();
        setInternalState(openIdConnectFilter, "userAuthorizationUri", "http://test.net/auth");
        setInternalState(openIdConnectFilter, "clientId", "1234");
        setInternalState(openIdConnectFilter, "clientSecret", "1d2e3f4g5h");
        setInternalState(openIdConnectFilter, "securityContext", securityContext);
    }

    /**
     * Test for method {@link OpenIdConnectFilter#requestAuthzCode(HttpServletRequest, HttpServletResponse)}
     * Expected outcome: redirection to authorization endpoint
     *
     * @throws IOException        in case of error during redirection to the Identity Id server
     * @throws URISyntaxException in case of parsing error regarding userAuthorizationUri parameter
     */
    @Test
    public void testRequestAuthzCode() throws IOException, URISyntaxException {
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getSession()).thenReturn(session);

//        securityContext = mock(SecurityContext.class);

        String authEndPoint = "http://opendid.net/auth";
        String callbackUri = "http://radien.net/web/auth";
        setInternalState(openIdConnectFilter, "userAuthorizationUri", authEndPoint);
        setInternalState(openIdConnectFilter, "redirectUri", callbackUri);
//        setInternalState(openIdConnectFilter, "securityContext", securityContext);

        ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);

        this.openIdConnectFilter.requestAuthzCode(request, response);
        verify(response).sendRedirect(uriCaptor.capture());

        String redirectionURL = uriCaptor.getValue();

        assertTrue(redirectionURL.startsWith(authEndPoint));
        assertTrue(redirectionURL.contains("response_type=code"));
        assertTrue(redirectionURL.contains("state="));

        String redirectUriEncoded = "redirect_uri=" + URLEncoder.encode(callbackUri, "UTF-8");
        assertTrue(redirectionURL.contains(redirectUriEncoded));
    }

    /**
     * Test for method {@link OpenIdConnectFilter#requestAccessToken(AuthorizationCode)}
     * for a previously obtained {@link AuthorizationCode}
     * Expected outcome: Success operation returning {@link Tokens}
     *
     * @throws TokenRequestException in case of any occurred during request token operation
     */
    @Test
    public void testRequestAccessToken() throws TokenRequestException, ParseException {
        String authCodeValue = "49321ddf-4fd5-456b-b06f-40a9b77059dd.e1557f6f-992a-47ad-b119-c2601cf1abec.0716628c-5101-4e80-b302-f0ee69a06532";
        AuthorizationCode authorizationCode = new AuthorizationCode(authCodeValue);

        String tokenEndPoint = "http://opendid.net/token";
        String callbackUri = "http://radien.net/web/auth";
        setInternalState(openIdConnectFilter, "accessTokenUri", tokenEndPoint);
        setInternalState(openIdConnectFilter, "redirectUri", callbackUri);
        setInternalState(openIdConnectFilter, "clientId", "1234");
        setInternalState(openIdConnectFilter, "clientSecret", "1d2e3f4g5h");
        setInternalState(openIdConnectFilter, "securityContext", securityContext);

        PowerMockito.mockStatic(TokenResponse.class);

        AccessToken accessToken = new BearerAccessToken("1111111");
        RefreshToken refreshToken = new RefreshToken("22222222");
        Tokens tokens = new Tokens(accessToken, refreshToken);
        TokenResponse tokenResponse = new AccessTokenResponse(tokens);

        when(TokenResponse.parse(any(HTTPResponse.class))).thenReturn(tokenResponse);
        Tokens outcome = this.openIdConnectFilter.requestAccessToken(authorizationCode);
        assertEquals(tokens, outcome);
        assertEquals(accessToken, tokens.getAccessToken());
        assertEquals(refreshToken, tokens.getRefreshToken());
    }

    /**
     * Test for method {@link OpenIdConnectFilter#requestAccessToken(AuthorizationCode)}
     * for a previously obtained {@link AuthorizationCode}
     * Expected outcome: Failure. Simulating error during processing of token
     * request operation. TokenRequestOperation is thrown
     *
     * @throws ParseException in case of any occurred during parsing token operation
     */
    @Test
    public void testRequestAccessTokenFailure() throws ParseException {
        String authCodeValue = "49321ddf-4fd5-456b-b06f-40a9b77059dd.e1557f6f-992a-47ad-b119-c2601cf1abec.0716628c-5101-4e80-b302-f0ee69a06532";
        AuthorizationCode authorizationCode = new AuthorizationCode(authCodeValue);

        String tokenEndPoint = "http://opendid.net/token";
        String callbackUri = "http://radien.net/web/auth";
        setInternalState(openIdConnectFilter, "accessTokenUri", tokenEndPoint);
        setInternalState(openIdConnectFilter, "redirectUri", callbackUri);
        setInternalState(openIdConnectFilter, "clientId", "1234");
        setInternalState(openIdConnectFilter, "clientSecret", "1d2e3f4g5h");
        setInternalState(openIdConnectFilter, "securityContext", securityContext);

        PowerMockito.mockStatic(TokenResponse.class);

        ErrorObject error = new ErrorObject("ATR1", "Error processing request", 400);
        TokenResponse tokenResponse = new TokenErrorResponse(error);

        when(TokenResponse.parse(any(HTTPResponse.class))).thenReturn(tokenResponse);

        TokenRequestException e = assertThrows(TokenRequestException.class,
                () -> openIdConnectFilter.requestAccessToken(authorizationCode));

        String expectedErrorMsg = error.getCode() + " " + error.getDescription() + " " + error.getHTTPStatusCode();
        assertEquals(expectedErrorMsg, e.getMessage());
    }

    /**
     * Test for method {@link OpenIdConnectFilter#requestAccessToken(AuthorizationCode)}
     * for a previously obtained {@link AuthorizationCode}
     * Expected outcome: Failure. Simulating error parsing while assembling parameters
     * to invoke request token endpoint. TokenRequestOperation is thrown
     *
     * @throws ParseException in case of any occurred during parsing operation for uri or token values
     */
    @Test
    public void testRequestAccessTokenException() throws ParseException {
        String authCodeValue = "49321ddf-4fd5-456b-b06f-40a9b77059dd.e1557f6f-992a-47ad-b119-c2601cf1abec.0716628c-5101-4e80-b302-f0ee69a06532";
        AuthorizationCode authorizationCode = new AuthorizationCode(authCodeValue);

        String tokenEndPoint = "http://opendid.net/token";
        String callbackUri = "http://radien.net/web/auth";
        setInternalState(openIdConnectFilter, "accessTokenUri", tokenEndPoint);
        setInternalState(openIdConnectFilter, "redirectUri", callbackUri);
        setInternalState(openIdConnectFilter, "clientId", "1234");
        setInternalState(openIdConnectFilter, "clientSecret", "1d2e3f4g5h");
        setInternalState(openIdConnectFilter, "securityContext", securityContext);

        PowerMockito.mockStatic(TokenResponse.class);

        String msg = "Parsing error";
        when(TokenResponse.parse(any(HTTPResponse.class))).
                thenThrow(new ParseException(msg));

        TokenRequestException e = assertThrows(TokenRequestException.class,
                () -> openIdConnectFilter.requestAccessToken(authorizationCode));

        String expectedErrorMsg = ParseException.class.getName() + ": " + msg;
        assertEquals(expectedErrorMsg, e.getMessage());
    }

    /**
     * Test for method {@link OpenIdConnectFilter#getAppContextURL(HttpServletRequest)}
     */
    @Test
    public void testGetAppContextURL() {
        setInternalState(openIdConnectFilter, "appBaseContext", "web");

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getScheme()).thenReturn("https");
        when(request.getServerName()).thenReturn("int.radien.io");
        when(request.getServerPort()).thenReturn(0);

        String expectedURL = "https://int.radien.io/web";
        String obtainedURL = openIdConnectFilter.getAppContextURL(request);
        assertEquals(expectedURL, obtainedURL);

        HttpServletRequest request2 = mock(HttpServletRequest.class);
        when(request2.getScheme()).thenReturn("https");
        when(request2.getServerName()).thenReturn("server1");
        when(request2.getServerPort()).thenReturn(8443);

        expectedURL = "https://server1:8443/web";
        obtainedURL = openIdConnectFilter.getAppContextURL(request2);
        assertEquals(expectedURL, obtainedURL);
    }

    /**
     * Test for method {@link OpenIdConnectFilter#assemblyUserDetails(Map)}
     */
    @Test
    public void testAssemblyUserDetails() {
        String sub = "85cbf1a9-g393-99bc-9c99-55ff3130d716";
        String email = "bruno.mars@acme.com";
        String userName = "bruno.mars";
        String name = "bruno";
        String surname = "mars";

        Map<String, Object> map = new HashMap<>();
        map.put("sub", sub);
        map.put("email", email);
        map.put("preferred_username", userName);
        map.put("given_name", name);
        map.put("family_name", surname);

        UserDetails userDetails = this.openIdConnectFilter.assemblyUserDetails(map);
        assertEquals(OpenIdConnectUserDetails.class, userDetails.getClass());
        assertEquals(sub, ((OpenIdConnectUserDetails) userDetails).getSub());
        assertEquals(email, ((OpenIdConnectUserDetails) userDetails).getUserEmail());
        assertEquals(userName, userDetails.getUsername());
        assertEquals(name, ((OpenIdConnectUserDetails) userDetails).getGivenname());
        assertEquals(surname, ((OpenIdConnectUserDetails) userDetails).getFamilyname());
    }

    /**
     * Test for method {@link OpenIdConnectFilter#saveInformation(UserDetails, Tokens, HttpServletRequest)}
     */
    @Test
    public void testSaveInformation() {
        SecurityContext securityContext = new OpenIdSecurityContext();
        setInternalState(openIdConnectFilter, "securityContext", securityContext);

        HttpSession session = mock(HttpSession.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getSession()).thenReturn(session);

        UserDetails userDetails = mock(UserDetails.class);
        AccessToken accessToken = new BearerAccessToken("1111111");
        RefreshToken refreshToken = new RefreshToken("22222222");
        Tokens tokens = new Tokens(accessToken, refreshToken);

        openIdConnectFilter.saveInformation(userDetails, tokens, request);
        assertEquals(userDetails, securityContext.getUserDetails());

        ArgumentCaptor<String> sessionAttr = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> objectValue = ArgumentCaptor.forClass(String.class);

        verify(session, times(2)).setAttribute(sessionAttr.capture(), objectValue.capture());

        assertTrue(sessionAttr.getAllValues().contains(ACCESS_TOKEN.getFieldName()));
        assertTrue(sessionAttr.getAllValues().contains(REFRESH_TOKEN.getFieldName()));
        assertTrue(objectValue.getAllValues().contains(accessToken.getValue()));
        assertTrue(objectValue.getAllValues().contains(refreshToken.getValue()));
    }

    /**
     * Test for method {@link OpenIdConnectFilter#getCompleteURL(HttpServletRequest)}
     */
    @Test
    public void testGetCompleteURL() {
        StringBuffer requestURL = new StringBuffer("https://localhost:8443/web/login/auth");
        String queryString = "state=1111&session_state=1233455&code=7e8ff1b3";

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURL()).thenReturn(requestURL);
        when(request.getQueryString()).thenReturn(queryString);

        String expected = requestURL + "?" + queryString;
        String resultURL = openIdConnectFilter.getCompleteURL(request);
        assertEquals(expected, resultURL);
    }

    /**
     * Test for method {@link OpenIdConnectFilter#isTriggeringURI(HttpServletRequest)}
     */
    @Test
    public void testIsTriggeringURI() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/login");

        HttpServletRequest request2 = mock(HttpServletRequest.class);
        when(request2.getRequestURI()).thenReturn("/login/auth");

        HttpServletRequest request3 = mock(HttpServletRequest.class);
        when(request3.getRequestURI()).thenReturn("/test");

        assertTrue(this.openIdConnectFilter.isTriggeringURI(request));
        assertTrue(this.openIdConnectFilter.isTriggeringURI(request2));
        assertFalse(this.openIdConnectFilter.isTriggeringURI(request3));
    }

    /**
     * Test for method {@link OpenIdConnectFilter#isAuthorizationCodeCallbackURI(HttpServletRequest)}
     */
    @Test
    public void testIsAuthorizationCodeCallbackURI() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/web/login");
        assertFalse(this.openIdConnectFilter.isAuthorizationCodeCallbackURI(request));

        HttpServletRequest request2 = mock(HttpServletRequest.class);
        when(request2.getRequestURI()).thenReturn("/web/login/auth");
        assertTrue(this.openIdConnectFilter.isAuthorizationCodeCallbackURI(request2));
    }

    /**
     * Test for method {@link OpenIdConnectFilter#validateAccessToken(AccessToken)}
     */
    @Test
    public void testValidateAccessToken() throws Exception {
        String accessTokenRawValue = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2l";

        TokenValidator tokenValidator = mock(TokenValidator.class);
        doNothing().when(tokenValidator).validate(any(JWSObject.class));

        setInternalState(openIdConnectFilter, "tokenValidator", tokenValidator);

        HashMap<String, Object> map = new HashMap<>();

        JWSObject jwsObject = mock(JWSObject.class);
        Payload payload = new Payload(map);
        PowerMockito.mockStatic(JWSObject.class);
        when(JWSObject.parse(anyString())).thenReturn(jwsObject);
        when(jwsObject.getPayload()).thenReturn(payload);

        AccessToken accessToken = new BearerAccessToken(accessTokenRawValue);
        UserDetails userDetails = this.openIdConnectFilter.validateAccessToken(accessToken);
        assertNotNull(userDetails);
    }

    /**
     * Test for method {@link OpenIdConnectFilter#validateAccessToken(AccessToken)}
     * when failure occurs during access token parsing
     */
    @Test(expected = InvalidAccessTokenException.class)
    public void testValidateAccessTokenFailure() throws Exception {
        String accessTokenRawValue = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2l";

        String msg = "Error parsing token String chain ";

        PowerMockito.mockStatic(JWSObject.class);
        when(JWSObject.parse(anyString())).thenThrow(new java.text.ParseException(msg, 0));

        AccessToken accessToken = new BearerAccessToken(accessTokenRawValue);
        openIdConnectFilter.validateAccessToken(accessToken);
    }

    /**
     * Test for method {@link OpenIdConnectFilter#processAuthCodeCallback(HttpServletRequest)}
     */
    @Test
    public void testProcessAuthCodeCallback() throws ParseException, AuthorizationCodeRequestException, URISyntaxException {
        State state = new State("1");
        AuthorizationCode authorizationCode = new AuthorizationCode("11122222");
        AccessToken accessToken = new BearerAccessToken("11111111111111111");
        ResponseMode responseMode = ResponseMode.JWT;

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(request.getRequestURL()).thenReturn(new StringBuffer("test"));

        SecurityContext securityContext = mock(SecurityContext.class);
        when(session.getAttribute(OIDC_STATE.getFieldName())).thenReturn(state.getValue());
        setInternalState(openIdConnectFilter, "securityContext", securityContext);

        PowerMockito.mockStatic(AuthorizationResponse.class);

        AuthorizationResponse authorizationResponse = new AuthorizationSuccessResponse(
                new URI("test"), authorizationCode, accessToken, state, responseMode);

        when(AuthorizationResponse.parse(any(URI.class))).thenReturn(authorizationResponse);

        AuthorizationCode result = openIdConnectFilter.processAuthCodeCallback(request);
        assertEquals(authorizationCode, result);
    }

    /**
     * Test for method {@link OpenIdConnectFilter#processAuthCodeCallback(HttpServletRequest)}
     * when sates does not match
     */
    @Test(expected = AuthorizationCodeRequestException.class)
    public void testProcessAuthCodeCallbackInvalidState() throws AuthorizationCodeRequestException, URISyntaxException, ParseException {
        State state = new State("1");

        AuthorizationCode authorizationCode = new AuthorizationCode("11122222");
        AccessToken accessToken = new BearerAccessToken("11111111111111111");
        ResponseMode responseMode = ResponseMode.JWT;

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(request.getRequestURL()).thenReturn(new StringBuffer("test"));

        SecurityContext securityContext = mock(SecurityContext.class);
        when(session.getAttribute(OIDC_STATE.getFieldName())).thenReturn("222");
        setInternalState(openIdConnectFilter, "securityContext", securityContext);

        PowerMockito.mockStatic(AuthorizationResponse.class);

        AuthorizationResponse authorizationResponse = new AuthorizationSuccessResponse(
                new URI("test"), authorizationCode, accessToken, state, responseMode);

        when(AuthorizationResponse.parse(any(URI.class))).thenReturn(authorizationResponse);

        openIdConnectFilter.processAuthCodeCallback(request);
    }

    /**
     * Test for method {@link OpenIdConnectFilter#processAuthCodeCallback(HttpServletRequest)}
     * when parsing issues occurs
     */
    @Test(expected = AuthorizationCodeRequestException.class)
    public void testProcessAuthCodeCallbackInvalidParsing() throws AuthorizationCodeRequestException, ParseException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURL()).thenReturn(new StringBuffer("test"));

        PowerMockito.mockStatic(AuthorizationResponse.class);

        when(AuthorizationResponse.parse(any(URI.class))).thenThrow(new ParseException("error parsing"));
        openIdConnectFilter.processAuthCodeCallback(request);
    }

    /**
     * Test for method {@link OpenIdConnectFilter#processAuthCodeCallback(HttpServletRequest)}
     * when response with invalid status is created
     */
    @Test(expected = AuthorizationCodeRequestException.class)
    public void testProcessAuthCodeCallbackInvalidResponse() throws AuthorizationCodeRequestException, URISyntaxException, ParseException {
        State state = new State("1");

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(request.getRequestURL()).thenReturn(new StringBuffer("test"));

        SecurityContext securityContext = mock(SecurityContext.class);
        when(session.getAttribute(OIDC_STATE.getFieldName())).thenReturn(state.getValue());
        setInternalState(openIdConnectFilter, "securityContext", securityContext);

        PowerMockito.mockStatic(AuthorizationResponse.class);

        ErrorObject errorObject = new ErrorObject("ERR1", "Error processing callback", 400);
        AuthorizationResponse authorizationResponse = new AuthorizationErrorResponse(
                new URI("test"), errorObject, state, ResponseMode.JWT);

        when(AuthorizationResponse.parse(any(URI.class))).thenReturn(authorizationResponse);
        openIdConnectFilter.processAuthCodeCallback(request);
    }

    /**
     * Test for {@link OpenIdConnectFilter#doFilter(ServletRequest, ServletResponse, FilterChain)}
     * Simple scenario where there is no necessity to intercept request
     */
    @Test
    public void testFilterProcessNoInterception() throws IOException, ServletException {
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        HttpServletResponse servletResponse = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(servletRequest.getRequestURI()).thenReturn("/web");

        ArgumentCaptor<HttpServletRequest> requestArgumentCaptor =
                ArgumentCaptor.forClass(HttpServletRequest.class);

        ArgumentCaptor<HttpServletResponse> responseArgumentCaptor =
                ArgumentCaptor.forClass(HttpServletResponse.class);

        openIdConnectFilter.doFilter(servletRequest, servletResponse, filterChain);
        verify(filterChain).doFilter(requestArgumentCaptor.capture(), responseArgumentCaptor.capture());

        assertEquals(servletRequest, requestArgumentCaptor.getValue());
        assertEquals(servletResponse, responseArgumentCaptor.getValue());
    }

    /**
     * Test for {@link OpenIdConnectFilter#doFilter(ServletRequest, ServletResponse, FilterChain)}
     * First part: Starting scenario where the authorization code is requested
     */
    @Test
    public void testFilterProcessRequestAuthCode() throws IOException, ServletException {
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getSession()).thenReturn(session);
        when(request.getRequestURI()).thenReturn("/login");

        String authEndPoint = "http://opendid.net/auth";
        String callbackUri = "http://radien.net/web/auth";
        securityContext = mock(SecurityContext.class);

        setInternalState(openIdConnectFilter, "userAuthorizationUri", authEndPoint);
        setInternalState(openIdConnectFilter, "redirectUri", callbackUri);
        setInternalState(openIdConnectFilter, "securityContext", securityContext);

        ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);

        this.openIdConnectFilter.doFilter(request, response, filterChain);
        verify(response).sendRedirect(uriCaptor.capture());

        String redirectionURL = uriCaptor.getValue();

        assertTrue(redirectionURL.startsWith(authEndPoint));
        assertTrue(redirectionURL.contains("response_type=code"));
        assertTrue(redirectionURL.contains("state="));

        String redirectUriEncoded = "redirect_uri=" + URLEncoder.encode(callbackUri, "UTF-8");
        assertTrue(redirectionURL.contains(redirectUriEncoded));
    }

    /**
     * Test for {@link OpenIdConnectFilter#doFilter(ServletRequest, ServletResponse, FilterChain)}
     * A variation of the First part: login process is requested but access code already exist
     * (Authentication already been done)
     */
    @Test
    public void testFilterAuthAlreadyDone() throws IOException, ServletException {
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getSession()).thenReturn(session);
        when(request.getRequestURI()).thenReturn("/login");
        when(request.getScheme()).thenReturn("https");
        when(request.getServerName()).thenReturn("int.radien.io");

        String authEndPoint = "http://opendid.net/auth";
        setInternalState(openIdConnectFilter, "userAuthorizationUri", authEndPoint);

        when(session.getAttribute(ACCESS_TOKEN.getFieldName())).thenReturn("11111");
        setInternalState(openIdConnectFilter, "securityContext", securityContext);
        setInternalState(openIdConnectFilter, "appBaseContext", "web");

        ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);

        this.openIdConnectFilter.doFilter(request, response, filterChain);

        verify(response).sendRedirect(uriCaptor.capture());
        String redirectionURL = uriCaptor.getValue();

        String expectedURL = "https://int.radien.io/web";

        assertEquals(expectedURL, redirectionURL);
    }

    /**
     * Test for {@link OpenIdConnectFilter#doFilter(ServletRequest, ServletResponse, FilterChain)}
     * Second Part: Authorization code request was already processed in the (OpenId) server.
     * Open Id server redirects the flow to App via callback uri. Now is necessary to process
     * the callback:
     * 1 - Obtaining Authorization Code
     * 2 - Comparing the received Grant Code
     * 3 - Prepare
     */
    @Test
    public void testFilterProcessingAuthCodeCallback() throws IOException, ServletException, URISyntaxException, ParseException, java.text.ParseException {
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        FilterChain filterChain = mock(FilterChain.class);
        HttpSession session = mock(HttpSession.class);

        securityContext = new OpenIdSecurityContext();
        TokenValidator tokenValidator = mock(TokenValidator.class);
        JWSObject jwsObject = mock(JWSObject.class);
        Payload payload = new Payload(new HashMap<>());

        String authEndPoint = "http://opendid.net/auth";
        String tokenEndPoint = "http://opendid.net/token";
        String redirectUri = "https://int.radien.io/web/login";
        String authCallbackUri = redirectUri + "/auth";

        setInternalState(openIdConnectFilter, "tokenValidator", tokenValidator);
        setInternalState(openIdConnectFilter, "userAuthorizationUri", authEndPoint);
        setInternalState(openIdConnectFilter, "accessTokenUri", tokenEndPoint);
        setInternalState(openIdConnectFilter, "redirectUri", redirectUri);
        setInternalState(openIdConnectFilter, "clientId", "1234");
        setInternalState(openIdConnectFilter, "clientSecret", "1d2e3f4g5h");
        setInternalState(openIdConnectFilter, "securityContext", securityContext);
        setInternalState(openIdConnectFilter, "appBaseContext", "web");

        ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);

        // Parameters to retrieve authorization code and callback processing
        State state = new State("1");
        when(session.getAttribute(OIDC_STATE.getFieldName())).thenReturn(state.getValue());

        PowerMockito.mockStatic(TokenResponse.class);
        PowerMockito.mockStatic(AuthorizationResponse.class);
        PowerMockito.mockStatic(JWSObject.class);

        AuthorizationCode authorizationCode = new AuthorizationCode("1111111");
        AccessToken accessToken = new BearerAccessToken("1111111");
        AuthorizationResponse authorizationResponse = new AuthorizationSuccessResponse(
                new URI(authCallbackUri), authorizationCode, accessToken, state, ResponseMode.JWT);

        RefreshToken refreshToken = new RefreshToken("22222222");
        Tokens tokens = new Tokens(accessToken, refreshToken);
        TokenResponse tokenResponse = new AccessTokenResponse(tokens);

        when(request.getSession()).thenReturn(session);
        when(request.getRequestURI()).thenReturn("/web/login/auth");
        when(request.getScheme()).thenReturn("https");
        when(request.getServerName()).thenReturn("int.radien.io");
        when(request.getRequestURL()).thenReturn(new StringBuffer("test"));
        when(AuthorizationResponse.parse(any(URI.class))).thenReturn(authorizationResponse);
        when(TokenResponse.parse(any(HTTPResponse.class))).thenReturn(tokenResponse);

        when(JWSObject.parse(anyString())).thenReturn(jwsObject);
        when(jwsObject.getPayload()).thenReturn(payload);

        this.openIdConnectFilter.doFilter(request, response, filterChain);

        verify(response).sendRedirect(uriCaptor.capture());
        String redirectionURL = uriCaptor.getValue();

        String expectedURL = "https://int.radien.io/web";

        assertEquals(expectedURL, redirectionURL);
    }

    /**
     * Test for {@link OpenIdConnectFilter#doFilter(ServletRequest, ServletResponse, FilterChain)}
     * Second Part: Authorization code request was already processed in the (OpenId) server.
     * Open Id server redirects the flow to App via callback uri. Now is necessary to process
     * the callback:
     * 1 - Obtaining Authorization Code (from auth code endpoint
     * 2 - Comparing the received Grant Code
     * 3 - Prepare to Request Access token (to token endpoint)
     * 4 - Processing response
     * 5 - Validating tokens
     * 6 - Extract and store user details
     */
    @Test
    public void testFilterProcessingAuthCodeFailure() throws IOException, ServletException, URISyntaxException, ParseException {
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        FilterChain filterChain = mock(FilterChain.class);

        securityContext = new OpenIdSecurityContext();

        String authEndPoint = "http://opendid.net/auth";
        String redirectUri = "https://int.radien.io/web/login";
        String authCallbackUri = redirectUri + "/auth";

        setInternalState(openIdConnectFilter, "userAuthorizationUri", authEndPoint);
        setInternalState(openIdConnectFilter, "redirectUri", redirectUri);
        setInternalState(openIdConnectFilter, "clientId", "1234");
        setInternalState(openIdConnectFilter, "clientSecret", "1d2e3f4g5h");
        setInternalState(openIdConnectFilter, "securityContext", securityContext);
        setInternalState(openIdConnectFilter, "appBaseContext", "web");

        // Parameters to retrieve authorization code and callback processing
        State state = new State("1");
        when(session.getAttribute(OIDC_STATE.getFieldName())).thenReturn(state.getValue());

        PowerMockito.mockStatic(AuthorizationResponse.class);

        ErrorObject errorObject = new ErrorObject("invalid_grant", "Invalid Grant",
                400, new URI(authCallbackUri));

        AuthorizationResponse authorizationResponse = new AuthorizationErrorResponse(
                new URI(authCallbackUri), errorObject, state, ResponseMode.JWT);

        when(request.getRequestURL()).thenReturn(new StringBuffer("test"));
        when(request.getRequestURI()).thenReturn("/login/auth");
        when(AuthorizationResponse.parse(any(URI.class))).thenReturn(authorizationResponse);

        this.openIdConnectFilter.doFilter(request, response, filterChain);

        ArgumentCaptor<Integer> errorStatusCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<String> errorMsgCaptor = ArgumentCaptor.forClass(String.class);
        verify(response).sendError(errorStatusCaptor.capture(), errorMsgCaptor.capture());

        assertEquals(400, errorStatusCaptor.getValue().intValue());
    }

}