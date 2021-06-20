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
 *//*

package io.radien.ms.rolemanagement.services;

import io.radien.api.entity.Page;
import io.radien.api.model.tenantrole.SystemTenantRoleUser;
import io.radien.api.model.tenantrole.SystemTenantRoleUserSearchFilter;
import io.radien.api.service.tenantrole.TenantRoleUserServiceAccess;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.rolemanagement.client.entities.TenantRoleUserSearchFilter;
import io.radien.ms.rolemanagement.entities.TenantRoleUser;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.*;

import javax.ejb.EJBException;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

*/
/**
 * @author Newton Carvalho
 *//*

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TenantRoleUserServiceTest {

    static Properties p;
    static TenantRoleUserServiceAccess tenantRoleUserServiceAccess;

    static Long baseUserId = 111L;
    static Long baseTenantRoleId = 222L;
    static EJBContainer container;

    @BeforeClass
    public static void start() throws Exception {
        p = new Properties();
        p.put("appframeDatabase", "new://Resource?type=DataSource");
        p.put("appframeDatabase.JdbcDriver", "org.hsqldb.jdbcDriver");
        p.put("appframeDatabase.JdbcUrl", "jdbc:hsqldb:mem:radienTest");
        p.put("appframeDatabase.userName", "sa");
        p.put("appframeDatabase.password", "");
        p.put("openejb.deployments.classpath.include",".*role.*");
        p.put("openejb.deployments.classpath.exclude",".*client.*");

        container = EJBContainer.createEJBContainer(p);
        final Context context = container.getContext();

        String lookupString = "java:global/rd-ms-rolemanagement//TenantRoleUserService";
        tenantRoleUserServiceAccess = (TenantRoleUserServiceAccess) context.lookup(lookupString);
    }

    @Before
    public void inject() throws NamingException {
        container.getContext().bind("inject", this);
    }

    @AfterClass
    public static void stop() {
        if (container != null) {
            container.close();
        }
    }

    */
/**
     * Add tenant role permission test.
     * Will create the new tenant role permission.
     * Expected result: Success.
     * Tested methods: void save(TenantRoleUser tenantRoleUser)     *
     * @throws UniquenessConstraintException in case of requested action is not well constructed
     *//*

    @Order(1)
    @Test
    public void testCreate() throws UniquenessConstraintException {

        SystemTenantRoleUser systemTenantRoleUser = new TenantRoleUser();
        systemTenantRoleUser.setUserId(baseUserId);
        systemTenantRoleUser.setTenantRoleId(baseTenantRoleId);

        this.tenantRoleUserServiceAccess.create(systemTenantRoleUser);
        Assertions.assertNotNull(systemTenantRoleUser.getId());
    }

    */
/**
     * Try to Add a tenant role permission with repeated information
     * Will not create the new tenant role.
     * Expected result: Fail. UniquenessConstraintException
     * Tested methods: void save(TenantRoleUser tenantRoleUser)     *
     *//*

    @Order(2)
    @Test
    public void testCreateDuplicatedWithError() {
        SystemTenantRoleUser systemTenantRoleUser = new TenantRoleUser();
        systemTenantRoleUser.setUserId(baseUserId);
        systemTenantRoleUser.setTenantRoleId(baseTenantRoleId);
        Assertions.assertThrows(UniquenessConstraintException.class, () ->
                this.tenantRoleUserServiceAccess.create(systemTenantRoleUser));
    }

    */
/**
     * Test retrieving a TenantRoleUser by Id
     * Tested methods: SystemTenantRoleUser get(Long id)
     *//*

    @Test
    @Order(3)
    public void testGetById() {

        SystemTenantRoleUser systemTenantRoleUser = new TenantRoleUser();
        systemTenantRoleUser.setTenantRoleId(1L);
        systemTenantRoleUser.setUserId(1L);
        Assertions.assertDoesNotThrow(() -> tenantRoleUserServiceAccess.create(systemTenantRoleUser));

        SystemTenantRoleUser retrieved = tenantRoleUserServiceAccess.get(systemTenantRoleUser.getId());
        Assertions.assertNotNull(retrieved);
    }

    */
/**
     * Test retrieving a TenantRoleUser by an Id that does not exist
     * Expected: Success
     * Tested methods: SystemTenantRoleUser get(Long id)
     *//*

    @Test
    @Order(4)
    public void testGetByIdNotExistent() {
        SystemTenantRoleUser systemTenantRoleUser = tenantRoleUserServiceAccess.get(999999L);
        Assertions.assertNull(systemTenantRoleUser);
    }

    */
/**
     * Test checking if an association (Tenant + role + user) exists
     * Expected: SUCCESS
     *//*

    @Test
    @Order(5)
    public void testAssociationExists() {
        Assertions.assertTrue(this.tenantRoleUserServiceAccess.isAssociationAlreadyExistent(baseUserId, baseTenantRoleId));
    }

    */
/**
     * Test checking if an association (Tenant + role + user) exists
     * Expected: FAIL
     *//*

    @Test
    @Order(6)
    public void testAssociationNotExists() {
        Assertions.assertFalse(this.tenantRoleUserServiceAccess.isAssociationAlreadyExistent(baseUserId, 88L));
        Assertions.assertFalse(this.tenantRoleUserServiceAccess.isAssociationAlreadyExistent(9L, 88L));
    }

    */
/**
     * Test checking if an association (Tenant + role + User) exists,
     * but omitting mandatory param Tenant Id
     * Expected: FAIL
     *//*

    @Test
    @Order(7)
    public void testAssociationWithoutInformingTenantRole() {
        EJBException ejbException = Assertions.assertThrows(EJBException.class,
                ()->tenantRoleUserServiceAccess.isAssociationAlreadyExistent(baseUserId, null));
        Assertions.assertTrue(ejbException.getCausedByException() instanceof IllegalArgumentException);
        Assertions.assertTrue(ejbException.getCausedByException().getMessage().contains("Tenant Role Id is mandatory"));
    }

    */
/**
     * Test checking if an association (Tenant + role + user) exists,
     * but omitting mandatory param Role Id
     * Expected: FAIL
     *//*

    @Test
    @Order(8)
    public void testAssociationWithoutInformingPermission() {
        EJBException ejbException = Assertions.assertThrows(EJBException.class,
                ()->tenantRoleUserServiceAccess.isAssociationAlreadyExistent(null, baseTenantRoleId));
        Assertions.assertTrue(ejbException.getCausedByException() instanceof IllegalArgumentException);
        Assertions.assertTrue(ejbException.getCausedByException().getMessage().contains("User Id is mandatory"));
    }

    */
/**
     * Try to delete an existent TenantRoleUser
     * Expected: SUCCESS
     *//*

    @Test
    @Order(9)
    public void testDeleteTenantRoleUser() {
        // Insert first
        SystemTenantRoleUser tenantRoleUser = new TenantRoleUser();
        tenantRoleUser.setUserId(69L);
        tenantRoleUser.setTenantRoleId(70L);
        Assertions.assertDoesNotThrow(() -> tenantRoleUserServiceAccess.create(tenantRoleUser));

        Long id = tenantRoleUser.getId();

        // Try to delete
        Assertions.assertDoesNotThrow(() -> Assertions.assertTrue(tenantRoleUserServiceAccess.delete(id)));

        // Try to retrieve to confirm
        SystemTenantRoleUser str = tenantRoleUserServiceAccess.get(id);
        Assertions.assertNull(str);
    }

    */
/**
     * Try to delete without informing Id
     * Expected: FAIL (Raise Exception)
     *//*

    @Test
    @Order(10)
    public void testDeleteWithoutInformingId() {
        EJBException e = Assertions.assertThrows(EJBException.class, () -> tenantRoleUserServiceAccess.delete(null));
        Assertions.assertTrue(e.getCausedByException() instanceof IllegalArgumentException);
    }

    */
/**
     * Retrieve TenantRoleUser association under filter approach.
     * For this case, applies (as parameters) the tenantRole id and the Permission id already used to
     * create the first TenantRoleUser association
     * Expected: SUCCESS
     *//*

    @Test
    @Order(11)
    public void testRetrieveTheFirstAssociationUsingFilter() {
        SystemTenantRoleUserSearchFilter filter = new TenantRoleUserSearchFilter(baseTenantRoleId, baseUserId,
                true, true);
        List<? extends SystemTenantRoleUser> list = tenantRoleUserServiceAccess.get(filter);
        Assertions.assertNotNull(list);
        Assertions.assertEquals(list.size(), 1);
    }

    */
/**
     * Testing retrieval setting isLogicalConjunction to false (Performing OR instead AND)
     * Expected: Success
     *//*

    @Test
    @Order(12)
    public void testRetrieveSettingLogicConjunctionToFalse() {
        SystemTenantRoleUser tenantRoleUser = new TenantRoleUser();
        tenantRoleUser.setUserId(404L);
        tenantRoleUser.setTenantRoleId(405L);
        Assertions.assertDoesNotThrow(() -> tenantRoleUserServiceAccess.create(tenantRoleUser));

        SystemTenantRoleUser tenantRoleUser2 = new TenantRoleUser();
        tenantRoleUser2.setUserId(406L);
        tenantRoleUser2.setTenantRoleId(407L);
        Assertions.assertDoesNotThrow(() -> tenantRoleUserServiceAccess.create(tenantRoleUser2));

        // Using OR
        SystemTenantRoleUserSearchFilter filter = new TenantRoleUserSearchFilter(405L, 406L,
                true, false);
        List<? extends SystemTenantRoleUser> list = tenantRoleUserServiceAccess.get(filter);
        Assertions.assertNotNull(list);
        Assertions.assertEquals(list.size(), 2);
    }

    */
/**
     * Filtering by parameters that does not exists
     * Expected: Fail (Empty collection)
     *//*

    @Test
    @Order(13)
    public void testFilterUsingInvalidParameters() {
        SystemTenantRoleUserSearchFilter filter = new TenantRoleUserSearchFilter(1234L, 4321L,
                true, true);
        List<? extends SystemTenantRoleUser> list = tenantRoleUserServiceAccess.get(filter);
        Assertions.assertNotNull(list);
        Assertions.assertTrue(list.isEmpty());

        filter = new TenantRoleUserSearchFilter(1234L, 4321L,
                true, false);
        list = tenantRoleUserServiceAccess.get(filter);
        Assertions.assertNotNull(list);
        Assertions.assertTrue(list.isEmpty());
    }

    */
/**
     * Retrieves all TenantRoleUsers (inserted during this test) under a pagination approach
     * Expected: SUCCESS (A page not empty)
     *//*

    @Test
    @Order(14)
    public void testPagination() {
        Page<SystemTenantRoleUser> p = tenantRoleUserServiceAccess.getAll(1, 100);
        Assertions.assertNotNull(p);
        Assertions.assertTrue(p.getTotalResults() > 0);
        Assertions.assertTrue(p.getTotalPages() ==  1);
        Assertions.assertNotNull(p.getResults());
        Assertions.assertFalse(p.getResults().isEmpty());
    }

    */
/**
     * Test for method getTenantRoleUserId(Long tenantRoleId, Long userId)
     *//*

    @Test
    @Order(15)
    public void testGetTenantRoleUserId() {
        SystemTenantRoleUser sru = new TenantRoleUser();
        sru.setTenantRoleId(101010L);
        sru.setUserId(101L);
        Assertions.assertDoesNotThrow(() -> this.tenantRoleUserServiceAccess.create(sru));

        Long expectedId = sru.getId();
        Assertions.assertNotNull(expectedId);

        Optional<Long> id = this.tenantRoleUserServiceAccess.getTenantRoleUserId(101010L, 101L);
        Assertions.assertTrue(id.isPresent());
        Assertions.assertEquals(expectedId, id.get());

        id = this.tenantRoleUserServiceAccess.getTenantRoleUserId(101010L, 202L);
        Assertions.assertFalse(id.isPresent());
    }
}*/
