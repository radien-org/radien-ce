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
package io.radien.ms.rolemanagement.services;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.security.TokensPlaceHolder;
import io.radien.api.service.role.SystemRolesEnum;
import io.radien.api.service.tenantrole.TenantRoleUserServiceAccess;
import io.radien.exception.SystemException;
import io.radien.exception.TenantRoleException;
import io.radien.exception.TenantRoleNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.authz.client.PermissionClient;
import io.radien.ms.authz.client.TenantRoleClient;
import io.radien.ms.authz.client.UserClient;
import io.radien.ms.openid.entities.Principal;
import io.radien.ms.rolemanagement.client.entities.TenantRoleUser;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * TenantRoleUser resource requests test
 * {@link io.radien.ms.rolemanagement.services.TenantRoleUserResource}
 *
 * @author Newton Carvalho
 */
public class TenantRoleUserResourceTest {

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
    public void before(){
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Tests response from getUsers method
     */
    @Test
    public void testGetUsers() {
        Response response = tenantRoleUserResource.getAll(1L,1L,
                2, 3);
        assertEquals(200, response.getStatus());
    }

    /**
     * Tests response from getUsers method when exceptions occur
     * during the processing
     */
    @Test
    public void testGetUserWithException() {
        doThrow(new RuntimeException("error")).
                when(tenantRoleUserServiceAccess).getAll(1L,1L,
                2, 3);
        Response response = tenantRoleUserResource.getAll(1L,1L,
                2, 3);
        assertEquals(500, response.getStatus());
    }

    /**
     * Tests response from getUsers method
     */
    @Test
    public void testGetUsersIds() {
        Response response = tenantRoleUserResource.getAllUserIds(1L,1L,
                2, 3);
        assertEquals(200, response.getStatus());
    }

    /**
     * Tests response from getUsers method when exceptions occur
     * during the processing
     */
    @Test
    public void testGetUsersIdsWithException() {
        doThrow(new RuntimeException("error")).
                when(tenantRoleUserServiceAccess).getAllUserIds(1L,1L,
                2, 3);
        Response response = tenantRoleUserResource.getAllUserIds(1L,1L,
                2, 3);
        assertEquals(500, response.getStatus());
    }

    /**
     * Test asserts Response from
     * UnAssignedUserTenantRoles
     */
    @Test
    public void testUnAssignUserTenantRoles() {
        Response response = tenantRoleUserResource.unAssignUser(1L, Arrays.asList(1L, 2L),1L);
        assertEquals(200, response.getStatus());
    }

    /**
     * Test asserts Response from unAssignUser
     * @throws TenantRoleException to denote cases in which business rules were not attended
     * @throws SystemException to denote cases of communication issues with the endpoint
     */
    @Test
    public void testUnAssignUserTenantRolesException() throws TenantRoleException, SystemException {
        Collection<Long> rolesIds = Arrays.asList(1L, 2L);
        doThrow(TenantRoleException.class).when(tenantRoleUserBusinessService).
                unAssignUser(anyLong(), anyCollection(), anyLong());
        Response response = tenantRoleUserResource.unAssignUser(1L, rolesIds, 1L);
        assertEquals(400, response.getStatus());

        doThrow(new SystemException("communication issue")).when(tenantRoleUserBusinessService).
                unAssignUser(anyLong(), anyCollection(), anyLong());
        Response response1 = tenantRoleUserResource.unAssignUser(1L, rolesIds, 1L);
        assertEquals(500, response1.getStatus());
    }

    /**
     * Tests response from assignUser method
     */
    @Test
    public void testAssignUser() {

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

        Response response = tenantRoleUserResource.assignUser(new TenantRoleUser());
        assertEquals(200, response.getStatus());
    }

    /**
     * Tests response from assignUser method when exceptions occur
     * during the processing
     */
    @Test
    public void testAssignUserWithException() {
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

        TenantRoleUser tenantRoleUser = new TenantRoleUser();
        try {
            doThrow(new UniquenessConstraintException("error")).
                    doThrow(new TenantRoleException("error")).
                    doThrow(new RuntimeException("error")).
                    when(tenantRoleUserBusinessService).assignUser(any());
        }
        catch (Exception e) {
            fail("unexpected");
        }
        Response response = tenantRoleUserResource.assignUser(tenantRoleUser);
        assertEquals(400, response.getStatus());

        response = tenantRoleUserResource.assignUser(tenantRoleUser);
        assertEquals(400, response.getStatus());

        response = tenantRoleUserResource.assignUser(tenantRoleUser);
        assertEquals(500, response.getStatus());
    }


    /**
     * Tests response from getTenants method
     */
    @Test
    public void testGetTenants() {
        Response response = tenantRoleUserResource.getTenants(1L, 2L);
        assertEquals(200, response.getStatus());
    }

    /**
     * Tests response from getTenants method when exceptions occur during the processing
     */
    @Test
    public void testGetTenantsWithException() {
        try {
            doThrow(new RuntimeException("error")).
                    when(tenantRoleUserBusinessService).getTenants(1L, 2L);
        }
        catch (Exception e) {
            fail("unexpected");
        }
        Response response = tenantRoleUserResource.getTenants(1L, 2L);
        assertEquals(500, response.getStatus());
        response = tenantRoleUserResource.getTenants(null, 2L);
        assertEquals(400, response.getStatus());
    }

    /**
     * Tests response from unassignUser method
     */
    @Test
    public void testUnAssignUser() {
        Response response = tenantRoleUserResource.unAssignUser(1L,
                Collections.singletonList(2L), 3L);
        assertEquals(200, response.getStatus());
    }

    /**
     * Tests response from unassignUser method when exceptions occur
     * during the processing
     */
    @Test
    public void testUnAssignUserWithException() {
        Collection<Long> rolesIds = Collections.singletonList(2L);
        try {
            doThrow(new TenantRoleException("error")).
                    doThrow(new RuntimeException("error")).
                    when(tenantRoleUserBusinessService).unAssignUser(1L,
                    rolesIds, 3L);
        }
        catch (Exception e) {
            fail("unexpected");
        }
        Response response = tenantRoleUserResource.unAssignUser(1L,
                rolesIds, 3L);
        assertEquals(400, response.getStatus());

        response = tenantRoleUserResource.unAssignUser(1L,
                rolesIds, 3L);
        assertEquals(500, response.getStatus());
    }

    /**
     * Tests response from delete method
     */
    @Test
    public void testDelete() {
        Response response = tenantRoleUserResource.delete(1L);
        assertEquals(200,response.getStatus());
    }

    /**
     * Tests response from delete method when exceptions occur during the processing
     */
    @Test
    public void testDeleteWithException() {
        try {
            when(tenantRoleUserBusinessService.delete(1L)).
                    thenThrow(new TenantRoleNotFoundException("Tenant role user cannot be found"));

            when(tenantRoleUserBusinessService.delete(2L)).
                    thenThrow(new RuntimeException("unpredictable error"));
        }
        catch (Exception e) {
            fail("unexpected");
        }
        Response response = tenantRoleUserResource.delete(1L);
        assertEquals(400,response.getStatus());
        response = tenantRoleUserResource.delete(2L);
        assertEquals(500,response.getStatus());
    }


}
