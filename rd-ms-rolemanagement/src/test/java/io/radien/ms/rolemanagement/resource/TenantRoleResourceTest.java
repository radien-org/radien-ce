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
import io.radien.api.security.TokensPlaceHolder;
import io.radien.api.service.role.SystemRolesEnum;
import io.radien.exception.InvalidArgumentException;
import io.radien.ms.authz.client.PermissionClient;
import io.radien.ms.authz.client.TenantRoleClient;
import io.radien.ms.authz.client.UserClient;
import io.radien.ms.openid.entities.Principal;
import io.radien.ms.rolemanagement.client.entities.TenantRole;
import io.radien.ms.rolemanagement.service.TenantRoleBusinessService;
import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * TenantRole resource requests test
 * {@link TenantRoleResource}
 *
 * @author Newton Carvalho
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TenantRoleResourceTest {

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
    void before(){
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Testing getAll
     */
    @Test
    @Order(1)
    void getAll() {
        Response response = tenantRoleResource.getAll(null, null, 1,10, null, true);
        assertEquals(200,response.getStatus());
    }

    /**
     * Tests response of the get specific association
     */
    @Test
    @Order(3)
    void testGetSpecific() {
        Response response = tenantRoleResource.getSpecific(2L,2L,true);
        assertEquals(200,response.getStatus());
    }

    /**
     * Tests response from getById method
     */
    @Test
    @Order(5)
    void testGetById() {
        Response response = tenantRoleResource.getById(1L);
        assertEquals(200,response.getStatus());
    }

    /**
     * Tests response from delete method
     */
    @Test
    @Order(7)
    void testDelete() {
        Response response = tenantRoleResource.delete(1L);
        assertEquals(200,response.getStatus());
    }

    /**
     * Tests response from save method
     */
    @Test
    @Order(9)
    void testSave() {
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


        Response response = tenantRoleResource.create(new TenantRole());
        assertEquals(200,response.getStatus());
    }

    /**
     * Tests response from exists method
     */
    @Test
    @Order(11)
    void testExists() throws InvalidArgumentException {
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
     * Tests response from isRoleExistentForUser method
     */
    @Test
    @Order(17)
    void testIsRoleExistentForUser() {
        Response response = tenantRoleResource.isRoleExistentForUser(1L, "test", 2L);
        assertEquals(200, response.getStatus());
    }

    /**
     * Tests response from isAnyRoleExistentForUser method
     */
    @Test
    @Order(19)
    void testIsAnyRoleExistentForUser() {
        Response response = tenantRoleResource.isAnyRoleExistentForUser(1L,
                Arrays.asList("test", "test2"), 2L);
        assertEquals(200, response.getStatus());
    }

    /**
     * Tests response from isPermissionExistentForUser method
     */
    @Test
    @Order(21)
    void testIsPermissionExistentForUser() {
        Response response = tenantRoleResource.isPermissionExistentForUser(1L,
                2L, 3L);
        assertEquals(200, response.getStatus());
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
     * Tests response from save method when exceptions occur during the processing
     */
    @Test
    void testSaveNotAllowed() {
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
        doReturn(10L).when(tenantRoleBusinessService).count();
        doReturn(Response.ok(Boolean.FALSE).build()).when(tenantRoleClient).isPermissionExistentForUser(1001L,1L,null);
        doReturn(Response.ok(Boolean.FALSE).build()).when(tenantRoleClient).
                isRoleExistentForUser(1001L, SystemRolesEnum.SYSTEM_ADMINISTRATOR.getRoleName(), null);
        TenantRole tenantRole = new TenantRole();
        tenantRole.setRoleId(1L); tenantRole.setTenantId(2L);
        Response response = tenantRoleResource.create(tenantRole);
        assertEquals(403,response.getStatus());
    }

    /**
     * Tests response from update method
     */
    @Test
    void testUpdate() {
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
        doReturn(Response.ok(Boolean.TRUE).build()).when(tenantRoleClient).isRoleExistentForUser(anyLong(), anyString(), any());

        Response response = tenantRoleResource.update(1L, new TenantRole());
        assertEquals(200,response.getStatus());
    }

    /**
     * Tests response from update method when exceptions occur during the processing
     */
    @Test
    void testUpdateNotAllowed() {
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
        doReturn(10L).when(tenantRoleBusinessService).count();
        doReturn(Response.ok(Boolean.FALSE).build()).when(tenantRoleClient).isPermissionExistentForUser(1001L,1L,null);
        doReturn(Response.ok(Boolean.FALSE).build()).when(tenantRoleClient).
                isRoleExistentForUser(1001L, SystemRolesEnum.SYSTEM_ADMINISTRATOR.getRoleName(), null);
        TenantRole tenantRole = new TenantRole();
        tenantRole.setRoleId(1L); tenantRole.setTenantId(2L);
        Response response = tenantRoleResource.update(1L, tenantRole);
        assertEquals(403,response.getStatus());
    }
}
