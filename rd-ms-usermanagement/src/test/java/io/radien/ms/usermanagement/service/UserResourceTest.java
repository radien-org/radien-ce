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

import io.radien.api.security.TokensPlaceHolder;
import io.radien.api.service.batch.BatchSummary;
import io.radien.api.service.batch.DataIssue;
import io.radien.api.service.role.SystemRolesEnum;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.SystemException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.exception.UserChangeCredentialException;
import io.radien.exception.UserNotFoundException;
import io.radien.ms.authz.client.PermissionClient;
import io.radien.ms.authz.client.TenantRoleClient;
import io.radien.ms.openid.entities.Principal;
import io.radien.ms.usermanagement.client.entities.User;
import io.radien.ms.usermanagement.client.entities.UserPasswordChanging;
import io.radien.ms.usermanagement.client.exceptions.RemoteResourceException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Response;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    TenantRoleClient tenantRoleClient;

    @Mock
    TokensPlaceHolder tokensPlaceHolder;

    @Mock
    PermissionClient permissionClient;

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
     * Test of user not found where we get the user on base of his subject
     */
    @Test(expected = SystemException.class)
    public void testGetCurrentUserIdBySubNotFoundCase() throws SystemException {
        when(userBusinessService.getUserId("login1")).thenReturn(null);
        userResource.getCurrentUserIdBySub("login1");
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

        doReturn(expectedAuthGranted).when(tenantRoleClient).checkPermissions(
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

        doReturn(expectedAuthGranted).when(tenantRoleClient).checkPermissions(
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

        doReturn(expectedAuthGranted).when(tenantRoleClient).checkPermissions(
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

        doReturn(expectedAuthGranted).when(tenantRoleClient).checkPermissions(
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

        doReturn(expectedAuthGranted).when(tenantRoleClient).checkPermissions(
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
        Response response = userResource.getUsers("subj","email@email.pt","logon",null,true,true);
        assertEquals(200,response.getStatus());
    }

    /**
     * Test Get users by should return error with a 500 error code message
     */
    @Test
    public void testGetUsersByException() {
        doThrow(new RuntimeException()).when(userBusinessService).getUsers(any());
        Response response = userResource.getUsers("subj","email@email.pt","logon",null,true,true);
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

        doReturn(expectedAuthGranted).when(tenantRoleClient).checkPermissions(
                1001L, roleList, null);

        Response response = userResource.delete(1L);
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

        doReturn(expectedAuthGranted).when(tenantRoleClient).checkPermissions(
                1001L, roleList, null);

        Response response = userResource.delete(1L);
        assertEquals(403,response.getStatus());
    }

    /**
     * Deletion of the record with error, should return a generic 500 error code message
     */
    @Test
    public void testDeleteGenericError() throws UserNotFoundException, RemoteResourceException {
        doThrow(new RemoteResourceException()).when(userBusinessService).delete(1L);
        Response response = userResource.delete(1L);
        assertEquals(500,response.getStatus());
    }

    /**
     * Creation with success of a record. Should return a 200 code message
     */
    @Test
    public void testCreate() {
        Principal principal = new Principal();
        principal.setSub("aaa-bbb-ccc-ddd");
        HttpSession session = Mockito.mock(HttpSession.class);

        when(servletRequest.getSession()).thenReturn(session);
        when(servletRequest.getSession(false)).thenReturn(session);
        when(session.getAttribute("USER")).thenReturn(principal);
        doReturn(1001L).when(this.userBusinessService). getUserId(principal.getSub());


        doReturn("token-yyz").when(tokensPlaceHolder).getAccessToken();

        Response expectedPermissionId = Response.ok().entity(1L).build();
        doReturn(expectedPermissionId).when(permissionClient).getIdByResourceAndAction(any(),any());
        doReturn(Response.ok(Boolean.TRUE).build()).when(tenantRoleClient).isPermissionExistentForUser(1001L,1L,null);

        Response response = userResource.create(new User());
        assertEquals(200,response.getStatus());
    }

    /**
     * Tests the create method by a requester that does not have the correct role or authorization
     */
    @Test
    public void testCreateWithAuthorizationDenied() {
        Principal loggedUser = new Principal();
        loggedUser.setSub("aaa-bbb-ccc-ddd");
        HttpSession session = Mockito.mock(HttpSession.class);

        User userToBeCreated = new User();
        userToBeCreated.setSub("xxx-yyy-zzz-www");

        when(servletRequest.getSession()).thenReturn(session);
        when(servletRequest.getSession(false)).thenReturn(session);
        when(session.getAttribute("USER")).thenReturn(loggedUser);
        when(this.userBusinessService.getUserId(loggedUser.getSub())).thenReturn(1001L);

        Response notFoundResponse = Response.status(Response.Status.NOT_FOUND).entity(GenericErrorCodeMessage.RESOURCE_NOT_FOUND.toString()).build();
        doReturn("token-yyz").when(tokensPlaceHolder).getAccessToken();

        doReturn(notFoundResponse).when(permissionClient).getIdByResourceAndAction(any(),any());

        Response response = userResource.create(new User());
        assertEquals(403,response.getStatus());
    }

    /**
     * User is accessing radien for the very fist time.
     * His profile were created on KeyCloak and now he is going to
     * register himself on the radien database
     */
    @Test
    public void testCreateSelfRegisterScenario() {
        Principal principal = new Principal();
        principal.setSub("aaa-bbb-ccc-ddd");
        HttpSession session = Mockito.mock(HttpSession.class);

        User userToBeRegistered = new User();
        userToBeRegistered.setSub(principal.getSub());

        when(servletRequest.getSession()).thenReturn(session);
        when(servletRequest.getSession(false)).thenReturn(session);
        when(session.getAttribute("USER")).thenReturn(principal);

        Response response = userResource.create(userToBeRegistered);
        assertEquals(200,response.getStatus());
    }

    /**
     * Tests Create method to return Remote Resource Exception
     * @throws UserNotFoundException in case of 404 error message
     * @throws RemoteResourceException in case of 500 error message
     * @throws UniquenessConstraintException in case of multiple existent users
     */
    @Test
    public void testGetResponseFromExceptionWhenCreating() throws UserNotFoundException, RemoteResourceException, UniquenessConstraintException {
        Principal principal = new Principal();
        principal.setSub("aaa-bbb-ccc-ddd");
        HttpSession session = Mockito.mock(HttpSession.class);

        when(servletRequest.getSession()).thenReturn(session);
        when(servletRequest.getSession(false)).thenReturn(session);
        when(session.getAttribute("USER")).thenReturn(principal);
        doReturn(1001L).when(this.userBusinessService). getUserId(principal.getSub());

        Response expectedAuthGranted = Response.ok().entity(Boolean.TRUE).build();
        doReturn("token-yyz").when(tokensPlaceHolder).getAccessToken();
        doReturn(expectedAuthGranted).when(tenantRoleClient).isRoleExistentForUser(
                1001L, SystemRolesEnum.SYSTEM_ADMINISTRATOR.getRoleName(), null);
        doReturn(expectedAuthGranted).when(tenantRoleClient).isRoleExistentForUser(
                1001L, SystemRolesEnum.USER_ADMINISTRATOR.getRoleName(), null);

        doThrow(new RemoteResourceException()).when(userBusinessService).create(any(), anyBoolean());

        User user = new User();
        Response response = userResource.create(user);
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

        Response expectedPermissionId = Response.ok().entity(1L).build();
        doReturn(expectedPermissionId).when(permissionClient).getIdByResourceAndAction(any(),any());
        doReturn(Response.ok(Boolean.TRUE).build()).when(tenantRoleClient).isPermissionExistentForUser(1001L,1L,null);

        doThrow(new UniquenessConstraintException()).when(userBusinessService).create(any(), anyBoolean());
        Response response = userResource.create(new User());
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
        doThrow(new RemoteResourceException()).when(userBusinessService).create(any(),anyBoolean());
        Response response = userResource.create(u);
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
        doThrow(new RuntimeException()).when(userBusinessService).create(any(),anyBoolean());
        Response response = userResource.create(u);
        assertEquals(500,response.getStatus());
    }

    /**
     * Updating with success of a record. Should return a 200 code message
     */
    @Test
    public void testUpdate() {
        Principal principal = new Principal();
        principal.setSub("aaa-bbb-ccc-ddd");
        HttpSession session = Mockito.mock(HttpSession.class);

        when(servletRequest.getSession()).thenReturn(session);
        when(servletRequest.getSession(false)).thenReturn(session);
        when(session.getAttribute("USER")).thenReturn(principal);
        doReturn(1001L).when(this.userBusinessService).getUserId(principal.getSub());


        doReturn("token-yyz").when(tokensPlaceHolder).getAccessToken();

        Response expectedPermissionId = Response.ok().entity(1L).build();
        doReturn(expectedPermissionId).when(permissionClient).getIdByResourceAndAction(any(),any());
        doReturn(Response.ok(Boolean.TRUE).build()).when(tenantRoleClient).isPermissionExistentForUser(1001L,1L,null);

        Response response = userResource.update(1L, new User());
        assertEquals(200,response.getStatus());
    }

    /**
     * Tests the update method by a requester that does not have the correct role or authorization
     */
    @Test
    public void testUpdateWithAuthorizationDenied() {
        Principal loggedUser = new Principal();
        loggedUser.setSub("aaa-bbb-ccc-ddd");
        HttpSession session = Mockito.mock(HttpSession.class);

        User userToBeUpdated = new User();
        userToBeUpdated.setSub("xxx-yyy-zzz-www");

        when(servletRequest.getSession()).thenReturn(session);
        when(servletRequest.getSession(false)).thenReturn(session);
        when(session.getAttribute("USER")).thenReturn(loggedUser);
        when(this.userBusinessService.getUserId(loggedUser.getSub())).thenReturn(1001L);

        Response notFoundResponse = Response.status(Response.Status.NOT_FOUND).entity(GenericErrorCodeMessage.RESOURCE_NOT_FOUND.toString()).build();
        doReturn("token-yyz").when(tokensPlaceHolder).getAccessToken();

        doReturn(notFoundResponse).when(permissionClient).getIdByResourceAndAction(any(),any());

        Response response = userResource.update(1L, new User());
        assertEquals(403,response.getStatus());
    }

    /**
     * Tests Update method to return Remote Resource Exception
     * @throws UserNotFoundException in case of 404 error message
     * @throws RemoteResourceException in case of 500 error message
     * @throws UniquenessConstraintException in case of multiple existent users
     */
    @Test
    public void testGetResponseFromExceptionWhenUpdating() throws UserNotFoundException, RemoteResourceException, UniquenessConstraintException {
        Principal principal = new Principal();
        principal.setSub("aaa-bbb-ccc-ddd");
        HttpSession session = Mockito.mock(HttpSession.class);

        when(servletRequest.getSession()).thenReturn(session);
        when(servletRequest.getSession(false)).thenReturn(session);
        when(session.getAttribute("USER")).thenReturn(principal);
        doReturn(1001L).when(this.userBusinessService). getUserId(principal.getSub());

        Response expectedAuthGranted = Response.ok().entity(Boolean.TRUE).build();
        doReturn("token-yyz").when(tokensPlaceHolder).getAccessToken();
        doReturn(expectedAuthGranted).when(tenantRoleClient).isRoleExistentForUser(
                1001L, SystemRolesEnum.SYSTEM_ADMINISTRATOR.getRoleName(), 1L);
        doReturn(expectedAuthGranted).when(tenantRoleClient).isRoleExistentForUser(
                1001L, SystemRolesEnum.USER_ADMINISTRATOR.getRoleName(), 1L);

        doThrow(new RemoteResourceException()).when(userBusinessService).update(any(), anyBoolean());

        User user = new User(); user.setId(1L); user.setSub("aaa-bbb-ccc-ddd");
        Response response = userResource.update(user.getId(), user);
        assertEquals(500, response.getStatus());
    }

    /**
     * Updating with error of a record. Should return a 400 code message Invalid Requested Exception
     * @throws UniquenessConstraintException in case of request could not be performed by any specific and justified in the
     * message reason
     * @throws UserNotFoundException in case of user not found
     */
    @Test
    public void testUpdateInvalid() throws UniquenessConstraintException, UserNotFoundException, RemoteResourceException {
        Principal principal = new Principal();
        principal.setSub("aaa-bbb-ccc-ddd");
        HttpSession session = Mockito.mock(HttpSession.class);

        when(servletRequest.getSession()).thenReturn(session);
        when(servletRequest.getSession(false)).thenReturn(session);
        when(session.getAttribute("USER")).thenReturn(principal);
        when(this.userBusinessService.getUserId(principal.getSub())).thenReturn(1001L);

        Response expectedPermissionId = Response.ok().entity(1L).build();
        doReturn(expectedPermissionId).when(permissionClient).getIdByResourceAndAction(any(),any());
        doReturn(Response.ok(Boolean.TRUE).build()).when(tenantRoleClient).isPermissionExistentForUser(1001L,1L,null);

        doThrow(new UniquenessConstraintException()).when(userBusinessService).update(any(), anyBoolean());
        Response response = userResource.update(1L, new User());
        assertEquals(400,response.getStatus());
    }

    /**
     * Updating of a record with error. Should return a generic error message 500
     * @throws UniquenessConstraintException in case of request could not be performed by any specific and justified in the
     * message reason
     * @throws UserNotFoundException in case of user not found
     */
    @Test
    public void testUpdateRemoteResourceError() throws UniquenessConstraintException, UserNotFoundException, RemoteResourceException {
        User u = new User();
        doThrow(new RemoteResourceException()).when(userBusinessService).update(any(),anyBoolean());
        Response response = userResource.update(1L, u);
        assertEquals(500,response.getStatus());
    }

    /**
     * Updating of a record with error. Should return a generic error message 500
     * @throws UniquenessConstraintException in case of request could not be performed by any specific and justified in the
     * message reason
     * @throws UserNotFoundException in case of user not found
     */
    @Test
    public void testUpdateGenericError() throws UniquenessConstraintException, UserNotFoundException, RemoteResourceException {
        User u = new User();
        doThrow(new RuntimeException()).when(userBusinessService).update(any(),anyBoolean());
        Response response = userResource.update(1L, u);
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
        assertEquals(BatchSummary.ProcessingStatus.SUCCESS, summary.getInternalStatus());
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
                GenericErrorCodeMessage.DUPLICATED_FIELD.toString("email or logon")));
        issuedItems.add(new DataIssue(3,
                GenericErrorCodeMessage.DUPLICATED_FIELD.toString("email or logon")));
        issuedItems.add(new DataIssue(7,
                GenericErrorCodeMessage.DUPLICATED_FIELD.toString("email or logon")));
        issuedItems.add(new DataIssue(8,
                GenericErrorCodeMessage.DUPLICATED_FIELD.toString("email or logon")));
        when(userBusinessService.create(anyList())).thenReturn(new BatchSummary(users.size(), issuedItems));
        Response response = userResource.create(users);
        assertEquals(200, response.getStatus());
        assertNotNull(response.getEntity());
        assertEquals(response.getEntity().getClass(), BatchSummary.class);
        BatchSummary summary = (BatchSummary) response.getEntity();
        assertEquals(4, summary.getTotalNonProcessed());
        assertEquals(6, summary.getTotalProcessed());
        assertEquals(BatchSummary.ProcessingStatus.PARTIAL_SUCCESS, summary.getInternalStatus());
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
                GenericErrorCodeMessage.DUPLICATED_FIELD.toString("email or logon")));
        issuedItems.add(new DataIssue(2,
                GenericErrorCodeMessage.DUPLICATED_FIELD.toString("email or logon")));
        issuedItems.add(new DataIssue(3,
                GenericErrorCodeMessage.DUPLICATED_FIELD.toString("email or logon")));
        issuedItems.add(new DataIssue(4,
                GenericErrorCodeMessage.DUPLICATED_FIELD.toString("email or logon")));
        when(userBusinessService.create(anyList())).thenReturn(new BatchSummary(4, issuedItems));
        Response response = userResource.create(users);
        assertEquals(400, response.getStatus());
        assertNotNull(response.getEntity());
        assertEquals(response.getEntity().getClass(), BatchSummary.class);
        BatchSummary summary = (BatchSummary) response.getEntity();
        assertEquals(4, summary.getTotalNonProcessed());
        assertEquals(0, summary.getTotalProcessed());
        assertEquals(BatchSummary.ProcessingStatus.FAIL, summary.getInternalStatus());
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
     * Tests the set email verify method and should finish with success
     * @throws UserNotFoundException in case of searched user is not existent or not found
     */
    @Test
    public void testUpdateEmailAndExecuteActionEmailVerify() throws UserNotFoundException {
        User systemUser = new User();
        systemUser.setId(1L);
        systemUser.setUserEmail("email@email.com");
        when(userBusinessService.get(1L)).thenReturn(systemUser);
        Response response = userResource.updateEmailAndExecuteActionEmailVerify(1L, systemUser, true);
        assertEquals(200,response.getStatus());
    }

    /**
     * Tests the set email verify method and should finish with generic 500 code message error
     * @throws UserNotFoundException in case of searched user is not existent or not found
     */
    @Test
    public void testUpdateUserEmailAndExecuteActionEmailVerifyGenericError() throws UserNotFoundException {
        doThrow(new RuntimeException()).when(userBusinessService).get(1L);
        Response response = userResource.updateEmailAndExecuteActionEmailVerify(1L, new User(), false);
        assertEquals(500,response.getStatus());
    }

    /**
     * Tests the set email verify method and should finish with generic 404 code message error
     * @throws UserNotFoundException in case of searched user is not existent or not found
     */
    @Test
    public void testUpdateUserEmailAndExecuteActionEmailVerify404() throws UserNotFoundException {
        when(userBusinessService.get(1L)).thenThrow(new UserNotFoundException("1"));
        Response response = userResource.updateEmailAndExecuteActionEmailVerify(1L, new User(), false);
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
     * Tests the refresh token with generic exception
     */
    @Test
    public void testRefreshTokenGenericException() {
        Response responseEmpty = userResource.refreshToken("");
        assertEquals("refresh token null or empty",responseEmpty.getEntity().toString());

        Response responseNull = userResource.refreshToken(null);
        assertEquals("refresh token null or empty",responseNull.getEntity().toString());

    }

    /**
     * Private method to mock the preprocess method requested while validating the roles
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

    /**
     * Test for method {@link UserResource#updatePassword(String, UserPasswordChanging)}
     * Simple scenario where operation is concluded with success.
     * Expected return status: 200 (OK)
     * @throws UserChangeCredentialException thrown in case of any issue regarding changing password business rules
     */
    @Test
    public void testUpdatePassword() throws UserChangeCredentialException {
        String subject = "aaa-bbb-ccc-ddd";

        UserPasswordChanging u = new UserPasswordChanging();
        Principal principal = new Principal();
        principal.setSub(subject);
        HttpSession session = Mockito.mock(HttpSession.class);

        when(servletRequest.getSession()).thenReturn(session);
        when(servletRequest.getSession(false)).thenReturn(session);
        when(session.getAttribute("USER")).thenReturn(principal);

        doReturn("token-yyz").when(tokensPlaceHolder).getAccessToken();
        doNothing().when(userBusinessService).changePassword(subject, u);
        assertEquals(200, userResource.updatePassword(subject, u).getStatus());
    }

    /**
     * Test for method {@link UserResource#updatePassword(String, UserPasswordChanging)}
     * Scenario: Changing password process not being requested by the current logged user and FOR
     * the current logged user. Expected return status: 403 (FORBIDDEN)
     * @throws UserChangeCredentialException thrown in case of any issue regarding changing password business rules
     */
    @Test
    public void testUpdatePasswordNotAllowed() throws UserChangeCredentialException {
        String subject = "aaa-bbb-ccc-ddd";

        UserPasswordChanging u = new UserPasswordChanging();
        Principal principal = new Principal();
        principal.setSub("1");
        HttpSession session = Mockito.mock(HttpSession.class);

        when(servletRequest.getSession()).thenReturn(session);
        when(servletRequest.getSession(false)).thenReturn(session);
        when(session.getAttribute("USER")).thenReturn(principal);

        doReturn("token-yyz").when(tokensPlaceHolder).getAccessToken();
        doNothing().when(userBusinessService).changePassword(subject, u);
        assertEquals(403, userResource.updatePassword(subject, u).getStatus());
    }

    /**
     * Test for method {@link UserResource#updatePassword(String, UserPasswordChanging)}
     * Scenario: Some business rule not met when trying to update user password
     * Expected return status: 400 (BAD REQUEST)
     * @throws UserChangeCredentialException thrown in case of any issue regarding changing password business rules
     */
    @Test
    public void testUpdatePasswordInvalidRule() throws UserChangeCredentialException {
        String subject = "aaa-bbb-ccc-ddd";

        UserPasswordChanging u = new UserPasswordChanging();
        Principal principal = new Principal();
        principal.setSub(subject);
        HttpSession session = Mockito.mock(HttpSession.class);

        when(servletRequest.getSession()).thenReturn(session);
        when(servletRequest.getSession(false)).thenReturn(session);
        when(session.getAttribute("USER")).thenReturn(principal);

        doReturn("token-yyz").when(tokensPlaceHolder).getAccessToken();
        doThrow(new UserChangeCredentialException("test")).when(userBusinessService).changePassword(subject, u);
        assertEquals(400, userResource.updatePassword(subject, u).getStatus());
    }

    /**
     * Test for method {@link UserResource#updatePassword(String, UserPasswordChanging)}
     * Scenario: Unexpected error when changing password.
     * Expected return status: 500 (SERVER ERROR)
     * @throws UserChangeCredentialException thrown in case of any issue regarding changing password business rules
     */
    @Test
    public void testUpdatePasswordUnexpectedError() throws UserChangeCredentialException {
        String subject = "aaa-bbb-ccc-ddd";

        UserPasswordChanging u = new UserPasswordChanging();
        Principal principal = new Principal();
        principal.setSub(subject);
        HttpSession session = Mockito.mock(HttpSession.class);

        when(servletRequest.getSession()).thenReturn(session);
        when(servletRequest.getSession(false)).thenReturn(session);
        when(session.getAttribute("USER")).thenReturn(principal);

        doReturn("token-yyz").when(tokensPlaceHolder).getAccessToken();
        doThrow(new RuntimeException("test")).when(userBusinessService).changePassword(subject, u);
        assertEquals(500, userResource.updatePassword(subject, u).getStatus());
    }
}