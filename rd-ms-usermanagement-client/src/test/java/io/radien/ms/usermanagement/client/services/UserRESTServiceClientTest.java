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
package io.radien.ms.usermanagement.client.services;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
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

import io.radien.exception.NotFoundException;
import io.radien.exception.TokenExpiredException;
import io.radien.ms.authz.client.UserClient;
import io.radien.ms.usermanagement.client.util.UserModelMapper;
import org.apache.cxf.bus.extension.ExtensionException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import io.radien.api.OAFProperties;
import io.radien.api.entity.Page;
import io.radien.exception.SystemException;
import io.radien.ms.usermanagement.client.entities.User;
import io.radien.ms.usermanagement.client.util.ClientServiceUtil;


/**
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

        when(resourceClient.getUsers(a,null,null,true,true))
                .thenReturn(response);

        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);

        assertEquals(Optional.empty(),target.getUserBySub(a));
    }

    @Test
    public void testGetUserBySubTokenExpiration() throws Exception {
        String a = "a";

        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.getUsers(a,null,null,true,true)).thenThrow(new TokenExpiredException("teste"));

        when(tokensPlaceHolder.getRefreshToken()).thenReturn("refreshToken");
        when(userClient.refreshToken(any())).thenReturn(Response.ok().entity("refreshToken").build());

        boolean success = false;
        try {
            target.getUserBySub(a);
        } catch (SystemException e){
            success = true;
        }
        assertTrue(success);
    }

    @Test
    public void testCreateUserTokenExpiration() throws Exception {
        SystemUser test = new User();

        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.save(any())).thenThrow(new TokenExpiredException("teste"));

        when(tokensPlaceHolder.getRefreshToken()).thenReturn("refreshToken");
        when(userClient.refreshToken(any())).thenReturn(Response.ok().entity("refreshToken").build());

        boolean success = false;
        try {
            target.create(test, false);
        } catch (SystemException e){
            success = true;
        }
        assertTrue(success);
    }

    @Test
    public void testSendUpdatePasswordEmailTokenExpiration() throws Exception {
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.sendUpdatePasswordEmail(anyLong())).thenThrow(new TokenExpiredException("teste"));

        when(tokensPlaceHolder.getRefreshToken()).thenReturn("refreshToken");
        when(userClient.refreshToken(any())).thenReturn(Response.ok().entity("refreshToken").build());

        assertFalse(target.sendUpdatePasswordEmail(2L));
    }

    @Test
    public void testDeleteUserTokenExpiration() throws Exception {
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.delete(anyLong())).thenThrow(new TokenExpiredException("teste"));

        when(tokensPlaceHolder.getRefreshToken()).thenReturn("refreshToken");
        when(userClient.refreshToken(any())).thenReturn(Response.ok().entity("refreshToken").build());

        assertFalse(target.deleteUser(2L));
    }

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

    @Test
    public void testCreateBatchProcessingException() throws Exception {
        SystemUser user = new User();
        SystemUser user2 = new User();

        List<SystemUser> listUsers = new ArrayList<>();
        listUsers.add(user2);
        listUsers.add(user);

        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.create(anyList())).thenThrow(new ProcessingException("teste"));

        boolean success = false;
        try {
            target.create(listUsers);
        } catch (ProcessingException e){
            success = true;
        }
        assertTrue(success);
    }

    @Test
    public void testGetAll() throws MalformedURLException {
        List<User> list = new ArrayList<>();
        list.add(UserFactory.create("test1", "lastname1",
                "logon1", "sub", null, "email", 2L));
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

    @Test
    public void testGetAllMalformedException() throws MalformedURLException {
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenThrow(new MalformedURLException());

        Page<? extends SystemUser> receivedPage = target.getAll(null, 1, 10, null, false);

        assertEquals(0, receivedPage.getTotalPages());
        assertEquals(0, receivedPage.getCurrentPage());
        assertEquals(0, receivedPage.getTotalResults());
    }

    @Test
    public void testGetAllRefreshTokenExpiration() throws MalformedURLException {
        List<User> list = new ArrayList<>();
        list.add(UserFactory.create("test1", "lastname1",
                "logon1", "sub", null, "email", 2L));
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

    @Test
    public void testCreateUserMalformedException() throws Exception {
        SystemUser test = new User();

        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenThrow(new MalformedURLException());

        boolean success = false;
        try {
            target.create(test, false);
        } catch (SystemException e){
            success = true;
        }
        assertTrue(success);
    }

    @Test
    public void testRefreshTokenReturnFalse() throws Exception {
        String a = "a";
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.getUsers(a,null,null,true,true)).thenThrow(new TokenExpiredException("teste"));

        when(tokensPlaceHolder.getRefreshToken()).thenReturn("refreshToken");
        when(userClient.refreshToken(any())).thenReturn(Response.notModified().entity("refreshToken").build());

        boolean success = false;
        try {
            target.getUserBySub(a);
        } catch (SystemException e){
            success = true;
        }
        assertTrue(success);
    }

    @Test
    public void testRefreshTokenException() throws Exception {
        String a = "a";
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.getUsers(a,null,null,true,true)).thenThrow(new TokenExpiredException("teste"));

        when(tokensPlaceHolder.getRefreshToken()).thenReturn("refreshToken");
        when(userClient.refreshToken(any())).thenThrow(new TokenExpiredException("teste"));

        boolean success = false;
        try {
            target.getUserBySub(a);
        } catch (SystemException e){
            success = true;
        }
        assertTrue(success);
    }

    private String getUserManagementUrl(){
        String url = "http://localhost";
        when(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_USERMANAGEMENT)).thenReturn(url);
        return url;
    }

    @Test
    public void testGetUserBySubWithResults() throws Exception {
        String a = "a";
        User user = UserFactory.create(null, null, "logon", null, null, null, null);
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
        when(resourceClient.getUsers(a,null,null,true,true))
                .thenReturn(response);
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);

        assertTrue(target.getUserBySub(a).isPresent());
    }

    @Test
    public void testGetSystemUserNotFoundException() throws Exception {
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);boolean success = false;

        when(resourceClient.getById(anyLong())).thenThrow(new NotFoundException());

        assertEquals(target.getUserById(2L), Optional.empty());
    }

    @Test
    public void testGetSystemUserException() throws Exception {
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenThrow(new MalformedURLException());;
        boolean success = false;
        try {
            target.getUserById(2L);
        }catch (Exception e) {
            success=true;
        }

        assertTrue(success);
    }

    @Test
    public void testGetUserBySubNonUnique() throws Exception {
        String a = "a";
        Page<User> page = new Page<>(new ArrayList<>(),1,2,0);

        JsonObjectBuilder builder = Json.createObjectBuilder();
        //FactoryUtilService.addValueInt(builder, "currentPage", page.getCurrentPage());
        //FactoryUtilService.addValueInt(builder, "totalPages", page.getTotalPages());
        //FactoryUtilService.addValueInt(builder, "totalResults", page.getTotalResults());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonWriter jsonWriter = Json.createWriter(baos);
        jsonWriter.writeObject(builder.build());
        jsonWriter.close();

        InputStream is = new ByteArrayInputStream(baos.toByteArray());

        Response response = Response.ok(is).build();

        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(resourceClient.getUsers(a,null,null,true,true))
                .thenReturn(response);
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        boolean success = false;
        try {
            target.getUserBySub(a);
        } catch (Exception e){
            success = true;
        }
        assertTrue(success);
    }

    @Test
    public void testGetUserBySubExtensionException() throws Exception {
        boolean success = false;
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenThrow(new ExtensionException(new Exception()));
        try {
            target.getUserBySub("a");

        }catch (SystemException es){
            if (es.getMessage().contains(ExtensionException.class.getName())) {

                success = true;
            }
        }
        assertTrue(success);
    }
    @Test
    public void testGetUserBySubProcessingException() throws Exception {
        boolean success = false;
        String a = "a";
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(resourceClient.getUsers(a,null,null,true,true))
                .thenThrow(new ProcessingException("test"));
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);

        try {
            target.getUserBySub(a);

        }catch (SystemException se){
            if (se.getMessage().contains(ProcessingException.class.getName())) {
                success = true;
            }
        }
        assertTrue(success);
    }

    private void mockRefreshToken() {
        when(tokensPlaceHolder.getAccessToken()).thenReturn("aaaaa-aaaaaa");
        Response response = Response.ok().entity("bbbb-bbb-bbb-bbb-bbb").build();
        when(userClient.refreshToken(any())).thenReturn(response);
    }

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

    @Test
    public void testGetUserByIdWithSecondTokenExpiredException() throws Exception {
        Long id = 1L;

        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);

        when(resourceClient.getById(id)).
                thenThrow(new TokenExpiredException()).
                thenThrow(new TokenExpiredException());

        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        mockRefreshToken();


        assertThrows(SystemException.class, () -> target.getUserById(id));
    }

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

        when(rc.getUsers(null, null, "test-logon", true, true)).
            thenThrow(new TokenExpiredException()).thenReturn(response);

        mockRefreshToken();

        assertEquals(Optional.of(u).get().getId(),
                target.getUserByLogon(u.getLogon()).get().getId());
    }

    @Test
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

        when(rc.getUsers(null, null, "test-logon", true, true)).
                thenReturn(response);

        boolean success = false;
        try{
            target.getUserByLogon("test-logon");
        } catch(Exception e) {
            success = true;
        }

        assertTrue(success);
    }

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

        when(rc.getUsers(null, null, "test-logon", true, true)).
                thenReturn(response);

        assertEquals(target.getUserByLogon("test-logon"), Optional.empty());
    }

    @Test
    public void testGetUserByLogonWithSecondTokenExpiredException() throws Exception {
        Long id = 1L;

        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);

        when(resourceClient.getUsers(null, null, "test-logon", true, true)).
                thenThrow(new TokenExpiredException()).
                thenThrow(new TokenExpiredException());

        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        mockRefreshToken();

        assertThrows(SystemException.class, () -> target.getUserByLogon("test-logon"));
    }

    @Test
    public void testCreate() throws MalformedURLException, SystemException, TokenExpiredException {
        User u = new User();
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(resourceClient.save(u)).thenReturn(Response.ok().build());
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);

        boolean success = false;
        try {
			assertTrue(target.create(u,true));
		} catch (SystemException e) {
			success = true;
        }
        assertFalse(success);
    }

    @Test
    public void testCreateFail() throws MalformedURLException, SystemException , TokenExpiredException{
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        User u = new User();
        when(resourceClient.save(u)).thenReturn(Response.serverError().entity("test error msg").build());
        when(clientServiceUtil.getUserResourceClient(any())).thenReturn(resourceClient);

        boolean success = false;
        try {
			assertFalse(target.create(u,true));
		} catch (SystemException e) {
			 success = true;
        }
        assertFalse(success);

    }

    @Test
    public void testCreateProcessingException() throws MalformedURLException , TokenExpiredException{
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        User u = new User();
        when(resourceClient.save(u)).thenThrow(new ProcessingException(""));
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        boolean success = false;
        try {
            target.create(u,true);
        }catch (SystemException se){
            if (se.getMessage().contains(ProcessingException.class.getName())) {
                success = true;
            }
        }
        assertTrue(success);
    }

  /*  @Test
    public void testGetUserListStatusResponse() throws MalformedURLException , TokenExpiredException{
        List<? extends SystemUser> list = new ArrayList<>();
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(resourceClient.getUserList()).thenReturn(Response.ok(list).build());
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        Response response = resourceClient.getUserList();
        assertEquals(200,response.getStatus());
    }

    @Test
    public void testGetUserListFailStatusResponse() throws MalformedURLException , TokenExpiredException{
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(resourceClient.getUserList()).thenReturn(Response.serverError().entity("test error msg").build());
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        Response response = resourceClient.getUserList();
        assertEquals(500,response.getStatus());
    }
*/
    @Test
    public void testSetInitiateResetPassword() throws MalformedURLException , TokenExpiredException{
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

    @Test
    public void testSetInitiateResetPasswordFail() throws MalformedURLException , TokenExpiredException{
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(resourceClient.sendUpdatePasswordEmail(dummyUser.getId())).thenReturn(Response.serverError().entity("test error msg").build());
        when(clientServiceUtil.getUserResourceClient(any())).thenReturn(resourceClient);

        boolean success = false;
        try {
            assertFalse(target.sendUpdatePasswordEmail(dummyUser.getId()));
        } catch (Exception e) {
            success = true;
        }
        assertFalse(success);
    }

    @Test
    public void testUpdatePasswordEmailMalformedException() throws MalformedURLException , TokenExpiredException{
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenThrow(new MalformedURLException());
        assertFalse(target.sendUpdatePasswordEmail(dummyUser.getId()));
    }

    @Test
    public void testDeleteMalformedException() throws MalformedURLException , TokenExpiredException{
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenThrow(new MalformedURLException());
        assertFalse(target.deleteUser(dummyUser.getId()));
    }

    @Test
    public void testUpdateUserMalformedException() throws MalformedURLException , TokenExpiredException{
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenThrow(new MalformedURLException());
        assertFalse(target.updateUser(dummyUser));
    }

    @Test
    public void testUpdateUser() throws MalformedURLException , TokenExpiredException{
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

    @Test
    public void testUpdateUserFail() throws MalformedURLException , TokenExpiredException{
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(resourceClient.save(dummyUser)).thenReturn(Response.serverError().entity("test error msg").build());
        when(clientServiceUtil.getUserResourceClient(any())).thenReturn(resourceClient);
        dummyUser.setLastname("lastname-updated");
        boolean success = false;
        try {
            assertFalse(target.updateUser(dummyUser));
        } catch (Exception e) {
            success = true;
        }
        assertFalse(success);
    }

    @Test
    public void testDeleteUser() throws MalformedURLException , TokenExpiredException{

        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(resourceClient.delete(dummyUser.getId())).thenReturn(Response.ok().build());
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);

        boolean success = false;
        try {
            assertTrue(target.deleteUser(1L));
        } catch (Exception e) {
            success = true;
        }
        assertFalse(success);
    }

    @Test
    public void testDeleteUserFail() throws MalformedURLException , TokenExpiredException{
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(resourceClient.delete(dummyUser.getId())).thenReturn(Response.serverError().entity("test error msg").build());
        when(clientServiceUtil.getUserResourceClient(any())).thenReturn(resourceClient);
        boolean success = false;
        try {
            assertFalse(target.deleteUser(1L));
        } catch (Exception e) {
            success = true;
        }
        assertFalse(success);
    }

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
        assertEquals(opt.get().getInternalStatus(), BatchSummary.ProcessingStatus.SUCCESS);
    }

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
        assertEquals(opt.get().getTotalProcessed(), 0);
        assertEquals(opt.get().getTotalNonProcessed(), rows);
        assertEquals(opt.get().getInternalStatus(), BatchSummary.ProcessingStatus.FAIL);
    }

    @Test
    public void testCreateBatchSomeNotInserted() throws MalformedURLException , TokenExpiredException{
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
        assertEquals(opt.get().getTotalProcessed(), 6);
        assertEquals(opt.get().getNonProcessedItems().size(), 4);
        assertEquals(opt.get().getInternalStatus(), BatchSummary.ProcessingStatus.PARTIAL_SUCCESS);
    }


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
}