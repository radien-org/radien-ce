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
package io.radien.ms.rolemanagement.resource;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.model.tenantrole.SystemTenantRolePermission;
import io.radien.api.security.TokensPlaceHolder;
import io.radien.api.service.role.SystemRolesEnum;
import io.radien.ms.authz.client.PermissionClient;
import io.radien.ms.authz.client.TenantRoleClient;
import io.radien.ms.authz.client.UserClient;
import io.radien.ms.openid.entities.Principal;
import io.radien.ms.rolemanagement.client.entities.TenantRolePermission;
import io.radien.ms.rolemanagement.service.TenantRolePermissionBusinessService;
import io.radien.ms.rolemanagement.datalayer.TenantRolePermissionService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Response;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * TenantRolePermission resource requests test
 * {@link TenantRoleResource}
 *
 * @author Newton Carvalho
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TenantRolePermissionResourceTest {

    @InjectMocks
    TenantRolePermissionResource tenantRolePermissionResource;

    @Mock
    TenantRolePermissionBusinessService tenantRolePermissionBusinessService;

    @Mock
    TenantRolePermissionService tenantRolePermissionService;

    @Mock
    HttpServletRequest servletRequest;

    @Mock
    PermissionClient permissionClient;

    @Mock
    TenantRoleClient tenantRoleClient;

    @Mock
    TokensPlaceHolder tokensPlaceHolder;

    @Mock
    OAFAccess oafAccess;

    @Mock
    UserClient userClient;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    /**
     * Tests response from assignPermission method
     */
    @Test
    public void test001AssignPermission() {
        Principal principal = new Principal();
        principal.setSub("aaa-bbb-ccc-ddd");
        HttpSession session = Mockito.mock(HttpSession.class);

        when(servletRequest.getSession()).thenReturn(session);
        when(servletRequest.getSession(false)).thenReturn(session);
        when(session.getAttribute("USER")).thenReturn(principal);
        doReturn(Response.ok().entity(1001L).build()).when(this.userClient).getUserIdBySub(principal.getSub());

        doReturn("token-yyz").when(tokensPlaceHolder).getAccessToken();
        when(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_USERMANAGEMENT)).thenReturn("http://url.pt");
        Response expectedPermissionId = Response.ok().entity(1L).build();
        doReturn(expectedPermissionId).when(permissionClient).getIdByResourceAndAction(any(),any());
        doReturn(Response.ok(Boolean.TRUE).build()).when(tenantRoleClient).isPermissionExistentForUser(1001L,1L,null);
        Response expectedAuthGranted = Response.ok().entity(Boolean.TRUE).build();
        doReturn(expectedAuthGranted).when(tenantRoleClient).isRoleExistentForUser(
                1001L, SystemRolesEnum.SYSTEM_ADMINISTRATOR.getRoleName(), null);

        Response response = tenantRolePermissionResource.assignPermission(new TenantRolePermission());
        assertEquals(200, response.getStatus());
    }

    /**
     * Tests response from unassignPermission method
     */
    @Test
    public void test003UnAssignPermission() {
        Response response = tenantRolePermissionResource.unAssignPermission(1L,
                2L, 3L);
        assertEquals(200, response.getStatus());
    }

    /**
     * Tests response from delete method
     */
    @Test
    public void test005Delete() {
        Response response = tenantRolePermissionResource.delete(1L);
        assertEquals(200,response.getStatus());
    }

    /**
     * Tests response from getAll method
     */
    @Test
    public void test007GetAll() {
        Response response = tenantRolePermissionResource.getAll(1L,1L,
                2, 3, null, false);
        assertEquals(200, response.getStatus());
    }


    /**
     * Tests response from getAll method
     */
    @Test
    public void test009GetTenantRolePermissions() {
        Response response = tenantRolePermissionResource.getSpecific(1L,1L,
                false);
        assertEquals(200, response.getStatus());
    }

    /**
     * Tests response from update method
     */
    @Test
    public void test011UpdatePermission() {
        Principal principal = new Principal();
        principal.setSub("aaa-bbb-ccc-ddd");
        HttpSession session = Mockito.mock(HttpSession.class);

        when(servletRequest.getSession()).thenReturn(session);
        when(servletRequest.getSession(false)).thenReturn(session);
        when(session.getAttribute("USER")).thenReturn(principal);
        doReturn(Response.ok().entity(1001L).build()).when(this.userClient).getUserIdBySub(principal.getSub());

        doReturn("token-yyz").when(tokensPlaceHolder).getAccessToken();
        when(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_USERMANAGEMENT)).thenReturn("http://url.pt");
        Response expectedPermissionId = Response.ok().entity(1L).build();
        doReturn(expectedPermissionId).when(permissionClient).getIdByResourceAndAction(any(), any());
        doReturn(Response.ok(Boolean.TRUE).build()).when(tenantRoleClient).isPermissionExistentForUser(1001L, 1L, null);
        Response expectedAuthGranted = Response.ok().entity(Boolean.TRUE).build();
        doReturn(expectedAuthGranted).when(tenantRoleClient).isRoleExistentForUser(
                1001L, SystemRolesEnum.SYSTEM_ADMINISTRATOR.getRoleName(), null);

        Response response = tenantRolePermissionResource.update(1L, new TenantRolePermission());
        assertEquals(200, response.getStatus());
    }

    /**
     * Tests response from getById method
     */
    @Test
    public void test015GetById() {
        long id = 1L;
        SystemTenantRolePermission m = mock(SystemTenantRolePermission.class);
        doReturn(m).when(tenantRolePermissionService).get(id);
        Response response = tenantRolePermissionResource.getById(1L);
        assertEquals(200,response.getStatus());
    }
}
