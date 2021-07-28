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
package io.radien.ms.tenantmanagement.service;

import io.radien.api.entity.Page;
import io.radien.api.model.tenant.SystemContract;

import io.radien.api.service.tenant.ContractServiceAccess;

import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.NotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.exception.UserNotFoundException;

import io.radien.ms.tenantmanagement.entities.ContractEntity;
import io.radien.ms.tenantmanagement.client.services.ContractFactory;
import org.junit.Test;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Nuno Santana
 * @author Bruno Gama
 */
public class ContractServiceTest {

    Properties p;
    ContractServiceAccess contractServiceAccess;
    SystemContract cTest;

    public ContractServiceTest() throws Exception {
        p = new Properties();
        p.put("appframeDatabase", "new://Resource?type=DataSource");
        p.put("appframeDatabase.JdbcDriver", "org.hsqldb.jdbcDriver");
        p.put("appframeDatabase.JdbcUrl", "jdbc:hsqldb:mem:radienTest");
        p.put("appframeDatabase.userName", "sa");
        p.put("appframeDatabase.password", "");
        p.put("openejb.deployments.classpath.include",".*tenant.*");
        p.put("openejb.deployments.classpath.exclude",".*client.*");

        final Context context = EJBContainer.createEJBContainer(p).getContext();

        contractServiceAccess = (ContractServiceAccess) context.lookup("java:global/rd-ms-tenantmanagement//ContractService");

        List<? extends SystemContract> contractList = contractServiceAccess.get("aa");
        if(contractList.size() > 0) {
            cTest = contractList.get(0);
        } else {
            String name = "aa";
            LocalDateTime start  = LocalDateTime.now();
            LocalDateTime end = LocalDateTime.now();
            cTest = new ContractEntity(ContractFactory.create(name,start,end,1L));
            contractServiceAccess.create(cTest);
        }
    }

    /**
     * Add user test.
     * Will create and save the user.
     * Expected result: Success.
     * Tested methods: void save(User user)
     *
     * @throws UniquenessConstraintException in case of requested action is not well constructed
     */
    @Test
    public void testAddUser() throws UserNotFoundException {
        SystemContract result = contractServiceAccess.get(cTest.getId());
        assertNotNull(result);
    }

    /**
     * Add user test with duplicated Email.
     * Will create and save the user, with an already existent email.
     * Expected result: Throw treated exception Error 101 Message There is more than one user with the same Email.
     * Tested methods: void save(User user)
     */
    @Test
    public void testAddDuplicatedName() {
        String name = "aa";
        LocalDateTime start  = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now();
        SystemContract c = ContractFactory.create(name,start,end,1L);
        Exception exception = assertThrows(UniquenessConstraintException.class, () -> contractServiceAccess.create(c));
        String expectedMessage = GenericErrorCodeMessage.DUPLICATED_FIELD.toString("Name");
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    /**
     * Add user test with duplicated Email.
     * Will create and save the user, with an already existent email.
     * Expected result: Throw treated exception Error 101 Message There is more than one user with the same Email.
     * Tested methods: void save(User user)
     */
    @Test
    public void testUpdateDuplicated() {
        String name = "aa";
        LocalDateTime start  = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now();
        SystemContract c = ContractFactory.create(name,start,end,1L);
        Exception exception = assertThrows(UniquenessConstraintException.class, () -> contractServiceAccess.update(c));
        String expectedMessage = GenericErrorCodeMessage.DUPLICATED_FIELD.toString("Name");
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }


    /**
     * Gets user using the PK (id).
     * Will create a new user, save it into the DB and retrieve the specific user using the ID.
     * Expected result: will return the correct inserted user.
     * Tested methods: SystemUser get(Long userId)
     *
     * @throws UniquenessConstraintException in case of requested action is not well constructed
     */
    @Test
    public void testGetById() throws UserNotFoundException, UniquenessConstraintException {
        String name = "a<a>";
        LocalDateTime start  = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now();
        SystemContract c = new ContractEntity(ContractFactory.create(name,start,end,1L));
        contractServiceAccess.create(c);
        SystemContract result = contractServiceAccess.get(c.getId());
        assertNotNull(result);
        assertEquals(c.getName(), result.getName());
        assertEquals(c.getStart(), result.getStart());
        assertEquals(c.getEnd(), result.getEnd());
    }

    /**
     * Deletes inserted user using the PK (id).
     * Will create a new user, save it into the DB and delete it after using the specific ID.
     * Expected result: will return null when retrieving the user.
     * Tested methods: void delete(Long userId)
     *
     * @throws UniquenessConstraintException in case of requested action is not well constructed
     */
    @Test
    public void testDeleteById() throws UserNotFoundException {
        SystemContract result = contractServiceAccess.get(cTest.getId());
        assertNotNull(result);
        assertEquals(cTest.getName(), result.getName());
        contractServiceAccess.delete(cTest.getId());
        result = contractServiceAccess.get(cTest.getId());
        assertNull(result);
    }

    /**
     * Deletes inserted user using the PK (id).
     * Will create a new user, save it into the DB and delete it after using the specific ID.
     * Expected result: will return null when retrieving the user.
     * Tested methods: void delete(Long userId)
     *
     * @throws UniquenessConstraintException in case of requested action is not well constructed
     */
    @Test
    public void testDeleteByListId() throws UserNotFoundException {
        SystemContract result = contractServiceAccess.get(cTest.getId());
        assertNotNull(result);
        assertEquals(cTest.getName(), result.getName());
        contractServiceAccess.delete(Collections.singletonList(cTest.getId()));
        result = contractServiceAccess.get(cTest.getId());
        assertNull(result);
    }

    /**
     * Test updates the user information. Should be a success in this test case, and the last name, the logon
     * and the user email from the user one, should have been updated to the user three information.
     * @throws Exception in case of user to be updated not found
     */
    @Test
    public void testUpdateSuccess() throws Exception {
        String name1 = "a<a>23";
        LocalDateTime start1  = LocalDateTime.now();
        LocalDateTime end1 = LocalDateTime.now();
        SystemContract c1 = new ContractEntity(ContractFactory.create(name1,start1,end1,1L));
        contractServiceAccess.create(c1);
        String name3 = "a<a>99zz";
        LocalDateTime start3  = LocalDateTime.now();
        LocalDateTime end3 = LocalDateTime.now();
        c1.setStart(start3);
        c1.setEnd(end3);
        c1.setName(name3);
        contractServiceAccess.update(c1);


        c1 = contractServiceAccess.get(c1.getId());

        assertEquals(name3, c1.getName());
        assertEquals(start3, c1.getStart());
        assertEquals(end3, c1.getEnd());
    }

    @Test
    public void testGetTotalRecordsCount() {
        long result = contractServiceAccess.getTotalRecordsCount();
        assertEquals(3, result);
    }

    @Test
    public void testGetAll() throws UniquenessConstraintException {
        LocalDateTime start1  = LocalDateTime.now();
        LocalDateTime end1 = LocalDateTime.now();
        SystemContract c1 = new ContractEntity(ContractFactory.create("getAll1",start1,end1,1L));
        contractServiceAccess.create(c1);
        SystemContract c2 = new ContractEntity(ContractFactory.create("getAll2",start1,end1,1L));
        contractServiceAccess.create(c2);
        SystemContract c3 = new ContractEntity(ContractFactory.create("getAll3",start1,end1,1L));
        contractServiceAccess.create(c3);

        Page<? extends SystemContract> page = contractServiceAccess.getAll(1, 10);

        assertTrue(page.getTotalResults()>=3);
    }

    @Test
    public void testExists() throws UniquenessConstraintException, NotFoundException {
        LocalDateTime start1  = LocalDateTime.now();
        LocalDateTime end1 = LocalDateTime.now();
        SystemContract c1 = new ContractEntity(ContractFactory.create("exists1",start1,end1,1L));
        contractServiceAccess.create(c1);

        assertTrue(contractServiceAccess.exists(c1.getId()));
    }
}