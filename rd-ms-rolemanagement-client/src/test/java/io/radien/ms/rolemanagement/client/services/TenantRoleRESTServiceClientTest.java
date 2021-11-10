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
package io.radien.ms.rolemanagement.client.services;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.entity.Page;
import io.radien.api.model.permission.SystemPermission;
import io.radien.api.model.role.SystemRole;
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.model.tenantrole.SystemTenantRole;
import io.radien.api.security.TokensPlaceHolder;
import io.radien.api.util.FactoryUtilService;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.NotFoundException;
import io.radien.exception.SystemException;
import io.radien.exception.TokenExpiredException;
import io.radien.ms.authz.client.UserClient;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.rolemanagement.client.entities.TenantRole;
import io.radien.ms.rolemanagement.client.exception.InternalServerErrorException;
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
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Tenant Role REST Service client requests tests
 *
 * @author Newton Carvalho
 */
public class TenantRoleRESTServiceClientTest {

    @InjectMocks
    TenantRoleRESTServiceClient target;

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
     * Test the get tenant role by a given specific id
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue       
     */
    @Test
    public void testGetTenantRoleById() throws MalformedURLException, SystemException {

        SystemTenantRole str = new TenantRole();
        str.setId(1L); str.setTenantId(2L); str.setRoleId(3L);

        JsonObjectBuilder builder = Json.createObjectBuilder();
        FactoryUtilService.addValueInt(builder, "id", 1);
        FactoryUtilService.addValueInt(builder, "tenantId", 2);
        FactoryUtilService.addValueInt(builder, "roleId", 3);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        JsonWriter jsonWriter = Json.createWriter(stream);
        jsonWriter.writeObject(builder.build());
        jsonWriter.close();

        InputStream is = new ByteArrayInputStream(stream.toByteArray());
        Response response = Response.ok(is).build();
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(client.getById(1L)).thenReturn(response);
        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).
                thenReturn(client);

        Optional<SystemTenantRole> result = target.getTenantRoleById(1L);

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(result.get().getId(), str.getId());
    }

    /**
     * Test the get association of a tenant role by id but this time with status not ok
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */ 
    @Test
    public void testGetTenantRoleByIdWithStatusNeqOK() throws MalformedURLException, SystemException {

        Response response = Response.status(300).build();
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(client.getById(1L)).thenReturn(response);
        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).
                thenReturn(client);

        Optional<SystemTenantRole> result = target.getTenantRoleById(1L);

        assertNotNull(result);
        assertFalse(result.isPresent());
    }

    /**
     * Test the get tenant role by id but with token expiration
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue       
     */
    @Test(expected = SystemException.class)
    public void testGetTenantRoleByIdTokenExpiration() throws MalformedURLException, SystemException {
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.getById(1L)).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getTenantRoleById(1L);
    }

    /**
     * Test the behaviour of getting the tenant role by id but with exception
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue       
     */
    @Test(expected = SystemException.class)
    public void testGetTenantRoleByIdException() throws MalformedURLException, SystemException {
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.getById(1L)).thenThrow(new ProcessingException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getTenantRoleById(1L);
    }

    /**
     * Test the get id method for a given tenant and role identifier
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void testGetIdByTenantRole() throws MalformedURLException, SystemException {
        Response response = Response.ok(1L).build();
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(client.getIdByTenantRole(1L,1L)).thenReturn(response);
        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).
                thenReturn(client);

        Optional<Long> result = target.getIdByTenantRole(1L,1L);

        assertNotNull(result);
        assertTrue(result.isPresent());
    }

    /**
     * Test the get id method for a given tenant and role identifier
     * (But for a scenario/situation where the id could not be found)
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void testGetIdByTenantRoleWithNotFoundException() throws MalformedURLException, SystemException {
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(client.getIdByTenantRole(1L,1L)).thenThrow(new NotFoundException());
        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).
                thenReturn(client);

        Optional<Long> result = target.getIdByTenantRole(1L,1L);

        assertNotNull(result);
        assertFalse(result.isPresent());
    }

    /**
     * Test the get id method for a given tenant and role identifier
     * (But for a scenario/situation where token expired exception occurs)
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void testGetIdByTenantRoleWithTokenExpiredException() throws MalformedURLException, SystemException {

        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        when(client.getIdByTenantRole(1L,1L)).
                thenThrow(TokenExpiredException.class).
                thenReturn(Response.ok(1L).build()).
                thenThrow(TokenExpiredException.class).
                thenThrow(TokenExpiredException.class);

        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).
                thenReturn(client);

        Optional<Long> result = target.getIdByTenantRole(1L,1L);

        assertNotNull(result);
        assertTrue(result.isPresent());

        SystemException se = assertThrows(SystemException.class, ()-> target.getIdByTenantRole(1L,1L));
        assertNotNull(se.getMessages());
        assertEquals(1, se.getMessages().size());
        assertEquals(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString(), se.getMessages().get(0));
    }


    /**
     * Test the get id method for a given tenant and role identifier
     * (But for a scenario/situation where some diffente exception occurs in the middle of the process)
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void testGetIdByTenantRoleWithException() throws MalformedURLException, SystemException {
        String expectedErrorMsg = "error";
        Exception thrownException = new ProcessingException(expectedErrorMsg);
        String expectedWrappedExceptionMsg = String.format("wrapped exception: %s with message: %s",
                thrownException.getClass().getName(), expectedErrorMsg);

        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        when(client.getIdByTenantRole(1L,1L)).
                thenThrow(new ProcessingException(expectedErrorMsg));

        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).
                thenReturn(client);

        SystemException se = assertThrows(SystemException.class, ()-> target.getIdByTenantRole(1L,1L));
        assertNotNull(se.getMessages());
        assertEquals(1, se.getMessages().size());
        assertEquals(expectedWrappedExceptionMsg, se.getMessages().get(0));
    }


    /**
     * Method to test saving the association tenant role
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue       
     */
    @Test
    public void testSave() throws MalformedURLException, SystemException {

        TenantRole str = new TenantRole();
        str.setId(1L); str.setTenantId(2L); str.setRoleId(3L);

        Response response = Response.ok().build();
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(client.save(str)).thenReturn(response);
        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).
                thenReturn(client);

        Boolean result = target.save(str);
        assertNotNull(result);
        assertTrue(result);
    }

    /**
     * Method to test saving the association tenant role but with status not ok
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue       
     */
    @Test
    public void testSaveWithStatusNeqOK() throws MalformedURLException, SystemException {

        Response response = Response.status(300).build();
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(client.save(any())).thenReturn(response);
        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).
                thenReturn(client);

        Boolean result = target.save(new TenantRole());

        assertNotNull(result);
        assertFalse(result);
    }

    /**
     * Method to test saving the association tenant role but with token expired
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue       
     */
    @Test(expected = SystemException.class)
    public void testSaveTokenExpiration() throws MalformedURLException, SystemException {
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.save(any())).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.save(new TenantRole());
    }

    /**
     * Method to test saving the association tenant role but with exception being throw
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue       
     */
    @Test(expected = SystemException.class)
    public void testSaveException() throws MalformedURLException, SystemException {
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.save(any())).thenThrow(new ProcessingException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.save(new TenantRole());
    }

    /**
     * Test to validate if specific required association exists
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue       
     */
    @Test
    public void testExists() throws MalformedURLException, SystemException {

        Response response = Response.ok(Boolean.TRUE).build();
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(client.exists(1L, 2L)).thenReturn(response);
        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).
                thenReturn(client);

        Boolean result = target.exists(1L, 2L);
        assertNotNull(result);
        assertTrue(result);
    }

    /**
     * Test to validate if specific required association exists but with status not ok
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue       
     */
    @Test
    public void testExistsWithStatusNeqOK() throws MalformedURLException, SystemException {

        Response response = Response.status(300).build();
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(client.exists(any(), any())).thenReturn(response);
        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).
                thenReturn(client);

        Boolean result = target.exists(1L, 2L);

        assertNotNull(result);
        assertFalse(result);
    }

    /**
     * Test to validate if specific required association exists but with token expired
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue       
     */
    @Test(expected = SystemException.class)
    public void testExistsTokenExpiration() throws MalformedURLException, SystemException {
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.exists(any(), any())).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.exists(1L, 2L);
    }

    /**
     * Test to validate if specific required association exists but with exception error
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue       
     */
    @Test(expected = SystemException.class)
    public void testExistsException() throws MalformedURLException, SystemException {
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.exists(any(), any())).thenThrow(new ProcessingException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.exists(1L, 2L);
    }

    /**
     * Test to get specific permission
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue       
     */
    @Test
    public void testGetPermissions() throws MalformedURLException, SystemException {

        String jsonArray = "[{\"id\": 1}, {\"id\": 2}]";
        InputStream is = new ByteArrayInputStream(jsonArray.getBytes());
        Response response = Response.ok(is).build();
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(client.getPermissions(1L, 2L, 3L)).thenReturn(response);
        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).
                thenReturn(client);

        List<? extends SystemPermission> result = target.getPermissions(1L, 2L, 3L);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    /**
     * Test to get specific permission but with token expired
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue       
     */
    @Test(expected = SystemException.class)
    public void testGetPermissionsTokenExpiration() throws MalformedURLException, SystemException {
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.getPermissions(1L, 2L, 3L)).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getPermissions(1L, 2L, 3L);
    }

    /**
     * Test to get specific permission but with exception being throw
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue       
     */
    @Test(expected = SystemException.class)
    public void testGetPermissionsException() throws MalformedURLException, SystemException {
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.getPermissions(1L, 2L, 3L)).thenThrow(new ProcessingException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getPermissions(1L, 2L, 3L);
    }

    /**
     * Test to get specific tenant
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue       
     */
    @Test
    public void testGetTenants() throws MalformedURLException, SystemException {

        String jsonArray = "[{\"id\": 1, \"tenantType\": \"CLIENT\"}, {\"id\": 2, \"tenantType\": \"SUB\"}]";
        InputStream is = new ByteArrayInputStream(jsonArray.getBytes());
        Response response = Response.ok(is).build();
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(client.getTenants(1L, 2L)).thenReturn(response);
        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).
                thenReturn(client);

        List<? extends SystemTenant> result = target.getTenants(1L, 2L);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    /**
     * Test to get specific tenant but with token expired
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue       
     */
    @Test(expected = SystemException.class)
    public void testGetTenantsTokenExpiration() throws MalformedURLException, SystemException {
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.getTenants(1L, 2L)).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getTenants(1L, 2L);
    }

    /**
     * Test for method that retrieves TenantRole associations using pagination approach 
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue 
     */
    @Test
    public void testGetAll() throws MalformedURLException, SystemException {
        String results = "[{\"id\": 1, \"tenantId\": 2, \"roleId\":3}, " +
                "{\"id\": 1, \"tenantId\": 2, \"roleId\": 4}, " +
                "{\"id\": 1, \"tenantId\": 2, \"roleId\": 10}]";

        String pageAsJsonString = "{\"currentPage\": 1, \"totalResults\": 3, \"totalPages\": 1, " +
                "\"results\": " + results + "}";

        InputStream is = new ByteArrayInputStream(pageAsJsonString.getBytes());
        Response response = Response.ok(is).build();
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(client.getAll(1, 3)).thenReturn(response);
        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).
                thenReturn(client);

        Page<? extends SystemTenantRole> result = 
                target.getAll(1, 3);
        assertNotNull(result);
        assertEquals(1,result.getTotalPages());
        assertEquals(3,result.getResults().size());
    }

    /**
     * Test for method that retrieves TenantRole associations using pagination approach,
     * but taking in consideration a scenario where JasonWeb token expires
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue 
     */
    @Test(expected = SystemException.class)
    public void testGetAllTokenExpiration() throws MalformedURLException, SystemException {
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.getAll(1, 2)).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getAll(1, 2);
    }

    /**
     * Test for method that retrieves TenantRole associations using pagination approach,
     * but taking in consideration a scenario where error occurs in the middle of processing
     * (Backend error)
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue 
     */
    @Test(expected = SystemException.class)
    public void testGetAllWithException() throws MalformedURLException, SystemException {
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.getAll(1, 2)).thenThrow(new InternalServerErrorException("test"));

        target.getAll(1, 2);
    }

    /**
     * Test for method that retrieves tenants for a given user (with a specific role) 
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue 
     */
    @Test(expected = SystemException.class)
    public void testGetTenantsException() throws MalformedURLException, SystemException {
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.getTenants(1L, 2L)).thenThrow(new ProcessingException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getTenants(1L, 2L);
    }

    /**
     * Test the retrieval of the tenant roles into a list
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue       
     */
    @Test
    public void testTenantRoleList() throws MalformedURLException, SystemException {
        String jsonArrayAsString = "[{\"id\": 1, \"tenantId\": 2, \"roleId\": 3}, " +
                "{\"id\": 2, \"tenantId\": 5, \"roleId\": 6}]";

        InputStream i = new ByteArrayInputStream(jsonArrayAsString.getBytes());
        Response response = Response.ok().entity(i).build();

        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.getSpecific(1L, 2L, true)).
                thenReturn(response);

        List<? extends SystemTenantRole> tenantRolesList =
                target.getTenantRoles(1L, 2L, true);
        assertNotNull(tenantRolesList);
        assertEquals(2, tenantRolesList.size());
    }

    /**
     * Test the retrieval of the tenant roles into a list but with token expired
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue       
     */
    @Test(expected = SystemException.class)
    public void testTenantRoleListWithTokenException() throws MalformedURLException, SystemException {
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.getSpecific(1L, 2L, true)).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getTenantRoles(1L, 2L, true);
    }

    /**
     * Test the retrieval of the tenant roles into a list but with exception being throw
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue       
     */
    @Test(expected = SystemException.class)
    public void testTenantRoleListWithException() throws MalformedURLException, SystemException {
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.getSpecific(1L, 2L, true)).thenThrow(new ProcessingException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getTenantRoles(1L, 2L, true);
    }

    /**
     * Test method getRoles()
     * Test case - success scenario
     */
    @Test
    public void testGetRoles() {

        String jsonArray = "[{\"id\": 1, \"role\": \"Role-1\", \"roleDescription\": \"RoleDescription-1\" }, " +
                "{\"id\": 2, \"role\": \"Role-2\", \"roleDescription\": \"RoleDescription-2\"}]";
        InputStream is = new ByteArrayInputStream(jsonArray.getBytes());
        Response response = Response.ok(is).build();
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(client.getRolesForUserTenant(1L, 1L)).thenReturn(response);
        assertDoesNotThrow(() -> when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).
                thenReturn(client));

        List<? extends SystemRole> result = assertDoesNotThrow(() -> target.getRolesForUserTenant(1L, 1L));
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    /**
     * Test method getRoles()
     * Test case - failure scenario of
     * Token expire
     * @throws Exception if any error
     */
    @Test(expected = SystemException.class)
    public void testGetRolesTokenExpiration() throws Exception {
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());
        when(client.getRolesForUserTenant(anyLong(), anyLong())).thenThrow(new TokenExpiredException("test"));

        target.getRolesForUserTenant(1L, 1L);
    }

    /**
     * Test method getRoles()
     * Test case - failure scenario
     * @throws Exception if any error
     */
    @Test(expected = SystemException.class)
    public void testGetRolesException() throws Exception {
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.getRolesForUserTenant(anyLong(), anyLong())).thenThrow(new ProcessingException("test"));
        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getRolesForUserTenant(1L, 1L);
    }

}
