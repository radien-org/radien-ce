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
package io.radien.ms.usermanagement.config;

import io.radien.api.SystemProperties;

/**
 * Keycloak configurations enumerated class
 *
 * @author Nuno Santana
 */
public enum KeycloakConfigs implements SystemProperties {

    ADMIN_URL("keycloak.admin.url"),
    ADMIN_REALM("keycloak.admin.realm"),
    ADMIN_CLIENT_ID("KEYCLOAK_CLIENT_ID"),
    ADMIN_USER("KEYCLOAK_ADMIN"),
    ADMIN_PASSWORD("KEYCLOAK_PASSWORD"),
    APP_REALM("SCRIPT_CLIENT_ID_VALUE"),
    IDP_URL("KEYCLOAK_IDP_URL"),
    TOKEN_PATH("KEYCLOAK_TOKEN_PATH"),
    USER_PATH("KEYCLOAK_USER_PATH"),
    RADIEN_CLIENT_ID("SCRIPT_CLIENT_ID_VALUE"),
    RADIEN_SECRET("SCRIPT_CLIENT_SECRET_VALUE"),
    RADIEN_TOKEN_PATH("REALMS_TOKEN_PATH");

    private String propKey;


    /**
     * Keycloak configuration constructor
     * @param propKey to be used
     */
    KeycloakConfigs(String propKey){
        this.propKey = propKey;
    }

    /**
     * Selected configuration property key getter
     * @return property key
     */
    public String propKey() {
        return propKey;
    }

}