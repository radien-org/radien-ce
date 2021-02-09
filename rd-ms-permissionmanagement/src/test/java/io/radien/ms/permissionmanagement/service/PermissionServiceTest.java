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
import io.radien.api.model.user.SystemUser;
import io.radien.api.service.permission.PermissionServiceAccess;
import io.radien.exception.PermissionNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.exception.PermissionNotFoundException;
import io.radien.ms.permissionmanagement.client.entities.PermissionSearchFilter;
import io.radien.ms.permissionmanagement.legacy.PermissionFactory;


import io.radien.ms.permissionmanagement.model.Permission;
import org.junit.Test;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 * @author Nuno Santana
 *
 * @author Bruno Gama
 */
public class PermissionServiceTest {

    Properties p;
    PermissionServiceAccess permissionServiceAccess;
    SystemPermission uTest;

    public PermissionServiceTest() throws Exception {
        p = new Properties();
        p.put("appframeDatabase", "new://Resource?type=DataSource");
        p.put("appframeDatabase.JdbcDriver", "org.hsqldb.jdbcDriver");
        p.put("appframeDatabase.JdbcUrl", "jdbc:hsqldb:mem:radien");
        p.put("appframeDatabase.PermissionName", "sa");
        p.put("appframeDatabase.password", "");

        final Context context = EJBContainer.createEJBContainer(p).getContext();

        permissionServiceAccess = (PermissionServiceAccess) context.lookup("java:global/rd-ms-permissionmanagement//PermissionService");

        String sub = "270e0461-416d-4faf-af9f-d6b45619ed62";

        Page<? extends SystemPermission> PermissionPage = permissionServiceAccess.getAll(null, 1, 10, null, true);
        if (PermissionPage.getTotalResults() > 0) {
            uTest = PermissionPage.getResults().get(0);
        } else {
            uTest = PermissionFactory.create("permissionName", 2L);
            permissionServiceAccess.save(uTest);
        }
    }

    /**
     * Add Permission test.
     * Will create and save the Permission.
     * Expected result: Success.
     * Tested methods: void save(Permission Permission)
     *
     * @throws UniquenessConstraintException in case of requested action is not well constructed
     * @throws io.radien.ms.permissionmanagement.client.exceptions.NotFoundException in case no Permission was found after the save in the DB
     */
    @Test
    public void testAddPermission() throws PermissionNotFoundException {
        SystemPermission result = permissionServiceAccess.get(uTest.getId());
        assertNotNull(result);
    }

    /**
     * Add Permission test with duplicated Name.
     * Will create and save the Permission, with an already existent name.
     * Expected result: Throw treated exception Error 103 Message There is more than one Permission with the same Name.
     * Tested methods: void save(Permission Permission)
     */
    @Test
    public void testAddDuplicatedName() {
        Permission u = PermissionFactory.create("permissionName", 2L);
        Exception exception = assertThrows(UniquenessConstraintException.class, () -> permissionServiceAccess.save(u));
        String expectedMessage = "{\"code\":101, \"key\":\"error.duplicated.field\", \"message\":\"There is more than" +
                " one permission with the same value for the field: Name\"}";
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
        Permission u = PermissionFactory.create("testGetIdFirstName", 
                2L);
        permissionServiceAccess.save(u);
        SystemPermission result = permissionServiceAccess.get(u.getId());
        assertNotNull(result);
        assertEquals(u.getName(), result.getName());
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
        Permission p1 = PermissionFactory.create("testGetByListOfIdsFirstName1", 2L);
        permissionServiceAccess.save(p1);

        Permission p2 = PermissionFactory.create("testGetByListOfIdsFirstName2", 2L);
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
    public void testDeleteById() throws PermissionNotFoundException {
        SystemPermission result = permissionServiceAccess.get(uTest.getId());
        assertNotNull(result);
        assertEquals(uTest.getName(), result.getName());
        permissionServiceAccess.delete(uTest.getId());
        result = permissionServiceAccess.get(uTest.getId());
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
        Permission p1 = PermissionFactory.create("testDeleteByListOfIdsFirstName1", 2L);
        permissionServiceAccess.save(p1);

        Permission p2 = PermissionFactory.create("testDeleteByListOfIdsFirstName2", 2L);
        permissionServiceAccess.save(p2);

        Permission p3 = PermissionFactory.create("testDeleteByListOfIdsFirstName3", 2L);
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
        SystemPermission p1 = PermissionFactory.create("testUpdatePermissionName1", 2L);
        permissionServiceAccess.save(p1);

        SystemPermission p2 = PermissionFactory.create("testUpdatePermissionName2", 2L);
        permissionServiceAccess.save(p2);

        SystemPermission p3 = PermissionFactory.create("testUpdatePermissionName1", 2L);

        p3.setId(p1.getId());

        permissionServiceAccess.save(p3);

        p1 = permissionServiceAccess.get(p1.getId());

        assertEquals(p1.getName(), p3.getName());
        SystemPermission u4 = PermissionFactory.create("testUpdatePermissionName4", 2L);

        u4.setId(p1.getId());

        permissionServiceAccess.save(u4);

        p1 = permissionServiceAccess.get(p1.getId());

        assertEquals(p1.getName(), u4.getName());

    }

    @Test
    public void testUpdateFailureMultipleRecords() throws Exception {
        Permission p1 = PermissionFactory.create("permissionName1", 2L);
        permissionServiceAccess.save(p1);

        Permission p2 = PermissionFactory.create("permissionName2", 2L);
        permissionServiceAccess.save(p2);

        Permission p3 = PermissionFactory.create("permissionName3", 2L);
        permissionServiceAccess.save(p3);

        Permission u4 = PermissionFactory.create("permissionName1", 2L);

        Exception exceptionForRepeatedName = assertThrows(Exception.class, () -> permissionServiceAccess.save(u4));
        String exceptionForRepeatedNameMessage = exceptionForRepeatedName.getMessage();
        String expectedMessage = "{\"code\":101, \"key\":\"error.duplicated.field\", \"message\":\"There is more than one permission with the same value for the field: Name\"}";
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
                "\"message\":\"There is more than one permission with the same value for the field: Name\"}";

        Permission p1 = PermissionFactory.create("permissionNamePerm1", 2L);
        permissionServiceAccess.save(p1);

        Permission p2 = PermissionFactory.create("permissionNamePerm2", 2L);
        permissionServiceAccess.save(p2);

        Permission p3 = PermissionFactory.create("permissionNamePerm1", 2L);

        Exception exceptionForFieldName = assertThrows(Exception.class, () -> permissionServiceAccess.save(p3));
        String actualMessage = exceptionForFieldName.getMessage();
        assertTrue(actualMessage.contains(expectedMessageName));

        Permission u4 = PermissionFactory.create("permissionNamePerm2", 2L);

        Exception exceptionName2 = assertThrows(Exception.class, () -> permissionServiceAccess.save(u4));
        String messageFromException = exceptionName2.getMessage();
        assertTrue(messageFromException.contains(expectedMessageName));
    }

    @Test
    public void testGetAllSort() throws UniquenessConstraintException, PermissionNotFoundException {
        SystemPermission permissionA = PermissionFactory.create("a", 2L);
        permissionServiceAccess.save(permissionA);
        SystemPermission permissionB = PermissionFactory.create("zzz", 2L);
        permissionServiceAccess.save(permissionB);
        SystemPermission permissionC = PermissionFactory.create("d", 2L);
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
        SystemPermission testById1 = PermissionFactory.create("zz", 1L);

        SystemPermission testById2 = PermissionFactory.create("aa", 1L);

        SystemPermission testById3 = PermissionFactory.create("aabb", 1L);

        permissionServiceAccess.save(testById1);
        permissionServiceAccess.save(testById2);
        permissionServiceAccess.save(testById3);

        List<? extends SystemPermission> permissionsAnd = permissionServiceAccess.getPermissions(new PermissionSearchFilter("zz",true,true));
        assertEquals(1,permissionsAnd.size());

        List<? extends SystemPermission> permissionsOr = permissionServiceAccess.getPermissions(new PermissionSearchFilter("aa",true,false));
        assertEquals(1,permissionsOr.size());

        List<? extends SystemPermission> permissionsNotExact = permissionServiceAccess.getPermissions(new PermissionSearchFilter("aa",false,true));
        assertEquals(2,permissionsNotExact.size());
    }
}