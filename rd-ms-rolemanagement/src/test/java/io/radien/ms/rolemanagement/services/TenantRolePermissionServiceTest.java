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

import io.radien.api.SystemVariables;
import io.radien.api.entity.Page;
import io.radien.api.model.tenantrole.SystemTenantRolePermission;
import io.radien.api.model.tenantrole.SystemTenantRolePermissionSearchFilter;
import io.radien.api.service.tenantrole.TenantRolePermissionServiceAccess;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.TenantRolePermissionNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.rolemanagement.client.entities.TenantRolePermissionSearchFilter;
import io.radien.ms.rolemanagement.entities.TenantRolePermissionEntity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import javax.ejb.EJBException;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tenant Role Permission Service tester
 * {@link io.radien.ms.rolemanagement.services.TenantRolePermissionService}
 *
 * @author Newton Carvalho
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TenantRolePermissionServiceTest {

    static Properties p;
    static TenantRolePermissionServiceAccess tenantRolePermissionServiceAccess;
    static EJBContainer container;
    static Long basePermissionId = 111L;
    static Long baseTenantRoleId = 222L;

    /**
     * Test preparation db connection
     * @throws Exception in case of failure mocking connection into the db
     */
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

    /**
     * Method to close the container after tests have run
     */
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

        SystemTenantRolePermission systemTenantRolePermission = new TenantRolePermissionEntity();
        systemTenantRolePermission.setPermissionId(basePermissionId);
        systemTenantRolePermission.setTenantRoleId(baseTenantRoleId);

        tenantRolePermissionServiceAccess.create(systemTenantRolePermission);
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
        SystemTenantRolePermission systemTenantRolePermission = new TenantRolePermissionEntity();
        systemTenantRolePermission.setPermissionId(basePermissionId);
        systemTenantRolePermission.setTenantRoleId(baseTenantRoleId);
        Assertions.assertThrows(UniquenessConstraintException.class, () ->
                tenantRolePermissionServiceAccess.create(systemTenantRolePermission));
    }

    /**
     * Test retrieving a TenantRolePermission by Id
     * Tested methods: SystemTenantRolePermission get(Long id)
     */
    @Test
    @Order(3)
    public void testGetById() {

        SystemTenantRolePermission systemTenantRolePermission = new TenantRolePermissionEntity();
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
        Assertions.assertTrue(tenantRolePermissionServiceAccess.isAssociationAlreadyExistent(basePermissionId, baseTenantRoleId));
    }

    /**
     * Test checking if an association (Tenant + role) exists
     * Expected: FAIL
     */
    @Test
    @Order(6)
    public void testAssociationNotExists() {
        Assertions.assertFalse(tenantRolePermissionServiceAccess.isAssociationAlreadyExistent(basePermissionId, 88L));
        Assertions.assertFalse(tenantRolePermissionServiceAccess.isAssociationAlreadyExistent(9L, 88L));
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
        Assertions.assertEquals(GenericErrorCodeMessage.TENANT_ROLE_FIELD_MANDATORY.toString(SystemVariables.ID.getLabel()), ejbException.getCausedByException().getMessage());
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
        Assertions.assertTrue(ejbException.getCausedByException().getMessage().contains(GenericErrorCodeMessage.TENANT_ROLE_FIELD_MANDATORY.toString(SystemVariables.PERMISSION_ID.getLabel())));
    }

    /**
     * Try to delete an existent TenantRolePermission
     * Expected: SUCCESS
     */
    @Test
    @Order(9)
    public void testDeleteTenantRolePermission() {
        // Insert first
        SystemTenantRolePermission tenantRolePermission = new TenantRolePermissionEntity();
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
        Assertions.assertEquals(1, list.size());
    }

    /**
     * Testing retrieval setting isLogicalConjunction to false (Performing OR instead AND)
     * Expected: Success
     */
    @Test
    @Order(12)
    public void testRetrieveSettingLogicConjunctionToFalse() {
        SystemTenantRolePermission tenantRolePermission = new TenantRolePermissionEntity();
        tenantRolePermission.setPermissionId(404L);
        tenantRolePermission.setTenantRoleId(405L);
        Assertions.assertDoesNotThrow(() -> tenantRolePermissionServiceAccess.create(tenantRolePermission));

        SystemTenantRolePermission tenantRolePermission2 = new TenantRolePermissionEntity();
        tenantRolePermission2.setPermissionId(406L);
        tenantRolePermission2.setTenantRoleId(407L);
        Assertions.assertDoesNotThrow(() -> tenantRolePermissionServiceAccess.create(tenantRolePermission2));

        // Using OR
        SystemTenantRolePermissionSearchFilter filter = new TenantRolePermissionSearchFilter(405L, 406L,
                true, false);
        List<? extends SystemTenantRolePermission> list = tenantRolePermissionServiceAccess.get(filter);
        Assertions.assertNotNull(list);
        Assertions.assertEquals(2, list.size());
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
        SystemTenantRolePermission sru = new TenantRolePermissionEntity();
        sru.setTenantRoleId(101010L);
        sru.setPermissionId(101L);
        Assertions.assertDoesNotThrow(() -> tenantRolePermissionServiceAccess.create(sru));

        Long expectedId = sru.getId();
        Assertions.assertNotNull(expectedId);

        Optional<Long> id = tenantRolePermissionServiceAccess.getTenantRolePermissionId(101010L, 101L);
        Assertions.assertTrue(id.isPresent());
        Assertions.assertEquals(expectedId, id.get());

        id = tenantRolePermissionServiceAccess.getTenantRolePermissionId(101010L, 202L);
        Assertions.assertFalse(id.isPresent());

        try{
            tenantRolePermissionServiceAccess.getTenantRolePermissionId(null, 202L);
        } catch (Exception e) {
            Assertions.assertTrue(e.getMessage().contains(GenericErrorCodeMessage.TENANT_ROLE_FIELD_MANDATORY.toString(SystemVariables.TENANT_ROLE_ID.getLabel())));
        }
    }

    /**
     * Test for method getTenantRolePermissionId(Long tenantRoleId, Long permissionId)
     */
    @Test
    @Order(16)
    public void testGetTenantRoleUserIdPermissionNull() {
        SystemTenantRolePermission sru = new TenantRolePermissionEntity();
        sru.setTenantRoleId(201010L);
        sru.setPermissionId(101L);
        Assertions.assertDoesNotThrow(() -> tenantRolePermissionServiceAccess.create(sru));

        Long expectedId = sru.getId();
        Assertions.assertNotNull(expectedId);

        Optional<Long> id = tenantRolePermissionServiceAccess.getTenantRolePermissionId(201010L, 101L);
        Assertions.assertTrue(id.isPresent());
        Assertions.assertEquals(expectedId, id.get());

        id = tenantRolePermissionServiceAccess.getTenantRolePermissionId(201010L, 202L);
        Assertions.assertFalse(id.isPresent());

        try{
            tenantRolePermissionServiceAccess.getTenantRolePermissionId(201010L, null);
        } catch (Exception e) {
            Assertions.assertTrue(e.getMessage().contains(GenericErrorCodeMessage.TENANT_ROLE_FIELD_MANDATORY.toString(SystemVariables.PERMISSION_ID.getLabel())));
        }
    }


    /**
     * Test method for {@link TenantRolePermissionService#getAll(Long, Long, int, int, List, boolean)}
     * @throws UniquenessConstraintException to be thrown in cases of repeated values
     */
    @Test
    @Order(17)
    void testTenantRolePermissionPagination() throws UniquenessConstraintException {
        Long tenantRoleId = 9000000L;
        Long permissionId = 9000001L;
        Long permissionId2 = 9000002L;

        SystemTenantRolePermission trp = new TenantRolePermissionEntity();
        trp.setTenantRoleId(tenantRoleId);
        trp.setPermissionId(permissionId);
        tenantRolePermissionServiceAccess.create(trp);

        SystemTenantRolePermission trp2 = new TenantRolePermissionEntity();
        trp2.setTenantRoleId(tenantRoleId);
        trp2.setPermissionId(permissionId2);
        tenantRolePermissionServiceAccess.create(trp2);

        List<String> sortBy = new ArrayList<>();
        sortBy.add("tenantRoleId");
        sortBy.add("permissionId");

        Page<SystemTenantRolePermission> page = tenantRolePermissionServiceAccess.getAll(tenantRoleId, permissionId, 1, 10,
                sortBy, true);
        assertEquals(1, page.getTotalResults());

        page = tenantRolePermissionServiceAccess.getAll(tenantRoleId, permissionId, 1, 10,
                null, true);
        assertEquals(1, page.getTotalResults());

        page = tenantRolePermissionServiceAccess.getAll(tenantRoleId, permissionId, 1, 10,
                new ArrayList<>(), true);
        assertEquals(1, page.getTotalResults());

        page = tenantRolePermissionServiceAccess.getAll(tenantRoleId, null, 1, 10,
                new ArrayList<>(), true);
        assertEquals(2, page.getTotalResults());

        page = tenantRolePermissionServiceAccess.getAll(null, permissionId, 1, 10,
                new ArrayList<>(), true);
        assertEquals(1, page.getTotalResults());

        page = tenantRolePermissionServiceAccess.getAll(null, null, 1, 10,
                Collections.singletonList(SystemVariables.TENANT_ROLE_ID.getFieldName()), true);
        assertTrue(page.getTotalResults() >= 2);

        page = tenantRolePermissionServiceAccess.getAll(999999999L, 99999L, 1, 10,
                Collections.singletonList(SystemVariables.TENANT_ROLE_ID.getFieldName()), false);
        assertEquals(0,page.getTotalResults());
    }

    /**
     * Test for method {@link io.radien.api.service.tenantrole.TenantRolePermissionServiceAccess#update(SystemTenantRolePermission)}
     * @throws UniquenessConstraintException to be thrown in cases of repeated values
     * @throws TenantRolePermissionNotFoundException to be thrown in case of not found a tenant role permission
     */
    @Test
    public void testUpdate() throws UniquenessConstraintException, TenantRolePermissionNotFoundException {
        Long tenantRoleId = 1111111111L;
        Long permissionId = 1111111111L;
        Long id = 1111111111L;

        SystemTenantRolePermission trp1 = new TenantRolePermissionEntity();
        trp1.setTenantRoleId(tenantRoleId);
        trp1.setPermissionId(permissionId);
        tenantRolePermissionServiceAccess.create(trp1);

        Long permissionId2 = 111111111112L;
        SystemTenantRolePermission trp2 = new TenantRolePermissionEntity();
        trp2 = new TenantRolePermissionEntity();
        trp2.setTenantRoleId(tenantRoleId);
        trp2.setPermissionId(permissionId2);
        tenantRolePermissionServiceAccess.create(trp2);

        SystemTenantRolePermission trp3 = new TenantRolePermissionEntity();
        trp3.setId(trp2.getId());
        trp3.setTenantRoleId(tenantRoleId);
        trp3.setPermissionId(permissionId);
        assertThrows(UniquenessConstraintException.class, ()->
                tenantRolePermissionServiceAccess.update(trp3));

        trp3.setPermissionId(122378900L);
        assertDoesNotThrow(()->tenantRolePermissionServiceAccess.update(trp3));

        SystemTenantRolePermission trp4 = new TenantRolePermissionEntity();
        trp4.setId(1111111111L);
        trp4.setTenantRoleId(tenantRoleId);
        trp4.setPermissionId(permissionId);
        assertThrows(TenantRolePermissionNotFoundException.class, ()->
                tenantRolePermissionServiceAccess.update(trp4));


    }
}