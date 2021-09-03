/*
 * Copyright (c) 2021-present radien GmbH. All rights reserved.
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

import javax.json.JsonObject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

/**
 * Callback servlet constructor class
 *
 * @author Nuno Santana
 */
@WebServlet(urlPatterns = "/callback")
public class CallbackServlet extends AbstractServlet {

    private static final Logger log = LoggerFactory.getLogger(CallbackServlet.class);
    private static final String ERROR = "error";
    /**
     * Called by the server (via the service method) to allow a servlet to handle a GET request.
     * Overriding this method to support a GET request also automatically supports an HTTP HEAD request. A HEAD request
     * is a GET request that returns no body in the response, only the request header fields.
     * When overriding this method, read the request data, write the response headers, get the response's writer or
     * output stream object, and finally, write the response data. It's best to include content type and encoding. When
     * using a PrintWriter object to return the response, set the content type before accessing the
     * PrintWriter object.
     * The servlet container must write the headers before committing the response, because in HTTP the headers must be
     * sent before the response body.
     * Where possible, set the Content-Length header (with the {@link javax.servlet.ServletResponse#setContentLength}
     * method), to allow the servlet container to use a persistent connection to return its response to the client,
     * improving performance. The content length is automatically set if the entire response fits inside the response
     * buffer.
     * When using HTTP 1.1 chunked encoding (which means that the response has a Transfer-Encoding header), do not set
     * the Content-Length header.
     * The GET method should be safe, that is, without any side effects for which users are held responsible. For
     * example, most form queries have no side effects. If a client request is intended to change stored data, the
     * request should use some other HTTP method.
     * The GET method should also be idempotent, meaning that it can be safely repeated. Sometimes making a method safe
     * also makes it idempotent. For example, repeating queries is both safe and idempotent, but buying a product online
     * or modifying data is neither safe nor idempotent.
     * If the request is incorrectly formatted, doGet returns an HTTP "Bad Request" message.
     * @param request  an {@link HttpServletRequest} object that contains the request the client has made of the servlet
     * @param response an {@link HttpServletResponse} object that contains the response the servlet sends to the client
     * @throws IOException      if an input or output error is detected when the servlet handles the GET request
     * @throws ServletException if the request for the GET could not be handled
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Config config = ConfigProvider.getConfig();
        String clientId = config.getValue("auth.clientId", String.class);
        String clientSecret = config.getValue("auth.clientSecret", String.class);

        //Error:
        String error = request.getParameter(ERROR);
        if (error != null) {
            request.setAttribute(ERROR, error);
            try {
                dispatch("/", request, response);
            } catch (ServletException |IOException e){
                log.error(e.getMessage());
            }
            return;
        }
        String localState = (String) request.getSession().getAttribute("CLIENT_LOCAL_STATE");
        if (localState == null || !localState.equals(request.getParameter("state"))) {
            request.setAttribute(ERROR, "The state attribute doesn't match !!");
            try {
                dispatch("/", request, response);
            } catch (ServletException |IOException e){
                log.error(e.getMessage());
            }
            return;
        }

        String code = request.getParameter("code");

        Client client = ClientBuilder.newClient();
        //"provider.tokenUri"
        WebTarget target = client.target(config.getValue("auth.accessTokenUri", String.class));

        Form form = new Form();
        form.param("grant_type", "authorization_code");
        form.param("code", code);
        form.param("redirect_uri", config.getValue("client.redirectUri", String.class));

        try {
            JsonObject tokenResponse = target.request(MediaType.APPLICATION_JSON_TYPE)
                    .header(HttpHeaders.AUTHORIZATION, getAuthorizationHeaderValue(clientId, clientSecret))
                    .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), JsonObject.class);
            request.getSession().setAttribute("tokenResponse", tokenResponse);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            request.setAttribute(ERROR, ex.getMessage());
        }
        try {
            dispatch("/", request, response);
        } catch (ServletException |IOException e) {
            log.error(e.getMessage());
        }
    }
}