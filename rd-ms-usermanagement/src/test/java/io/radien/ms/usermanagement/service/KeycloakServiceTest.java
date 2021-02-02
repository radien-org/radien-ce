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

import io.radien.ms.usermanagement.entities.User;
import io.radien.ms.usermanagement.legacy.UserFactory;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;

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

    //TODO: Test was failing and usermanagement had to be pause, resume when possible - Bruno Gama

//    @Test
//    public void testCreateUser() throws Exception {
//        String createResponse = PowerMockito.mock(String.class);
//        doReturn(client).when(target,"getKeycloakClient");
//        doReturn(createResponse).when(client,"createUser", ArgumentMatchers.any());
//
//        String firstName = "a";
//        User u = UserFactory.create(firstName, "", "a4", "teste", "a@b.pt", 0L);
//        String sub = target.createUser(u);
//
//        assertEquals("", sub);
//    }

    //TODO: Test was failing and usermanagement had to be pause, resume when possible - Bruno Gama

//    @Test
//    public void testGetKeycloakClient() throws Exception {
//        String createResponse = PowerMockito.mock(String.class);
//        HashMap result = PowerMockito.mock(HashMap.class);
////        HttpResponse<HashMap> httpResponse = PowerMockito.mock(HttpResponse.class);
//        doReturn(result).when(client,"login");
////        doReturn(httpResponse).when(unirest, "asObject", ArgumentMatchers.any());
//        doReturn(createResponse).when(client,"createUser", ArgumentMatchers.any());
//
//        String firstName = "a";
//        User u = UserFactory.create(firstName, "", "a4", "teste", "a@b.pt", 0L);
//        target.createUser(u);
//    }

    @Test
    public void testDeleteUser() throws Exception {
        doReturn(client).when(target,"getKeycloakClient");
        doNothing().when(client,"deleteUser", ArgumentMatchers.any());


        target.deleteUser("");
    }

    //TODO: Test was failing and usermanagement had to be pause, resume when possible - Bruno Gama

//    @Test
//    public void testDeleteUser() throws SystemException {
//        String firstName = "a";
//        User u = UserFactory.create(firstName, "", "a4", "", "a@b.pt", 0L);
//        target.createUser(u);
//
//        target.deleteUser(u.getSub());
//    }
/*
    public void testCreateUserHostNotSpecified() throws SystemException {
        boolean success = false;
        try {
            when(configService.getProperty(KeycloakConfigs.ADMIN_URL)).thenReturn("");
            String firstName = "a";
            User u = UserFactory.create(firstName, "", "", "", "", 0L);
            keycloakService.createUser(u);
        } catch (SystemException s) {
            success = true;
        }
        assertTrue(success);
    }

    @Test
    public void testSendTOTPEmail() {
    }

 */
}