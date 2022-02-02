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
package io.radien.ms.openid.security;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Class that aggregates UnitTest cases for AuthenticationFilter
 */
public class AuthenticationFilterTest {

    private final String accessToken = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJJTEtqRFdzbzhUU3NnT1ZuVEZBUlJs" +
            "WDIxTXBfN29zYUpNTnRwTWVsNV9VIn0.eyJleHAiOjE2MTQ2ODAwOTMsImlhdCI6MTYxNDY3OTc5MywianRpIjoiODEyYzU2NzAtNzM4Ni0" +
            "0MDY1LTkxZmQtZGRiNzQ2OTZmYTU3IiwiaXNzIjoiaHR0cHM6Ly9pZHAtaW50LnJhZGllbi5pby9hdXRoL3JlYWxtcy9yYWRpZW4iLCJhdWQ" +
            "iOiJhY2NvdW50Iiwic3ViIjoiZWM0NDE2YjMtOWY3YS00YWUxLTg5ZjUtZGZkMDlhNDRlZThkIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoicm" +
            "FkaWVuIiwic2Vzc2lvbl9zdGF0ZSI6ImQ3Y2EzN2Y1LTM3YTgtNDQ1My05MzdkLTkzMmE1MDE2MzI3MyIsImFjciI6IjEiLCJhbGxvd2VkL" +
            "W9yaWdpbnMiOlsiaHR0cHM6Ly9sb2NhbGhvc3Q6ODQ0MyJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJ1" +
            "bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5h" +
            "Z2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjp0cn" +
            "VlLCJuYW1lIjoiTnVubyAgU2FudGFuYSIsInByZWZlcnJlZF91c2VybmFtZSI6Im4uc2FudGFuYS11c2VybmFtZSIsImdpdmVuX25hbWUi" +
            "OiJOdW5vICIsImZhbWlseV9uYW1lIjoiU2FudGFuYSIsImVtYWlsIjoibi5zYW50YW5hQHJhZGllbi5pbyJ9.x37IRsQWE-NjIwcK-wzZc" +
            "9rj_nGDBo7FWt7Kj_agzhpNx0lbMy6LcrklZJ6oIBb-rW6YpfIbqBP17hrK27ZNyAB2Tb3-jADSg8WIkTpbjQ16h3Zn4_Hn2GQIHrEOryA" +
            "z7DZhkGj6MFZEVuebwbHmPh5MqX8pwNl8bpwS63zxa6YK9q1ny_hLrzgrsJWERgo6TsOKFle3asP8LzlGt3YI4_ePd3MSXDv-Z627bD-9P" +
            "rAdpJqDujcDca58DXukoVE-zigD1-AAI2E85Ala-7kCDdR_KlBKk6frATfqpgZ5QrzHir0h_qRs9UGwDPOF1iv9fCyvVyBy-_VbzMWxqZCksQ";

    @InjectMocks
    AuthenticationFilter target;

    /**
     * Prepares required mock objects
     */
    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test for method {@link AuthenticationFilter#doFilter(ServletRequest, ServletResponse, FilterChain)}
     * @throws ServletException defines a general exception a servlet can throw when it encounters difficulty.
     * @throws IOException if an exception occurs that interferes with the filter's normal operation
     */
    @Test
    public void testDoFilter() throws ServletException, IOException {
        PrintWriter printWriter = Mockito.mock(PrintWriter.class);
        prePareMockObjectsForDoFilter(printWriter, "Bearer "+ accessToken, "a");
        Mockito.verify(printWriter).write("Failed authentication..");
    }

    /**
     * Test for method {@link AuthenticationFilter#doFilter(ServletRequest, ServletResponse, FilterChain)}
     * @throws ServletException defines a general exception a servlet can throw when it encounters difficulty.
     * @throws IOException if an exception occurs that interferes with the filter's normal operation
     */
    @Test(expected = Test.None.class)
    public void testDoFilterNullAuthorization() throws ServletException, IOException {
        PrintWriter printWriter = Mockito.mock(PrintWriter.class);
        prePareMockObjectsForDoFilter(printWriter,null, "v1/user/refresh");
    }

    /**
     * Test for method {@link AuthenticationFilter#doFilter(ServletRequest, ServletResponse, FilterChain)}
     * @throws ServletException defines a general exception a servlet can throw when it encounters difficulty.
     * @throws IOException if an exception occurs that interferes with the filter's normal operation
     */
    @Test(expected = Test.None.class)
    public void testDoFilterAccessTokenIsNotBearer() throws ServletException, IOException {
        PrintWriter printWriter = Mockito.mock(PrintWriter.class);
        prePareMockObjectsForDoFilter(printWriter, "asdrf17651", "service/v1/health");
    }

    /**
     * Test for method {@link AuthenticationFilter#doFilter(ServletRequest, ServletResponse, FilterChain)}
     * @throws ServletException defines a general exception a servlet can throw when it encounters difficulty.
     * @throws IOException if an exception occurs that interferes with the filter's normal operation
     */
    @Test(expected = Test.None.class)
    public void testDoFilterServiceInfoURI() throws ServletException, IOException {
        PrintWriter printWriter = Mockito.mock(PrintWriter.class);
        prePareMockObjectsForDoFilter(printWriter, "asdrf", "service/v1/info");
    }

    /**
     * Test for method {@link AuthenticationFilter#doFilter(ServletRequest, ServletResponse, FilterChain)}
     * @throws ServletException defines a general exception a servlet can throw when it encounters difficulty.
     * @throws IOException if an exception occurs that interferes with the filter's normal operation
     */
    @Test(expected = Test.None.class)
    public void testDoFilterJWSVerifierFalse() throws ServletException, IOException {
        PrintWriter printWriter = Mockito.mock(PrintWriter.class);
        prePareMockObjectsForDoFilter(printWriter, "Bearer " + accessToken, "v1/user/refresh");

    }


    @Test
    public void testInvalidIssuer(){
        assertTrue(target.invalidIssuer("a","b","PROD"));
        assertFalse(target.invalidIssuer("a","a","PROD"));
        assertTrue(target.invalidIssuer("localhost","host.docker.internal","PROD"));
        assertFalse(target.invalidIssuer("localhost","host.docker.internal","LOCAL"));
    }
    /**
     * Method that prepares mock objects required to perform testing of invoke method
     * @throws ServletException defines a general exception a servlet can throw when it encounters difficulty.
     * @throws IOException if an exception occurs that interferes with the filter's normal operation
     */
    private void prePareMockObjectsForDoFilter(PrintWriter printWriter, String authToken, String reqURI) throws ServletException, IOException {
        target.init(null);
        ServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletRequest req = (HttpServletRequest) request;
        ServletResponse response = Mockito.mock(HttpServletResponse.class);
        HttpServletResponse resp = (HttpServletResponse) response;
        FilterChain chain = Mockito.mock(FilterChain.class);
        when(req.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(authToken);
        when(req.getRequestURI()).thenReturn(reqURI);
        when(resp.getWriter()).thenReturn(printWriter);
        target.doFilter(request,response,chain);
        target.destroy();
    }

}