package io.radien.security.openid.config;

import io.radien.api.KeycloakConfigs;
import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class OpenIdConfig {

    @Inject
    private OAFAccess oafAccess;

    private String clientId;

    private String clientSecret;

    private String accessTokenUri;

    private String userAuthorizationUri;

    private String redirectUri;

    private String authPrivateContexts;

    private String issuer;

    private String jwkUrl;

    private String env;

    @PostConstruct
    public void init(){
        clientId = oafAccess.getProperty(KeycloakConfigs.RADIEN_CLIENT_ID);
        clientSecret = oafAccess.getProperty(KeycloakConfigs.RADIEN_SECRET);
        accessTokenUri = oafAccess.getProperty(OpenIdProperties.AUTH_ACCESS_TOKEN_URI);
        userAuthorizationUri = oafAccess.getProperty(OpenIdProperties.AUTH_USER_AUTHORIZATION_URI);
        redirectUri = oafAccess.getProperty(OpenIdProperties.AUTH_REDIRECT_URI);
        authPrivateContexts = oafAccess.getProperty(OpenIdProperties.AUTH_PRIVATE_CONTEXTS,"/module");
        issuer = oafAccess.getProperty(OpenIdProperties.AUTH_ISSUER);
        jwkUrl = oafAccess.getProperty(OpenIdProperties.AUTH_JWKURL);
        env = oafAccess.getProperty(OAFProperties.RADIEN_ENV,"PROD");
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getAccessTokenUri() {
        return accessTokenUri;
    }

    public String getUserAuthorizationUri() {
        return userAuthorizationUri;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public String getAuthPrivateContexts() {
        return authPrivateContexts;
    }

    public String getIssuer() {
        return issuer;
    }

    public String getJwkUrl() {
        return jwkUrl;
    }

    public String getEnv() {
        return env;
    }
}
