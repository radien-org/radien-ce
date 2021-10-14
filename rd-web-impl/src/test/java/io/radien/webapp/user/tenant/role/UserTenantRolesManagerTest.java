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
package io.radien.webapp.user.tenant.role;

import io.radien.api.model.role.SystemRole;
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.model.tenantrole.SystemTenantRole;
import io.radien.api.model.user.SystemUser;
import io.radien.api.service.tenantrole.TenantRoleRESTServiceAccess;
import io.radien.api.service.tenantrole.TenantRoleUserRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.rolemanagement.client.entities.Role;
import io.radien.ms.rolemanagement.client.entities.TenantRole;
import io.radien.ms.tenantmanagement.client.entities.Tenant;
import io.radien.ms.usermanagement.client.entities.User;
import io.radien.webapp.DataModelEnum;
import io.radien.webapp.JSFUtil;
import io.radien.webapp.JSFUtilAndFaceContextMessagesTest;
import io.radien.webapp.user.UserDataModel;
import java.util.ArrayList;
import java.util.Collections;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
/**
 * Class that aggregates UnitTest cases for UserTenantRolesManager
 *
 * @author Rajesh Gavvala
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({JSFUtil.class, FacesContext.class, ExternalContext.class})
public class UserTenantRolesManagerTest extends JSFUtilAndFaceContextMessagesTest {

    @InjectMocks
    private UserTenantRolesManager userTenantRolesManager;

    @Mock
    private TenantRoleRESTServiceAccess tenantRoleRESTServiceAccess;

    @Mock
    private TenantRoleUserRESTServiceAccess tenantRoleUserRESTServiceAccess;

    @Mock
    private UserDataModel userDataModel;

    private SystemTenant systemTenant;

    private List<SystemRole> systemRoles = new ArrayList<>();
    private List<SystemRole> assignedUserTenantRoles;
    private List<SystemTenant> systemTenants;

    private final Map<Long, Boolean> isRoleAssigned = new HashMap<>();

    private static final int TOTAL_RESULTS = 5;

    Set<Long> assignableUserTenantRoles = new HashSet<>();
    Set<Long> unassignedUserTenantRoles = new HashSet<>();

    /**
     * Prepares require objects when requires to invoke
     */
    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);

        handleJSFUtilAndFaceContextMessages();
        initUserTenants();

        systemRoles = initSystemRole();
        assignableUserTenantRoles = systemRoles.subList(0,2).stream().map(SystemRole::getId).collect(Collectors.toSet());
        unassignedUserTenantRoles = systemRoles.subList(2,4).stream().map(SystemRole::getId).collect(Collectors.toSet());

        assignedUserTenantRoles = systemRoles.subList(0,2);
        for(SystemRole systemRoleObject : assignedUserTenantRoles){
            isRoleAssigned.put(systemRoleObject.getId(), true);
        }

        SystemUser systemUser = new User();
        systemUser.setId(1L);
        systemUser.setLogon("testLogon");
        doReturn(systemUser).when(userDataModel).getSelectedUser();

        systemTenant = new Tenant();
        systemTenant.setId(1L);
        systemTenant.setName("Tenant-1");
        userTenantRolesManager.setTenant(systemTenant);
        assertEquals(systemTenant, userTenantRolesManager.getTenant());
    }

    /**
     * Test case for init()
     * Asserts User Tenants and
     * SystemRoles of LazyData initialize
     */
    @Test
    public void testInit() throws SystemException {
        doReturn(Collections.unmodifiableList(systemTenants)).when(tenantRoleRESTServiceAccess).getTenants(anyLong(), anyLong());

        userTenantRolesManager.init();
        userTenantRolesManager.setUserTenants(systemTenants);
        assertEquals(systemTenants, userTenantRolesManager.getUserTenants());
    }

    /**
     * Test method selectedChangeTenant()
     * asserts ValueChangeEvent object info values
     * @throws SystemException if any error
     */
    @Test
    public void testSelectedChangeTenant() throws SystemException {
        ValueChangeEvent event = mock(ValueChangeEvent.class);
        doReturn(systemTenant).when(event).getNewValue();

        userTenantRolesManager.selectedChangeTenant(event);
        assertNotNull(event.getNewValue());
    }

    /**
     * Test case for loadUserTenantRoles() - success
     * @throws SystemException if any error
     */
    @Test
    public void testLoadUserTenantRoles() throws SystemException {
        userTenantRolesManager.setAssignedRolesForUserTenant(assignedUserTenantRoles);
        assertEquals(assignedUserTenantRoles, userTenantRolesManager.getAssignedRolesForUserTenant());

        doReturn(assignedUserTenantRoles).when(this.tenantRoleRESTServiceAccess).getRolesForUserTenant(anyLong(), anyLong());

        userTenantRolesManager.setAssignedRolesForUserTenant(assignedUserTenantRoles);
        userTenantRolesManager.setIsRoleAssigned(isRoleAssigned);

        userTenantRolesManager.loadUserTenantRoles(systemTenant);

        assertEquals(assignedUserTenantRoles, userTenantRolesManager.getAssignedRolesForUserTenant());
        assertEquals(isRoleAssigned, userTenantRolesManager.getIsRoleAssigned());
    }

    /**
     * Test case for loadUserTenantRoles() - failure
     * @throws SystemException if any error
     */
    @Test
    public void testLoadUserTenantRolesException() throws SystemException {
        userTenantRolesManager.setAssignedRolesForUserTenant(assignedUserTenantRoles);
        assertEquals(assignedUserTenantRoles, userTenantRolesManager.getAssignedRolesForUserTenant());

        doThrow(RuntimeException.class).when(tenantRoleRESTServiceAccess).getRolesForUserTenant(anyLong(), anyLong());

        userTenantRolesManager.loadUserTenantRoles(systemTenant);
    }


    /**
     * Test method isAssignableOrUnassignedRole()
     * asserts ValueChangeEvent object info values
     */
    @Test
    public void testIsAssignableOrUnassignedRole() {
        ValueChangeEvent event = mock(ValueChangeEvent.class);

        assertNull(event.getNewValue());
        assertNull(event.getOldValue());

        userTenantRolesManager.isAssignableOrUnassignedRole(event);

        doReturn(true).when(event).getNewValue();
        doReturn(true).when(event).getOldValue();

        userTenantRolesManager.isAssignableOrUnassignedRole(event);
        assertNotNull(event.getOldValue());
        assertNotNull(event.getNewValue());
        assertEquals(true, event.getNewValue());
        assertEquals(true, event.getOldValue());
    }

    /**
     * Test Method isAssignableOrUnassignedRoleType()
     * asserts isRoleAssigned/isRoleUnassigned methods
     */
    @Test
    public void testIsAssignableOrUnassignedRoleType(){
        userTenantRolesManager.setOldRoleObjectUnassigned(true);

        SystemRole systemRole1 = new Role();
        systemRole1.setId(1L);
        systemRole1.setName("Role-" + 1);

        SystemRole systemRole2 = new Role();
        systemRole2.setId(2L);
        systemRole2.setName("Role-" + 2);

        userTenantRolesManager.isAssignableOrUnassignedRoleType(systemRole1);
        userTenantRolesManager.isAssignableOrUnassignedRoleType(systemRole2);
        assertTrue(userTenantRolesManager.isOldRoleObjectUnassigned());
        assertFalse(userTenantRolesManager.isNewRoleObjectAssigned());
        assertNotNull(userTenantRolesManager.getUnassignedUserTenantRoles());

        userTenantRolesManager.setNewRoleObjectAssigned(true);
        userTenantRolesManager.isAssignableOrUnassignedRoleType(systemRole1);
        assertNotNull(userTenantRolesManager.getAssignableUserTenantRoles());

        userTenantRolesManager.isAssignableOrUnassignedRoleType(systemRole2);
        assertEquals(0, userTenantRolesManager.getUnassignedUserTenantRoles().size());

        userTenantRolesManager.setAssignableUserTenantRoles(assignableUserTenantRoles);
        userTenantRolesManager.setUnassignedUserTenantRoles(unassignedUserTenantRoles);
    }

    /**
     * Test method assignOrUnassignedRolesToUserTenant()
     * Asserts persistent calls to DB while performing assignable or
     * Unassigned Role(s) of User Tenant
     * @throws SystemException if any error
     */
    @Test
    public void testAssignOrUnassignedPermissionsToActiveUserTenant() throws SystemException {
        userTenantRolesManager.setAssignableUserTenantRoles(assignableUserTenantRoles);
        userTenantRolesManager.setUnassignedUserTenantRoles(unassignedUserTenantRoles);
        assertEquals(assignableUserTenantRoles, userTenantRolesManager.getAssignableUserTenantRoles());
        assertEquals(unassignedUserTenantRoles, userTenantRolesManager.getUnassignedUserTenantRoles());

        SystemTenantRole systemTenantRole = new TenantRole();
        systemTenantRole.setTenantId(1L);
        systemTenantRole.setRoleId(1L);

        doReturn(true).when(tenantRoleRESTServiceAccess).create(any());
        doReturn(true).when(tenantRoleUserRESTServiceAccess).assignUser(any());
        doReturn(Boolean.TRUE).when(tenantRoleUserRESTServiceAccess).unAssignUser(anyLong(), anyCollection(), anyLong());

        userTenantRolesManager.assignOrUnassignedRolesToUserTenant();
    }

    /**
     * Test method assignOrUnassignedRolesToUserTenant()
     * catch exception error handle message
     * Unassigned Role(s) of User Tenant
     * @throws SystemException if any error
     */
    @Test
    public void testAssignOrUnassignedPermissionsToActiveUserTenantException() throws SystemException {
        userTenantRolesManager.setAssignableUserTenantRoles(assignableUserTenantRoles);
        userTenantRolesManager.setUnassignedUserTenantRoles(unassignedUserTenantRoles);
        assertEquals(assignableUserTenantRoles, userTenantRolesManager.getAssignableUserTenantRoles());
        assertEquals(unassignedUserTenantRoles, userTenantRolesManager.getUnassignedUserTenantRoles());

        SystemTenantRole systemTenantRole = new TenantRole();
        systemTenantRole.setTenantId(1L);
        systemTenantRole.setRoleId(1L);

        doReturn(true).when(tenantRoleRESTServiceAccess).create(any());
        doThrow(RuntimeException.class).when(tenantRoleUserRESTServiceAccess).assignUser(any());
        doThrow(RuntimeException.class).when(tenantRoleUserRESTServiceAccess).unAssignUser(anyLong(), anyCollection(), anyLong());

        userTenantRolesManager.assignOrUnassignedRolesToUserTenant();
    }

    /**
     * Test method clearAssignableOrUnAssignedRoles()
     * asserts mapped role assigned object
     */
    @Test
    public void testClearAssignableOrUnAssignedRoles(){
        userTenantRolesManager.setAssignableUserTenantRoles(assignableUserTenantRoles);
        userTenantRolesManager.setUnassignedUserTenantRoles(unassignedUserTenantRoles);

        userTenantRolesManager.clearAssignableOrUnAssignedRoles();
        assertEquals(0L, userTenantRolesManager.getAssignableUserTenantRoles().size());
        assertEquals(0L, userTenantRolesManager.getUnassignedUserTenantRoles().size());
    }

    /**
     * Test method clearDefaultRolesAssignedMap()
     * asserts mapped role assigned object
     */
    @Test
    public void testClearDefaultRolesAssignedMap(){
        userTenantRolesManager.setIsRoleAssigned(isRoleAssigned);
        userTenantRolesManager.clearDefaultRolesAssignedMap();
        assertEquals(0, userTenantRolesManager.getIsRoleAssigned().size());
    }

    /**
     * Test case for the method returnBackToUsersTable()
     *  asserts user url page
     */
    @Test
    public void testReturnHome(){
        SystemUser systemUser = new User();
        doNothing().when(userDataModel).setSelectedUser(systemUser);
        assertEquals(DataModelEnum.USERS_PATH.getValue(),userTenantRolesManager.returnBackToUsersTable());
    }

    /**
     * Test case for the method returnBackToUsersTable()
     * failure scenario throws exception
     */
    @Test
    public void testReturnHomeThrowsException(){
        doThrow(new RuntimeException()).when(userDataModel).setSelectedUser(null);
        assertNotNull(userTenantRolesManager.returnBackToUsersTable());
    }

    /**
     * This method creates the
     * list of Tenant object
     * for given size
     */
    private void initUserTenants() {
        systemTenants = new ArrayList<>();
        for (int i = 0; i < UserTenantRolesManagerTest.TOTAL_RESULTS; i++) {
            SystemTenant systemTenant = new Tenant();
            systemTenant.setId((long) i);
            systemTenant.setName("Tenant-" + i);
            systemTenants.add(systemTenant);
        }
    }

    /**
     * This method creates the
     * list of Role object
     * for given size
     */
    private List<SystemRole> initSystemRole() {
        for (int i = 0; i < UserTenantRolesManagerTest.TOTAL_RESULTS; i++) {
            SystemRole systemRole = new Role();
            systemRole.setId((long) i);
            systemRole.setName("Role-" + i);
            systemRole.setDescription("RoleDescription-" + i);
            systemRoles.add(systemRole);
        }
        return systemRoles;
    }
}