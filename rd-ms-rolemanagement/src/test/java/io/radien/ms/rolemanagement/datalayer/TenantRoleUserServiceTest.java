/*
 * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
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
package io.radien.ms.rolemanagement.datalayer;

import io.radien.api.SystemVariables;
import io.radien.api.entity.Page;
import io.radien.api.model.tenantrole.SystemTenantRole;
import io.radien.api.model.tenantrole.SystemTenantRoleUser;
import io.radien.api.model.tenantrole.SystemTenantRoleUserSearchFilter;
import io.radien.api.service.tenantrole.TenantRoleServiceAccess;
import io.radien.api.service.tenantrole.TenantRoleUserServiceAccess;
import io.radien.exception.InvalidArgumentException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.rolemanagement.client.entities.TenantRoleUserSearchFilter;
import io.radien.ms.rolemanagement.entities.TenantRoleEntity;
import io.radien.ms.rolemanagement.entities.TenantRoleUserEntity;
import org.junit.*;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.NamingException;

import static org.junit.Assert.*;


/**
 * Tenant Role User Service rest requests and responses with access into the db
 * {@link TenantRoleServiceTest}
 * @author Newton Carvalho
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TenantRoleUserServiceTest {

    static Properties p;
    static TenantRoleServiceAccess tenantRoleServiceAccess;
    static TenantRoleUserServiceAccess tenantRoleUserServiceAccess;

    static Long baseUserId = 111L;

    static Long baseTenantId = 99900000L;
    static Long baseRoleId = 99900001L;
    static Long baseTenantRoleId;
    static EJBContainer container;

    /**
     * Method before test preparation
     */
    @BeforeClass
    static public void start() {
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
    @Before
    public void inject() throws NamingException {
        String lookupString = "java:global/rd-ms-rolemanagement//TenantRoleUserService";
        tenantRoleUserServiceAccess = (TenantRoleUserServiceAccess) container.getContext().lookup(lookupString);
        lookupString = "java:global/rd-ms-rolemanagement//TenantRoleService";
        tenantRoleServiceAccess = (TenantRoleServiceAccess) container.getContext().lookup(lookupString);
    }

    /**
     * Method to stop the container after the testing classes have perform
     */
    @AfterClass
    public static void stop() {
        if (container != null) {
            container.close();
        }
    }

    /**
     * Add tenant role permission test.
     * Will create the new tenant role permission.
     * Expected result: Success.
     * Tested methods: public void save(TenantRoleUser tenantRoleUser)     *
     * @throws UniquenessConstraintException in case of requested action is not well constructed
     */
    @Test
    public void test001Create() throws UniquenessConstraintException, InvalidArgumentException {

        SystemTenantRole systemTenantRole = new TenantRoleEntity();
        systemTenantRole.setRoleId(baseRoleId);
        systemTenantRole.setTenantId(baseTenantId);
        tenantRoleServiceAccess.create(systemTenantRole);
        baseTenantRoleId = systemTenantRole.getId();

        SystemTenantRoleUser systemTenantRoleUser = new TenantRoleUserEntity();
        systemTenantRoleUser.setUserId(baseUserId);
        systemTenantRoleUser.setTenantRoleId(baseTenantRoleId);

        tenantRoleUserServiceAccess.create(systemTenantRoleUser);
        assertNotNull(systemTenantRoleUser.getId());
    }

    /**
     * Try to Add a tenant role permission with repeated information
     * Will not create the new tenant role.
     * Expected result: Fail. UniquenessConstraintException
     * Tested methods: public void save(TenantRoleUser tenantRoleUser)     *
     */
    @Test
    public void test002CreateDuplicatedWithError() {
        SystemTenantRoleUser systemTenantRoleUser = new TenantRoleUserEntity();
        systemTenantRoleUser.setUserId(baseUserId);
        systemTenantRoleUser.setTenantRoleId(baseTenantRoleId);
        assertThrows(UniquenessConstraintException.class, () ->
                tenantRoleUserServiceAccess.create(systemTenantRoleUser));
    }

    /**
     * Test retrieving a TenantRoleUser by Id
     * Tested methods: SystemTenantRoleUser get(Long id)
     */
    @Test
    public void test003GetById() {

        SystemTenantRoleUser systemTenantRoleUser = new TenantRoleUserEntity();
        systemTenantRoleUser.setTenantRoleId(1L);
        systemTenantRoleUser.setUserId(1L);

        try{
            tenantRoleUserServiceAccess.create(systemTenantRoleUser);
        }
        catch(Exception e){
            fail();
        }

        SystemTenantRoleUser retrieved = tenantRoleUserServiceAccess.get(systemTenantRoleUser.getId());
        assertNotNull(retrieved);
    }

    /**
     * Test retrieving a TenantRoleUser by an Id that does not exist
     * Expected: Success
     * Tested methods: SystemTenantRoleUser get(Long id)
     */
    @Test
    public void test004GetByIdNotExistent() {
        SystemTenantRoleUser systemTenantRoleUser = tenantRoleUserServiceAccess.get(999999L);
        assertNull(systemTenantRoleUser);
    }

    /**
     * Test checking if an association (Tenant + role + user) exists
     * Expected: SUCCESS
     */
    @Test
    public void test005AssociationExists() throws InvalidArgumentException {
        assertTrue(tenantRoleUserServiceAccess.isAssociationAlreadyExistent(baseUserId, baseTenantRoleId));
    }

    /**
     * Test checking if an association (Tenant + role + user) exists
     * Expected: FAIL
     */
    @Test
    public void test006AssociationNotExists() throws InvalidArgumentException {
        assertFalse(tenantRoleUserServiceAccess.isAssociationAlreadyExistent(baseUserId, 88L));
        assertFalse(tenantRoleUserServiceAccess.isAssociationAlreadyExistent(9L, 88L));
    }

    /**
     * Test checking if an association (Tenant + role + User) exists,
     * but omitting mandatory param Tenant Id
     * Expected: FAIL
     */
    @Test
    public void test007AssociationWithoutInformingTenantRole() {
        assertThrows(InvalidArgumentException.class, ()->tenantRoleUserServiceAccess.isAssociationAlreadyExistent(baseUserId, null));
    }

    /**
     * Test checking if an association (Tenant + role + user) exists,
     * but omitting mandatory param Role Id
     * Expected: FAIL
     */
    @Test
    public void test008AssociationWithoutInformingPermission() {
        assertThrows(InvalidArgumentException.class, ()->tenantRoleUserServiceAccess.isAssociationAlreadyExistent(null, baseTenantRoleId));
    }

    /**
     * Try to delete an existent TenantRoleUser
     * Expected: SUCCESS
     */
    @Test
    public void test009DeleteTenantRoleUser() {
        // Insert first
        SystemTenantRoleUser tenantRoleUser = new TenantRoleUserEntity();
        tenantRoleUser.setUserId(69L);
        tenantRoleUser.setTenantRoleId(70L);
        try {
            tenantRoleUserServiceAccess.create(tenantRoleUser);
        }
        catch(Exception e){
            fail();
        }

        Long id = tenantRoleUser.getId();

        // Try to delete
        try {
            assertTrue(tenantRoleUserServiceAccess.delete(id));
        }
        catch(Exception e){
            fail();
        }

        // Try to retrieve to confirm
        SystemTenantRoleUser str = tenantRoleUserServiceAccess.get(id);
        assertNull(str);
    }

    /**
     * Retrieve TenantRoleUser association under filter approach.
     * For this case, applies (as parameters) the tenantRole id and the Permission id already used to
     * create the first TenantRoleUser association
     * Expected: SUCCESS
     */
    @Test
    public void test011RetrieveTheFirstAssociationUsingFilter() {
        SystemTenantRoleUserSearchFilter filter = new TenantRoleUserSearchFilter(baseTenantRoleId, baseUserId,
                true, true);
        List<? extends SystemTenantRoleUser> list = tenantRoleUserServiceAccess.get(filter);
        assertNotNull(list);
        assertEquals(1, list.size());
    }

    /**
     * Testing retrieval setting isLogicalConjunction to false (Performing OR instead AND)
     * Expected: Success
     */
    @Test
    public void test012RetrieveSettingLogicConjunctionToFalse() {
        SystemTenantRoleUser tenantRoleUser = new TenantRoleUserEntity();
        tenantRoleUser.setUserId(404L);
        tenantRoleUser.setTenantRoleId(405L);

        try {
            tenantRoleUserServiceAccess.create(tenantRoleUser);
        }
        catch(Exception e){
            fail();
        }

        SystemTenantRoleUser tenantRoleUser2 = new TenantRoleUserEntity();
        tenantRoleUser2.setUserId(406L);
        tenantRoleUser2.setTenantRoleId(407L);

        try {
            tenantRoleUserServiceAccess.create(tenantRoleUser2);
        }
        catch(Exception e){
            fail();
        }

        // Using OR
        SystemTenantRoleUserSearchFilter filter = new TenantRoleUserSearchFilter(405L, 406L,
                true, false);
        List<? extends SystemTenantRoleUser> list = tenantRoleUserServiceAccess.get(filter);
        assertNotNull(list);
        assertEquals(2, list.size());
    }

    /**
     * Filtering by parameters that does not exists
     * Expected: Fail (Empty collection)
     */
    @Test
    public void test013FilterUsingInvalidParameters() {
        SystemTenantRoleUserSearchFilter filter = new TenantRoleUserSearchFilter(1234L, 4321L,
                true, true);
        List<? extends SystemTenantRoleUser> list = tenantRoleUserServiceAccess.get(filter);
        assertNotNull(list);
        assertTrue(list.isEmpty());

        filter = new TenantRoleUserSearchFilter(1234L, 4321L,
                true, false);
        list = tenantRoleUserServiceAccess.get(filter);
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    /**
     * Test(s) for method {@link TenantRoleUserService#getAll(Long, Long, int, int, List, boolean)}
     * @throws UniquenessConstraintException to be thrown in cases of repeated values
     */
    @Test
    public void test014Pagination() throws UniquenessConstraintException, InvalidArgumentException {
        Long tenantRoleId = 9000000L;
        Long userId = 9000001L;
        Long userId2 = 9000002L;

        SystemTenantRoleUser trp = new TenantRoleUserEntity();
        trp.setTenantRoleId(tenantRoleId);
        trp.setUserId(userId);
        tenantRoleUserServiceAccess.create(trp);

        SystemTenantRoleUser trp2 = new TenantRoleUserEntity();
        trp2.setTenantRoleId(tenantRoleId);
        trp2.setUserId(userId2);
        tenantRoleUserServiceAccess.create(trp2);

        List<String> sortBy = new ArrayList<>();
        sortBy.add("tenantRoleId");
        sortBy.add("userId");

        Page<SystemTenantRoleUser> page = tenantRoleUserServiceAccess.getAll(tenantRoleId, userId, 1, 10,
                sortBy, true);
        assertEquals(1, page.getTotalResults());

        page = tenantRoleUserServiceAccess.getAll(tenantRoleId, userId, 1, 10,
                null, true);
        assertEquals(1, page.getTotalResults());

        page = tenantRoleUserServiceAccess.getAll(tenantRoleId, userId, 1, 10,
                new ArrayList<>(), true);
        assertEquals(1, page.getTotalResults());

        page = tenantRoleUserServiceAccess.getAll(tenantRoleId, null, 1, 10,
                new ArrayList<>(), true);
        assertEquals(2, page.getTotalResults());

        page = tenantRoleUserServiceAccess.getAll(null, userId, 1, 10,
                new ArrayList<>(), true);
        assertEquals(1, page.getTotalResults());

        page = tenantRoleUserServiceAccess.getAll(null, null, 1, 10,
                Collections.singletonList(SystemVariables.TENANT_ROLE_ID.getFieldName()), true);
        assertTrue(page.getTotalResults() >= 2);

        page = tenantRoleUserServiceAccess.getAll(999999999L, 99999L, 1, 10,
                Collections.singletonList(SystemVariables.TENANT_ROLE_ID.getFieldName()), false);
        assertEquals(0,page.getTotalResults());
    }

    /**
     * Retrieves all TenantRoleUsers (inserted during this test) under a pagination approach,
     * but taking in account an specific tenantRole identifier
     * Expected: SUCCESS (A page not empty)
     */
    @Test
    public void test015PaginationWithTenantRoleSpecified() {
        Page<SystemTenantRoleUser> p = tenantRoleUserServiceAccess.getAll(baseTenantRoleId, baseUserId,
                1, 100, Arrays.asList("tenantRoleId", "userId"), true);
        assertNotNull(p);
        assertTrue(p.getTotalResults() > 0);
        assertEquals(1, p.getTotalPages());
        assertNotNull(p.getResults());
        assertFalse(p.getResults().isEmpty());

        Page<Long> p2 = tenantRoleUserServiceAccess.getAllUserIds(baseTenantId, baseRoleId,
                1, 100);
        assertTrue(p2.getTotalResults() > 0);
    }

    /**
     * Try to retrieve TenantRoleUsers (under a pagination approach,
     * but taking in account a tenantRole that does not exist
     * Expected: An empty page
     */
    @Test
    public void test016PaginationForNotExistentTenantRole() {
        Page<SystemTenantRoleUser> p = tenantRoleUserServiceAccess.getAll(10000L,10001L,
                1, 100, Arrays.asList("tenantRoleId", "userId"), false);
        assertNotNull(p);
        assertEquals(0,p.getTotalResults());
        assertEquals(0,p.getTotalPages());
        assertNotNull(p.getResults());
        assertTrue(p.getResults().isEmpty());

        Page<Long> p2 = tenantRoleUserServiceAccess.getAllUserIds(10000L,10001L,
                1, 100);
        assertNotNull(p);
        assertEquals(0,p2.getTotalResults());
        assertEquals(0,p2.getTotalPages());
        assertNotNull(p2.getResults());
        assertTrue(p2.getResults().isEmpty());
    }

    /**
     * Test for method getTenantRoleUserId(Long tenantRoleId, Long userId)
     */
    @Test
    public void test017GetTenantRoleUserId() throws InvalidArgumentException {
        SystemTenantRoleUser sru = new TenantRoleUserEntity();
        sru.setTenantRoleId(101010L);
        sru.setUserId(101L);

        try{
            tenantRoleUserServiceAccess.create(sru);
        }
        catch(Exception e){
            fail();
        }

        Long expectedId = sru.getId();
        assertNotNull(expectedId);

        Optional<Long> id = tenantRoleUserServiceAccess.getTenantRoleUserId(101010L, 101L);
        assertTrue(id.isPresent());
        assertEquals(expectedId, id.get());

        id = tenantRoleUserServiceAccess.getTenantRoleUserId(101010L, 202L);
        assertFalse(id.isPresent());

        try{
            tenantRoleUserServiceAccess.getTenantRoleUserId(null, 202L);
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Mandatory parameter missing"));
        }
    }

    /**
     * Test for method getTenantRoleUserId(Long tenantRoleId, Long userId)
     */
    @Test
    public void test016GetTenantRoleUserIdNullUser() throws InvalidArgumentException {
        SystemTenantRoleUser sru = new TenantRoleUserEntity();
        sru.setTenantRoleId(301010L);
        sru.setUserId(601L);

        try{
            tenantRoleUserServiceAccess.create(sru);
        }
        catch(Exception e){
            fail();
        }

        Long expectedId = sru.getId();
        assertNotNull(expectedId);

        Optional<Long> id = tenantRoleUserServiceAccess.getTenantRoleUserId(301010L, 601L);
        assertTrue(id.isPresent());
        assertEquals(expectedId, id.get());

        id = tenantRoleUserServiceAccess.getTenantRoleUserId(301010L, 602L);
        assertFalse(id.isPresent());

        try{
            tenantRoleUserServiceAccess.getTenantRoleUserId(301010L, null);
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Mandatory parameter missing"));
        }
    }

    /**
     * Utility method to create TenantRole instances and reduce cognitive complexity
     * @param tenant tenant id
     * @param role role id
     * @throws UniquenessConstraintException thrown in case of repeated value (in therms of ids combination)
     * @return SystemTenantRole instance for the given parameters
     */
    protected SystemTenantRole createTenantRole(Long tenant, Long role) throws UniquenessConstraintException, InvalidArgumentException {
        SystemTenantRole tenantRole = new TenantRoleEntity();
        tenantRole.setTenantId(tenant);
        tenantRole.setRoleId(role);
        tenantRoleServiceAccess.create(tenantRole);
        return tenantRole;
    }

    /**
     * Utility method to create TenantRoleUser instances and reduce cognitive complexity
     * @param tr System Tenant Role instance
     * @param user user ide
     * @throws UniquenessConstraintException thrown in case of repeated value (in therms of ids combination)
     * @return SystemTenantRoleUser instance for the given parameters
     */
    protected SystemTenantRoleUser createTenantRoleUser(SystemTenantRole tr, Long user) throws UniquenessConstraintException, InvalidArgumentException {
        SystemTenantRoleUser tenantRoleUser = new TenantRoleUserEntity();
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
    public void test017DeleteByTenantAndUserInformation() throws UniquenessConstraintException, InvalidArgumentException {
        Long role1 = 700001L, role2 = 700002L, role3 = 700003L;
        Long tenant1 = 500001L;
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
        Collection<Long> ids = tenantRoleUserServiceAccess.getTenantRoleUserIds(tenant1, null, user1);
        assertTrue(tenantRoleUserServiceAccess.delete(ids));
        ids = tenantRoleUserServiceAccess.getTenantRoleUserIds(tenant1, Collections.singletonList(role1), user2);
        assertTrue(tenantRoleUserServiceAccess.delete(ids));

        // Check if association cannot be found for user1
        assertFalse(tenantRoleUserServiceAccess.isAssociationAlreadyExistent(user1, tenant1Role1User1.getTenantRoleId()));
        assertFalse(tenantRoleUserServiceAccess.isAssociationAlreadyExistent(user1, tenant1Role2User1.getTenantRoleId()));
        assertFalse(tenantRoleUserServiceAccess.isAssociationAlreadyExistent(user1, tenant1Role3User1.getTenantRoleId()));
        assertFalse(tenantRoleUserServiceAccess.isAssociatedWithTenant(user1, tenant1));


        // Check if association cannot be found for user2
        assertFalse(tenantRoleUserServiceAccess.isAssociationAlreadyExistent(user2, tenant1Role1User2.getTenantRoleId()));
        assertTrue(tenantRoleUserServiceAccess.isAssociationAlreadyExistent(user2, tenant1Role2User2.getTenantRoleId()));
        assertTrue(tenantRoleUserServiceAccess.isAssociationAlreadyExistent(user2, tenant1Role3User2.getTenantRoleId()));
        assertTrue(tenantRoleUserServiceAccess.isAssociatedWithTenant(user2, tenant1));
    }

    /**
     * Test method getTenantRoleUserIds
     * asserts TenantRoleUserIds
     */
    @Test
    public void testGetTenantRoleUserIds() {
        SystemTenantRoleUser sru = new TenantRoleUserEntity();
        sru.setTenantRoleId(117L);
        sru.setUserId(118L);
        try{
            tenantRoleUserServiceAccess.create(sru);
        } catch (Exception e) {
            fail();
        }

        Long expectedId = sru.getId();
        assertNotNull(expectedId);

        ArrayList<Long> tenantRoleIds = new ArrayList<>();
        tenantRoleIds.add(117L);

        List<Long> ids = (List<Long>) tenantRoleUserServiceAccess.getTenantRoleUserIds(tenantRoleIds, 118L);
        assertFalse(ids.isEmpty());
        assertEquals(expectedId, ids.get(0));

        ids = (List<Long>) tenantRoleUserServiceAccess.getTenantRoleUserIds(tenantRoleIds, 119L);
        assertTrue(ids.isEmpty());
    }

    /**
     * Test for method {@link TenantRoleUserService#isAssociatedWithTenant(Long, Long)}
     * inferring the behaviour when the expected parameters are not informed (tenant id is null)
     */
    @Test
    public void testIsAssociatedWithTenantWhenTenantIdIsNull() {
        Long userId = 1L;
        assertThrows(InvalidArgumentException.class, () -> tenantRoleUserServiceAccess.isAssociatedWithTenant(userId, null));
    }

    /**
     * Test for method {@link TenantRoleUserService#isAssociatedWithTenant(Long, Long)}
     * inferring the behaviour when the expected parameters are not informed (user id is null)
     */
    @Test
    public void testIsAssociatedWithTenantWhenUserIdIsNull() {
        Long tenantId = 1L;
        assertThrows(InvalidArgumentException.class, () -> tenantRoleUserServiceAccess.isAssociatedWithTenant(null, tenantId));
    }

    /**
     * Test for method {@link io.radien.api.service.tenantrole.TenantRoleUserServiceAccess#update(SystemTenantRoleUser)}
     * @throws UniquenessConstraintException to be thrown in cases of repeated values
     */
    @Test
    public void testUpdate() throws UniquenessConstraintException, InvalidArgumentException {
        Long tenantRoleId = 1111111111L;
        Long userId = 1111111111L;

        SystemTenantRoleUser trp1 = new TenantRoleUserEntity();
        trp1.setTenantRoleId(tenantRoleId);
        trp1.setUserId(userId);
        tenantRoleUserServiceAccess.create(trp1);

        Long userId2 = 111111111112L;
        SystemTenantRoleUser trp2 = new TenantRoleUserEntity();
        trp2 = new TenantRoleUserEntity();
        trp2.setTenantRoleId(tenantRoleId);
        trp2.setUserId(userId2);
        tenantRoleUserServiceAccess.create(trp2);

        SystemTenantRoleUser trp3 = new TenantRoleUserEntity();
        trp3.setId(trp2.getId());
        trp3.setTenantRoleId(tenantRoleId);
        trp3.setUserId(userId);
        assertThrows(UniquenessConstraintException.class, ()->
                tenantRoleUserServiceAccess.update(trp3));

        trp3.setUserId(122378900L);

        try{
            tenantRoleUserServiceAccess.update(trp3);
        } catch (Exception e) {
            fail();
        }

        SystemTenantRoleUser trp4 = new TenantRoleUserEntity();
        trp4.setId(1111111111L);
        trp4.setTenantRoleId(tenantRoleId);
        trp4.setUserId(userId);
        assertThrows(UniquenessConstraintException.class, ()->
                tenantRoleUserServiceAccess.update(trp4));


    }
}