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
package io.radien.webapp.linkedAuthorization;

import io.radien.api.entity.Page;
import io.radien.api.model.linked.authorization.SystemLinkedAuthorization;
import io.radien.api.model.permission.SystemPermission;
import io.radien.api.model.role.SystemRole;
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.model.user.SystemUser;
import io.radien.api.service.linked.authorization.LinkedAuthorizationRESTServiceAccess;
import io.radien.api.service.permission.PermissionRESTServiceAccess;
import io.radien.api.service.role.RoleRESTServiceAccess;
import io.radien.api.service.tenant.TenantRESTServiceAccess;
import io.radien.api.service.user.UserRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.permissionmanagement.client.entities.Permission;
import io.radien.ms.rolemanagement.client.entities.LinkedAuthorization;
import io.radien.ms.rolemanagement.client.entities.Role;
import io.radien.ms.tenantmanagement.client.entities.Tenant;
import io.radien.ms.usermanagement.client.entities.User;
import io.radien.webapp.AbstractBaseJsfTester;
import io.radien.webapp.DataModelEnum;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import javax.faces.application.FacesMessage;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.nullable;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Test class referring to the manager bean {@link LinkedAuthorizationManager}
 */
public class LinkedAuthorizationManagerTest extends AbstractBaseJsfTester {

    @InjectMocks
    private LinkedAuthorizationManager target;

    @Mock
    private PermissionRESTServiceAccess permissionRESTServiceAccess;

    @Mock
    private RoleRESTServiceAccess roleRESTServiceAccess;

    @Mock
    private TenantRESTServiceAccess tenantRESTServiceAccess;

    @Mock
    private UserRESTServiceAccess userRESTServiceAccess;

    @Mock
    private LinkedAuthorizationRESTServiceAccess linkedAuthorizationRESTServiceAccess;

    /**
     * Test for getter method {@link LinkedAuthorizationManager#getLinkedAuthorization()}
     */
    @Test
    public void testGetLinkedAuthorization() {
        LinkedAuthorization l = mock(LinkedAuthorization.class);
        target.setLinkedAuthorization(l);
        assertEquals(l, target.getLinkedAuthorization());
    }

    /**
     * Test for getter method {@link LinkedAuthorizationManager#getSelectedPermission()}
     */
    @Test
    public void testGetSelectedPermission() {
        Permission p = mock(Permission.class);
        target.setSelectedPermission(p);
        assertEquals(p, target.getSelectedPermission());
    }

    /**
     * Test for getter method {@link LinkedAuthorizationManager#getSelectedRole()} ()}
     */
    @Test
    public void testGetSelectedRole() {
        Role r = mock(Role.class);
        target.setSelectedRole(r);
        assertEquals(r, target.getSelectedRole());
    }

    /**
     * Test for getter method {@link LinkedAuthorizationManager#getSelectedTenant()}
     */
    @Test
    public void testGetSelectedTenant() {
        Tenant t = mock(Tenant.class);
        target.setSelectedTenant(t);
        assertEquals(t, target.getSelectedTenant());
    }

    /**
     * Test for getter method {@link LinkedAuthorizationManager#getSelectedUser()}
     */
    @Test
    public void testGetSelectedUser() {
        User u = mock(User.class);
        target.setSelectedUser(u);
        assertEquals(u, target.getSelectedUser());
    }

    /**
     * Test for method {@link LinkedAuthorizationManager#prepareFilterParam(String)}
     */
    @Test
    public void testPrepareFilterParam() {
        assertNull(target.prepareFilterParam(null));
        assertNull(target.prepareFilterParam(""));
        String filterParam = "test"; String expected = "test%";
        assertEquals(expected, target.prepareFilterParam(filterParam));
        assertEquals(expected, target.prepareFilterParam("test%"));
    }

    /**
     * Test for method {@link LinkedAuthorizationManager#filterTenantsByName(String)}
     * @throws SystemException thrown by tenant rest client. See
     * {@link TenantRESTServiceAccess#getAll(String, int, int, List, boolean)}
     */
    @Test
    public void testFilterTenantsByName() throws SystemException {
        List<SystemTenant> tenants = new ArrayList<>();
        tenants.add(mock(SystemTenant.class));

        Page<SystemTenant> page = new Page<>();
        page.setResults(tenants);

        when(tenantRESTServiceAccess.getAll("test%", 1, 10, null, false)).
                then(i -> page);

        List<? extends SystemTenant> list = target.filterTenantsByName("test");
        assertNotNull(list);
        assertEquals(1, list.size());
    }

    /**
     * Test for method {@link LinkedAuthorizationManager#filterRolesByName(String)}
     * @throws SystemException thrown by role rest client. See
     * {@link RoleRESTServiceAccess#getAll(String, int, int, List, boolean)}
     */
    @Test
    public void testFilterRolesByName() throws SystemException {
        List<SystemRole> roles = new ArrayList<>();
        roles.add(mock(SystemRole.class));

        Page<SystemRole> page = new Page<>();
        page.setResults(roles);

        when(roleRESTServiceAccess.getAll("test%", 1, 10, null, false)).
                then(i -> page);

        List<? extends SystemRole> list = target.filterRolesByName("test");
        assertNotNull(list);
        assertEquals(1, list.size());
    }

    /**
     * Test for method {@link LinkedAuthorizationManager#filterPermissionsByName(String)}
     * @throws SystemException thrown by permission rest client. See
     * {@link PermissionRESTServiceAccess#getAll(String, int, int, List, boolean)}
     */
    @Test
    public void testFilterPermissionsByName() throws SystemException {
        List<SystemPermission> permissions = new ArrayList<>();
        permissions.add(mock(SystemPermission.class));

        Page<SystemPermission> page = new Page<>();
        page.setResults(permissions);

        when(permissionRESTServiceAccess.getAll("test%", 1, 10, null, false)).
                then(i -> page);

        List<? extends SystemPermission> list = target.filterPermissionsByName("test");
        assertNotNull(list);
        assertEquals(1, list.size());
    }

    /**
     * Test for method {@link LinkedAuthorizationManager#getLogon(Long)}
     * @throws SystemException thrown by user rest client. See {@link UserRESTServiceAccess#getUserById(Long)}
     */
    @Test
    public void testGetLogon() throws SystemException {
        Long unknownUserId = 11111L;
        String logonFromExistentUser = "test";

        User existentUser = mock(User.class);
        when(existentUser.getId()).thenReturn(1L);
        when(existentUser.getLogon()).thenReturn(logonFromExistentUser);

        when(userRESTServiceAccess.getUserById(existentUser.getId())).
                then(i -> Optional.of(existentUser));
        when(userRESTServiceAccess.getUserById(unknownUserId)).
                then(i -> Optional.empty());

        assertEquals(logonFromExistentUser, target.getLogon(existentUser.getId()));
        assertNull(target.getLogon(unknownUserId));
    }

    /**
     * Test for method {@link LinkedAuthorizationManager#getTenantName(Long)}
     * @throws SystemException thrown by tenant rest client. See {@link TenantRESTServiceAccess#getTenantById(Long)}
     */
    @Test
    public void testGetTenantName() throws SystemException {
        Long unknownTenantId = 11111L;
        String nameForExistentTenant = "test";

        Tenant existentTenant = mock(Tenant.class);
        when(existentTenant.getId()).then(i -> 1L);
        when(existentTenant.getName()).then(i -> nameForExistentTenant);

        when(tenantRESTServiceAccess.getTenantById(existentTenant.getId())).
                then(i -> Optional.of(existentTenant));
        when(tenantRESTServiceAccess.getTenantById(unknownTenantId)).
                then(i -> Optional.empty());

        assertEquals(nameForExistentTenant, target.getTenantName(existentTenant.getId()));
        assertNull(target.getTenantName(unknownTenantId));
    }

    /**
     * Test for method {@link LinkedAuthorizationManager#getRoleName(Long)}
     * @throws Exception thrown by role rest client. See {@link RoleRESTServiceAccess#getRoleById(Long)}
     */
    @Test
    public void testGetRoleName() throws Exception {
        Long unknownRoleId = 11111L;
        String nameForExistentRole = "test";

        Role existentRole = mock(Role.class);
        when(existentRole.getId()).then(i -> 1L);
        when(existentRole.getName()).then(i -> nameForExistentRole);

        when(roleRESTServiceAccess.getRoleById(existentRole.getId())).
                then(i -> Optional.of(existentRole));
        when(roleRESTServiceAccess.getRoleById(unknownRoleId)).
                then(i -> Optional.empty());

        assertEquals(nameForExistentRole, target.getRoleName(existentRole.getId()));
        assertNull(target.getRoleName(unknownRoleId));
    }

    /**
     * Test for method {@link LinkedAuthorizationManager#getPermissionName(Long)}
     * @throws Exception thrown by permission rest client. See {@link PermissionRESTServiceAccess#getPermissionById(Long)}
     */
    @Test
    public void testGetPermissionName() throws Exception {
        Long unknownPermissionId = 11111L;
        String nameForExistentPermission = "test";

        Permission existentPermission = mock(Permission.class);
        when(existentPermission.getId()).then(i -> 1L);
        when(existentPermission.getName()).then(i -> nameForExistentPermission);

        when(permissionRESTServiceAccess.getPermissionById(existentPermission.getId())).
                then(i -> Optional.of(existentPermission));
        when(permissionRESTServiceAccess.getPermissionById(unknownPermissionId)).
                then(i -> Optional.empty());

        assertEquals(nameForExistentPermission, target.getPermissionName(existentPermission.getId()));
        assertNull(target.getPermissionName(unknownPermissionId));
    }

    /**
     * Test for method {@link LinkedAuthorizationManager#save(SystemLinkedAuthorization)}
     * @throws SystemException thrown by User and LinkedAuthorization rest client
     */
    @Test
    public void testSave() throws SystemException {
        String userLogon = "test";
        SystemLinkedAuthorization linkedAuthorization = new LinkedAuthorization();

        SystemTenant tenant = mock(SystemTenant.class);
        when(tenant.getId()).thenReturn(1L);
        target.setSelectedTenant(tenant);

        SystemRole role = mock(SystemRole.class);
        when(role.getId()).thenReturn(1L);
        target.setSelectedRole(role);

        SystemPermission permission = mock(SystemPermission.class);
        when(permission.getId()).thenReturn(1L);
        target.setSelectedPermission(permission);

        SystemUser user = mock(SystemUser.class);
        when(user.getId()).thenReturn(1L);
        when(user.getLogon()).thenReturn(userLogon);
        target.setSelectedUser(user);

        when(userRESTServiceAccess.getUserByLogon(userLogon)).thenReturn(Optional.of(user));
        when(linkedAuthorizationRESTServiceAccess.create(linkedAuthorization)).thenReturn(Boolean.TRUE);

        String redirectionIdPath = target.save(linkedAuthorization);
        assertEquals(DataModelEnum.LINKED_AUTHORIZATION_PATH.getValue(), redirectionIdPath);

        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture());

        FacesMessage captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_INFO, captured.getSeverity());
        assertEquals(DataModelEnum.SAVE_SUCCESS_MESSAGE.getValue(), captured.getSummary());
    }

    /**
     * Test for method {@link LinkedAuthorizationManager#save(SystemLinkedAuthorization)}
     * when is not possible to persist due some internal server error
     * regarding LinkedAuthorization API
     * @throws SystemException thrown by User and LinkedAuthorization rest client
     */
    @Test
    public void testSaveWithFailure() throws SystemException {
        String userLogon = "test";
        SystemLinkedAuthorization linkedAuthorization = new LinkedAuthorization();

        SystemTenant tenant = mock(SystemTenant.class);
        when(tenant.getId()).thenReturn(1L);
        target.setSelectedTenant(tenant);

        SystemRole role = mock(SystemRole.class);
        when(role.getId()).thenReturn(1L);
        target.setSelectedRole(role);

        SystemPermission permission = mock(SystemPermission.class);
        when(permission.getId()).thenReturn(1L);
        target.setSelectedPermission(permission);

        SystemUser user = mock(SystemUser.class);
        when(user.getId()).thenReturn(1L);
        when(user.getLogon()).thenReturn(userLogon);
        target.setSelectedUser(user);

        when(userRESTServiceAccess.getUserByLogon(userLogon)).thenReturn(Optional.of(user));
        when(linkedAuthorizationRESTServiceAccess.create(linkedAuthorization)).
                thenThrow(new SystemException("something went wrong"));

        String redirectionIdPath = target.save(linkedAuthorization);
        assertEquals(DataModelEnum.LINKED_AUTHORIZATION_PATH.getValue(), redirectionIdPath);

        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture());

        FacesMessage captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_ERROR, captured.getSeverity());
        assertEquals(DataModelEnum.SAVE_ERROR_MESSAGE.getValue(), captured.getSummary());
    }

    /**
     * Test for method {@link LinkedAuthorizationManager#save(SystemLinkedAuthorization)}
     * when the user could not be found by the informed logon
     * regarding LinkedAuthorization API
     * @throws SystemException thrown by User and LinkedAuthorization rest client
     */
    @Test
    public void testSaveWithFailureDueUserNotFound() throws SystemException {
        String userLogon = "test";
        SystemLinkedAuthorization linkedAuthorization = new LinkedAuthorization();

        when(userRESTServiceAccess.getUserByLogon(userLogon)).thenReturn(Optional.empty());

        String redirectionIdPath = target.save(linkedAuthorization);
        assertEquals(DataModelEnum.LINKED_AUTHORIZATION_PATH.getValue(), redirectionIdPath);

        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture());

        FacesMessage captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_ERROR, captured.getSeverity());
        assertEquals(DataModelEnum.SAVE_ERROR_MESSAGE.getValue(), captured.getSummary());
    }

    /**
     * Test for method {@link LinkedAuthorizationManager#edit(SystemLinkedAuthorization)}
     * when the entities (Permission) could not be found
     * regarding LinkedAuthorization API
     * @throws SystemException thrown by User, Role, Tenant, Permission and LinkedAuthorization rest client
     */
    @Test
    public void testEditorErrorDuePermissionNotFound() throws SystemException {
        LinkedAuthorization linkedAuthorization = new LinkedAuthorization();
        linkedAuthorization.setUserId(1L);
        linkedAuthorization.setRoleId(1L);
        linkedAuthorization.setTenantId(1L);
        linkedAuthorization.setPermissionId(1L);

        // Permission not found
        when(permissionRESTServiceAccess.getPermissionById(linkedAuthorization.getPermissionId())).
                then(i->Optional.empty());
        String redirectionIdPath = target.edit(linkedAuthorization);
        assertEquals(DataModelEnum.LINKED_AUTHORIZATION_PATH.getValue(), redirectionIdPath);

        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture());

        FacesMessage captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_ERROR, captured.getSeverity());
        assertEquals(DataModelEnum.EDIT_ERROR_MESSAGE.getValue(), captured.getSummary());
        assertTrue(captured.getDetail().contains(DataModelEnum.PERMISSION_NOT_FOUND_MESSAGE.getValue()));
    }

    /**
     * Test for method {@link LinkedAuthorizationManager#edit(SystemLinkedAuthorization)}
     * when the entities (Role) could not be found
     * regarding LinkedAuthorization API
     * @throws Exception thrown Role rest client
     */
    @Test
    public void testEditorErrorDueRoleNotFound() throws Exception {
        LinkedAuthorization linkedAuthorization = new LinkedAuthorization();
        linkedAuthorization.setUserId(1L);
        linkedAuthorization.setRoleId(1L);
        linkedAuthorization.setTenantId(1L);
        linkedAuthorization.setRoleId(1L);

        when(permissionRESTServiceAccess.getPermissionById(any())).
                then(i->Optional.of(new Permission()));

        // Role not found
        when(roleRESTServiceAccess.getRoleById(linkedAuthorization.getRoleId())).
                then(i->Optional.empty());
        String redirectionIdPath = target.edit(linkedAuthorization);
        assertEquals(DataModelEnum.LINKED_AUTHORIZATION_PATH.getValue(), redirectionIdPath);

        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture());

        FacesMessage captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_ERROR, captured.getSeverity());
        assertEquals(DataModelEnum.EDIT_ERROR_MESSAGE.getValue(), captured.getSummary());
        assertTrue(captured.getDetail().contains(DataModelEnum.ROLE_NOT_FOUND_MESSAGE.getValue()));
    }

    /**
     * Test for method {@link LinkedAuthorizationManager#edit(SystemLinkedAuthorization)}
     * when the entities (Role) could not be found
     * regarding LinkedAuthorization API
     * @throws SystemException thrown by Permission and Tenant rest clients
     * @throws Exception thrown Role rest client
     */
    @Test
    public void testEditorErrorDueTenantNotFound() throws Exception {
        LinkedAuthorization linkedAuthorization = new LinkedAuthorization();
        linkedAuthorization.setUserId(1L);
        linkedAuthorization.setRoleId(1L);
        linkedAuthorization.setTenantId(1L);
        linkedAuthorization.setRoleId(1L);

        when(permissionRESTServiceAccess.getPermissionById(any())).
                then(i->Optional.of(new Permission()));
        when(roleRESTServiceAccess.getRoleById(any())).
                then(i->Optional.of(new Role()));

        // Tenant not found
        when(tenantRESTServiceAccess.getTenantById(linkedAuthorization.getTenantId())).
                then(i->Optional.empty());

        String redirectionIdPath = target.edit(linkedAuthorization);
        assertEquals(DataModelEnum.LINKED_AUTHORIZATION_PATH.getValue(), redirectionIdPath);

        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture());

        FacesMessage captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_ERROR, captured.getSeverity());
        assertEquals(DataModelEnum.EDIT_ERROR_MESSAGE.getValue(), captured.getSummary());
        assertTrue(captured.getDetail().contains(DataModelEnum.TENANT_NOT_FOUND_MESSAGE.getValue()));
    }

    /**
     * Test for method {@link LinkedAuthorizationManager#edit(SystemLinkedAuthorization)}
     * when the entities (User) could not be found
     * regarding LinkedAuthorization API
     * @throws SystemException thrown by Permission and Tenant rest clients
     * @throws Exception thrown Role rest client
     */
    @Test
    public void testEditorErrorDueUserNotFound() throws Exception {
        LinkedAuthorization linkedAuthorization = new LinkedAuthorization();
        linkedAuthorization.setUserId(1L);
        linkedAuthorization.setRoleId(1L);
        linkedAuthorization.setTenantId(1L);
        linkedAuthorization.setRoleId(1L);

        when(permissionRESTServiceAccess.getPermissionById(any())).
                then(i->Optional.of(new Permission()));
        when(roleRESTServiceAccess.getRoleById(any())).
                then(i->Optional.of(new Role()));
        when(tenantRESTServiceAccess.getTenantById(any())).
                then(i->Optional.of(new Tenant()));

        // User not found
        when(userRESTServiceAccess.getUserById(linkedAuthorization.getUserId())).
                then(i->Optional.empty());

        String redirectionIdPath = target.edit(linkedAuthorization);
        assertEquals(DataModelEnum.LINKED_AUTHORIZATION_PATH.getValue(), redirectionIdPath);

        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture());

        FacesMessage captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_ERROR, captured.getSeverity());
        assertEquals(DataModelEnum.EDIT_ERROR_MESSAGE.getValue(), captured.getSummary());
        assertTrue(captured.getDetail().contains(DataModelEnum.USER_NOT_FOUND_MESSAGE.getValue()));
    }
}
