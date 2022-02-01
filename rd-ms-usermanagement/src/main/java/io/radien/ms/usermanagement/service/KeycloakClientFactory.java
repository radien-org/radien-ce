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
package io.radien.ms.usermanagement.service;

import io.radien.api.KeycloakConfigs;
import io.radien.api.SystemProperties;
import io.radien.ms.usermanagement.client.exceptions.RemoteResourceException;
import javax.ejb.Stateless;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.radien.api.OAFProperties.RADIEN_ENV;

/**
 * Component responsible for build/create KeyCloakClient instances in a decoupled way
 * @author newton carvalho
 */
@Stateless
public class KeycloakClientFactory {

    private static final Logger log = LoggerFactory.getLogger(KeycloakClientFactory.class);

    /**
     * Method to retrieve active keycloak client
     * @return the active keycloak client session
     * @throws RemoteResourceException exceptions that may occur during the execution of a remote method call.
     */
    public KeycloakClient getKeycloakClient() throws RemoteResourceException {


        String idpUrl =getProperty(KeycloakConfigs.IDP_URL);
        String tokenPath= getProperty(KeycloakConfigs.TOKEN_PATH);
        String userPath = getProperty(KeycloakConfigs.USER_PATH);
        String clientId = getProperty(KeycloakConfigs.ADMIN_CLIENT_ID);
        log.info("Idp url:{} tokenPath:{} userPath:{} clientId:{}",idpUrl,tokenPath,userPath,clientId);
        KeycloakClient client = new KeycloakClient()
                .clientId(clientId)
                .clientSecret(getProperty(KeycloakConfigs.ADMIN_CLIENT_SECRET))
                .idpUrl(idpUrl)
                .tokenPath(tokenPath)
                .userPath(userPath)
                .radienClientId(getProperty(KeycloakConfigs.RADIEN_CLIENT_ID))
                .radienSecret(getProperty(KeycloakConfigs.RADIEN_SECRET))
                .radienTokenPath(getProperty(KeycloakConfigs.RADIEN_TOKEN_PATH))
                .environment(getPropertyWithDefault(RADIEN_ENV,"PROD"));
        client.login();
        return client;
    }


    /**
     * Method to retrieve the keycloak client configuration
     * @param cfg to be retrieved
     * @return a string value of the keycloak property configuration
     */
    private String getProperty(SystemProperties cfg) {
        Config config = ConfigProvider.getConfig();
        return config.getValue(cfg.propKey(),String.class);
    }

    /**
     * Method to retrieve the keycloak client configuration
     * @param cfg to be retrieved
     * @return a string value of the keycloak property configuration
     */
    private String getPropertyWithDefault(SystemProperties cfg,String defaultValue) {
        Config config = ConfigProvider.getConfig();
        return config.getOptionalValue(cfg.propKey(),String.class).orElse(defaultValue);
    }
}
