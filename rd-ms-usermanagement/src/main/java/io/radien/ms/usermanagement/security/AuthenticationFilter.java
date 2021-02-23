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
package io.radien.ms.usermanagement.security;


import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import io.radien.ms.usermanagement.client.services.UserFactory;
import io.radien.ms.usermanagement.client.entities.User;
import org.keycloak.AuthorizationContext;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.common.util.Base64;
import org.keycloak.representations.JsonWebToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
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
        if(req.getHeader(HttpHeaders.AUTHORIZATION) != null){
            String accessToken = req.getHeader(HttpHeaders.AUTHORIZATION);
            if(accessToken.startsWith("Bearer ")){
                accessToken = accessToken.substring(7);
                failed = !validateToken(req.getSession(),accessToken);
                if(!failed){
                    chain.doFilter(request, response);
                }
            }

        }

        if (failed){
            HttpServletResponse resp = (HttpServletResponse) response;
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Failed authentication..");
        }

    }

    private boolean validateToken(HttpSession session, String accessToken){
        try {
            JWSObject jwsObject = JWSObject.parse(accessToken);

            String text = readFromResource("keycloak.pem",StandardCharsets.UTF_8);
            //TODO: validate that this was signed by keycloak public key present in URL
            //        https://idp-int.radien.io/auth/realms/radien/protocol/openid-connect/certs
            //          in x5c field


            JWSHeader header = jwsObject.getHeader();
            String kid = header.getKeyID();

            Payload payload =jwsObject.getPayload();
            JsonReader reader = Json.createReader(new StringReader(payload.toString()));
            JsonObject jsonObject = reader.readObject();
            String[] scopes = jsonObject.getString("scope").split(" ");
            //TODO: validate scopes

            LocalDateTime exp = LocalDateTime.ofInstant(Instant.ofEpochSecond(jsonObject.getJsonNumber("exp").longValue()), ZoneId.systemDefault());
            LocalDateTime now = exp.now();
            if(exp.isBefore(now)){
                //TODO: refresh token
                return false;

            }
            User principal = getUser(jsonObject);
            session.setAttribute("USER",principal);

            return true;
        } catch (ParseException e) {
            log.error("Unable to parse Access Token",e);
        }
        return false;
    }
    // TODO: maybe put into UserFactory
    private User getUser(JsonObject jsonObject){
        //String name = jsonObject.getString("name");
        String givenName = jsonObject.getString("given_name");
        String familyName = jsonObject.getString("family_name");
        String userName = jsonObject.getString("preferred_username");
        String email = jsonObject.getString("email");
        String sub = jsonObject.getString("sub");
        return UserFactory.create(givenName,familyName,userName,sub,email,-1L);
    }

    // TODO: maybe put into Some File Utils
    private String readFromResource(String filename, Charset charset){
        return new BufferedReader(
                new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(filename), charset))
                .lines()
                .collect(Collectors.joining("\n"));
    }

    @Override
    public void destroy() {

    }
}
