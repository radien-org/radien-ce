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
package io.radien.ms.rolemanagement.client.services;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.model.role.SystemRole;
import io.radien.api.security.TokensPlaceHolder;
import io.radien.api.util.FactoryUtilService;
import io.radien.exception.SystemException;
import io.radien.exception.TokenExpiredException;
import io.radien.ms.authz.client.UserClient;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.rolemanagement.client.entities.Role;
import io.radien.ms.rolemanagement.client.util.ClientServiceUtil;
import io.radien.ms.rolemanagement.client.util.RoleModelMapper;
import org.apache.cxf.bus.extension.ExtensionException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

/**
 * @author Bruno Gama
 */
public class RoleRESTServiceClientTest {

    @InjectMocks
    RoleRESTServiceClient target;

    @Mock
    ClientServiceUtil roleServiceUtil;

    @Mock
    OAFAccess oafAccess;

    @Mock
    AuthorizationChecker authorizationChecker;

    @Mock
    UserClient userClient;

    @Mock
    TokensPlaceHolder tokensPlaceHolder;

    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetAll() throws MalformedURLException, SystemException {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        FactoryUtilService.addValueInt(builder, "currentPage", 1);
        FactoryUtilService.addValueInt(builder, "totalPages", 1);
        FactoryUtilService.addValueInt(builder, "totalResults", 1);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonWriter jsonWriter = Json.createWriter(baos);
        jsonWriter.writeObject(builder.build());
        jsonWriter.close();

        InputStream is = new ByteArrayInputStream(baos.toByteArray());

        Response response = Response.ok(is).build();

        RoleResourceClient roleResourceClient = Mockito.mock(RoleResourceClient.class);

        when(roleResourceClient.getAll(null,1, 10,  null, false)).thenReturn(response);

        when(roleServiceUtil.getRoleResourceClient(getPermissionManagementUrl())).thenReturn(roleResourceClient);

        List<? extends SystemRole> list = new ArrayList<>();

        List<? extends SystemRole> returnedList = target.getAll(null, 1, 10, null, false).getResults();

        assertEquals(list, returnedList);
    }

    @Test(expected = SystemException.class)
    public void testGetContractByNameTokenExpiration() throws Exception {
        RoleResourceClient roleResourceClient = Mockito.mock(RoleResourceClient.class);

        when(roleServiceUtil.getRoleResourceClient(getPermissionManagementUrl())).thenReturn(roleResourceClient);
        when(roleResourceClient.getAll(anyString(), anyInt(), anyInt(), anyList(), anyBoolean())).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        List<String> sortBy = new ArrayList<>();
        target.getAll("a", 1, 10, sortBy, false);
    }

    @Test
    public void testGetAllException() throws Exception {
        boolean success = false;
        when(roleServiceUtil.getRoleResourceClient(getPermissionManagementUrl())).thenThrow(new MalformedURLException());
        try {
            target.getAll(null,1, 10, null, false);
        }catch (SystemException se){
            success = true;
        }
        assertTrue(success);
    }

    @Test
    public void testGetRolesByDescription() throws MalformedURLException, SystemException {
        String a = "a";

        JsonArrayBuilder builder = Json.createArrayBuilder();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonWriter jsonWriter = Json.createWriter(baos);
        jsonWriter.writeArray(builder.build());
        jsonWriter.close();

        InputStream is = new ByteArrayInputStream(baos.toByteArray());

        Response response = Response.ok(is).build();

        RoleResourceClient roleResourceClient = Mockito.mock(RoleResourceClient.class);

        when(roleResourceClient.getSpecificRoles(any(), any(), any(), anyBoolean(), anyBoolean())).thenReturn(response);

        when(roleServiceUtil.getRoleResourceClient(getPermissionManagementUrl())).thenReturn(roleResourceClient);

        List<? extends SystemRole> list = new ArrayList<>();

        assertEquals(list, target.getRolesByDescription(a));
    }

    @Test
    public void testGetRolesByDescriptionException() throws Exception {
        boolean success = false;
        when(roleServiceUtil.getRoleResourceClient(getPermissionManagementUrl())).thenThrow(new ExtensionException(new Exception()));
        try {
            target.getRolesByDescription("A");
        }catch (SystemException se){
            if (se.getMessage().contains(ExtensionException.class.getName())) {
                success = true;
            }
        }
        assertTrue(success);
    }

    @Test
    public void testGetRolesByName() throws MalformedURLException, SystemException {
        Role role = new Role();
        role.setId(1L);
        role.setName("admin-test");
        role.setDescription("test");

        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        arrayBuilder.add(RoleModelMapper.map(role));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JsonWriter jsonWriter = Json.createWriter(outputStream);
        jsonWriter.writeArray(arrayBuilder.build());
        jsonWriter.close();

        InputStream is = new ByteArrayInputStream(outputStream.toByteArray());

        Response response = Response.ok(is).build();

        RoleResourceClient roleResourceClient = Mockito.mock(RoleResourceClient.class);

        when(roleResourceClient.getSpecificRoles(role.getName(), null, null,true, true)).
                thenReturn(response);

        when(roleServiceUtil.getRoleResourceClient(getPermissionManagementUrl())).thenReturn(roleResourceClient);

        assertEquals(target.getRoleByName(role.getName()).get().getId(), role.getId());
    }

    @Test
    public void testGetRolesByNameFoundNothing() throws MalformedURLException, SystemException {
        String roleName = "admin-test";

        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JsonWriter jsonWriter = Json.createWriter(outputStream);
        jsonWriter.writeArray(arrayBuilder.build());
        jsonWriter.close();

        InputStream is = new ByteArrayInputStream(outputStream.toByteArray());
        Response response = Response.ok(is).build();

        RoleResourceClient roleResourceClient = Mockito.mock(RoleResourceClient.class);

        when(roleResourceClient.getSpecificRoles(roleName, null, null, true, true)).
                thenReturn(response);

        when(roleServiceUtil.getRoleResourceClient(getPermissionManagementUrl())).thenReturn(roleResourceClient);

        assertEquals(target.getRoleByName(roleName), Optional.empty());
    }

    @Test
    public void testGetRolesByNameException() throws Exception {
        String roleName = "admin-test";
        boolean success = false;
        RoleResourceClient roleResourceClient = Mockito.mock(RoleResourceClient.class);
        when(roleServiceUtil.getRoleResourceClient(getPermissionManagementUrl())).
                thenThrow(new MalformedURLException("error")).
                thenReturn(roleResourceClient).
                thenReturn(roleResourceClient);
        when(roleResourceClient.getSpecificRoles(roleName, null, null,true, true)).
                thenThrow(new ExtensionException(new Exception("test"))).
                thenThrow(new ProcessingException(new Exception("test")));
        try {
            target.getRoleByName(roleName);
        }catch (SystemException se){
            if (se.getMessage().contains(MalformedURLException.class.getName())) {
                success = true;
            }
        }
        assertTrue(success);

        success = false;
        try {
            target.getRoleByName(roleName);
        }catch (SystemException se){
            if (se.getMessage().contains(ExtensionException.class.getName())) {
                success = true;
            }
        }
        assertTrue(success);

        success = false;
        try {
            target.getRoleByName(roleName);
        }catch (SystemException se){
            if (se.getMessage().contains(ProcessingException.class.getName())) {
                success = true;
            }
        }
        assertTrue(success);
    }


    private String getPermissionManagementUrl(){
        String url = "";
        when(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT)).thenReturn(url);
        return url;
    }

    @Test
    public void testCreate() throws MalformedURLException {
        RoleResourceClient resourceClient = Mockito.mock(RoleResourceClient.class);
        when(resourceClient.save(any())).thenReturn(Response.ok().build());
        when(roleServiceUtil.getRoleResourceClient(getPermissionManagementUrl())).thenReturn(resourceClient);
        boolean success = false;
        try {
			assertTrue(target.create(new Role()));
		} catch (SystemException e) {
			success = true;
        }
        assertFalse(success);
    }

    @Test
    public void testCreateMalformedException() throws MalformedURLException {
        when(roleServiceUtil.getRoleResourceClient(getPermissionManagementUrl())).thenThrow(new MalformedURLException());
        boolean success = false;
        try {
            assertTrue(target.create(new Role()));
        } catch(SystemException e) {
            success = true;
        }
        assertTrue(success);
    }

    @Test
    public void testCreateFail() throws MalformedURLException {
        RoleResourceClient roleResourceClient = Mockito.mock(RoleResourceClient.class);
        when(roleResourceClient.save(any())).thenReturn(Response.serverError().entity("test error msg").build());
        when(roleServiceUtil.getRoleResourceClient(getPermissionManagementUrl())).thenReturn(roleResourceClient);
        boolean success = false;
        try {
            assertFalse(target.create(new Role()));
        } catch (SystemException e) {
            success = true;
        }
        assertFalse(success);
    }

    @Test
    public void testCreateProcessingException() throws MalformedURLException {
        RoleResourceClient roleResourceClient = Mockito.mock(RoleResourceClient.class);
        when(roleResourceClient.save(any())).thenThrow(new ProcessingException(""));
        when(roleServiceUtil.getRoleResourceClient(getPermissionManagementUrl())).thenReturn(roleResourceClient);
        boolean success = false;
        try {
            target.create(new Role());
        }catch (ProcessingException | SystemException es){
            success = true;
        }
        assertTrue(success);

    }

    @Test(expected = SystemException.class)
    public void testGetRoleByIdTokenExpiration() throws Exception {
        RoleResourceClient roleResourceClient = Mockito.mock(RoleResourceClient.class);

        when(roleServiceUtil.getRoleResourceClient(getPermissionManagementUrl())).thenReturn(roleResourceClient);
        when(roleResourceClient.getById(anyLong())).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getRoleById(2L);
    }

    @Test(expected = SystemException.class)
    public void testGetRoleByNameTokenExpiration() throws Exception {
        RoleResourceClient roleResourceClient = Mockito.mock(RoleResourceClient.class);

        when(roleServiceUtil.getRoleResourceClient(getPermissionManagementUrl())).thenReturn(roleResourceClient);
        when(roleResourceClient.getSpecificRoles(anyString(), any(), any(), anyBoolean(), anyBoolean())).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getRoleByName("name");
    }

    @Test(expected = SystemException.class)
    public void testGetRoleByDescriptionTokenExpiration() throws Exception {
        RoleResourceClient roleResourceClient = Mockito.mock(RoleResourceClient.class);

        when(roleServiceUtil.getRoleResourceClient(getPermissionManagementUrl())).thenReturn(roleResourceClient);
        when(roleResourceClient.getSpecificRoles(any(), anyString(), any(), anyBoolean(), anyBoolean())).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getRolesByDescription("description");
    }

    @Test(expected = SystemException.class)
    public void testCreateTokenExpiration() throws Exception {
        RoleResourceClient roleResourceClient = Mockito.mock(RoleResourceClient.class);

        when(roleServiceUtil.getRoleResourceClient(getPermissionManagementUrl())).thenReturn(roleResourceClient);
        when(roleResourceClient.save(any())).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        SystemRole role = RoleFactory.create("name", "description", 2L);
        target.create(role);
    }

    @Test
    public void testGetTenantById() throws MalformedURLException, SystemException {
        Role dummyRole = new Role();
        dummyRole.setId(3L);
        dummyRole.setName("name");
        dummyRole.setDescription("description");

        InputStream is = new ByteArrayInputStream(RoleModelMapper.map(dummyRole).toString().getBytes());
        Response response = Response.ok(is).build();
        RoleResourceClient roleResourceClient = Mockito.mock(RoleResourceClient.class);
        when(roleServiceUtil.getRoleResourceClient(getPermissionManagementUrl())).thenReturn(roleResourceClient);
        when(roleResourceClient.getById(anyLong())).thenReturn(response);

        Optional<SystemRole> opt = target.getRoleById(dummyRole.getId());
        assertNotNull(opt);
        assertTrue(opt.isPresent());
        assertEquals(opt.get().getId(), dummyRole.getId());
    }

    @Test
    public void testGetRoleByIdException() throws Exception {
        boolean success = false;
        when(roleServiceUtil.getRoleResourceClient(getPermissionManagementUrl())).thenThrow(new MalformedURLException());
        try {
            target.getRoleById(3L);
        }catch (Exception se){
            success = true;
        }
        assertTrue(success);
    }

    /**
     * Test for method getRolesByIds(List ids)
     * It corresponds to the successful situation when roles are retrieved
     * Expected result (SUCCESS): Roles retrieved
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void testGetRolesByIds() throws MalformedURLException, SystemException {
        Role t1 = new Role();
        t1.setId(1L);

        Role t2 = new Role();
        t2.setId(2L);

        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        jsonArrayBuilder.add(RoleModelMapper.map(t1));
        jsonArrayBuilder.add(RoleModelMapper.map(t2));
        JsonArray jsonArray = jsonArrayBuilder.build();

        List<Long> ids = Arrays.asList(t1.getId(), t2.getId());

        InputStream is = new ByteArrayInputStream(jsonArray.toString().getBytes());
        Response response = Response.ok(is).build();

        RoleResourceClient resourceClient = Mockito.mock(RoleResourceClient.class);
        when(roleServiceUtil.getRoleResourceClient(getPermissionManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.getSpecificRoles(null, null, ids, true, true)).thenReturn(response);

        List<? extends SystemRole> outcome = target.getRolesByIds(ids);
        assertNotNull(outcome);
        assertFalse(outcome.isEmpty());
        assertEquals(2, outcome.size());
    }

    /**
     * Test for method getRolesByIds(List ids)
     * It corresponds to the unsuccessful situation where exception occurs
     * Expected result (FAIL): SystemException thrown
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testGetRolesByIdsException() throws MalformedURLException, SystemException {
        when(roleServiceUtil.getRoleResourceClient(getPermissionManagementUrl())).thenThrow(new MalformedURLException());
        target.getRolesByIds(Arrays.asList(3L, 4L));
    }

    /**
     * Test for method getRolesByIds(List ids)
     * It corresponds to the unsuccessful situation where JWT expiration occurs and
     * is not possible to recover from that
     * Expected result (FAIL): SystemException thrown
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testGetRolesByIdsTokenExpiration() throws MalformedURLException, SystemException {
        List<Long> ids = Arrays.asList(1L, 2L);
        RoleResourceClient resourceClient = Mockito.mock(RoleResourceClient.class);
        when(roleServiceUtil.getRoleResourceClient(getPermissionManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.getSpecificRoles(null, null, ids, true, true)).
                thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getRolesByIds(ids);
    }

    /**
     * Test for method getRolesByIds(List ids)
     * It corresponds to the the case where is not possible to find Roles
     * Expected result (SUCCESS): Empty list. No Roles found for the informed ids
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void testGetRolesByIdsNoResults() throws MalformedURLException, SystemException {
        List<Long> ids = Arrays.asList(1L, 2L);
        InputStream is = new ByteArrayInputStream("[]".getBytes());
        Response response = Response.ok(is).build();

        RoleResourceClient resourceClient = Mockito.mock(RoleResourceClient.class);
        when(roleServiceUtil.getRoleResourceClient(getPermissionManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.getSpecificRoles(null, null, ids, true, true)).
                thenReturn(response);

        List<? extends SystemRole> outcome = target.getRolesByIds(ids);
        assertNotNull(outcome);
        assertTrue(outcome.isEmpty());
    }

    @Test
    public void testDelete() throws MalformedURLException, SystemException {
        RoleResourceClient resourceClient = Mockito.mock(RoleResourceClient.class);
        when(resourceClient.delete(anyLong())).thenReturn(Response.ok().build());
        when(roleServiceUtil.getRoleResourceClient(getPermissionManagementUrl())).thenReturn(resourceClient);
        assertTrue(target.delete(2L));
    }

    @Test(expected = SystemException.class)
    public void testDeleteMalformedException() throws MalformedURLException, SystemException {
        when(roleServiceUtil.getRoleResourceClient(getPermissionManagementUrl())).thenThrow(new MalformedURLException());
        target.delete(2L);
    }

    @Test
    public void testDeleteFail() throws MalformedURLException, SystemException {
        RoleResourceClient roleResourceClient = Mockito.mock(RoleResourceClient.class);
        when(roleResourceClient.delete(anyLong())).thenReturn(Response.serverError().entity("test error msg").build());
        when(roleServiceUtil.getRoleResourceClient(getPermissionManagementUrl())).thenReturn(roleResourceClient);
        assertFalse(target.delete(2L));
    }

    @Test(expected = SystemException.class)
    public void testDeleteProcessingException() throws MalformedURLException, SystemException {
        RoleResourceClient roleResourceClient = Mockito.mock(RoleResourceClient.class);
        when(roleResourceClient.delete(anyLong())).thenThrow(new ProcessingException(""));
        when(roleServiceUtil.getRoleResourceClient(getPermissionManagementUrl())).thenReturn(roleResourceClient);
        target.delete(2L);
    }

    @Test(expected = SystemException.class)
    public void testDeleteTokenExpiration() throws Exception {
        RoleResourceClient roleResourceClient = Mockito.mock(RoleResourceClient.class);

        when(roleServiceUtil.getRoleResourceClient(getPermissionManagementUrl())).thenReturn(roleResourceClient);
        when(roleResourceClient.delete(anyLong())).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.delete(2L);
    }
}