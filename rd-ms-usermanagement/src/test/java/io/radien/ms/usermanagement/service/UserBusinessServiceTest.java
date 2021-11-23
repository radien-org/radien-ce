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

import io.radien.api.entity.Page;
import io.radien.api.model.user.SystemUser;
import io.radien.api.model.user.SystemUserSearchFilter;
import io.radien.api.service.batch.BatchSummary;
import io.radien.api.service.user.UserServiceAccess;
import io.radien.exception.UniquenessConstraintException;
import io.radien.exception.UserChangeCredentialException;
import io.radien.exception.UserNotFoundException;
import io.radien.ms.usermanagement.client.entities.User;
import io.radien.ms.usermanagement.client.entities.UserPasswordChanging;
import io.radien.ms.usermanagement.client.entities.UserSearchFilter;
import io.radien.ms.usermanagement.client.exceptions.RemoteResourceException;
import io.radien.ms.usermanagement.client.services.UserFactory;
import io.radien.ms.usermanagement.entities.UserEntity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static io.radien.api.SystemVariables.CONFIRM_NEW_PASSWORD;
import static io.radien.api.SystemVariables.LOGON;
import static io.radien.api.SystemVariables.NEW_PASSWORD;
import static io.radien.api.SystemVariables.OLD_PASSWORD;
import static io.radien.exception.GenericErrorCodeMessage.INVALID_VALUE_FOR_PARAMETER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doNothing;

/**
 * Class that aggregates UnitTest cases for UserBusinessService
 */
public class UserBusinessServiceTest {

    @InjectMocks
    UserBusinessService userBusinessService;

    @Mock
    UserServiceAccess userServiceAccess;

    @Mock
    KeycloakService keycloakService;

    /**
     * Prepares required mock objects
     */
    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test for method {@link UserBusinessService#getUserId(String)}
     */
    @Test
    public void testGetUserIdBySub() {

        when(userServiceAccess.getUserId("sub1")).thenReturn(1L);
        when(userServiceAccess.getUserId("sub2")).thenReturn(null);

        Long id = userBusinessService.getUserId("sub1");
        Assert.assertNotNull( id );
        Assert.assertEquals( 1L, (long) id );

        id = userBusinessService.getUserId("sub2");
        Assert.assertNull( id );
    }

    /**
     * Test for method {@link UserBusinessService#getAll(String, int, int, List, boolean)}
     */
    @Test
    public void testGetAll() {
        String search = "";
        Page<SystemUser> p = new Page<>(new ArrayList<>(),-1,0,0);
        when(userServiceAccess.getAll(search,1,2,null,true)).thenReturn(p);
        Page<? extends SystemUser> result = userBusinessService.getAll(search,1,2,null,true);
        Assert.assertEquals( p, result );
    }

    /**
     * Test for method {@link UserBusinessService#getUsers(SystemUserSearchFilter)}
     */
    @Test
    public void testGetUsers() {
        UserSearchFilter filter = new UserSearchFilter();
        when(userServiceAccess.getUsers(filter)).thenReturn(new ArrayList<>());
        List<? extends SystemUser> results = userBusinessService.getUsers(filter);
        Assert.assertEquals( 0, results.size() );
    }

    /**
     * Test for method {@link UserBusinessService#get(List)}
     * @throws UserNotFoundException if user not found
     */
    @Test
    public void testGet() throws UserNotFoundException {
        SystemUser user = UserFactory.create("a","b","l","s","e",1L);
        List<Long> listIds = Collections.singletonList(2L);
        List<SystemUser> listUsers = Collections.singletonList(user);
        when(userServiceAccess.get(2L)).thenReturn(user);
        when(userServiceAccess.get(listIds)).thenReturn(listUsers);
        SystemUser result = userBusinessService.get(2L);
        List<SystemUser> results = userBusinessService.get(listIds);
        Assert.assertEquals( user, result );
        Assert.assertEquals( listUsers, results );
    }

    /**
     * Test for method {@link UserBusinessService#delete(long)}
     * @throws UserNotFoundException if user not found
     */
    @Test
    public void testDelete() throws UserNotFoundException {
        SystemUser user = UserFactory.create("a", "b", "l", "subTest", "e", 1L);
        when(userServiceAccess.get((Long) any())).thenReturn(user);

        boolean success = false;
        try{
            userBusinessService.delete(1L);
        } catch (Exception e){
            success = true;
        }
        Assert.assertFalse( success );
    }

    /**
     * Test method for {@link UserBusinessService#create(User, boolean)}
     * @throws UniquenessConstraintException if unique user founds
     * @throws UserNotFoundException if user not found
     * @throws RemoteResourceException if any empty values of logon, email, first and last name
     */
    @Test
    public void testCreateException() throws UniquenessConstraintException, RemoteResourceException, UserNotFoundException {
        User u = UserFactory.create("a","b","l","s","e",1L);
        doThrow(new UniquenessConstraintException("")).when(userServiceAccess).create(u);
        boolean success = false;
        try{
            userBusinessService.create(u,true);
        } catch (UniquenessConstraintException e){
            success = true;
        }
        Assert.assertTrue( success );
    }

    /**
     * Test method for {@link UserBusinessService#create(User, boolean)}
     * @throws UniquenessConstraintException if unique user founds
     * @throws UserNotFoundException if user not found
     */
    @Test
    public void testCreateSkipKeycloak() throws UniquenessConstraintException, UserNotFoundException {
        User user = UserFactory.create("a","b","l",null,"e",1L);
        user.setId(2L);
        User user2 = UserFactory.create("a","b","l",null,"e",1L);
        when(userServiceAccess.get((Long) any())).thenReturn(user2);

        boolean success = true;
        try{
            userBusinessService.create(user2, false);
        } catch (RemoteResourceException e){
            success = false;
        }
        Assert.assertTrue( success );
    }

    /**
     * Test method for {@link UserBusinessService#create(User, boolean)}
     * @throws UniquenessConstraintException if unique user founds
     * @throws UserNotFoundException if user not found
     * @throws RemoteResourceException if any empty values of logon, email, first and last name
     */
    @Test
    public void testCreateSkipKeycloakWithCreationRemoteException() throws UniquenessConstraintException, UserNotFoundException, RemoteResourceException {
        User user2 = UserFactory.create("a","b","l",null,"e",1L);
        when(userServiceAccess.get((Long) any())).thenReturn(user2);
        doThrow(new RemoteResourceException()).when(keycloakService).createUser(any());
        boolean success = true;
        try{
            userBusinessService.create(user2, false);
        } catch (RemoteResourceException e){
            success = false;
        }
        Assert.assertFalse( success );
    }

    /**
     * Test method for {@link UserBusinessService#create(User, boolean)}
     * @throws UniquenessConstraintException if unique user founds
     * @throws UserNotFoundException if user not found
     * @throws RemoteResourceException if any empty values of logon, email, first and last name
     */
    @Test
    public void testCreateSkipKeycloakWithCreationRemoteExceptionSub() throws UniquenessConstraintException, UserNotFoundException, RemoteResourceException {
        User user2 = UserFactory.create("a","b","l","sub","e",1L);
        when(userServiceAccess.get((Long) any())).thenReturn(user2);
        doThrow(new RemoteResourceException()).when(keycloakService).getSubFromEmail(anyString());
        boolean success = true;
        try{
            userBusinessService.create(user2, false);
        } catch (RemoteResourceException e){
            success = false;
        }
        Assert.assertTrue( success );
    }

    /**
     * Test method for {@link UserBusinessService#create(User, boolean)}
     * @throws UserNotFoundException if user not found
     * @throws RemoteResourceException if any empty values of logon, email, first and last name
     */
    @Test
    public void testCreateUserFirstNameIsNull() throws UserNotFoundException, RemoteResourceException {
        prepareTestForCreateUserFirstNameLastNameIsNull(null, "last");
    }

    /**
     * Test for method {@link UserBusinessService#create(User, boolean)}
     * @throws UserNotFoundException if user not found
     */
    @Test
    public void testCreateUserLastNameIsNull() throws UserNotFoundException, RemoteResourceException {
        prepareTestForCreateUserFirstNameLastNameIsNull("first", null);
    }


    /**
     * Test method for {@link UserBusinessService#create(User, boolean)}
     * @throws UserNotFoundException if user not found
     */
    @Test
    public void testCreateUserCreationIsTrue() throws UserNotFoundException {
        User user4 = new User();
        user4.setLogon("logon");
        user4.setUserEmail("email@email.com");
        user4.setFirstname("first");
        user4.setLastname("last");
        when(userServiceAccess.get((Long) any())).thenReturn(user4);
        boolean success4 = true;
        try{
            userBusinessService.create(user4, true);
        } catch (RemoteResourceException | UniquenessConstraintException e){
            success4 = false;
        }
        Assert.assertTrue(success4);
    }

    /**
     * Test method for {@link UserBusinessService#update(User, boolean)}
     * skipping keycloak
     * @throws UserNotFoundException if user not found
     */
    @Test
    public void testUpdateUserSkippingKeycloakSuccess() throws UserNotFoundException {
        User user4 = new User();
        user4.setLogon("logon");
        user4.setUserEmail("email@email.com");
        user4.setFirstname("first");
        user4.setLastname("last");
        boolean success4 = true;
        try{
            userBusinessService.update(user4, true);
        } catch (RemoteResourceException | UniquenessConstraintException e){
            success4 = false;
        }
        Assert.assertTrue(success4);
    }

    /**
     * Test method for {@link UserBusinessService#update(User, boolean)}
     * but NOT skipping keycloak. Expected outcome: SUCCESS.
     * @throws UserNotFoundException if user not found
     * @throws RemoteResourceException thrown in case of communication error with Keycloak service
     * @throws UniquenessConstraintException thrown in case of conflict regarding repeated values
     * during update operation
     */
    @Test
    public void testUpdateUserNotSkippingKeycloak() throws UserNotFoundException,
            RemoteResourceException, UniquenessConstraintException {
        User user4 = new User();
        user4.setId(111111L);
        user4.setLogon("logon");
        user4.setUserEmail("email@email.com");
        user4.setFirstname("first");
        user4.setLastname("last");
        boolean success4 = true;
        try{
            userBusinessService.update(user4, false);
        } catch (RemoteResourceException e){
            success4 = false;
        }
        Assert.assertTrue(success4);
    }

    /**
     * Test method for {@link UserBusinessService#update(User, boolean)}
     * but expecting an error being thrown by Keycloak. Expected outcome: FAILURE.
     * @throws UserNotFoundException if user not found
     * @throws RemoteResourceException thrown in case of communication error with Keycloak service
     * @throws UniquenessConstraintException thrown in case of conflict regarding repeated values
     * during update operation
     */
    @Test(expected = RemoteResourceException.class)
    public void testUpdateUserKeycloakError() throws UserNotFoundException,
            RemoteResourceException, UniquenessConstraintException {
        User user4 = new User();
        user4.setId(111111L);
        user4.setLogon("logon");
        user4.setUserEmail("email@email.com");
        user4.setFirstname("first");
        user4.setLastname("last");
        boolean success4 = true;
        when(userServiceAccess.get(user4.getId())).thenReturn(user4);
        doThrow(new RemoteResourceException("remote error")).
                when(keycloakService).updateUser(user4);
        userBusinessService.update(user4, false);
    }



    /**
     * Test method for {@link UserBusinessService#create(User, boolean)}
     * @throws UniquenessConstraintException if unique user founds
     * @throws UserNotFoundException if user not found
     */
    @Test
    public void testCreateEmptyLogon() throws UniquenessConstraintException, UserNotFoundException {
        User user = UserFactory.create("a","b","",null,"e",1L);
        user.setId(2L);
        when(userServiceAccess.get((Long) any())).thenReturn(user);

        boolean success = true;
        try{
            userBusinessService.create(user, false);
        } catch (RemoteResourceException e){
            success = false;
        }
        Assert.assertFalse(success);
    }

    /**
     * Test method for {@link UserBusinessService#create(User, boolean)}
     * @throws UniquenessConstraintException if unique user founds
     * @throws UserNotFoundException if user not found
     */
    @Test
    public void testCreateEmptyFirstName() throws UniquenessConstraintException, UserNotFoundException {
        User user = UserFactory.create("","b","logon",null,"c",1L);
        user.setId(2L);
        when(userServiceAccess.get((Long) any())).thenReturn(user);

        boolean success = true;
        try{
            userBusinessService.create(user, false);
        } catch (RemoteResourceException e){
            success = false;
        }
        Assert.assertFalse(success);
    }

    /**
     * Test method for {@link UserBusinessService#create(User, boolean)}
     * @throws UniquenessConstraintException if unique user founds
     * @throws UserNotFoundException if user not found
     */
    @Test
    public void testCreateEmptyLastName() throws UniquenessConstraintException, UserNotFoundException {
        User user = UserFactory.create("a","","logon",null,"c",1L);
        user.setId(2L);
        when(userServiceAccess.get((Long) any())).thenReturn(user);

        boolean success = true;
        try{
            userBusinessService.create(user, false);
        } catch (RemoteResourceException e){
            success = false;
        }
        Assert.assertFalse(success);
    }

    /**
     * Test method for {@link UserBusinessService#create(User, boolean)}
     * @throws UniquenessConstraintException if unique user founds
     * @throws UserNotFoundException if user not found
     */
    @Test
    public void testCreateEmptyEmail() throws UniquenessConstraintException, UserNotFoundException {
        User user = UserFactory.create("a","b","logon",null,"",1L);
        user.setId(2L);
        when(userServiceAccess.get((Long) any())).thenReturn(user);

        boolean success = true;
        try{
            userBusinessService.create(user, false);
        } catch (RemoteResourceException e){
            success = false;
        }
        Assert.assertFalse( success );
    }

    /**
     * Test method for {@link UserBusinessService#create(User, boolean)} not skipping Keycloak
     * and throwing RemoteException
     * @throws UniquenessConstraintException if unique user founds
     * @throws UserNotFoundException if user not found
     * @throws RemoteResourceException if any empty values of logon, email, first and last name
     */
    @Test
    public void testCreateNoSkippingKeycloakWithRemoteException() throws UniquenessConstraintException, UserNotFoundException, RemoteResourceException {
        User user = UserFactory.create("a","b","l",null,"e",1L);
        user.setId(2L);
        User user2 = UserFactory.create("a","b","l",null,"e",1L);
        when(userServiceAccess.get((Long) any())).thenReturn(user2);
        doThrow(new RemoteResourceException()).when(keycloakService).createUser(any());

        boolean success = true;
        try{
            userBusinessService.create(user, false);
        } catch (RemoteResourceException e){
            success = false;
        }
        Assert.assertFalse(success);
    }

    /**
     * Test method for {@link UserBusinessService#create(User, boolean)}
     * @throws UniquenessConstraintException if unique user founds
     * @throws UserNotFoundException if user not found
     */
    @Test
    public void testCreateEmptyUsername() throws UniquenessConstraintException, UserNotFoundException {
        User u = UserFactory.create("a","b","","s","e",1L);
        boolean success = false;
        try{
            userBusinessService.create(u,false);
        } catch (RemoteResourceException e){
            success = true;
        }
        Assert.assertTrue(success);
    }

    /**
     * Test method for {@link UserBusinessService#create(User, boolean)}
     * @throws UniquenessConstraintException if unique user founds
     * @throws UserNotFoundException if user not found
     */
    @Test
    public void testCreateCreationFalse() throws UniquenessConstraintException, UserNotFoundException {
        User user = UserFactory.create("a","b","l",null,"e",1L);
        user.setId(2L);
        User user2 = UserFactory.create("a","b","l",null,"e",1L);
        when(userServiceAccess.get((Long) any())).thenReturn(user2);

        boolean success = true;
        try{
            userBusinessService.create(user, false);
        } catch (RemoteResourceException e){
            success = false;
        }
        Assert.assertTrue(success);
    }

    /**
     * Test method for {@link UserBusinessService#create(List)}
     */
    @Test
    public void testCreateBatch() {
        List<io.radien.ms.usermanagement.entities.UserEntity> users = new ArrayList<>();
        int numberOfElementsToInsert = 100;
        when(userServiceAccess.create(anyList())).thenReturn(new BatchSummary(numberOfElementsToInsert));
        BatchSummary batchSummary = userServiceAccess.create(users);
        Assert.assertNotNull(batchSummary);
        Assert.assertEquals(batchSummary.getTotalProcessed(), numberOfElementsToInsert);
        Assert.assertEquals(batchSummary.getTotal(), batchSummary.getTotalProcessed());
        Assert.assertEquals(0, batchSummary.getTotalNonProcessed());
        Assert.assertNotNull(batchSummary.getNonProcessedItems());
        Assert.assertEquals(0, batchSummary.getNonProcessedItems().size());
    }

    /**
     * Test method for {@link UserBusinessService#create(List)}
     * @throws Exception if any error occurs
     */
    @Test
    public void testSendUpdatePasswordEmail() throws Exception {
        User user = UserFactory.create("first", "last", "logon", "test-sub", "u@email.com", 1L);
        when(userServiceAccess.get((Long) any())).thenReturn(user);
        doNothing().when(keycloakService,"sendUpdatePasswordEmail", ArgumentMatchers.any());
        boolean success = true;
        try {
            userBusinessService.sendUpdatePasswordEmail(user);
        } catch (Exception e) {
            success = false;
        }
        Assert.assertTrue(success);
    }

    /**
     * Test method for {@link UserBusinessService#create(List)}
     */
    @Test
    public void testCreate() {
        SystemUser user = UserFactory.create("a","b","l",null,"e",1L);
        user.setId(2L);

        List<SystemUser> listOfUsers = new ArrayList<>();
        listOfUsers.add(user);

        boolean success = true;
        try{
            userBusinessService.create(listOfUsers);
        } catch (Exception e){
            success = false;
        }
        Assert.assertTrue(success);
    }

    /**
     * Test method for {@link UserBusinessService#sendUpdatePasswordEmail(User)}
     */
    @Test
    public void testSendPasswordEmail() {
        User user = UserFactory.create("a","b","l",null,"e",1L);
        user.setId(2L);

        boolean success = true;
        try{
            userBusinessService.sendUpdatePasswordEmail(user);
        } catch (RemoteResourceException e){
            success = false;
        }
        Assert.assertTrue(success);
    }

    /**
     * Test method for {@link UserBusinessService#updateEmailAndExecuteActionEmailVerify(User, boolean)}
     */
    @Test
    public void testUpdateEmailAndExecuteActionEmailVerify() {
        User user = new UserEntity();
        user.setId(1L);
        user.setLogon("logon");
        user.setUserEmail("email@email.com");
        boolean success = true;
        try {
            userBusinessService.updateEmailAndExecuteActionEmailVerify(user, true);
        } catch (Exception e) {
            success = false;
        }
        Assert.assertTrue(success);
    }

    /**
     * Test method for {@link UserBusinessService#updateEmailAndExecuteActionEmailVerify(User, boolean)}
     */
    @Test(expected = Test.None.class)
    public void testUpdateUserEmailAndExecuteActionEmailVerifyException() throws Exception {
        User user = new UserEntity();
        userBusinessService.updateEmailAndExecuteActionEmailVerify(user, true);
    }


    /**
     * Test method for {@link UserBusinessService#refreshToken(String)}
     */
    @Test
    public void testRefreshToken() {
        boolean success = true;
        try{
            userBusinessService.refreshToken("test");
        } catch (RemoteResourceException e){
            success = false;
        }
        Assert.assertTrue(success);
    }

    /**
     * Prepares test method of save mock objects
     * @param first name to be set
     * @param last name to be set
     * @throws UserNotFoundException if user not found
     * @throws RemoteResourceException if any empty values of logon, email, first and last name
     */
    private void prepareTestForCreateUserFirstNameLastNameIsNull(String first, String last) throws UserNotFoundException, RemoteResourceException {
        User user3 = new User();
        user3.setLogon("logon");
        user3.setUserEmail("email@email.com");
        user3.setFirstname(first);
        user3.setLastname(last);
        user3.setId(2L);
        when(userServiceAccess.get((Long) any())).thenReturn(user3);
        when(keycloakService.getSubFromEmail(anyString())).thenReturn( Optional.empty() );
        boolean success3 = true;
        try{
            userBusinessService.create(user3, true);
        } catch (RemoteResourceException | UniquenessConstraintException e){
            success3 = false;
        }
        Assert.assertTrue(success3);
    }

    /**
     * Test for method {@link UserBusinessService#changePassword(String, UserPasswordChanging)}
     * Scenario: Logon not informed
     * @throws UserChangeCredentialException thrown in case of any issue regarding changing password business rules
     * @throws RemoteResourceException thrown in case of any issue regarding communication with KeyCloak service
     */
    @Test
    public void testChangePassword() throws UserChangeCredentialException, RemoteResourceException {
        UserPasswordChanging u = new UserPasswordChanging();
        u.setLogin("test.test");
        u.setOldPassword("test");
        u.setNewPassword("test1");
        u.setConfirmNewPassword("test1");
        String subject = "12345";
        doNothing().when(keycloakService).validateChangeCredentials(u.getLogin(), subject,
                u.getOldPassword(), u.getNewPassword());
        try {
            userBusinessService.changePassword(subject, u);
        } catch (UserChangeCredentialException | RemoteResourceException e) {
            fail();
        }
    }

    /**
     * Test for method {@link UserBusinessService#changePassword(String, UserPasswordChanging)}
     * Scenario: Logon not informed
     */
    @Test
    public void testChangePasswordLoginEmpty() {
        UserPasswordChanging u = new UserPasswordChanging();
        UserChangeCredentialException e = assertThrows(UserChangeCredentialException.class,
                ()->userBusinessService.changePassword("12345", u));
        assertEquals(INVALID_VALUE_FOR_PARAMETER.toString(LOGON.getLabel()), e.getMessage());
    }

    /**
     * Test for method {@link UserBusinessService#changePassword(String, UserPasswordChanging)}
     * Scenario: Old password not informed
     */
    @Test
    public void testChangePasswordOldPasswordEmpty() {
        UserPasswordChanging u = new UserPasswordChanging();
        u.setLogin("test.test");
        UserChangeCredentialException e = assertThrows(UserChangeCredentialException.class,
                ()->userBusinessService.changePassword("12345", u));
        assertEquals(INVALID_VALUE_FOR_PARAMETER.toString(OLD_PASSWORD.getLabel()), e.getMessage());
    }

    /**
     * Test for method {@link UserBusinessService#changePassword(String, UserPasswordChanging)}
     * Scenario: New password not informed
     */
    @Test
    public void testChangePasswordNewPasswordEmpty() {
        UserPasswordChanging u = new UserPasswordChanging();
        u.setLogin("test.test");
        u.setOldPassword("test");
        UserChangeCredentialException e = assertThrows(UserChangeCredentialException.class,
                ()->userBusinessService.changePassword("12345", u));
        assertEquals(INVALID_VALUE_FOR_PARAMETER.toString(NEW_PASSWORD.getLabel()), e.getMessage());
    }

    /**
     * Test for method {@link UserBusinessService#changePassword(String, UserPasswordChanging)}
     * Scenario: Confirmation password not informed
     */
    @Test
    public void testChangePasswordConfirmNewPasswordEmpty() {
        UserPasswordChanging u = new UserPasswordChanging();
        u.setLogin("test.test");
        u.setOldPassword("test");
        u.setNewPassword("test");
        UserChangeCredentialException e = assertThrows(UserChangeCredentialException.class,
                ()->userBusinessService.changePassword("12345", u));
        assertEquals(INVALID_VALUE_FOR_PARAMETER.toString(CONFIRM_NEW_PASSWORD.getLabel()), e.getMessage());
    }

    /**
     * Test for method {@link UserBusinessService#changePassword(String, UserPasswordChanging)}
     * Scenario: New password and confirmation password do not matching
     * @throws UserChangeCredentialException thrown in case of any issue regarding changing password business rules
     * @throws RemoteResourceException thrown in case of any issue regarding communication with KeyCloak service
     */
    @Test
    public void testChangePasswordWithInconsistentValues() throws UserChangeCredentialException, RemoteResourceException {
        UserPasswordChanging u = new UserPasswordChanging();
        u.setNewPassword("test");
        u.setConfirmNewPassword("test1");
        UserChangeCredentialException e = assertThrows(UserChangeCredentialException.class,
                ()->userBusinessService.changePassword("12345", u));
        assertEquals(INVALID_VALUE_FOR_PARAMETER.toString(CONFIRM_NEW_PASSWORD.getLabel()), e.getMessage());
    }
}