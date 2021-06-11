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
import io.radien.ms.usermanagement.client.exceptions.RemoteResourceException;
import io.radien.ms.usermanagement.config.KeycloakConfigs;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;

/**
 * Keycloak request services and actions
 *
 * @author Nuno Santana
 */
@Stateless
public class KeycloakService {

    private static final Logger log = LoggerFactory.getLogger(KeycloakService.class);

    /**
     * Method to retrieve active keycloak client
     * @return the active keycloak client session
     * @throws RemoteResourceException exceptions that may occur during the execution of a remote method call.
     */
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

    /**
     * Request to the keycloak client to create given user
     * @param user to be created
     * @return the newlly created user subject
     * @throws RemoteResourceException exceptions that may occur during the execution of a remote method call.
     */
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

    /**
     * Method to request keycloak to delete specific user
     * @param sub of the user to be deleted
     * @throws RemoteResourceException exceptions that may occur during the execution of a remote method call.
     */
    public void deleteUser(String sub) throws RemoteResourceException {
        KeycloakClient client = getKeycloakClient();
        client.deleteUser(sub);
    }

    /**
     * Method to request keycloak to update a specific user information
     * @param newUser information to be update
     * @throws RemoteResourceException exceptions that may occur during the execution of a remote method call.
     */
    public void updateUser(SystemUser newUser) throws RemoteResourceException {
        UserRepresentation userRepresentation = KeycloakFactory.convertToUserRepresentation(newUser);
        KeycloakClient client = getKeycloakClient();
        client.updateUser(newUser.getSub(), userRepresentation);
    }

    /**
     * Method to request the keycloak client to send new updated password to the specific user
     * @param user to be reset password and sent email
     * @throws RemoteResourceException exceptions that may occur during the execution of a remote method call.
     */
    public void sendUpdatePasswordEmail(SystemUser user) throws RemoteResourceException{
        KeycloakClient client = getKeycloakClient();
        client.sendUpdatePasswordEmail(user.getSub());
    }

    /**
     * Method to request the keycloak client to refresh access token by a given refresh token (after validation)
     * @param refreshToken to be validated if can refresh access token
     * @return the new access token
     * @throws RemoteResourceException exceptions that may occur during the execution of a remote method call.
     */
    public String refeshToken(String refreshToken) throws RemoteResourceException {
        KeycloakClient client = getKeycloakClient();
        return client.refreshToken(refreshToken);
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

}
