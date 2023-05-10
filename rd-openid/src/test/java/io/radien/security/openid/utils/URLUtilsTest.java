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
package io.radien.security.openid.utils;

import javax.servlet.http.HttpServletRequest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test class for Utility class {@link URLUtils}
 */
public class URLUtilsTest {

    /**
     * Test for method {@link URLUtils#getAppContextURL(HttpServletRequest)}
     */
    @Test
    public void testGetAppContextURL() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getScheme()).thenReturn("https");
        when(request.getServerName()).thenReturn("int.radien.io");
        when(request.getServerPort()).thenReturn(0);
        when(request.getContextPath()).thenReturn("/web");

        String expectedURL = "https://int.radien.io/web";
        String obtainedURL = URLUtils.getAppContextURL(request);
        assertEquals(expectedURL, obtainedURL);

        HttpServletRequest request2 = mock(HttpServletRequest.class);
        when(request2.getScheme()).thenReturn("https");
        when(request2.getServerName()).thenReturn("server1");
        when(request2.getServerPort()).thenReturn(8443);
        when(request2.getContextPath()).thenReturn("/web");

        expectedURL = "https://server1:8443/web";
        obtainedURL = URLUtils.getAppContextURL(request2);
        assertEquals(expectedURL, obtainedURL);
    }


    /**
     * Test for method {@link URLUtils#getCompleteURL(HttpServletRequest)}
     */
    @Test
    public void testGetCompleteURL() {
        StringBuffer requestURL = new StringBuffer("https://localhost:8443/web/login/auth");
        String queryString = "state=1111&session_state=1233455&code=7e8ff1b3";

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURL()).thenReturn(requestURL);
        when(request.getQueryString()).thenReturn(queryString);

        String expected = requestURL + "?" + queryString;
        String resultURL = URLUtils.getCompleteURL(request);
        assertEquals(expected, resultURL);
    }
}
