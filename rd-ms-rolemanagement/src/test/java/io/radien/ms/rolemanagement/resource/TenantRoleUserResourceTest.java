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
import io.radien.api.model.tenantrole.SystemTenantRoleUser;
import io.radien.api.security.TokensPlaceHolder;
import io.radien.api.service.role.SystemRolesEnum;
import io.radien.api.service.tenantrole.TenantRoleUserServiceAccess;
import io.radien.ms.authz.client.PermissionClient;
import io.radien.ms.authz.client.TenantRoleClient;
import io.radien.ms.authz.client.UserClient;
import io.radien.ms.openid.entities.Principal;
import io.radien.ms.rolemanagement.client.entities.TenantRoleUser;
import io.radien.ms.rolemanagement.service.TenantRoleUserBusinessService;
import java.util.Arrays;
import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * TenantRoleUser resource requests test
 * {@link TenantRoleUserResource}
 *
 * @author Newton Carvalho
 */
class TenantRoleUserResourceTest {

    @InjectMocks
    TenantRoleUserResource tenantRoleUserResource;

    @Mock
    TenantRoleUserServiceAccess tenantRoleUserServiceAccess;

    @Mock
    TenantRoleUserBusinessService tenantRoleUserBusinessService;

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

    @BeforeEach
    void before(){
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Tests response from getUsers method
     */
    @Test
    void testGetUsers() {
        Response response = tenantRoleUserResource.getAll(1L,1L,
                2, 3, null, false);
        assertEquals(200, response.getStatus());
    }

    /**
     * Tests response from getUsers method
     */
    @Test
    void testGetUsersIds() {
        Response response = tenantRoleUserResource.getAllUserIds(1L,1L,
                2, 3);
        assertEquals(200, response.getStatus());
    }

    /**
     * Test asserts Response from
     * UnAssignedUserTenantRoles
     */
    @Test
    void testUnAssignUserTenantRoles() {
        Response response = tenantRoleUserResource.unAssignUser(1L, Arrays.asList(1L, 2L),1L);
        assertEquals(200, response.getStatus());
    }

    /**
     * Tests response from assignUser method
     */
    @Test
    void testAssignUser() {

        Principal principal = new Principal();
        principal.setSub("aaa-bbb-ccc-ddd");
        HttpSession session = mock(HttpSession.class);

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

        Response response = tenantRoleUserResource.assignUser(new TenantRoleUser());
        assertEquals(200, response.getStatus());
    }

    /**
     * Tests response from getTenants method
     */
    @Test
    void testGetTenants() {
        Response response = tenantRoleUserResource.getTenants(1L, 2L);
        assertEquals(200, response.getStatus());
    }

    /**
     * Tests response from unassignUser method
     */
    @Test
    void testUnAssignUser() {
        Response response = tenantRoleUserResource.unAssignUser(1L,
                Collections.singletonList(2L), 3L);
        assertEquals(200, response.getStatus());
    }

    /**
     * Tests response from delete method
     */
    @Test
    void testDelete() {
        Response response = tenantRoleUserResource.delete(1L);
        assertEquals(200,response.getStatus());
    }

    /**
     * Tests response from update method
     */
    @Test
    void testUpdateTenantRoleUser() {
        Principal principal = new Principal();
        principal.setSub("aaa-bbb-ccc-ddd");
        HttpSession session = mock(HttpSession.class);

        when(servletRequest.getSession()).thenReturn(session);
        when(servletRequest.getSession(false)).thenReturn(session);
        when(session.getAttribute("USER")).thenReturn(principal);
        doReturn(Response.ok().entity(1001L).build()).when(this.userClient).getUserIdBySub(principal.getSub());

        doReturn("token-yyz").when(tokensPlaceHolder).getAccessToken();
        when(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_USERMANAGEMENT)).thenReturn("http://url.pt");
        Response expectedUserId = Response.ok().entity(1L).build();
        doReturn(expectedUserId).when(permissionClient).getIdByResourceAndAction(any(),any());
        doReturn(Response.ok(Boolean.TRUE).build()).when(tenantRoleClient).isPermissionExistentForUser(1001L,1L,null);
        Response expectedAuthGranted = Response.ok().entity(Boolean.TRUE).build();
        doReturn(expectedAuthGranted).when(tenantRoleClient).isRoleExistentForUser(
                1001L, SystemRolesEnum.SYSTEM_ADMINISTRATOR.getRoleName(), null);

        Response response = tenantRoleUserResource.update(1L, new TenantRoleUser());
        assertEquals(200, response.getStatus());
    }

    /**
     * Tests response from getSpecfic method
     */
    @Test
    void testGetTenantRoleUsers() {
        Response response = tenantRoleUserResource.getSpecific(1L,1L,
                false);
        assertEquals(200, response.getStatus());
    }

    /**
     * Tests response from getById method
     */
    @Test
    void testGetById() {
        long id = 1L;
        SystemTenantRoleUser m = mock(SystemTenantRoleUser.class);
        doReturn(m).when(tenantRoleUserServiceAccess).get(id);
        Response response = tenantRoleUserResource.getById(1L);
        assertEquals(200,response.getStatus());
    }

    /**
     * Tests response from getRoles method
     */
    @Test
    void testGetRoles() {
        Response response = tenantRoleUserResource.getRolesForUserTenant(1L, 1L);
        assertEquals(200, response.getStatus());
    }

}
