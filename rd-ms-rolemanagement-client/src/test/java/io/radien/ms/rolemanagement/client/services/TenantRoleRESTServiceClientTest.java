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
import io.radien.api.model.tenantrole.SystemTenantRole;
import io.radien.api.security.TokensPlaceHolder;
import io.radien.api.util.FactoryUtilService;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.InternalServerErrorException;
import io.radien.exception.NotFoundException;
import io.radien.exception.SystemException;
import io.radien.exception.TokenExpiredException;
import io.radien.ms.authz.client.UserClient;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.rolemanagement.client.entities.TenantRole;
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
import static org.mockito.ArgumentMatchers.any;
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

        when(client.create(str)).thenReturn(response);
        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).
                thenReturn(client);

        Boolean result = target.create(str);
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

        when(client.create(any())).thenReturn(response);
        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).
                thenReturn(client);

        Boolean result = target.create(new TenantRole());

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
        when(client.create(any())).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.create(new TenantRole());
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
        when(client.create(any())).thenThrow(new ProcessingException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.create(new TenantRole());
    }

    /**
     * Method to test create the association tenant role but with exception being throw
     * due malformed url
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testCreateMalformedURLException() throws MalformedURLException, SystemException {
        TenantRole tenantRole = new TenantRole(); tenantRole.setId(1L);
        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).
                thenThrow(new MalformedURLException());
        target.create(tenantRole);
    }

    /**
     * Test to validate if specific required association exists
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue       
     */
    @Test
    public void testExists() throws MalformedURLException, SystemException {

        Response response = Response.noContent().build();
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
     * (Http Status code different from 204)
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
     * Test to validate if specific required association exists but with status not ok
     * (Due association not found)
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void testExistsWhenTenantRoleAssociationNotFound() throws MalformedURLException, SystemException {
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);
        when(client.exists(any(), any())).thenThrow(new NotFoundException());
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
     * Test to validate if specific required association exists after token expired (ReTry)
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void testExistsAfterTokenExpiration() throws MalformedURLException, SystemException {
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.exists(any(), any())).
                thenThrow(new TokenExpiredException("test")).
                thenReturn(Response.noContent().build());

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        assertTrue(target.exists(1L, 2L));
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
     * Test to validate if specific required association exists but with exception error
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testExistsMalformedURLException() throws MalformedURLException, SystemException {
        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).
                thenThrow(new MalformedURLException());
        target.exists(1L, 2L);
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

        when(client.getAll(1L, 1L, 1, 3, null, false)).thenReturn(response);
        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).
                thenReturn(client);

        Page<? extends SystemTenantRole> result = 
                target.getAll(1L, 1L, 1, 3, null, false);
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
        when(client.getAll(1L, 1L, 1, 2, null, false)).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getAll(1L, 1L, 1, 2, null, false);
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
        when(client.getAll(1L, 1L, 1, 2, null, false)).
                thenThrow(new InternalServerErrorException("test"));

        target.getAll(1L, 1L, 1, 2, null, false);
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
     * Method to test update the association tenant role
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void testUpdate() throws MalformedURLException, SystemException {

        TenantRole str = new TenantRole();
        str.setId(1L); str.setTenantId(2L); str.setRoleId(3L);

        Response response = Response.ok().build();
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(client.update(str.getId(), str)).thenReturn(response);
        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).
                thenReturn(client);

        Boolean result = target.update(str);
        assertNotNull(result);
        assertTrue(result);
    }

    /**
     * Method to test updating the association tenant role but with status not ok
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void testUpdateWithStatusNeqOK() throws MalformedURLException, SystemException {
        TenantRole tenantRole = new TenantRole(); tenantRole.setId(1L);
        Response response = Response.status(300).build();
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(client.update(tenantRole.getId(), tenantRole)).thenReturn(response);
        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).
                thenReturn(client);

        Boolean result = target.update(tenantRole);

        assertNotNull(result);
        assertFalse(result);
    }


    /**
     * Method to test updating the association tenant role but with token expired
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testUpdateTokenExpiration() throws MalformedURLException, SystemException {
        TenantRole tenantRole = new TenantRole(); tenantRole.setId(1L);
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.update(tenantRole.getId(), tenantRole)).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.update(tenantRole);
    }

    /**
     * Method to test update the association tenant role but with exception being throw
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testUpdateException() throws MalformedURLException, SystemException {
        TenantRole tenantRole = new TenantRole(); tenantRole.setId(1L);
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.update(tenantRole.getId(), tenantRole)).thenThrow(new ProcessingException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.update(tenantRole);
    }

    /**
     * Method to test update the association tenant role but with exception being throw
     * due malformed url
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testUpdateMalformedURLException() throws MalformedURLException, SystemException {
        TenantRole tenantRole = new TenantRole(); tenantRole.setId(1L);
        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).
                thenThrow(new MalformedURLException());
        target.update(tenantRole);
    }
}
