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
package io.radien.ms.usermanagement.datalayer;

import io.radien.api.entity.Page;
import io.radien.api.model.user.SystemUser;
import io.radien.api.service.batch.BatchSummary;
import io.radien.api.service.batch.DataIssue;
import io.radien.api.service.user.UserServiceAccess;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.NotFoundException;
import io.radien.exception.SystemException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.exception.UserNotFoundException;
import io.radien.ms.usermanagement.client.entities.PagedUserSearchFilter;
import io.radien.ms.usermanagement.client.entities.UserSearchFilter;
import io.radien.ms.usermanagement.client.services.UserFactory;
import io.radien.ms.usermanagement.entities.UserEntity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/**
 * Tenant Role User Service rest requests and responses with access into the db
 * {@link UserService}
 *
 * @author Nuno Santana
 * @author Bruno Gama
 */
public class UserServiceTest {

    static Properties p;
    static UserServiceAccess userServiceAccess;
    static SystemUser uTest;
    static EJBContainer container;

    /**
     * Method before test preparation
     */
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
        p.put("openejb.cdi.activated-on-ejb", "false");

        container = EJBContainer.createEJBContainer(p);
        final Context context = container.getContext();

        userServiceAccess = (UserServiceAccess) context.lookup("java:global/rd-ms-usermanagement//UserService");

        String sub = "270e0461-416d-4faf-af9f-d6b45619ed62";

        Page<? extends SystemUser> userPage = userServiceAccess.getAll(null, 1, 10, null, true);
        if(userPage.getTotalResults()>0) {
            uTest = userPage.getResults().get(0);
        } else {
            uTest = new UserEntity(UserFactory.create("firstName", "lastName", "logon",
                    sub, "email@email.pt", "951",2L,false));
            userServiceAccess.create(uTest);
        }
    }

    /**
     * Injection method before starting the tests and data preparation
     * @throws NamingException in case of naming injection value exception
     */
    @Before
    public void inject() throws NamingException {
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
     * Test to attempt to get a user id by searching for his subject
     */
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
     * @throws UserNotFoundException in case no user was found after the save in the DB
     */
    @Test
    public void testAddUser() throws UserNotFoundException {
        SystemUser result = userServiceAccess.get(uTest.getId());
        assertNotNull(result);
    }

    /**
     * Test method for {@link UserService#get(List)}
     */
    @Test
    public void testGet() {
        List<SystemUser>  result = userServiceAccess.get((List<Long>) null);
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
        UserEntity u = new UserEntity(UserFactory.create("testAddFirstName", "testAddLastName", "testAddLogon",
                null, "email@email.pt","951", 2L, false));
        Exception exception = assertThrows(UniquenessConstraintException.class, () -> userServiceAccess.create(u));
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
        UserEntity u = new UserEntity(UserFactory.create("testAddFirstName", "testAddLastName", "logon", null,
                "testAddEmail@testAddEmail.pt", "951",2L,false));
        Exception exception = assertThrows(UniquenessConstraintException.class, () -> userServiceAccess.create(u));
        String expectedMessage = GenericErrorCodeMessage.DUPLICATED_FIELD.toString("Logon");
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    /**
     * Test to get a user list
     * @throws UniquenessConstraintException in case there is a issue in the data and it is duplicated
     */
    @Test
    public void testGetUserList() throws UniquenessConstraintException {
        UserEntity u = new UserEntity(UserFactory.create("testGetIdFirstName2", "testGetIdLastName2", "testGetIdLogon2",
                null, "testGetIdEmail2@testGetIdEmail2.pt", "951",2L,false));
        userServiceAccess.create(u);
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
        UserEntity u = new UserEntity(UserFactory.create("testGetIdFirstName", "testGetIdLastName", "testGetIdLogon",
                null, "testGetIdEmail@testGetIdEmail.pt", "951",2L,false));
        userServiceAccess.create(u);
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
    public void testGetByListOfIds() throws UniquenessConstraintException {
        UserEntity u1 = new UserEntity(UserFactory.create("testGetByListOfIdsFirstName1", "testGetByListOfIdsLastName1",
                "testGetByListOfIdsLogon1", null,
                "testGetByListOfIdsEmail1@testGetByListOfIdsEmail1.pt", "951",2L,false));
        userServiceAccess.create(u1);

        UserEntity u2 = new UserEntity(UserFactory.create("testGetByListOfIdsFirstName2", "testGetByListOfIdsLastName2",
                "testGetByListOfIdsLogon2", null,
                "testGetByListOfIdsEmail2@testGetByListOfIdsEmail2.pt", "951",2L,false));
        userServiceAccess.create(u2);

        List<Long> userIds = Arrays.asList(u1.getId(), u2.getId());
        List<SystemUser> result = userServiceAccess.get(userIds);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    /**
     * Test to attempt to get a list by giving a empty list of ids
     */
    @Test
    public void testGetByEmptyListOfIds() {

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
     * @throws UserNotFoundException in case no user was found after the save in the DB
     */
    @Test
    public void testDeleteById() throws UserNotFoundException {
        userServiceAccess.delete(uTest.getId());
        assertNull(userServiceAccess.get(uTest.getId()));
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
        UserEntity u1 = new UserEntity(UserFactory.create("testDeleteByListOfIdsFirstName1", "testDeleteByListOfIdsLastName1",
                "testDeleteByListOfIdsLogon1", null,
                "testDeleteByListOfIdsEmail1@testDeleteByListOfIdsEmail1.pt", "951",2L,false));
        userServiceAccess.create(u1);

        UserEntity u2 = new UserEntity(UserFactory.create("testDeleteByListOfIdsFirstName2", "testDeleteByListOfIdsLastName2",
                "testDeleteByListOfIdsLogon2", null,
                "testDeleteByListOfIdsEmail2@testDeleteByListOfIdsEmail2.pt", "951",2L,false));
        userServiceAccess.create(u2);

        UserEntity u3 = new UserEntity(UserFactory.create("testDeleteByListOfIdsFirstName3", "testDeleteByListOfIdsLastName3",
                "testDeleteByListOfIdsLogon3", null,
                "testDeleteByListOfIdsEmail3@testDeleteByListOfIdsEmail3.pt","951",2L,false));
        userServiceAccess.create(u3);

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
        SystemUser u1 = new UserEntity(UserFactory.create("testUpdateFirstName1", "testUpdateLastName1",
                "testUpdateLogon1", "testeUpdateSub1", "testeUpdateEmail1@testeUpdate1.pt", "951",2L,false));
        userServiceAccess.create(u1);

        SystemUser u2 = new UserEntity(UserFactory.create("testUpdateFirstName2", "testUpdateLastName2",
                "testUpdateLogon2", "testeUpdateSub2", "testeUpdateEmail2@testeUpdate2.pt", "951",2L,false));
        userServiceAccess.create(u2);

        SystemUser u3 = new UserEntity(UserFactory.create("testUpdateFirstName1", "testUpdateLastName3",
                "testUpdateLogon3", "testeUpdateSub3", "testeUpdateEmail3@testeUpdate3.pt", "951",2L,false));

        u3.setId(u1.getId());

        userServiceAccess.update(u3);

        u1 = userServiceAccess.get(u1.getId());

        assertEquals(u1.getFirstname(), u3.getFirstname());
        assertEquals(u1.getLastname(), u3.getLastname());
        assertEquals(u1.getLogon(), u3.getLogon());
        assertEquals(u1.getSub(), u3.getSub());
        assertEquals(u1.getUserEmail(), u3.getUserEmail());

        SystemUser u4 = new UserEntity(UserFactory.create("testUpdateFirstName4", "testUpdateLastName4",
                "testUpdateLogon4", "testeUpdateSub4","testeUpdateEmail4@testeUpdate4.pt","951", 2L, false));

        u4.setId(u1.getId());

        userServiceAccess.update(u4);

        u1 = userServiceAccess.get(u1.getId());

        assertEquals(u1.getFirstname(), u4.getFirstname());
        assertEquals(u1.getLastname(), u4.getLastname());
        assertEquals(u1.getLogon(), u4.getLogon());
        assertEquals(u1.getSub(), u4.getSub());
        assertEquals(u1.getUserEmail(), u4.getUserEmail());
    }

    /**
     * Test updates the user information asserts UniquenessConstraintException
     * @throws Exception in case of user to be updated not found
     */
    @Test(expected = UniquenessConstraintException.class)
    public void testUpdateUniquenessConstraintException() throws Exception {
        SystemUser u1 = new UserEntity(UserFactory.create("testUpdateFirstName1", "testUpdateLastName1",
                "testUpdateLogon1", "testeUpdateSub1", "testeUpdateEmail1@testeUpdate1.pt", "951",2L,false));
        userServiceAccess.create(u1);

        SystemUser u2 = new UserEntity(UserFactory.create("testUpdateFirstName2", "testUpdateLastName2",
                "testUpdateLogon1", "testeUpdateSub2", "testeUpdateEmail2@testeUpdate2.pt", "951",2L,false));
        userServiceAccess.create(u2);
    }

    /**
     * Test to attempt to update multiple records with failure
     * @throws Exception to be throw
     */
    @Test
    public void testUpdateFailureMultipleRecords() throws Exception {
        UserEntity u1 = new UserEntity(UserFactory.create("testUpdateFailureMultipleRecordsFirstName1",
                "testUpdateFailureMultipleRecordsLastName1", "testUpdateFailureMultipleRecordsLogon1",
                "testUpdateFailureMultipleRecordsSub1",
                "testUpdateFailureMultipleRecordsEmail1@testUpdateFailureMultipleRecordsEmail1.pt", "951",2L,false));
        userServiceAccess.create(u1);

        UserEntity u2 = new UserEntity(UserFactory.create("testUpdateFailureMultipleRecordsFirstName2",
                "testUpdateFailureMultipleRecordsLastName2", "testUpdateFailureMultipleRecordsLogon2",
                "testUpdateFailureMultipleRecordsSub2",
                "testUpdateFailureMultipleRecordsEmail2@testUpdateFailureMultipleRecordsEmail2.pt", "951",2L,false));
        userServiceAccess.create(u2);

        UserEntity u3 = new UserEntity(UserFactory.create("testUpdateFailureMultipleRecordsFirstName3",
                "testUpdateFailureMultipleRecordsLastName3", "testUpdateFailureMultipleRecordsLogon3",
                "testUpdateFailureMultipleRecordsSub3",
                "testUpdateFailureMultipleRecordsEmail3@testUpdateFailureMultipleRecordsEmail3.pt", "951",2L,false));
        userServiceAccess.create(u3);

        UserEntity u4 = new UserEntity(UserFactory.create("testUpdateFailureMultipleRecordsFirstName4",
                "testUpdateFailureMultipleRecordsLastName4", "testUpdateFailureMultipleRecordsLogon3",
                "testUpdateFailureMultipleRecordsSub4",
                "testUpdateFailureMultipleRecordsEmail2@testUpdateFailureMultipleRecordsEmail2.pt", "951",2L,false));

        Exception exceptionEmailAndLogon = assertThrows(Exception.class, () -> userServiceAccess.create(u4));
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

        UserEntity u1 = new UserEntity(UserFactory.create("testUpdateFailureDuplicatedEmailFirstName1",
                "testUpdateFailureDuplicatedEmailLastName1", "testUpdateFailureDuplicatedEmailLogon1",
                "testUpdateFailureDuplicatedEmailSub1",
                "testUpdateFailureDuplicatedEmail1@testUpdateFailureDuplicatedEmail1.pt", "951",2L,false));
        userServiceAccess.create(u1);

        UserEntity u2 = new UserEntity(UserFactory.create("testUpdateFailureDuplicatedEmailFirstName2",
                "testUpdateFailureDuplicatedEmailLastName2", "testUpdateFailureDuplicatedEmailLogon2",
                "testUpdateFailureDuplicatedEmailSub2",
                "testUpdateFailureDuplicatedEmail2@testUpdateFailureDuplicatedEmail2.pt", "951",2L,false));
        userServiceAccess.create(u2);

        UserEntity u3 = new UserEntity(UserFactory.create("testUpdateFailureDuplicatedEmailFirstName1",
                "testUpdateFailureDuplicatedEmailLastName1", "testUpdateFailureDuplicatedEmailLogon3",
                "testUpdateFailureDuplicatedEmailSub1",
                "testUpdateFailureDuplicatedEmail2@testUpdateFailureDuplicatedEmail2.pt", "951",2L,false));

        Exception exceptionEmail = assertThrows(Exception.class, () -> userServiceAccess.create(u3));
        String actualMessageEmail = exceptionEmail.getMessage();
        assertTrue(actualMessageEmail.contains(expectedMessageEmail));

        UserEntity u4 = new UserEntity(UserFactory.create("testUpdateFailureDuplicatedEmailFirstName4",
                "testUpdateFailureDuplicatedEmailLastName4", "testUpdateFailureDuplicatedEmailLogon3",
                "testUpdateFailureDuplicatedEmailSub4",
                "testUpdateFailureDuplicatedEmail2@testUpdateFailureDuplicatedEmail2.pt", "951",2L,false));

        Exception exceptionEmail2 = assertThrows(Exception.class, () -> userServiceAccess.create(u4));
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
        String expectedMessageLogon = GenericErrorCodeMessage.DUPLICATED_FIELD.toString("Logon");

        UserEntity u1 = new UserEntity(UserFactory.create("testUpdateFailureDuplicatedLogonFirstName1",
                "testUpdateFailureDuplicatedLogonLastName1", "testUpdateFailureDuplicatedLogonLogon1",
                "testUpdateFailureDuplicatedLogonSub1",
                "testUpdateFailureDuplicatedLogonEmail1@testUpdateFailureDuplicatedLogonEmail1.pt", "951",2L,false));
        userServiceAccess.create(u1);

        UserEntity u2 = new UserEntity(UserFactory.create("testUpdateFailureDuplicatedLogonFirstName2",
                "testUpdateFailureDuplicatedLogonLastName2", "testUpdateFailureDuplicatedLogonLogon2",
                "testUpdateFailureDuplicatedLogonSub2",
                "testUpdateFailureDuplicatedLogonEmail2@testUpdateFailureDuplicatedLogonEmail2.pt", "951",2L,false));
        userServiceAccess.create(u2);

        UserEntity u3 = new UserEntity(UserFactory.create("testUpdateFailureDuplicatedLogonFirstName1",
                "testUpdateFailureDuplicatedLogonLastName1", "testUpdateFailureDuplicatedLogonLogon2",
                "testUpdateFailureDuplicatedLogonSub2",
                "testUpdateFailureDuplicatedLogonEmail3@testUpdateFailureDuplicatedLogonEmail3.pt", "951",2L,false));

        Exception exceptionLogon = assertThrows(Exception.class, () -> userServiceAccess.create(u3));
        String actualMessageLogon = exceptionLogon.getMessage();
        assertTrue(actualMessageLogon.contains(expectedMessageLogon));

    }

    /**
     * Test updates the user information. Should be a failure in this test case and no information should be updated,
     * since that we are updating user one with the information from user three, but using a duplicated email address.
     * @throws Exception in case of user to be updated not found
     */
    @Test
    public void testUpdateFailureDuplicatedEmailAndLogon() throws Exception {
        UserEntity u1 = new UserEntity(UserFactory.create("testUpdateFailureDuplicatedEmailAndLogonFirstName1",
                "testUpdateFailureDuplicatedEmailAndLogonLastName1", "testUpdateFailureDuplicatedEmailAndLogonLogon1",
                "testUpdateFailureDuplicatedEmailAndLogonSub1",
                "testUpdateFailureDuplicatedEmailAndLogon1@testUpdateFailureDuplicatedEmailAndLogon1.pt", "951",2L,false));
        userServiceAccess.create(u1);

        UserEntity u2 = new UserEntity(UserFactory.create("testUpdateFailureDuplicatedEmailAndLogonFirstName2",
                "testUpdateFailureDuplicatedEmailAndLogonLastName2", "testUpdateFailureDuplicatedEmailAndLogonLogon2",
                "testUpdateFailureDuplicatedEmailAndLogonSub2",
                "testUpdateFailureDuplicatedEmailAndLogon2@testUpdateFailureDuplicatedEmailAndLogon2.pt", "951",2L,false));
        userServiceAccess.create(u2);

        UserEntity u3 = new UserEntity(UserFactory.create("testUpdateFailureDuplicatedEmailAndLogonFirstName3",
                "testUpdateFailureDuplicatedEmailAndLogonLastName3", "testUpdateFailureDuplicatedEmailAndLogonLogon3",
                "testUpdateFailureDuplicatedEmailAndLogonSub3",
                "testUpdateFailureDuplicatedEmailAndLogon3@testUpdateFailureDuplicatedEmailAndLogon3.pt", "951",2L,false));
        userServiceAccess.create(u3);

        UserEntity u4 = new UserEntity(UserFactory.create("testUpdateFailureDuplicatedEmailAndLogonFirstName1",
                "testUpdateFailureDuplicatedEmailAndLogonLastName1", "testUpdateFailureDuplicatedEmailAndLogonLogon2",
                "testUpdateFailureDuplicatedEmailAndLogonSub1",
                "testUpdateFailureDuplicatedEmailAndLogon2@testUpdateFailureDuplicatedEmailAndLogon2.pt", "951",2L,false));

        Exception exceptionEmailAndLogon = assertThrows(Exception.class, () -> userServiceAccess.create(u4));
        System.out.println(exceptionEmailAndLogon.toString());
        String actualMessageEmailAndLogon = exceptionEmailAndLogon.getMessage();
        String expectedMessageEmailAndLogon = GenericErrorCodeMessage.DUPLICATED_FIELD.toString("Email Address and Logon");
        assertTrue(actualMessageEmailAndLogon.contains(expectedMessageEmailAndLogon));
    }

    /**
     * Test to attempt to get all the users in a sorting criteria order
     * @throws UniquenessConstraintException in case of duplicated data
     */
    @Test
    public void testGetAllSort() throws UniquenessConstraintException {
        SystemUser userA = new UserEntity(UserFactory.create("a", "lastName", "aGetAllSort",
                "a","aGetAllSort@email.pt", "951",2L,false));
        userServiceAccess.create(userA);
        SystemUser userB = new UserEntity(UserFactory.create("zzz", "lastName", "bGetAllSort",
                "b","bGetAllSort@email.pt", "951",2L,false));
        userServiceAccess.create(userB);

        List<String> orderby = new ArrayList<>();
        orderby.add("firstname");

        Page<? extends SystemUser> userPage = userServiceAccess.getAll(null, 1, 10, orderby, true);

        assertTrue(userPage.getTotalResults()>=2);

        assertEquals("a",userPage.getResults().get(0).getFirstname());

        userPage = userServiceAccess.getAll(null, 1, 10, orderby, false);
        assertTrue(userPage.getTotalResults()>=2);
        assertEquals("zzz",userPage.getResults().get(0).getFirstname());

        List<String> stringList = new ArrayList<>();
        PagedUserSearchFilter filter = new PagedUserSearchFilter();
        filter.setEmail("aGetAllSort@email.pt");
        Page<? extends SystemUser> userPageWhere = userServiceAccess.getAll(filter, 1, 10, stringList, true);
        assertEquals(1, userPageWhere.getTotalResults());

        assertEquals("a",userPageWhere.getResults().get(0).getFirstname());
    }

    @Test
    public void testCount() {
        Long result = userServiceAccess.count();
        assertNotNull(result);
        assertTrue(result > 0);
    }

    /**
     * Test to retrieve the users with and without exact logical search
     * @throws UniquenessConstraintException in case of duplicated data
     */
    @Test
    public void testGetByIsExactOrLogical() throws UniquenessConstraintException {
        SystemUser testById1 = new UserEntity(UserFactory.create("zz", "lastName", "zz",
                "zz","zz@b.pt", "951",1L,false));

        SystemUser testById2 = new UserEntity(UserFactory.create("aa", "lastName", "aa",
                "aa","aa@b.pt", "951",1L,false));

        SystemUser testById3 = new UserEntity(UserFactory.create("aabb", "lastName", "aabb",
                "aabb","aabb@b.pt", "951",1L,false));

        userServiceAccess.create(testById1);
        userServiceAccess.create(testById2);
        userServiceAccess.create(testById3);

        List<? extends SystemUser> usersAnd = userServiceAccess.getUsers(new UserSearchFilter("zz","zz@b.pt","zz",null,true,true));
        assertEquals(1,usersAnd.size());

        List<? extends SystemUser> usersOr = userServiceAccess.getUsers(new UserSearchFilter("aa","aa@b.pt","zz",new ArrayList<>(),true,false));
        assertEquals(2,usersOr.size());

        List<? extends SystemUser> usersNotExact = userServiceAccess.getUsers(new UserSearchFilter("aa","aa","aa",null,false,true));
        assertEquals(2,usersNotExact.size());

        List<Long> ids = Arrays.asList(testById1.getId(), testById2.getId(), testById3.getId());
        List<? extends SystemUser> usersById = userServiceAccess.getUsers(
                new UserSearchFilter(null,null,null,ids,false,false));
        assertEquals(3,usersById.size());

        usersById = userServiceAccess.getUsers(
                new UserSearchFilter("zz",null,null,ids,false,true));
        assertEquals(1,usersById.size());
    }

    /**
     * Test to attempt batch creation and all elements are inserted
     */
    @Test
    public void testBatchAllElementsInserted() {
        List<UserEntity> users = new ArrayList<>();
        int size = 100;
        for (int i=1; i<=size; i++) {
            users.add(new UserEntity(UserFactory.create("userb",
                    String.valueOf(i),
                    String.format("userb.%d", i),
                    String.format("sub%d", i),
                    String.format("userb.%d@emmail.pt", i),
                    String.format("951", i),
                    1L, false)));
        }
        BatchSummary batchSummary = userServiceAccess.create(users);
        assertNotNull(batchSummary);
        assertEquals(batchSummary.getTotalProcessed(), size);
        assertEquals(batchSummary.getTotal(), batchSummary.getTotalProcessed());
        assertEquals(0, batchSummary.getTotalNonProcessed());
        assertNotNull(batchSummary.getNonProcessedItems());
        assertEquals(0, batchSummary.getNonProcessedItems().size());

        PagedUserSearchFilter filter = new PagedUserSearchFilter();
        filter.setLogon("userb.%");
        Page<? extends SystemUser> page = userServiceAccess.getAll(filter, 1, 200, null, true);
        assertNotNull(page);
        assertEquals(batchSummary.getTotalProcessed(), page.getTotalResults());
    }

    /**
     * Test to attempt batch creation and not all elements are inserted
     */
    @Test
    public void testBatchNotAllElementsInserted() {
        List<UserEntity> users = new ArrayList<>();
        int firstSetSize = 4;
        for (int i=1; i<=firstSetSize; i++) {
            users.add(new UserEntity(UserFactory.create("userbatch",
                    String.valueOf(i),
                    String.format("userbatch.%d", i),
                    String.format("userbatch%d", i),
                    String.format("userbatch.%d@emmail.pt", i),
                    String.format("951", i),
                    1L, false)));
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
            users.add(new UserEntity(UserFactory.create("userbatch",
                    String.valueOf(i+secondSetSize),
                    String.format("userbatch.%d", i+secondSetSize),
                    String.format("userbatch%d", i+secondSetSize),
                    String.format("userbatch.%d@emmail.pt", i+secondSetSize),
                    String.format("951", i),
                    1L, false)));
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
                dataIssue -> dataIssue.getRowId() == 5L).findFirst();
        assertTrue(issue.isPresent());
        assertEquals(5L, issue.get().getRowId());
        assertEquals(2, issue.get().getReasons().size());

        // Retrieving issue for 11th element
        issue = batchSummary.getNonProcessedItems().stream().filter(
                dataIssue -> dataIssue.getRowId() == 11L).findFirst();
        assertTrue(issue.isPresent());
        assertEquals(11L, issue.get().getRowId());
        assertEquals(3, issue.get().getReasons().size());

        PagedUserSearchFilter filter = new PagedUserSearchFilter();
        filter.setSub("userbatch%");
        Page<? extends SystemUser> page = userServiceAccess.getAll(filter, 1, 200, null, true);
        assertNotNull(page);
        assertEquals((firstSetSize + secondSetSize) - 6, page.getTotalResults());

    }

    /**
     * Test for a situation where is tried to update a user that
     * does not exist in the database
     * @throws UniquenessConstraintException in case there is a issue in the data and it is duplicated
     * @throws UserNotFoundException in case certain user could not be found
     */
    @Test(expected = SystemException.class)
    public void testUpdateNotExistentUser() throws UniquenessConstraintException, UserNotFoundException, SystemException {
        SystemUser user = new UserEntity();
        user.setUserEmail("emailtest@email.com");
        user.setId(111111111111L);
        user.setLastUpdate(new Date());
        user.setLastUpdateUser(1L);

        userServiceAccess.update(user);
    }
}