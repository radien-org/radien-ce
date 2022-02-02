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
package io.radien.security.openid.validation;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import io.radien.exception.InvalidAccessTokenException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Component responsible for perform token validation
 * @author Newton Carvalho
 */
@ApplicationScoped
public class OpenIdTokenValidator implements TokenValidator {

    @Inject
    @ConfigProperty(name = "AUTH_ISSUER")
    private String issuer;

    @Inject
    @ConfigProperty(name = "AUTH_JWKURL")
    private String jwkUrl;

    @Inject
    @ConfigProperty(name = "RADIEN_ENV",defaultValue = "PROD")
    private String env;

    JwkProvider provider;

    /**
     * Validates a token
     * @param jwsObject token to be validate
     * @throws InvalidAccessTokenException thrown in case of token being invalid
     */
    @Override
    public void validate(JWSObject jwsObject) throws InvalidAccessTokenException {
        try {
            JWSHeader header = jwsObject.getHeader();

            String kid = header.getKeyID();
            Jwk jwk = getProvider().get(kid);

            JWSVerifier verifier = new RSASSAVerifier((RSAPublicKey) jwk.getPublicKey());
            if (!jwsObject.verify(verifier)) {
                throw new InvalidAccessTokenException("Invalid verify");
            }

            Map<String, Object> payloadMap = jwsObject.getPayload().toJSONObject();

            if (invalidIssuer(issuer,(String)payloadMap.get("iss"),env)) {throw new InvalidAccessTokenException("Invalid iss");}


            if (!payloadMap.get("typ").equals("Bearer")) {
                throw new InvalidAccessTokenException("Invalid type");
            }

            Long expAttr = (Long) payloadMap.get("exp");
            LocalDateTime exp = LocalDateTime.ofInstant(Instant.ofEpochSecond(expAttr), ZoneId.systemDefault());
            if (exp.isBefore(LocalDateTime.now())) {
                throw new InvalidAccessTokenException("Token Expired");
            }
        }
        catch (MalformedURLException | JwkException | JOSEException e) {
            throw new InvalidAccessTokenException(e);
        }
    }

    /**
     * Build a {@link JwkProvider} for a given jwkUrl paramter
     * @return instance of {@link JwkProvider}
     * @throws MalformedURLException in case of invalid url
     */
    protected JwkProvider getProvider() throws MalformedURLException {
        if (provider == null) {
             provider = new UrlJwkProvider(new URL(jwkUrl));
        }
        return provider;
    }

    protected boolean invalidIssuer(String configIssuer,String issuer,String env){
        return !configIssuer.equals(issuer) &&
                (
                        !configIssuer.replace("localhost","host.docker.internal").equals(issuer)
                                || !env.equalsIgnoreCase("LOCAL")
                );
    }
}
