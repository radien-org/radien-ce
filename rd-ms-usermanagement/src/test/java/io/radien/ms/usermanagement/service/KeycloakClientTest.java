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
import io.radien.ms.usermanagement.entities.User;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeycloakClientTest extends TestCase {

    protected final static Logger log = LoggerFactory.getLogger(KeycloakClientTest.class);

    KeycloakClient client;

    public void testKeycloakClient(){
        User u = new User();
        u.setLogon("abc");
        u.setSub("test");
        client = new KeycloakClient()
                .clientId("admin-cli")
                .username("adminsantana")
                .password("NM7uR6ybEx3eu3J")
                .idpUrl("https://idp-int.radien.io")
                .tokenPath("/auth/realms/master/protocol/openid-connect/token")
                .userPath("/auth/admin/realms/radien/users");
        try {
            client.login();
            String id = client.createUser(KeycloakFactory.convertToUserRepresentation(u));
            assertNotNull(id);
        } catch (RemoteResourceException e){
            log.error(e.getMessage(),e);
        }
    }
}