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
package io.radien.ms.usermanagement.service;

import io.radien.api.entity.Page;
import io.radien.api.model.user.SystemUser;
import io.radien.api.service.batch.BatchSummary;
import io.radien.api.service.batch.DataIssue;
import io.radien.api.service.user.UserServiceAccess;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.NotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.exception.UserNotFoundException;
import io.radien.ms.usermanagement.client.entities.UserSearchFilter;
import io.radien.ms.usermanagement.entities.User;
import io.radien.ms.usermanagement.legacy.UserFactory;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

/**
 * @author Nuno Santana
 * @author Bruno Gama
 */
public class UserServiceTest {

    static Properties p;
    static UserServiceAccess userServiceAccess;
    static SystemUser uTest;
    static EJBContainer container;

    @BeforeClass
    public static void start() throws Exception {
        p = new Properties();
        p.put("appframeDatabase", "new://Resource?type=DataSource");
        p.put("appframeDatabase.JdbcDriver", "org.hsqldb.jdbcDriver");
        p.put("appframeDatabase.JdbcUrl", "jdbc:hsqldb:mem:radien");
        p.put("appframeDatabase.userName", "sa");
        p.put("appframeDatabase.password", "");
        p.put("openejb.exclude-include.order", "include-exclude");
        p.put("openejb.deployments.classpath.include",".*usermanagement.*");
        p.put("openejb.deployments.classpath.exclude",".*client.*");

        container = EJBContainer.createEJBContainer(p);
        final Context context = container.getContext();

        userServiceAccess = (UserServiceAccess) context.lookup("java:global/rd-ms-usermanagement//UserService");

        String sub = "270e0461-416d-4faf-af9f-d6b45619ed62";

        Page<? extends SystemUser> userPage = userServiceAccess.getAll(null, 1, 10, null, true);
        if(userPage.getTotalResults()>0) {
            uTest = userPage.getResults().get(0);
        } else {
            uTest = UserFactory.create("firstName", "lastName", "logon",
                    sub, "email@email.pt", 2L);
            userServiceAccess.save(uTest);
        }
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

    @Test
    public void testGetUserIdBySub() {
        Long id = userServiceAccess.getUserId(uTest.getSub());
        assertNotNull(id);
        assertEquals(id, uTest.getId());

        id = userServiceAccess.getUserId("non-existent-sub");
        assertNull(id);
    }


    /**
     * Add user test.
     * Will create and save the user.
     * Expected result: Success.
     * Tested methods: void save(User user)
     *
     * @throws UniquenessConstraintException in case of requested action is not well constructed
     * @throws NotFoundException in case no user was found after the save in the DB
     */
    @Test
    public void testAddUser() throws UserNotFoundException {
        SystemUser result = userServiceAccess.get(uTest.getId());
        assertNotNull(result);
    }

    /**
     * Add user test with duplicated Email.
     * Will create and save the user, with an already existent email.
     * Expected result: Throw treated exception Error 101 Message There is more than one user with the same Email.
     * Tested methods: void save(User user)
     */
    @Test
    public void testAddDuplicatedUserEmail() {
        User u = UserFactory.create("testAddFirstName", "testAddLastName", "testAddLogon",
                null, "email@email.pt", 2L);
        Exception exception = assertThrows(UniquenessConstraintException.class, () -> userServiceAccess.save(u));
        String expectedMessage = GenericErrorCodeMessage.DUPLICATED_FIELD.toString("Email Address");
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    /**
     * Add user test with duplicated Logon.
     * Will create and save the user, with an already existent logon.
     * Expected result: Throw treated exception Error 103 Message There is more than one user with the same Logon.
     * Tested methods: void save(User user)
     */
    @Test
    public void testAddDuplicatedUserLogon() {
        User u = UserFactory.create("testAddFirstName", "testAddLastName", "logon", null,
                "testAddEmail@testAddEmail.pt", 2L);
        Exception exception = assertThrows(UniquenessConstraintException.class, () -> userServiceAccess.save(u));
        String expectedMessage = GenericErrorCodeMessage.DUPLICATED_FIELD.toString("Logon");
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testGetUserList() throws UserNotFoundException, UniquenessConstraintException {
        User u = UserFactory.create("testGetIdFirstName2", "testGetIdLastName2", "testGetIdLogon2",
                null, "testGetIdEmail2@testGetIdEmail2.pt", 2L);
        userServiceAccess.save(u);
        List<? extends SystemUser> result = userServiceAccess.getUserList();
        assertNotNull(result);
    }

    /**
     * Gets user using the PK (id).
     * Will create a new user, save it into the DB and retrieve the specific user using the ID.
     * Expected result: will return the correct inserted user.
     * Tested methods: SystemUser get(Long userId)
     *
     * @throws UniquenessConstraintException in case of requested action is not well constructed
     * @throws NotFoundException in case no user was found after the save in the DB
     */
    @Test
    public void testGetById() throws UserNotFoundException, UniquenessConstraintException {
        User u = UserFactory.create("testGetIdFirstName", "testGetIdLastName", "testGetIdLogon",
                null, "testGetIdEmail@testGetIdEmail.pt", 2L);
        userServiceAccess.save(u);
        SystemUser result = userServiceAccess.get(u.getId());
        assertNotNull(result);
        assertEquals(u.getFirstname(), result.getFirstname());
        assertEquals(u.getLastname(), result.getLastname());
        assertEquals(u.getUserEmail(), result.getUserEmail());
    }

    /**
     * Gets multiple users using a list of PK (id).
     * Will create 2 new users, save them into the DB and retrieve the specific users using the ID's.
     * Expected result: will return the correct number of inserted user.
     * Tested methods: List<SystemUser> get(List<Long> userIds)
     *
     * @throws UniquenessConstraintException in case of requested action is not well constructed
     */
    @Test
    public void testGetByListOfIds() throws UniquenessConstraintException, UserNotFoundException {
        User u1 = UserFactory.create("testGetByListOfIdsFirstName1", "testGetByListOfIdsLastName1",
                "testGetByListOfIdsLogon1", null,
                "testGetByListOfIdsEmail1@testGetByListOfIdsEmail1.pt", 2L);
        userServiceAccess.save(u1);

        User u2 = UserFactory.create("testGetByListOfIdsFirstName2", "testGetByListOfIdsLastName2",
                "testGetByListOfIdsLogon2", null,
                "testGetByListOfIdsEmail2@testGetByListOfIdsEmail2.pt", 2L);
        userServiceAccess.save(u2);

        List<Long> userIds = Arrays.asList(u1.getId(), u2.getId());
        List<SystemUser> result = userServiceAccess.get(userIds);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void testGetByEmptyListOfIds() throws UniquenessConstraintException {

        List<SystemUser> result = userServiceAccess.get(new ArrayList<>());
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Deletes inserted user using the PK (id).
     * Will create a new user, save it into the DB and delete it after using the specific ID.
     * Expected result: will return null when retrieving the user.
     * Tested methods: void delete(Long userId)
     *
     * @throws UniquenessConstraintException in case of requested action is not well constructed
     * @throws NotFoundException in case no user was found after the save in the DB
     */
    @Test
    public void testDeleteById() throws UserNotFoundException {
        SystemUser result = userServiceAccess.get(uTest.getId());
        assertNotNull(result);
        assertEquals(uTest.getUserEmail(), result.getUserEmail());
        userServiceAccess.delete(uTest.getId());
        boolean success = false;
        try {
            result = userServiceAccess.get(uTest.getId());
        } catch (UserNotFoundException e){
            success = true;
        }
        assertTrue(success);
    }

    /**
     * Deletes multiple users using a list of PK (id).
     * Will create 3 new users, save them into the DB and delete the specific 2 users using the ID's.
     * Expected result: will delete the correct number of inserted user and return only one of them (the not deleted
     * one).
     * Tested methods: void delete(Collection<Long> userIds)
     *
     * @throws UniquenessConstraintException in case of requested action is not well constructed
     * @throws NotFoundException in case no user was found after the save in the DB
     */
    @Test
    public void testDeleteByListOfIds() throws UserNotFoundException, UniquenessConstraintException {
        User u1 = UserFactory.create("testDeleteByListOfIdsFirstName1", "testDeleteByListOfIdsLastName1",
                "testDeleteByListOfIdsLogon1", null,
                "testDeleteByListOfIdsEmail1@testDeleteByListOfIdsEmail1.pt", 2L);
        userServiceAccess.save(u1);

        User u2 = UserFactory.create("testDeleteByListOfIdsFirstName2", "testDeleteByListOfIdsLastName2",
                "testDeleteByListOfIdsLogon2", null,
                "testDeleteByListOfIdsEmail2@testDeleteByListOfIdsEmail2.pt", 2L);
        userServiceAccess.save(u2);

        User u3 = UserFactory.create("testDeleteByListOfIdsFirstName3", "testDeleteByListOfIdsLastName3",
                "testDeleteByListOfIdsLogon3", null,
                "testDeleteByListOfIdsEmail3@testDeleteByListOfIdsEmail3.pt", 2L);
        userServiceAccess.save(u3);

        List<Long> userIds = Arrays.asList(u1.getId(), u2.getId());
        userServiceAccess.delete(userIds);
        List<SystemUser> result = userServiceAccess.get(userIds);
        assertEquals(0, result.size());
        SystemUser resultExistentOne = userServiceAccess.get(u3.getId());
        assertNotNull(result);
        assertEquals(u3.getFirstname(), resultExistentOne.getFirstname());
        assertEquals(u3.getLastname(), resultExistentOne.getLastname());
        assertEquals(u3.getUserEmail(), resultExistentOne.getUserEmail());
    }


    /**
     * Test updates the user information. Should be a success in this test case, and the last name, the logon
     * and the user email from the user one, should have been updated to the user three information.
     * @throws Exception in case of user to be updated not found
     */
    @Test
    public void testUpdateSuccess() throws Exception {
        SystemUser u1 = UserFactory.create("testUpdateFirstName1", "testUpdateLastName1",
                "testUpdateLogon1", "testeUpdateSub1", "testeUpdateEmail1@testeUpdate1.pt", 2L);
        userServiceAccess.save(u1);

        SystemUser u2 = UserFactory.create("testUpdateFirstName2", "testUpdateLastName2",
                "testUpdateLogon2", "testeUpdateSub2", "testeUpdateEmail2@testeUpdate2.pt", 2L);
        userServiceAccess.save(u2);

        SystemUser u3 = UserFactory.create("testUpdateFirstName1", "testUpdateLastName3",
                "testUpdateLogon3", "testeUpdateSub3", "testeUpdateEmail3@testeUpdate3.pt", 2L);

        u3.setId(u1.getId());

        userServiceAccess.save(u3);

        u1 = userServiceAccess.get(u1.getId());

        assertEquals(u1.getFirstname(), u3.getFirstname());
        assertEquals(u1.getLastname(), u3.getLastname());
        assertEquals(u1.getLogon(), u3.getLogon());
        assertEquals(u1.getSub(), u3.getSub());
        assertEquals(u1.getUserEmail(), u3.getUserEmail());

        SystemUser u4 = UserFactory.create("testUpdateFirstName4", "testUpdateLastName4",
                "testUpdateLogon4", "testeUpdateSub4","testeUpdateEmail4@testeUpdate4.pt", 2L);

        u4.setId(u1.getId());

        userServiceAccess.save(u4);

        u1 = userServiceAccess.get(u1.getId());

        assertEquals(u1.getFirstname(), u4.getFirstname());
        assertEquals(u1.getLastname(), u4.getLastname());
        assertEquals(u1.getLogon(), u4.getLogon());
        assertEquals(u1.getSub(), u4.getSub());
        assertEquals(u1.getUserEmail(), u4.getUserEmail());
    }

    @Test
    public void testUpdateFailureMultipleRecords() throws Exception {
        User u1 = UserFactory.create("testUpdateFailureMultipleRecordsFirstName1",
                "testUpdateFailureMultipleRecordsLastName1", "testUpdateFailureMultipleRecordsLogon1",
                "testUpdateFailureMultipleRecordsSub1",
                "testUpdateFailureMultipleRecordsEmail1@testUpdateFailureMultipleRecordsEmail1.pt", 2L);
        userServiceAccess.save(u1);

        User u2 = UserFactory.create("testUpdateFailureMultipleRecordsFirstName2",
                "testUpdateFailureMultipleRecordsLastName2", "testUpdateFailureMultipleRecordsLogon2",
                "testUpdateFailureMultipleRecordsSub2",
                "testUpdateFailureMultipleRecordsEmail2@testUpdateFailureMultipleRecordsEmail2.pt", 2L);
        userServiceAccess.save(u2);

        User u3 = UserFactory.create("testUpdateFailureMultipleRecordsFirstName3",
                "testUpdateFailureMultipleRecordsLastName3", "testUpdateFailureMultipleRecordsLogon3",
                "testUpdateFailureMultipleRecordsSub3",
                "testUpdateFailureMultipleRecordsEmail3@testUpdateFailureMultipleRecordsEmail3.pt", 2L);
        userServiceAccess.save(u3);

        User u4 = UserFactory.create("testUpdateFailureMultipleRecordsFirstName4",
                "testUpdateFailureMultipleRecordsLastName4", "testUpdateFailureMultipleRecordsLogon3",
                "testUpdateFailureMultipleRecordsSub4",
                "testUpdateFailureMultipleRecordsEmail2@testUpdateFailureMultipleRecordsEmail2.pt", 2L);

        Exception exceptionEmailAndLogon = assertThrows(Exception.class, () -> userServiceAccess.save(u4));
        String actualMessageEmailAndLogon = exceptionEmailAndLogon.getMessage();
        String expectedMessageEmailAndLogon = GenericErrorCodeMessage.DUPLICATED_FIELD.toString("Email Address and Logon");
        assertTrue(actualMessageEmailAndLogon.contains(expectedMessageEmailAndLogon));

    }

    /**
     * Test updates the user information. Should be a failure in this test case and no information should be updated,
     * since that we are updating user one with the information from user three, but using a duplicated email address.
     * @throws Exception in case of user to be updated not found
     */
    @Test
    public void testUpdateFailureDuplicatedEmail() throws Exception {
        String expectedMessageEmail = GenericErrorCodeMessage.DUPLICATED_FIELD.toString("Email Address");

        User u1 = UserFactory.create("testUpdateFailureDuplicatedEmailFirstName1",
                "testUpdateFailureDuplicatedEmailLastName1", "testUpdateFailureDuplicatedEmailLogon1",
                "testUpdateFailureDuplicatedEmailSub1",
                "testUpdateFailureDuplicatedEmail1@testUpdateFailureDuplicatedEmail1.pt", 2L);
        userServiceAccess.save(u1);

        User u2 = UserFactory.create("testUpdateFailureDuplicatedEmailFirstName2",
                "testUpdateFailureDuplicatedEmailLastName2", "testUpdateFailureDuplicatedEmailLogon2",
                "testUpdateFailureDuplicatedEmailSub2",
                "testUpdateFailureDuplicatedEmail2@testUpdateFailureDuplicatedEmail2.pt", 2L);
        userServiceAccess.save(u2);

        User u3 = UserFactory.create("testUpdateFailureDuplicatedEmailFirstName1",
                "testUpdateFailureDuplicatedEmailLastName1", "testUpdateFailureDuplicatedEmailLogon3",
                "testUpdateFailureDuplicatedEmailSub1",
                "testUpdateFailureDuplicatedEmail2@testUpdateFailureDuplicatedEmail2.pt", 2L);

        Exception exceptionEmail = assertThrows(Exception.class, () -> userServiceAccess.save(u3));
        String actualMessageEmail = exceptionEmail.getMessage();
        assertTrue(actualMessageEmail.contains(expectedMessageEmail));

        User u4 = UserFactory.create("testUpdateFailureDuplicatedEmailFirstName4",
                "testUpdateFailureDuplicatedEmailLastName4", "testUpdateFailureDuplicatedEmailLogon3",
                "testUpdateFailureDuplicatedEmailSub4",
                "testUpdateFailureDuplicatedEmail2@testUpdateFailureDuplicatedEmail2.pt", 2L);

        Exception exceptionEmail2 = assertThrows(Exception.class, () -> userServiceAccess.save(u4));
        String actualMessageEmail2 = exceptionEmail2.getMessage();
        assertTrue(actualMessageEmail2.contains(expectedMessageEmail));
    }

    /**
     * Test updates the user information. Should be a failure in this test case and no information should be updated,
     * since that we are updating user one with the information from user three, but using a duplicated email address.
     * @throws Exception in case of user to be updated not found
     */
    @Test
    public void testUpdateFailureDuplicatedLogon() throws Exception {
        String expectedMessageLogon = GenericErrorCodeMessage.DUPLICATED_FIELD.toString("Logon");;

        User u1 = UserFactory.create("testUpdateFailureDuplicatedLogonFirstName1",
                "testUpdateFailureDuplicatedLogonLastName1", "testUpdateFailureDuplicatedLogonLogon1",
                "testUpdateFailureDuplicatedLogonSub1",
                "testUpdateFailureDuplicatedLogonEmail1@testUpdateFailureDuplicatedLogonEmail1.pt", 2L);
        userServiceAccess.save(u1);

        User u2 = UserFactory.create("testUpdateFailureDuplicatedLogonFirstName2",
                "testUpdateFailureDuplicatedLogonLastName2", "testUpdateFailureDuplicatedLogonLogon2",
                "testUpdateFailureDuplicatedLogonSub2",
                "testUpdateFailureDuplicatedLogonEmail2@testUpdateFailureDuplicatedLogonEmail2.pt", 2L);
        userServiceAccess.save(u2);

        User u3 = UserFactory.create("testUpdateFailureDuplicatedLogonFirstName1",
                "testUpdateFailureDuplicatedLogonLastName1", "testUpdateFailureDuplicatedLogonLogon2",
                "testUpdateFailureDuplicatedLogonSub2",
                "testUpdateFailureDuplicatedLogonEmail3@testUpdateFailureDuplicatedLogonEmail3.pt", 2L);

        Exception exceptionLogon = assertThrows(Exception.class, () -> userServiceAccess.save(u3));
        String actualMessageLogon = exceptionLogon.getMessage();
        assertTrue(actualMessageLogon.contains(expectedMessageLogon));

        User u4 = UserFactory.create("testUpdateFailureDuplicatedLogonFirstName4",
                "testUpdateFailureDuplicatedLogonLastName4", "testUpdateFailureDuplicatedLogonLogon2",
                "testUpdateFailureDuplicatedLogonSub4",
                "testUpdateFailureDuplicatedLogonEmail4@testUpdateFailureDuplicatedLogonEmail4.pt", 2L);

        Exception exceptionLogon2 = assertThrows(Exception.class, () -> userServiceAccess.save(u4));
        String actualMessageLogon2 = exceptionLogon2.getMessage();
        assertTrue(actualMessageLogon2.contains(expectedMessageLogon));
    }

    /**
     * Test updates the user information. Should be a failure in this test case and no information should be updated,
     * since that we are updating user one with the information from user three, but using a duplicated email address.
     * @throws Exception in case of user to be updated not found
     */
    @Test
    public void testUpdateFailureDuplicatedEmailAndLogon() throws Exception {
        User u1 = UserFactory.create("testUpdateFailureDuplicatedEmailAndLogonFirstName1",
                "testUpdateFailureDuplicatedEmailAndLogonLastName1", "testUpdateFailureDuplicatedEmailAndLogonLogon1",
                "testUpdateFailureDuplicatedEmailAndLogonSub1",
                "testUpdateFailureDuplicatedEmailAndLogon1@testUpdateFailureDuplicatedEmailAndLogon1.pt", 2L);
        userServiceAccess.save(u1);

        User u2 = UserFactory.create("testUpdateFailureDuplicatedEmailAndLogonFirstName2",
                "testUpdateFailureDuplicatedEmailAndLogonLastName2", "testUpdateFailureDuplicatedEmailAndLogonLogon2",
                "testUpdateFailureDuplicatedEmailAndLogonSub2",
                "testUpdateFailureDuplicatedEmailAndLogon2@testUpdateFailureDuplicatedEmailAndLogon2.pt", 2L);
        userServiceAccess.save(u2);

        User u3 = UserFactory.create("testUpdateFailureDuplicatedEmailAndLogonFirstName3",
                "testUpdateFailureDuplicatedEmailAndLogonLastName3", "testUpdateFailureDuplicatedEmailAndLogonLogon3",
                "testUpdateFailureDuplicatedEmailAndLogonSub3",
                "testUpdateFailureDuplicatedEmailAndLogon3@testUpdateFailureDuplicatedEmailAndLogon3.pt", 2L);
        userServiceAccess.save(u3);

        User u4 = UserFactory.create("testUpdateFailureDuplicatedEmailAndLogonFirstName1",
                "testUpdateFailureDuplicatedEmailAndLogonLastName1", "testUpdateFailureDuplicatedEmailAndLogonLogon2",
                "testUpdateFailureDuplicatedEmailAndLogonSub1",
                "testUpdateFailureDuplicatedEmailAndLogon2@testUpdateFailureDuplicatedEmailAndLogon2.pt", 2L);

        Exception exceptionEmailAndLogon = assertThrows(Exception.class, () -> userServiceAccess.save(u4));
        System.out.println(exceptionEmailAndLogon.toString());
        String actualMessageEmailAndLogon = exceptionEmailAndLogon.getMessage();
        String expectedMessageEmailAndLogon = GenericErrorCodeMessage.DUPLICATED_FIELD.toString("Email Address and Logon");;
        assertTrue(actualMessageEmailAndLogon.contains(expectedMessageEmailAndLogon));
    }

    @Test
    public void testGetAllSort() throws UniquenessConstraintException, UserNotFoundException {
        SystemUser userA = UserFactory.create("a", "lastName", "aGetAllSort",
                "a","aGetAllSort@email.pt", 2L);
        userServiceAccess.save(userA);
        SystemUser userB = UserFactory.create("zzz", "lastName", "bGetAllSort",
                "b","bGetAllSort@email.pt", 2L);
        userServiceAccess.save(userB);

        List<String> orderby = new ArrayList<>();
        orderby.add("firstname");

        Page<? extends SystemUser> userPage = userServiceAccess.getAll(null, 1, 10, orderby, true);

        assertTrue(userPage.getTotalResults()>=2);

        assertEquals("a",userPage.getResults().get(0).getFirstname());

        userPage = userServiceAccess.getAll(null, 1, 10, orderby, false);
        assertTrue(userPage.getTotalResults()>=2);
        assertEquals("zzz",userPage.getResults().get(0).getFirstname());

        Page<? extends SystemUser> userPageWhere = userServiceAccess.getAll("aGetAllSort@email.pt", 1, 10, null, true);
        assertTrue(userPageWhere.getTotalResults() == 1);

        assertEquals("a",userPageWhere.getResults().get(0).getFirstname());
    }
    @Test
    public void testGetByIsExactOrLogical() throws UniquenessConstraintException, UserNotFoundException {
        SystemUser testById1 = UserFactory.create("zz", "lastName", "zz",
                "zz","zz@b.pt", 1L);

        SystemUser testById2 = UserFactory.create("aa", "lastName", "aa",
                "aa","aa@b.pt", 1L);

        SystemUser testById3 = UserFactory.create("aabb", "lastName", "aabb",
                "aabb","aabb@b.pt", 1L);

        userServiceAccess.save(testById1);
        userServiceAccess.save(testById2);
        userServiceAccess.save(testById3);

        List<? extends SystemUser> usersAnd = userServiceAccess.getUsers(new UserSearchFilter("zz","zz@b.pt","zz",true,true));
        assertEquals(1,usersAnd.size());

        List<? extends SystemUser> usersOr = userServiceAccess.getUsers(new UserSearchFilter("aa","aa@b.pt","zz",true,false));
        assertEquals(2,usersOr.size());

        List<? extends SystemUser> usersNotExact = userServiceAccess.getUsers(new UserSearchFilter("aa","aa","aa",false,true));
        assertEquals(2,usersNotExact.size());
    }


    @Test
    public void testBatchAllElementsInserted() {
        List<User> users = new ArrayList<>();
        int size = 100;
        for (int i=1; i<=size; i++) {
            users.add(UserFactory.create("userb",
                    String.valueOf(i),
                    String.format("userb.%d", i),
                    String.format("sub%d", i),
                    String.format("userb.%d@emmail.pt", i),
                    1L));
        }
        BatchSummary batchSummary = userServiceAccess.create(users);
        assertNotNull(batchSummary);
        assertEquals(batchSummary.getTotalProcessed(), size);
        assertEquals(batchSummary.getTotal(), batchSummary.getTotalProcessed());
        assertEquals(0, batchSummary.getTotalNonProcessed());
        assertNotNull(batchSummary.getNonProcessedItems());
        assertEquals(0, batchSummary.getNonProcessedItems().size());

        Page<? extends SystemUser> page = userServiceAccess.getAll("userb.%", 1, 200, null, true);
        assertNotNull(page);
        assertEquals(batchSummary.getTotalProcessed(), page.getTotalResults());
    }

    @Test
    public void testBatchNotAllElementsInserted() {
        List<User> users = new ArrayList<>();
        int firstSetSize = 4;
        for (int i=1; i<=firstSetSize; i++) {
            users.add(UserFactory.create("userbatch",
                    String.valueOf(i),
                    String.format("userbatch.%d", i),
                    String.format("userbatch%d", i),
                    String.format("userbatch.%d@emmail.pt", i),
                    1L));
        }
        BatchSummary batchSummary = userServiceAccess.create(users);
        assertNotNull(batchSummary);
        assertEquals(batchSummary.getTotal(), firstSetSize);
        assertEquals(batchSummary.getTotalProcessed(), firstSetSize);
        assertEquals(0, batchSummary.getTotalNonProcessed());
        assertNotNull(batchSummary.getNonProcessedItems());
        assertEquals(0, batchSummary.getNonProcessedItems().size());

        // Preparing a new set
        users.clear();
        int secondSetSize = 20;
        for (int i=1; i<=secondSetSize; i++) {
            users.add(UserFactory.create("userbatch",
                    String.valueOf(i+secondSetSize),
                    String.format("userbatch.%d", i+secondSetSize),
                    String.format("userbatch%d", i+secondSetSize),
                    String.format("userbatch.%d@emmail.pt", i+secondSetSize),
                    1L));
        }

        // Setting repeated info
        users.get(5).setUserEmail("userbatch.1@emmail.pt");
        users.get(3).setLogon("userbatch.2");
        users.get(8).setUserEmail("userbatch.1@emmail.pt");
        users.get(9).setLogon("userbatch.3");
        users.get(10).setLogon("userbatch.21");
        users.get(10).setUserEmail("userbatch.22@emmail.pt");
        users.get(10).setSub("userbatch24");

        // Adding 2 issues into 5th element (info already inserted
        users.get(4).setLogon("userbatch.4");
        users.get(4).setUserEmail("userbatch.1@emmail.pt");

        batchSummary = userServiceAccess.create(users);
        assertNotNull(batchSummary);
        assertEquals(batchSummary.getTotal(), secondSetSize);
        assertEquals(batchSummary.getTotalProcessed(), secondSetSize-6);
        assertEquals(6, batchSummary.getTotalNonProcessed());
        assertNotNull(batchSummary.getNonProcessedItems());
        assertEquals(6, batchSummary.getNonProcessedItems().size());

        // Retrieving issues for 5th element
        Optional<DataIssue> issue = batchSummary.getNonProcessedItems().stream().filter(
                dataIssue -> dataIssue.getRowId() == 5l).findFirst();
        assertTrue(issue.isPresent());
        assertTrue(issue.get().getRowId() == 5l);
        assertTrue(issue.get().getReasons().size() == 2);

        // Retrieving issue for 11th element
        issue = batchSummary.getNonProcessedItems().stream().filter(
                dataIssue -> dataIssue.getRowId() == 11l).findFirst();
        assertTrue(issue.isPresent());
        assertTrue(issue.get().getRowId() == 11l);
        assertTrue(issue.get().getReasons().size() == 3);

        Page<? extends SystemUser> page = userServiceAccess.getAll("userbatch%", 1, 200, null, true);
        assertNotNull(page);
        assertEquals((firstSetSize + secondSetSize) - 6, page.getTotalResults());

    }
}