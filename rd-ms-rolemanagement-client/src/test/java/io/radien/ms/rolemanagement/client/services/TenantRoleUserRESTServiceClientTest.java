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
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.model.tenantrole.SystemTenantRoleUser;
import io.radien.api.security.TokensPlaceHolder;
import io.radien.api.util.FactoryUtilService;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.NotFoundException;
import io.radien.exception.SystemException;
import io.radien.exception.TokenExpiredException;
import io.radien.ms.authz.client.UserClient;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.rolemanagement.client.entities.TenantRoleUser;
import io.radien.ms.rolemanagement.client.exception.InternalServerErrorException;
import io.radien.ms.rolemanagement.client.util.ClientServiceUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Collections;
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
public class TenantRoleUserRESTServiceClientTest {

    @InjectMocks
    TenantRoleUserRESTServiceClient target;

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
     * Test for method that retrieves TenantRoleUser associations using pagination approach
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void testGetAll() throws MalformedURLException, SystemException{
        String results = "[{\"id\": 1, \"tenantRoleId\": 2, \"userId\":3}, " +
                "{\"id\": 1, \"tenantRoleId\": 2, \"userId\": 4}, " +
                "{\"id\": 1, \"tenantRoleId\": 2, \"userId\": 10}]";

        String pageAsJsonString = "{\"currentPage\": 1, \"totalResults\": 3, \"totalPages\": 1, " +
                "\"results\": " + results + "}";

        InputStream is = new ByteArrayInputStream(pageAsJsonString.getBytes());
        Response response = Response.ok(is).build();
        TenantRoleUserResourceClient client = mock(TenantRoleUserResourceClient.class);

        when(client.getAll(2L, 3L, 1, 3, null, false)).thenReturn(response);
        when(roleServiceUtil.getTenantRoleUserResourceClient(getRoleManagementUrl())).
                thenReturn(client);

        Page<? extends SystemTenantRoleUser> result = target.getAll(2L, 3L, 1, 3, null, false);
        assertNotNull(result);
        assertEquals(1, result.getTotalPages());
        assertEquals(3,result.getResults().size());
    }

    /**
     * Test for method that retrieves TenantRoleUser associations using pagination approach,
     * but taking in consideration a scenario where JasonWeb token expires
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testGetAllTokenExpiration() throws MalformedURLException, SystemException {
        TenantRoleUserResourceClient client = mock(TenantRoleUserResourceClient.class);

        // Simulates JWT expire even on the reattempt
        when(roleServiceUtil.getTenantRoleUserResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.getAll(33L, 44L, 1, 2, null, false)).
                thenThrow(new TokenExpiredException("test")).
                thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getAll(33L, 44L, 1, 2, null, false);
    }

    /**
     * Test for method that retrieves TenantRoleUser associations using pagination approach,
     * but taking in consideration a scenario where error occurs in the middle of processing
     * (Backend error)
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testGetAllException() throws MalformedURLException, SystemException {
        TenantRoleUserResourceClient client = mock(TenantRoleUserResourceClient.class);

        when(roleServiceUtil.getTenantRoleUserResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.getAll(33L, 44L, 1, 2, null, false)).thenThrow(new InternalServerErrorException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getAll(33L, 44L, 1, 2, null, false);
    }

    /**
     * Test for method that retrieves Users (Ids) currently associated with Tenant and Role,
     * The ids are retrieved using a pagination approach
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void testGetUsersIds() throws MalformedURLException, SystemException{
        String results = "[3, 4, 10]";

        String pageAsJsonString = "{\"currentPage\": 1, \"totalResults\": 3, \"totalPages\": 1, " +
                "\"results\": " + results + "}";

        InputStream is = new ByteArrayInputStream(pageAsJsonString.getBytes());
        Response response = Response.ok(is).build();
        TenantRoleUserResourceClient client = mock(TenantRoleUserResourceClient.class);

        when(client.getAllUserIds(2L, 3L, 1, 3)).thenReturn(response);
        when(roleServiceUtil.getTenantRoleUserResourceClient(getRoleManagementUrl())).
                thenReturn(client);

        Page<Long> result = target.getUsersIds(2L, 3L, 1, 3);
        assertNotNull(result);
        assertEquals(1, result.getTotalPages());
        assertEquals(3,result.getResults().size());
    }

    /**
     * Test for method that retrieves Users Ids (associated with tenant and role) using pagination approach,
     * but taking in consideration a scenario where JasonWeb token expires
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testGetUsersIdsTokenExpiration() throws MalformedURLException, SystemException {
        TenantRoleUserResourceClient client = mock(TenantRoleUserResourceClient.class);

        // Simulates JWT expire even on the reattempt
        when(roleServiceUtil.getTenantRoleUserResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.getAllUserIds(33L, 44L, 1, 2)).
                thenThrow(new TokenExpiredException("test")).
                thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getUsersIds(33L, 44L, 1, 2);
    }

    /**
     * Test for method that retrieves User ids (associated with a tenant and role) using pagination approach,
     * but taking in consideration a scenario where error occurs in the middle of processing
     * (Backend error)
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testGetUsersIdsException() throws MalformedURLException, SystemException {
        TenantRoleUserResourceClient client = mock(TenantRoleUserResourceClient.class);

        when(roleServiceUtil.getTenantRoleUserResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.getAllUserIds(33L, 44L, 1, 2)).thenThrow(new InternalServerErrorException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getUsersIds(33L, 44L, 1, 2);
    }

    /**
     * Test the assignment of a user to the association but with token expiration
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testAssignUserTokenExpiration() throws MalformedURLException, SystemException {
        TenantRoleUserResourceClient client = mock(TenantRoleUserResourceClient.class);

        TenantRoleUser tenantRoleUser = mock(TenantRoleUser.class);
        when(roleServiceUtil.getTenantRoleUserResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.assignUser(tenantRoleUser)).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.assignUser(tenantRoleUser);
    }

    /**
     * Test the assignment of a user to the association but with exception to be throw
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testAssignUserException() throws MalformedURLException, SystemException {
        TenantRoleUserResourceClient client = mock(TenantRoleUserResourceClient.class);

        TenantRoleUser tenantRoleUser = mock(TenantRoleUser.class);
        when(roleServiceUtil.getTenantRoleUserResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.assignUser(tenantRoleUser)).thenThrow(new ProcessingException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.assignUser(tenantRoleUser);
    }

    /**
     * Test the assignment of a user to the association
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void testAssignUser() throws MalformedURLException, SystemException {
        TenantRoleUserResourceClient client = mock(TenantRoleUserResourceClient.class);
        TenantRoleUser tenantRoleUser = mock(TenantRoleUser.class);
        when(client.assignUser(tenantRoleUser)).
                thenReturn(Response.ok().build()).
                thenReturn(Response.status(300).build());

        when(roleServiceUtil.getTenantRoleUserResourceClient(getRoleManagementUrl())).
                thenReturn(client);

        Boolean result = target.assignUser(tenantRoleUser);
        assertNotNull(result);
        assertTrue(result);

        result = target.assignUser(tenantRoleUser);
        assertNotNull(result);
        assertFalse(result);
    }

    /**
     * Test the un-assignment of a user to the association
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void testUnAssignUser() throws MalformedURLException, SystemException {
        TenantRoleUserResourceClient client = mock(TenantRoleUserResourceClient.class);
        Collection<Long> roleIds = Collections.singletonList(2L);
        when(client.unAssignUser(1L, roleIds, 3L)).
                thenReturn(Response.ok(Boolean.TRUE).build());
        when(client.unAssignUser(1L, roleIds, 4L)).
                thenReturn(Response.status(303).build());

        when(roleServiceUtil.getTenantRoleUserResourceClient(getRoleManagementUrl())).
                thenReturn(client);

        Boolean result = target.unAssignUser(1L, roleIds, 3L);
        assertNotNull(result);
        assertTrue(result);

        result = target.unAssignUser(1L, roleIds, 4L);
        assertNotNull(result);
        assertFalse(result);
    }

    /**
     * Test the un-assignment of a user to the association but with token expired
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void testUnAssignUserTokenExpiration() throws MalformedURLException, SystemException {
        TenantRoleUserResourceClient client = mock(TenantRoleUserResourceClient.class);
        Collection<Long> roleIds = Collections.singletonList(2L);
        String msg = "error performing renewing token";
        when(roleServiceUtil.getTenantRoleUserResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.unAssignUser(1L, roleIds, 3L)).
                thenThrow(new TokenExpiredException(msg)).
                thenReturn(Response.ok().build());

        when(client.unAssignUser(2L, roleIds, 3L)).
                thenThrow(new TokenExpiredException(msg)).
                thenThrow(new TokenExpiredException(msg));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        assertTrue(target.unAssignUser(1L, roleIds, 3L));
        SystemException se = assertThrows(SystemException.class, () -> target.unAssignUser(2L, roleIds, 3L));
        assertEquals(1, se.getMessages().size());
        assertEquals(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString(), se.getMessages().get(0));
    }

    /**
     * Test the un-assignment of a user to the association but with exception being throw
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testUnAssignUserException() throws MalformedURLException, SystemException {
        TenantRoleUserResourceClient client = mock(TenantRoleUserResourceClient.class);
        Collection<Long> roleIds = Collections.singletonList(2L);
        when(roleServiceUtil.getTenantRoleUserResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.unAssignUser(1L, roleIds, 3L)).
                thenThrow(new ProcessingException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.unAssignUser(1L, roleIds, 3L);
    }

    /**
     * Test the un-assignment of a user to the association
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void testUnAssignUserBasedOnSingleId() throws MalformedURLException, SystemException {
        TenantRoleUserResourceClient client = mock(TenantRoleUserResourceClient.class);

        when(client.delete(1L)).
                thenReturn(Response.ok(Boolean.TRUE).build()).
                thenReturn(Response.status(303).build());

        when(roleServiceUtil.getTenantRoleUserResourceClient(getRoleManagementUrl())).
                thenReturn(client);

        Boolean result = target.delete(1L);
        assertNotNull(result);
        assertTrue(result);

        result = target.delete(1L);
        assertNotNull(result);
        assertFalse(result);
    }

    /**
     * Test the un-assignment of a user to the association but with token expired
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void testUnAssignUserBasedOnSingleIdWithTokenExpiration() throws MalformedURLException, SystemException {
        TenantRoleUserResourceClient client = mock(TenantRoleUserResourceClient.class);
        String msg = "test";
        when(roleServiceUtil.getTenantRoleUserResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.delete(1L)).thenThrow(new TokenExpiredException(msg)).
                thenReturn(Response.ok().build());

        when(client.delete(2L)).
                thenThrow(new TokenExpiredException(msg)).
                thenThrow(new TokenExpiredException(msg));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        assertTrue(target.delete(1L));
        SystemException se = assertThrows(SystemException.class, () -> target.delete(2L));
        assertEquals(1, se.getMessages().size());
        assertEquals(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString(), se.getMessages().get(0));
    }

    /**
     * Test the un-assignment of a user to the association but with exception being throw
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testUnAssignUserBasedOnSingleIdWithException() throws MalformedURLException, SystemException {
        TenantRoleUserResourceClient client = mock(TenantRoleUserResourceClient.class);

        when(roleServiceUtil.getTenantRoleUserResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.delete(1L)).thenThrow(new ProcessingException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.delete(1L);
    }

    /**
     * Test the updating of a TenantRoleUser but with token expiration
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testUpdateTenantRoleUserTokenExpiration() throws MalformedURLException, SystemException {
        TenantRoleUserResourceClient client = mock(TenantRoleUserResourceClient.class);

        long id = 1L;
        TenantRoleUser tenantRoleUser = mock(TenantRoleUser.class);
        when(tenantRoleUser.getId()).thenReturn(id);
        when(roleServiceUtil.getTenantRoleUserResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.update(id, tenantRoleUser)).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.update(tenantRoleUser);
    }

    /**
     * Test the updating of a TenantRoleUser but with exception to be throw
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testUpdateTenantRoleUserWithException() throws MalformedURLException, SystemException {
        TenantRoleUserResourceClient client = mock(TenantRoleUserResourceClient.class);

        TenantRoleUser tenantRoleUser = mock(TenantRoleUser.class);
        when(tenantRoleUser.getId()).thenReturn(1L);
        when(roleServiceUtil.getTenantRoleUserResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.update(tenantRoleUser.getId(), tenantRoleUser)).thenThrow(new ProcessingException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.update(tenantRoleUser);
    }

    /**
     * Test the updating for a previously created TenantRoleUser
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testUpdateTenantRoleUserMalformedURL() throws MalformedURLException, SystemException {
        when(roleServiceUtil.getTenantRoleUserResourceClient(getRoleManagementUrl())).
                thenThrow(new MalformedURLException("url issue"));
        target.update(new TenantRoleUser());
    }

    /**
     * Test the updating for a previously created TenantRoleUser
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void testUpdateTenantRoleUser() throws MalformedURLException, SystemException {
        TenantRoleUserResourceClient client = mock(TenantRoleUserResourceClient.class);
        TenantRoleUser tenantRoleUser = mock(TenantRoleUser.class);
        long id = 1L;
        when(tenantRoleUser.getId()).thenReturn(id);
        when(client.update(id, tenantRoleUser)).
                thenReturn(Response.ok().build()).
                thenReturn(Response.status(300).build());

        when(roleServiceUtil.getTenantRoleUserResourceClient(getRoleManagementUrl())).
                thenReturn(client);

        Boolean result = target.update(tenantRoleUser);
        assertNotNull(result);
        assertTrue(result);

        result = target.update(tenantRoleUser);
        assertNotNull(result);
        assertFalse(result);
    }

    /**
     * Test for method that retrieves TenantRoleUser associations
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void testGetTenantRoleUsers() throws MalformedURLException, SystemException{
        String results = "[{\"id\": 1, \"tenantRoleId\": 2, \"userId\":3}, " +
                "{\"id\": 1, \"tenantRoleId\": 2, \"userId\": 4}, " +
                "{\"id\": 1, \"tenantRoleId\": 2, \"userId\": 10}]";

        InputStream is = new ByteArrayInputStream(results.getBytes());
        Response response = Response.ok(is).build();
        TenantRoleUserResourceClient client = mock(TenantRoleUserResourceClient.class);

        when(client.getSpecific(2L, 3L, false)).thenReturn(response);
        when(roleServiceUtil.getTenantRoleUserResourceClient(getRoleManagementUrl())).
                thenReturn(client);

        List<? extends SystemTenantRoleUser> result = target.getTenantRoleUsers(
                2L, 3L, false);
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    /**
     * Test for method that retrieves TenantRoleUser associations,
     * but taking in consideration a scenario where JasonWeb token expires
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testGetTenantRoleUsersTokenExpiration() throws MalformedURLException, SystemException {
        TenantRoleUserResourceClient client = mock(TenantRoleUserResourceClient.class);

        // Simulates JWT expire even on the reattempt
        when(roleServiceUtil.getTenantRoleUserResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.getSpecific(33L, 44L, false)).
                thenThrow(new TokenExpiredException("test")).
                thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getTenantRoleUsers(33L, 44L, false);
    }

    /**
     * Test for method that retrieves TenantRoleUser associations,
     * but taking in consideration a scenario where error occurs in the middle of processing
     * (Backend error)
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testGetTenantRoleUsersException() throws MalformedURLException, SystemException {
        TenantRoleUserResourceClient client = mock(TenantRoleUserResourceClient.class);

        when(roleServiceUtil.getTenantRoleUserResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.getSpecific(33L, 44L, false)).
                thenThrow(new InternalServerErrorException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getTenantRoleUsers(33L, 44L, false);
    }

    /**
     * Test for method that retrieves TenantRoleUser associations,
     * but taking in consideration a scenario where error occurs in the middle of processing
     * (Backend error)
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testGetTenantRoleUsersMalformedURLException() throws MalformedURLException, SystemException {
        when(roleServiceUtil.getTenantRoleUserResourceClient(getRoleManagementUrl())).
                thenThrow(new MalformedURLException("invalid url"));
        target.getTenantRoleUsers(33L, 44L, false);
    }

    /**
     * Test the get tenant role user by a given specific id
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void testGetTenantRoleUserById() throws MalformedURLException, SystemException {

        SystemTenantRoleUser trp = new TenantRoleUser();
        trp.setId(1L); trp.setTenantRoleId(2L); trp.setUserId(3L);

        JsonObjectBuilder builder = Json.createObjectBuilder();
        FactoryUtilService.addValueLong(builder, "id", trp.getId());
        FactoryUtilService.addValueLong(builder, "tenantRoleId", trp.getTenantRoleId());
        FactoryUtilService.addValueLong(builder, "userId", trp.getUserId());

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        JsonWriter jsonWriter = Json.createWriter(stream);
        jsonWriter.writeObject(builder.build());
        jsonWriter.close();

        InputStream is = new ByteArrayInputStream(stream.toByteArray());
        Response response = Response.ok(is).build();
        TenantRoleUserResourceClient client = Mockito.mock(TenantRoleUserResourceClient.class);

        long tenantRoleUserId = 1L;
        when(client.getById(tenantRoleUserId)).thenReturn(response);
        when(roleServiceUtil.getTenantRoleUserResourceClient(getRoleManagementUrl())).
                thenReturn(client);

        Optional<SystemTenantRoleUser> result = target.
                getTenantRoleUserById(tenantRoleUserId);

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(result.get().getId(), trp.getId());
    }

    /**
     * Test the get association of a tenant role user by id but this time with status not ok
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void testGetTenantRoleUserByIdWithStatusNeqOK() throws MalformedURLException, SystemException {

        Response response = Response.status(300).build();
        TenantRoleUserResourceClient client = mock(TenantRoleUserResourceClient.class);

        long tenantRoleUserId = 1L;

        when(client.getById(tenantRoleUserId)).thenReturn(response);
        when(roleServiceUtil.getTenantRoleUserResourceClient(getRoleManagementUrl())).
                thenReturn(client);

        Optional<SystemTenantRoleUser> result = target.
                getTenantRoleUserById(tenantRoleUserId);

        assertNotNull(result);
        assertFalse(result.isPresent());
    }

    /**
     * Test the get association of a tenant role user by id but this time with
     * explicit status NOT FOUND
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void testGetTenantRoleUserNotFound() throws MalformedURLException, SystemException {

        TenantRoleUserResourceClient client = mock(TenantRoleUserResourceClient.class);

        long tenantRoleUserId = 1L;

        when(client.getById(tenantRoleUserId)).thenThrow(new NotFoundException());
        when(roleServiceUtil.getTenantRoleUserResourceClient(getRoleManagementUrl())).
                thenReturn(client);

        Optional<SystemTenantRoleUser> result = target.
                getTenantRoleUserById(tenantRoleUserId);

        assertNotNull(result);
        assertFalse(result.isPresent());
    }

    /**
     * Test the get tenant role user by id but with token expiration
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testGetTenantRoleUserByIdTokenExpiration() throws MalformedURLException, SystemException {
        TenantRoleUserResourceClient client = Mockito.mock(TenantRoleUserResourceClient.class);

        when(roleServiceUtil.getTenantRoleUserResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.getById(1L)).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getTenantRoleUserById(1L);
    }

    /**
     * Test the behaviour of getting the tenant role user by id but with exception
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testGetTenantRoleByIdException() throws MalformedURLException, SystemException {
        TenantRoleUserResourceClient client = mock(TenantRoleUserResourceClient.class);

        when(roleServiceUtil.getTenantRoleUserResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.getById(1L)).thenThrow(new ProcessingException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getTenantRoleUserById(1L);
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
        TenantRoleUserResourceClient client = mock(TenantRoleUserResourceClient.class);

        when(roleServiceUtil.getTenantRoleUserResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.getTenants(1L, 2L)).thenReturn(response);
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
        TenantRoleUserResourceClient client = mock(TenantRoleUserResourceClient.class);

        when(roleServiceUtil.getTenantRoleUserResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.getTenants(1L, 2L)).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getTenants(1L, 2L);
    }


    /**
     * Test for method that retrieves tenants for a given user (with a specific role)
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testGetTenantsException() throws MalformedURLException, SystemException {
        TenantRoleUserResourceClient client = mock(TenantRoleUserResourceClient.class);

        when(roleServiceUtil.getTenantRoleUserResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.getTenants(1L, 2L)).thenThrow(new ProcessingException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getTenants(1L, 2L);
    }
}
