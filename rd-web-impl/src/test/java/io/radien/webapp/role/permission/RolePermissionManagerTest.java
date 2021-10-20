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
package io.radien.webapp.role.permission;

import io.radien.api.model.permission.SystemPermission;
import io.radien.api.model.role.SystemRole;
import io.radien.api.model.tenant.SystemActiveTenant;
import io.radien.api.model.tenantrole.SystemTenantRole;
import io.radien.api.service.tenantrole.TenantRolePermissionRESTServiceAccess;
import io.radien.api.service.tenantrole.TenantRoleRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.exception.TenantRoleIllegalArgumentException;
import io.radien.ms.permissionmanagement.client.entities.Permission;
import io.radien.ms.rolemanagement.client.entities.Role;
import io.radien.ms.rolemanagement.client.entities.TenantRole;
import io.radien.ms.tenantmanagement.client.entities.ActiveTenant;
import io.radien.webapp.DataModelEnum;
import io.radien.webapp.JSFUtil;
import io.radien.webapp.JSFUtilAndFaceContextMessagesTest;
import io.radien.webapp.activeTenant.ActiveTenantDataModelManager;
import io.radien.webapp.util.TenantRoleUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.primefaces.event.ToggleEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Class that aggregates UnitTest cases for RolePermissionManager
 *
 * @author Rajesh Gavvala
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({JSFUtil.class, FacesContext.class, ExternalContext.class})
public class RolePermissionManagerTest extends JSFUtilAndFaceContextMessagesTest {

    @InjectMocks
    private RolePermissionManager rolePermissionManager;

    @Mock
    private TenantRoleRESTServiceAccess tenantRoleRESTServiceAccess;

    @Mock
    private TenantRolePermissionRESTServiceAccess tenantRolePermissionRESTServiceAccess;

    @Mock
    private ActiveTenantDataModelManager activeTenantDataModelManager;

    @Mock
    private TenantRoleUtil tenantRoleUtil;

    private final ToggleEvent toggleEvent = mock(ToggleEvent.class);

    private SystemRole systemRole;

    private SystemActiveTenant systemActiveTenant;

    private List<SystemPermission> systemPermissionList;
    private List<? extends SystemPermission> retrievedSystemPermissionList;

    private final Map<Long, Boolean> isPermissionsAssigned = new HashMap<>();

    private Set<Long> assignableRolePermissions = new HashSet<>();
    private Set<Long> unassignedRolePermissions = new HashSet<>();

    private static final int TOTAL_PERMISSION_RESULTS = 5;

    /**
     * Prepares require objects when requires to invoke
     */
    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);

        handleJSFUtilAndFaceContextMessages();

        systemRole = new Role();
        systemRole.setId(1L);
        rolePermissionManager.setSystemRole(systemRole);

        initPermissionData();
        assignableRolePermissions = systemPermissionList.subList(0,2).stream().map(SystemPermission::getId).collect(Collectors.toSet());
        unassignedRolePermissions = systemPermissionList.subList(2,4).stream().map(SystemPermission::getId).collect(Collectors.toSet());

        retrievedSystemPermissionList = initRetrievedPermissionList();

        systemActiveTenant = new ActiveTenant();
        systemActiveTenant.setTenantId(1L);
        systemActiveTenant.setUserId(1L);

        rolePermissionManager.setSystemActiveTenant(systemActiveTenant);
    }

    /**
     * Test method onRowExpand()
     * @throws SystemException if any error
     */
    @Test
    public void testOnRowExpand() throws SystemException {
        assertNull(toggleEvent.getData());
        rolePermissionManager.onRowExpand(toggleEvent);
    }

    /**
     * Test for method {@link RolePermissionManager#loadRolePermissions(SystemRole)}
     * @throws SystemException in case of any error thrown by TenantRole Rest client
     */
    @Test
    public void testOnLoadRolePermissions() throws SystemException {
        SystemRole systemRole = new Role();
        systemRole.setId(1L);
        when(tenantRolePermissionRESTServiceAccess.getPermissions(anyLong(), anyLong(), nullable(Long.class))).
                then(i -> retrievedSystemPermissionList);
        rolePermissionManager.loadRolePermissions(systemRole);
        assertNotEquals(0, rolePermissionManager.getIsPermissionsAssigned().size());
    }

    /**
     * Test for method {@link RolePermissionManager#loadRolePermissions(SystemRole)}
     * when an exception occurs while trying to retrieve permission via TenantRole API
     * @throws SystemException in case of any error thrown by TenantRole Rest client
     */
    @Test
    public void testOnLoadRolePermissionsWithException() throws SystemException {
        SystemRole systemRole = new Role();
        systemRole.setId(1L);
        when(tenantRolePermissionRESTServiceAccess.getPermissions(anyLong(), anyLong(), nullable(Long.class))).
                thenThrow(new SystemException("error while retrieving permissions"));
        rolePermissionManager.loadRolePermissions(systemRole);
        assertEquals(0, rolePermissionManager.getIsPermissionsAssigned().size());
    }

    /**
     * Test for method {@link RolePermissionManager#loadRolePermissions(SystemRole)}
     * @throws SystemException in case of any error thrown by TenantRole Rest client
     */
    @Test
    public void testOnLoadRolePermissionsWithNoSelectedActiveTenant() throws SystemException {
        rolePermissionManager.setSystemActiveTenant(null);
        when(rolePermissionManager.getSystemActiveTenant()).then(i -> null);
        when(tenantRolePermissionRESTServiceAccess.getPermissions(anyLong(), anyLong(), nullable(Long.class))).
                then(i -> retrievedSystemPermissionList);
        SystemRole role = new Role();
        role.setId(1L);
        rolePermissionManager.loadRolePermissions(role);
        assertEquals(0, rolePermissionManager.getIsPermissionsAssigned().size());
    }

    /**
     * Test Method onRowExpand()
     * Expects runTimeException
     * @throws SystemException if any error
     */
    @Test
    public void testOnRowExpandException() throws SystemException {
        doReturn(systemRole).when(toggleEvent).getData();
        assertNotNull(toggleEvent.getData());

        rolePermissionManager.onRowExpand(toggleEvent);
    }

    /**
     * Test method loadRolePermissions()
     * @throws SystemException if any error occurs
     */
    @Test
    public void testLoadRolePermissions() throws SystemException {
        doReturn(retrievedSystemPermissionList).when(tenantRolePermissionRESTServiceAccess).
                getPermissions(anyLong(), anyLong(), anyLong());

        rolePermissionManager.setIsPermissionsAssigned(isPermissionsAssigned);
        rolePermissionManager.loadRolePermissions(systemRole);

        assertEquals(TOTAL_PERMISSION_RESULTS, rolePermissionManager.getIsPermissionsAssigned().size());
    }

    /**
     * Test method loadRolePermissions() expects
     * Null pointer exception
     * @throws SystemException if any error
     */
    @Test(expected = NullPointerException.class)
    public void testLoadRolePermissionsException() throws SystemException {
        doThrow(SystemException.class).when(tenantRolePermissionRESTServiceAccess).getPermissions(anyLong(), anyLong(), nullable(Long.class));
        rolePermissionManager.loadRolePermissions(systemRole);
    }

    /**
     * Test method isAssignableOrUnassignedPermission()
     * asserts ValueChangeEvent object info values
     */
    @Test
    public void testIsAssignableOrUnassignedPermission(){
        ValueChangeEvent event = mock(ValueChangeEvent.class);

        assertNull(event.getOldValue());
        assertNull(event.getNewValue());

        rolePermissionManager.isAssignableOrUnassignedPermission(event);

        doReturn(true).when(event).getNewValue();
        doReturn(true).when(event).getOldValue();

        rolePermissionManager.isAssignableOrUnassignedPermission(event);
        assertNotNull(event.getOldValue());
        assertNotNull(event.getNewValue());
        assertEquals(true, event.getNewValue());
        assertEquals(true, event.getOldValue());
    }

    /**
     * Test method(s) SystemRole
     * Getter and Setter
     */
    @Test
    public void testSystemRole(){
        Long systemRoleId = 1L;
        assertEquals(systemRole, rolePermissionManager.getSystemRole());
        assertEquals(systemRoleId, rolePermissionManager.getSystemRole().getId());
    }

    /**
     * Test Method isAssignableOrUnassignedRolePermission()
     * asserts isPermissionAssigned/isPermissionUnassigned methods
     */
    @Test
    public void testIsAssignableOrUnassignedRolePermission(){
        rolePermissionManager.setPermissionUnassigned(true);

        SystemPermission systemPermission1 = new Permission();
        systemPermission1.setId(1L);
        systemPermission1.setName("Permission-" + 1);

        SystemPermission systemPermission2 = new Permission();
        systemPermission2.setId(2L);
        systemPermission2.setName("Permission-" + 2);

        rolePermissionManager.isAssignableOrUnassignedRolePermission(systemPermission1);
        rolePermissionManager.isAssignableOrUnassignedRolePermission(systemPermission2);
        assertTrue(rolePermissionManager.isPermissionUnassigned());
        assertFalse(rolePermissionManager.isPermissionAssigned());
        assertNotNull(rolePermissionManager.getUnassignedRolePermissions());

        rolePermissionManager.setPermissionAssigned(true);
        rolePermissionManager.isAssignableOrUnassignedRolePermission(systemPermission1);
        assertNotNull(rolePermissionManager.getAssignableRolePermissions());

        rolePermissionManager.isAssignableOrUnassignedRolePermission(systemPermission2);
        assertEquals(0, rolePermissionManager.getUnassignedRolePermissions().size());

        rolePermissionManager.setAssignableRolePermissions(assignableRolePermissions);
        rolePermissionManager.setUnassignedRolePermissions(unassignedRolePermissions);
    }

    /**
     * Test method assignOrUnassignedPermissionsToActiveUserTenant()
     * Asserts persistent calls to DB while performing assignable or
     * Unassigned permission(s) of Role to the active user Tenant
     * @throws SystemException if any error
     */
    @Test
    public void testAssignOrUnassignedPermissionsToActiveUserTenant() throws SystemException {
        rolePermissionManager.setAssignableRolePermissions(assignableRolePermissions);
        rolePermissionManager.setUnassignedRolePermissions(unassignedRolePermissions);
        assertEquals(assignableRolePermissions, rolePermissionManager.getAssignableRolePermissions());
        assertEquals(unassignedRolePermissions, rolePermissionManager.getUnassignedRolePermissions());

        doReturn(systemActiveTenant).when(activeTenantDataModelManager).getActiveTenant();
        assertEquals(systemActiveTenant, rolePermissionManager.getSystemActiveTenant());

        doReturn(retrievedSystemPermissionList).when(tenantRolePermissionRESTServiceAccess).getPermissions(anyLong(), anyLong(), nullable(Long.class));
        doReturn(true).when(tenantRoleRESTServiceAccess).create(any());
        doReturn(true).when(tenantRolePermissionRESTServiceAccess).unAssignPermission(anyLong(), anyLong(), anyLong());

        String roles_url = rolePermissionManager.assignOrUnassignedPermissionsToActiveUserTenant();
        assertEquals(DataModelEnum.ROLE_MAIN_PAGE.getValue(), roles_url);
    }

    /**
     * Test method assignOrUnassignedPermissionsToActiveUserTenant(), but without active tenant selected
     * @throws SystemException if any error
     */
    @Test
    public void testAssignOrUnassignedPermissionsWithNoActiveUserTenant() throws SystemException {
        rolePermissionManager.setAssignableRolePermissions(assignableRolePermissions);
        rolePermissionManager.setUnassignedRolePermissions(unassignedRolePermissions);
        assertEquals(assignableRolePermissions, rolePermissionManager.getAssignableRolePermissions());
        assertEquals(unassignedRolePermissions, rolePermissionManager.getUnassignedRolePermissions());

        doReturn(null).when(activeTenantDataModelManager).getActiveTenant();
        rolePermissionManager.setSystemActiveTenant(null);

        String roles_url = rolePermissionManager.assignOrUnassignedPermissionsToActiveUserTenant();
        assertEquals(DataModelEnum.ROLE_MAIN_PAGE.getValue(), roles_url);

        assertNotEquals(0, rolePermissionManager.getAssignableRolePermissions().size());
        assertNotEquals(0, rolePermissionManager.getUnassignedRolePermissions().size());
    }


    /**
     * Test for method {@link RolePermissionManager#doAssignedPermissionsForRole()}
     * @throws SystemException in case of any error thrown by TenantRole Rest client
     * @throws TenantRoleIllegalArgumentException in case of insufficient params for {@link TenantRoleUtil#getTenantRoleId(Long, Long)}
     */
    @Test
    public void testDoAssignedPermissionsForRole() throws SystemException, TenantRoleIllegalArgumentException {
        rolePermissionManager.setSystemPermissionsIdsList(retrievedSystemPermissionList.stream().
                map(SystemPermission::getId).collect(Collectors.toList()));
        rolePermissionManager.setAssignableRolePermissions(new HashSet<>(Arrays.asList(1L, 10000L, 10001L)));
        when(tenantRoleRESTServiceAccess.exists(systemActiveTenant.getTenantId(), systemRole.getId())).
                then(i -> Boolean.FALSE).then(i -> Boolean.TRUE);
        when(tenantRoleRESTServiceAccess.create(any())).thenReturn(Boolean.TRUE);
        when(tenantRolePermissionRESTServiceAccess.assignPermission(any())).then(i -> Boolean.TRUE);

        SystemTenantRole tenantRole = new TenantRole();
        tenantRole.setId(111L);
        when(tenantRoleUtil.getTenantRoleId(systemActiveTenant.getTenantId(), systemRole.getId())).
                thenReturn(tenantRole.getId());
        rolePermissionManager.doAssignedPermissionsForRole();
        assertEquals(0, rolePermissionManager.getAssignableRolePermissions().size());
    }

    /**
     * Test for method {@link RolePermissionManager#doUnassignedPermissionsForRole()}
     * @throws SystemException in case of any error thrown by TenantRole Rest client
     */
    @Test
    public void testDoUnassignedPermissionsForRole() throws SystemException {
        rolePermissionManager.setSystemPermissionsIdsList(retrievedSystemPermissionList.stream().
                map(SystemPermission::getId).collect(Collectors.toList()));
        rolePermissionManager.setUnassignedRolePermissions(unassignedRolePermissions);
        when(tenantRolePermissionRESTServiceAccess.unAssignPermission(anyLong(), anyLong(), anyLong())).then(i -> Boolean.TRUE);
        rolePermissionManager.doUnassignedPermissionsForRole();
        assertEquals(0, rolePermissionManager.getUnassignedRolePermissions().size());
    }

    /**
     * Test Method refresh()
     * asserts isPermissionAssigned or
     * isPermissionUnassigned
     * objects clears empty sets
     */
    @Test
    public void testRefresh(){
        rolePermissionManager.setAssignableRolePermissions(assignableRolePermissions);
        rolePermissionManager.setUnassignedRolePermissions(unassignedRolePermissions);

        rolePermissionManager.refresh();
        assertEquals(0L, rolePermissionManager.getAssignableRolePermissions().size());
        assertEquals(0L, rolePermissionManager.getUnassignedRolePermissions().size());
    }


    /**
     * This method creates the
     * list of Permissions for given size
     */
    private void initPermissionData() {
        systemPermissionList = new ArrayList<>();
        for (int i = 0; i < RolePermissionManagerTest.TOTAL_PERMISSION_RESULTS; i++) {
            SystemPermission systemPermission = new Permission();
            systemPermission.setId((long) i);
            systemPermission.setName("Permission-" + i);
            systemPermissionList.add(systemPermission);
        }
    }

    /**
     * This method creates the list of Permissions for given size.
     * A collection that represents the list of permissions retrieved via {@link TenantRoleRESTServiceAccess} API
     */
    private List<? extends SystemPermission> initRetrievedPermissionList() {
        List<SystemPermission> list = new ArrayList<>();
        for (long i = 1; i <= RolePermissionManagerTest.TOTAL_PERMISSION_RESULTS; i++) {
            SystemPermission systemPermission = new Permission();
            systemPermission.setId(i);
            systemPermission.setName(String.valueOf(i));
            systemPermission.setResourceId(i);
            systemPermission.setActionId(i);
            list.add(systemPermission);
            isPermissionsAssigned.put(systemPermission.getId(), true);
        }
        return list;
    }

}