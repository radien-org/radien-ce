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

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doReturn;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Response;

import io.radien.api.security.TokensPlaceHolder;
import io.radien.api.service.role.SystemRolesEnum;
import io.radien.ms.authz.client.LinkedAuthorizationClient;
import io.radien.ms.openid.entities.Principal;
import io.radien.ms.usermanagement.client.exceptions.RemoteResourceException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import io.radien.api.service.batch.BatchSummary;
import io.radien.api.service.batch.DataIssue;
import io.radien.exception.UniquenessConstraintException;
import io.radien.exception.UserNotFoundException;
import io.radien.ms.usermanagement.client.entities.User;
import io.radien.ms.usermanagement.client.exceptions.ErrorCodeMessage;

/**
 * User Resource rest requests and responses
 *
 * @author Nuno Santana
 * @author Bruno Gama
 */
public class UserResourceTest {

    @InjectMocks
    UserResource userResource;

    @Mock
    UserBusinessService userBusinessService;

    @Mock
    HttpServletRequest servletRequest;

    @Mock
    LinkedAuthorizationClient linkedAuthorizationClient;

    @Mock
    TokensPlaceHolder tokensPlaceHolder;

    /**
     * Method before test preparation
     */
    @Before
    public void before(){
       MockitoAnnotations.initMocks(this);
    }

    /**
     * Test of success to get the user on base of the subject
     */
    @Test
    public void testGetUserIdBySub() {
        when(userBusinessService.getUserId("login1")).thenReturn(1L);
        Response response = userResource.getUserIdBySub("login1");
        assertEquals(200, response.getStatus());
        assertEquals(1L, (long) response.readEntity(Long.class));
    }

    /**
     * Test of user not found where we get the user on base of his subject
     */
    @Test
    public void testGetUserIdBySubNotFoundCase() {
        when(userBusinessService.getUserId("login1")).thenReturn(null);
        Response response = userResource.getUserIdBySub("login1");
        assertEquals(404, response.getStatus());
    }

    /**
     * Test of generic exception where we get the user on base of his subject
     */
    @Test
    public void testGetUserIdBySubExceptionCase() {
        when(userBusinessService.getUserId("login1")).
                thenThrow(new RuntimeException("Error retrieving id"));
        Response response = userResource.getUserIdBySub("login1");
        assertEquals(500, response.getStatus());
    }

    /**
     * Test the Get All request which will return a success message code 200.
     */
    @Test
    public void testGetAll() {
        preProcessAuthentication();

        Response expectedAuthGranted = Response.ok().entity(Boolean.TRUE).build();
        doReturn("token-yyz").when(tokensPlaceHolder).getAccessToken();
        List<String> roleList = new ArrayList<>();
        roleList.add(SystemRolesEnum.SYSTEM_ADMINISTRATOR.getRoleName());
        roleList.add(SystemRolesEnum.USER_ADMINISTRATOR.getRoleName());

        doReturn(expectedAuthGranted).when(linkedAuthorizationClient).checkPermissions(
                1001L, roleList, null);

        Response response = userResource.getAll(null,1,10,null,true);
        assertEquals(200,response.getStatus());
    }

    /**
     * Test the Get All for cases in which the current logged user do not have Authorization.
     */
    @Test
    public void testGetAllWithNoAuthorization() {
        preProcessAuthentication();

        Response expectedAuthGranted = Response.ok().entity(Boolean.FALSE).build();
        doReturn("token-yyz").when(tokensPlaceHolder).getAccessToken();
        List<String> roleList = new ArrayList<>();
        roleList.add(SystemRolesEnum.SYSTEM_ADMINISTRATOR.getRoleName());
        roleList.add(SystemRolesEnum.USER_ADMINISTRATOR.getRoleName());

        doReturn(expectedAuthGranted).when(linkedAuthorizationClient).checkPermissions(
                1001L, roleList, null);

        Response response = userResource.getAll(null,1,10,null,true);
        assertEquals(403,response.getStatus());
    }

    /**
     * Test the Get All request Exception which will return a generic error message code 500.
     */
    @Test
    public void testGetAllGenericException() {

        HttpSession session = Mockito.mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);
        when(userBusinessService.getAll(null,1,10,null,true))
                .thenThrow(new RuntimeException());
        Response response = userResource.getAll(null,1,10,null,true);
        assertEquals(500,response.getStatus());
    }

    /**
     * Test that will test the error message 404 User Not Found
     */
    @Test
    public void testGetById404() throws UserNotFoundException {
        preProcessAuthentication();

        Response expectedAuthGranted = Response.ok().entity(Boolean.TRUE).build();
        List<String> roleList = new ArrayList<>();
        roleList.add(SystemRolesEnum.SYSTEM_ADMINISTRATOR.getRoleName());
        roleList.add(SystemRolesEnum.USER_ADMINISTRATOR.getRoleName());

        doReturn(expectedAuthGranted).when(linkedAuthorizationClient).checkPermissions(
                1001L, roleList, null);


        when(userBusinessService.get(1L)).thenThrow(new UserNotFoundException("1"));
        Response response = userResource.getById(1L);
        assertEquals(404,response.getStatus());
    }

    /**
     * Test that will test the error message 404 User Not Found
     */
    @Test
    public void testGetByIdUserWithoutRole() throws UserNotFoundException {
        preProcessAuthentication();

        Response expectedAuthGranted = Response.ok().entity(Boolean.FALSE).build();
        List<String> roleList = new ArrayList<>();
        roleList.add(SystemRolesEnum.SYSTEM_ADMINISTRATOR.getRoleName());
        roleList.add(SystemRolesEnum.USER_ADMINISTRATOR.getRoleName());

        doReturn(expectedAuthGranted).when(linkedAuthorizationClient).checkPermissions(
                1001L, roleList, null);


        when(userBusinessService.get(1L)).thenThrow(new UserNotFoundException("1"));
        Response response = userResource.getById(1L);
        assertEquals(403,response.getStatus());
    }

    /**
     * Get by ID with success should return a 200 code message
     * @throws UserNotFoundException in case of user not found
     */
    @Test
    public void testGetById() throws UserNotFoundException {
        preProcessAuthentication();

        Response expectedAuthGranted = Response.ok().entity(Boolean.TRUE).build();
        List<String> roleList = new ArrayList<>();
        roleList.add(SystemRolesEnum.SYSTEM_ADMINISTRATOR.getRoleName());
        roleList.add(SystemRolesEnum.USER_ADMINISTRATOR.getRoleName());

        doReturn(expectedAuthGranted).when(linkedAuthorizationClient).checkPermissions(
                1001L, roleList, null);


        when(userBusinessService.get(1L)).thenReturn(new User());
        Response response = userResource.getById(1L);
        assertEquals(200,response.getStatus());
    }

    /**
     * Test Get by ID exception which will return a 500 error code message
     * @throws UserNotFoundException in case of user not found
     */
    @Test
    public void testGetByIdGenericException() throws UserNotFoundException {
        when(userBusinessService.get(1L)).thenThrow(new RuntimeException());
        Response response = userResource.getById(1L);
        assertEquals(500,response.getStatus());
    }

    /**
     * Test Get users by should return success with a 200 code
     */
    @Test
    public void testGetUsersBy() {
        Response response = userResource.getUsers("subj","email@email.pt","logon",true,true);
        assertEquals(200,response.getStatus());
    }

    /**
     * Test Get users by should return error with a 500 error code message
     */
    @Test
    public void testGetUsersByException() {
        doThrow(new RuntimeException()).when(userBusinessService).getUsers(any());
        Response response = userResource.getUsers("subj","email@email.pt","logon",true,true);
        assertEquals(500,response.getStatus());
    }

    /**
     * Deletion of the record with success, should return a 200 code message
     */
    @Test
    public void testDelete() {
        preProcessAuthentication();

        Response expectedAuthGranted = Response.ok().entity(Boolean.TRUE).build();
        List<String> roleList = new ArrayList<>();
        roleList.add(SystemRolesEnum.SYSTEM_ADMINISTRATOR.getRoleName());
        roleList.add(SystemRolesEnum.USER_ADMINISTRATOR.getRoleName());

        doReturn(expectedAuthGranted).when(linkedAuthorizationClient).checkPermissions(
                1001L, roleList, null);

        Response response = userResource.delete(1l);
        assertEquals(200,response.getStatus());
    }

    /**
     * Deletion of the record with success, should return a 200 code message
     */
    @Test
    public void testDeleteUserWithoutRole() {
        preProcessAuthentication();

        Response expectedAuthGranted = Response.ok().entity(Boolean.FALSE).build();
        List<String> roleList = new ArrayList<>();
        roleList.add(SystemRolesEnum.SYSTEM_ADMINISTRATOR.getRoleName());
        roleList.add(SystemRolesEnum.USER_ADMINISTRATOR.getRoleName());

        doReturn(expectedAuthGranted).when(linkedAuthorizationClient).checkPermissions(
                1001L, roleList, null);

        Response response = userResource.delete(1l);
        assertEquals(403,response.getStatus());
    }

    /**
     * Deletion of the record with error, should return a generic 500 error code message
     */
    @Test
    public void testDeleteGenericError() throws UserNotFoundException, RemoteResourceException {
        doThrow(new RemoteResourceException()).when(userBusinessService).delete(1l);
        Response response = userResource.delete(1l);
        assertEquals(500,response.getStatus());
    }

    /**
     * Creation with success of a record. Should return a 200 code message
     */
    @Test
    public void testSave() {
        Principal principal = new Principal();
        principal.setSub("aaa-bbb-ccc-ddd");
        HttpSession session = Mockito.mock(HttpSession.class);

        when(servletRequest.getSession()).thenReturn(session);
        when(servletRequest.getSession(false)).thenReturn(session);
        when(session.getAttribute("USER")).thenReturn(principal);
        doReturn(1001L).when(this.userBusinessService). getUserId(principal.getSub());

        Response expectedAuthGranted = Response.ok().entity(Boolean.TRUE).build();
        doReturn("token-yyz").when(tokensPlaceHolder).getAccessToken();
        List<String> roleList = new ArrayList<>();
        roleList.add(SystemRolesEnum.SYSTEM_ADMINISTRATOR.getRoleName());
        roleList.add(SystemRolesEnum.USER_ADMINISTRATOR.getRoleName());

        doReturn(expectedAuthGranted).when(linkedAuthorizationClient).checkPermissions(
                1001L, roleList, null);

        Response response = userResource.save(new User());
        assertEquals(200,response.getStatus());
    }

    /**
     * Tests the save method by a requester that does not have the correct role or authorization
     */
    @Test
    public void testSaveWithAuthorizationDenied() {
        Principal loggedUser = new Principal();
        loggedUser.setSub("aaa-bbb-ccc-ddd");
        HttpSession session = Mockito.mock(HttpSession.class);

        User userToBeCreated = new User();
        userToBeCreated.setSub("xxx-yyy-zzz-www");

        when(servletRequest.getSession()).thenReturn(session);
        when(servletRequest.getSession(false)).thenReturn(session);
        when(session.getAttribute("USER")).thenReturn(loggedUser);
        when(this.userBusinessService.getUserId(loggedUser.getSub())).thenReturn(1001L);

        Response notAuthorizedResponse = Response.ok().entity(Boolean.FALSE).build();
        doReturn("token-yyz").when(tokensPlaceHolder).getAccessToken();
        List<String> roleList = new ArrayList<>();
        roleList.add(SystemRolesEnum.SYSTEM_ADMINISTRATOR.getRoleName());
        roleList.add(SystemRolesEnum.USER_ADMINISTRATOR.getRoleName());

        doReturn(notAuthorizedResponse).when(linkedAuthorizationClient).checkPermissions(
                1001L, roleList, null);

        Response response = userResource.save(new User());
        assertEquals(403,response.getStatus());
    }

    /**
     * User is accessing radien for the very fist time.
     * His profile were created on KeyCloak and now he is going to
     * register himself on the radien database
     */
    @Test
    public void testSaveSelfRegisterScenario() {
        Principal principal = new Principal();
        principal.setSub("aaa-bbb-ccc-ddd");
        HttpSession session = Mockito.mock(HttpSession.class);

        User userToBeRegistered = new User();
        userToBeRegistered.setSub(principal.getSub());

        when(servletRequest.getSession()).thenReturn(session);
        when(servletRequest.getSession(false)).thenReturn(session);
        when(session.getAttribute("USER")).thenReturn(principal);

        Response response = userResource.save(userToBeRegistered);
        assertEquals(200,response.getStatus());
    }

    /**
     * Tests Save method to return Remote Resource Exception
     * @throws UserNotFoundException in case of 404 error message
     * @throws RemoteResourceException in case of 500 error message
     * @throws UniquenessConstraintException in case of multiple existent users
     */
    @Test
    public void testGetResponseFromException() throws UserNotFoundException, RemoteResourceException, UniquenessConstraintException {
        Principal principal = new Principal();
        principal.setSub("aaa-bbb-ccc-ddd");
        HttpSession session = Mockito.mock(HttpSession.class);

        when(servletRequest.getSession()).thenReturn(session);
        when(servletRequest.getSession(false)).thenReturn(session);
        when(session.getAttribute("USER")).thenReturn(principal);
        doReturn(1001L).when(this.userBusinessService). getUserId(principal.getSub());

        Response expectedAuthGranted = Response.ok().entity(Boolean.TRUE).build();
        doReturn("token-yyz").when(tokensPlaceHolder).getAccessToken();
        doReturn(expectedAuthGranted).when(linkedAuthorizationClient).isRoleExistentForUser(
                1001L, SystemRolesEnum.SYSTEM_ADMINISTRATOR.getRoleName(), null);
        doReturn(expectedAuthGranted).when(linkedAuthorizationClient).isRoleExistentForUser(
                1001L, SystemRolesEnum.USER_ADMINISTRATOR.getRoleName(), null);

        doThrow(new RemoteResourceException()).when(userBusinessService).save(any(), anyBoolean());

        User user = new User();
        Response response = userResource.save(user);
        assertEquals(500, response.getStatus());
    }

    /**
     * Creation with error of a record. Should return a 400 code message Invalid Requested Exception
     * @throws UniquenessConstraintException in case of request could not be performed by any specific and justified in the
     * message reason
     * @throws UserNotFoundException in case of user not found
     */
    @Test
    public void testCreateInvalid() throws UniquenessConstraintException, UserNotFoundException, RemoteResourceException {
        Principal principal = new Principal();
        principal.setSub("aaa-bbb-ccc-ddd");
        HttpSession session = Mockito.mock(HttpSession.class);

        when(servletRequest.getSession()).thenReturn(session);
        when(servletRequest.getSession(false)).thenReturn(session);
        when(session.getAttribute("USER")).thenReturn(principal);
        when(this.userBusinessService.getUserId(principal.getSub())).thenReturn(1001L);

        Response notAuthorizedResponse = Response.ok().entity(Boolean.TRUE).build();
        List<String> roleList = new ArrayList<>();
        roleList.add(SystemRolesEnum.SYSTEM_ADMINISTRATOR.getRoleName());
        roleList.add(SystemRolesEnum.USER_ADMINISTRATOR.getRoleName());

        doReturn(notAuthorizedResponse).when(linkedAuthorizationClient).checkPermissions(
                1001L, roleList, null);

        doThrow(new UniquenessConstraintException()).when(userBusinessService).save(any(), anyBoolean());
        Response response = userResource.save(new User());
        assertEquals(400,response.getStatus());
    }

    /**
     * Creation of a record with error. Should return a generic error message 500
     * @throws UniquenessConstraintException in case of request could not be performed by any specific and justified in the
     * message reason
     * @throws UserNotFoundException in case of user not found
     */
    @Test
    public void testCreateRemoteResourceError() throws UniquenessConstraintException, UserNotFoundException, RemoteResourceException {
        User u = new User();
        doThrow(new RemoteResourceException()).when(userBusinessService).save(any(),anyBoolean());
        Response response = userResource.save(u);
        assertEquals(500,response.getStatus());
    }

    /**
     * Creation of a record with error. Should return a generic error message 500
     * @throws UniquenessConstraintException in case of request could not be performed by any specific and justified in the
     * message reason
     * @throws UserNotFoundException in case of user not found
     */
    @Test
    public void testCreateGenericError() throws UniquenessConstraintException, UserNotFoundException, RemoteResourceException {
        User u = new User();
        doThrow(new RuntimeException()).when(userBusinessService).save(any(),anyBoolean());
        Response response = userResource.save(u);
        assertEquals(500,response.getStatus());
    }

    /**
     * Test batch insertion with success
     */
    @Test
    public void testBatchAllElementsInserted() {
        List<User> users = new ArrayList<>();
        for (int i=1; i<=4; i++) {
            User user = new User();
            user.setFirstname("user");
            user.setLastname(String.valueOf(i));
            user.setLogon(String.format("user.%d", i));
            user.setSub("sub");
            user.setUserEmail(String.format("user.%d@emmail.pt", i));
            users.add(user);
        }
        when(userBusinessService.create(anyList())).thenReturn(new BatchSummary(users.size()));
        Response response = userResource.create(users);
        assertEquals(200, response.getStatus());
        assertNotNull(response.getEntity());
        assertEquals(response.getEntity().getClass(), BatchSummary.class);
        BatchSummary summary = (BatchSummary) response.getEntity();
        assertEquals(summary.getTotal(), users.size());
        assertEquals(summary.getInternalStatus(), BatchSummary.ProcessingStatus.SUCCESS);
    }

    /**
     * Tests batch insertion but without success
     */
    @Test
    public void testBatchSomeElementsNotInserted() {
        List<User> users = new ArrayList<>();
        for (int i=1; i<=10; i++) {
            User user = new User();
            user.setFirstname("user");
            user.setLastname(String.valueOf(i));
            user.setLogon(String.format("user.%d", i));
            user.setSub("sub");
            user.setUserEmail(String.format("user.%d@emmail.pt", i));
            users.add(user);
        }
        List<DataIssue> issuedItems = new ArrayList<>();
        issuedItems.add(new DataIssue(2,
                ErrorCodeMessage.DUPLICATED_FIELD.toString("email or logon")));
        issuedItems.add(new DataIssue(3,
                ErrorCodeMessage.DUPLICATED_FIELD.toString("email or logon")));
        issuedItems.add(new DataIssue(7,
                ErrorCodeMessage.DUPLICATED_FIELD.toString("email or logon")));
        issuedItems.add(new DataIssue(8,
                ErrorCodeMessage.DUPLICATED_FIELD.toString("email or logon")));
        when(userBusinessService.create(anyList())).thenReturn(new BatchSummary(users.size(), issuedItems));
        Response response = userResource.create(users);
        assertEquals(200, response.getStatus());
        assertNotNull(response.getEntity());
        assertEquals(response.getEntity().getClass(), BatchSummary.class);
        BatchSummary summary = (BatchSummary) response.getEntity();
        assertEquals(summary.getTotalNonProcessed(), 4);
        assertEquals(summary.getTotalProcessed(), 6);
        assertEquals(summary.getInternalStatus(), BatchSummary.ProcessingStatus.PARTIAL_SUCCESS);
    }

    /**
     * Test batch insertion but no records are inserted
     */
    @Test
    public void testBatchNoneElementsInserted() {
        List<User> users = new ArrayList<>();
        for (int i=1; i<=4; i++) {
            users.add(new User());
        }
        List<DataIssue> issuedItems = new ArrayList<>();
        issuedItems.add(new DataIssue(1,
                ErrorCodeMessage.DUPLICATED_FIELD.toString("email or logon")));
        issuedItems.add(new DataIssue(2,
                ErrorCodeMessage.DUPLICATED_FIELD.toString("email or logon")));
        issuedItems.add(new DataIssue(3,
                ErrorCodeMessage.DUPLICATED_FIELD.toString("email or logon")));
        issuedItems.add(new DataIssue(4,
                ErrorCodeMessage.DUPLICATED_FIELD.toString("email or logon")));
        when(userBusinessService.create(anyList())).thenReturn(new BatchSummary(4, issuedItems));
        Response response = userResource.create(users);
        assertEquals(400, response.getStatus());
        assertNotNull(response.getEntity());
        assertEquals(response.getEntity().getClass(), BatchSummary.class);
        BatchSummary summary = (BatchSummary) response.getEntity();
        assertEquals(summary.getTotalNonProcessed(), 4);
        assertEquals(summary.getTotalProcessed(), 0);
        assertEquals(summary.getInternalStatus(), BatchSummary.ProcessingStatus.FAIL);
    }

    /**
     * Test Batch insertion generic exception
     */
    @Test
    public void testBatchInsertException() {
        List<User> users = new ArrayList<>();
        when(userBusinessService.create(anyList())).thenThrow(new RuntimeException());
        Response response = userResource.create(users);
        assertEquals(500, response.getStatus());
    }

    /**
     * Tests the set admin password method and should finish with success
     * @throws UserNotFoundException in case of searched user is not existent or not found
     */
    @Test
    public void testSetAdminResetPassword() throws UserNotFoundException {
        when(userBusinessService.get(1L)).thenReturn(new User());
        Response response = userResource.sendUpdatePasswordEmail(1L);
        assertEquals(200,response.getStatus());
    }

    /**
     * Tests the set admin password method and should finish with generic 500 code message error
     * @throws UserNotFoundException in case of searched user is not existent or not found
     */
    @Test
    public void testSetAdminResetPasswordGenericError() throws UserNotFoundException {
        doThrow(new RuntimeException()).when(userBusinessService).get(1L);
        Response response = userResource.sendUpdatePasswordEmail(1L);
        assertEquals(500,response.getStatus());
    }

    /**
     * Tests the set admin password method and should finish with generic 404 code message error
     * @throws UserNotFoundException in case of searched user is not existent or not found
     */
    @Test
    public void testSetAdminResetPassword404() throws UserNotFoundException {
        when(userBusinessService.get(1L)).thenThrow(new UserNotFoundException("1"));
        Response response = userResource.sendUpdatePasswordEmail(1L);
        assertEquals(404,response.getStatus());
    }

    /**
     * Tests the refresh token with success
     */
    @Test
    public void testRefreshToken() {
        Response response = userResource.refreshToken("test");
        assertEquals(200,response.getStatus());
    }

    /**
     * Tests the refresh token with generic exception 500 error code
     * @throws RemoteResourceException in case of any issue while generating the refresh token
     */
    @Test
    public void testRefreshTokenException() throws RemoteResourceException {
        doThrow(new RuntimeException()).when(userBusinessService).refreshToken(anyString());
        Response response = userResource.refreshToken("any");
        assertEquals(500,response.getStatus());
    }

    /**
     * Private method to mock the pre process method requested while validating the roles
     */
    private void preProcessAuthentication() {
        HttpSession session = Mockito.mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);

        Principal principal = new Principal();
        principal.setSub("aaa-bbb-ccc-ddd");

        when(servletRequest.getSession()).thenReturn(session);
        when(servletRequest.getSession(false)).thenReturn(session);
        when(session.getAttribute("USER")).thenReturn(principal);
        doReturn(1001L).when(this.userBusinessService).getUserId(principal.getSub());
    }
}