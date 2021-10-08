/*
 * Copyright (c) 2021-present radien GmbH & its legal owners. All rights reserved.
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

import static org.mockito.ArgumentMatchers.*;

import io.radien.api.model.user.SystemUser;
import io.radien.ms.usermanagement.client.exceptions.RemoteResourceException;
import io.radien.ms.usermanagement.entities.UserEntity;
import io.radien.ms.usermanagement.legacy.UserFactory;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(fullyQualifiedNames = "io.radien.ms.usermanagement.service.KeycloakService")
public class KeycloakServiceTest extends TestCase {
    KeycloakService target;

    KeycloakClient client;

    @Before
    public void setUp() throws Exception {
        target = spy(new KeycloakService());
        client = spy(new KeycloakClient());
    }

    @Test
    public void testCreateUser() throws Exception {
        String createResponse = PowerMockito.mock(String.class);
        doReturn(client).when(target,"getKeycloakClient");
        doReturn(createResponse).when(client,"createUser", ArgumentMatchers.any());

        doNothing().when(client).refreshToken();
        doNothing().when(client).sendUpdatePasswordEmail(any());

        String firstName = "a";
        UserEntity u = UserFactory.create(firstName, "", "a4", "teste", "a@b.pt", 0L);
        String sub = target.createUser(u);

        assertEquals("", sub);
    }

    @Test
    public void testCreateUserException() throws Exception {
        String createResponse = PowerMockito.mock(String.class);
        doReturn(client).when(target,"getKeycloakClient");
        doReturn(createResponse).when(client,"createUser", ArgumentMatchers.any());

        doNothing().when(client).refreshToken();
        doThrow(new RemoteResourceException()).when(client).sendUpdatePasswordEmail(any());
        doNothing().when(client).deleteUser(any());

        String firstName = "a";
        UserEntity u = UserFactory.create(firstName, "", "a4", "teste", "a@b.pt", 0L);

        boolean success = false;

        try{
            target.createUser(u);
        }catch (Exception e) {
            success = true;
        }

        assertTrue(success);
    }

    @Test
    public void testUpdateUser() throws Exception {
        String createResponse = PowerMockito.mock(String.class);
        doReturn(client).when(target,"getKeycloakClient");
        doReturn(createResponse).when(client,"createUser", ArgumentMatchers.any());

        SystemUser u = UserFactory.create("firstname", "", "a4", "teste", "a@b.pt", 0L);

        doNothing().when(client).updateUser(any(), any());
        boolean success = true;

        try{
            target.updateUser(u);
        }catch (Exception e) {
            success = false;
        }

        assertTrue(success);
    }

    @Test
    public void testSendUpdatePasswordEmail() throws Exception {
        String createResponse = PowerMockito.mock(String.class);
        doReturn(client).when(target,"getKeycloakClient");
        doReturn(createResponse).when(client,"createUser", ArgumentMatchers.any());

        SystemUser u = UserFactory.create("firstname", "", "a4", "teste","a@b.pt", 0L);

        doNothing().when(client).sendUpdatePasswordEmail(any());
        boolean success = true;

        try{
            target.sendUpdatePasswordEmail(u);
        }catch (Exception e) {
            success = false;
        }

        assertTrue(success);
    }

    @Test
    public void testRefreshToken() throws Exception {
        String createResponse = PowerMockito.mock(String.class);
        doReturn(client).when(target,"getKeycloakClient");
        doReturn(createResponse).when(client,"createUser", ArgumentMatchers.any());

        SystemUser u = UserFactory.create("firstname", "", "a4", "teste", "a@b.pt", 0L);

        doReturn("teste").when(client).refreshToken(any());

        String refreshToken = target.refreshToken("test");

        assertEquals("teste", refreshToken);
    }

    @Test
    public void testDeleteUser() throws Exception {
        doReturn(client).when(target,"getKeycloakClient");
        doNothing().when(client,"deleteUser", ArgumentMatchers.any());

        boolean success = true;
        try {
            target.deleteUser("");
        } catch (Exception e) {
            success = false;
        }
        assertTrue(success);
    }
}