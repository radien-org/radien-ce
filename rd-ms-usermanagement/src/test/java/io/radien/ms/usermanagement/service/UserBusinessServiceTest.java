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

import io.radien.api.entity.Page;
import io.radien.api.model.user.SystemUser;
import io.radien.api.model.user.SystemUserSearchFilter;
import io.radien.api.service.batch.BatchSummary;
import io.radien.api.service.user.UserServiceAccess;
import io.radien.exception.UniquenessConstraintException;
import io.radien.exception.UserNotFoundException;
import io.radien.ms.usermanagement.client.entities.User;
import io.radien.ms.usermanagement.client.entities.UserSearchFilter;
import io.radien.ms.usermanagement.client.exceptions.RemoteResourceException;
import io.radien.ms.usermanagement.client.services.UserFactory;
import io.radien.ms.usermanagement.entities.UserEntity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
            userBusinessService.delete(1l);
        } catch (Exception e){
            success = true;
        }
        Assert.assertFalse( success );
    }

    /**
     * Test method for {@link UserBusinessService#save(User, boolean)}
     * @throws UniquenessConstraintException if unique user founds
     * @throws UserNotFoundException if user not found
     * @throws RemoteResourceException if any empty values of logon, email, first and last name
     */
    @Test
    public void testSaveException() throws UniquenessConstraintException, UserNotFoundException, RemoteResourceException {
        User u = UserFactory.create("a","b","l","s","e",1L);
        doThrow(new UserNotFoundException("")).when(userServiceAccess).save(u);
        boolean success = false;
        try{
            userBusinessService.save(u,true);
        } catch (UserNotFoundException e){
            success = true;
        }
        Assert.assertTrue( success );
    }

    /**
     * Test method for {@link UserBusinessService#save(User, boolean)}
     * @throws UniquenessConstraintException if unique user founds
     * @throws UserNotFoundException if user not found
     * @throws RemoteResourceException if any empty values of logon, email, first and last name
     */
    @Test
    public void testSaveSkipKeycloak() throws UniquenessConstraintException, UserNotFoundException, RemoteResourceException {
        User user = UserFactory.create("a","b","l",null,"e",1L);
        user.setId(2L);
        User user2 = UserFactory.create("a","b","l",null,"e",1L);
        when(userServiceAccess.get((Long) any())).thenReturn(user2);

        boolean success = true;
        try{
            userBusinessService.save(user2, false);
        } catch (RemoteResourceException e){
            success = false;
        }
        Assert.assertTrue( success );
    }

    /**
     * Test method for {@link UserBusinessService#save(User, boolean)}
     * @throws UniquenessConstraintException if unique user founds
     * @throws UserNotFoundException if user not found
     * @throws RemoteResourceException if any empty values of logon, email, first and last name
     */
    @Test
    public void testSaveSkipKeycloakWithCreationRemoteException() throws UniquenessConstraintException, UserNotFoundException, RemoteResourceException {
        User user2 = UserFactory.create("a","b","l",null,"e",1L);
        when(userServiceAccess.get((Long) any())).thenReturn(user2);
        doThrow(new RemoteResourceException()).when(keycloakService).createUser(any());
        boolean success = true;
        try{
            userBusinessService.save(user2, false);
        } catch (RemoteResourceException e){
            success = false;
        }
        Assert.assertFalse( success );
    }

    /**
     * Test method for {@link UserBusinessService#save(User, boolean)}
     * @throws UniquenessConstraintException if unique user founds
     * @throws UserNotFoundException if user not found
     * @throws RemoteResourceException if any empty values of logon, email, first and last name
     */
    @Test
    public void testSaveSkipKeycloakWithCreationRemoteExceptionSub() throws UniquenessConstraintException, UserNotFoundException, RemoteResourceException {
        User user2 = UserFactory.create("a","b","l","sub","e",1L);
        when(userServiceAccess.get((Long) any())).thenReturn(user2);
        doThrow(new RemoteResourceException()).when(keycloakService).getSubFromEmail(anyString());
        boolean success = true;
        try{
            userBusinessService.save(user2, false);
        } catch (RemoteResourceException e){
            success = false;
        }
        Assert.assertTrue( success );
    }

    /**
     * Test method for {@link UserBusinessService#save(User, boolean)}
     * @throws UniquenessConstraintException if unique user founds
     * @throws UserNotFoundException if user not found
     * @throws RemoteResourceException if any empty values of logon, email, first and last name
     */
    @Test
    public void testSaveSkipKeycloakWithCreationSub() throws UniquenessConstraintException, UserNotFoundException, RemoteResourceException {
        User user2 = UserFactory.create("a","b","l","sub1234ABCD","e",1L);
        when(userServiceAccess.get((Long) any())).thenReturn(user2);
        when(keycloakService.getSubFromEmail(anyString())).thenReturn( java.util.Optional.of( "sub1234ABCD" ) );
        boolean success = true;
        try{
            userBusinessService.save(user2, false);
        } catch (RemoteResourceException e){
            success = false;
        }
        Assert.assertTrue(success);
    }

    /**
     * Test method for {@link UserBusinessService#save(User, boolean)}
     * @throws UniquenessConstraintException if unique user founds
     * @throws UserNotFoundException if user not found
     * @throws RemoteResourceException if any empty values of logon, email, first and last name
     */
    @Test
    public void testSaveUserFirstNameIsNull() throws UniquenessConstraintException, UserNotFoundException, RemoteResourceException {
        prepareTestForSaveUserFirstNameLastNameIsNull(null, "last");
    }

    /**
     * Test for method {@link UserBusinessService#save(User, boolean)}
     * @throws UserNotFoundException if user not found
     */
    @Test
    public void testSaveUserLastNameIsNull() throws UserNotFoundException, RemoteResourceException {
        prepareTestForSaveUserFirstNameLastNameIsNull("first", null);
    }


    /**
     * Test method for {@link UserBusinessService#save(User, boolean)}
     * @throws UserNotFoundException if user not found
     */
    @Test
    public void testSaveUserCreationIsTrue() throws UserNotFoundException {
        User user4 = new User();
        user4.setLogon("logon");
        user4.setUserEmail("email@email.com");
        user4.setFirstname("first");
        user4.setLastname("last");
        when(userServiceAccess.get((Long) any())).thenReturn(user4);
        boolean success4 = true;
        try{
            userBusinessService.save(user4, true);
        } catch (RemoteResourceException | UniquenessConstraintException e){
            success4 = false;
        }
        Assert.assertTrue(success4);
    }

    /**
     * Test method for {@link UserBusinessService#save(User, boolean)}
     * @throws UniquenessConstraintException if unique user founds
     * @throws UserNotFoundException if user not found
     */
    @Test
    public void testSaveEmptyLogon() throws UniquenessConstraintException, UserNotFoundException {
        User user = UserFactory.create("a","b","",null,"e",1L);
        user.setId(2L);
        when(userServiceAccess.get((Long) any())).thenReturn(user);

        boolean success = true;
        try{
            userBusinessService.save(user, false);
        } catch (RemoteResourceException e){
            success = false;
        }
        Assert.assertFalse(success);
    }

    /**
     * Test method for {@link UserBusinessService#save(User, boolean)}
     * @throws UniquenessConstraintException if unique user founds
     * @throws UserNotFoundException if user not found
     */
    @Test
    public void testSaveEmptyFirstName() throws UniquenessConstraintException, UserNotFoundException {
        User user = UserFactory.create("","b","logon",null,"c",1L);
        user.setId(2L);
        when(userServiceAccess.get((Long) any())).thenReturn(user);

        boolean success = true;
        try{
            userBusinessService.save(user, false);
        } catch (RemoteResourceException e){
            success = false;
        }
        Assert.assertFalse(success);
    }

    /**
     * Test method for {@link UserBusinessService#save(User, boolean)}
     * @throws UniquenessConstraintException if unique user founds
     * @throws UserNotFoundException if user not found
     */
    @Test
    public void testSaveEmptyLastName() throws UniquenessConstraintException, UserNotFoundException {
        User user = UserFactory.create("a","","logon",null,"c",1L);
        user.setId(2L);
        when(userServiceAccess.get((Long) any())).thenReturn(user);

        boolean success = true;
        try{
            userBusinessService.save(user, false);
        } catch (RemoteResourceException e){
            success = false;
        }
        Assert.assertFalse(success);
    }

    /**
     * Test method for {@link UserBusinessService#save(User, boolean)}
     * @throws UniquenessConstraintException if unique user founds
     * @throws UserNotFoundException if user not found
     */
    @Test
    public void testSaveEmptyEmail() throws UniquenessConstraintException, UserNotFoundException {
        User user = UserFactory.create("a","b","logon",null,"",1L);
        user.setId(2L);
        when(userServiceAccess.get((Long) any())).thenReturn(user);

        boolean success = true;
        try{
            userBusinessService.save(user, false);
        } catch (RemoteResourceException e){
            success = false;
        }
        Assert.assertFalse( success );
    }

    /**
     * Test method for {@link UserBusinessService#save(User, boolean)}
     * @throws UniquenessConstraintException if unique user founds
     * @throws UserNotFoundException if user not found
     * @throws RemoteResourceException if any empty values of logon, email, first and last name
     */
    @Test
    public void testSaveSkipKeycloakRemoteException() throws UniquenessConstraintException, UserNotFoundException, RemoteResourceException {
        User user = UserFactory.create("a","b","l",null,"e",1L);
        user.setId(2L);
        User user2 = UserFactory.create("a","b","l",null,"e",1L);
        when(userServiceAccess.get((Long) any())).thenReturn(user2);
        doThrow(new RemoteResourceException()).when(keycloakService).updateUser(any());

        boolean success = true;
        try{
            userBusinessService.save(user, false);
        } catch (RemoteResourceException e){
            success = false;
        }
        Assert.assertFalse(success);
    }

    /**
     * Test method for {@link UserBusinessService#save(User, boolean)}
     * @throws UniquenessConstraintException if unique user founds
     * @throws UserNotFoundException if user not found
     */
    @Test
    public void testSaveEmptyUsername() throws UniquenessConstraintException, UserNotFoundException {
        User u = UserFactory.create("a","b","","s","e",1L);
        boolean success = false;
        try{
            userBusinessService.save(u,false);
        } catch (RemoteResourceException e){
            success = true;
        }
        Assert.assertTrue(success);
    }

    /**
     * Test method for {@link UserBusinessService#save(User, boolean)}
     * @throws UniquenessConstraintException if unique user founds
     * @throws UserNotFoundException if user not found
     */
    @Test
    public void testSaveCreationFalse() throws UniquenessConstraintException, UserNotFoundException {
        User user = UserFactory.create("a","b","l",null,"e",1L);
        user.setId(2L);
        User user2 = UserFactory.create("a","b","l",null,"e",1L);
        when(userServiceAccess.get((Long) any())).thenReturn(user2);

        boolean success = true;
        try{
            userBusinessService.save(user, false);
        } catch (RemoteResourceException e){
            success = false;
        }
        Assert.assertTrue(success);
    }

    /**
     * Test method for {@link UserBusinessService#create(List)}
     */
    @Test
    public void testSaveBatch() {
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
     * Test method for {@link UserBusinessService#updateEmailAndExecuteActionEmailVerify(long, String, User, boolean)}
     * @throws Exception if any exception
     */
    @Test
    public void testUpdateEmailAndExecuteActionEmailVerify() throws Exception {
        User user = new UserEntity();
        user.setId(1L);
        user.setLogon("logon");
        user.setUserEmail("email@email.com");
        boolean success = true;
        try {
            userBusinessService.updateEmailAndExecuteActionEmailVerify(1L, "", user, true);
        } catch (Exception e) {
            success = false;
        }
        Assert.assertTrue(success);
    }

    /**
     * Test method for {@link UserBusinessService#updateEmailAndExecuteActionEmailVerify(long, String, User, boolean)}
     */
    @Test(expected = Test.None.class)
    public void testUpdateUserEmailAndExecuteActionEmailVerifyException() throws Exception {
        User user = new UserEntity();
        userBusinessService.updateEmailAndExecuteActionEmailVerify(1L, "", user, true);
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
    private void prepareTestForSaveUserFirstNameLastNameIsNull(String first, String last) throws UserNotFoundException, RemoteResourceException {
        User user3 = new User();
        user3.setLogon("logon");
        user3.setUserEmail("email@email.com");
        user3.setFirstname(first);
        user3.setLastname(last);
        user3.setId(2L);
        when(userServiceAccess.get((Long) any())).thenReturn(user3);
        when(keycloakService.getSubFromEmail(anyString())).thenReturn( java.util.Optional.of( "sub1234ABCD" ) );
        boolean success3 = true;
        try{
            userBusinessService.save(user3, true);
        } catch (RemoteResourceException | UniquenessConstraintException e){
            success3 = false;
        }
        Assert.assertTrue(success3);
    }

}