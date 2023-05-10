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

import io.radien.security.openid.config.OpenIdConfig;
import io.radien.security.openid.context.OpenIdSecurityContext;
import io.radien.security.openid.context.SecurityContext;
import io.radien.security.openid.model.UserDetails;
import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;


import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link OpenIdURLCheckerTest}
 */
public class OpenIdURLCheckerTest {

    @InjectMocks
    private OpenIdURLChecker openIdURLChecker;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private OpenIdConfig openIdConfig;
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    final String privateContexts = "/module,/user,/test";
    final String authTriggerURI = "https://localhost:8443/web/login";

    @Before
    public void setUp() {
        when(openIdConfig.getAuthPrivateContexts()).thenReturn(privateContexts);
    }

    /**
     * Test for method {@link OpenIdURLChecker#doFilter(ServletRequest, ServletResponse, FilterChain)}
     * Scenario 1: Accessing public URI
     */
    @Test
    public void testDoFilterAccessingPublicURI() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getServletPath()).thenReturn("/web/public/index");

        ArgumentCaptor<HttpServletRequest> requestArgumentCaptor =
                ArgumentCaptor.forClass(HttpServletRequest.class);

        ArgumentCaptor<HttpServletResponse> responseArgumentCaptor =
                ArgumentCaptor.forClass(HttpServletResponse.class);

        openIdURLChecker.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(requestArgumentCaptor.capture(), responseArgumentCaptor.capture());

        assertEquals(request, requestArgumentCaptor.getValue());
        assertEquals(response, responseArgumentCaptor.getValue());
    }

    /**
     * Test for method {@link OpenIdURLChecker#doFilter(ServletRequest, ServletResponse, FilterChain)}
     * Scenario 2: Accessing private URI
     */
    @Test
    public void testDoFilterAccessingPrivateURI() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        FilterChain filterChain = mock(FilterChain.class);
        when(request.getSession()).thenReturn(session);
        when(request.getScheme()).thenReturn("https");
        when(request.getServerName()).thenReturn("localhost");
        when(request.getServerPort()).thenReturn(8443);
        when(request.getContextPath()).thenReturn("/web");
        when(request.getServletPath()).thenReturn("/module/permissions");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8443/web/module/permissions"));

        ArgumentCaptor<String> urlPath = ArgumentCaptor.forClass(String.class);

        openIdURLChecker.doFilter(request, response, filterChain);

        verify(response).sendRedirect(urlPath.capture());

        assertEquals(this.authTriggerURI, urlPath.getValue());
    }

    /**
     * Test for method {@link OpenIdURLChecker#doFilter(ServletRequest, ServletResponse, FilterChain)}
     * Scenario 2: Accessing private URI
     */
    @Test
    public void testAccessingPrivateURIAfterAuthentication() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        UserDetails userDetails = mock(UserDetails.class);
        when(securityContext.getUserDetails()).thenReturn(userDetails);

        when(request.getServletPath()).thenReturn("/module/tenants");

        ArgumentCaptor<HttpServletRequest> requestArgumentCaptor =
                ArgumentCaptor.forClass(HttpServletRequest.class);

        ArgumentCaptor<HttpServletResponse> responseArgumentCaptor =
                ArgumentCaptor.forClass(HttpServletResponse.class);

        openIdURLChecker.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(requestArgumentCaptor.capture(), responseArgumentCaptor.capture());

        assertEquals(request, requestArgumentCaptor.getValue());
        assertEquals(response, responseArgumentCaptor.getValue());
    }

}
