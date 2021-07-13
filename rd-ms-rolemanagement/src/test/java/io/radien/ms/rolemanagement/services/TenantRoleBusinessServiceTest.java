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
import io.radien.api.service.tenant.ActiveTenantRESTServiceAccess;
import io.radien.api.service.tenant.TenantRESTServiceAccess;
import io.radien.api.service.tenantrole.TenantRolePermissionServiceAccess;
import io.radien.api.service.tenantrole.TenantRoleServiceAccess;
import io.radien.api.service.tenantrole.TenantRoleUserServiceAccess;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.NotFoundException;
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
import io.radien.ms.tenantmanagement.client.exceptions.InternalServerErrorException;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import static io.radien.exception.GenericErrorCodeMessage.TENANT_ROLE_NO_ASSOCIATION_FOUND_FOR;
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
 * Tenant Role Business Service rest requests and responses into the db access
 * {@link io.radien.ms.rolemanagement.services.TenantRoleBusinessService}
 *
 * @author Newton Carvalho
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TenantRoleBusinessServiceTest {

    static Long user1Id = 1111L;
    static Long tenantId = 1000L;
    static Long tenantId2 = 2000L;
    static Long tenantId3 = 3000L;

    static String roleNameForTenantAdministrator = "Tenant Administrator";

    static Properties p;
    static TenantRoleBusinessService tenantRoleBusinessService;

    static TenantRoleServiceAccess tenantRoleServiceAccess;
    static TenantRolePermissionServiceAccess tenantRolePermissionServiceAccess;
    static TenantRoleUserServiceAccess tenantRoleUserServiceAccess;
    static RoleServiceAccess roleServiceAccess;
    static EJBContainer container;


    static String lookupString;

    /**
     * Method before test preparation
     * @throws NamingException in case of injection naming exception
     */
    @BeforeAll
    public static void start() throws NamingException {
        p = new Properties();
        p.put("appframeDatabase", "new://Resource?type=DataSource");
        p.put("appframeDatabase.JdbcDriver", "org.hsqldb.jdbcDriver");
        p.put("appframeDatabase.JdbcUrl", "jdbc:hsqldb:mem:radienTest");
        p.put("appframeDatabase.userName", "sa");
        p.put("appframeDatabase.password", "");
        p.put("openejb.deployments.classpath.include",".*role.*");
        p.put("openejb.deployments.classpath.exclude",".*client.*");
        p.put("openejb.cdi.activated-on-ejb", "false");

        container = EJBContainer.createEJBContainer(p);
        final Context context = container.getContext();

        lookupString = "java:global/rd-ms-rolemanagement//TenantRoleService";
        tenantRoleServiceAccess = (TenantRoleServiceAccess) context.lookup(lookupString);

        lookupString = "java:global/rd-ms-rolemanagement//TenantRolePermissionService";
        tenantRolePermissionServiceAccess = (TenantRolePermissionServiceAccess) context.lookup(lookupString);

        lookupString = "java:global/rd-ms-rolemanagement//TenantRoleUserService";
        tenantRoleUserServiceAccess = (TenantRoleUserServiceAccess) context.lookup(lookupString);

        lookupString = "java:global/rd-ms-rolemanagement//RoleService";
        roleServiceAccess = (RoleServiceAccess) context.lookup(lookupString);

        tenantRoleBusinessService = new TenantRoleBusinessService();
        tenantRoleBusinessService.setRoleServiceAccess(roleServiceAccess);
        tenantRoleBusinessService.setTenantRoleServiceAccess(tenantRoleServiceAccess);
        tenantRoleBusinessService.setTenantRolePermissionService(tenantRolePermissionServiceAccess);
        tenantRoleBusinessService.setTenantRoleUserServiceAccess(tenantRoleUserServiceAccess);
        tenantRoleBusinessService.setTenantRESTServiceAccess(mock(TenantRESTServiceAccess.class));
        tenantRoleBusinessService.setActiveTenantRESTServiceAccess(mock(ActiveTenantRESTServiceAccess.class));
    }

    /**
     * Injection method
     * @throws NamingException in case of injection naming exception
     */
    @BeforeEach
    public void inject() throws NamingException {
        container.getContext().bind("inject", this);
    }

    /**
     * Method to stop the container after the testing classes have perform
     */
    @AfterAll
    public static void stop() {
        if (container != null) {
            container.close();
        }
    }

    /**
     * Method to get a specific role by specific given name
     * @param name to be found
     * @return the requested system role
     */
    protected SystemRole getRoleByName(String name) {
        SystemRoleSearchFilter filter = new RoleSearchFilter();
        filter.setName(name);
        List<? extends SystemRole> roles = roleServiceAccess.getSpecificRoles(filter);
        return roles.isEmpty() ? null : roles.get(0);
    }

    /**
     * Method to create a requested role
     * @param name of the role to be created
     * @return the newly created system role
     * @throws RoleNotFoundException in case the role could not be found
     * @throws UniquenessConstraintException in case of duplicated name
     */
    protected SystemRole createRole(String name) throws RoleNotFoundException, UniquenessConstraintException {
        SystemRole sr = getRoleByName(name);
        if (sr == null) {
            sr = new Role();
            sr.setName(name);
            roleServiceAccess.save(sr);
        }
        return sr;
    }

    /**
     * Method to create a specific tenant role
     * @param roleId to be associated
     * @param tenantId to be associated
     * @return the newly created system tenant role
     * @throws SystemException in case of token expiration
     * @throws UniquenessConstraintException in case of duplicated fields
     * @throws TenantRoleException in case of issue while creating the newly tenant role
     */
    protected SystemTenantRole createTenantRole(Long roleId, Long tenantId) throws
            SystemException, UniquenessConstraintException, TenantRoleException {

        List<? extends SystemTenantRole> tenantRoles = tenantRoleBusinessService.
                getSpecific(tenantId, roleId, true);

        SystemTenantRole tenantRole = tenantRoles.isEmpty() ? null : tenantRoles.get(0);

        if (tenantRole == null) {
            tenantRole = new TenantRole();
            tenantRole.setRoleId(roleId);
            tenantRole.setTenantId(tenantId);

            if (tenantRoleBusinessService.getTenantRESTServiceAccess() == null) {
                tenantRoleBusinessService.setTenantRESTServiceAccess(mock(TenantRESTServiceAccess.class));
            }

            when(tenantRoleBusinessService.getTenantRESTServiceAccess().isTenantExistent(tenantId)).
                    thenReturn(Boolean.TRUE);

            tenantRoleBusinessService.save(tenantRole);
        }

        return tenantRole;
    }

    /**
     * Test to create tenant role administrator
     */
    @Test
    @Order(2)
    public void createRoleAdmin() {
        SystemRole roleAdmin = assertDoesNotThrow(() -> createRole(roleNameForTenantAdministrator));
        assertNotNull(roleAdmin);
    }

    /**
     * Test to create a tenant role
     */
    @Test
    @Order(3)
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

        try {
            when(tenantRoleBusinessService.getTenantRESTServiceAccess().isTenantExistent(mockedInvalidTenant)).
                    thenReturn(Boolean.FALSE);
            when(tenantRoleBusinessService.getTenantRESTServiceAccess().isTenantExistent(mockedValidTenant)).
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

    /**
     * Test to validate the get by id
     */
    @Test
    @Order(4)
    public void getById() {
        SystemRole roleTestAdmin = assertDoesNotThrow(() -> createRole("testAdmin"));
        SystemTenantRole tenantRole = assertDoesNotThrow(() ->
                createTenantRole(roleTestAdmin.getId(), tenantId));

        SystemTenantRole retrievedById = assertDoesNotThrow(() ->
                tenantRoleBusinessService.getById(tenantRole.getId()));

        assertNotNull(retrievedById);
        assertEquals(retrievedById.getId(), tenantRole.getId());
    }

    /**
     * Test to validate the get by id with not found exception
     */
    @Test
    @Order(5)
    public void getByIdNotFoundCase() {
        Long notExistentTenantRoleId = 11111L;
        assertThrows(TenantRoleException.class, () ->
                tenantRoleBusinessService.getById(notExistentTenantRoleId));
    }

    /**
     * Test to validate if requested association already exists
     */
    @Test
    @Order(6)
    public void existAssociation() {
        SystemRole roleAdmin = assertDoesNotThrow(() -> createRole(roleNameForTenantAdministrator));
        assertDoesNotThrow(() ->
                createTenantRole(roleAdmin.getId(), tenantId));
        assertTrue(this.tenantRoleBusinessService.
                existsAssociation(tenantId, roleAdmin.getId()));
    }

    /**
     * Test to validate if requested association already exists but without any to be found
     */
    @Test
    @Order(7)
    public void existAssociationNegativeCase() {
        assertFalse(this.tenantRoleBusinessService.
                existsAssociation(1111L, 1000L));
    }

    /**
     * Test to assign user into association
     * @throws SystemException in case of communication issues with Tenant Rest Client or
     * ActiveTenant Rest Client.
     */
    @Test
    @Order(8)
    public void assignUser() throws SystemException{
        // Assign user to the role "Tenant Administrator" for the tenant "1"
        SystemRole roleAdmin = assertDoesNotThrow(() -> createRole(roleNameForTenantAdministrator));

        // Create mocked Tenants
        Tenant tenantForId1 = new Tenant(); tenantForId1.setId(tenantId);

        // Mock for Tenant Rest Client
        when(tenantRoleBusinessService.getTenantRESTServiceAccess().getTenantById(tenantId)).
                thenReturn(Optional.of(tenantForId1));

        ActiveTenantRESTServiceAccess activeTenantRESTServiceAccess = mock(ActiveTenantRESTServiceAccess.class);
        when(activeTenantRESTServiceAccess.isActiveTenantExistent(user1Id, tenantId)).thenReturn(Boolean.FALSE);
        when(activeTenantRESTServiceAccess.create(any())).thenReturn(Boolean.TRUE);
        tenantRoleBusinessService.setActiveTenantRESTServiceAccess(activeTenantRESTServiceAccess);

        assertDoesNotThrow(() -> this.tenantRoleBusinessService.assignUser(tenantId,
                roleAdmin.getId(), user1Id));
    }

    /**
     * Test to assign user into association but with an invalid association
     */
    @Test
    @Order(9)
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

    /**
     * Test to validate if given role is existent for specific user
     */
    @Test
    @Order(10)
    public void isRoleExistentForUser() {
        // Check for Role Name
        assertTrue(this.tenantRoleBusinessService.isRoleExistentForUser(user1Id,
                roleNameForTenantAdministrator, null));
        // Check for Role Name and specific Tenant
        assertTrue(this.tenantRoleBusinessService.isRoleExistentForUser(user1Id,
                roleNameForTenantAdministrator, tenantId));

    }

    /**
     * Test to validate if given role is existent for specific user but without anything to be found
     */
    @Test
    @Order(11)
    public void isRoleExistentForUserNegativeCases() {
        // Check for Role Name under non associated tenant
        Long notAssociatedTenant = 343L;
        assertFalse(this.tenantRoleBusinessService.isRoleExistentForUser(user1Id,
                roleNameForTenantAdministrator, notAssociatedTenant));
        // Check for not associated Role
        assertFalse(this.tenantRoleBusinessService.isRoleExistentForUser(user1Id,
                "super admin", tenantId));
    }

    /**
     * Test to validate if user has any role
     * @throws SystemException
     */
    @Test
    @Order(12)
    public void isAnyRoleExistentForUser() throws SystemException {
        Long tenant4 = 4L;
        Long tenant5 = 5L;

        tenantRoleBusinessService.setTenantRESTServiceAccess(mock(TenantRESTServiceAccess.class));
        tenantRoleBusinessService.setActiveTenantRESTServiceAccess(mock(ActiveTenantRESTServiceAccess.class));

        // Create mocked Tenants
        Tenant tenantForId1 = new Tenant(); tenantForId1.setId(tenantId);
        Tenant tenantForId4 = new Tenant(); tenantForId4.setId(tenant4);
        Tenant tenantForId5 = new Tenant(); tenantForId4.setId(tenant5);

        when(tenantRoleBusinessService.getTenantRESTServiceAccess().
                getTenantById(tenantId)).thenReturn(Optional.of(tenantForId1));
        when(tenantRoleBusinessService.getTenantRESTServiceAccess().
                getTenantById(tenant4)).thenReturn(Optional.of(tenantForId4));
        when(tenantRoleBusinessService.getTenantRESTServiceAccess().
                getTenantById(tenant5)).thenReturn(Optional.of(tenantForId5));

        // Assign user to the role "READER" for the tenant "1"
        SystemRole reader = assertDoesNotThrow(() -> createRole("READER"));
        assertDoesNotThrow(() -> createTenantRole(reader.getId(), tenantId));
        assertDoesNotThrow(() -> this.tenantRoleBusinessService.assignUser(tenantId,
                reader.getId(), user1Id));

        // Assign user to the role "WRITER" for the tenant "4"
        SystemRole writer = assertDoesNotThrow(() -> createRole("WRITER"));
        assertDoesNotThrow(() -> createTenantRole(writer.getId(), tenant4));
        assertDoesNotThrow(() -> this.tenantRoleBusinessService.assignUser(tenant4,
                writer.getId(), user1Id));

        // Assign user to the role "OBSERVER" for the tenant "5"
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

    /**
     * Test for {@link TenantRoleBusinessService#getTenants(Long, Long)}
     * @throws SystemException in case of any communication issue regarding Tenant Rest Client
     * @throws UniquenessConstraintException in case of duplicated values during insertion
     * @Throws TenantRoleException if user assigment already exists
     */
    @Test
    @Order(13)
    public void getTenants() throws SystemException, UniquenessConstraintException, TenantRoleException, RoleNotFoundException {
//        Long user = 100000L;

        // Create new roles
        SystemRole guest = createRole("guest");
        SystemRole publisher = createRole("publisher");

        // Create mocked Tenants
        Tenant tenantForId1 = new Tenant(); tenantForId1.setId(tenantId);
        tenantRoleBusinessService.setTenantRESTServiceAccess(mock(TenantRESTServiceAccess.class));
        when(tenantRoleBusinessService.getTenantRESTServiceAccess().getTenantById(tenantId)).
                thenReturn(Optional.of(tenantForId1));
        Tenant tenantForId2 = new Tenant(); tenantForId2.setId(tenantId2);
        when(tenantRoleBusinessService.getTenantRESTServiceAccess().getTenantById(tenantId2)).
                thenReturn(Optional.of(tenantForId2));
        Tenant tenantForId3 = new Tenant(); tenantForId3.setId(tenantId3);
        when(tenantRoleBusinessService.getTenantRESTServiceAccess().getTenantById(tenantId3)).
                thenReturn(Optional.of(tenantForId3));

        // Create Tenant Role associations (all for user Id 1)
        createTenantRole(guest.getId(), tenantId);
        createTenantRole(guest.getId(), tenantId2);
        createTenantRole(publisher.getId(), tenantId3);

        tenantRoleBusinessService.assignUser(tenantId, guest.getId(), user1Id);
        tenantRoleBusinessService.assignUser(tenantId2, guest.getId(), user1Id);
        tenantRoleBusinessService.assignUser(tenantId3, publisher.getId(), user1Id);

        // User is associated with All 3 Tenants
        List<SystemTenant> tenants = assertDoesNotThrow(() ->
                tenantRoleBusinessService.getTenants(user1Id, null));
        assertNotNull(tenants);
        assertEquals(3, tenants.size());

        // User is associated in one single tenant For the role guest
        tenants = assertDoesNotThrow(() ->
                tenantRoleBusinessService.getTenants(user1Id, guest.getId()));
        assertNotNull(tenants);
        assertEquals(2, tenants.size());
    }

    /**
     * Test to get invalid tenants
     */
    @Test
    @Order(14)
    public void getTenantsInvalidCase() {
        // Try to retrieve tenants taking in account a Role for which the user is not associated
        Long notAssociateRoleId = 8888L;
        List<SystemTenant> tenants = assertDoesNotThrow(() ->
                tenantRoleBusinessService.getTenants(user1Id, notAssociateRoleId));
        assertNotNull(tenants);
        assertTrue(tenants.isEmpty());
    }

    /**
     * Test to un-assign a user from the association
     */
    @Test
    @Order(15)
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

    /**
     * Test to un-assign a user using invalid parameters (tenant and user with null values)
     */
    @Test
    @Order(16)
    public void unAssignUserWithNullableParameters() {
        // Get previously created Role
        SystemRole guest = assertDoesNotThrow(() -> createRole("guest"));

        // Doing (un)assignment process with null tenant
        Long invalidTenant = null;
        String expectedErrorMessage = GenericErrorCodeMessage.TENANT_ROLE_FIELD_MANDATORY.toString("tenant id");
        TenantRoleException tre = assertThrows(TenantRoleException.class, () -> tenantRoleBusinessService.
                unassignUser(invalidTenant, guest.getId(), user1Id));
        assertEquals(expectedErrorMessage, tre.getMessage());

        // Doing (un)assignment process with null user
        Long validTenant = 1111111L, invalidUser = null;
        expectedErrorMessage = GenericErrorCodeMessage.TENANT_ROLE_FIELD_MANDATORY.toString("user id");
        tre = assertThrows(TenantRoleException.class, () -> tenantRoleBusinessService.
                unassignUser(validTenant, guest.getId(), invalidUser));
        assertEquals(expectedErrorMessage, tre.getMessage());
    }

    /**
     * Test to un-assign a user, but in a scenario where there is no associations
     * for the three parameters (user, tenant and role)
     */
    @Test
    @Order(17)
    public void unAssignUserWithInvalidTenantRoleUserCombination() {
        // Get previously created Role
        SystemRole guest = assertDoesNotThrow(() -> createRole("guest"));

        // Doing (un)assignment process with a tenant/role combination that does not exist
        Long invalidTenant = 1111111111111L, invalidUser = 2222222222222L;
        String expectedErrorMessage = TENANT_ROLE_NO_ASSOCIATION_FOUND_FOR.
                toString(String.valueOf(invalidTenant), String.valueOf(guest.getId()), String.valueOf(invalidUser));
        TenantRoleException tre = assertThrows(TenantRoleException.class, () -> tenantRoleBusinessService.
                unassignUser(invalidTenant, guest.getId(), invalidUser));
        assertEquals(expectedErrorMessage, tre.getMessage());
    }

    /**
     * Test to assign permission to tenatn role association
     */
    @Test
    @Order(18)
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

    /**
     * Test to assign permissions into a invalid non existent tenant role association
     */
    @Test
    @Order(19)
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
        TenantRoleException e = assertThrows(TenantRoleException.class,  () ->
                tenantRoleBusinessService.assignPermission(tenantId3,
                        11111L, assemblyDocument.getId()));
        assertNotNull(e.getMessage());
    }

    /**
     * Test to assign permission into an already assigned permission existent in a tenant role association
     */
    @Test
    @Order(20)
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

    /**
     * Test to validate the retrieval of the permissions
     */
    @Test
    @Order(21)
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
        assertEquals(3, permissions.size());

        // Permissions were not assigned to the following user
        Long nonRegisteredUser = 22222L;
        permissions = assertDoesNotThrow(() -> tenantRoleBusinessService.getPermissions(
                tenantId3, publisher.getId(), nonRegisteredUser));
        assertNotNull(permissions);
        assertEquals(0, permissions.size());

        // But were automatically assigned to user user1Id, since he is assigned to
        // correspondent role
        permissions = assertDoesNotThrow(() -> tenantRoleBusinessService.getPermissions(
                tenantId3, publisher.getId(), user1Id));
        assertNotNull(permissions);
        assertEquals(3, permissions.size());
    }

    /**
     * Test to validate which permission is existent for the given user
     */
    @Test
    @Order(22)
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

    /**
     * Test to un-assign permission
     */
    @Test
    @Order(23)
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

    /**
     * Test to un-assign permission that are not valid
     */
    @Test
    @Order(24)
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

    /**
     * Test to get all the tenant role associations
     */
    @Test
    @Order(25)
    public void getAll() {
        Page<SystemTenantRole> page = assertDoesNotThrow(() ->
                tenantRoleBusinessService.getAll(1, 10));
        assertNotNull(page);
        assertNotNull(page.getResults());
        assertFalse(page.getResults().isEmpty());
        assertTrue(page.getTotalPages() > 0);
        assertTrue(page.getTotalResults() > 0);
    }

    /**
     * Test to delete a specific association that does not exist
     */
    @Test
    @Order(26)
    public void deleteTenantRoleInvalidCaseNotExistentAssociation() {
        // Trying to remove for a non registered tenant
        assertThrows(TenantRoleException.class, () -> tenantRoleBusinessService.
                delete(10000L));
    }

    /**
     * Test to delete tenant roles that are invalid
     */
    @Test
    @Order(27)
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

    /**
     * Test to check the permission in the association parameters that do not exist
     */
    @Test
    @Order(28)
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
    }

    /**
     * Test to check the tenant in the association parameters that do not exist
     */
    @Test
    @Order(29)
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

        assertThrows(TenantRoleException.class, ()-> tenantRoleBusinessService.
                save(systemTenantRole));
    }

    /**
     * Test to validate if we can retrieve the correct tenant rest service access
     */
    @Test
    @Order(30)
    public void getTenantRESTServiceAccess() {
        TenantRESTServiceAccess tenantRESTServiceAccess =
                mock(TenantRESTServiceAccess.class);
        tenantRoleBusinessService.setTenantRESTServiceAccess(tenantRESTServiceAccess);
        assertEquals(tenantRESTServiceAccess, tenantRoleBusinessService.getTenantRESTServiceAccess());
    }

    /**
     * Test to validate if we can retrieve the correct permission rest service access
     */
    @Test
    @Order(31)
    public void getPermissionRESTServiceAccess() {
        PermissionRESTServiceAccess permissionRESTServiceAccess =
                mock(PermissionRESTServiceAccess.class);
        tenantRoleBusinessService.setPermissionRESTServiceAccess(permissionRESTServiceAccess);
        assertEquals(permissionRESTServiceAccess, tenantRoleBusinessService.getPermissionRESTServiceAccess());
    }

    /**
     * Test to validate if we can retrieve the correct tenant role service access
     */
    @Test
    @Order(32)
    public void getTenantRoleServiceAccess() {
        TenantRoleServiceAccess mockTenantRoleServiceAccess = mock(TenantRoleServiceAccess.class);
        tenantRoleBusinessService.setTenantRoleServiceAccess(mockTenantRoleServiceAccess);
        assertEquals(mockTenantRoleServiceAccess, tenantRoleBusinessService.getTenantRoleServiceAccess());
    }

    /**
     * Test to validate if we can retrieve the correct tenant role user service access
     */
    @Test
    @Order(33)
    public void getTenantRoleUserServiceAccess() {
        TenantRoleUserServiceAccess mockTenantRoleUserServiceAccess = mock(TenantRoleUserServiceAccess.class);
        tenantRoleBusinessService.setTenantRoleUserServiceAccess(mockTenantRoleUserServiceAccess);
        assertEquals(mockTenantRoleUserServiceAccess, tenantRoleBusinessService.getTenantRoleUserServiceAccess());
    }

    /**
     * Test to validate if we can retrieve the correct tenant role permission service access
     */
    @Test
    @Order(34)
    public void getTenantRolePermissionServiceAccess() {
        TenantRolePermissionServiceAccess mockTenantRolePermissionServiceAccess = mock(TenantRolePermissionServiceAccess.class);
        tenantRoleBusinessService.setTenantRolePermissionService(mockTenantRolePermissionServiceAccess);
        assertEquals(mockTenantRolePermissionServiceAccess, tenantRoleBusinessService.getTenantRolePermissionService());
    }

    /**
     * Test to validate if we can retrieve the correct tenant role service access
     */
    @Test
    @Order(35)
    public void getRoleServiceAccess() {
        RoleServiceAccess mockRoleServiceAccess = mock(RoleServiceAccess.class);
        tenantRoleBusinessService.setRoleServiceAccess(mockRoleServiceAccess);
        assertEquals(mockRoleServiceAccess, tenantRoleBusinessService.getRoleServiceAccess());
    }

    /**
     * Test to validate if we can retrieve the correct active tenant rest service access
     */
    @Test
    @Order(36)
    public void getActiveTenantRESTServiceAccess() {
        ActiveTenantRESTServiceAccess mockActiveTenantRESTServiceAccess = mock(ActiveTenantRESTServiceAccess.class);
        tenantRoleBusinessService.setActiveTenantRESTServiceAccess(mockActiveTenantRESTServiceAccess);
        assertEquals(mockActiveTenantRESTServiceAccess, tenantRoleBusinessService.getActiveTenantRESTServiceAccess());
    }

    /**
     * Test method {@link TenantRoleBusinessService#retrieveTenant(Long)} for
     * a scenario where Tenant does not exist
     * @throws SystemException thrown by TenantRESTServiceAccess when trying to retrieve tenant by id
     */
    @Test
    @Order(37)
    public void retrieveNotExistentTenant() throws SystemException {
        Long notExistentTenantId = 1111111L;
        String expectedErrorMsg = GenericErrorCodeMessage.
                TENANT_ROLE_NO_TENANT_FOUND.toString(String.valueOf(notExistentTenantId));
        TenantRESTServiceAccess mockedTenantRestServiceAccess = mock(TenantRESTServiceAccess.class);
        tenantRoleBusinessService.setTenantRESTServiceAccess(mockedTenantRestServiceAccess);
        when(mockedTenantRestServiceAccess.getTenantById(notExistentTenantId)).
                thenThrow(new NotFoundException());
        TenantRoleException tre = assertThrows(TenantRoleException.class, () -> tenantRoleBusinessService.
                retrieveTenant(notExistentTenantId));
        assertEquals(expectedErrorMsg, tre.getMessage());
    }

    /**
     * Test method {@link TenantRoleBusinessService#retrieveTenant(Long)} in a scenario where Internal Server Error occurs
     * @throws SystemException thrown by TenantRESTServiceAccess when trying to retrieve tenant by id
     */
    @Test
    @Order(37)
    public void retrieveTenantWithInternalServerError() throws SystemException {
        Long notExistentTenantId = 1111111L;
        String errorMSg = "Internal Server Error - 500";
        String expectedErrorMsg = new SystemException(new InternalServerErrorException(errorMSg)).getMessage();
        TenantRESTServiceAccess mockedTenantRestServiceAccess = mock(TenantRESTServiceAccess.class);
        tenantRoleBusinessService.setTenantRESTServiceAccess(mockedTenantRestServiceAccess);
        when(mockedTenantRestServiceAccess.getTenantById(notExistentTenantId)).
                thenThrow(new InternalServerErrorException(errorMSg));
        SystemException se = assertThrows(SystemException.class, () -> tenantRoleBusinessService.
                retrieveTenant(notExistentTenantId));
        assertEquals(expectedErrorMsg, se.getMessage());
    }


    /**
     * Test method {@link TenantRoleBusinessService#retrieveTenant(Long)} in a scenario where
     * SytemException occurs
     * @throws SystemException thrown by TenantRESTServiceAccess when trying to retrieve tenant by id
     */
    @Test
    @Order(38)
    public void retrieveTenantWithSystemException() throws SystemException {
        Long notExistentTenantId = 1111111L;
        String expectedErrorMsg = new SystemException(new SystemException()).getMessage();
        TenantRESTServiceAccess mockedTenantRestServiceAccess = mock(TenantRESTServiceAccess.class);
        tenantRoleBusinessService.setTenantRESTServiceAccess(mockedTenantRestServiceAccess);
        when(mockedTenantRestServiceAccess.getTenantById(notExistentTenantId)).
                thenThrow(new SystemException());
        SystemException se = assertThrows(SystemException.class, () -> tenantRoleBusinessService.
                retrieveTenant(notExistentTenantId));
        assertEquals(expectedErrorMsg, se.getMessage());
    }
}