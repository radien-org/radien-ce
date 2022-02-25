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
import io.radien.security.openid.context.SecurityContext;
import io.radien.security.openid.utils.URLUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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

import static io.radien.security.openid.utils.OpenIdConstants.LOGIN_URI;
import static io.radien.security.openid.utils.OpenIdConstants.SAVED_URL_STATE;

/**
 * The aims for this filter:
 * 1 - Intercepting requests
 * 2 - Check if the request refers a private URI
 * 3 - If is about a private URI and there is no Authentication information (user did not authenticate himself)
 * 4 - Redirects to the URI that triggers the authentication process.
 */
public class OpenIdURLChecker implements Filter {

    private static final Logger log = LoggerFactory.getLogger(OpenIdURLChecker.class);

    @Inject
    private SecurityContext securityContext;

    @Inject
    private OpenIdConfig openIdConfig;

    /**
     * Where the interception takes place. The request will be checked,
     * if refers a private URI, and user is not authenticated yet, redirects
     * to an URI that triggers/starts the authentication process
     * @param servletRequest servlet request parameter (See {@link ServletRequest}
     * @param servletResponse servlet response parameter (See {@link ServletResponse}
     * @param chain filter chain parameter (See {@link FilterChain}
     * @throws IOException in case of any issue doing I/O processing
     * @throws ServletException in case of any issue processing servlet stuff
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        if (isPrivateURI(request) && securityContext.getUserDetails() == null) {
            redirectToAuthProcess(request, response);
            return;
        }
        chain.doFilter(request, response);
    }

    /**
     * Saves the current URL to be retrieve afterward (in case of successful authentication)
     * Assemblies URL that will trigger Authentication process and do the redirection.
     * @param request parameter to supply information for url assembling
     * @throws IOException in case of i/o issue during redirection process
     */
    protected void redirectToAuthProcess(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("Saving current url {}", request.getServletPath());
        request.getSession().setAttribute(SAVED_URL_STATE, request.getRequestURL().toString());
        String authUrl = URLUtils.getAppContextURL(request) + LOGIN_URI;
        log.info("Redirecting to auth url {}", authUrl);
        response.sendRedirect(authUrl);
    }

    /**
     * Check if the current requests refers a private URI, in other words, if is a URI that can only
     * be accessed by an authenticated user.
     * @param request servlet request parameter from which the URI will be extracted
     * @return true if is a private URI, otherwise false
     */
    protected boolean isPrivateURI(HttpServletRequest request) {
        Collection<String> contexts = getPrivateContextList();
        for (String ctx:contexts) {
            if (request.getServletPath().startsWith(ctx)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Splits the privateContexts parameter (String) into a list of possible
     * private contexts to be checked against the URI
     * @return list containing String
     */
    protected List<String> getPrivateContextList() {
        List<String> privateContextList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (String ctx:openIdConfig.getAuthPrivateContexts().split(",")) {
            if (ctx.trim().length() > 0) {
                if (!ctx.startsWith("/")) {
                    sb.append("/");
                }
                sb.append(ctx);
                privateContextList.add(sb.toString());
            }
            sb.setLength(0);
        }
        return privateContextList;
    }
}
