package io.radien.security.openid.filter;

import io.radien.security.openid.context.OpenIdSecurityContext;
import io.radien.security.openid.context.SecurityContext;
import io.radien.security.openid.model.UserDetails;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;


import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.reflect.Whitebox.setInternalState;

/**
 * Test class for {@link OpenIdURLCheckerTest}
 */
public class OpenIdURLCheckerTest {

    OpenIdURLChecker openIdURLChecker;
    SecurityContext securityContext;

    final String privateContexts = "/module,/user,/test";
    final String authTriggerURI = "https://localhost:8443/web/login";

    @Before
    public void setUp() {
        openIdURLChecker = new OpenIdURLChecker();
        securityContext = new OpenIdSecurityContext();
        setInternalState(openIdURLChecker, "securityContext", securityContext);
        setInternalState(openIdURLChecker, "authenticationTriggerURI", authTriggerURI);
        setInternalState(openIdURLChecker, "privateContexts", privateContexts);
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
        FilterChain filterChain = mock(FilterChain.class);
        when(request.getServletPath()).thenReturn("/module/permissions");

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
        this.securityContext.setUserDetails(userDetails);

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
