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
package io.radien.ms.usermanagement.client.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import java.net.MalformedURLException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.json.JsonObject;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;

import io.radien.api.OAFAccess;
import io.radien.api.model.user.SystemUser;
import io.radien.api.security.TokensPlaceHolder;
import io.radien.api.service.batch.BatchSummary;
import io.radien.api.service.batch.DataIssue;
import io.radien.api.OAFProperties;
import io.radien.api.entity.Page;

import io.radien.exception.NotFoundException;
import io.radien.exception.TokenExpiredException;
import io.radien.exception.SystemException;

import io.radien.ms.authz.client.UserClient;
import io.radien.ms.usermanagement.client.util.UserModelMapper;
import io.radien.ms.usermanagement.client.entities.User;
import io.radien.ms.usermanagement.client.util.ClientServiceUtil;

import org.apache.cxf.bus.extension.ExtensionException;

import org.junit.Before;
import org.junit.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
/**
 * User REST service client requests and responses test
 * {@link io.radien.ms.usermanagement.client.services.UserRESTServiceClient}
 *
 * @author Nuno Santana
 * @author Bruno Gama
 */
public class UserRESTServiceClientTest {

    @InjectMocks
    UserRESTServiceClient target;

    @Mock
    ClientServiceUtil clientServiceUtil;

    @Mock
    TokensPlaceHolder tokensPlaceHolder;

    @Mock
    OAFAccess oafAccess;

    @Mock
    UserClient userClient;

    private User dummyUser = new User();

    /**
     * Method for variable preparation before testing
     */
    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
        dummyUser.setId(1L);
        dummyUser.setLastname("last");
        dummyUser.setFirstname("first");
        dummyUser.setEnabled(true);
        dummyUser.setLogon("test-logon");
        dummyUser.setDelegatedCreation(false);
    }

    /**
     * Method to attempt to get a user based on his subject
     * @throws Exception to be thrown in multiple cases
     */
    @Test
    public void testGetUserBySub() throws Exception {
        String a = "a";

        JsonArrayBuilder builder = Json.createArrayBuilder();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonWriter jsonWriter = Json.createWriter(baos);
        jsonWriter.writeArray(builder.build());
        jsonWriter.close();

        InputStream is = new ByteArrayInputStream(baos.toByteArray());

        Response response = Response.ok(is).build();

        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);

        when(resourceClient.getUsers(a,null,null,null,true,true))
                .thenReturn(response);

        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);

        assertEquals(Optional.empty(),target.getUserBySub(a));
    }

    /**
     * Method to attempt to get a user based on his subject but with token expired
     * @throws Exception to be thrown in multiple cases
     */
    @Test(expected = SystemException.class)
    public void testGetUserBySubTokenExpiration() throws Exception {
        String a = "a";

        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.getUsers(a,null,null,null,true,true)).thenThrow(new TokenExpiredException("teste"));

        when(tokensPlaceHolder.getRefreshToken()).thenReturn("refreshToken");
        when(userClient.refreshToken(any())).thenReturn(Response.ok().entity("refreshToken").build());
        target.getUserBySub(a);
    }

    /**
     * Method to re attempt to get a user based on his subject but with token expired
     * @throws Exception to be thrown in multiple cases
     */
    @Test
    public void testGetUserBySubTokenExpirationReAttempt() throws Exception {
        String a = "a";

        JsonArrayBuilder builder = Json.createArrayBuilder();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonWriter jsonWriter = Json.createWriter(baos);
        jsonWriter.writeArray(builder.build());
        jsonWriter.close();

        InputStream is = new ByteArrayInputStream(baos.toByteArray());

        Response response = Response.ok(is).build();

        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);

        when(resourceClient.getUsers(a,null,null,null,true,true))
                .thenThrow(new TokenExpiredException("teste")).thenReturn(response);

        when(tokensPlaceHolder.getRefreshToken()).thenReturn("refreshToken");
        when(userClient.refreshToken(any())).thenReturn(Response.ok().entity("refreshToken").build());
        assertNotNull(target.getUserBySub(a));
    }

    /**
     * Method to attempt to create a user but with token expired
     * @throws Exception to be thrown in multiple cases
     */
    @Test(expected = SystemException.class)
    public void testCreateUserTokenExpiration() throws Exception {
        SystemUser test = new User();

        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.save(any())).thenThrow(new TokenExpiredException("teste"));

        when(tokensPlaceHolder.getRefreshToken()).thenReturn("refreshToken");
        when(userClient.refreshToken(any())).thenReturn(Response.ok().entity("refreshToken").build());
        target.create(test, false);
    }

    /**
     * Method to re attempt to create a user but with token expired
     * @throws Exception to be thrown in multiple cases
     */
    @Test
    public void testCreateUserTokenExpirationReAttempt() throws Exception {
        SystemUser test = new User();

        Response response = Response.ok().build();
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.save(any())).thenThrow(new TokenExpiredException("teste")).thenReturn(response);

        when(tokensPlaceHolder.getRefreshToken()).thenReturn("refreshToken");
        when(userClient.refreshToken(any())).thenReturn(Response.ok().entity("refreshToken").build());
        assertTrue(target.create(test, false));
    }

    /**
     * Test to attempt to update user password by sending him a email but the token is expired
     * @throws Exception to be thrown in multiple cases
     */
    @Test
    public void testSendUpdatePasswordEmailTokenExpiration() throws Exception {
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.sendUpdatePasswordEmail(anyLong())).thenThrow(new TokenExpiredException("teste"));

        when(tokensPlaceHolder.getRefreshToken()).thenReturn("refreshToken");
        when(userClient.refreshToken(any())).thenReturn(Response.ok().entity("refreshToken").build());

        assertFalse(target.sendUpdatePasswordEmail(2L));
    }

    /**
     * Test to attempt to delete user but token is expired
     * @throws Exception to be thrown in multiple cases
     */
    @Test
    public void testDeleteUserTokenExpiration() throws Exception {
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.delete(anyLong())).thenThrow(new TokenExpiredException("teste"));

        when(tokensPlaceHolder.getRefreshToken()).thenReturn("refreshToken");
        when(userClient.refreshToken(any())).thenReturn(Response.ok().entity("refreshToken").build());

        assertFalse(target.deleteUser(2L));
    }

    /**
     * Test to create multiple users via batch mode but with token expired
     * @throws Exception to be thrown in multiple cases
     */
    @Test
    public void testCreateBatchTokenExpiration() throws Exception {
        SystemUser user = new User();
        SystemUser user2 = new User();

        List<SystemUser> listUsers = new ArrayList<>();
        listUsers.add(user2);
        listUsers.add(user);

        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.create(anyList())).thenThrow(new TokenExpiredException("teste"));

        when(tokensPlaceHolder.getRefreshToken()).thenReturn("refreshToken");
        when(userClient.refreshToken(any())).thenReturn(Response.ok().entity("refreshToken").build());

        Optional<BatchSummary> optional = target.create(listUsers);

        assertEquals(Optional.empty(), optional);
    }

    /**
     * Test to create multiple users via batch mode but with processing exception being throw
     * @throws Exception to be thrown in multiple cases
     */
    @Test(expected = ProcessingException.class)
    public void testCreateBatchProcessingException() throws Exception {
        SystemUser user = new User();
        SystemUser user2 = new User();

        List<SystemUser> listUsers = new ArrayList<>();
        listUsers.add(user2);
        listUsers.add(user);

        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.create(anyList())).thenThrow(new ProcessingException("teste"));
        target.create(listUsers);
    }

    /**
     * Test to attempt to get all the users
     * @throws MalformedURLException in case of wrong url for the user endpoint
     */
    @Test
    public void testGetAll() throws MalformedURLException {
        List<User> list = new ArrayList<>();
        list.add(UserFactory.create("test1", "lastname1",
                "logon1", "sub", "email", 2L));
        Page<SystemUser> page = new Page<>(list, 1, 1, 1);

        JsonObjectBuilder objectBuilder = Json.createObjectBuilder()
                .add("currentPage", page.getCurrentPage())
                .add("totalResults", page.getTotalResults())
                .add("totalPages", page.getTotalPages())
                .add("results", UserModelMapper.map(list));

        JsonObject jsonObject = objectBuilder.build();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(jsonObject.toString().getBytes());
        Response expectedResponse = Response.ok(inputStream).build();

        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.getAll(any(), anyInt(), anyInt(), any(), anyBoolean()))
                .thenReturn(expectedResponse);

        Page<? extends SystemUser> receivedPage = target.getAll(null, 1, 10, null, false);

        assertEquals(1, receivedPage.getTotalPages());
        assertEquals(1, receivedPage.getCurrentPage());
        assertEquals(1, receivedPage.getTotalResults());
    }

    /**
     * Test to attempt to get all the users with malformed exception being throw
     * @throws MalformedURLException in case of wrong url for the user endpoint
     */
    @Test
    public void testGetAllMalformedException() throws MalformedURLException {
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenThrow(new MalformedURLException());

        Page<? extends SystemUser> receivedPage = target.getAll(null, 1, 10, null, false);

        assertEquals(0, receivedPage.getTotalPages());
        assertEquals(0, receivedPage.getCurrentPage());
        assertEquals(0, receivedPage.getTotalResults());
    }

    /**
     * Test to attempt to get all the users with token expired
     * @throws MalformedURLException in case of wrong url for the user endpoint
     */
    @Test
    public void testGetAllRefreshTokenExpiration() throws MalformedURLException {
        List<User> list = new ArrayList<>();
        list.add(UserFactory.create("test1", "lastname1",
                "logon1", "sub",  "email", 2L));
        Page<SystemUser> page = new Page<>(list, 1, 1, 1);

        JsonObjectBuilder objectBuilder = Json.createObjectBuilder()
                .add("currentPage", page.getCurrentPage())
                .add("totalResults", page.getTotalResults())
                .add("totalPages", page.getTotalPages())
                .add("results", UserModelMapper.map(list));

        JsonObject jsonObject = objectBuilder.build();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(jsonObject.toString().getBytes());
        Response expectedResponse = Response.ok(inputStream).build();

        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.getAll(any(), anyInt(), anyInt(), any(), anyBoolean()))
                .thenThrow(new TokenExpiredException());

        when(tokensPlaceHolder.getRefreshToken()).thenReturn("refreshToken");
        when(userClient.refreshToken(any())).thenReturn(Response.ok().entity("refreshToken").build());

        assertNull(target.getAll(null, 1, 10, null, false));
    }

    /**
     * Test to re attempt to get all the users with token expired
     * @throws MalformedURLException in case of wrong url for the user endpoint
     */
    @Test
    public void testGetAllRefreshTokenExpirationReAttempt() throws MalformedURLException {
        List<User> list = new ArrayList<>();
        list.add(UserFactory.create("test1", "lastname1",
                "logon1", "sub",  "email", 2L));
        Page<SystemUser> page = new Page<>(list, 1, 1, 1);

        JsonObjectBuilder objectBuilder = Json.createObjectBuilder()
                .add("currentPage", page.getCurrentPage())
                .add("totalResults", page.getTotalResults())
                .add("totalPages", page.getTotalPages())
                .add("results", UserModelMapper.map(list));

        JsonObject jsonObject = objectBuilder.build();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(jsonObject.toString().getBytes());
        Response expectedResponse = Response.ok(inputStream).build();

        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.getAll(any(), anyInt(), anyInt(), any(), anyBoolean()))
                .thenThrow(new TokenExpiredException()).thenReturn(expectedResponse);

        when(tokensPlaceHolder.getRefreshToken()).thenReturn("refreshToken");
        when(userClient.refreshToken(any())).thenReturn(Response.ok().entity("refreshToken").build());

        assertNotNull(target.getAll(null, 1, 10, null, false));
    }

    /**
     * Test to attempt to update a user with token expired
     * @throws Exception in case of error
     */
    @Test
    public void testUpdateUserTokenExpiration() throws Exception {
        SystemUser user = new User();
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.save(any())).thenThrow(new TokenExpiredException("teste"));

        when(tokensPlaceHolder.getRefreshToken()).thenReturn("refreshToken");
        when(userClient.refreshToken(any())).thenReturn(Response.ok().entity("refreshToken").build());

        assertFalse(target.updateUser(user));
    }

    /**
     * Test to attempt to update a user with malformed url
     * @throws Exception in case of error
     */
    @Test(expected = SystemException.class)
    public void testCreateUserMalformedException() throws Exception {
        SystemUser test = new User();
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenThrow(new MalformedURLException());
        target.create(test, false);
    }

    /**
     * Test to attempt to refresh access token without success
     * @throws Exception in case of error
     */
    @Test(expected = SystemException.class)
    public void testRefreshTokenReturnFalse() throws Exception {
        String a = "a";
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.getUsers(a,null,null,null,true,true)).thenThrow(new TokenExpiredException("teste"));

        when(tokensPlaceHolder.getRefreshToken()).thenReturn("refreshToken");
        when(userClient.refreshToken(any())).thenReturn(Response.notModified().entity("refreshToken").build());
        target.getUserBySub(a);
    }

    /**
     * Test to attempt to refresh access token but with exception being throw
     * @throws Exception to be throw
     */
    @Test(expected = SystemException.class)
    public void testRefreshTokenException() throws Exception {
        String a = "a";
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.getUsers(a,null,null,null,true,true)).thenThrow(new TokenExpiredException("teste"));

        when(tokensPlaceHolder.getRefreshToken()).thenReturn("refreshToken");
        when(userClient.refreshToken(any())).thenThrow(new TokenExpiredException("teste"));
        target.getUserBySub(a);
    }

    /**
     * Private method to get the user management endpoint url
     * @return user endpoint url
     */
    private String getUserManagementUrl(){
        String url = "http://localhost";
        when(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_USERMANAGEMENT)).thenReturn(url);
        return url;
    }

    /**
     * Test to get multiple users by a subject
     * @throws Exception in case of error or user not found
     */
    @Test
    public void testGetUserBySubWithResults() throws Exception {
        String a = "a";
        User user = UserFactory.create(null, null, "logon", null, null, null);
        user.setSub(a);

        JsonArrayBuilder builder = Json.createArrayBuilder();
        builder.add(UserFactory.convertToJsonObject(user));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonWriter jsonWriter = Json.createWriter(baos);
        jsonWriter.writeArray(builder.build());
        jsonWriter.close();

        InputStream is = new ByteArrayInputStream(baos.toByteArray());

        Response response = Response.ok(is).build();

        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(resourceClient.getUsers(a,null,null,null,true,true))
                .thenReturn(response);
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);

        assertTrue(target.getUserBySub(a).isPresent());
    }

    /**
     * Test to get system user but he is not found
     * @throws Exception to be throw in cas user is not found
     */
    @Test
    public void testGetSystemUserNotFoundException() throws Exception {
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);

        when(resourceClient.getById(anyLong())).thenThrow(new NotFoundException());

        assertEquals(target.getUserById(2L), Optional.empty());
    }

    /**
     * Test to try to get system user but exception being throw
     * @throws Exception to be throw
     */
    @Test(expected = Exception.class)
    public void testGetSystemUserException() throws Exception {
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenThrow(new MalformedURLException());;
        target.getUserById(2L);
    }

    /**
     * Test to get user by subject but there are more then one
     * @throws Exception to be throw
     */
    @Test(expected = Exception.class)
    public void testGetUserBySubNonUnique() throws Exception {
        String a = "a";
        Page<User> page = new Page<>(new ArrayList<>(),1,2,0);

        JsonObjectBuilder builder = Json.createObjectBuilder();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonWriter jsonWriter = Json.createWriter(baos);
        jsonWriter.writeObject(builder.build());
        jsonWriter.close();

        InputStream is = new ByteArrayInputStream(baos.toByteArray());

        Response response = Response.ok(is).build();

        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(resourceClient.getUsers(a,null,null,null,true,true))
                .thenReturn(response);
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        target.getUserBySub(a);
    }

    /**
     * Test to get users by sub but extension exception being throw
     * @throws Exception to be throw
     */
    @Test(expected = SystemException.class)
    public void testGetUserBySubExtensionException() throws Exception {
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenThrow(new ExtensionException(new Exception()));
        target.getUserBySub("a");
    }

    /**
     * Test to get users by sub but processing exception being throw
     * @throws Exception to be throw
     */
    @Test(expected = SystemException.class)
    public void testGetUserBySubProcessingException() throws Exception {
        String a = "a";
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(resourceClient.getUsers(a,null,null,null,true,true))
                .thenThrow(new ProcessingException("test"));
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        target.getUserBySub(a);
    }

    /**
     * Private method to refresh access token
     */
    private void mockRefreshToken() {
        when(tokensPlaceHolder.getAccessToken()).thenReturn("aaaaa-aaaaaa");
        Response response = Response.ok().entity("bbbb-bbb-bbb-bbb-bbb").build();
        when(userClient.refreshToken(any())).thenReturn(response);
    }

    /**
     * Test to get user by id with the first refresh token expired so that we can refresh it
     * @throws Exception to be throw if token expired
     */
    @Test
    public void testGetUserByIdWithFirstTokenExpiredException() throws Exception {
        Long id = 1L;
        User u = new User();
        u.setId(id);
        u.setFirstname("test");
        u.setLastname("test");
        u.setUserEmail("test");
        u.setSub("sub");
        u.setLogon("test");

        ByteArrayOutputStream i = new ByteArrayOutputStream();
        JsonWriter jsonWriter = Json.createWriter(i);
        jsonWriter.writeObject(UserModelMapper.map(u));
        jsonWriter.close();

        InputStream is = new ByteArrayInputStream(i.toByteArray());

        Response response = Response.ok(is).build();

        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);

        when(resourceClient.getById(id)).
                thenThrow(new TokenExpiredException()).
                thenReturn(response);
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);

        mockRefreshToken();

        assertEquals(Optional.of(u).get().getId(), target.getUserById(id).get().getId());
    }

    /**
     * Test to get user by id with the second refresh token expired so that we can refresh it
     * @throws Exception to be throw if token expired
     */
    @Test(expected = SystemException.class)
    public void testGetUserByIdWithSecondTokenExpiredException() throws Exception {
        Long id = 1L;

        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);

        when(resourceClient.getById(id)).
                thenThrow(new TokenExpiredException()).
                thenThrow(new TokenExpiredException());

        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        mockRefreshToken();

        target.getUserById(id);
    }

    /**
     * Test to get user by logon with the first refresh token expired so that we can refresh it
     * @throws Exception to be throw if token expired
     */
    @Test
    public void testGetUserByLogonWithFirstTokenExpiredException() throws Exception {
        Long id = 1L;
        User u = new User();
        u.setId(id);
        u.setFirstname("test");
        u.setLastname("test");
        u.setUserEmail("test");
        u.setSub("sub");
        u.setLogon("test-logon");

        JsonArrayBuilder builder = Json.createArrayBuilder();
        builder.add(UserFactory.convertToJsonObject(u));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonWriter jsonWriter = Json.createWriter(baos);
        jsonWriter.writeArray(builder.build());
        jsonWriter.close();

        InputStream is = new ByteArrayInputStream(baos.toByteArray());

        Response response = Response.ok(is).build();

        UserResourceClient rc = Mockito.mock(UserResourceClient.class);

        doReturn(rc).when(clientServiceUtil).getUserResourceClient(any());

        when(rc.getUsers(null, null, "test-logon", null,true, true)).
            thenThrow(new TokenExpiredException()).thenReturn(response);

        mockRefreshToken();

        assertEquals(Optional.of(u).get().getId(),
                target.getUserByLogon(u.getLogon()).get().getId());
    }

    /**
     * Test to get user by logon with the second refresh token expired so that we can refresh it
     * @throws Exception to be throw if token expired
     */
    @Test(expected = Exception.class)
    public void testGetSystemUserByLogonException() throws Exception {
        JsonArrayBuilder builder = Json.createArrayBuilder();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonWriter jsonWriter = Json.createWriter(baos);
        jsonWriter.writeArray(builder.build());
        jsonWriter.close();

        InputStream is = new ByteArrayInputStream(baos.toByteArray());

        Response response = Response.ok(is).build();

        UserResourceClient rc = Mockito.mock(UserResourceClient.class);

        doThrow(new MalformedURLException()).when(clientServiceUtil).getUserResourceClient(any());

        when(rc.getUsers(null, null, "test-logon", null,true, true)).
                thenReturn(response);
        target.getUserByLogon("test-logon");
    }

    /**
     * Test to get user by logon but without any results to be found
     * @throws Exception to be throw
     */
    @Test
    public void testGetUserByLogonWithNoResults() throws Exception {
        JsonArrayBuilder builder = Json.createArrayBuilder();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonWriter jsonWriter = Json.createWriter(baos);
        jsonWriter.writeArray(builder.build());
        jsonWriter.close();

        InputStream is = new ByteArrayInputStream(baos.toByteArray());

        Response response = Response.ok(is).build();

        UserResourceClient rc = Mockito.mock(UserResourceClient.class);

        doReturn(rc).when(clientServiceUtil).getUserResourceClient(any());

        when(rc.getUsers(null, null, "test-logon", null,true, true)).
                thenReturn(response);

        assertEquals(target.getUserByLogon("test-logon"), Optional.empty());
    }

    /**
     * test to get user by logon with second expiration token expired
     * @throws Exception to be throw
     */
    @Test(expected = SystemException.class)
    public void testGetUserByLogonWithSecondTokenExpiredException() throws Exception {
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);

        when(resourceClient.getUsers(null, null, "test-logon", null,true, true)).
                thenThrow(new TokenExpiredException()).
                thenThrow(new TokenExpiredException());

        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        mockRefreshToken();

        target.getUserByLogon("test-logon");
    }

    /**
     * Test to attempt the user creation
     * @throws MalformedURLException in case of endpoint url is wrong
     * @throws SystemException in case of token expiration
     * @throws TokenExpiredException in case of token expiration
     */
    @Test
    public void testCreate() throws MalformedURLException, SystemException, TokenExpiredException {
        User u = new User();
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(resourceClient.save(u)).thenReturn(Response.ok().build());
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        assertTrue(target.create(u,true));
    }

    /**
     * Test to attempt the user creation but without success
     * @throws MalformedURLException in case of endpoint url is wrong
     * @throws SystemException in case of token expiration
     * @throws TokenExpiredException in case of token expiration
     */
    @Test
    public void testCreateFail() throws MalformedURLException, TokenExpiredException, SystemException {
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        User u = new User();
        when(resourceClient.save(u)).thenReturn(Response.serverError().entity("test error msg").build());
        when(clientServiceUtil.getUserResourceClient(any())).thenReturn(resourceClient);
        assertFalse(target.create(u,true));
    }

    /**
     * Test to attempt the user creation but with processing exception being throw
     * @throws MalformedURLException in case of endpoint url is wrong
     * @throws SystemException in case of token expiration
     * @throws TokenExpiredException in case of token expiration
     */
    @Test(expected = SystemException.class)
    public void testCreateProcessingException() throws MalformedURLException, TokenExpiredException, SystemException {
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        User u = new User();
        when(resourceClient.save(u)).thenThrow(new ProcessingException(""));
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        target.create(u,true);
    }

    /**
     * Test to validate the initiate resetting password
     * @throws MalformedURLException in case of endpoint url is wrong
     * @throws TokenExpiredException in case of token expiration
     */
    @Test
    public void testSetInitiateResetPassword() throws MalformedURLException, TokenExpiredException{
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(resourceClient.sendUpdatePasswordEmail(dummyUser.getId())).thenReturn(Response.ok().build());
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);

        boolean success = false;
        try {
            assertTrue(target.sendUpdatePasswordEmail(dummyUser.getId()));
        } catch (Exception e) {
            success = true;
        }
        assertFalse(success);
    }

    /**
     *Test to validate the initiate resetting password but without success
     * @throws MalformedURLException in case of endpoint url is wrong
     * @throws TokenExpiredException in case of token expiration
     */
    @Test
    public void testSetInitiateResetPasswordFail() throws MalformedURLException, TokenExpiredException{
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(resourceClient.sendUpdatePasswordEmail(dummyUser.getId())).thenReturn(Response.serverError().entity("test error msg").build());
        when(clientServiceUtil.getUserResourceClient(any())).thenReturn(resourceClient);
        assertFalse(target.sendUpdatePasswordEmail(dummyUser.getId()));
    }

    /**
     *Test to validate the update password but with malformed url
     * @throws MalformedURLException in case of endpoint url is wrong
     * @throws TokenExpiredException in case of token expiration
     */
    @Test
    public void testUpdatePasswordEmailMalformedException() throws MalformedURLException, TokenExpiredException{
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenThrow(new MalformedURLException());
        assertFalse(target.sendUpdatePasswordEmail(dummyUser.getId()));
    }

    /**
     * Test to re attempt to update user password by sending him email but the token is expired
     * @throws Exception to be thrown in multiple cases
     */
    @Test
    public void testUpdatePasswordEmailTokenExpirationReAttempt() throws Exception {
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        Response response = Response.ok().build();
        when(resourceClient.sendUpdatePasswordEmail(anyLong())).thenThrow(new TokenExpiredException("test email change password")).thenReturn(response);

        when(tokensPlaceHolder.getRefreshToken()).thenReturn("refreshToken");
        when(userClient.refreshToken(any())).thenReturn(Response.ok().entity("refreshToken").build());

        assertTrue(target.sendUpdatePasswordEmail(2L));
    }

    /**
     * Test to validate the initiate resetting email verify
     * @throws MalformedURLException in case of endpoint url is wrong
     * @throws TokenExpiredException in case of token expiration
     */
    @Test
    public void testUpdateUserEmailAndExecuteActionEmailVerify() throws MalformedURLException, TokenExpiredException{
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(resourceClient.updateUserEmailAndExecuteActionEmailVerify(anyLong(), anyString()))
                .thenReturn(Response.ok().build());
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);

        boolean success = false;
        try {
            assertTrue(target.updateUserEmailAndExecuteActionEmailVerify(dummyUser.getId(), dummyUser.getUserEmail()));
        } catch (Exception e) {
            success = true;
        }
        assertTrue(success);
    }

    /**
     *Test to validate the initiate resetting email verify but without success
     * @throws MalformedURLException in case of endpoint url is wrong
     * @throws TokenExpiredException in case of token expiration
     */
    @Test
    public void testUpdateUserEmailAndExecuteActionEmailVerifyFail() throws MalformedURLException, TokenExpiredException{
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(resourceClient.updateUserEmailAndExecuteActionEmailVerify(anyLong(), anyString())).thenReturn(Response.serverError().entity("test msg error").build());
        when(clientServiceUtil.getUserResourceClient(any())).thenReturn(resourceClient);

        assertFalse(target.updateUserEmailAndExecuteActionEmailVerify(dummyUser.getId(), "email@email.com"));
    }

    /**
     *Test to validate the update email verify but with malformed url
     * @throws MalformedURLException in case of endpoint url is wrong
     * @throws TokenExpiredException in case of token expiration
     */
    @Test
    public void testUpdateUserEmailAndExecuteActionEmailVerifyMalformedException() throws MalformedURLException, TokenExpiredException{
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenThrow(new MalformedURLException());
        assertFalse(target.updateUserEmailAndExecuteActionEmailVerify(dummyUser.getId(), dummyUser.getUserEmail()));
    }

    /**
     * Test to attempt to update user email verify by sending him email but the token is expired
     * @throws Exception to be thrown in multiple cases
     */
    @Test
    public void testUpdateUserEmailAndExecuteActionEmailVerifyTokenExpiration() throws Exception {
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.updateUserEmailAndExecuteActionEmailVerify(anyLong(), anyString()))
                .thenThrow(new TokenExpiredException("test email verify"));

        when(tokensPlaceHolder.getRefreshToken()).thenReturn("refreshToken");
        when(userClient.refreshToken(any())).thenReturn(Response.ok().entity("refreshToken").build());

        assertFalse(target.updateUserEmailAndExecuteActionEmailVerify(2L, "email@email.com"));
    }

    /**
     * Test to attempt to update user email verify by sending him a email but the token is expired and
     * trying to attempt to retry after refreshed the token
     * @throws Exception to be thrown in multiple cases
     */
    @Test
    public void testUpdateUserEmailAndExecuteActionEmailVerifyTokenExpirationReAttempt() throws Exception {
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        Response response = Response.ok().build();
        when(resourceClient.updateUserEmailAndExecuteActionEmailVerify(anyLong(), anyString())).thenThrow(new TokenExpiredException("test email verify")).thenReturn(response);

        when(tokensPlaceHolder.getRefreshToken()).thenReturn("refreshToken");
        when(userClient.refreshToken(any())).thenReturn(Response.ok().entity("refreshToken").build());

        assertTrue(target.updateUserEmailAndExecuteActionEmailVerify(2L, "email@email.com"));
    }

    /**
     *Test to validate the delete user but with malformed exception
     * @throws MalformedURLException in case of endpoint url is wrong
     * @throws TokenExpiredException in case of token expiration
     */
    @Test
    public void testDeleteMalformedException() throws MalformedURLException, TokenExpiredException{
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenThrow(new MalformedURLException());
        assertFalse(target.deleteUser(dummyUser.getId()));
    }

    /**
     *Test to validate the update user but with malformed exception
     * @throws MalformedURLException in case of endpoint url is wrong
     * @throws TokenExpiredException in case of token expiration
     */
    @Test
    public void testUpdateUserMalformedException() throws MalformedURLException, TokenExpiredException{
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenThrow(new MalformedURLException());
        assertFalse(target.updateUser(dummyUser));
    }

    /**
     *Test to validate the update user
     * @throws MalformedURLException in case of endpoint url is wrong
     * @throws TokenExpiredException in case of token expiration
     */
    @Test
    public void testUpdateUser() throws MalformedURLException, TokenExpiredException{
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(resourceClient.save(dummyUser)).thenReturn(Response.ok().build());
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        dummyUser.setFirstname("firstname-update");

        boolean success = false;
        try {
            assertTrue(target.updateUser(dummyUser));
        } catch (Exception e) {
            success = true;
        }
        assertFalse(success);
    }

    /**
     * Test to attempt to update user but without success
     * @throws MalformedURLException in case of endpoint url is wrong
     * @throws TokenExpiredException in case of token expiration
     */
    @Test
    public void testUpdateUserFail() throws MalformedURLException, TokenExpiredException{
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(resourceClient.save(dummyUser)).thenReturn(Response.serverError().entity("test error msg").build());
        when(clientServiceUtil.getUserResourceClient(any())).thenReturn(resourceClient);
        dummyUser.setLastname("lastname-updated");
        assertFalse(target.updateUser(dummyUser));
    }

    /**
     * Test update with reattempting refresh token
     * @throws MalformedURLException in case of endpoint url is wrong
     * @throws TokenExpiredException in case of token expiration
     */
    @Test
    public void testUpdateUserTokenExpirationReAttempt() throws MalformedURLException , TokenExpiredException {
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        Response response = Response.ok().build();
        when(resourceClient.save(any())).thenThrow(new TokenExpiredException("test update user")).thenReturn(response);

        when(tokensPlaceHolder.getRefreshToken()).thenReturn("refreshToken");
        when(userClient.refreshToken(any())).thenReturn(Response.ok().entity("refreshToken").build());

        assertTrue(target.updateUser(new User()));
    }

    /**
     * Test to delete user
     * @throws MalformedURLException in case of endpoint url is wrong
     * @throws TokenExpiredException in case of token expiration
     */
    @Test
    public void testDeleteUser() throws MalformedURLException , TokenExpiredException{

        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(resourceClient.delete(dummyUser.getId())).thenReturn(Response.ok().build());
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        assertTrue(target.deleteUser(1L));
    }

    /**
     * Test delete without success
     * @throws MalformedURLException in case of endpoint url is wrong
     * @throws TokenExpiredException in case of token expiration
     */
    @Test
    public void testDeleteUserFail() throws MalformedURLException , TokenExpiredException{
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(resourceClient.delete(dummyUser.getId())).thenReturn(Response.serverError().entity("test error msg").build());
        when(clientServiceUtil.getUserResourceClient(any())).thenReturn(resourceClient);
        assertFalse(target.deleteUser(1L));
    }

    /**
     * Test delete with reattempting refresh token
     * @throws MalformedURLException in case of endpoint url is wrong
     * @throws TokenExpiredException in case of token expiration
     */
    @Test
    public void testDeleteUserTokenExpirationReAttempt() throws MalformedURLException , TokenExpiredException {
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        Response response = Response.ok().build();
        when(resourceClient.delete(anyLong())).thenThrow(new TokenExpiredException("test delete user")).thenReturn(response);

        when(tokensPlaceHolder.getRefreshToken()).thenReturn("refreshToken");
        when(userClient.refreshToken(any())).thenReturn(Response.ok().entity("refreshToken").build());

        assertTrue(target.deleteUser(2000L));
    }

    /**
     * Test create users in batch mode
     * @throws MalformedURLException in case of endpoint url is wrong
     * @throws TokenExpiredException in case of token expiration
     */
    @Test
    public void testCreateBatch() throws MalformedURLException , TokenExpiredException{
        List<SystemUser> userList = new ArrayList<>();
        BatchSummary batchSummary = new BatchSummary(100);
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(resourceClient.create(anyList())).thenReturn(Response.ok().entity(batchSummary).build());
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        Optional<BatchSummary> opt = target.create(userList);
        assertTrue(opt.isPresent());
        assertNotNull(opt.get());
        assertEquals(BatchSummary.ProcessingStatus.SUCCESS, opt.get().getInternalStatus());
    }

    /**
     * Test create users in batch mode but non is inserted
     * @throws MalformedURLException in case of endpoint url is wrong
     * @throws TokenExpiredException in case of token expiration
     */
    @Test
    public void testCreateBatchNoneInserted() throws MalformedURLException , TokenExpiredException{
        int rows = 4;
        List<SystemUser> userList = new ArrayList<>();
        List<DataIssue> issues = new ArrayList<>();
        for (int rowIndex=1; rowIndex<=rows; rowIndex++) {
            issues.add(new DataIssue(rowIndex, "Invalid fiel"));
        }
        BatchSummary batchSummary = new BatchSummary(rows, issues);
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(resourceClient.create(anyList())).thenReturn(Response.ok().entity(batchSummary).build());
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        Optional<BatchSummary> opt = target.create(userList);
        assertTrue(opt.isPresent());
        assertNotNull(opt.get());
        assertEquals(opt.get().getTotal(), rows);
        assertEquals(0, opt.get().getTotalProcessed());
        assertEquals(opt.get().getTotalNonProcessed(), rows);
        assertEquals(BatchSummary.ProcessingStatus.FAIL, opt.get().getInternalStatus());
    }

    /**
     * Test create users in batch mode and just some of them are inserted
     * @throws MalformedURLException in case of endpoint url is wrong
     * @throws TokenExpiredException in case of token expiration
     */
    @Test
    public void testCreateBatchSomeNotInserted() throws MalformedURLException, TokenExpiredException{
        int rows = 10;
        List<SystemUser> userList = new ArrayList<>();
        List<DataIssue> issues = new ArrayList<>();
        for (int rowIndex=1; rowIndex<=rows-6; rowIndex++) {
            issues.add(new DataIssue(rowIndex, "Invalid fiel"));
        }
        BatchSummary batchSummary = new BatchSummary(rows, issues);
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(resourceClient.create(anyList())).thenReturn(Response.ok().entity(batchSummary).build());
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        Optional<BatchSummary> opt = target.create(userList);
        assertTrue(opt.isPresent());
        assertNotNull(opt.get());
        assertEquals(opt.get().getTotal(), rows);
        assertEquals(6, opt.get().getTotalProcessed());
        assertEquals(4, opt.get().getNonProcessedItems().size());
        assertEquals(BatchSummary.ProcessingStatus.PARTIAL_SUCCESS, opt.get().getInternalStatus());
    }

    /**
     * Test create users in batch mode with failure
     * @throws MalformedURLException in case of endpoint url is wrong
     * @throws TokenExpiredException in case of token expiration
     */
    @Test
    public void testCreateBatchFail() throws MalformedURLException , TokenExpiredException{
        List<SystemUser> userList = new ArrayList<>();
        BatchSummary batchSummary = new BatchSummary(100);
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(resourceClient.create(anyList())).thenReturn(Response.serverError().entity("test error msg").build());
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        Optional<BatchSummary> opt = target.create(userList);
        assertFalse(opt.isPresent());
    }

    /**
     * Test to get multiple users by a list of ids
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void testGetUsersByIdsWithResults() throws MalformedURLException, SystemException {
        User user1 = UserFactory.create(null, null, "logon1", null, null, null);
        user1.setId(1L);

        User user2 = UserFactory.create(null, null, "logon2", null, null, null);
        user2.setId(2L);

        List<Long> ids = Arrays.asList(user1.getId(), user2.getId());

        JsonArrayBuilder builder = Json.createArrayBuilder();
        builder.add(UserFactory.convertToJsonObject(user1));
        builder.add(UserFactory.convertToJsonObject(user2));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonWriter jsonWriter = Json.createWriter(baos);
        jsonWriter.writeArray(builder.build());
        jsonWriter.close();

        InputStream is = new ByteArrayInputStream(baos.toByteArray());

        Response response = Response.ok(is).build();

        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(resourceClient.getUsers(null,null,null,ids,true,true)).thenReturn(response);
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);

        List<? extends SystemUser> outcome = target.getUsersByIds(ids);
        assertNotNull(outcome);
        assertEquals(2, outcome.size());
    }

    /**
     * Test to get users by ids but extension exception being throw
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testGetUsersByIdsExtensionException() throws MalformedURLException, SystemException {
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenThrow(new ExtensionException(new Exception()));
        target.getUsersByIds(new ArrayList());
    }

    /**
     * Test to get users by ids but processing exception being throw
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testGetUsersByIdsProcessingException() throws MalformedURLException, SystemException {
        List<Long> ids = new ArrayList();
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(resourceClient.getUsers(null,null,null,ids,true,true))
                .thenThrow(new ProcessingException("test"));
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        target.getUsersByIds(ids);
    }

    /**
     * Method to attempt to get a list of users based on their ids but with token expired
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testGetUsersByIdsTokenExpiration() throws MalformedURLException, SystemException {
        List<Long> ids = Arrays.asList(9L, 10L, 11L);

        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.getUsers(null,null,null,ids,true,true)).thenThrow(new TokenExpiredException("teste"));

        when(tokensPlaceHolder.getRefreshToken()).thenReturn("refreshToken");
        when(userClient.refreshToken(any())).thenReturn(Response.ok().entity("refreshToken").build());
        target.getUsersByIds(ids);
    }

    /**
     * In this scenario the list of users is retrieved (based on their ids) by reattempt (retry)
     * after a JWT token expires
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void testGetUsersByIdsByReattempt() throws MalformedURLException, SystemException {
        User user3 = UserFactory.create(null, null, "logon1", null, null, null);
        user3.setId(999L);

        List<Long> ids = Collections.singletonList(user3.getId());

        JsonArrayBuilder builder = Json.createArrayBuilder();
        builder.add(UserFactory.convertToJsonObject(user3));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonWriter jsonWriter = Json.createWriter(baos);
        jsonWriter.writeArray(builder.build());
        jsonWriter.close();

        InputStream is = new ByteArrayInputStream(baos.toByteArray());

        Response response = Response.ok(is).build();

        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.getUsers(null,null,null,ids,true,true)).
                thenThrow(new TokenExpiredException("test")).
                thenReturn(response);

        when(tokensPlaceHolder.getRefreshToken()).thenReturn("refreshToken");
        when(userClient.refreshToken(any())).thenReturn(Response.ok().entity("refreshToken").build());

        List<? extends SystemUser> outcome = target.getUsersByIds(ids);
        assertNotNull(outcome);
        assertEquals(1, outcome.size());
    }
}