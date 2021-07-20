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
import io.radien.exception.SystemException;
import io.radien.exception.TokenExpiredException;

import io.radien.ms.authz.client.UserClient;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.rolemanagement.client.exception.InternalServerErrorException;
import io.radien.ms.rolemanagement.client.util.ClientServiceUtil;

import java.util.Arrays;
import java.util.HashSet;
import javax.ws.rs.ProcessingException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
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
        TenantRoleUserResourceClient client = Mockito.mock(TenantRoleUserResourceClient.class);

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
        TenantRoleUserResourceClient client = Mockito.mock(TenantRoleUserResourceClient.class);

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
        TenantRoleUserResourceClient client = Mockito.mock(TenantRoleUserResourceClient.class);

        when(roleServiceUtil.getTenantRoleUserResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.getAll(33L, 44L, 1, 2)).thenThrow(new InternalServerErrorException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getUsers(33L, 44L, 1, 2);
    }

    /**
     * Test Unassigned User Tenant Role(s)
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any error
     */
    @Test
    public void testUnAssignedUserTenantRoles() throws MalformedURLException, SystemException {
        TenantRoleUserResourceClient client = Mockito.mock(TenantRoleUserResourceClient.class);

        when(client.deleteUnAssignedUserTenantRoles(anyLong(), anyLong(), anyCollection())).
                thenReturn(Response.ok(Boolean.TRUE).build());

        when(roleServiceUtil.getTenantRoleUserResourceClient(getRoleManagementUrl())).thenReturn(client);

        Boolean resultTrue = target.deleteUnAssignedUserTenantRoles(1L, 2L, new HashSet<>( Arrays.asList(1L, 2L)));
        assertNotNull(resultTrue);
        assertTrue(resultTrue);

        when(client.deleteUnAssignedUserTenantRoles(anyLong(), anyLong(), anyCollection())).thenReturn(Response.status(303).build());
        Boolean resultFalse = target.deleteUnAssignedUserTenantRoles(1L, 2L, new HashSet<>(Arrays.asList(1L, 2L)));
        assertFalse(resultFalse);
    }

    /**
     * Test Unassigned User Tenant Role(s)
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any error
     */
    @Test(expected = SystemException.class)
    public void testUnAssignedUserTenantRolesTokenExpiration() throws MalformedURLException, SystemException {
        TenantRoleUserResourceClient client = Mockito.mock(TenantRoleUserResourceClient.class);

        when(roleServiceUtil.getTenantRoleUserResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());
        when(client.deleteUnAssignedUserTenantRoles(anyLong(), anyLong(), anyCollection())).thenThrow(new TokenExpiredException("test"));

        target.deleteUnAssignedUserTenantRoles(1L, 2L, new HashSet<>(Arrays.asList(1L, 2L)));
    }

    /**
     * Test Unassigned User Tenant Role(s)
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any error
     */
    @Test(expected = SystemException.class)
    public void testUnAssignedUserTenantRolesExpiration() throws MalformedURLException, SystemException {
        TenantRoleUserResourceClient client = Mockito.mock(TenantRoleUserResourceClient.class);

        when(roleServiceUtil.getTenantRoleUserResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.deleteUnAssignedUserTenantRoles(anyLong(), anyLong(), anyCollection())).thenThrow(new ProcessingException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());


        target.deleteUnAssignedUserTenantRoles(1L, 2L, new HashSet<>(Arrays.asList(1L, 2L)));
    }

}
