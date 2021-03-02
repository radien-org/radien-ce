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

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@WebServlet(urlPatterns = "/authorize")
public class AuthorizationCodeServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(AuthorizationCodeServlet.class);

    //UmmsOAF
    //authorize already exists in keycloak with
    // https://idp-int.radien.io/auth/realms/radien/protocol/openid-connect/auth?response_type=code&client_id=radien&redirect_uri=https%3A%2F%2Flocalhost%3A8443%2Fweb%2Fmodule%2Fuser%2Fprofile
    //redirect_uri needs to be added in keycloak management console
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        request.getSession().removeAttribute("tokenResponse");
        String state = UUID.randomUUID().toString();
        request.getSession().setAttribute("CLIENT_LOCAL_STATE", state);
        Config config = ConfigProvider.getConfig();
        String authorizationUri = config.getValue("auth.userAuthorizationUri", String.class);
        String clientId = config.getValue("auth.clientId", String.class);
        String redirectUri = config.getValue("client.redirectUri", String.class);;
        String scope = config.getValue("client.scope", String.class);

        String authorizationLocation = authorizationUri + "?response_type=code"
                + "&client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&scope=" + scope
                + "&state=" + state;

        try {
            response.sendRedirect(authorizationLocation);
        } catch (IOException e){
            log.error("Error sending redirect",e);
            //this was added to block Exception details to reach user
            throw new IOException();
        }
    }
}
