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

public abstract class AbstractServlet extends HttpServlet {

    protected void dispatch(String location, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher requestDispatcher = request.getRequestDispatcher(location);
        requestDispatcher.forward(request, response);
    }

    protected String getAuthorizationHeaderValue(String clientId, String clientSecret) {
        String token = clientId + ":" + clientSecret;
        String encodedString = Base64.getEncoder().encodeToString(token.getBytes());
        return "Basic " + encodedString;
    }
}
