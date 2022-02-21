/*
 *
 *  Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
 *  <p>
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  <p>
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
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
import io.radien.ms.openid.entities.Authenticated;
import io.radien.ms.openid.entities.Principal;
import io.radien.ms.openid.entities.Public;
import io.radien.ms.openid.service.PrincipalFactory;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Filter that performs authentication of a particular request.
 * @author Nuno Santana, André Sousa
 */
@Provider
@Authenticated
public class AuthenticationFilter implements ContainerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationFilter.class);

    @Context
    private ResourceInfo resourceInfo;
    @Context
    private HttpServletRequest httpRequest;

    /**
     * The filter method of the ContainerRequestFilter is called by the container each time a request is
     * sent to any name bound artifact to the authenticated annotation.
     *
     * A typical implementation of this method would follow the following pattern:
     * Examine the request
     * Optionally wrap the request object with a custom implementation to filter content or headers for input
     * filtering
     * Optionally wrap the response object with a custom implementation to filter content or headers for output
     * filtering
     * Either return, continuing the request, or abort the request with a different response
     * @param containerRequestContext  the ServletRequest object contains the client's request
     * @throws IOException      if an I/O related error has occurred during the processing
     **/
    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        Method method = resourceInfo.getResourceMethod();
        if(!method.isAnnotationPresent(Public.class)) {
            checkAuthentication(containerRequestContext);
        }
    }

    public void checkAuthentication(ContainerRequestContext containerRequestContext) {
        boolean failed = true;
        if (httpRequest.getHeader(HttpHeaders.AUTHORIZATION) != null) {
            String accessToken = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);
            if (accessToken.startsWith("Bearer ")) {
                accessToken = accessToken.substring(7);
                failed = !validateToken(httpRequest.getSession(), accessToken);
                if (!failed) {
                    return;
                }
            }
        }

        if(!(httpRequest.getRequestURI().endsWith("service/v1/health")) &&
            !(httpRequest.getRequestURI().endsWith("service/v1/info"))) {
            containerRequestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Failed authentication..").build());
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


                if (invalidIssuer(issuer,jsonObject.getString("iss"),ConfigProvider.getConfig().getOptionalValue("RADIEN_ENV", String.class).orElse("PROD"))) {return false;}

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

    protected boolean invalidIssuer(String configIssuer,String issuer,String env){
        return !configIssuer.equals(issuer) &&
                (
                        !configIssuer.replace("localhost","host.docker.internal").equals(issuer)
                                || !env.equalsIgnoreCase("LOCAL")
                );
    }


}
