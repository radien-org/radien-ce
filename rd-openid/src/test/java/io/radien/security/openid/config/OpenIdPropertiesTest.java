package io.radien.security.openid.config;

import org.junit.Test;

import static org.junit.Assert.*;

public class OpenIdPropertiesTest {
    @Test
    public void test(){
        assertEquals("AUTH_ACCESS_TOKEN_URI",OpenIdProperties.AUTH_ACCESS_TOKEN_URI.propKey());
        assertEquals("auth.issuer",OpenIdProperties.AUTH_ISSUER.propKey());
        assertEquals("auth.jwkUrl",OpenIdProperties.AUTH_JWKURL.propKey());
        assertEquals("auth.redirectUri",OpenIdProperties.AUTH_REDIRECT_URI.propKey());
        assertEquals("auth.privateContexts",OpenIdProperties.AUTH_PRIVATE_CONTEXTS.propKey());
        assertEquals("AUTH_USER_AUTHORIZATION_URI",OpenIdProperties.AUTH_USER_AUTHORIZATION_URI.propKey());
    }
}