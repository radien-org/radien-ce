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
import io.radien.api.model.tenantrole.SystemTenantRole;
import io.radien.api.model.tenantrole.SystemTenantRoleUser;
import io.radien.api.model.tenantrole.SystemTenantRoleUserSearchFilter;
import io.radien.api.service.tenantrole.TenantRoleServiceAccess;
import io.radien.api.service.tenantrole.TenantRoleUserServiceAccess;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.rolemanagement.client.entities.TenantRoleUserSearchFilter;
import io.radien.ms.rolemanagement.entities.TenantRole;
import io.radien.ms.rolemanagement.entities.TenantRoleUser;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import javax.ejb.EJBException;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.NamingException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tenant Role User Service rest requests and responses with access into the db
 * {@link io.radien.ms.rolemanagement.services.TenantRoleServiceTest}
 * @author Newton Carvalho
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TenantRoleUserServiceTest {

    static Properties p;
    static TenantRoleServiceAccess tenantRoleServiceAccess;
    static TenantRoleUserServiceAccess tenantRoleUserServiceAccess;

    static Long baseUserId = 111L;
    static Long baseTenantRoleId = 222L;
    static EJBContainer container;

    /**
     * Method before test preparation
     */
    @BeforeAll
    public static void start() {
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
    }

    /**
     * Injection method before starting the tests and data preparation
     * @throws NamingException in case of naming injection value exception
     */
    @BeforeEach
    public void inject() throws NamingException {
        String lookupString = "java:global/rd-ms-rolemanagement//TenantRoleUserService";
        tenantRoleUserServiceAccess = (TenantRoleUserServiceAccess) container.getContext().lookup(lookupString);
        lookupString = "java:global/rd-ms-rolemanagement//TenantRoleService";
        tenantRoleServiceAccess = (TenantRoleServiceAccess) container.getContext().lookup(lookupString);
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
     * Add tenant role permission test.
     * Will create the new tenant role permission.
     * Expected result: Success.
     * Tested methods: void save(TenantRoleUser tenantRoleUser)     *
     * @throws UniquenessConstraintException in case of requested action is not well constructed
     */
    @Order(1)
    @Test
    public void testCreate() throws UniquenessConstraintException {

        SystemTenantRoleUser systemTenantRoleUser = new TenantRoleUser();
        systemTenantRoleUser.setUserId(baseUserId);
        systemTenantRoleUser.setTenantRoleId(baseTenantRoleId);

        this.tenantRoleUserServiceAccess.create(systemTenantRoleUser);
        Assertions.assertNotNull(systemTenantRoleUser.getId());
    }

    /**
     * Try to Add a tenant role permission with repeated information
     * Will not create the new tenant role.
     * Expected result: Fail. UniquenessConstraintException
     * Tested methods: void save(TenantRoleUser tenantRoleUser)     *
     */
    @Order(2)
    @Test
    public void testCreateDuplicatedWithError() {
        SystemTenantRoleUser systemTenantRoleUser = new TenantRoleUser();
        systemTenantRoleUser.setUserId(baseUserId);
        systemTenantRoleUser.setTenantRoleId(baseTenantRoleId);
        Assertions.assertThrows(UniquenessConstraintException.class, () ->
                this.tenantRoleUserServiceAccess.create(systemTenantRoleUser));
    }

    /**
     * Test retrieving a TenantRoleUser by Id
     * Tested methods: SystemTenantRoleUser get(Long id)
     */
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

    /**
     * Test retrieving a TenantRoleUser by an Id that does not exist
     * Expected: Success
     * Tested methods: SystemTenantRoleUser get(Long id)
     */
    @Test
    @Order(4)
    public void testGetByIdNotExistent() {
        SystemTenantRoleUser systemTenantRoleUser = tenantRoleUserServiceAccess.get(999999L);
        Assertions.assertNull(systemTenantRoleUser);
    }

    /**
     * Test checking if an association (Tenant + role + user) exists
     * Expected: SUCCESS
     */
    @Test
    @Order(5)
    public void testAssociationExists() {
        Assertions.assertTrue(this.tenantRoleUserServiceAccess.isAssociationAlreadyExistent(baseUserId, baseTenantRoleId));
    }

    /**
     * Test checking if an association (Tenant + role + user) exists
     * Expected: FAIL
     */
    @Test
    @Order(6)
    public void testAssociationNotExists() {
        Assertions.assertFalse(this.tenantRoleUserServiceAccess.isAssociationAlreadyExistent(baseUserId, 88L));
        Assertions.assertFalse(this.tenantRoleUserServiceAccess.isAssociationAlreadyExistent(9L, 88L));
    }

    /**
     * Test checking if an association (Tenant + role + User) exists,
     * but omitting mandatory param Tenant Id
     * Expected: FAIL
     */
    @Test
    @Order(7)
    public void testAssociationWithoutInformingTenantRole() {
        EJBException ejbException = Assertions.assertThrows(EJBException.class,
                ()->tenantRoleUserServiceAccess.isAssociationAlreadyExistent(baseUserId, null));
        Assertions.assertTrue(ejbException.getCausedByException() instanceof IllegalArgumentException);
        Assertions.assertEquals(GenericErrorCodeMessage.TENANT_ROLE_FIELD_MANDATORY.toString("id"), ejbException.getCausedByException().getMessage());
    }

    /**
     * Test checking if an association (Tenant + role + user) exists,
     * but omitting mandatory param Role Id
     * Expected: FAIL
     */
    @Test
    @Order(8)
    public void testAssociationWithoutInformingPermission() {
        EJBException ejbException = Assertions.assertThrows(EJBException.class,
                ()->tenantRoleUserServiceAccess.isAssociationAlreadyExistent(null, baseTenantRoleId));
        Assertions.assertTrue(ejbException.getCausedByException() instanceof IllegalArgumentException);
        Assertions.assertTrue(ejbException.getCausedByException().getMessage().contains(GenericErrorCodeMessage.TENANT_ROLE_FIELD_MANDATORY.toString("user id")));
    }

    /**
     * Try to delete an existent TenantRoleUser
     * Expected: SUCCESS
     */
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

    /**
     * Try to delete without informing Id
     * Expected: FAIL (Raise Exception)
     */
    @Test
    @Order(10)
    public void testDeleteWithoutInformingId() {
        Long idAsNull = null;
        EJBException e = Assertions.assertThrows(EJBException.class, () -> tenantRoleUserServiceAccess.delete(idAsNull));
        Assertions.assertTrue(e.getCausedByException() instanceof IllegalArgumentException);
    }

    /**
     * Retrieve TenantRoleUser association under filter approach.
     * For this case, applies (as parameters) the tenantRole id and the Permission id already used to
     * create the first TenantRoleUser association
     * Expected: SUCCESS
     */
    @Test
    @Order(11)
    public void testRetrieveTheFirstAssociationUsingFilter() {
        SystemTenantRoleUserSearchFilter filter = new TenantRoleUserSearchFilter(baseTenantRoleId, baseUserId,
                true, true);
        List<? extends SystemTenantRoleUser> list = tenantRoleUserServiceAccess.get(filter);
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
        Assertions.assertEquals(2, list.size());
    }

    /**
     * Filtering by parameters that does not exists
     * Expected: Fail (Empty collection)
     */
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

    /**
     * Retrieves all TenantRoleUsers (inserted during this test) under a pagination approach
     * Expected: SUCCESS (A page not empty)
     */
    @Test
    @Order(14)
    public void testPagination() {
        Page<SystemTenantRoleUser> p = tenantRoleUserServiceAccess.getAll(null,1, 100);
        Assertions.assertNotNull(p);
        Assertions.assertTrue(p.getTotalResults() > 0);
        Assertions.assertEquals(1, p.getTotalPages());
        Assertions.assertNotNull(p.getResults());
        Assertions.assertFalse(p.getResults().isEmpty());
    }

    /**
     * Retrieves all TenantRoleUsers (inserted during this test) under a pagination approach,
     * but taking in account an specific tenantRole identifier
     * Expected: SUCCESS (A page not empty)
     */
    @Test
    @Order(15)
    public void testPaginationWithTenantRoleSpecified() {
        Page<SystemTenantRoleUser> p = tenantRoleUserServiceAccess.getAll(baseTenantRoleId,
                1, 100);
        Assertions.assertNotNull(p);
        Assertions.assertTrue(p.getTotalResults() > 0);
        Assertions.assertEquals(1, p.getTotalPages());
        Assertions.assertNotNull(p.getResults());
        Assertions.assertFalse(p.getResults().isEmpty());
    }

    /**
     * Try to retrieve TenantRoleUsers (under a pagination approach,
     * but taking in account a tenantRole that does not exist
     * Expected: An empty page
     */
    @Test
    @Order(16)
    public void testPaginationForNotExistentTenantRole() {
        Page<SystemTenantRoleUser> p = tenantRoleUserServiceAccess.getAll(10000L,
                1, 100);
        Assertions.assertNotNull(p);
        Assertions.assertEquals(0,p.getTotalResults());
        Assertions.assertEquals(0,p.getTotalPages());
        Assertions.assertNotNull(p.getResults());
        Assertions.assertTrue(p.getResults().isEmpty());
    }

    /**
     * Test for method getTenantRoleUserId(Long tenantRoleId, Long userId)
     */
    @Test
    @Order(17)
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

        try{
            this.tenantRoleUserServiceAccess.getTenantRoleUserId(null, 202L);
        } catch (Exception e) {
            Assertions.assertTrue(e.getMessage().contains(GenericErrorCodeMessage.TENANT_ROLE_FIELD_MANDATORY.toString("id")));
        }
    }

    /**
     * Test for method getTenantRoleUserId(Long tenantRoleId, Long userId)
     */
    @Test
    @Order(16)
    public void testGetTenantRoleUserIdNullUser() {
        SystemTenantRoleUser sru = new TenantRoleUser();
        sru.setTenantRoleId(301010L);
        sru.setUserId(601L);
        Assertions.assertDoesNotThrow(() -> this.tenantRoleUserServiceAccess.create(sru));

        Long expectedId = sru.getId();
        Assertions.assertNotNull(expectedId);

        Optional<Long> id = this.tenantRoleUserServiceAccess.getTenantRoleUserId(301010L, 601L);
        Assertions.assertTrue(id.isPresent());
        Assertions.assertEquals(expectedId, id.get());

        id = this.tenantRoleUserServiceAccess.getTenantRoleUserId(301010L, 602L);
        Assertions.assertFalse(id.isPresent());

        try{
            this.tenantRoleUserServiceAccess.getTenantRoleUserId(301010L, null);
        } catch (Exception e) {
            Assertions.assertTrue(e.getMessage().contains(GenericErrorCodeMessage.TENANT_ROLE_FIELD_MANDATORY.toString("user id")));
        }
    }

    /**
     * Utility method to create TenantRole instances and reduce cognitive complexity
     * @param tenant tenant id
     * @param role role id
     * @throws UniquenessConstraintException thrown in case of repeated value (in therms of ids combination)
     * @return SystemTenantRole instance for the given parameters
     */
    protected SystemTenantRole createTenantRole(Long tenant, Long role) throws UniquenessConstraintException {
        SystemTenantRole tenantRole = new TenantRole();
        tenantRole.setTenantId(tenant);
        tenantRole.setRoleId(role);
        tenantRoleServiceAccess.save(tenantRole);
        return tenantRole;
    }

    /**
     * Utility method to create TenantRoleUser instances and reduce cognitive complexity
     * @param tr System Tenant Role instance
     * @param user user ide
     * @throws UniquenessConstraintException thrown in case of repeated value (in therms of ids combination)
     * @return SystemTenantRoleUser instance for the given parameters
     */
    protected SystemTenantRoleUser createTenantRoleUser(SystemTenantRole tr, Long user) throws UniquenessConstraintException {
        SystemTenantRoleUser tenantRoleUser = new TenantRoleUser();
        tenantRoleUser.setTenantRoleId(tr.getId());
        tenantRoleUser.setUserId(user);
        tenantRoleUserServiceAccess.create(tenantRoleUser);
        return tenantRoleUser;
    }

    /**
     * Test for method {@link TenantRoleUserServiceAccess#delete(Collection)}
     * Given a Tenant and User Id, remove the TenantRoleUser associations
     * @throws UniquenessConstraintException in case of insertion (save) using repeated values (combination of ids)
     */
    @Test
    @Order(17)
    public void testDeleteByTenantAndUserInformation() throws UniquenessConstraintException {
        Long role1 = 700001L, role2 = 700002L, role3 = 700003L;
        Long tenant1 = 500001L, tenant2 = 500002L, tenant3 = 500003L;
        Long user1 = 800001L, user2 = 800002L;

        SystemTenantRole tenant1Role1 = createTenantRole(tenant1, role1);
        SystemTenantRole tenant1Role2 = createTenantRole(tenant1, role2);
        SystemTenantRole tenant1Role3 = createTenantRole(tenant1, role3);

        SystemTenantRoleUser tenant1Role1User1 = createTenantRoleUser(tenant1Role1, user1);
        SystemTenantRoleUser tenant1Role2User1 = createTenantRoleUser(tenant1Role2, user1);
        SystemTenantRoleUser tenant1Role3User1 = createTenantRoleUser(tenant1Role3, user1);

        SystemTenantRoleUser tenant1Role1User2 = createTenantRoleUser(tenant1Role1, user2);
        SystemTenantRoleUser tenant1Role2User2 = createTenantRoleUser(tenant1Role2, user2);
        SystemTenantRoleUser tenant1Role3User2 = createTenantRoleUser(tenant1Role3, user2);

        // Delete
        Collection<Long> ids = tenantRoleUserServiceAccess.getIds(tenant1, null, user1);
        assertTrue(tenantRoleUserServiceAccess.delete(ids));
        ids = tenantRoleUserServiceAccess.getIds(tenant1, role1, user2);
        assertTrue(tenantRoleUserServiceAccess.delete(ids));

        // Check if association cannot be found for user1
        assertFalse(tenantRoleUserServiceAccess.isAssociationAlreadyExistent(user1, tenant1Role1User1.getTenantRoleId()));
        assertFalse(tenantRoleUserServiceAccess.isAssociationAlreadyExistent(user1, tenant1Role2User1.getTenantRoleId()));
        assertFalse(tenantRoleUserServiceAccess.isAssociationAlreadyExistent(user1, tenant1Role3User1.getTenantRoleId()));

        // Check if association cannot be found for user2
        assertFalse(tenantRoleUserServiceAccess.isAssociationAlreadyExistent(user2, tenant1Role1User2.getTenantRoleId()));
        assertTrue(tenantRoleUserServiceAccess.isAssociationAlreadyExistent(user2, tenant1Role2User2.getTenantRoleId()));
        assertTrue(tenantRoleUserServiceAccess.isAssociationAlreadyExistent(user2, tenant1Role3User2.getTenantRoleId()));
    }
}