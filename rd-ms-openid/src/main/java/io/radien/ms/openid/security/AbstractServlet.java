/*
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
package io.radien.ms.openid.security;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;

/**
 * AbstractServlet allows HTTP server modules to be reused across multiple servers and
 * allows encapsulation of functionality.
 * By default a servlet will respond to GET, HEAD (through an alias to GET) and OPTIONS requests.
 * @author Nuno Santana
 */
public abstract class AbstractServlet extends HttpServlet {

    /**
     * Encapsulate and send the request into the dispacher
     * @param location for the request to be sent
     * @param request to be sent
     * @param response to be sent
     * @throws ServletException defines a general exception a servlet can throw when it encounters difficulty.
     * @throws IOException signals that an I/O exception of some sort has occurred. This class is the general
     * class of exceptions produced by failed or interrupted I/O operations.
     */
    protected void dispatch(String location, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher requestDispatcher = request.getRequestDispatcher(location);
        requestDispatcher.forward(request, response);
    }

    /**
     * Construct the request and fill the header message with the authorization in the header of the request
     * @param clientId that is sending the request
     * @param clientSecret client secret token
     * @return authorization token
     */
    protected String getAuthorizationHeaderValue(String clientId, String clientSecret) {
        String token = clientId + ":" + clientSecret;
        String encodedString = Base64.getEncoder().encodeToString(token.getBytes());
        return "Basic " + encodedString;
    }
}
