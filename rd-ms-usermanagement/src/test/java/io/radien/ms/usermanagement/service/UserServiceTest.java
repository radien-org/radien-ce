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
import io.radien.exception.UserNotFoundException;
import io.radien.ms.usermanagement.client.exceptions.InvalidRequestException;
import io.radien.ms.usermanagement.client.exceptions.NotFoundException;
import io.radien.ms.usermanagement.entities.User;
import io.radien.ms.usermanagement.legacy.UserFactory;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 * @author Nuno Santana
 * @author Bruno Gama
 */
public class UserServiceTest {

    protected static final Logger log = LoggerFactory.getLogger(UserServiceTest.class);

    Properties p;
    UserService userService;
    SystemUser uTest;

    public UserServiceTest() throws Exception {
        p = new Properties();
        p.put("appframeDatabase", "new://Resource?type=DataSource");
        p.put("appframeDatabase.JdbcDriver", "org.hsqldb.jdbcDriver");
        p.put("appframeDatabase.JdbcUrl", "jdbc:hsqldb:mem:radien");
        p.put("appframeDatabase.userName", "sa");
        p.put("appframeDatabase.password", "");

        final Context context = EJBContainer.createEJBContainer(p).getContext();

        userService = (UserService) context.lookup("java:global/rd-ms-usermanagement//UserService");

        String sub = "270e0461-416d-4faf-af9f-d6b45619ed62";
        Page<? extends SystemUser> userPage = userService.getAll(null, 1, 10, null, true);
        if(userPage.getTotalResults()>0) {
            uTest = userPage.getResults().get(0);
        } else {
            uTest = UserFactory.create("firstName", "lastName", "logon",
                    sub, "email@email.pt", 2L);
            userService.save(uTest);
        }
    }

    /**
     * Add user test.
     * Will create and save the user.
     * Expected result: Success.
     * Tested methods: void save(User user)
     *
     * @throws InvalidRequestException in case of requested action is not well constructed
     * @throws NotFoundException in case no user was found after the save in the DB
     */
    @Test
    public void testAddUser() throws UserNotFoundException {
        SystemUser result = userService.get(uTest.getId());
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
        Exception exception = assertThrows(InvalidRequestException.class, () -> userService.save(u));
        String expectedMessage = "{\"code\":101, \"key\":\"error.duplicated.field\", \"message\":\"There is more than" +
                " one resource with the same value for the field: Email Address\"}";
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
        Exception exception = assertThrows(InvalidRequestException.class, () -> userService.save(u));
        String expectedMessage = "{\"code\":101, \"key\":\"error.duplicated.field\", \"message\":\"There is more than" +
                " one resource with the same value for the field: Logon\"}";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    /**
     * Gets user using the PK (id).
     * Will create a new user, save it into the DB and retrieve the specific user using the ID.
     * Expected result: will return the correct inserted user.
     * Tested methods: SystemUser get(Long userId)
     *
     * @throws InvalidRequestException in case of requested action is not well constructed
     * @throws NotFoundException in case no user was found after the save in the DB
     */
    @Test
    public void testGetById() throws UserNotFoundException {
        User u = UserFactory.create("testGetIdFirstName", "testGetIdLastName", "testGetIdLogon",
                null, "testGetIdEmail@testGetIdEmail.pt", 2L);
        userService.save(u);
        SystemUser result = userService.get(u.getId());
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
     * @throws InvalidRequestException in case of requested action is not well constructed
     */
    @Test
    public void testGetByListOfIds() throws InvalidRequestException {
        User u1 = UserFactory.create("testGetByListOfIdsFirstName1", "testGetByListOfIdsLastName1",
                "testGetByListOfIdsLogon1", null,
                "testGetByListOfIdsEmail1@testGetByListOfIdsEmail1.pt", 2L);
        userService.save(u1);

        User u2 = UserFactory.create("testGetByListOfIdsFirstName2", "testGetByListOfIdsLastName2",
                "testGetByListOfIdsLogon2", null,
                "testGetByListOfIdsEmail2@testGetByListOfIdsEmail2.pt", 2L);
        userService.save(u2);

        List<Long> userIds = Arrays.asList(u1.getId(), u2.getId());
        List<SystemUser> result = userService.get(userIds);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void testGetByEmptyListOfIds() throws InvalidRequestException {

        List<SystemUser> result = userService.get(new ArrayList<>());
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Deletes inserted user using the PK (id).
     * Will create a new user, save it into the DB and delete it after using the specific ID.
     * Expected result: will return null when retrieving the user.
     * Tested methods: void delete(Long userId)
     *
     * @throws InvalidRequestException in case of requested action is not well constructed
     * @throws NotFoundException in case no user was found after the save in the DB
     */
    @Test
    public void testDeleteById() throws UserNotFoundException {
        SystemUser result = userService.get(uTest.getId());
        assertNotNull(result);
        assertEquals(uTest.getUserEmail(), result.getUserEmail());
        userService.delete(uTest.getId());
        result = userService.get(uTest.getId());
        assertNull(result);
    }

    /**
     * Deletes multiple users using a list of PK (id).
     * Will create 3 new users, save them into the DB and delete the specific 2 users using the ID's.
     * Expected result: will delete the correct number of inserted user and return only one of them (the not deleted
     * one).
     * Tested methods: void delete(Collection<Long> userIds)
     *
     * @throws InvalidRequestException in case of requested action is not well constructed
     * @throws NotFoundException in case no user was found after the save in the DB
     */
    @Test
    public void testDeleteByListOfIds() throws UserNotFoundException {
        User u1 = UserFactory.create("testDeleteByListOfIdsFirstName1", "testDeleteByListOfIdsLastName1",
                "testDeleteByListOfIdsLogon1", null,
                "testDeleteByListOfIdsEmail1@testDeleteByListOfIdsEmail1.pt", 2L);
        userService.save(u1);

        User u2 = UserFactory.create("testDeleteByListOfIdsFirstName2", "testDeleteByListOfIdsLastName2",
                "testDeleteByListOfIdsLogon2", null,
                "testDeleteByListOfIdsEmail2@testDeleteByListOfIdsEmail2.pt", 2L);
        userService.save(u2);

        User u3 = UserFactory.create("testDeleteByListOfIdsFirstName3", "testDeleteByListOfIdsLastName3",
                "testDeleteByListOfIdsLogon3", null,
                "testDeleteByListOfIdsEmail3@testDeleteByListOfIdsEmail3.pt", 2L);
        userService.save(u3);

        List<Long> userIds = Arrays.asList(u1.getId(), u2.getId());
        userService.delete(userIds);
        List<SystemUser> result = userService.get(userIds);
        assertEquals(0, result.size());
        SystemUser resultExistentOne = userService.get(u3.getId());
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
        User u1 = UserFactory.create("testUpdateFirstName1", "testUpdateLastName1", "testUpdateLogon1", "testeUpdateSub1", "testeUpdateEmail1@testeUpdate1.pt", 2L);
        userService.save(u1);

        User u2 = UserFactory.create("testUpdateFirstName2", "testUpdateLastName2", "testUpdateLogon2", "testeUpdateSub2", "testeUpdateEmail2@testeUpdate2.pt", 2L);
        userService.save(u2);

        User u3 = UserFactory.create("testUpdateFirstName1", "testUpdateLastName3", "testUpdateLogon3", "testeUpdateSub3", "testeUpdateEmail3@testeUpdate3.pt", 2L);

        userService.save(u3);

        assertEquals(u1.getFirstname(), u3.getFirstname());
        assertEquals(u1.getLastname(), u3.getLastname());
        assertEquals(u1.getLogon(), u3.getLogon());
        assertNotEquals(u1.getSub(), u3.getSub());
        assertEquals(u1.getUserEmail(), u3.getUserEmail());

        User u4 = UserFactory.create("testUpdateFirstName1", "testUpdateLastName3", "testUpdateLogon3", "testeUpdateSub3", "testeUpdateEmail3@testeUpdate3.pt", 2L);

        userService.save(u4);

        assertEquals(u1.getFirstname(), u4.getFirstname());
        assertEquals(u1.getLastname(), u4.getLastname());
        assertEquals(u1.getLogon(), u4.getLogon());
        assertNotEquals(u1.getSub(), u4.getSub());
        assertEquals(u1.getUserEmail(), u4.getUserEmail());
    }

    @Test
    public void testUpdateFailureMultipleRecords() throws Exception {
        User u1 = UserFactory.create("testUpdateFailureMultipleRecordsFirstName1", "testUpdateFailureMultipleRecordsLastName1", "testUpdateFailureMultipleRecordsLogon1", "testUpdateFailureMultipleRecordsSub1", "testUpdateFailureMultipleRecordsEmail1@testUpdateFailureMultipleRecordsEmail1.pt", 2L);
        userService.save(u1);

        User u2 = UserFactory.create("testUpdateFailureMultipleRecordsFirstName2", "testUpdateFailureMultipleRecordsLastName2", "testUpdateFailureMultipleRecordsLogon2", "testUpdateFailureMultipleRecordsSub2", "testUpdateFailureMultipleRecordsEmail2@testUpdateFailureMultipleRecordsEmail2.pt", 2L);
        userService.save(u2);

        User u3 = UserFactory.create("testUpdateFailureMultipleRecordsFirstName3", "testUpdateFailureMultipleRecordsLastName3", "testUpdateFailureMultipleRecordsLogon3", "testUpdateFailureMultipleRecordsSub3", "testUpdateFailureMultipleRecordsEmail3@testUpdateFailureMultipleRecordsEmail3.pt", 2L);
        userService.save(u3);

        User u4 = UserFactory.create("testUpdateFailureMultipleRecordsFirstName4", "testUpdateFailureMultipleRecordsLastName4", "testUpdateFailureMultipleRecordsLogon3", "testUpdateFailureMultipleRecordsSub4", "testUpdateFailureMultipleRecordsEmail2@testUpdateFailureMultipleRecordsEmail2.pt", 2L);

        Exception exceptionEmailAndLogon = assertThrows(Exception.class, () -> userService.save(u4));
        String actualMessageEmailAndLogon = exceptionEmailAndLogon.getMessage();
        String expectedMessageEmailAndLogon = "{\"code\":101, \"key\":\"error.duplicated.field\", \"message\":\"There is more than one resource with the same value for the field: Email address and Logon\"}";
        assertTrue(actualMessageEmailAndLogon.contains(expectedMessageEmailAndLogon));

    }

    /**
     * Test updates the user information. Should be a failure in this test case and no information should be updated,
     * since that we are updating user one with the information from user three, but using a duplicated email address.
     * @throws Exception in case of user to be updated not found
     */
    @Test
    public void testUpdateFailureDuplicatedEmail() throws Exception {
        String expectedMessageEmail = "{\"code\":101, \"key\":\"error.duplicated.field\", \"message\":\"There is more than" +
                " one resource with the same value for the field: Email address\"}";

        User u1 = UserFactory.create("testUpdateFailureDuplicatedEmailFirstName1", "testUpdateFailureDuplicatedEmailLastName1", "testUpdateFailureDuplicatedEmailLogon1", "testUpdateFailureDuplicatedEmailSub1", "testUpdateFailureDuplicatedEmail1@testUpdateFailureDuplicatedEmail1.pt", 2L);
        userService.save(u1);

        User u2 = UserFactory.create("testUpdateFailureDuplicatedEmailFirstName2", "testUpdateFailureDuplicatedEmailLastName2", "testUpdateFailureDuplicatedEmailLogon2", "testUpdateFailureDuplicatedEmailSub2", "testUpdateFailureDuplicatedEmail2@testUpdateFailureDuplicatedEmail2.pt", 2L);
        userService.save(u2);

        User u3 = UserFactory.create("testUpdateFailureDuplicatedEmailFirstName1", "testUpdateFailureDuplicatedEmailLastName1", "testUpdateFailureDuplicatedEmailLogon1", "testUpdateFailureDuplicatedEmailSub1", "testUpdateFailureDuplicatedEmail2@testUpdateFailureDuplicatedEmail2.pt", 2L);

        Exception exceptionEmail = assertThrows(Exception.class, () -> userService.save(u3));
        String actualMessageEmail = exceptionEmail.getMessage();
        assertTrue(actualMessageEmail.contains(expectedMessageEmail));

        User u4 = UserFactory.create("testUpdateFailureDuplicatedEmailFirstName4", "testUpdateFailureDuplicatedEmailLastName4", "testUpdateFailureDuplicatedEmailLogon3", "testUpdateFailureDuplicatedEmailSub4", "testUpdateFailureDuplicatedEmail2@testUpdateFailureDuplicatedEmail2.pt", 2L);

        Exception exceptionEmail2 = assertThrows(Exception.class, () -> userService.save(u4));
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
        String expectedMessageLogon = "{\"code\":101, \"key\":\"error.duplicated.field\", \"message\":\"There is more than" +
                " one resource with the same value for the field: Logon\"}";

        User u1 = UserFactory.create("testUpdateFailureDuplicatedLogonFirstName1", "testUpdateFailureDuplicatedLogonLastName1", "testUpdateFailureDuplicatedLogonLogon1", "testUpdateFailureDuplicatedLogonSub1", "testUpdateFailureDuplicatedLogonEmail1@testUpdateFailureDuplicatedLogonEmail1.pt", 2L);
        userService.save(u1);

        User u2 = UserFactory.create("testUpdateFailureDuplicatedLogonFirstName2", "testUpdateFailureDuplicatedLogonLastName2", "testUpdateFailureDuplicatedLogonLogon2", "testUpdateFailureDuplicatedLogonSub2", "testUpdateFailureDuplicatedLogonEmail2@testUpdateFailureDuplicatedLogonEmail2.pt", 2L);
        userService.save(u2);

        User u3 = UserFactory.create("testUpdateFailureDuplicatedLogonFirstName1", "testUpdateFailureDuplicatedLogonLastName1", "testUpdateFailureDuplicatedLogonLogon2", "testUpdateFailureDuplicatedLogonSub1", "testUpdateFailureDuplicatedLogonEmail1@testUpdateFailureDuplicatedLogonEmail1.pt", 2L);

        Exception exceptionLogon = assertThrows(Exception.class, () -> userService.save(u3));
        String actualMessageLogon = exceptionLogon.getMessage();
        assertTrue(actualMessageLogon.contains(expectedMessageLogon));

        User u4 = UserFactory.create("testUpdateFailureDuplicatedLogonFirstName4", "testUpdateFailureDuplicatedLogonLastName4", "testUpdateFailureDuplicatedLogonLogon2", "testUpdateFailureDuplicatedLogonSub4", "testUpdateFailureDuplicatedLogonEmail4@testUpdateFailureDuplicatedLogonEmail4.pt", 2L);

        Exception exceptionLogon2 = assertThrows(Exception.class, () -> userService.save(u4));
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
        User u1 = UserFactory.create("testUpdateFailureDuplicatedEmailAndLogonFirstName1", "testUpdateFailureDuplicatedEmailAndLogonLastName1", "testUpdateFailureDuplicatedEmailAndLogonLogon1", "testUpdateFailureDuplicatedEmailAndLogonSub1", "testUpdateFailureDuplicatedEmailAndLogon1@testUpdateFailureDuplicatedEmailAndLogon1.pt", 2L);
        userService.save(u1);

        User u2 = UserFactory.create("testUpdateFailureDuplicatedEmailAndLogonFirstName2", "testUpdateFailureDuplicatedEmailAndLogonLastName2", "testUpdateFailureDuplicatedEmailAndLogonLogon2", "testUpdateFailureDuplicatedEmailAndLogonSub2", "testUpdateFailureDuplicatedEmailAndLogon2@testUpdateFailureDuplicatedEmailAndLogon2.pt", 2L);
        userService.save(u2);

        User u3 = UserFactory.create("testUpdateFailureDuplicatedEmailAndLogonFirstName3", "testUpdateFailureDuplicatedEmailAndLogonLastName3", "testUpdateFailureDuplicatedEmailAndLogonLogon3", "testUpdateFailureDuplicatedEmailAndLogonSub3", "testUpdateFailureDuplicatedEmailAndLogon3@testUpdateFailureDuplicatedEmailAndLogon3.pt", 2L);
        userService.save(u3);

        User u4 = UserFactory.create("testUpdateFailureDuplicatedEmailAndLogonFirstName1", "testUpdateFailureDuplicatedEmailAndLogonLastName1", "testUpdateFailureDuplicatedEmailAndLogonLogon2", "testUpdateFailureDuplicatedEmailAndLogonSub1", "testUpdateFailureDuplicatedEmailAndLogon2@testUpdateFailureDuplicatedEmailAndLogon2.pt", 2L);

        Exception exceptionEmailAndLogon = assertThrows(Exception.class, () -> userService.save(u4));
        System.out.println(exceptionEmailAndLogon.toString());
        String actualMessageEmailAndLogon = exceptionEmailAndLogon.getMessage();
        String expectedMessageEmailAndLogon = "{\"code\":101, \"key\":\"error.duplicated.field\", \"message\":\"There is more than" +
                " one resource with the same value for the field: Email address and Logon\"}";
        assertTrue(actualMessageEmailAndLogon.contains(expectedMessageEmailAndLogon));
    }

    public void testGetAllSort() throws InvalidRequestException {
        uTest = UserFactory.create("a", "lastName", "a",
                "a", "a@email.pt", 2L);
        userService.save(uTest);
        uTest = UserFactory.create("zzz", "lastName", "b",
                "b", "b@email.pt", 2L);
        userService.save(uTest);
        Page<? extends SystemUser> userPage = userService.getAll(null, 1, 10, null, true);
        assertTrue(userPage.getTotalResults()>=2);
        assertEquals("a",userPage.getResults().get(0).getFirstname());

        userPage = userService.getAll(null, 1, 10, null, true);
        assertTrue(userPage.getTotalResults()>=2);
        assertEquals("zzz",userPage.getResults().get(0).getFirstname());

    }
}
