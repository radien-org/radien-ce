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
import io.radien.api.entity.Page;
import io.radien.api.model.permission.SystemPermission;
import io.radien.api.model.tenantrole.SystemTenantRolePermission;
import io.radien.api.security.TokensPlaceHolder;
import io.radien.api.util.FactoryUtilService;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.NotFoundException;
import io.radien.exception.SystemException;
import io.radien.exception.TokenExpiredException;
import io.radien.ms.authz.client.UserClient;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.rolemanagement.client.entities.TenantRole;
import io.radien.ms.rolemanagement.client.entities.TenantRolePermission;
import io.radien.exception.InternalServerErrorException;
import io.radien.ms.rolemanagement.client.util.ClientServiceUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tenant Role User REST Service client requests tests
 *
 * @author Newton Carvalho
 */
public class TenantRolePermissionRESTServiceClientTest {

    @InjectMocks
    TenantRolePermissionRESTServiceClient target;

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

    /**
     * Constructor method to prepare all the variables and properties before running the tests
     */
    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Private method to create the role management URL endpoint
     * @return the role management url endpoint
     */
    private String getRoleManagementUrl(){
        String url = "";
        when(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT)).thenReturn(url);
        return url;
    }


    /**
     * Test the assignment of a permission to the association but with token expiration
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue       
     */
    @Test(expected = SystemException.class)
    public void testAssignPermissionTokenExpiration() throws MalformedURLException, SystemException {
        TenantRolePermissionResourceClient client = Mockito.mock(TenantRolePermissionResourceClient.class);

        TenantRolePermission tenantRolePermission = mock(TenantRolePermission.class);
        when(roleServiceUtil.getTenantRolePermissionResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.assignPermission(tenantRolePermission)).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.assignPermission(tenantRolePermission);
    }

    /**
     * Test the assignment of a permission to the association but with exception to be throw
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue       
     */
    @Test(expected = SystemException.class)
    public void testAssignPermissionException() throws MalformedURLException, SystemException {
        TenantRolePermissionResourceClient client = Mockito.mock(TenantRolePermissionResourceClient.class);

        TenantRolePermission tenantRolePermission = mock(TenantRolePermission.class);
        when(roleServiceUtil.getTenantRolePermissionResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.assignPermission(tenantRolePermission)).thenThrow(new ProcessingException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.assignPermission(tenantRolePermission);
    }

    /**
     * Test the assignment of a permission to the association
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue       
     */
    @Test
    public void testAssignPermission() throws MalformedURLException, SystemException {
        TenantRolePermissionResourceClient client = Mockito.mock(TenantRolePermissionResourceClient.class);
        TenantRolePermission tenantRolePermission = mock(TenantRolePermission.class);
        when(client.assignPermission(tenantRolePermission)).
                thenReturn(Response.ok().build()).
                thenReturn(Response.status(300).build());

        when(roleServiceUtil.getTenantRolePermissionResourceClient(getRoleManagementUrl())).
                thenReturn(client);

        Boolean result = target.assignPermission(tenantRolePermission);
        assertNotNull(result);
        assertTrue(result);

        result = target.assignPermission(tenantRolePermission);
        assertNotNull(result);
        assertFalse(result);
    }

    /**
     * Test the un-assignment of a permission to the association
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue       
     */
    @Test
    public void testUnAssignPermission() throws MalformedURLException, SystemException {
        TenantRolePermissionResourceClient client = Mockito.mock(TenantRolePermissionResourceClient.class);

        when(client.unAssignPermission(1L, 2L, 3L)).
                thenReturn(Response.ok(Boolean.TRUE).build());
        when(client.unAssignPermission(1L, 2L, 4L)).
                thenReturn(Response.status(303).build());

        when(roleServiceUtil.getTenantRolePermissionResourceClient(getRoleManagementUrl())).
                thenReturn(client);

        Boolean result = target.unAssignPermission(1L, 2L, 3L);
        assertNotNull(result);
        assertTrue(result);

        result = target.unAssignPermission(1L, 2L, 4L);
        assertNotNull(result);
        assertFalse(result);
    }

    /**
     * Test the un-assignment of a permission to the association but with token expired
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue       
     */
    @Test
    public void testUnAssignPermissionTokenExpiration() throws MalformedURLException, SystemException {
        TenantRolePermissionResourceClient client = Mockito.mock(TenantRolePermissionResourceClient.class);
        String msg = "error performing renewing token";
        when(roleServiceUtil.getTenantRolePermissionResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.unAssignPermission(1L, 2L, 3L)).
                thenThrow(new TokenExpiredException(msg)).
                thenReturn(Response.ok().build());

        when(client.unAssignPermission(2L, 2L, 3L)).
                thenThrow(new TokenExpiredException(msg)).
                thenThrow(new TokenExpiredException(msg));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        assertTrue(target.unAssignPermission(1L, 2L, 3L));
        SystemException se = assertThrows(SystemException.class, () -> target.unAssignPermission(2L, 2L, 3L));
        assertEquals(1, se.getMessages().size());
        assertEquals(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString(), se.getMessages().get(0));
    }

    /**
     * Test the un-assignment of a permission to the association but with exception being throw
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue       
     */
    @Test(expected = SystemException.class)
    public void testUnAssignPermissionException() throws MalformedURLException, SystemException {
        TenantRolePermissionResourceClient client = Mockito.mock(TenantRolePermissionResourceClient.class);

        when(roleServiceUtil.getTenantRolePermissionResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.unAssignPermission(1L, 2L, 3L)).thenThrow(new ProcessingException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.unAssignPermission(1L, 2L, 3L);
    }

    /**
     * Test the un-assignment of a permission to the association
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void testUnAssignPermissionBasedOnSingleId() throws MalformedURLException, SystemException {
        TenantRolePermissionResourceClient client = Mockito.mock(TenantRolePermissionResourceClient.class);

        when(client.delete(1L)).
                thenReturn(Response.ok(Boolean.TRUE).build()).
                thenReturn(Response.status(303).build());

        when(roleServiceUtil.getTenantRolePermissionResourceClient(getRoleManagementUrl())).
                thenReturn(client);

        Boolean result = target.unAssignPermission(1L);
        assertNotNull(result);
        assertTrue(result);

        result = target.unAssignPermission(1L);
        assertNotNull(result);
        assertFalse(result);
    }

    /**
     * Test the un-assignment of a permission to the association but with token expired
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void testUnAssignPermissionBasedOnSingleIdWithTokenExpiration() throws MalformedURLException, SystemException {
        TenantRolePermissionResourceClient client = Mockito.mock(TenantRolePermissionResourceClient.class);
        String msg = "test";
        when(roleServiceUtil.getTenantRolePermissionResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.delete(1L)).thenThrow(new TokenExpiredException(msg)).
                thenReturn(Response.ok().build());

        when(client.delete(2L)).
                thenThrow(new TokenExpiredException(msg)).
                thenThrow(new TokenExpiredException(msg));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        assertTrue(target.unAssignPermission(1L));
        SystemException se = assertThrows(SystemException.class, () -> target.unAssignPermission(2L));
        assertEquals(1, se.getMessages().size());
        assertEquals(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString(), se.getMessages().get(0));
    }

    /**
     * Test the un-assignment of a permission to the association but with exception being throw
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testUnAssignPermissionBasedOnSingleIdWithException() throws MalformedURLException, SystemException {
        TenantRolePermissionResourceClient client = Mockito.mock(TenantRolePermissionResourceClient.class);

        when(roleServiceUtil.getTenantRolePermissionResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.delete(1L)).thenThrow(new ProcessingException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.unAssignPermission(1L);
    }

    /**
     * Test for method that retrieves TenantRolePermission associations using pagination approach
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void testGetAll() throws MalformedURLException, SystemException{
        String results = "[{\"id\": 1, \"tenantRoleId\": 2, \"permissionId\":3}, " +
                "{\"id\": 1, \"tenantRoleId\": 2, \"permissionId\": 4}, " +
                "{\"id\": 1, \"tenantRoleId\": 2, \"permissionId\": 10}]";

        String pageAsJsonString = "{\"currentPage\": 1, \"totalResults\": 3, \"totalPages\": 1, " +
                "\"results\": " + results + "}";

        InputStream is = new ByteArrayInputStream(pageAsJsonString.getBytes());
        Response response = Response.ok(is).build();
        TenantRolePermissionResourceClient client = Mockito.mock(TenantRolePermissionResourceClient.class);

        when(client.getAll(2L, 3L, 1, 3, null, false)).thenReturn(response);
        when(roleServiceUtil.getTenantRolePermissionResourceClient(getRoleManagementUrl())).
                thenReturn(client);

        Page<? extends SystemTenantRolePermission> result = target.getAll(2L, 3L, 1, 3, null, false);
        assertNotNull(result);
        assertEquals(1, result.getTotalPages());
        assertEquals(3,result.getResults().size());
    }

    /**
     * Test for method that retrieves TenantRolePermission associations using pagination approach,
     * but taking in consideration a scenario where JasonWeb token expires
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testGetAllTokenExpiration() throws MalformedURLException, SystemException {
        TenantRolePermissionResourceClient client = Mockito.mock(TenantRolePermissionResourceClient.class);

        // Simulates JWT expire even on the reattempt
        when(roleServiceUtil.getTenantRolePermissionResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.getAll(33L, 44L, 1, 2, null, false)).
                thenThrow(new TokenExpiredException("test")).
                thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getAll(33L, 44L, 1, 2, null, false);
    }

    /**
     * Test for method that retrieves TenantRolePermission associations using pagination approach,
     * but taking in consideration a scenario where error occurs in the middle of processing
     * (Backend error)
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testGetAllException() throws MalformedURLException, SystemException {
        TenantRolePermissionResourceClient client = Mockito.mock(TenantRolePermissionResourceClient.class);

        when(roleServiceUtil.getTenantRolePermissionResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.getAll(33L, 44L, 1, 2, null, false)).thenThrow(new InternalServerErrorException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getAll(33L, 44L, 1, 2, null, false);
    }

    /**
     * Test for method that retrieves TenantRolePermission associations
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void testGetTenantRolePermissions() throws MalformedURLException, SystemException{
        String results = "[{\"id\": 1, \"tenantRoleId\": 2, \"permissionId\":3}, " +
                "{\"id\": 1, \"tenantRoleId\": 2, \"permissionId\": 4}, " +
                "{\"id\": 1, \"tenantRoleId\": 2, \"permissionId\": 10}]";

        InputStream is = new ByteArrayInputStream(results.getBytes());
        Response response = Response.ok(is).build();
        TenantRolePermissionResourceClient client = Mockito.mock(TenantRolePermissionResourceClient.class);

        when(client.getSpecific(2L, 3L, false)).thenReturn(response);
        when(roleServiceUtil.getTenantRolePermissionResourceClient(getRoleManagementUrl())).
                thenReturn(client);

        List<? extends SystemTenantRolePermission> result = target.getTenantRolePermissions(
                2L, 3L, false);
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    /**
     * Test for method that retrieves TenantRolePermission associations,
     * but taking in consideration a scenario where JasonWeb token expires
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testGetTenantRolePermissionsTokenExpiration() throws MalformedURLException, SystemException {
        TenantRolePermissionResourceClient client = Mockito.mock(TenantRolePermissionResourceClient.class);

        // Simulates JWT expire even on the reattempt
        when(roleServiceUtil.getTenantRolePermissionResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.getSpecific(33L, 44L, false)).
                thenThrow(new TokenExpiredException("test")).
                thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getTenantRolePermissions(33L, 44L, false);
    }

    /**
     * Test for method that retrieves TenantRolePermission associations,
     * but taking in consideration a scenario where error occurs in the middle of processing
     * (Backend error)
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testGetTenantRolePermissionsException() throws MalformedURLException, SystemException {
        TenantRolePermissionResourceClient client = Mockito.mock(TenantRolePermissionResourceClient.class);

        when(roleServiceUtil.getTenantRolePermissionResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.getSpecific(33L, 44L, false)).
                thenThrow(new InternalServerErrorException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getTenantRolePermissions(33L, 44L, false);
    }

    /**
     * Test for method that retrieves TenantRolePermission associations,
     * but taking in consideration a scenario where error occurs in the middle of processing
     * (Backend error)
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testGetTenantRolePermissionsMalformedURLException() throws MalformedURLException, SystemException {
        when(roleServiceUtil.getTenantRolePermissionResourceClient(getRoleManagementUrl())).
                thenThrow(new MalformedURLException("invalid url"));
        target.getTenantRolePermissions(33L, 44L, false);
    }

    /**
     * Test the updating of a TenantRolePermission but with token expiration
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testUpdateTenantRolePermissionTokenExpiration() throws MalformedURLException, SystemException {
        TenantRolePermissionResourceClient client = Mockito.mock(TenantRolePermissionResourceClient.class);

        long id = 1L;
        TenantRolePermission tenantRolePermission = mock(TenantRolePermission.class);
        when(tenantRolePermission.getId()).thenReturn(id);
        when(roleServiceUtil.getTenantRolePermissionResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.update(id, tenantRolePermission)).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.update(tenantRolePermission);
    }

    /**
     * Test the updating of a TenantRolePermission but with exception to be throw
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testUpdateTenantRolePermissionWithException() throws MalformedURLException, SystemException {
        TenantRolePermissionResourceClient client = Mockito.mock(TenantRolePermissionResourceClient.class);

        TenantRolePermission tenantRolePermission = mock(TenantRolePermission.class);
        when(tenantRolePermission.getId()).thenReturn(1L);
        when(roleServiceUtil.getTenantRolePermissionResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.update(tenantRolePermission.getId(), tenantRolePermission)).thenThrow(new ProcessingException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.update(tenantRolePermission);
    }

    /**
     * Test the updating for a previously created TenantRolePermission
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void testUpdateTenantRolePermission() throws MalformedURLException, SystemException {
        TenantRolePermissionResourceClient client = Mockito.mock(TenantRolePermissionResourceClient.class);
        TenantRolePermission tenantRolePermission = mock(TenantRolePermission.class);
        long id = 1L;
        when(tenantRolePermission.getId()).thenReturn(id);
        when(client.update(id, tenantRolePermission)).
                thenReturn(Response.ok().build()).
                thenReturn(Response.status(300).build());

        when(roleServiceUtil.getTenantRolePermissionResourceClient(getRoleManagementUrl())).
                thenReturn(client);

        Boolean result = target.update(tenantRolePermission);
        assertNotNull(result);
        assertTrue(result);

        result = target.update(tenantRolePermission);
        assertNotNull(result);
        assertFalse(result);
    }


    /**
     * Test the get tenant role permission by a given specific id
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void testGetTenantRolePermissionById() throws MalformedURLException, SystemException {

        SystemTenantRolePermission trp = new TenantRolePermission();
        trp.setId(1L); trp.setTenantRoleId(2L); trp.setPermissionId(3L);

        JsonObjectBuilder builder = Json.createObjectBuilder();
        FactoryUtilService.addValueLong(builder, "id", trp.getId());
        FactoryUtilService.addValueLong(builder, "tenantRoleId", trp.getTenantRoleId());
        FactoryUtilService.addValueLong(builder, "permissionId", trp.getPermissionId());

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        JsonWriter jsonWriter = Json.createWriter(stream);
        jsonWriter.writeObject(builder.build());
        jsonWriter.close();

        InputStream is = new ByteArrayInputStream(stream.toByteArray());
        Response response = Response.ok(is).build();
        TenantRolePermissionResourceClient client = Mockito.mock(TenantRolePermissionResourceClient.class);

        long tenantRolePermissionId = 1L;
        when(client.getById(tenantRolePermissionId)).thenReturn(response);
        when(roleServiceUtil.getTenantRolePermissionResourceClient(getRoleManagementUrl())).
                thenReturn(client);

        Optional<SystemTenantRolePermission> result = target.
                getTenantRolePermissionById(tenantRolePermissionId);

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(result.get().getId(), trp.getId());
    }

    /**
     * Test the get association of a tenant role permission by id but this time with status not ok
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void testGetTenantRolePermissionByIdWithStatusNeqOK() throws MalformedURLException, SystemException {

        Response response = Response.status(300).build();
        TenantRolePermissionResourceClient client = mock(TenantRolePermissionResourceClient.class);

        long tenantRolePermissionId = 1l;

        when(client.getById(tenantRolePermissionId)).thenReturn(response);
        when(roleServiceUtil.getTenantRolePermissionResourceClient(getRoleManagementUrl())).
                thenReturn(client);

        Optional<SystemTenantRolePermission> result = target.
                getTenantRolePermissionById(tenantRolePermissionId);

        assertNotNull(result);
        assertFalse(result.isPresent());
    }

    /**
     * Test the get association of a tenant role permission by id but this time with
     * explicit status NOT FOUND
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void testGetTenantRolePermissionNotFound() throws MalformedURLException, SystemException {

        TenantRolePermissionResourceClient client = mock(TenantRolePermissionResourceClient.class);

        long tenantRolePermissionId = 1l;

        when(client.getById(tenantRolePermissionId)).thenThrow(new NotFoundException());
        when(roleServiceUtil.getTenantRolePermissionResourceClient(getRoleManagementUrl())).
                thenReturn(client);

        Optional<SystemTenantRolePermission> result = target.
                getTenantRolePermissionById(tenantRolePermissionId);

        assertNotNull(result);
        assertFalse(result.isPresent());
    }

    /**
     * Test the get tenant role permission by id but with token expiration
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testGetTenantRolePermissionByIdTokenExpiration() throws MalformedURLException, SystemException {
        TenantRolePermissionResourceClient client = Mockito.mock(TenantRolePermissionResourceClient.class);

        when(roleServiceUtil.getTenantRolePermissionResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.getById(1L)).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getTenantRolePermissionById(1L);
    }

    /**
     * Test the behaviour of getting the tenant role permission by id but with exception
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testGetTenantRoleByIdException() throws MalformedURLException, SystemException {
        TenantRolePermissionResourceClient client = mock(TenantRolePermissionResourceClient.class);

        when(roleServiceUtil.getTenantRolePermissionResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.getById(1L)).thenThrow(new ProcessingException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getTenantRolePermissionById(1L);
    }

    /**
     * Test to get permission for tenant Role associations
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void testGetAssignedPermissions() throws MalformedURLException, SystemException {

        String jsonArray = "[{\"id\": 1}, {\"id\": 2}]";
        InputStream is = new ByteArrayInputStream(jsonArray.getBytes());
        Response response = Response.ok(is).build();
        TenantRolePermissionResourceClient client = Mockito.mock(TenantRolePermissionResourceClient.class);

        when(client.getPermissions(1L, 2L, 3L)).thenReturn(response);
        when(roleServiceUtil.getTenantRolePermissionResourceClient(getRoleManagementUrl())).
                thenReturn(client);

        List<? extends SystemPermission> result = target.getPermissions(1L, 2L, 3L);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    /**
     * Test to get permission for tenant Role associations but with token expired
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testGetAssignedPermissionsTokenExpiration() throws MalformedURLException, SystemException {
        TenantRolePermissionResourceClient client = Mockito.mock(TenantRolePermissionResourceClient.class);

        when(roleServiceUtil.getTenantRolePermissionResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.getPermissions(1L, 2L, 3L)).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getPermissions(1L, 2L, 3L);
    }

    /**
     * Test to get permission for tenant Role associations but after token expiration (Retry schema)
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void testGetAssignedPermissionsAfterTokenExpiration() throws MalformedURLException, SystemException {
        TenantRolePermissionResourceClient client = Mockito.mock(TenantRolePermissionResourceClient.class);

        String jsonArray = "[{\"id\": 1}, {\"id\": 2}]";
        InputStream is = new ByteArrayInputStream(jsonArray.getBytes());
        Response response = Response.ok(is).build();

        when(roleServiceUtil.getTenantRolePermissionResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.getPermissions(1L, 2L, 3L)).thenThrow(new TokenExpiredException("test")).
                thenReturn(response);

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        assertNotNull(target.getPermissions(1L, 2L, 3L));
    }

    /**
     * Test to get permission for tenant Role associations  but with exception being throw
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testGetAssignedPermissionsException() throws MalformedURLException, SystemException {
        TenantRolePermissionResourceClient client = Mockito.mock(TenantRolePermissionResourceClient.class);

        when(roleServiceUtil.getTenantRolePermissionResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.getPermissions(1L, 2L, 3L)).thenThrow(new ProcessingException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getPermissions(1L, 2L, 3L);
    }

}
