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
package io.radien.ms.rolemanagement.client.services;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.entity.Page;
import io.radien.api.model.tenantrole.SystemTenantRoleUser;
import io.radien.api.security.TokensPlaceHolder;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.SystemException;
import io.radien.exception.TokenExpiredException;
import io.radien.ms.authz.client.UserClient;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.rolemanagement.client.entities.TenantRoleUser;
import io.radien.ms.rolemanagement.client.exception.InternalServerErrorException;
import io.radien.ms.rolemanagement.client.util.ClientServiceUtil;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Collections;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
    public void testGetUsers() throws MalformedURLException, SystemException{
        String results = "[{\"id\": 1, \"tenantRoleId\": 2, \"userId\":3}, " +
                "{\"id\": 1, \"tenantRoleId\": 2, \"userId\": 4}, " +
                "{\"id\": 1, \"tenantRoleId\": 2, \"userId\": 10}]";

        String pageAsJsonString = "{\"currentPage\": 1, \"totalResults\": 3, \"totalPages\": 1, " +
                "\"results\": " + results + "}";

        InputStream is = new ByteArrayInputStream(pageAsJsonString.getBytes());
        Response response = Response.ok(is).build();
        TenantRoleUserResourceClient client = mock(TenantRoleUserResourceClient.class);

        when(client.getAll(2L, 3L, 1, 3)).thenReturn(response);
        when(roleServiceUtil.getTenantRoleUserResourceClient(getRoleManagementUrl())).
                thenReturn(client);

        Page<? extends SystemTenantRoleUser> result = target.getUsers(2L, 3L, 1, 3);
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
    public void testGetUsersTokenExpiration() throws MalformedURLException, SystemException {
        TenantRoleUserResourceClient client = mock(TenantRoleUserResourceClient.class);

        // Simulates JWT expire even on the reattempt
        when(roleServiceUtil.getTenantRoleUserResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.getAll(33L, 44L, 1, 2)).
                thenThrow(new TokenExpiredException("test")).
                thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getUsers(33L, 44L, 1, 2);
    }

    /**
     * Test for method that retrieves TenantRoleUser associations using pagination approach,
     * but taking in consideration a scenario where error occurs in the middle of processing
     * (Backend error)
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testGetUsersException() throws MalformedURLException, SystemException {
        TenantRoleUserResourceClient client = mock(TenantRoleUserResourceClient.class);

        when(roleServiceUtil.getTenantRoleUserResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.getAll(33L, 44L, 1, 2)).thenThrow(new InternalServerErrorException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getUsers(33L, 44L, 1, 2);
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

        Boolean result = target.unAssignUser(1L);
        assertNotNull(result);
        assertTrue(result);

        result = target.unAssignUser(1L);
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

        assertTrue(target.unAssignUser(1L));
        SystemException se = assertThrows(SystemException.class, () -> target.unAssignUser(2L));
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

        target.unAssignUser(1L);
    }

}
