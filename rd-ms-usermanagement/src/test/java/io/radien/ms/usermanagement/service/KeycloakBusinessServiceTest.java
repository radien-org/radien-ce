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

import io.radien.api.model.user.SystemUser;
import io.radien.exception.BadRequestException;
import io.radien.ms.usermanagement.client.exceptions.RemoteResourceException;
import io.radien.ms.usermanagement.client.services.UserFactory;
import io.radien.ms.usermanagement.entities.UserEntity;
import io.radien.ms.usermanagement.util.KeycloakClientFactory;
import java.util.Optional;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class KeycloakBusinessServiceTest {

    @InjectMocks
    KeycloakBusinessService target;

    @Mock
    KeycloakClient client;

    @Mock
    KeycloakClientFactory keycloakClientFactory;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    static String EMPTY_MOCK_RESPONSE = "";

    @Before
    public void setUp() throws Exception {
        when(keycloakClientFactory.getKeycloakClient()).thenReturn(client);
    }

    @Test
    public void testCreateUser() throws Exception {
        doReturn(EMPTY_MOCK_RESPONSE).when(client).createUser(any());
        doNothing().when(client).refreshToken();
        doNothing().when(client).sendUpdatePasswordEmail(any());
        String firstName = "a";
        UserEntity u = new UserEntity(UserFactory.create(firstName, "", "a4", "teste", "a@b.pt", 0L));
        String sub = target.createUser(u);
        assertEquals("", sub);
    }

    @Test
    public void testCreateUserException() throws Exception {
        doReturn(EMPTY_MOCK_RESPONSE).when(client).createUser(any());
        doNothing().when(client).refreshToken();
        doThrow(new RemoteResourceException()).when(client).sendUpdatePasswordEmail(any());
        doNothing().when(client).deleteUser(any());

        String firstName = "a";
        UserEntity u = new UserEntity(UserFactory.create(firstName, "", "a4", "teste", "a@b.pt", 0L));
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
        doReturn(EMPTY_MOCK_RESPONSE).when(client).createUser(any());
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
        doReturn(EMPTY_MOCK_RESPONSE).when(client).createUser(any());
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
        doReturn(EMPTY_MOCK_RESPONSE).when(client).refreshToken(any());
        String refreshToken = target.refreshToken("test");
        assertEquals(EMPTY_MOCK_RESPONSE, refreshToken);
    }

    @Test
    public void testDeleteUser() throws Exception {
        doNothing().when(client).deleteUser(any());
        boolean success = true;
        try {
            target.deleteUser("");
        } catch (Exception e) {
            success = false;
        }
        assertTrue(success);
    }

    /**
     * Test method for {@link KeycloakBusinessService#validateChangeCredentials(String, String, String, String)}
     * Scenario: Credential Validation concluded with success. Change password also concluded successfully
     * @throws Exception thrown in case of any issue regarding communication with KeyCloak service,
     * or mocking injection issues
     */
    @Test
    public void testValidateChangeCredentials() throws Exception {
        String username = "test.test";
        String subject = "aaa-bb-cc-dd";
        String password = "test";
        String newPassword = "test2";
        doReturn(Boolean.TRUE).when(client).validateCredentials(username, password);
        doNothing().when(client).changePassword(subject, newPassword);
        try {
            target.validateChangeCredentials(username, subject, password, newPassword);
        }
        catch (BadRequestException | RemoteResourceException e) {
            fail();
        }
    }

    /**
     * Test method for {@link KeycloakBusinessService#validateChangeCredentials(String, String, String, String)}
     * Scenario: Credential Validation fails. Expected outcome: UserChangeCredentialException thrown.
     * @throws Exception thrown in case of any issue regarding communication with KeyCloak service,
     * or mocking injection issues
     */
    @Test(expected = BadRequestException.class)
    public void testCredentialValidationFails() throws Exception {
        String username = "test.test";
        String subject = "aaa-bb-cc-dd";
        String password = "test";
        String newPassword = "test2";
        doReturn(Boolean.FALSE).when(client).validateCredentials(username, password);
        target.validateChangeCredentials(username, subject, password, newPassword);
    }

    /**
     * Test method for {@link KeycloakBusinessService#validateChangeCredentials(String, String, String, String)}
     * Scenario: Credential Validation successfully concluded.
     * But an issue occurs during execution of reset-password endpoint.
     * @throws Exception thrown in case of any issue regarding communication with KeyCloak service,
     * or mocking injection issues
     */
    @Test(expected = RemoteResourceException.class)
    public void testResetFails() throws Exception {
        String username = "test.test";
        String subject = "aaa-bb-cc-dd";
        String password = "test";
        String newPassword = "test2";
        doReturn(Boolean.TRUE).when(client).validateCredentials(username, password);
        doThrow(new RemoteResourceException("error")).when(client).changePassword(subject, newPassword);
        target.validateChangeCredentials(username, subject, password, newPassword);
    }

    /**
     * Test for method {@link KeycloakBusinessService#updateEmailAndExecuteActionEmailVerify(String, String, boolean)}
     * @throws RemoteResourceException thrown in case of any communication issue with KeyCloak
     */
    @Test
    public void testUpdateEmailAndExecuteActionEmailVerify() throws RemoteResourceException {
        String email = "test.test@test.com";
        String sub = "111-22-2222-eeee";
        boolean emailVerify = true;
        doNothing().when(client).updateEmailAndExecuteActionEmailVerify(argThat(sub::equals),
                argThat(ur -> ur.getEmail().equals(email) && ur.isEmailVerified() == emailVerify));
        doNothing().when(client).sendUpdatedEmailToVerify(sub);
        try {
            target.updateEmailAndExecuteActionEmailVerify(email, sub, emailVerify);
        } catch (RemoteResourceException r) {
            fail();
        }
    }

    /**
     * Test for method {@link KeycloakBusinessService#getSubFromEmail(String)}
     * @throws RemoteResourceException thrown in case of any communication issue with KeyCloak
     */
    @Test
    public void testGetSubFromEmail() throws RemoteResourceException{
        String email = "test.test@test.com";
        String sub = "aaa-bbb-ccc-ddd";
        doReturn(Optional.of(sub)).when(client).getSubFromEmail(email);
        try {
            target.getSubFromEmail(email);
        } catch (RemoteResourceException r) {
            fail();
        }
    }


}