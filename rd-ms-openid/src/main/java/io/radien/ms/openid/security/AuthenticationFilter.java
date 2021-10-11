/*
 * Copyright (c) 2021-present radien GmbH & its legal owners. All rights reserved.
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

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import io.radien.ms.openid.entities.Principal;
import io.radien.ms.openid.service.PrincipalFactory;
import org.eclipse.microprofile.config.ConfigProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;

import java.security.interfaces.RSAPublicKey;

import java.text.ParseException;
import java.time.Instant;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 *A Filter that performs authentication of a particular request.
 * @author Nuno Santana
 */
@WebFilter("/*")
public class AuthenticationFilter implements Filter {

    //TODO: Move this to security library so it can be reused by multiple microservices
    @Context
    private ResourceInfo resourceInfo;
    private static final Logger log = LoggerFactory.getLogger(AuthenticationFilter.class);


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.debug("AuthenticationFilter init");
    }

    /**
     * The doFilter method of the Filter is called by the container each time a request/response pair is
     * passed through the chain due to a client request for a resource at the end of the chain. The FilterChain passed
     * in to this method allows the Filter to pass on the request and response to the next entity in the chain.
     *
     * A typical implementation of this method would follow the following pattern:
     * Examine the request
     * Optionally wrap the request object with a custom implementation to filter content or headers for input
     * filtering
     * Optionally wrap the response object with a custom implementation to filter content or headers for output
     * filtering
     * Either invoke the next entity in the chain using the FilterChain object
     * or not pass on the request/response pair to the next entity in the filter chain to block the
     * request processing
     * Directly set headers on the response after invocation of the next entity in the filter chain.
     *
     * @param request  the ServletRequest object contains the client's request
     * @param response the ServletResponse object contains the filter's response
     * @param chain    the FilterChain for invoking the next filter or the resource
     * @throws IOException      if an I/O related error has occurred during the processing
     * @throws ServletException if an exception occurs that interferes with the filter's normal operation
     **/
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        boolean failed = true;
        //TODO: Maybe it needs to redirect to Authorization Code Servlet when its not present/valid
        //          then Answer after authentication comes from callback

        if (req.getHeader(HttpHeaders.AUTHORIZATION) != null) {
            String accessToken = req.getHeader(HttpHeaders.AUTHORIZATION);
            if (accessToken.startsWith("Bearer ")) {
                accessToken = accessToken.substring(7);
                failed = !validateToken(req.getSession(), accessToken);
                if (!failed) {
                    chain.doFilter(request, response);
                }
            }

        }

        //public
        if(req.getRequestURI().endsWith("v1/user/refresh") || (req.getRequestURI().endsWith("service/v1/health")) || (req.getRequestURI().endsWith("service/v1/info"))){
            chain.doFilter(request, response);

        } else if (failed) {
            HttpServletResponse resp = (HttpServletResponse) response;
            ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("Failed authentication..");
            resp.setContentType("application/json; charset=UTF-8");
        }

    }

    /**
     * Everytime the container receives a request/response the following method will get the authorization issuer and
     * url to validate the request/response authentication
     * @param session where the request/response is coming or going into
     * @param accessToken user access token to be validated or added
     * @return true in case the token has been validated with success
     */
    private boolean validateToken(HttpSession session, String accessToken) {
        try {
            JWSObject jwsObject = JWSObject.parse(accessToken);

            String issuer = ConfigProvider.getConfig().getValue("auth.issuer", String.class);
            String jwkUrl = ConfigProvider.getConfig().getValue("auth.jwkUrl", String.class);

            //check acr on payload when with totp
            //acr stands for Authentication Context Class

            JWSHeader header = jwsObject.getHeader();
            String kid = header.getKeyID();

            final JwkProvider provider = new UrlJwkProvider(new URL(jwkUrl));
            final Jwk jwk = provider.get(kid);


            JWSVerifier verifier = new RSASSAVerifier((RSAPublicKey) jwk.getPublicKey());
            if (!jwsObject.verify(verifier)) {
                return false;
            }

            Payload payload = jwsObject.getPayload();
            try (JsonReader reader = Json.createReader(new StringReader(payload.toString()))) {
                JsonObject jsonObject = reader.readObject();

                if (!issuer.equals(jsonObject.getString("iss"))) {
                    return false;
                }

                if (!jsonObject.getString("typ").equals("Bearer")) {
                    return false;
                }

                LocalDateTime exp = LocalDateTime.ofInstant(Instant.ofEpochSecond(jsonObject.getJsonNumber("exp").longValue()), ZoneId.systemDefault());
                if (exp.isBefore(LocalDateTime.now())) {
                    //TODO: refresh token
                    return false;

                }
                Principal principal = PrincipalFactory.convert(jsonObject);
                session.setAttribute("USER", principal);
            }



            return true;
        } catch (ParseException | MalformedURLException | JwkException | JOSEException e) {
            log.error("Unable to parse Access Token", e);
        }
        return false;
    }

    /**
     * Called by the web container to indicate to a filter that it is being taken out of service.
     * This method is only called once all threads within the filter's doFilter method have exited or after a timeout
     * period has passed. After the web container calls this method, it will not call the doFilter method again on this
     * instance of the filter.
     * This method gives the filter an opportunity to clean up any resources that are being held (for example, memory,
     * file handles, threads) and make sure that any persistent state is synchronized with the filter's current state in
     * memory.
     */
    @Override
    public void destroy() {
        log.debug("AuthenticationFilter destroy");
    }
}
