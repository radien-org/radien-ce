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

import io.radien.ms.usermanagement.client.exceptions.RemoteResourceException;
import io.radien.ms.usermanagement.entities.UserEntity;
import junit.framework.TestCase;
import org.eclipse.microprofile.config.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeycloakClientTest extends TestCase {

    protected final static Logger log = LoggerFactory.getLogger(KeycloakClientTest.class);

    KeycloakClient client;

    public void testKeycloakClient(){
        UserEntity u = new UserEntity();
        u.setLogon("abc");
        u.setSub("test");
        client = new KeycloakClient()
                .clientId(ConfigProvider.getConfig().getValue("KEYCLOAK_CLIENT_ID", String.class))
                .username(ConfigProvider.getConfig().getValue("KEYCLOAK_ADMIN", String.class))
                .password(ConfigProvider.getConfig().getValue("KEYCLOAK_PASSWORD", String.class))
                .idpUrl(ConfigProvider.getConfig().getValue("KEYCLOAK_IDP_URL", String.class))
                .tokenPath(ConfigProvider.getConfig().getValue("KEYCLOAK_TOKEN_PATH", String.class))
                .userPath(ConfigProvider.getConfig().getValue("KEYCLOAK_USER_PATH", String.class));
        try {
            client.login();
            String id = client.createUser(KeycloakFactory.convertToUserRepresentation(u));
            assertNotNull(id);
        } catch (RemoteResourceException e){
            log.error(e.getMessage(),e);
        }
    }
}