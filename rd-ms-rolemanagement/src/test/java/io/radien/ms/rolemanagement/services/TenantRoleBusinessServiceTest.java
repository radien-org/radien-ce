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
package io.radien.ms.rolemanagement.services;

import io.radien.api.entity.Page;
import io.radien.api.model.permission.SystemPermission;
import io.radien.api.model.role.SystemRole;
import io.radien.api.model.role.SystemRoleSearchFilter;
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.model.tenantrole.SystemTenantRole;
import io.radien.api.model.tenantrole.SystemTenantRoleUser;
import io.radien.api.model.tenantrole.SystemTenantRoleUserSearchFilter;
import io.radien.api.service.permission.PermissionRESTServiceAccess;
import io.radien.api.service.role.RoleServiceAccess;
import io.radien.api.service.tenant.TenantRESTServiceAccess;
import io.radien.api.service.tenantrole.TenantRolePermissionServiceAccess;
import io.radien.api.service.tenantrole.TenantRoleServiceAccess;
import io.radien.api.service.tenantrole.TenantRoleUserServiceAccess;
import io.radien.exception.RoleNotFoundException;
import io.radien.exception.SystemException;
import io.radien.exception.TenantRoleException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.permissionmanagement.client.entities.Permission;
import io.radien.ms.rolemanagement.client.entities.RoleSearchFilter;
import io.radien.ms.rolemanagement.client.entities.TenantRoleUserSearchFilter;
import io.radien.ms.rolemanagement.entities.Role;
import io.radien.ms.rolemanagement.entities.TenantRole;
import io.radien.ms.tenantmanagement.client.entities.Tenant;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

/**
 * @author Newton Carvalho
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TenantRoleBusinessServiceTest {

    Long user1Id = 1111L;

    Long tenantId = 1000L;
    Long tenantId2 = 2000L;
    Long tenantId3 = 3000L;

    String roleNameForTenantAdministrator = "Tenant Administrator";

    Properties p;
    TenantRoleBusinessService tenantRoleBusinessService;

    TenantRoleServiceAccess tenantRoleServiceAccess;
    TenantRolePermissionServiceAccess tenantRolePermissionServiceAccess;
    TenantRoleUserServiceAccess tenantRoleUserServiceAccess;
    RoleServiceAccess roleServiceAccess;

    public TenantRoleBusinessServiceTest() throws NamingException {
        p = new Properties();
        p.put("appframeDatabase", "new://Resource?type=DataSource");
        p.put("appframeDatabase.JdbcDriver", "org.hsqldb.jdbcDriver");
        p.put("appframeDatabase.JdbcUrl", "jdbc:hsqldb:mem:radienTest");
        p.put("appframeDatabase.userName", "sa");
        p.put("appframeDatabase.password", "");
        p.put("openejb.deployments.classpath.include",".*");
        p.put("openejb.deployments.classpath.exclude",".*rd-ms-usermanagement-client.*");

        final Context context = EJBContainer.createEJBContainer(p).getContext();

        String lookupString = "java:global/rd-ms-rolemanagement//TenantRoleBusinessService";
        tenantRoleBusinessService = (TenantRoleBusinessService) context.lookup(lookupString);

        lookupString = "java:global/rd-ms-rolemanagement//TenantRoleService";
        tenantRoleServiceAccess = (TenantRoleServiceAccess) context.lookup(lookupString);

        lookupString = "java:global/rd-ms-rolemanagement//TenantRolePermissionService";
        tenantRolePermissionServiceAccess = (TenantRolePermissionServiceAccess) context.lookup(lookupString);

        lookupString = "java:global/rd-ms-rolemanagement//TenantRoleUserService";
        tenantRoleUserServiceAccess = (TenantRoleUserServiceAccess) context.lookup(lookupString);

        lookupString = "java:global/rd-ms-rolemanagement//RoleService";
        roleServiceAccess = (RoleServiceAccess) context.lookup(lookupString);
    }

    protected SystemRole getRoleByName(String name) {
        SystemRoleSearchFilter filter = new RoleSearchFilter();
        filter.setName(name);
        List<? extends SystemRole> roles = this.roleServiceAccess.getSpecificRoles(filter);
        return roles.isEmpty() ? null : roles.get(0);
    }

    protected SystemRole createRole(String name) throws RoleNotFoundException, UniquenessConstraintException {
        SystemRole sr = getRoleByName(name);
        if (sr == null) {
            sr = new Role();
            sr.setName(name);
            roleServiceAccess.save(sr);
        }
        return sr;
    }

    protected SystemTenantRole createTenantRole(Long roleId, Long tenantId) throws
            SystemException, UniquenessConstraintException, TenantRoleException {

        List<? extends SystemTenantRole> tenantRoles = tenantRoleBusinessService.
                getSpecific(tenantId, roleId, true);

        SystemTenantRole tenantRole = tenantRoles.isEmpty() ? null : tenantRoles.get(0);

        if (tenantRole == null) {
            tenantRole = new TenantRole();
            tenantRole.setRoleId(roleId);
            tenantRole.setTenantId(tenantId);

            TenantRESTServiceAccess tenantRESTServiceAccess =
                    Mockito.mock(TenantRESTServiceAccess.class);
            tenantRoleBusinessService.setTenantRESTServiceAccess(tenantRESTServiceAccess);
            when(tenantRESTServiceAccess.isTenantExistent(tenantId)).thenReturn(Boolean.TRUE);

            this.tenantRoleBusinessService.save(tenantRole);
        }

        return tenantRole;
    }

    @Test
    @Order(1)
    public void createRoleAdmin() throws RoleNotFoundException, UniquenessConstraintException {
        SystemRole roleAdmin = assertDoesNotThrow(() -> createRole(roleNameForTenantAdministrator));
        assertNotNull(roleAdmin);
    }

    @Test
    @Order(2)
    public void save() {
        // Create The TenantRole for the very first Time
        SystemRole roleAdmin = assertDoesNotThrow(() -> createRole(roleNameForTenantAdministrator));
        SystemTenantRole tenantRole = assertDoesNotThrow(() ->
                createTenantRole(roleAdmin.getId(), tenantId));
        assertNotNull(tenantRole);

        // Try to create again using the same parameters
        SystemTenantRole repeated = new TenantRole();
        repeated.setTenantId(tenantId);
        repeated.setRoleId(roleAdmin.getId());

        assertThrows(UniquenessConstraintException.class,
                () -> tenantRoleBusinessService.save(repeated));

        // Try to insert with invalid Tenant
        Long mockedInvalidTenant = 9999L;
        Long mockedValidTenant = 8888L;
        SystemTenantRole tenantRoleWithInvalidTenant = new TenantRole();
        tenantRoleWithInvalidTenant.setTenantId(mockedInvalidTenant);
        tenantRoleWithInvalidTenant.setRoleId(roleAdmin.getId());

        TenantRESTServiceAccess tenantRESTServiceAccess =
                Mockito.mock(TenantRESTServiceAccess.class);
        tenantRoleBusinessService.setTenantRESTServiceAccess(tenantRESTServiceAccess);
        try {
            when(tenantRESTServiceAccess.isTenantExistent(mockedInvalidTenant)).
                    thenReturn(Boolean.FALSE);
            when(tenantRESTServiceAccess.isTenantExistent(mockedValidTenant)).
                    thenReturn(Boolean.TRUE);
        } catch (SystemException systemException) {
            fail("unexpected");
        }
        assertThrows(TenantRoleException.class, () -> tenantRoleBusinessService.
                save(tenantRoleWithInvalidTenant));

        // Try to insert with invalid Role
        SystemTenantRole tenantRoleWithInvalidRole = new TenantRole();
        tenantRoleWithInvalidRole.setTenantId(mockedValidTenant);
        tenantRoleWithInvalidRole.setRoleId(1111111L);
        assertThrows(TenantRoleException.class, () -> tenantRoleBusinessService.
                save(tenantRoleWithInvalidRole));
    }

    @Test
    @Order(3)
    public void getById() {
        SystemRole roleTestAdmin = assertDoesNotThrow(() -> createRole("testAdmin"));
        SystemTenantRole tenantRole = assertDoesNotThrow(() ->
                createTenantRole(roleTestAdmin.getId(), tenantId));

        SystemTenantRole retrievedById = assertDoesNotThrow(() ->
                tenantRoleBusinessService.getById(tenantRole.getId()));

        assertNotNull(retrievedById);
        assertEquals(retrievedById.getId(), tenantRole.getId());
    }

    @Test
    @Order(4)
    public void getByIdNotFoundCase() {
        Long notExistentTenantRoleId = 11111L;
        assertThrows(TenantRoleException.class, () ->
                tenantRoleBusinessService.getById(notExistentTenantRoleId));
    }

    @Test
    @Order(5)
    public void existAssociation() {
        SystemRole roleAdmin = assertDoesNotThrow(() -> createRole(roleNameForTenantAdministrator));
        SystemTenantRole tenantRole = assertDoesNotThrow(() ->
                createTenantRole(roleAdmin.getId(), tenantId));
        assertTrue(this.tenantRoleBusinessService.
                existsAssociation(tenantId, roleAdmin.getId()));
    }

    @Test
    @Order(6)
    public void existAssociationNegativeCase() {
        assertFalse(this.tenantRoleBusinessService.
                existsAssociation(1111L, 1000L));
    }

    @Test
    @Order(7)
    public void assignUser() {
        // Assign user to the role "Tenant Administrator" for the tenant "1"
        SystemRole roleAdmin = assertDoesNotThrow(() -> createRole(roleNameForTenantAdministrator));
        assertDoesNotThrow(() -> this.tenantRoleBusinessService.assignUser(tenantId,
                roleAdmin.getId(), user1Id));
    }

    @Test
    @Order(8)
    public void assignUserInvalidCase() {
        // Try to assign to a non existent combination of role and tenant
        Long notExistentRole = 2222L;
        assertThrows(TenantRoleException.class, () ->
                this.tenantRoleBusinessService.assignUser(tenantId, notExistentRole, user1Id));

        // Again: Try to assign  user to the role "Tenant Administrator" for the tenant "1"
        SystemRole roleAdmin = assertDoesNotThrow(() -> createRole(roleNameForTenantAdministrator));
        assertThrows(TenantRoleException.class, () -> this.tenantRoleBusinessService.assignUser(tenantId,
                roleAdmin.getId(), user1Id));
    }

    @Test
    @Order(9)
    public void isRoleExistentForUser() {
        // Check for Role Name
        assertTrue(this.tenantRoleBusinessService.isRoleExistentForUser(user1Id,
                roleNameForTenantAdministrator, null));
        // Check for Role Name and specific Tenant
        assertTrue(this.tenantRoleBusinessService.isRoleExistentForUser(user1Id,
                roleNameForTenantAdministrator, tenantId));

    }

    @Test
    @Order(10)
    public void isRoleExistentForUserNegativeCases() {
        // Check for Role Name under non associated tenant
        Long notAssociatedTenant = 343L;
        assertFalse(this.tenantRoleBusinessService.isRoleExistentForUser(user1Id,
                roleNameForTenantAdministrator, notAssociatedTenant));
        // Check for not associated Role
        assertFalse(this.tenantRoleBusinessService.isRoleExistentForUser(user1Id,
                "super admin", tenantId));
    }


    @Test
    @Order(11)
    public void isAnyRoleExistentForUser() {

        // Assign user to the role "READER" for the tenant "1"
        SystemRole reader = assertDoesNotThrow(() -> createRole("READER"));
        assertDoesNotThrow(() -> createTenantRole(reader.getId(), tenantId));
        assertDoesNotThrow(() -> this.tenantRoleBusinessService.assignUser(tenantId,
                reader.getId(), user1Id));

        // Assign user to the role "WRITER" for the tenant "4"
        Long tenant4 = 4L;
        SystemRole writer = assertDoesNotThrow(() -> createRole("WRITER"));
        assertDoesNotThrow(() -> createTenantRole(writer.getId(), tenant4));
        assertDoesNotThrow(() -> this.tenantRoleBusinessService.assignUser(tenant4,
                writer.getId(), user1Id));

        // Assign user to the role "OBSERVER" for the tenant "5"
        Long tenant5 = 5L;
        SystemRole observer = assertDoesNotThrow(() -> createRole("OBSERVER"));
        assertDoesNotThrow(() -> createTenantRole(observer.getId(), tenant5));
        assertDoesNotThrow(() -> this.tenantRoleBusinessService.assignUser(tenant5,
                observer.getId(), user1Id));

        // Check for All three not informing Tenant
        assertTrue(tenantRoleBusinessService.isAnyRoleExistentForUser(user1Id,
                Arrays.asList("READER", "WRITER", "OBSERVER"), null));

        // Check for All three but informing one specific Tenant
        assertTrue(tenantRoleBusinessService.isAnyRoleExistentForUser(user1Id,
                Arrays.asList("READER", "WRITER", "OBSERVER"), tenantId));

        // Checking just WRITER
        assertTrue(tenantRoleBusinessService.isAnyRoleExistentForUser(user1Id,
                Arrays.asList("WRITER"), null));

        assertTrue(tenantRoleBusinessService.isAnyRoleExistentForUser(user1Id,
                Arrays.asList("WRITER"), tenant4));

        assertFalse(tenantRoleBusinessService.isAnyRoleExistentForUser(user1Id,
                Arrays.asList("WRITER"), 7777L));

        // Checking just OBSERVER
        assertTrue(tenantRoleBusinessService.isAnyRoleExistentForUser(user1Id,
                Arrays.asList("OBSERVER"), null));

        assertTrue(tenantRoleBusinessService.isAnyRoleExistentForUser(user1Id,
                Arrays.asList("OBSERVER"), tenant5));

        assertFalse(tenantRoleBusinessService.isAnyRoleExistentForUser(user1Id,
                Arrays.asList("OBSERVER"), tenantId));

        // Checking "OBSERVER" and other non assigned roles
        assertTrue(tenantRoleBusinessService.isAnyRoleExistentForUser(user1Id,
                Arrays.asList("OBSERVER", "role-d", "role-f"), tenant5));

        // Check for All three again but informing one specific (not existent) Tenant
        assertFalse(tenantRoleBusinessService.isAnyRoleExistentForUser(user1Id,
                Arrays.asList("READER", "WRITER", "OBSERVER"), 99999L));

        // Trying to check without informing roles
        assertThrows(Exception.class, ()->
                tenantRoleBusinessService.isAnyRoleExistentForUser(user1Id,
                        null, 99999L));
    }

    @Test
    @Order(12)
    public void getTenants() {

        // Create new roles
        SystemRole guest = assertDoesNotThrow(() -> createRole("guest"));
        SystemRole publisher = assertDoesNotThrow(() -> createRole("publisher"));

        // Create mocked Tenants
        Tenant tenantForId1 = new Tenant(); tenantForId1.setId(tenantId);
        Tenant tenantForId2 = new Tenant(); tenantForId2.setId(tenantId2);
        Tenant tenantForId3 = new Tenant(); tenantForId3.setId(tenantId3);

        // Create Tenant Role associations (all for user Id 1)
        assertDoesNotThrow(() -> createTenantRole(guest.getId(), tenantId2));
        assertDoesNotThrow(() -> tenantRoleBusinessService.assignUser(tenantId2,
                guest.getId(), user1Id));
        assertDoesNotThrow(() -> createTenantRole(publisher.getId(), tenantId3));
        assertDoesNotThrow(() -> tenantRoleBusinessService.assignUser(tenantId3,
                publisher.getId(), user1Id));

        // Mock for Tenant Rest Client
        TenantRESTServiceAccess restServiceAccess = mock(TenantRESTServiceAccess.class);
        try {
            when(restServiceAccess.getTenantById(tenantId)).
                    thenReturn(Optional.of(tenantForId1));
            when(restServiceAccess.getTenantById(tenantId2)).
                    thenReturn(Optional.of(tenantForId2));
            when(restServiceAccess.getTenantById(tenantId3)).
                    thenReturn(Optional.of(tenantForId3));
        } catch (SystemException se) {
            fail("unexpected");
        }
        tenantRoleBusinessService.setTenantRESTServiceAccess(restServiceAccess);

        // User is associated with All 3 Tenants
        List<SystemTenant> tenants = assertDoesNotThrow(() ->
                tenantRoleBusinessService.getTenants(user1Id, null));
        assertNotNull(tenants);
        assertTrue(tenants.size() == 3);

        // User is associated in one single tenant For the role guest
        tenants = assertDoesNotThrow(() ->
                tenantRoleBusinessService.getTenants(user1Id, guest.getId()));
        assertNotNull(tenants);
        assertTrue(tenants.size() == 1);
    }

    @Test
    @Order(13)
    public void getTenantsInvalidCase() {
        // Try to retrieve tenants taking in account a Role for which the user is not associated
        Long notAssociateRoleId = 8888L;
        List<SystemTenant> tenants = assertDoesNotThrow(() ->
                tenantRoleBusinessService.getTenants(user1Id, notAssociateRoleId));
        assertNotNull(tenants);
        assertTrue(tenants.isEmpty());
    }

    @Test
    @Order(13)
    public void unassignUser() {
        // Get previously created Role
        SystemRole guest = assertDoesNotThrow(() -> createRole("guest"));

        // Proving that the user still have access to the role
        assertTrue(tenantRoleBusinessService.isRoleExistentForUser(user1Id,
                guest.getName(), tenantId2));

        // Doing unassigning process
        assertDoesNotThrow(() -> tenantRoleBusinessService.unassignUser(tenantId2,
                guest.getId(), user1Id));

        // Currently user has no access to "guest" role on tenantId 2
        assertFalse(tenantRoleBusinessService.isRoleExistentForUser(user1Id,
                guest.getName(), tenantId2));
    }
    @Test
    @Order(15)
    public void unassignUserInvalidCaseNoTenantRoleAssociated() {
        Long notAssociatedTenant = 99999L;
        Long notAssociatedRole = 99988L;
        assertThrows(TenantRoleException.class, () -> tenantRoleBusinessService.
                unassignUser(notAssociatedTenant, notAssociatedRole, user1Id));
    }

    @Test
    @Order(16)
    public void unassignUserInvalidCaseUserNotAssigned() {
        // Get previously created Role
        SystemRole guest = assertDoesNotThrow(() -> createRole("guest"));

        // User was already removed/unassigned
        assertThrows(TenantRoleException.class, () -> tenantRoleBusinessService.
                unassignUser(tenantId2, guest.getId(), user1Id));
    }

    @Test
    @Order(17)
    public void assignPermission() {
        // Get previously created Role
        SystemRole publisher = assertDoesNotThrow(() -> createRole("publisher"));

        // Creating permissions
        SystemPermission readDocument = new Permission();
        readDocument.setName("READ DOCUMENT"); readDocument.setId(9999L);

        SystemPermission publishDocument = new Permission();
        publishDocument.setName("PUBLISH DOCUMENT"); publishDocument.setId(8999L);

        SystemPermission deleteDocument = new Permission();
        deleteDocument.setName("DELETE DOCUMENT"); deleteDocument.setId(9988L);

        // Mocking PermissionRESTServiceClient
        try {
            PermissionRESTServiceAccess permissionRESTServiceAccess =
                    mock(PermissionRESTServiceAccess.class);
            when(permissionRESTServiceAccess.isPermissionExistent(readDocument.getId(), null)).
                    thenReturn(Boolean.TRUE);
            when(permissionRESTServiceAccess.isPermissionExistent(publishDocument.getId(), null)).
                    thenReturn(Boolean.TRUE);
            when(permissionRESTServiceAccess.isPermissionExistent(deleteDocument.getId(), null)).
                    thenReturn(Boolean.TRUE);
            tenantRoleBusinessService.setPermissionRESTServiceAccess(permissionRESTServiceAccess);
        }
        catch (SystemException se) {
            fail("unexpected");
        }

        // Assigning permission to the Tenant Role
        assertDoesNotThrow(() ->
                tenantRoleBusinessService.assignPermission(tenantId3,
                        publisher.getId(), readDocument.getId()));
        assertDoesNotThrow(() ->
                tenantRoleBusinessService.assignPermission(tenantId3,
                        publisher.getId(), publishDocument.getId()));

        assertDoesNotThrow(() ->
                tenantRoleBusinessService.assignPermission(tenantId3,
                        publisher.getId(), deleteDocument.getId()));
    }

    @Test
    @Order(18)
    public void assignPermissionInvalidCaseNoTenantRole() {

        // Creating permissions
        SystemPermission assemblyDocument = new Permission();
        assemblyDocument.setName("ASSEMBLY DOCUMENT"); assemblyDocument.setId(10000L);

        // Mocking PermissionRESTServiceClient
        try {
            PermissionRESTServiceAccess permissionRESTServiceAccess =
                    mock(PermissionRESTServiceAccess.class);
            when(permissionRESTServiceAccess.isPermissionExistent(assemblyDocument.getId(), null)).
                    thenReturn(Boolean.TRUE);
            tenantRoleBusinessService.setPermissionRESTServiceAccess(permissionRESTServiceAccess);
        }
        catch (SystemException se) {
            fail("unexpected");
        }

        // Assigning permission to the Tenant Role
        assertThrows(TenantRoleException.class,  () ->
                tenantRoleBusinessService.assignPermission(tenantId3,
                        11111L, assemblyDocument.getId()));
    }

    @Test
    @Order(19)
    public void assignPermissionInvalidCaseAssignmentAlreadyPerformed() {
        // Get previously created Role
        SystemRole publisher = assertDoesNotThrow(() -> createRole("publisher"));

        // Previously created permissions
        SystemPermission readDocument = new Permission();
        readDocument.setName("READ DOCUMENT"); readDocument.setId(9999L);

        SystemPermission publishDocument = new Permission();
        publishDocument.setName("PUBLISH DOCUMENT"); publishDocument.setId(8999L);

        SystemPermission deleteDocument = new Permission();
        deleteDocument.setName("DELETE DOCUMENT"); deleteDocument.setId(9988L);

        // Mocking PermissionRESTServiceClient
        try {
            PermissionRESTServiceAccess permissionRESTServiceAccess =
                    mock(PermissionRESTServiceAccess.class);
            when(permissionRESTServiceAccess.getPermissionById(readDocument.getId())).
                    thenReturn(Optional.of(readDocument));
        }
        catch (SystemException se) {
            fail("unexpected");
        }

        // Trying to Assigning permission again
        assertThrows(TenantRoleException.class, () ->
                tenantRoleBusinessService.assignPermission(tenantId3,
                        publisher.getId(), readDocument.getId()));

    }

    @Test
    @Order(20)
    public void getPermissions() {
        // Get previously created Role
        SystemRole publisher = assertDoesNotThrow(() -> createRole("publisher"));

        // Previously created permissions
        SystemPermission readDocument = new Permission();
        readDocument.setName("READ DOCUMENT"); readDocument.setId(9999L);

        SystemPermission publishDocument = new Permission();
        publishDocument.setName("PUBLISH DOCUMENT"); publishDocument.setId(8999L);

        SystemPermission deleteDocument = new Permission();
        deleteDocument.setName("DELETE DOCUMENT"); deleteDocument.setId(9988L);

        // Mocking PermissionRESTServiceClient
        try {
            PermissionRESTServiceAccess permissionRESTServiceAccess =
                    mock(PermissionRESTServiceAccess.class);
            when(permissionRESTServiceAccess.getPermissionById(readDocument.getId())).
                    thenReturn(Optional.of(readDocument));
            when(permissionRESTServiceAccess.getPermissionById(publishDocument.getId())).
                    thenReturn(Optional.of(publishDocument));
            when(permissionRESTServiceAccess.getPermissionById(deleteDocument.getId())).
                    thenReturn(Optional.of(deleteDocument));
            tenantRoleBusinessService.setPermissionRESTServiceAccess(permissionRESTServiceAccess);
        }
        catch (SystemException se) {
            fail("unexpected");
        }

        // Permissions were assigned to tenant id = 3 and role "publisher"
        List<SystemPermission> permissions = assertDoesNotThrow(() -> tenantRoleBusinessService.getPermissions(
                tenantId3, publisher.getId(), null));

        assertNotNull(permissions);
        assertEquals(permissions.size(), 3);

        // Permissions were not assigned to the following user
        Long nonRegisteredUser = 22222L;
        permissions = assertDoesNotThrow(() -> tenantRoleBusinessService.getPermissions(
                tenantId3, publisher.getId(), nonRegisteredUser));
        assertNotNull(permissions);
        assertEquals(permissions.size(), 0);

        // But were automatically assigned to user user1Id, since he is assigned to
        // correspondent role
        permissions = assertDoesNotThrow(() -> tenantRoleBusinessService.getPermissions(
                tenantId3, publisher.getId(), user1Id));
        assertNotNull(permissions);
        assertEquals(permissions.size(), 3);
    }

    @Test
    @Order(21)
    public void isPermissionExistentForUser() {
        // Get previously created Role
        SystemRole publisher = assertDoesNotThrow(() -> createRole("publisher"));

        // Previously created permissions
        SystemPermission readDocument = new Permission();
        readDocument.setName("READ DOCUMENT"); readDocument.setId(9999L);

        SystemPermission publishDocument = new Permission();
        publishDocument.setName("PUBLISH DOCUMENT"); publishDocument.setId(8999L);

        SystemPermission deleteDocument = new Permission();
        deleteDocument.setName("DELETE DOCUMENT"); deleteDocument.setId(9988L);

        // Checking without informing Tenant
        assertTrue(tenantRoleBusinessService.isPermissionExistentForUser(user1Id,
                readDocument.getId(), null));
        assertTrue(tenantRoleBusinessService.isPermissionExistentForUser(user1Id,
                publishDocument.getId(), null));
        assertTrue(tenantRoleBusinessService.isPermissionExistentForUser(user1Id,
                deleteDocument.getId(), null));

        // Checking informing Tenant
        assertTrue(tenantRoleBusinessService.isPermissionExistentForUser(user1Id,
                readDocument.getId(), tenantId3));
        assertTrue(tenantRoleBusinessService.isPermissionExistentForUser(user1Id,
                publishDocument.getId(), tenantId3));
        assertTrue(tenantRoleBusinessService.isPermissionExistentForUser(user1Id,
                deleteDocument.getId(), tenantId3));

        // Checking informing wrong Tenant
        assertFalse(tenantRoleBusinessService.isPermissionExistentForUser(user1Id,
                readDocument.getId(), 44L));
        assertFalse(tenantRoleBusinessService.isPermissionExistentForUser(user1Id,
                publishDocument.getId(), 44L));
        assertFalse(tenantRoleBusinessService.isPermissionExistentForUser(user1Id,
                deleteDocument.getId(), 44L));

        SystemPermission nonRegisteredPermission = new Permission();
        nonRegisteredPermission.setName("TEST APP"); nonRegisteredPermission.setId(1000L);

        // Checking informing non registered permission
        assertFalse(tenantRoleBusinessService.isPermissionExistentForUser(user1Id,
                nonRegisteredPermission.getId(), null));
        assertFalse(tenantRoleBusinessService.isPermissionExistentForUser(user1Id,
                nonRegisteredPermission.getId(), tenantId3));

    }

    @Test
    @Order(22)
    public void unAssignPermission() {
        // Get previously created Role
        SystemRole publisher = assertDoesNotThrow(() -> createRole("publisher"));

        // Previously created permissions
        SystemPermission deleteDocument = new Permission();
        deleteDocument.setName("DELETE DOCUMENT"); deleteDocument.setId(9988L);

        // Checking without informing Tenant
        assertTrue(tenantRoleBusinessService.isPermissionExistentForUser(user1Id,
                deleteDocument.getId(), null));

        // Checking informing Tenant
        assertTrue(tenantRoleBusinessService.isPermissionExistentForUser(user1Id,
                deleteDocument.getId(), tenantId3));

        // Removing permission
        assertDoesNotThrow(() -> tenantRoleBusinessService.unassignPermission(tenantId3,
                publisher.getId(), deleteDocument.getId()));

        // User has no access to the unassigned permission
        assertFalse(tenantRoleBusinessService.isPermissionExistentForUser(user1Id,
                deleteDocument.getId(), null));

        // Trying to (un)assign permission again
        assertThrows(TenantRoleException.class, () -> tenantRoleBusinessService.unassignPermission(tenantId3,
                publisher.getId(), deleteDocument.getId()));

    }

    @Test
    @Order(23)
    public void unAssignPermissionInvalidCase() {
        // Get previously created Role
        SystemRole publisher = assertDoesNotThrow(() -> createRole("publisher"));

        // Using previously created permission
        SystemPermission readDocument = new Permission();
        readDocument.setName("READ DOCUMENT"); readDocument.setId(9999L);

        // Trying to remove for a non registered tenant
        assertThrows(TenantRoleException.class, () -> tenantRoleBusinessService.
                unassignPermission(tenantId, publisher.getId(), readDocument.getId()));
    }

    @Test
    @Order(24)
    public void getAll() {
        Page<SystemTenantRole> page = assertDoesNotThrow(() ->
                tenantRoleBusinessService.getAll(1, 10));
        assertNotNull(page);
        assertNotNull(page.getResults());
        assertFalse(page.getResults().isEmpty());
        assertTrue(page.getTotalPages() > 0);
        assertTrue(page.getTotalResults() > 0);
    }

    @Test
    @Order(25)
    public void deleteTenantRoleInvalidCaseNotExistentAssociation() {
        // Trying to remove for a non registered tenant
        assertThrows(TenantRoleException.class, () -> tenantRoleBusinessService.
                delete(10000L));
    }

    @Test
    @Order(26)
    public void deleteTenantRoleInvalidCase() {
        // Get previously created Role
        SystemRole publisher = assertDoesNotThrow(() -> createRole("publisher"));

        List<? extends SystemTenantRole> specifics = tenantRoleBusinessService.
                getSpecific(tenantId3, publisher.getId(), true);

        assertNotNull(specifics);
        assertFalse(specifics.isEmpty());

        SystemTenantRole tenantRole = specifics.get(0);

        // There are permissions previously assigned
        List<Long> permissionIds = tenantRoleServiceAccess.
                getPermissions(tenantId3, publisher.getId(), null);
        assertFalse(permissionIds.isEmpty());

        // There are users previously assigned
        SystemTenantRoleUserSearchFilter filter = new TenantRoleUserSearchFilter();
        filter.setTenantRoleId(tenantRole.getId());
        List<? extends SystemTenantRoleUser> usersRef = tenantRoleUserServiceAccess.get(filter);
        assertFalse(usersRef.isEmpty());

        assertThrows(TenantRoleException.class,
                () -> tenantRoleBusinessService.delete(tenantRole.getId()));

        // (Un)assign the users
        for (SystemTenantRoleUser tru: usersRef) {
            assertDoesNotThrow(() -> tenantRoleBusinessService.
                    unassignUser(tenantId3, publisher.getId(), tru.getUserId()));
        }

        assertThrows(TenantRoleException.class,
                () -> tenantRoleBusinessService.delete(tenantRole.getId()));

        // (Un)assign the permissions
        for (Long permissionId : permissionIds) {
            assertDoesNotThrow(() -> tenantRoleBusinessService.
                    unassignPermission(tenantId3, publisher.getId(), permissionId));
        }
        assertDoesNotThrow(() -> tenantRoleBusinessService.delete(tenantRole.getId()));
    }

    @Test
    @Order(27)
    public void checkParamPermissionNotExists() {

        PermissionRESTServiceAccess permissionRESTServiceAccess =
                mock(PermissionRESTServiceAccess.class);

        // Positive case - no issue found
        Long tenantTestCase1 = 100L;
        Long permissionTestCase1 = 101L;
        Long permissionTestCase2 = 102L;
        SystemRole role = assertDoesNotThrow(() -> createRole("test"));

        // Setting mocked REST Client for positive test cases
        tenantRoleBusinessService.setPermissionRESTServiceAccess(permissionRESTServiceAccess);
        try {
            doThrow(new SystemException("HTTP 404 Not Found")).
                    when(permissionRESTServiceAccess).
                    isPermissionExistent(permissionTestCase1, null);
            doThrow(new SystemException("Communication breakdown")).
                    when(permissionRESTServiceAccess).
                    isPermissionExistent(permissionTestCase2, null);
        }
        catch (SystemException se) {
            fail("unexpected");
        }

        assertThrows(TenantRoleException.class, () -> tenantRoleBusinessService.
                assignPermission(1L, 2L, permissionTestCase1));

        assertThrows(SystemException.class, () -> tenantRoleBusinessService.
                assignPermission(1L, 2L, permissionTestCase2));

    }

    @Test
    @Order(28)
    public void checkParamTenantNotExists() {
        TenantRESTServiceAccess tenantRESTServiceAccess =
                mock(TenantRESTServiceAccess.class);

        tenantRoleBusinessService.setTenantRESTServiceAccess(tenantRESTServiceAccess);

        // Positive case - no issue found
        Long tenantTestCase1 = 100L;
        SystemRole role = assertDoesNotThrow(() -> createRole("test"));

        SystemTenantRole systemTenantRole = new TenantRole();
        systemTenantRole.setTenantId(tenantTestCase1);
        systemTenantRole.setRoleId(role.getId());

        // Setting mocked REST Client for positive test cases
        try {
            doThrow(new SystemException("Communication issue")).
                    when(tenantRESTServiceAccess).isTenantExistent(tenantTestCase1);
        }
        catch (SystemException se) {
            fail("unexpected");
        }

        assertThrows(SystemException.class, ()-> tenantRoleBusinessService.
                save(systemTenantRole));
    }

    @Test
    @Order(29)
    public void getTenantRESTServiceAccess() {
        TenantRESTServiceAccess tenantRESTServiceAccess =
                mock(TenantRESTServiceAccess.class);
        tenantRoleBusinessService.setTenantRESTServiceAccess(tenantRESTServiceAccess);
        assertEquals(tenantRESTServiceAccess, tenantRoleBusinessService.getTenantRESTServiceAccess());
    }

    @Test
    @Order(30)
    public void getPermissionRESTServiceAccess() {
        PermissionRESTServiceAccess permissionRESTServiceAccess =
                mock(PermissionRESTServiceAccess.class);
        tenantRoleBusinessService.setPermissionRESTServiceAccess(permissionRESTServiceAccess);
        assertEquals(permissionRESTServiceAccess, tenantRoleBusinessService.getPermissionRESTServiceAccess());
    }

    /**
     * Test for method TenantRoleBusinessService.getUsers(tenantRoleId, pageNumber, pageSize)
     */
    @Test
    @Order(31)
    public void getUsers() {
        // Retrieving all User associations
        Page<SystemTenantRoleUser> page = tenantRoleBusinessService.getUsers(null,
                1, 10);
        assertNotNull(page);
        assertFalse(page.getResults().isEmpty());
        assertTrue(page.getTotalResults() > 0);

        // Try to retrieve for a non existent TenantRole association
        page = tenantRoleBusinessService.getUsers(111111L, 1, 10);

        assertNotNull(page);
        assertTrue(page.getResults().isEmpty());
        assertTrue(page.getTotalResults() == 0);
    }
}
