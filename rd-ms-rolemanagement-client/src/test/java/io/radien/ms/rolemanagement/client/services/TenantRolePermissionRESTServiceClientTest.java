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
import io.radien.api.model.tenantrole.SystemTenantRolePermission;
import io.radien.api.security.TokensPlaceHolder;
import io.radien.exception.SystemException;
import io.radien.exception.TokenExpiredException;
import io.radien.ms.authz.client.UserClient;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.rolemanagement.client.entities.TenantRolePermission;
import io.radien.ms.rolemanagement.client.exception.InternalServerErrorException;
import io.radien.ms.rolemanagement.client.util.ClientServiceUtil;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
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
    @Test(expected = SystemException.class)
    public void testUnAssignPermissionTokenExpiration() throws MalformedURLException, SystemException {
        TenantRolePermissionResourceClient client = Mockito.mock(TenantRolePermissionResourceClient.class);
        String msg = "error performing renewing token";
        when(roleServiceUtil.getTenantRolePermissionResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.unAssignPermission(1L, 2L, 3L)).
                thenThrow(new TokenExpiredException(msg)).
                thenReturn(Response.ok().build()).
                thenThrow(new TokenExpiredException(msg)).
                thenThrow(new TokenExpiredException(msg));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        assertTrue(target.unAssignPermission(1L, 2L, 3L));
        target.unAssignPermission(1L, 2L, 3L);
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
    @Test(expected = SystemException.class)
    public void testUnAssignPermissionBasedOnSingleIdWithTokenExpiration() throws MalformedURLException, SystemException {
        TenantRolePermissionResourceClient client = Mockito.mock(TenantRolePermissionResourceClient.class);
        String msg = "test";
        when(roleServiceUtil.getTenantRolePermissionResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.delete(1L)).thenThrow(new TokenExpiredException(msg)).
                thenReturn(Response.ok().build()).
                thenThrow(new TokenExpiredException(msg)).
                thenThrow(new TokenExpiredException(msg));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        assertTrue(target.unAssignPermission(1L));
        target.unAssignPermission(1L);
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
    public void testGetPermissions() throws MalformedURLException, SystemException{
        String results = "[{\"id\": 1, \"tenantRoleId\": 2, \"permissionId\":3}, " +
                "{\"id\": 1, \"tenantRoleId\": 2, \"permissionId\": 4}, " +
                "{\"id\": 1, \"tenantRoleId\": 2, \"permissionId\": 10}]";

        String pageAsJsonString = "{\"currentPage\": 1, \"totalResults\": 3, \"totalPages\": 1, " +
                "\"results\": " + results + "}";

        InputStream is = new ByteArrayInputStream(pageAsJsonString.getBytes());
        Response response = Response.ok(is).build();
        TenantRolePermissionResourceClient client = Mockito.mock(TenantRolePermissionResourceClient.class);

        when(client.getAll(2L, 3L, 1, 3)).thenReturn(response);
        when(roleServiceUtil.getTenantRolePermissionResourceClient(getRoleManagementUrl())).
                thenReturn(client);

        Page<? extends SystemTenantRolePermission> result = target.getPermissions(2L, 3L, 1, 3);
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
    public void testGetPermissionsTokenExpiration() throws MalformedURLException, SystemException {
        TenantRolePermissionResourceClient client = Mockito.mock(TenantRolePermissionResourceClient.class);

        // Simulates JWT expire even on the reattempt
        when(roleServiceUtil.getTenantRolePermissionResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.getAll(33L, 44L, 1, 2)).
                thenThrow(new TokenExpiredException("test")).
                thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getPermissions(33L, 44L, 1, 2);
    }

    /**
     * Test for method that retrieves TenantRolePermission associations using pagination approach,
     * but taking in consideration a scenario where error occurs in the middle of processing
     * (Backend error)
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testGetPermissionsException() throws MalformedURLException, SystemException {
        TenantRolePermissionResourceClient client = Mockito.mock(TenantRolePermissionResourceClient.class);

        when(roleServiceUtil.getTenantRolePermissionResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.getAll(33L, 44L, 1, 2)).thenThrow(new InternalServerErrorException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getPermissions(33L, 44L, 1, 2);
    }
}
