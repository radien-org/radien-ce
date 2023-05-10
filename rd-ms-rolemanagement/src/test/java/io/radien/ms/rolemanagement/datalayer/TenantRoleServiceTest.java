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
import io.radien.api.model.role.SystemRole;
import io.radien.api.model.tenantrole.SystemTenantRole;
import io.radien.api.model.tenantrole.SystemTenantRolePermission;
import io.radien.api.model.tenantrole.SystemTenantRoleSearchFilter;
import io.radien.api.model.tenantrole.SystemTenantRoleUser;
import io.radien.api.service.role.RoleServiceAccess;
import io.radien.api.service.tenantrole.TenantRolePermissionServiceAccess;
import io.radien.api.service.tenantrole.TenantRoleServiceAccess;
import io.radien.api.service.tenantrole.TenantRoleUserServiceAccess;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.InvalidArgumentException;
import io.radien.api.service.role.exception.TenantRoleNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.rolemanagement.client.entities.TenantRoleSearchFilter;
import io.radien.ms.rolemanagement.entities.RoleEntity;
import io.radien.ms.rolemanagement.entities.TenantRoleEntity;
import io.radien.ms.rolemanagement.entities.TenantRolePermissionEntity;
import io.radien.ms.rolemanagement.entities.TenantRoleUserEntity;
import org.junit.*;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;

import static org.junit.Assert.*;

/**
 * Tenant Role Service test
 * {@link TenantRoleService}
 *
 * @author Newton Carvalho
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TenantRoleServiceTest {

    static Properties p;
    static TenantRoleServiceAccess tenantRoleServiceAccess;
    static TenantRolePermissionServiceAccess tenantRolePermissionServiceAccess;
    static TenantRoleUserServiceAccess tenantRoleUserServiceAccess;
    static RoleServiceAccess roleServiceAccess;

    static EJBContainer container;
    Long baseRoleId = 111L;
    Long baseTenantId = 222L;

    /**
     * Method before test preparation
     * @throws Exception in case of injection naming exception
     */
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
        p.put("openejb.cdi.activated-on-ejb", "false");
        container = EJBContainer.createEJBContainer(p);
        final Context context = container.getContext();

        String lookupString = "java:global/rd-ms-rolemanagement//TenantRolePermissionService";
        tenantRolePermissionServiceAccess = (TenantRolePermissionServiceAccess) context.lookup(lookupString);

        lookupString = "java:global/rd-ms-rolemanagement//TenantRoleUserService";
        tenantRoleUserServiceAccess = (TenantRoleUserServiceAccess) context.lookup(lookupString);

        lookupString = "java:global/rd-ms-rolemanagement//RoleService";
        roleServiceAccess = (RoleServiceAccess) context.lookup(lookupString);
    }

    /**
     * Injection method
     * @throws NamingException in case of injection naming exception
     */
    @Before
    public void inject() throws NamingException {
        String lookupString = "java:global/rd-ms-rolemanagement//TenantRoleService";
        tenantRoleServiceAccess = (TenantRoleServiceAccess) container.getContext().lookup(lookupString);
        container.getContext().bind("inject", this);
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
     * Add tenant role test.
     * Will create the new tenant role.
     * Expected result: Success.
     * Tested methods: void create(TenantRole tenantRole)     *
     * @throws UniquenessConstraintException in case of requested action is not well constructed
     */
    @Test
    public void test001create() throws UniquenessConstraintException, InvalidArgumentException {

        SystemTenantRole systemTenantRole = new TenantRoleEntity();
        systemTenantRole.setRoleId(baseRoleId);
        systemTenantRole.setTenantId(baseTenantId);

        tenantRoleServiceAccess.create(systemTenantRole);
        assertNotNull(systemTenantRole.getId());
    }

    /**
     * Try to Add a tenant role with repeated information
     * Will not create the new tenant role.
     * Expected result: Fail. UniquenessConstraintException
     * Tested methods: void create(TenantRole tenantRole)     *
     */
    @Test
    public void test002createDuplicatedWithError() {
        SystemTenantRole systemTenantRole = new TenantRoleEntity();
        systemTenantRole.setRoleId(baseRoleId);
        systemTenantRole.setTenantId(baseTenantId);
        assertThrows(UniquenessConstraintException.class, () ->
                tenantRoleServiceAccess.create(systemTenantRole));
    }

    /**
     * Try to create a Tenant Role and then update it
     * Expected result: Success.
     * Tested methods: void create(TenantRole tenantRole)
     */
    @Test
    public void test003Update() throws UniquenessConstraintException, TenantRoleNotFoundException, InvalidArgumentException {
        Long originalRoleId = 22L, originalTenantId = 23L;

        SystemTenantRole systemTenantRole = new TenantRoleEntity();
        systemTenantRole.setRoleId(originalRoleId);
        systemTenantRole.setTenantId(originalTenantId);
        tenantRoleServiceAccess.create(systemTenantRole);

        Long generatedId = systemTenantRole.getId();
        assertNotNull(generatedId);

        SystemTenantRole retrieved = tenantRoleServiceAccess.get(generatedId);
        assertNotNull(retrieved);
        assertEquals(retrieved.getRoleId(), originalRoleId);
        assertEquals(retrieved.getTenantId(), originalTenantId);

        Long newRoleId = 44L, newTenantId = 45L;
        SystemTenantRole systemTenantRole1 = new TenantRoleEntity();
        systemTenantRole1.setId(generatedId);
        systemTenantRole1.setRoleId(newRoleId);
        systemTenantRole1.setTenantId(newTenantId);
        tenantRoleServiceAccess.update(systemTenantRole1);

        retrieved = tenantRoleServiceAccess.get(generatedId);
        assertNotNull(retrieved.getId());
        assertEquals(retrieved.getRoleId(), newRoleId);
        assertEquals(retrieved.getTenantId(), newTenantId);
    }

    /**
     * Test updating a TenantRole with repeated information (The set Tenant + Role is unique)
     * Expected: Fail (Raising UniquenessConstraintException)
     * Tested methods: void create(TenantRole tenantRole)
     */
    @Test
    public void test004UpdateWithError() {
        SystemTenantRole tenantRole = new TenantRoleEntity();
        tenantRole.setTenantId(101L);
        tenantRole.setRoleId(102L);
        try{
            tenantRoleServiceAccess.create(tenantRole);
        }catch(Exception e){
            fail();
        }

        SystemTenantRole updatableTenantRole = new TenantRoleEntity();
        updatableTenantRole.setId(tenantRole.getId());
        updatableTenantRole.setRoleId(baseRoleId);
        updatableTenantRole.setTenantId(baseTenantId);

        assertThrows(UniquenessConstraintException.class,() -> tenantRoleServiceAccess.create(updatableTenantRole));
    }

    /**
     * Test retrieving a TenantRole by Id
     * Tested methods: SystemTenantRole get(Long id)
     */
    @Test
    public void test005GetById() {

        SystemTenantRole systemTenantRole = new TenantRoleEntity();
        systemTenantRole.setTenantId(1L);
        systemTenantRole.setRoleId(1L);
        try{
            tenantRoleServiceAccess.create(systemTenantRole);
        }catch(Exception e){
            fail();
        }

        SystemTenantRole retrieved = tenantRoleServiceAccess.get(systemTenantRole.getId());
        assertNotNull(retrieved);
    }

    /**
     * Test retrieving a TenantRole by an Id that does not exist
     * Expected: Success
     * Tested methods: SystemTenantRole get(Long id)
     */
    @Test
    public void test006GetByIdNotExistent() {
        SystemTenantRole systemTenantRole = tenantRoleServiceAccess.get(999999L);
        assertNull(systemTenantRole);
    }

    /**
     * Test checking if an association (Tenant + role) exists
     * Expected: SUCCESS
     */
    @Test
    public void test007AssociationExists() throws InvalidArgumentException {
        assertTrue(tenantRoleServiceAccess.isAssociationAlreadyExistent(baseRoleId, baseTenantId));
    }

    /**
     * Test checking if an association (Tenant + role) exists
     * Expected: FAIL
     */
    @Test
    public void test008AssociationNotExists() throws InvalidArgumentException {
        assertFalse(tenantRoleServiceAccess.isAssociationAlreadyExistent(baseRoleId, 88L));
        assertFalse(tenantRoleServiceAccess.isAssociationAlreadyExistent(9L, 88L));
    }

    /**
     * Test checking if an association (Tenant + role) exists,
     * but omitting mandatory param Tenant Id
     * Expected: FAIL
     */
    @Test
    public void test009AssociationWithoutInformingTenant() {
        boolean success = false;
        try {
            tenantRoleServiceAccess.isAssociationAlreadyExistent(baseRoleId, null);
        } catch (InvalidArgumentException e) {
            success = true;
        }
        assertTrue(success);
    }

    /**
     * Test checking if an association (Tenant + role) exists,
     * but omitting mandatory param Role Id
     * Expected: FAIL
     */
    @Test
    public void test010AssociationWithoutInformingRole() {
        boolean success = false;
        try {
            tenantRoleServiceAccess.isAssociationAlreadyExistent(null, baseTenantId);
        } catch (InvalidArgumentException e) {
            success = true;
        }
        assertTrue(success);
    }

    /**
     * Try to delete an existent TenantRole
     * Expected: SUCCESS
     */
    @Test
    public void test011DeleteTenantRole() {
        // Insert first
        SystemTenantRole tenantRole = new TenantRoleEntity();
        tenantRole.setRoleId(69L);
        tenantRole.setTenantId(70L);

        try {
            tenantRoleServiceAccess.create(tenantRole);
        } catch (Exception e) {
            fail();
        }

        Long id = tenantRole.getId();

        // Try to delete
        try {
            assertTrue(tenantRoleServiceAccess.delete(id));
        } catch (Exception e) {
            fail();
        }

        // Try to retrieve to confirm
        SystemTenantRole str = tenantRoleServiceAccess.get(id);
        assertNull(str);
    }

    /**
     * Try to delete without informing Id
     * Expected: FAIL (Raise Exception)
     */
    @Test
    public void test012DeleteWithoutInformingId() throws InvalidArgumentException {
        assertEquals(false, tenantRoleServiceAccess.delete(null));
    }

    /**
     * Try to delete an TenantRole that has an User (TenantRoleUser) associated
     */
    @Test
    public void test013DeleteWithUserAssociated() {
        // Step 1: Create a Tenant Role
        SystemTenantRole tenantRole = new TenantRoleEntity();
        tenantRole.setTenantId(120L);
        tenantRole.setRoleId(121L);

        try {
            tenantRoleServiceAccess.create(tenantRole);
        } catch (Exception e) {
            fail();
        }

        // Step 2: Create a Tenant Role User
        SystemTenantRoleUser tenantRoleUser = new TenantRoleUserEntity();
        tenantRoleUser.setTenantRoleId(tenantRole.getId());
        tenantRoleUser.setUserId(11111L);
        try {
            tenantRoleUserServiceAccess.create(tenantRoleUser);
        } catch (Exception e) {
            fail();
        }

        // Step 3: Try to delete the Tenant Role
        assertThrows(InvalidArgumentException.class, () -> tenantRoleServiceAccess.delete(tenantRole.getId()));
    }

    /**
     * Try to delete an TenantRole that has a Permission (TenantRolePermission) associated
     * Expected: FAIL (Raising TenantRoleException)
     */
    @Test
    public void test014DeleteWithPermissionAssociated() {
        // Step 1: Create a Tenant Role
        SystemTenantRole tenantRole = new TenantRoleEntity();
        tenantRole.setTenantId(120L);
        tenantRole.setRoleId(122L);
        try {
            tenantRoleServiceAccess.create(tenantRole);
        } catch (Exception e) {
            fail();
        }

        // Step 2: Create a Tenant Role Permission
        SystemTenantRolePermission tenantRolePermission = new TenantRolePermissionEntity();
        tenantRolePermission.setTenantRoleId(tenantRole.getId());
        tenantRolePermission.setPermissionId(11111L);
        try {
            tenantRolePermissionServiceAccess.create(tenantRolePermission);       }
        catch (Exception e) {
            fail();
        }

        // Step 3: Try to delete the Tenant Role
        assertThrows(InvalidArgumentException.class, () -> tenantRoleServiceAccess.delete(tenantRole.getId()));
    }

    /**
     * Retrieves all TenantRoles (inserted during this test) under a pagination approach
     * Expected: SUCCESS (A page not empty)
     */
    @Test
    public void test015Pagination() {
        SystemTenantRole tenantRole = new TenantRoleEntity();
        tenantRole.setTenantId(123L);
        tenantRole.setRoleId(124L);
        try {
            tenantRoleServiceAccess.create(tenantRole);
        }
        catch (Exception e) {
            fail();
        }

        SystemTenantRole tenantRole2 = new TenantRoleEntity();
        tenantRole2.setTenantId(123L);
        tenantRole2.setRoleId(125L);
        try {
            tenantRoleServiceAccess.create(tenantRole2);
        }
        catch (Exception e) {
            fail();
        }

        List<String> sortBy = Arrays.asList("tenantId", "roleId");
        Page<SystemTenantRole> p = tenantRoleServiceAccess.getAll(123L, 124L,
                1, 100, sortBy, false);
        assertEquals(1, p.getTotalResults());

        p = tenantRoleServiceAccess.getAll(null, 125L,
                1, 100, sortBy, true);
        assertEquals(1, p.getTotalResults());

        p = tenantRoleServiceAccess.getAll(123L, null,
                1, 100, null, true);
        assertEquals(2, p.getTotalResults());

        sortBy = new ArrayList<>();
        p = tenantRoleServiceAccess.getAll(123L, 125L,
                1, 100, sortBy, true);
        assertEquals(1, p.getTotalResults());

        sortBy = new ArrayList<>();
        p = tenantRoleServiceAccess.getAll(124L, null,
                1, 100, sortBy, true);
        assertEquals(0, p.getTotalResults());

        sortBy = new ArrayList<>();
        p = tenantRoleServiceAccess.getAll(null, 125L,
                1, 100, sortBy, true);
        assertEquals(1, p.getTotalResults());

        p = tenantRoleServiceAccess.getAll(null, null,
                1, 100, sortBy, true);
        assertTrue(p.getTotalResults() >= 2);
    }

    /**
     * Retrieve TenantRole association under filter approach.
     * For this case, applies (as parameters) the tenant id and the role id already used to
     * create the first TenantRole association
     * Expected: SUCCESS
     */
    @Test
    public void test016RetrieveTheFirstAssociationUsingFilter() {
        SystemTenantRoleSearchFilter filter = new TenantRoleSearchFilter(baseTenantId, baseRoleId,
                true, true);
        List<? extends SystemTenantRole> list = tenantRoleServiceAccess.get(filter);
        assertNotNull(list);
        assertEquals(1, list.size());
    }

    /**
     * Testing retrieval setting isLogicalConjunction to false (Performing OR instead AND)
     * Expected: Success
     */
    @Test
    public void test017RetrieveSettingLogicConjunctionToFalse() {
        SystemTenantRole tenantRole = new TenantRoleEntity();
        tenantRole.setRoleId(404L);
        tenantRole.setTenantId(405L);
        try {
            tenantRoleServiceAccess.create(tenantRole);
        }
        catch (Exception e) {
            fail();
        }

        SystemTenantRole tenantRole2 = new TenantRoleEntity();
        tenantRole2.setRoleId(406L);
        tenantRole2.setTenantId(407L);
        try {
            tenantRoleServiceAccess.create(tenantRole2);
        }
        catch (Exception e) {
            fail();
        }

        // Using OR
        SystemTenantRoleSearchFilter filter = new TenantRoleSearchFilter(405L, 406L,
                true, false);
        List<? extends SystemTenantRole> list = tenantRoleServiceAccess.get(filter);
        assertNotNull(list);
        assertEquals(2, list.size());
    }

    /**
     * Filtering by parameters that does not exists
     * Expected: Fail (Empty collection)
     */
    @Test
    public void test018FilterUsingInvalidParameters() {
        SystemTenantRoleSearchFilter filter = new TenantRoleSearchFilter(1234L, 4321L,
                true, true);
        List<? extends SystemTenantRole> list = tenantRoleServiceAccess.get(filter);
        assertNotNull(list);
        assertTrue(list.isEmpty());

        filter = new TenantRoleSearchFilter(1234L, 4321L,
                true, false);
        list = tenantRoleServiceAccess.get(filter);
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    /**
     * Tests method getTenantRoleId(Long tenant, Long role)
     */
    @Test
    public void test019GetTenantRoleId() throws InvalidArgumentException {
        SystemTenantRole str = new TenantRoleEntity();
        str.setTenantId(10000L);
        str.setRoleId(10001L);
        try {
            tenantRoleServiceAccess.create(str);
        }
        catch (Exception e) {
            fail();
        }

        Long expectedId = str.getId();
        assertNotNull(expectedId);

        Optional<Long> id = tenantRoleServiceAccess.getTenantRoleId(10000L, 10001L);
        assertTrue(id.isPresent());
        assertEquals(expectedId, id.get());

        id = tenantRoleServiceAccess.getTenantRoleId(101010L, 202L);
        assertFalse(id.isPresent());
    }

    /**
     * Tests for method hasAnyRole(Long userId, List<String> roles, tenantId)
     */
    @Test
    public void test020HasAnyRole() throws InvalidArgumentException {
        SystemRole roleA = new RoleEntity();
        roleA.setName("role-a");
        try {
            roleServiceAccess.create(roleA);
        }
        catch (Exception e) {
            fail();
        }

        SystemRole roleB = new RoleEntity();
        roleB.setName("role-b");
        try {
            roleServiceAccess.create(roleB);
        }
        catch (Exception e) {
            fail();
        }

        SystemRole roleC = new RoleEntity();
        roleC.setName("role-c");
        try {
            roleServiceAccess.create(roleC);
        }
        catch (Exception e) {
            fail();
        }

        Long tenant1 = 444L;
        Long tenant2 = 445L;

        SystemTenantRole tenant1RoleA = new TenantRoleEntity();
        tenant1RoleA.setTenantId(tenant1); tenant1RoleA.setRoleId(roleA.getId());
        try {
            tenantRoleServiceAccess.create(tenant1RoleA);
        }
        catch (Exception e) {
            fail();
        }

        SystemTenantRole tenant1RoleB = new TenantRoleEntity();
        tenant1RoleB.setTenantId(tenant1); tenant1RoleB.setRoleId(roleB.getId());
        try {
            tenantRoleServiceAccess.create(tenant1RoleB);
        }
        catch (Exception e) {
            fail();
        }

        SystemTenantRole tenant2RoleC = new TenantRoleEntity();
        tenant2RoleC.setTenantId(tenant2); tenant2RoleC.setRoleId(roleC.getId());
        try {
            tenantRoleServiceAccess.create(tenant2RoleC);
        }
        catch (Exception e) {
            fail();
        }

        Long user1 = 100000000L;
        Long user2 = 100000011L;

        SystemTenantRoleUser tenant1RoleAUser1 = new TenantRoleUserEntity();
        tenant1RoleAUser1.setUserId(user1); tenant1RoleAUser1.setTenantRoleId(tenant1RoleA.getId());
        try {
            tenantRoleUserServiceAccess.create(tenant1RoleAUser1);
        }
        catch (Exception e) {
            fail();
        }

        SystemTenantRoleUser tenant1RoleBUser1 = new TenantRoleUserEntity();
        tenant1RoleBUser1.setUserId(user1); tenant1RoleBUser1.setTenantRoleId(tenant1RoleB.getId());
        try {
            tenantRoleUserServiceAccess.create(tenant1RoleBUser1);
        }
        catch (Exception e) {
            fail();
        }

        SystemTenantRoleUser tenant2RoleCUser2 = new TenantRoleUserEntity();
        tenant2RoleCUser2.setUserId(user2); tenant2RoleCUser2.setTenantRoleId(tenant2RoleC.getId());
        try {
            tenantRoleUserServiceAccess.create(tenant2RoleCUser2);
        }
        catch (Exception e) {
            fail();
        }

        // time for the truth
        assertTrue(tenantRoleServiceAccess.
                hasAnyRole(user1, Arrays.asList("role-a", "role-b"), null));

        assertTrue(tenantRoleServiceAccess.
                hasAnyRole(user1, Arrays.asList("role-a", "role-c"), null));

        assertTrue(tenantRoleServiceAccess.
                hasAnyRole(user1, Arrays.asList("role-a", "role-c"), tenant1));

        assertTrue(tenantRoleServiceAccess.
                hasAnyRole(user1, Arrays.asList("role-a", "role-b", "role-c"), tenant1));

        assertFalse(tenantRoleServiceAccess.
                hasAnyRole(user1, Arrays.asList("role-a", "role-c"), tenant2));

        assertFalse(tenantRoleServiceAccess.
                hasAnyRole(user2, Arrays.asList("role-a", "role-b", "role-c"), tenant1));

        assertTrue(tenantRoleServiceAccess.
                hasAnyRole(user2, Arrays.asList("role-a", "role-b", "role-c"), tenant2));

        try {
            tenantRoleServiceAccess.hasAnyRole(null, Arrays.asList("role-a", "role-b"), null);
        } catch(Exception e) {
            assertTrue(e.getMessage().contains("Mandatory parameter missing"));
        }
    }

    /**
     * Tests for method hasPermission(Long userId, Long permissionId, Long tenantId)
     */
    @Test
    public void test021HasPermission() throws InvalidArgumentException {

        // Roles
        SystemRole roleC = new RoleEntity();
        roleC.setName("role-c1");
        try {
            roleServiceAccess.create(roleC);
        } catch(Exception e) {
            fail();
        }

        SystemRole roleD = new RoleEntity();
        roleD.setName("role-d1");
        try {
            roleServiceAccess.create(roleD);
        } catch(Exception e) {
            fail();
        }

        Long tenant1 = 888L;
        Long tenant2 = 889L;

        //Tenant Roles
        SystemTenantRole tenant1RoleC = new TenantRoleEntity();
        tenant1RoleC.setTenantId(tenant1); tenant1RoleC.setRoleId(roleC.getId());
        try {
            tenantRoleServiceAccess.create(tenant1RoleC);
        } catch(Exception e) {
            fail();
        }

        SystemTenantRole tenant2RoleD = new TenantRoleEntity();
        tenant2RoleD.setTenantId(tenant2); tenant2RoleD.setRoleId(roleD.getId());
        try {
            tenantRoleServiceAccess.create(tenant2RoleD);
        } catch(Exception e) {
            fail();
        }

        Long permission1 = 100001L;
        Long permission2 = 100002L;
        Long permission3 = 100003L;

        // Tenant Role Permission
        SystemTenantRolePermission tenant1RoleCPermission1 = new TenantRolePermissionEntity();
        tenant1RoleCPermission1.setTenantRoleId(tenant1RoleC.getId());
        tenant1RoleCPermission1.setPermissionId(permission1);
        try {
            tenantRolePermissionServiceAccess.create(tenant1RoleCPermission1);
        } catch(Exception e) {
            fail();
        }

        SystemTenantRolePermission tenant1RoleCPermission2 = new TenantRolePermissionEntity();
        tenant1RoleCPermission2.setTenantRoleId(tenant1RoleC.getId());
        tenant1RoleCPermission2.setPermissionId(permission2);
        try {
            tenantRolePermissionServiceAccess.create(tenant1RoleCPermission2);
        } catch(Exception e) {
            fail();
        }

        SystemTenantRolePermission tenant2RoleDPermission2 = new TenantRolePermissionEntity();
        tenant2RoleDPermission2.setTenantRoleId(tenant2RoleD.getId());
        tenant2RoleDPermission2.setPermissionId(permission2);
        try {
            tenantRolePermissionServiceAccess.create(tenant2RoleDPermission2);
        } catch(Exception e) {
            fail();
        }

        SystemTenantRolePermission tenant2RoleDPermission3 = new TenantRolePermissionEntity();
        tenant2RoleDPermission3.setTenantRoleId(tenant2RoleD.getId());
        tenant2RoleDPermission3.setPermissionId(permission3);
        try {
            tenantRolePermissionServiceAccess.create(tenant2RoleDPermission3);
        } catch(Exception e) {
            fail();
        }

        /* Tenant Role Users */

        Long user1 = 10002222L;
        Long user2 = 10002223L;

        SystemTenantRoleUser tenant1RoleCUser1 = new TenantRoleUserEntity();
        tenant1RoleCUser1.setUserId(user1); tenant1RoleCUser1.setTenantRoleId(tenant1RoleC.getId());
        try {
            tenantRoleUserServiceAccess.create(tenant1RoleCUser1);
        } catch(Exception e) {
            fail();
        }

        SystemTenantRoleUser tenant2RoleDUser2 = new TenantRoleUserEntity();
        tenant2RoleDUser2.setUserId(user2); tenant2RoleDUser2.setTenantRoleId(tenant2RoleD.getId());
        try {
            tenantRoleUserServiceAccess.create(tenant2RoleDUser2);
        } catch(Exception e) {
            fail();
        }

        // Checking permission
        assertTrue(tenantRoleServiceAccess.hasPermission(user1, permission1, null));
        assertFalse(tenantRoleServiceAccess.hasPermission(user1, permission3, null));
        assertTrue(tenantRoleServiceAccess.hasPermission(user1, permission1, tenant1));
        assertFalse(tenantRoleServiceAccess.hasPermission(user1, permission1, tenant2));
        assertTrue(tenantRoleServiceAccess.hasPermission(user1, permission2, null));
        assertFalse(tenantRoleServiceAccess.hasPermission(user1, permission2, tenant2));
    }

    /**
     * Tests for method getTenantRoleId(Long tenant, Long role)
     * without informing the mandatory field Role
     */
    @Test
    public void test027GetTenantRoleIdWithoutMandatoryFields() {
        assertThrows(InvalidArgumentException.class, () -> tenantRoleServiceAccess.getTenantRoleId(null, null));
    }

    /**
     * Tests for method hasAnyRole(Long userId, List<String> roles, tenantId)
     */
    @Test
    public void test028HasAnyRoleNullArrayList() {
        SystemRole roleA = new RoleEntity();
        roleA.setName("role-xx");
        try {
            roleServiceAccess.create(roleA);
        } catch(Exception e) {
            fail();
        }

        Long tenant1 = 544L;

        SystemTenantRole tenant1RoleA = new TenantRoleEntity();
        tenant1RoleA.setTenantId(tenant1); tenant1RoleA.setRoleId(roleA.getId());
        try {
            tenantRoleServiceAccess.create(tenant1RoleA);
        } catch(Exception e) {
            fail();
        }

        Long user1 = 105000000L;

        SystemTenantRoleUser tenant1RoleAUser1 = new TenantRoleUserEntity();
        tenant1RoleAUser1.setUserId(user1); tenant1RoleAUser1.setTenantRoleId(tenant1RoleA.getId());
        try {
            tenantRoleUserServiceAccess.create(tenant1RoleAUser1);
        } catch(Exception e) {
            fail();
        }

        try {
            tenantRoleServiceAccess.
                    hasAnyRole(user1, Collections.emptyList(), null);
        } catch(Exception e) {
            assertTrue(e.getMessage().contains(GenericErrorCodeMessage.TENANT_ROLE_FIELD_MANDATORY.toString(SystemVariables.ROLE_NAME.getLabel())));
        }
    }

    /**
     * Test method for getRoles(Long userId, Long tenantId)
     */
    @Test
    public void test029GetRoles() throws InvalidArgumentException {
        SystemRole role1 = new RoleEntity();
        roleObject(role1, "role1");

        SystemRole role2 = new RoleEntity();
        roleObject(role2, "role2");

        SystemRole role3 = new RoleEntity();
        roleObject(role3, "role3");


        Long tenant1=444L, tenant2=445L, tenant3 = 446L;
        SystemTenantRole tenant1Role1 = new TenantRoleEntity();
        tenantRoleObject(tenant1Role1, tenant1, role1);

        SystemTenantRole tenant1Role2 = new TenantRoleEntity();
        tenantRoleObject(tenant1Role2, tenant1, role2);

        SystemTenantRole tenant2Role1 = new TenantRoleEntity();
        tenantRoleObject(tenant2Role1, tenant2, role1);

        SystemTenantRole tenant2Role3 = new TenantRoleEntity();
        tenantRoleObject(tenant2Role3, tenant2, role3);

        SystemTenantRole tenant3Role3 = new TenantRoleEntity();
        tenantRoleObject(tenant3Role3, tenant3, role3);


        Long user1 = 100000000L, user2 = 100000011L;
        SystemTenantRoleUser tenant1Role1User1 = new TenantRoleUserEntity();
        userTenantRoleObject(tenant1Role1User1,user1,tenant1Role1);

        SystemTenantRoleUser tenant1Role2User1 = new TenantRoleUserEntity();
        userTenantRoleObject(tenant1Role2User1,user1,tenant1Role2);

        SystemTenantRoleUser tenant2Role1User1 = new TenantRoleUserEntity();
        userTenantRoleObject(tenant2Role1User1,user1,tenant2Role1);

        SystemTenantRoleUser tenant3Role3User1 = new TenantRoleUserEntity();
        userTenantRoleObject(tenant3Role3User1,user1,tenant3Role3);

        SystemTenantRoleUser tenant1Role2User2 = new TenantRoleUserEntity();
        userTenantRoleObject(tenant1Role2User2,user2,tenant1Role2);

        SystemTenantRoleUser tenant2Role3User2 = new TenantRoleUserEntity();
        userTenantRoleObject(tenant2Role3User2,user2,tenant2Role3);


        List<Long> ids = tenantRoleServiceAccess.getRoleIdsForUserTenant(user1, tenant1);
        assertTrue(ids.contains(role1.getId()) && ids.contains(role2.getId()));
        assertEquals(4, ids.size());

        ids = tenantRoleServiceAccess.getRoleIdsForUserTenant(user2, tenant2);
        assertTrue(ids.contains(role3.getId()));
        assertEquals(2, ids.size());

        ids = tenantRoleServiceAccess.getRoleIdsForUserTenant(99999999L, tenant3);
        assertTrue(ids.isEmpty());
    }

    /**
     * Test method getRoles(Long userId, Long tenantId)
     * without informing the mandatory field Role
     */
    @Test
    public void testGetRolesWithoutMandatoryFieldUser() {
        assertThrows(InvalidArgumentException.class, () -> tenantRoleServiceAccess.getRoleIdsForUserTenant(null, null));
    }

    /**
     * This method construct role object
     * @param role reference role object
     * @param roleName Role name
     */
    private void roleObject(SystemRole role, String roleName){
        role.setName(roleName);
        try {
            roleServiceAccess.create(role);
        } catch(Exception e) {
            assertTrue(e.getMessage().contains(GenericErrorCodeMessage.TENANT_ROLE_FIELD_MANDATORY.toString(SystemVariables.ROLE_NAME.getLabel())));
        }
    }

    /**
     * This method construct tenantRole object
     * @param tenantRole reference tenantRole object
     * @param tenantId Tenant Identifier
     * @param role reference role object
     */
    private void tenantRoleObject(SystemTenantRole tenantRole, Long tenantId, SystemRole role){
        tenantRole.setTenantId(tenantId); tenantRole.setRoleId(role.getId());
        try {
            tenantRoleServiceAccess.create(tenantRole);
        } catch(Exception e) {
            assertTrue(e.getMessage().contains(GenericErrorCodeMessage.TENANT_ROLE_FIELD_MANDATORY.toString(SystemVariables.ROLE_NAME.getLabel())));
        }
    }

    /**
     * This method construct systemTenantRoleUser object
     * @param systemTenantRoleUser reference systemTenantRoleUser object
     * @param userId User Identifier
     * @param tenantRole reference tenantRole object
     */
    private void userTenantRoleObject(SystemTenantRoleUser systemTenantRoleUser, Long userId, SystemTenantRole tenantRole){
        systemTenantRoleUser.setUserId(userId); systemTenantRoleUser.setTenantRoleId(tenantRole.getId());
        try {
            tenantRoleUserServiceAccess.create(systemTenantRoleUser);
        } catch(Exception e) {
            assertTrue(e.getMessage().contains(GenericErrorCodeMessage.TENANT_ROLE_FIELD_MANDATORY.toString(SystemVariables.ROLE_NAME.getLabel())));
        }
    }
}