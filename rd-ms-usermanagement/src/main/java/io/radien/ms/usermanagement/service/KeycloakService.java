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
import io.radien.ms.usermanagement.config.KeycloakConfigs;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;

import javax.ws.rs.BadRequestException;



@Stateless
public class KeycloakService {

    private static final Logger log = LoggerFactory.getLogger(KeycloakService.class);

    private KeycloakClient getKeycloakClient() throws SystemException {
        KeycloakClient client = new KeycloakClient()
                .clientId(getProperty(KeycloakConfigs.ADMIN_CLIENT_ID))
                .username(getProperty(KeycloakConfigs.ADMIN_USER))
                .password(getProperty(KeycloakConfigs.ADMIN_PASSWORD))
                //TODO : ADD missing configurations
                .idpUrl("https://idp-int.radien.io")
                .tokenPath("/auth/realms/master/protocol/openid-connect/token")
                .userPath("/auth/admin/realms/radien/users");
        client.login();
        return client;
    }

    public String createUser(SystemUser user) throws SystemException {
        UserRepresentation userRepresentation = KeycloakFactory.convertToUserRepresentation(user);
        KeycloakClient client = getKeycloakClient();
        String sub= client.createUser(userRepresentation);
        try {
            client.sendUpdatePasswordEmail(sub);
        } catch (SystemException e){
            deleteUser(sub);
            throw e;
        }

        return sub;
//        try(Response response = usersResource.create(userRepresentation)){
//            if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
//                userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
//                try {
//                    sendUpdatePasswordEmail(usersResource, userId);
//                } catch (SystemException e){
//                    deleteUser(userId);
//                    throw new SystemException("Unable to create User caused by:"+ e.getMessage());
//                }
//                return userId;
//            } else {
//                //TODO: ERROR HANDLING
//                //response.readEntity(Map.class).get("errorMessage");
//                String msg = response.readEntity(String.class);
//                log.error(msg);
//                //can be caused by user exists with same username
//                //can be caused by username is empty
//                throw new SystemException("Unable to create user on keycloak:" + msg);
//            }
//        } catch (ProcessingException e) {
//            //can be caused by ClientProtocolException when no host is provided
//            //can be caused by UnknownHostException when dns is not resolved
//            //can be caused by NotAuthorizedException when password is not present or wrong
//            //can be caused by NotFoundException when host is resolved but it doesn't have an answer
//            //  happens for example when configurations are not set other than the host
//            log.error(e.getMessage(),e);
//            throw new SystemException("Unable to contact Keycloak");
//        }

    }

    public void deleteUser(String sub) throws SystemException {
        KeycloakClient client = getKeycloakClient();
        client.deleteUser(sub);
    }

    public void updateUser(SystemUser newUser) throws SystemException {
        UserRepresentation userRepresentation = KeycloakFactory.convertToUserRepresentation(newUser);
        KeycloakClient client = getKeycloakClient();
        client.updateUser(newUser.getSub(), userRepresentation);
    }

    private String getProperty(SystemProperties cfg) {
        Config config = ConfigProvider.getConfig();
        return config.getValue(cfg.propKey(),String.class);
    }

}
