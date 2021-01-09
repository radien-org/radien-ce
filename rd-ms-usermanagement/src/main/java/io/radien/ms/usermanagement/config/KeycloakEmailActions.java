package io.radien.ms.usermanagement.config;

import io.radien.api.SystemProperties;

public enum KeycloakEmailActions  {

    TOTP("CONFIGURE_TOTP"),
    TERMS("TERMS_AND_CONDITIONS") ,
    UPDATE_PASSWORD("UPDATE_PASSWORD") ,
    UPDATE_PROFILE("UPDATE_PROFILE") ,
    VERIFY_EMAIL("VERIFY_EMAIL");

    private String propKey;

    KeycloakEmailActions(String propKey){
        this.propKey = propKey;
    }

    public String propKey() {
        return propKey;
    }

}
