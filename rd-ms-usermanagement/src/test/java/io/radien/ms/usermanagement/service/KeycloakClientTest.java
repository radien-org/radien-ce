/*
 * Copyright (c) 2021-present radien GmbH. All rights reserved.
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

import javax.ws.rs.core.Response;
import kong.unirest.Unirest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertNotNull;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "javax.net.ssl.*" })
public class KeycloakClientTest {

    @InjectMocks
    private KeycloakClient keycloakClient;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        keycloakClient = new KeycloakClient()
                .clientId("CLIENT ID")
                .username("USER NAME")
                .password("PASSWORD")
                .idpUrl("IDP URL")
                .tokenPath("TOKEN/PATH")
                .radienClientId("RADIEN CLIENT ID")
                .radienSecret("RADIEN SECRET")
                .radienTokenPath("RADIEN TOKEN PATH")
                .userPath("/USER/PATH");

        KeycloakClient keycloakClient1 = new KeycloakClient();
        assertNotNull(keycloakClient1);

    }


    @Test(expected = Exception.class)
    public void testLoginException() throws RemoteResourceException {
        keycloakClient.login();
    }


    @Test(expected = Exception.class)
    public void testCreateUserException() throws RemoteResourceException {
        UserEntity u = new UserEntity();
        u.setLogon("abc");
        u.setSub("test");

        keycloakClient.createUser(KeycloakFactory.convertToUserRepresentation(u));
    }

    @Test(expected = Exception.class)
    public void testSendUpdatePasswordEmail() throws RemoteResourceException {
        UserEntity u = new UserEntity();
        u.setLogon("abc");
        u.setSub("test");

        keycloakClient.sendUpdatePasswordEmail(u.getSub());
    }

    @Test(expected = Exception.class)
    public void testRefreshTokenException() throws RemoteResourceException {

        keycloakClient.refreshToken();
    }

    @Test(expected = Exception.class)
    public void testRefreshTokenWithException() throws RemoteResourceException {

        keycloakClient.refreshToken(keycloakClient.getRefreshToken());
    }

    @Test(expected = Exception.class)
    public void testUpdateUserException() throws RemoteResourceException {
        UserEntity u = new UserEntity();
        u.setLogon("abc");
        u.setSub("test");

        keycloakClient.updateUser("test", KeycloakFactory.convertToUserRepresentation(u));

    }

    @Test(expected = Exception.class)
    public void testDeleteUserException() throws RemoteResourceException {
        keycloakClient.deleteUser("test");
    }

}