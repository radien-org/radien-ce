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
package io.radien.ms.usermanagement.service;

import io.radien.api.SystemProperties;
import io.radien.api.model.user.SystemUser;
import io.radien.exception.SystemException;
import io.radien.ms.usermanagement.client.exceptions.RemoteResourceException;
import io.radien.ms.usermanagement.config.KeycloakConfigs;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;

@Stateless
public class KeycloakService {

    private static final Logger log = LoggerFactory.getLogger(KeycloakService.class);

    private KeycloakClient getKeycloakClient() throws RemoteResourceException {
        KeycloakClient client = new KeycloakClient()
                .clientId(getProperty(KeycloakConfigs.ADMIN_CLIENT_ID))
                .username(getProperty(KeycloakConfigs.ADMIN_USER))
                .password(getProperty(KeycloakConfigs.ADMIN_PASSWORD))
                //TODO : ADD missing configurations
                .idpUrl(getProperty(KeycloakConfigs.IDP_URL))
                .tokenPath(getProperty(KeycloakConfigs.TOKEN_PATH))
                .userPath(getProperty(KeycloakConfigs.USER_PATH))
                .radienClientId(getProperty(KeycloakConfigs.RADIEN_CLIENT_ID))
                .radienSecret(getProperty(KeycloakConfigs.RADIEN_SECRET))
                .radienTokenPath(getProperty(KeycloakConfigs.RADIEN_TOKEN_PATH));
        client.login();
        return client;
    }

    public String createUser(SystemUser user) throws RemoteResourceException {
        UserRepresentation userRepresentation = KeycloakFactory.convertToUserRepresentation(user);
        KeycloakClient client = getKeycloakClient();
        String sub= client.createUser(userRepresentation);
        try {
            client.refreshToken();
            client.sendUpdatePasswordEmail(sub);
        } catch (RemoteResourceException e){
            deleteUser(sub);
            throw e;
        }

        return sub;
    }

    public void deleteUser(String sub) throws RemoteResourceException {
        KeycloakClient client = getKeycloakClient();
        client.deleteUser(sub);
    }

    public void updateUser(SystemUser newUser) throws RemoteResourceException {
        UserRepresentation userRepresentation = KeycloakFactory.convertToUserRepresentation(newUser);
        KeycloakClient client = getKeycloakClient();
        client.updateUser(newUser.getSub(), userRepresentation);
    }

    public void sendUpdatePasswordEmail(SystemUser user) throws RemoteResourceException{
        KeycloakClient client = getKeycloakClient();
        client.sendUpdatePasswordEmail(user.getSub());
    }

    public String refeshToken(String refreshToken) throws RemoteResourceException {
        KeycloakClient client = getKeycloakClient();
        return client.refreshToken(refreshToken);
    }

    private String getProperty(SystemProperties cfg) {
        Config config = ConfigProvider.getConfig();
        return config.getValue(cfg.propKey(),String.class);
    }

}
