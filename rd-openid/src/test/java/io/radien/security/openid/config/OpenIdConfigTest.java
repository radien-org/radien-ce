package io.radien.security.openid.config;


import io.radien.api.KeycloakConfigs;
import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class OpenIdConfigTest {
    @InjectMocks
    OpenIdConfig openIdConfig;
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();
    @Mock
    OAFAccess oafAccess;

    @Test
    public void test() {
        when(oafAccess.getProperty(KeycloakConfigs.RADIEN_CLIENT_ID)).thenReturn("clientId");
        when(oafAccess.getProperty(KeycloakConfigs.RADIEN_SECRET)).thenReturn("secret");
        when(oafAccess.getProperty(OpenIdProperties.AUTH_ACCESS_TOKEN_URI)).thenReturn("tokenUri");
        when(oafAccess.getProperty(OpenIdProperties.AUTH_USER_AUTHORIZATION_URI)).thenReturn("authUri");
        when(oafAccess.getProperty(OpenIdProperties.AUTH_REDIRECT_URI)).thenReturn("redirectUri");
        when(oafAccess.getProperty(OpenIdProperties.AUTH_ISSUER)).thenReturn("issuer");
        when(oafAccess.getProperty(OpenIdProperties.AUTH_JWKURL)).thenReturn("jwkurl");
        when(oafAccess.getProperty(OpenIdProperties.AUTH_PRIVATE_CONTEXTS,"/module")).thenReturn("/module");
        when(oafAccess.getProperty(OAFProperties.RADIEN_ENV,"PROD")).thenReturn("PROD");
        openIdConfig.init();
        assertEquals("clientId",openIdConfig.getClientId());
        assertEquals("secret",openIdConfig.getClientSecret());
        assertEquals("tokenUri",openIdConfig.getAccessTokenUri());
        assertEquals("authUri",openIdConfig.getUserAuthorizationUri());
        assertEquals("redirectUri",openIdConfig.getRedirectUri());
        assertEquals("issuer",openIdConfig.getIssuer());
        assertEquals("jwkurl",openIdConfig.getJwkUrl());
        assertEquals("/module",openIdConfig.getAuthPrivateContexts());
        assertEquals("PROD",openIdConfig.getEnv());
    }
}