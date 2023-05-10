package io.radien.security.openid.config;

import io.radien.api.SystemProperties;

public enum OpenIdProperties implements SystemProperties {

    AUTH_ACCESS_TOKEN_URI("AUTH_ACCESS_TOKEN_URI"),
    AUTH_USER_AUTHORIZATION_URI("AUTH_USER_AUTHORIZATION_URI"),
    AUTH_REDIRECT_URI("auth.redirectUri"),
    AUTH_PRIVATE_CONTEXTS("auth.privateContexts"),
    AUTH_ISSUER("auth.issuer"),
    AUTH_JWKURL("auth.jwkUrl");

    private String propKey;

    OpenIdProperties(String propKey){
        this.propKey= propKey;
    }

    @Override
    public String propKey() {
        return propKey;
    }
}
