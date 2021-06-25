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

import io.radien.api.model.tenantrole.SystemTenantRolePermission;
import io.radien.api.model.tenantrole.SystemTenantRolePermissionSearchFilter;
import io.radien.api.service.tenantrole.TenantRolePermissionServiceAccess;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.rolemanagement.client.entities.TenantRolePermissionSearchFilter;
import io.radien.ms.rolemanagement.entities.TenantRolePermission;
import org.junit.jupiter.api.*;

import javax.ejb.EJBException;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

/**
 * @author Newton Carvalho
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TenantRolePermissionServiceTest {

    static Properties p;
    static TenantRolePermissionServiceAccess tenantRolePermissionServiceAccess;
    static EJBContainer container;
    static Long basePermissionId = 111L;
    static Long baseTenantRoleId = 222L;

    @BeforeEach
    public void start() throws Exception {
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

        String lookupString = "java:global/rd-ms-rolemanagement//TenantRolePermissionService";
        tenantRolePermissionServiceAccess = (TenantRolePermissionServiceAccess) context.lookup(lookupString);

        container.getContext().bind("inject", this);
    }

    @AfterAll
    public static void stop() {
        if (container != null) {
            container.close();
        }
    }

    /**
     * Add tenant role permission test.
     * Will create the new tenant role permission.
     * Expected result: Success.
     * Tested methods: void save(TenantRolePermission tenantRolePermission)     *
     * @throws UniquenessConstraintException in case of requested action is not well constructed
     */

    @Order(1)
    @Test
    public void testCreate() throws UniquenessConstraintException {

        SystemTenantRolePermission systemTenantRolePermission = new TenantRolePermission();
        systemTenantRolePermission.setPermissionId(basePermissionId);
        systemTenantRolePermission.setTenantRoleId(baseTenantRoleId);

        this.tenantRolePermissionServiceAccess.create(systemTenantRolePermission);
        Assertions.assertNotNull(systemTenantRolePermission.getId());
    }

    /**
     * Try to Add a tenant role permission with repeated information
     * Will not create the new tenant role.
     * Expected result: Fail. UniquenessConstraintException
     * Tested methods: void save(TenantRolePermission tenantRolePermission)     *
     */
    @Order(2)
    @Test
    public void testCreateDuplicatedWithError() {
        SystemTenantRolePermission systemTenantRolePermission = new TenantRolePermission();
        systemTenantRolePermission.setPermissionId(basePermissionId);
        systemTenantRolePermission.setTenantRoleId(baseTenantRoleId);
        Assertions.assertThrows(UniquenessConstraintException.class, () ->
                this.tenantRolePermissionServiceAccess.create(systemTenantRolePermission));
    }

    /**
     * Test retrieving a TenantRolePermission by Id
     * Tested methods: SystemTenantRolePermission get(Long id)
     */
    @Test
    @Order(3)
    public void testGetById() {

        SystemTenantRolePermission systemTenantRolePermission = new TenantRolePermission();
        systemTenantRolePermission.setTenantRoleId(1L);
        systemTenantRolePermission.setPermissionId(1L);
        Assertions.assertDoesNotThrow(() -> tenantRolePermissionServiceAccess.create(systemTenantRolePermission));

        SystemTenantRolePermission retrieved = tenantRolePermissionServiceAccess.get(systemTenantRolePermission.getId());
        Assertions.assertNotNull(retrieved);
    }

    /**
     * Test retrieving a TenantRolePermission by an Id that does not exist
     * Expected: Success
     * Tested methods: SystemTenantRolePermission get(Long id)
     */
    @Test
    @Order(4)
    public void testGetByIdNotExistent() {
        SystemTenantRolePermission systemTenantRolePermission = tenantRolePermissionServiceAccess.get(999999L);
        Assertions.assertNull(systemTenantRolePermission);
    }

    /**
     * Test checking if an association (Tenant + role) exists
     * Expected: SUCCESS
     */
    @Test
    @Order(5)
    public void testAssociationExists() {
        Assertions.assertTrue(this.tenantRolePermissionServiceAccess.isAssociationAlreadyExistent(basePermissionId, baseTenantRoleId));
    }

    /**
     * Test checking if an association (Tenant + role) exists
     * Expected: FAIL
     */
    @Test
    @Order(6)
    public void testAssociationNotExists() {
        Assertions.assertFalse(this.tenantRolePermissionServiceAccess.isAssociationAlreadyExistent(basePermissionId, 88L));
        Assertions.assertFalse(this.tenantRolePermissionServiceAccess.isAssociationAlreadyExistent(9L, 88L));
    }

    /**
     * Test checking if an association (Tenant + role) exists,
     * but omitting mandatory param Tenant Id
     * Expected: FAIL
     */
    @Test
    @Order(7)
    public void testAssociationWithoutInformingTenantRole() {
        EJBException ejbException = Assertions.assertThrows(EJBException.class,
                ()->tenantRolePermissionServiceAccess.isAssociationAlreadyExistent(basePermissionId, null));
        Assertions.assertTrue(ejbException.getCausedByException() instanceof IllegalArgumentException);
        Assertions.assertTrue(ejbException.getCausedByException().getMessage().contains("Tenant Role Id is mandatory"));
    }

    /**
     * Test checking if an association (Tenant + role) exists,
     * but omitting mandatory param Role Id
     * Expected: FAIL
     */
    @Test
    @Order(8)
    public void testAssociationWithoutInformingPermission() {
        EJBException ejbException = Assertions.assertThrows(EJBException.class,
                ()->tenantRolePermissionServiceAccess.isAssociationAlreadyExistent(null, baseTenantRoleId));
        Assertions.assertTrue(ejbException.getCausedByException() instanceof IllegalArgumentException);
        Assertions.assertTrue(ejbException.getCausedByException().getMessage().contains("Permission Id is mandatory"));
    }

    /**
     * Try to delete an existent TenantRolePermission
     * Expected: SUCCESS
     */
    @Test
    @Order(9)
    public void testDeleteTenantRolePermission() {
        // Insert first
        SystemTenantRolePermission tenantRolePermission = new TenantRolePermission();
        tenantRolePermission.setPermissionId(69L);
        tenantRolePermission.setTenantRoleId(70L);
        Assertions.assertDoesNotThrow(() -> tenantRolePermissionServiceAccess.create(tenantRolePermission));

        Long id = tenantRolePermission.getId();

        // Try to delete
        Assertions.assertDoesNotThrow(() -> Assertions.assertTrue(tenantRolePermissionServiceAccess.delete(id)));

        // Try to retrieve to confirm
        SystemTenantRolePermission str = tenantRolePermissionServiceAccess.get(id);
        Assertions.assertNull(str);
    }

    /**
     * Try to delete without informing Id
     * Expected: FAIL (Raise Exception)
     */
    @Test
    @Order(10)
    public void testDeleteWithoutInformingId() {
        EJBException e = Assertions.assertThrows(EJBException.class, () -> tenantRolePermissionServiceAccess.delete(null));
        Assertions.assertTrue(e.getCausedByException() instanceof IllegalArgumentException);
    }

    /**
     * Retrieve TenantRolePermission association under filter approach.
     * For this case, applies (as parameters) the tenant id and the role id already used to
     * create the first TenantRolePermission association
     * Expected: SUCCESS
     */
    @Test
    @Order(11)
    public void testRetrieveTheFirstAssociationUsingFilter() {
        SystemTenantRolePermissionSearchFilter filter = new TenantRolePermissionSearchFilter(baseTenantRoleId, basePermissionId,
                true, true);
        List<? extends SystemTenantRolePermission> list = tenantRolePermissionServiceAccess.get(filter);
        Assertions.assertNotNull(list);
        Assertions.assertEquals(list.size(), 1);
    }

    /**
     * Testing retrieval setting isLogicalConjunction to false (Performing OR instead AND)
     * Expected: Success
     */
    @Test
    @Order(12)
    public void testRetrieveSettingLogicConjunctionToFalse() {
        SystemTenantRolePermission tenantRolePermission = new TenantRolePermission();
        tenantRolePermission.setPermissionId(404L);
        tenantRolePermission.setTenantRoleId(405L);
        Assertions.assertDoesNotThrow(() -> tenantRolePermissionServiceAccess.create(tenantRolePermission));

        SystemTenantRolePermission tenantRolePermission2 = new TenantRolePermission();
        tenantRolePermission2.setPermissionId(406L);
        tenantRolePermission2.setTenantRoleId(407L);
        Assertions.assertDoesNotThrow(() -> tenantRolePermissionServiceAccess.create(tenantRolePermission2));

        // Using OR
        SystemTenantRolePermissionSearchFilter filter = new TenantRolePermissionSearchFilter(405L, 406L,
                true, false);
        List<? extends SystemTenantRolePermission> list = tenantRolePermissionServiceAccess.get(filter);
        Assertions.assertNotNull(list);
        Assertions.assertEquals(list.size(), 2);
    }

    /**
     * Filtering by parameters that does not exists
     * Expected: Fail (Empty collection)
     */
    @Test
    @Order(13)
    public void testFilterUsingInvalidParameters() {
        SystemTenantRolePermissionSearchFilter filter = new TenantRolePermissionSearchFilter(1234L, 4321L,
                true, true);
        List<? extends SystemTenantRolePermission> list = tenantRolePermissionServiceAccess.get(filter);
        Assertions.assertNotNull(list);
        Assertions.assertTrue(list.isEmpty());

        filter = new TenantRolePermissionSearchFilter(1234L, 4321L,
                true, false);
        list = tenantRolePermissionServiceAccess.get(filter);
        Assertions.assertNotNull(list);
        Assertions.assertTrue(list.isEmpty());
    }

    /**
     * Test for method getTenantRolePermissionId(Long tenantRoleId, Long permissionId)
     */
    @Test
    @Order(15)
    public void testGetTenantRoleUserId() {
        SystemTenantRolePermission sru = new TenantRolePermission();
        sru.setTenantRoleId(101010L);
        sru.setPermissionId(101L);
        Assertions.assertDoesNotThrow(() -> this.tenantRolePermissionServiceAccess.create(sru));

        Long expectedId = sru.getId();
        Assertions.assertNotNull(expectedId);

        Optional<Long> id = this.tenantRolePermissionServiceAccess.getTenantRolePermissionId(101010L, 101L);
        Assertions.assertTrue(id.isPresent());
        Assertions.assertEquals(expectedId, id.get());

        id = this.tenantRolePermissionServiceAccess.getTenantRolePermissionId(101010L, 202L);
        Assertions.assertFalse(id.isPresent());
    }
}