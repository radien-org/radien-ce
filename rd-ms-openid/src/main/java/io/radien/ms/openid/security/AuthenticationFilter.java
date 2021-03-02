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
import io.radien.ms.usermanagement.client.services.UserFactory;
import io.radien.ms.usermanagement.client.entities.User;
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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import java.security.interfaces.RSAPublicKey;

import java.text.ParseException;
import java.time.Instant;

import java.time.LocalDateTime;
import java.time.ZoneId;

import java.util.stream.Collectors;

@WebFilter("/*")
public class AuthenticationFilter implements Filter {
    //TODO: Move this to security library so it can be reused by multiple microservices
    @Context
    private ResourceInfo resourceInfo;
    private static final Logger log = LoggerFactory.getLogger(AuthenticationFilter.class);


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

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

        if (failed) {
            HttpServletResponse resp = (HttpServletResponse) response;
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Failed authentication..");
        }

    }

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
            JsonReader reader = Json.createReader(new StringReader(payload.toString()));
            JsonObject jsonObject = reader.readObject();
            String[] scopes = jsonObject.getString("scope").split(" ");
            //TODO: validate scopes

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
            User principal = getUser(jsonObject);
            session.setAttribute("USER", principal);

            return true;
        } catch (ParseException | MalformedURLException | JwkException | JOSEException e) {
            log.error("Unable to parse Access Token", e);
        }
        return false;
    }

    // TODO: maybe put into UserFactory
    private User getUser(JsonObject jsonObject) {
        //String name = jsonObject.getString("name");
        String givenName = jsonObject.getString("given_name");
        String familyName = jsonObject.getString("family_name");
        String userName = jsonObject.getString("preferred_username");
        String email = jsonObject.getString("email");
        String sub = jsonObject.getString("sub");
        return UserFactory.create(givenName, familyName, userName, sub, email, -1L);
    }

    @Override
    public void destroy() {

    }
}
