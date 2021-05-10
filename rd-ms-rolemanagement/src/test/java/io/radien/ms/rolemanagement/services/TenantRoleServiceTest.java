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
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.model.tenantrole.SystemTenantRole;
import io.radien.api.model.tenantrole.SystemTenantRolePermission;
import io.radien.api.model.tenantrole.SystemTenantRoleSearchFilter;
import io.radien.api.model.tenantrole.SystemTenantRoleUser;
import io.radien.api.service.tenantrole.TenantRolePermissionServiceAccess;
import io.radien.api.service.tenantrole.TenantRoleServiceAccess;
import io.radien.api.service.tenantrole.TenantRoleUserServiceAccess;
import io.radien.exception.TenantRoleException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.rolemanagement.client.entities.TenantRoleSearchFilter;
import io.radien.ms.rolemanagement.entities.TenantRole;
import io.radien.ms.rolemanagement.entities.TenantRolePermission;
import io.radien.ms.rolemanagement.entities.TenantRoleUser;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import javax.ejb.EJBException;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import java.util.List;
import java.util.Properties;

/**
 * @author Newton Carvalho
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TenantRoleServiceTest {

    Properties p;
    TenantRoleServiceAccess tenantRoleServiceAccess;
    TenantRolePermissionServiceAccess tenantRolePermissionServiceAccess;
    TenantRoleUserServiceAccess tenantRoleUserServiceAccess;

    SystemTenant rootTenant = null;

    Long baseRoleId = 111L;
    Long baseTenantId = 222L;

    public TenantRoleServiceTest() throws Exception {
        p = new Properties();
        p.put("appframeDatabase", "new://Resource?type=DataSource");
        p.put("appframeDatabase.JdbcDriver", "org.hsqldb.jdbcDriver");
        p.put("appframeDatabase.JdbcUrl", "jdbc:hsqldb:mem:radienTest");
        p.put("appframeDatabase.userName", "sa");
        p.put("appframeDatabase.password", "");
        p.put("openejb.deployments.classpath.include",".*");
        p.put("openejb.deployments.classpath.exclude",".*rd-ms-usermanagement-client.*");


        final Context context = EJBContainer.createEJBContainer(p).getContext();

        String lookupString = "java:global/rd-ms-rolemanagement//TenantRoleService";
        tenantRoleServiceAccess = (TenantRoleServiceAccess) context.lookup(lookupString);

        lookupString = "java:global/rd-ms-rolemanagement//TenantRolePermissionService";
        tenantRolePermissionServiceAccess = (TenantRolePermissionServiceAccess) context.lookup(lookupString);

        lookupString = "java:global/rd-ms-rolemanagement//TenantRoleUserService";
        tenantRoleUserServiceAccess = (TenantRoleUserServiceAccess) context.lookup(lookupString);
    }

    /**
     * Add tenant role test.
     * Will create the new tenant role.
     * Expected result: Success.
     * Tested methods: void save(TenantRole tenantRole)     *
     * @throws UniquenessConstraintException in case of requested action is not well constructed
     */
    @Order(1)
    @Test
    public void testSave() throws UniquenessConstraintException {

        SystemTenantRole systemTenantRole = new TenantRole();
        systemTenantRole.setRoleId(baseRoleId);
        systemTenantRole.setTenantId(baseTenantId);

        this.tenantRoleServiceAccess.save(systemTenantRole);
        Assertions.assertNotNull(systemTenantRole.getId());
    }

    /**
     * Try to Add a tenant role with repeated information
     * Will not create the new tenant role.
     * Expected result: Fail. UniquenessConstraintException
     * Tested methods: void save(TenantRole tenantRole)     *
     */
    @Order(2)
    @Test
    public void testSaveDuplicatedWithError() {
        SystemTenantRole systemTenantRole = new TenantRole();
        systemTenantRole.setRoleId(baseRoleId);
        systemTenantRole.setTenantId(baseTenantId);
        Assertions.assertThrows(UniquenessConstraintException.class, () ->
                this.tenantRoleServiceAccess.save(systemTenantRole));
    }

    /**
     * Try to create a Tenant Role and then update it
     * Expected result: Success.
     * Tested methods: void save(TenantRole tenantRole)
     */
    @Test
    @Order(3)
    public void testUpdate() throws UniquenessConstraintException {
        Long originalRoleId = 22L, originalTenantId = 23L;

        SystemTenantRole systemTenantRole = new TenantRole();
        systemTenantRole.setRoleId(originalRoleId);
        systemTenantRole.setTenantId(originalTenantId);
        this.tenantRoleServiceAccess.save(systemTenantRole);

        Long generatedId = systemTenantRole.getId();
        Assertions.assertNotNull(generatedId);

        SystemTenantRole retrieved = this.tenantRoleServiceAccess.get(generatedId);
        Assertions.assertNotNull(retrieved);
        Assertions.assertEquals(retrieved.getRoleId(), originalRoleId);
        Assertions.assertEquals(retrieved.getTenantId(), originalTenantId);

        Long newRoleId = 44L, newTenantId = 45L;
        SystemTenantRole systemTenantRole1 = new TenantRole();
        systemTenantRole1.setId(generatedId);
        systemTenantRole1.setRoleId(newRoleId);
        systemTenantRole1.setTenantId(newTenantId);
        this.tenantRoleServiceAccess.save(systemTenantRole1);

        retrieved = this.tenantRoleServiceAccess.get(generatedId);
        Assertions.assertNotNull(retrieved.getId());
        Assertions.assertEquals(retrieved.getRoleId(), newRoleId);
        Assertions.assertEquals(retrieved.getTenantId(), newTenantId);
    }

    /**
     * Test updating a TenantRole with repeated information (The set Tenant + Role is unique)
     * Expected: Fail (Raising UniquenessConstraintException)
     * Tested methods: void save(TenantRole tenantRole)
     */
    @Test
    @Order(4)
    public void testUpdateWithError() {
        SystemTenantRole tenantRole = new TenantRole();
        tenantRole.setTenantId(101L);
        tenantRole.setRoleId(102L);
        Assertions.assertDoesNotThrow(() -> this.tenantRoleServiceAccess.save(tenantRole));

        SystemTenantRole updatableTenantRole = new TenantRole();
        updatableTenantRole.setId(tenantRole.getId());
        updatableTenantRole.setRoleId(baseRoleId);
        updatableTenantRole.setTenantId(baseTenantId);

        Assertions.assertThrows(UniquenessConstraintException.class,() -> this.tenantRoleServiceAccess.save(updatableTenantRole));
    }

    /**
     * Test retrieving a TenantRole by Id
     * Tested methods: SystemTenantRole get(Long id)
     */
    @Test
    @Order(5)
    public void testGetById() {

        SystemTenantRole systemTenantRole = new TenantRole();
        systemTenantRole.setTenantId(1L);
        systemTenantRole.setRoleId(1L);
        Assertions.assertDoesNotThrow(() -> tenantRoleServiceAccess.save(systemTenantRole));

        SystemTenantRole retrieved = tenantRoleServiceAccess.get(systemTenantRole.getId());
        Assertions.assertNotNull(retrieved);
    }

    /**
     * Test retrieving a TenantRole by an Id that does not exist
     * Expected: Success
     * Tested methods: SystemTenantRole get(Long id)
     */
    @Test
    @Order(6)
    public void testGetByIdNotExistent() {
        SystemTenantRole systemTenantRole = tenantRoleServiceAccess.get(999999L);
        Assertions.assertNull(systemTenantRole);
    }

    /**
     * Test checking if an association (Tenant + role) exists
     * Expected: SUCCESS
     */
    @Test
    @Order(7)
    public void testAssociationExists() {
        Assertions.assertTrue(this.tenantRoleServiceAccess.isAssociationAlreadyExistent(baseRoleId, baseTenantId));
    }

    /**
     * Test checking if an association (Tenant + role) exists
     * Expected: FAIL
     */
    @Test
    @Order(8)
    public void testAssociationNotExists() {
        Assertions.assertFalse(this.tenantRoleServiceAccess.isAssociationAlreadyExistent(baseRoleId, 88L));
        Assertions.assertFalse(this.tenantRoleServiceAccess.isAssociationAlreadyExistent(9L, 88L));
    }

    /**
     * Test checking if an association (Tenant + role) exists,
     * but omitting mandatory param Tenant Id
     * Expected: FAIL
     */
    @Test
    @Order(9)
    public void testAssociationWithoutInformingTenant() {
        EJBException ejbException = Assertions.assertThrows(EJBException.class,
                ()->tenantRoleServiceAccess.isAssociationAlreadyExistent(baseRoleId, null));
        Assertions.assertTrue(ejbException.getCausedByException() instanceof IllegalArgumentException);
        Assertions.assertTrue(ejbException.getCausedByException().getMessage().contains("Tenant Id is mandatory"));
    }

    /**
     * Test checking if an association (Tenant + role) exists,
     * but omitting mandatory param Role Id
     * Expected: FAIL
     */
    @Test
    @Order(10)
    public void testAssociationWithoutInformingRole() {
        EJBException ejbException = Assertions.assertThrows(EJBException.class,
                ()->tenantRoleServiceAccess.isAssociationAlreadyExistent(null, baseTenantId));
        Assertions.assertTrue(ejbException.getCausedByException() instanceof IllegalArgumentException);
        Assertions.assertTrue(ejbException.getCausedByException().getMessage().contains("Role Id is mandatory"));
    }

    /**
     * Try to delete an existent TenantRole
     * Expected: SUCCESS
     */
    @Test
    @Order(11)
    public void testDeleteTenantRole() {
        // Insert first
        SystemTenantRole tenantRole = new TenantRole();
        tenantRole.setRoleId(69L);
        tenantRole.setTenantId(70L);
        Assertions.assertDoesNotThrow(() -> tenantRoleServiceAccess.save(tenantRole));

        Long id = tenantRole.getId();

        // Try to delete
        Assertions.assertDoesNotThrow(() -> Assertions.assertTrue(tenantRoleServiceAccess.delete(id)));

        // Try to retrieve to confirm
        SystemTenantRole str = tenantRoleServiceAccess.get(id);
        Assertions.assertNull(str);
    }

    /**
     * Try to delete without informing Id
     * Expected: FAIL (Raise Exception)
     */
    @Test
    @Order(12)
    public void testDeleteWithoutInformingId() {
        EJBException e = Assertions.assertThrows(EJBException.class, () -> tenantRoleServiceAccess.delete(null));
        Assertions.assertTrue(e.getCausedByException() instanceof IllegalArgumentException);
    }

    /**
     * Try to delete an TenantRole that has an User (TenantRoleUser) associated
     * Expected: FAIL (Raising TenantRoleException)
     */
    @Test
    @Order(13)
    public void testDeleteWithUserAssociated() {
        // Step 1: Create a Tenant Role
        SystemTenantRole tenantRole = new TenantRole();
        tenantRole.setTenantId(120L);
        tenantRole.setRoleId(121L);
        Assertions.assertDoesNotThrow(() -> tenantRoleServiceAccess.save(tenantRole));

        // Step 2: Create a Tenant Role User
        SystemTenantRoleUser tenantRoleUser = new TenantRoleUser();
        tenantRoleUser.setTenantRoleId(tenantRole.getId());
        tenantRoleUser.setUserId(11111L);
        Assertions.assertDoesNotThrow(() -> tenantRoleUserServiceAccess.create(tenantRoleUser));

        // Step 3: Try to delete the Tenant Role
        Assertions.assertThrows(TenantRoleException.class, () -> tenantRoleServiceAccess.delete(tenantRole.getId()));
    }

    /**
     * Try to delete an TenantRole that has a Permission (TenantRolePermission) associated
     * Expected: FAIL (Raising TenantRoleException)
     */
    @Test
    @Order(14)
    public void testDeleteWithPermissionAssociated() {
        // Step 1: Create a Tenant Role
        SystemTenantRole tenantRole = new TenantRole();
        tenantRole.setTenantId(120L);
        tenantRole.setRoleId(122L);
        Assertions.assertDoesNotThrow(() -> tenantRoleServiceAccess.save(tenantRole));

        // Step 2: Create a Tenant Role Permission
        SystemTenantRolePermission tenantRolePermission = new TenantRolePermission();
        tenantRolePermission.setTenantRoleId(tenantRole.getId());
        tenantRolePermission.setPermissionId(11111L);
        Assertions.assertDoesNotThrow(() -> tenantRolePermissionServiceAccess.create(tenantRolePermission));

        // Step 3: Try to delete the Tenant Role
        Assertions.assertThrows(TenantRoleException.class, () -> tenantRoleServiceAccess.delete(tenantRole.getId()));
    }

    /**
     * Retrieves all TenantRoles (inserted during this test) under a pagination approach
     * Expected: SUCCESS (A page not empty)
     */
    @Test
    @Order(15)
    public void testPagination() {
        Page<SystemTenantRole> p = tenantRoleServiceAccess.getAll(1, 100);
        Assertions.assertNotNull(p);
        Assertions.assertTrue(p.getTotalResults() > 0);
        Assertions.assertTrue(p.getTotalPages() ==  1);
        Assertions.assertNotNull(p.getResults());
        Assertions.assertFalse(p.getResults().isEmpty());
    }

    /**
     * Retrieve TenantRole association under filter approach.
     * For this case, applies (as parameters) the tenant id and the role id already used to
     * create the first TenantRole association
     * Expected: SUCCESS
     */
    @Test
    @Order(16)
    public void testRetrieveTheFirstAssociationUsingFilter() {
        SystemTenantRoleSearchFilter filter = new TenantRoleSearchFilter(baseTenantId, baseRoleId,
                true, true);
        List<? extends SystemTenantRole> list = tenantRoleServiceAccess.get(filter);
        Assertions.assertNotNull(list);
        Assertions.assertEquals(list.size(), 1);
    }

    /**
     * Testing retrieval setting isLogicalConjunction to false (Performing OR instead AND)
     * Expected: Success
     */
    @Test
    @Order(17)
    public void testRetrieveSettingLogicConjunctionToFalse() {
        SystemTenantRole tenantRole = new TenantRole();
        tenantRole.setRoleId(404L);
        tenantRole.setTenantId(405L);
        Assertions.assertDoesNotThrow(() -> tenantRoleServiceAccess.save(tenantRole));

        SystemTenantRole tenantRole2 = new TenantRole();
        tenantRole2.setRoleId(406L);
        tenantRole2.setTenantId(407L);
        Assertions.assertDoesNotThrow(() -> tenantRoleServiceAccess.save(tenantRole2));

        // Using OR
        SystemTenantRoleSearchFilter filter = new TenantRoleSearchFilter(405L, 406L,
                true, false);
        List<? extends SystemTenantRole> list = tenantRoleServiceAccess.get(filter);
        Assertions.assertNotNull(list);
        Assertions.assertEquals(list.size(), 2);
    }

    /**
     * Filtering by parameters that does not exists
     * Expected: Fail (Empty collection)
     */
    @Test
    @Order(17)
    public void testFilterUsingInvalidParameters() {
        SystemTenantRoleSearchFilter filter = new TenantRoleSearchFilter(1234L, 4321L,
                true, true);
        List<? extends SystemTenantRole> list = tenantRoleServiceAccess.get(filter);
        Assertions.assertNotNull(list);
        Assertions.assertTrue(list.isEmpty());

        filter = new TenantRoleSearchFilter(1234L, 4321L,
                true, false);
        list = tenantRoleServiceAccess.get(filter);
        Assertions.assertNotNull(list);
        Assertions.assertTrue(list.isEmpty());
    }
}