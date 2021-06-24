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
package io.radien.ms.permissionmanagement.service;

import io.radien.api.entity.Page;

import io.radien.api.model.permission.SystemPermission;
import io.radien.api.model.permission.SystemResource;
import io.radien.api.model.permission.SystemAction;
import io.radien.api.model.permission.SystemActionSearchFilter;
import io.radien.api.model.permission.SystemPermissionSearchFilter;
import io.radien.api.service.permission.ActionServiceAccess;
import io.radien.api.service.permission.PermissionServiceAccess;
import io.radien.api.service.permission.ResourceServiceAccess;
import io.radien.exception.PermissionNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.permissionmanagement.client.entities.ActionSearchFilter;
import io.radien.ms.permissionmanagement.client.entities.PermissionSearchFilter;
import io.radien.ms.permissionmanagement.legacy.ActionFactory;
import io.radien.ms.permissionmanagement.legacy.PermissionFactory;

import io.radien.ms.permissionmanagement.model.Action;
import io.radien.ms.permissionmanagement.model.Permission;
import io.radien.ms.permissionmanagement.model.Resource;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * @author Nuno Santana
 *
 * @author Bruno Gama
 */
public class PermissionServiceTest {

    static PermissionServiceAccess permissionServiceAccess;
    static ActionServiceAccess actionServiceAccess;
    static ResourceServiceAccess resourceServiceAccess;
    static SystemPermission pTest;
    static SystemAction aTest;
    static EJBContainer container;

    @BeforeClass
    public static void start() throws Exception {
        Properties p = new Properties();
        p.put("openejb.deployments.classpath.include",".*permission.*");
        p.put("openejb.deployments.classpath.exclude",".*client.*");
        container = EJBContainer.createEJBContainer(p);
        final Context context = container.getContext();
        permissionServiceAccess = (PermissionServiceAccess) context.lookup("java:global/rd-ms-permissionmanagement//PermissionService");
        actionServiceAccess= (ActionServiceAccess) context.lookup("java:global/rd-ms-permissionmanagement//ActionService");
        resourceServiceAccess = (ResourceServiceAccess) context.lookup("java:global/rd-ms-permissionmanagement//ResourceService");
    }

    @Before
    public void init() throws UniquenessConstraintException, NamingException {
        container.getContext().bind("inject", this);
        Page<? extends SystemAction> actionPage = actionServiceAccess.getAll(null, 1,
                1000, null, true);
        Page<? extends SystemPermission> permissionPage = permissionServiceAccess.getAll(null,
                1, 1000, null, true);

        permissionServiceAccess.delete(permissionPage.getResults().stream().
                map(SystemPermission::getId).collect(Collectors.toList()));

        actionServiceAccess.delete(actionPage.getResults().stream().
                map(SystemAction::getId).collect(Collectors.toList()));
    }

    @AfterClass
    public static void stop() {
        if (container != null) {
            container.close();
        }
    }

    /**
     * Add Permission test.
     * Will create and save the Permission.
     * Expected result: Success.
     * Tested methods: void save(Permission Permission)
     *
     * @throws UniquenessConstraintException in case of requested action is not well constructed
     */
    @Test
    public void testAddPermission() throws UniquenessConstraintException {
        final String name = "TestReadPermission";
        SystemPermission permission = new Permission();
        permission.setName(name);
        permission.setResourceId(1L);
        permission.setActionId(111L);
        permissionServiceAccess.save(permission);

        List<? extends SystemPermission> permissions = permissionServiceAccess.
                getPermissions(new PermissionSearchFilter(name, 111L, 1L, true, true));

        assertNotNull(permissions);
        assertEquals(permissions.size(), 1);

        SystemPermission systemPermission = permissions.get(0);
        assertEquals(systemPermission.getName(), permission.getName());
    }

    /**
     * Add Permission test with duplicated Name.
     * Will create and save the Permission, with an already existent name.
     * Expected result: Throw treated exception Error 103 Message There is more than one Permission with the same Name.
     * Tested methods: void save(Permission Permission)
     */
    @Test
    public void testAddDuplicatedName() throws UniquenessConstraintException {
        Permission u = PermissionFactory.create("permissionNameXYZ", null,2L);
        permissionServiceAccess.save(u);

        Permission u2 = PermissionFactory.create("permissionNameXYZ", null,2L);
        Exception exception = assertThrows(UniquenessConstraintException.class, () -> permissionServiceAccess.save(u2));
        String expectedMessage = "{\"code\":101, \"key\":\"error.duplicated.field\", \"message\":\"There is more than" +
                " one resource with the same value for the field: Name\"}";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    /**
     * Gets Permission using the PK (id).
     * Will create a new Permission, save it into the DB and retrieve the specific Permission using the ID.
     * Expected result: will return the correct inserted Permission.
     * Tested methods: SystemPermission get(Long PermissionId)
     *
     * @throws UniquenessConstraintException in case of requested action is not well constructed
     * @throws PermissionNotFoundException in case no Permission was found after the save in the DB
     */
    @Test
    public void testGetById() throws PermissionNotFoundException, UniquenessConstraintException {
        Permission u = PermissionFactory.create("testGetIdFirstName", null, 2L);
        permissionServiceAccess.save(u);
        SystemPermission result = permissionServiceAccess.get(u.getId());
        assertNotNull(result);
        assertEquals(u.getName(), result.getName());
    }

    @Test
    public void testGetTotalRecordsCount() {
        long result = permissionServiceAccess.getTotalRecordsCount();
        assertEquals(0, result);
    }

    /**
     * Gets multiple Permissions using a list of PK (id).
     * Will create 2 new Permissions, save them into the DB and retrieve the specific Permissions using the ID's.
     * Expected result: will return the correct number of inserted Permission.
     * Tested methods: List<SystemPermission> get(List<Long> PermissionIds)
     *
     * @throws UniquenessConstraintException in case of requested action is not well constructed
     */
    @Test
    public void testGetByListOfIds() throws UniquenessConstraintException, PermissionNotFoundException {
        Permission p1 = PermissionFactory.create("testGetByListOfIdsFirstName1", null, 2L);
        permissionServiceAccess.save(p1);

        Permission p2 = PermissionFactory.create("testGetByListOfIdsFirstName2", null,2L);
        permissionServiceAccess.save(p2);

        List<Long> PermissionIds = Arrays.asList(p1.getId(), p2.getId());
        List<SystemPermission> result = permissionServiceAccess.get(PermissionIds);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void testGetByEmptyListOfIds() {

        List<SystemPermission> result = permissionServiceAccess.get(new ArrayList<>());
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Deletes inserted Permission using the PK (id).
     * Will create a new Permission, save it into the DB and delete it after using the specific ID.
     * Expected result: will return null when retrieving the Permission.
     * Tested methods: void delete(Long PermissionId)
     *
     * @throws PermissionNotFoundException in case no Permission was found after the save in the DB
     */
    @Test
    public void testDeleteById() throws UniquenessConstraintException, PermissionNotFoundException {
        pTest = PermissionFactory.create("testGetIdFirstName", null, 2L);
        permissionServiceAccess.save(pTest);
        SystemPermission result = permissionServiceAccess.get(pTest.getId());
        assertNotNull(result);
        assertEquals(pTest.getName(), result.getName());
        permissionServiceAccess.delete(pTest.getId());
        result = permissionServiceAccess.get(pTest.getId());
        assertNull(result);
    }

    /**
     * Deletes multiple Permissions using a list of PK (id).
     * Will create 3 new Permissions, save them into the DB and delete the specific 2 Permissions using the ID's.
     * Expected result: will delete the correct number of inserted Permission and return only one of them (the not deleted
     * one).
     * Tested methods: void delete(Collection<Long> PermissionIds)
     *
     * @throws UniquenessConstraintException in case of requested action is not well constructed
     * @throws PermissionNotFoundException in case no Permission was found after the save in the DB
     */
    @Test
    public void testDeleteByListOfIds() throws PermissionNotFoundException, UniquenessConstraintException {
        Permission p1 = PermissionFactory.create("testDeleteByListOfIdsFirstName1",
                null, 2L);
        permissionServiceAccess.save(p1);

        Permission p2 = PermissionFactory.create("testDeleteByListOfIdsFirstName2",
                null, 2L);
        permissionServiceAccess.save(p2);

        Permission p3 = PermissionFactory.create("testDeleteByListOfIdsFirstName3",
                null, 2L);
        permissionServiceAccess.save(p3);

        List<Long> permissionIds = Arrays.asList(p1.getId(), p2.getId());
        permissionServiceAccess.delete(permissionIds);
        List<SystemPermission> result = permissionServiceAccess.get(permissionIds);
        assertEquals(0, result.size());
        SystemPermission resultExistentOne = permissionServiceAccess.get(p3.getId());
        assertNotNull(result);
        assertEquals(p3.getName(), resultExistentOne.getName());
    }


    /**
     * Test updates the Permission information. 
     * Should be a success in this test case, if the name attribute from the Permission one, 
     * should have been updated to the Permission three information.
     * @throws Exception in case of Permission to be updated not found
     */
    @Test
    public void testUpdateSuccess() throws Exception {
        SystemPermission p1 = PermissionFactory.create("testUpdatePermissionName1",
                null, 2L);
        permissionServiceAccess.save(p1);

        SystemPermission p2 = PermissionFactory.create("testUpdatePermissionName2",
                null, 2L);
        permissionServiceAccess.save(p2);

        SystemPermission p3 = PermissionFactory.create("testUpdatePermissionName1",
                null, 2L);

        p3.setId(p1.getId());

        permissionServiceAccess.save(p3);

        p1 = permissionServiceAccess.get(p1.getId());

        assertEquals(p1.getName(), p3.getName());
        SystemPermission u4 = PermissionFactory.create("testUpdatePermissionName4",
                null, 2L);

        u4.setId(p1.getId());

        permissionServiceAccess.save(u4);

        p1 = permissionServiceAccess.get(p1.getId());

        assertEquals(p1.getName(), u4.getName());

    }

    @Test
    public void testUpdateFailureMultipleRecords() throws Exception {
        Permission p1 = PermissionFactory.create("permissionName1", null, 2L);
        permissionServiceAccess.save(p1);

        Permission p2 = PermissionFactory.create("permissionName2", null, 2L);
        permissionServiceAccess.save(p2);

        Permission p3 = PermissionFactory.create("permissionName3", null, 2L);
        permissionServiceAccess.save(p3);

        Permission u4 = PermissionFactory.create("permissionName1", null, 2L);

        Exception exceptionForRepeatedName = assertThrows(Exception.class, () -> permissionServiceAccess.save(u4));
        String exceptionForRepeatedNameMessage = exceptionForRepeatedName.getMessage();
        String expectedMessage = "{\"code\":101, \"key\":\"error.duplicated.field\", \"message\":\"There is more than one resource with the same value for the field: Name\"}";
        assertTrue(exceptionForRepeatedNameMessage.contains(expectedMessage));

    }

    /**
     * Test updates the Permission information. Should be a failure in this test case and no information should be updated,
     * since that we are updating Permission one with the information from Permission three, but using a duplicated email address.
     * @throws UniquenessConstraintException in case of Permission to be updated not found
     */
    @Test
    public void testUpdateFailureDuplicatedName() throws UniquenessConstraintException {
        String expectedMessageName = "{\"code\":101, \"key\":\"error.duplicated.field\", " +
                "\"message\":\"There is more than one resource with the same value for the field: Name\"}";

        Permission p1 = PermissionFactory.create("permissionNamePerm1", null, 2L);
        permissionServiceAccess.save(p1);

        Permission p2 = PermissionFactory.create("permissionNamePerm2", null, 2L);
        permissionServiceAccess.save(p2);

        Permission p3 = PermissionFactory.create("permissionNamePerm1", null, 2L);

        Exception exceptionForFieldName = assertThrows(Exception.class, () -> permissionServiceAccess.save(p3));
        String actualMessage = exceptionForFieldName.getMessage();
        assertTrue(actualMessage.contains(expectedMessageName));

        Permission u4 = PermissionFactory.create("permissionNamePerm2", null, 2L);

        Exception exceptionName2 = assertThrows(Exception.class, () -> permissionServiceAccess.save(u4));
        String messageFromException = exceptionName2.getMessage();
        assertTrue(messageFromException.contains(expectedMessageName));
    }

    @Test
    public void testGetAllSort() throws UniquenessConstraintException, PermissionNotFoundException {
        SystemPermission permissionA = PermissionFactory.create("a", null, 2L);
        permissionServiceAccess.save(permissionA);
        SystemPermission permissionB = PermissionFactory.create("zzz", null, 2L);
        permissionServiceAccess.save(permissionB);
        SystemPermission permissionC = PermissionFactory.create("d", null, 2L);
        permissionServiceAccess.save(permissionC);

        List<String> orderby = new ArrayList<>();
        orderby.add("name");

        Page<? extends SystemPermission> permissionPage = permissionServiceAccess.getAll(null, 1, 10, orderby, true);
  
        assertTrue(permissionPage.getTotalResults()>=3);

        assertEquals("a",permissionPage.getResults().get(0).getName());

        permissionPage = permissionServiceAccess.getAll(null, 1, 10, orderby, false);
        assertTrue(permissionPage.getTotalResults()>=3);
        assertEquals("zzz",permissionPage.getResults().get(0).getName());

        Page<? extends SystemPermission> permissionPageWhere = permissionServiceAccess.getAll("a", 1, 10, null, true);
        assertTrue(permissionPageWhere.getTotalResults() == 1);

        assertEquals("a",permissionPageWhere.getResults().get(0).getName());
    }
    @Test
    public void testGetByIsExactOrLogical() throws UniquenessConstraintException, PermissionNotFoundException {
        SystemPermission testById1 = PermissionFactory.create("zz", null, 1L);

        SystemPermission testById2 = PermissionFactory.create("aa", null, 1L);

        SystemPermission testById3 = PermissionFactory.create("aabb", null, 1L);

        SystemPermission testById4 = PermissionFactory.create("aabaco", null, 1L);

        permissionServiceAccess.save(testById1);
        permissionServiceAccess.save(testById2);
        permissionServiceAccess.save(testById3);
        permissionServiceAccess.save(testById4);

        List<? extends SystemPermission> permissionsAnd = permissionServiceAccess.getPermissions(
                new PermissionSearchFilter("zz",null, null,true,true));
        assertEquals(1, permissionsAnd.size());

        List<? extends SystemPermission> permissionsOr = permissionServiceAccess.getPermissions(
                new PermissionSearchFilter("aa",null, null,true,false));
        assertEquals(1, permissionsOr.size());

        List<? extends SystemPermission> permissionsNotExact = permissionServiceAccess.getPermissions(
                new PermissionSearchFilter("aa",null,null,false,true));
        assertEquals(3, permissionsNotExact.size());
    }

    @Test
    public void associatePermissionAndAction() throws UniquenessConstraintException {
        SystemPermission sp = PermissionFactory.create("perm-radien-1", null, 1L);
        permissionServiceAccess.save(sp);
        sp = PermissionFactory.create("perm-radien-2", null, 1L);
        permissionServiceAccess.save(sp);

        SystemAction sa = ActionFactory.create("read-contract", null);
        actionServiceAccess.save(sa);

        // Retrieve the permission
        SystemPermissionSearchFilter permissionFilter = new PermissionSearchFilter();
        permissionFilter.setName("perm-radien-1");
        permissionFilter.setExact(true);
        List<? extends SystemPermission> permissions = permissionServiceAccess.getPermissions(permissionFilter);
        Permission permission = (Permission) permissions.get(0);

        // Retrieve the action
        SystemActionSearchFilter filter = new ActionSearchFilter();
        filter.setName("read-contract");
        filter.setLogicConjunction(true);
        List<? extends SystemAction> actions = actionServiceAccess.getActions(filter);
        Action action = (Action) actions.get(0);

        assertNotNull(action);
        assertEquals(action.getName(), sa.getName());

        // Setting action
        permission.setActionId(action.getId());

        // Save permission
        permissionServiceAccess.save(permission);

        // Retrieve the permission again
        permissions = permissionServiceAccess.getPermissions(permissionFilter);
        SystemPermission p = permissions.get(0);

        assertNotNull(p.getActionId());
        assertEquals(p.getActionId(), action.getId());
    }

    @Test
    public void testFilterPermissionByAction() throws UniquenessConstraintException {
        Long actionId1 = 111L;
        Long actionId2 = 101L;
        Long actionId3 = 11L;

        SystemPermission p1 = new Permission();
        p1.setName("perm1");
        p1.setActionId(actionId1);
        permissionServiceAccess.save(p1);

        SystemPermission p2 = new Permission();
        p2.setName("perm2");
        p2.setActionId(actionId2);
        permissionServiceAccess.save(p2);

        SystemPermission p3 = new Permission();
        p3.setName("perm3");
        p3.setActionId(actionId3);
        permissionServiceAccess.save(p3);

        SystemPermission p4 = new Permission();
        p4.setName("perm4");
        p4.setActionId(actionId3);
        permissionServiceAccess.save(p4);

        SystemPermission p5 = new Permission();
        p5.setName("perm5");
        p5.setActionId(actionId3);
        permissionServiceAccess.save(p5);

        SystemPermission p6 = new Permission();
        p6.setName("perm6");
        p6.setActionId(actionId2);
        permissionServiceAccess.save(p6);

        // Retrieving permissions by actionId3
        SystemPermissionSearchFilter filter = new PermissionSearchFilter(null, actionId3,
                null, true, true);
        List<? extends SystemPermission> systemPermissions = permissionServiceAccess.getPermissions(filter);
        assertEquals(systemPermissions.size(), 3);

        // Retrieving permissions by actionId2
        filter = new PermissionSearchFilter(null, actionId2,
                null, true, true);
        systemPermissions = permissionServiceAccess.getPermissions(filter);
        assertEquals(systemPermissions.size(), 2);

        // Retrieving permissions by actionId1
        filter = new PermissionSearchFilter(null, actionId1,
                null, true, true);
        systemPermissions = permissionServiceAccess.getPermissions(filter);
        assertEquals(systemPermissions.size(), 1);
    }

    @Test
    public void testFilterPermissionByActionAndResource() throws UniquenessConstraintException {
        Long actionId1 = 111L;
        Long resourceId1 = 11111L;
        Long actionId2 = 101L;
        Long resourceId2 = 10101L;
        Long actionId3 = 11L;

        SystemPermission p1 = new Permission();
        p1.setName("perm1");
        p1.setActionId(actionId1);
        p1.setResourceId(resourceId1);
        permissionServiceAccess.save(p1);

        SystemPermission p2 = new Permission();
        p2.setName("perm2");
        p2.setActionId(actionId2);
        p2.setResourceId(resourceId2);
        permissionServiceAccess.save(p2);

        SystemPermission p3 = new Permission();
        p3.setName("perm3");
        p3.setActionId(actionId3);
        p3.setResourceId(resourceId2);
        permissionServiceAccess.save(p3);

        SystemPermission p4 = new Permission();
        p4.setName("perm4");
        p4.setActionId(actionId3);
        p4.setResourceId(resourceId2);
        assertThrows(Exception.class, () -> permissionServiceAccess.save(p4));

        SystemPermissionSearchFilter filter = new PermissionSearchFilter("perm3",
                actionId3, resourceId2, true, true);
        List<? extends SystemPermission> permissions = permissionServiceAccess.getPermissions(filter);
        assertEquals(permissions.size(), 1);
        assertEquals(permissions.get(0).getId(), p3.getId());
        assertEquals(permissions.get(0).getActionId(), p3.getActionId());
        assertEquals(permissions.get(0).getResourceId(), p3.getResourceId());

        filter = new PermissionSearchFilter("perm",
                actionId3, resourceId2, false, true);
        permissions = permissionServiceAccess.getPermissions(filter);
        assertEquals(permissions.size(), 1);

        // Only p3 and p4 have actionId3 and resourceId2
        boolean p3AndP4 = permissions.stream().
                allMatch(p -> p.getId() == p3.getId() || p.getId() == p4.getId());
        assertTrue(p3AndP4);

        filter = new PermissionSearchFilter("perm",
                null, resourceId2, false, true);
        permissions = permissionServiceAccess.getPermissions(filter);
        assertEquals(permissions.size(), 2);

        // Only p3 and p4 have actionId3 and resourceId2
        boolean p2AndP3AndP4 = permissions.stream().
                allMatch( p -> p.getId() == p2.getId() || p.getId() == p3.getId() || p.getId() == p4.getId());
        assertTrue(p2AndP3AndP4);
    }

    @Test
    public void testExistsPermissionMethod() throws UniquenessConstraintException {
        Permission experiment = new Permission();
        experiment.setName("create-contract");

        permissionServiceAccess.save(experiment);

        boolean checkExistsById = permissionServiceAccess.exists(experiment.getId(), null);
        assertTrue(checkExistsById);

        boolean checkExistByName = permissionServiceAccess.exists(null,"create-contract");
        assertTrue(checkExistByName);

        boolean checkingWithValidIdAndInvalidName = permissionServiceAccess.exists(experiment.getId(),
                "create-organization");
        assertTrue(checkingWithValidIdAndInvalidName);

        boolean checkWithInvalidIdAndCorrectName = permissionServiceAccess.exists(999L, "create-contract");
        assertFalse(checkWithInvalidIdAndCorrectName);
    }

    @Test
    public void testExistsPermissionNotFound() {
        boolean check = permissionServiceAccess.exists(3333L, null);
        assertFalse(check);

        check = permissionServiceAccess.exists(null, "create-subtenant");
        assertFalse(check);
    }

    @Test
    public void testExistsPermissionWithNoArguments() {
        boolean check = permissionServiceAccess.exists(null, null);
        assertFalse(check);
    }

    @Test
    public void testPermissionRetrievalByActionAndResourceNames() throws UniquenessConstraintException {
        SystemAction a1 = new Action();
        a1.setName("delete");
        actionServiceAccess.save(a1);

        SystemAction a2 = new Action();
        a2.setName("create");
        actionServiceAccess.save(a2);

        SystemAction a3 = new Action();
        a3.setName("update");
        actionServiceAccess.save(a3);

        SystemResource r1 = new Resource();
        r1.setName("dealer");
        resourceServiceAccess.save(r1);

        SystemPermission p1 = new Permission();
        p1.setName(a1.getName() + "-" + r1.getName());
        p1.setResourceId(r1.getId());
        p1.setActionId(a1.getId());
        permissionServiceAccess.save(p1);

        SystemPermission p2 = new Permission();
        p2.setName(a2.getName() + "-" + r1.getName());
        p2.setResourceId(r1.getId());
        p2.setActionId(a2.getId());
        permissionServiceAccess.save(p2);

        SystemPermission p3 = permissionServiceAccess.
                getPermissionByActionAndResourceNames(a1.getName(), r1.getName());

        assertNotNull(p3);
        assertEquals(p3.getId(), p1.getId());
        assertEquals(p3.getResourceId(), r1.getId());
        assertEquals(p3.getActionId(), a1.getId());

        SystemPermission p4 = permissionServiceAccess.
                getPermissionByActionAndResourceNames("test", "test");
        assertNull(p4);

        SystemPermission p5 = new Permission();
        p5.setName(a3.getName() + "-" + r1.getName());
        permissionServiceAccess.save(p5);
        assertNotNull(p5.getId());

        SystemPermission p6 = new Permission();
        p6.setName("create-dealer");
        assertThrows(UniquenessConstraintException.class, () -> permissionServiceAccess.save(p6));

        SystemPermission p7 = permissionServiceAccess.getPermissionByActionAndResourceNames("create", "dealer");
        assertNotNull(p7);
        assertEquals(p7.getActionId(), a2.getId());
        assertEquals(p7.getResourceId(), r1.getId());
    }
}
