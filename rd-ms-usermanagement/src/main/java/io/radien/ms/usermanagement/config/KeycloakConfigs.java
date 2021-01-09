package io.radien.ms.usermanagement.config;

import io.radien.api.SystemProperties;

public enum KeycloakConfigs implements SystemProperties {

    ADMIN_URL("keycloak.admin.url"),
    ADMIN_REALM("keycloak.admin.realm"),
    ADMIN_CLIENT_ID("keycloak.admin.client.id"),
    ADMIN_USER("keycloak.admin.user"),
    ADMIN_PASSWORD("keycloak.admin.password"),
    APP_REALM("keycloak.app.realm");
    private String propKey;

    KeycloakConfigs(String propKey){
        this.propKey = propKey;
    }

    public String propKey() {
        return propKey;
    }

}
