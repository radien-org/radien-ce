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
import io.radien.exception.RoleNotFoundException;
import io.radien.exception.SystemException;
import io.radien.exception.TenantRoleException;
import io.radien.exception.TenantRoleIllegalArgumentException;
import io.radien.exception.TenantRoleNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.authz.client.PermissionClient;
import io.radien.ms.authz.client.TenantRoleClient;
import io.radien.ms.authz.client.UserClient;
import io.radien.ms.openid.entities.Principal;
import io.radien.ms.rolemanagement.client.entities.TenantRole;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.MethodOrderer;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Response;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * TenantRole resource requests test
 * {@link io.radien.ms.rolemanagement.services.TenantRoleResource}
 *
 * @author Newton Carvalho
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TenantRoleResourceTest {

    @InjectMocks
    TenantRoleResource tenantRoleResource;

    @Mock
    TenantRoleBusinessService tenantRoleBusinessService;

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
     * Testing getAll
     */
    @Test
    @Order(1)
    public void getAll() {
        Response response = tenantRoleResource.getAll(1,10);
        assertEquals(200,response.getStatus());
    }

    /**
     * Testing getAll with exception during the processing
     */
    @Test
    @Order(2)
    public void getAllWithException() {
        when(tenantRoleResource.getAll(1,10))
                .thenThrow(new RuntimeException());
        Response response = tenantRoleResource.getAll(1,10);
        assertEquals(500,response.getStatus());
    }

    /**
     * Tests response of the get specific association
     */
    @Test
    @Order(3)
    public void testGetSpecific() {
        Response response = tenantRoleResource.getSpecific(2L,2L,true);
        assertEquals(200,response.getStatus());
    }

    /**
     * Tests exception from the get specific association
     */
    @Test
    @Order(4)
    public void testGetSpecificWithException() {
        doThrow(new RuntimeException("error")).when(tenantRoleBusinessService).
                getSpecific(1L, 2L, true);
        Response response = tenantRoleResource.getSpecific(1L,2L,true);
        assertEquals(500,response.getStatus());
    }

    /**
     * Tests response from getById method
     */
    @Test
    @Order(5)
    public void testGetById() {
        Response response = tenantRoleResource.getById(1L);
        assertEquals(200,response.getStatus());
    }

    /**
     * Tests response from getById when exceptions occur during the processing
     * @throws TenantRoleNotFoundException when thrown denotes a Tenant Role could not be found
     * for a given identifier
     */
    @Test
    @Order(6)
    public void testGetByIdWithExceptions() throws TenantRoleNotFoundException {
        doThrow(new TenantRoleNotFoundException("No Tenant Role found for id")).
                when(tenantRoleBusinessService).getById(1L);
        doThrow(new RuntimeException("error")).
                when(tenantRoleBusinessService).getById(2L);

        // Association Not Found
        Response response = tenantRoleResource.getById(1L);
        assertEquals(404,response.getStatus());

        // Generic Error
        response = tenantRoleResource.getById(2L);
        assertEquals(500,response.getStatus());
    }

    /**
     * Tests response from delete method
     */
    @Test
    @Order(7)
    public void testDelete() {
        Response response = tenantRoleResource.delete(1L);
        assertEquals(200,response.getStatus());
    }

    /**
     * Tests response from delete method when exceptions occur during the processing
     */
    @Test
    @Order(8)
    public void testDeleteWithException() {
        try {
            when(tenantRoleBusinessService.delete(1L)).
                    thenThrow(new TenantRoleException("Tenant role cannot be deleted"));

            when(tenantRoleBusinessService.delete(2L)).
                    thenThrow(new RuntimeException("unpredictable error"));
        }
        catch (Exception e) {
            fail("unexpected");
        }
        Response response = tenantRoleResource.delete(1L);
        assertEquals(400,response.getStatus());
        response = tenantRoleResource.delete(2L);
        assertEquals(500,response.getStatus());
    }

    /**
     * Tests response from save method
     */
    @Test
    @Order(9)
    public void testSave() {
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


        Response response = tenantRoleResource.save(new TenantRole());
        assertEquals(200,response.getStatus());
    }

    /**
     * Tests response from save method when exceptions occur during the processing
     */
    @Test
    @Order(10)
    public void testSaveWithException() {
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

        TenantRole tenantRole = new TenantRole();
        tenantRole.setRoleId(1L); tenantRole.setTenantId(2L);
        try {
            doThrow(new UniquenessConstraintException()).doThrow(new TenantRoleException("Found similar tenant role")).
                    doThrow(new SystemException("Communication breakdown")).when(tenantRoleBusinessService).save(any());
        }
        catch (Exception e) {
            fail("unexpected");
        }
        Response response = tenantRoleResource.save(tenantRole);
        assertEquals(400,response.getStatus());

        response = tenantRoleResource.save(tenantRole);
        assertEquals(400,response.getStatus());

        response = tenantRoleResource.save(tenantRole);
        assertEquals(500,response.getStatus());
    }

    /**
     * Tests response from exists method
     */
    @Test
    @Order(11)
    public void testExists() {
        when(tenantRoleBusinessService.existsAssociation(1L, 2L)).
                thenReturn(Boolean.TRUE);
        when(tenantRoleBusinessService.existsAssociation(1L, 3L)).
                thenReturn(Boolean.FALSE);

        Response responseOK = tenantRoleResource.exists(1L, 2L);
        assertEquals(204, responseOK.getStatus());

        Response responseNOK = tenantRoleResource.exists(1L, 3L);
        assertEquals(404, responseNOK.getStatus());
    }

    /**
     * Tests response from exists method when exceptions occur during the processing
     */
    @Test
    @Order(12)
    public void testExistsWithException() {
        doThrow(new RuntimeException("error")).
                when(tenantRoleBusinessService).existsAssociation(1L, 2L);
        Response response = tenantRoleResource.exists(1L, 2L);
        assertEquals(500, response.getStatus());
    }

    /**
     * Tests response from getPermissions method
     */
    @Test
    @Order(13)
    public void testGetPermissions() {
        Response response = tenantRoleResource.getPermissions(1L, 2L, 3L);
        assertEquals(200, response.getStatus());
    }

    /**
     * Tests response from getPermissions method when exceptions occur during the processing
     */
    @Test
    @Order(14)
    public void testGetPermissionsWithException() {
        try {
            doThrow(new RuntimeException("error")).
                    when(tenantRoleBusinessService).getPermissions(1L, 2L, 3L);
        }
        catch (Exception e) {
            fail("unexpected");
        }
        Response response = tenantRoleResource.getPermissions(1L, 2L, 3L);
        assertEquals(500, response.getStatus());
    }

    /**
     * Tests response from getTenants method
     */
    @Test
    @Order(15)
    public void testGetTenants() {
        Response response = tenantRoleResource.getTenants(1L, 2L);
        assertEquals(200, response.getStatus());
    }

    /**
     * Tests response from getTenants method when exceptions occur during the processing
     */
    @Test
    @Order(16)
    public void testGetTenantsWithException() {
        try {
            doThrow(new RuntimeException("error")).
                    when(tenantRoleBusinessService).getTenants(1L, 2L);
        }
        catch (Exception e) {
            fail("unexpected");
        }
        Response response = tenantRoleResource.getTenants(1L, 2L);
        assertEquals(500, response.getStatus());
    }

    /**
     * Tests response from isRoleExistentForUser method
     */
    @Test
    @Order(17)
    public void testIsRoleExistentForUser() {
        Response response = tenantRoleResource.isRoleExistentForUser(1L, "test", 2L);
        assertEquals(200, response.getStatus());
    }

    /**
     * Tests response from isRoleExistentForUser method when exceptions occur
     * during the processing
     */
    @Test
    @Order(18)
    public void testIsRoleExistentForUserWithException() {
        try {
            doThrow(new RuntimeException("error")).
                    when(tenantRoleBusinessService).isRoleExistentForUser(1L, "test", 2L);
        }
        catch (Exception e) {
            fail("unexpected");
        }
        Response response = tenantRoleResource.isRoleExistentForUser(1L, "test", 2L);
        assertEquals(500, response.getStatus());
    }

    /**
     * Tests response from isAnyRoleExistentForUser method
     */
    @Test
    @Order(19)
    public void testIsAnyRoleExistentForUser() {
        Response response = tenantRoleResource.isAnyRoleExistentForUser(1L,
                Arrays.asList("test", "test2"), 2L);
        assertEquals(200, response.getStatus());
    }

    /**
     * Tests response from isAnyRoleExistentForUser method when exceptions occur
     * during the processing
     */
    @Test
    @Order(20)
    public void testIsAnyRoleExistentForUserWithException() {
        try {
            doThrow(new RuntimeException("error")).
                    when(tenantRoleBusinessService).isAnyRoleExistentForUser(1L,
                    Arrays.asList("test", "test2"), 2L);
        }
        catch (Exception e) {
            fail("unexpected");
        }
        Response response = tenantRoleResource.isAnyRoleExistentForUser(1L,
                Arrays.asList("test", "test2"), 2L);
        assertEquals(500, response.getStatus());
    }

    /**
     * Tests response from isPermissionExistentForUser method
     */
    @Test
    @Order(21)
    public void testIsPermissionExistentForUser() {
        Response response = tenantRoleResource.isPermissionExistentForUser(1L,
                2L, 3L);
        assertEquals(200, response.getStatus());
    }

    /**
     * Tests response from isPermissionExistentForUser method when exceptions occur
     * during the processing
     */
    @Test
    @Order(22)
    public void testIsPermissionExistentForUserWithException() {
        try {
            doThrow(new RuntimeException("error")).
                    when(tenantRoleBusinessService).isPermissionExistentForUser(1L,
                    2L, 3L);
        }
        catch (Exception e) {
            fail("unexpected");
        }
        Response response = tenantRoleResource.isPermissionExistentForUser(1L,
                2L, 3L);
        assertEquals(500, response.getStatus());
    }


    /**
     * Tests response from getRoles method
     */
    @Test
    public void testGetRoles() {
        Response response = tenantRoleResource.getRolesForUserTenant(1L, 1L);
        assertEquals(200, response.getStatus());
    }

    /**
     * Tests response from getRoles method when exceptions occur during the processing
     */
    @Test
    public void testGetRolesWithException() {
        try {
            doThrow(new RoleNotFoundException("error")).
                    when(tenantRoleBusinessService).getRolesForUserTenant(1L, 1L);
        }
        catch (Exception e) {
            fail("unexpected");
        }
        Response response = tenantRoleResource.getRolesForUserTenant(1L, 1L);
        assertEquals(404, response.getStatus());
    }

    /**
     * Tests response from getIdByTenantRole
     */
    @Test
    void testGetIdByTenantRole() {
        Response response = tenantRoleResource.getById(1L);
        assertEquals(200,response.getStatus());
    }

    /**
     * Tests response from getIdByTenantRole when exceptions occurs during the processing
     * @throws TenantRoleNotFoundException thrown when no id could be found for the tenant role combination
     * @throws TenantRoleIllegalArgumentException thrown when params were not correctly informed
     */
    @Test
    void testGetIdByTenantRoleWithExceptions() throws TenantRoleNotFoundException, TenantRoleIllegalArgumentException {
        doThrow(new TenantRoleNotFoundException("No Tenant Role found for id")).
                when(tenantRoleBusinessService).getTenantRoleId(1L, 1L);
        doThrow(new RuntimeException("error")).
                when(tenantRoleBusinessService).getTenantRoleId(2L, 2L);
        doThrow(new TenantRoleIllegalArgumentException("error")).
                when(tenantRoleBusinessService).getTenantRoleId(null, 2L);

        // Association Not Found
        Response response = tenantRoleResource.getIdByTenantRole(1L, 1L);
        assertEquals(404,response.getStatus());

        // Generic Error
        response = tenantRoleResource.getIdByTenantRole(2L,2L);
        assertEquals(500,response.getStatus());

        // Invalid request
        response = tenantRoleResource.getIdByTenantRole(null,2L);
        assertEquals(400,response.getStatus());
    }

}
