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
import io.radien.api.model.permission.SystemAction;
import io.radien.api.service.permission.ActionServiceAccess;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.permissionmanagement.client.entities.ActionSearchFilter;
import io.radien.ms.permissionmanagement.legacy.ActionFactory;
import io.radien.ms.permissionmanagement.model.ActionEntity;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;
import java.util.Properties;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Action Service test, to test the crud requests and responses
 * {@link io.radien.ms.permissionmanagement.service.ActionService}
 *
 * @author Nuno Santana
 * @author Bruno Gama
 */
public class ActionServiceTest {

    static ActionServiceAccess actionServiceAccess;
    static SystemAction actionTest;
    static EJBContainer container;

    /**
     * Constructor method to prepare all the variables and properties before running the tests
     * @throws Exception in case of any issue
     */
    @BeforeClass
    public static void start() throws Exception {
        Properties p = new Properties();
        p.put("openejb.deployments.classpath.include",".*permission.*");
        p.put("openejb.deployments.classpath.exclude",".*client.*");
        container = EJBContainer.createEJBContainer(p);
        final Context context = container.getContext();

        actionServiceAccess = (ActionServiceAccess) 
                context.lookup("java:global/rd-ms-permissionmanagement//ActionService");

        Page<? extends SystemAction> actionPage =
                actionServiceAccess.getAll(null, 1, 10, null, true);
        if (actionPage.getTotalResults() > 0) {
            actionTest = actionPage.getResults().get(0);
        } else {
            actionTest = ActionFactory.create("actionName", 2L);
            actionServiceAccess.save(actionTest);
        }
    }

    /**
     * Injection method before starting the tests
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
     * Add Action test.
     * Will create and save the Action.
     * Expected result: Success.
     * Tested methods: void save(Action Action)
     */
    @Test
    public void testAddAction() {
        SystemAction result = actionServiceAccess.get(actionTest.getId());
        assertNotNull(result);
    }

    /**
     * Test to try to get a non existent action
     */
    @Test
    public void testGetNotExistentAction() {
        SystemAction result = actionServiceAccess.get(111111111L);
        assertNull(result);
    }


    /**
     * Add Action test with duplicated Name.
     * Will create and save the Action, with an already existent name.
     * Expected result: Throw treated exception Error 103 Message There is more than one Action with the same Name.
     * Tested methods: void save(Action Action)
     */
    @Test
    public void testAddDuplicatedName() throws UniquenessConstraintException {
        ActionEntity u = ActionFactory.create("actionNameXXX", 2L);
        actionServiceAccess.save(u);

        ActionEntity u2 = ActionFactory.create("actionNameXXX", 2L);
        Exception exception = assertThrows(UniquenessConstraintException.class, () -> actionServiceAccess.save(u2));
        String expectedMessage = GenericErrorCodeMessage.DUPLICATED_FIELD.toString("Name");;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    /**
     * Gets Action using the PK (id).
     * Will create a new Action, save it into the DB and retrieve the specific Action using the ID.
     * Expected result: will return the correct inserted Action.
     * Tested methods: SystemAction get(Long ActionId)
     *
     * @throws UniquenessConstraintException in case of requested action is not well constructed
     */
    @Test
    public void testGetById() throws UniquenessConstraintException {
        ActionEntity u = ActionFactory.create("testGetIdFirstName", 2L);
        actionServiceAccess.save(u);
        SystemAction result = actionServiceAccess.get(u.getId());
        assertNotNull(result);
        assertEquals(u.getName(), result.getName());
    }

    /**
     * Gets multiple Actions using a list of PK (id).
     * Will create 2 new Actions, save them into the DB and retrieve the specific Actions using the ID's.
     * Expected result: will return the correct number of inserted Action.
     * Tested methods: List<SystemAction> get(List<Long> actionIds)
     *
     * @throws UniquenessConstraintException in case of requested action is not well constructed
     */
    @Test
    public void testGetByListOfIds() throws UniquenessConstraintException {
        ActionEntity p1 = ActionFactory.create("testGetByListOfIdsFirstName1", 2L);
        actionServiceAccess.save(p1);

        ActionEntity p2 = ActionFactory.create("testGetByListOfIdsFirstName2", 2L);
        actionServiceAccess.save(p2);

        List<Long> ActionIds = Arrays.asList(p1.getId(), p2.getId());
        List<SystemAction> result = actionServiceAccess.get(ActionIds);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    /**
     * Test to try to get a empty list of given id's
     */
    @Test
    public void testGetByEmptyListOfIds() {

        List<SystemAction> result = actionServiceAccess.get(new ArrayList<>());
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Deletes inserted Action using the PK (id).
     * Will create a new Action, save it into the DB and delete it after using the specific ID.
     * Expected result: will return null when retrieving the Action.
     * Tested methods: void delete(Long ActionId)
     */
    @Test
    public void testDeleteById() {
        SystemAction result = actionServiceAccess.get(actionTest.getId());
        assertNotNull(result);
        assertEquals(actionTest.getName(), result.getName());
        actionServiceAccess.delete(actionTest.getId());
        result = actionServiceAccess.get(actionTest.getId());
        assertNull(result);
    }

    /**
     * Deletes multiple Actions using a list of PK (id).
     * Will create 3 new Actions, save them into the DB and delete the specific 2 Actions using the ID's.
     * Expected result: will delete the correct number of inserted Action and return only one of them (the not deleted
     * one).
     * Tested methods: void delete(Collection<Long> ActionIds)
     *
     * @throws UniquenessConstraintException in case of requested action is not well constructed
     */
    @Test
    public void testDeleteByListOfIds() throws UniquenessConstraintException {
        SystemAction p1 = ActionFactory.create("testDeleteByListOfIdsFirstName1", 2L);
        actionServiceAccess.save(p1);

        SystemAction p2 = ActionFactory.create("testDeleteByListOfIdsFirstName2",2L);
        actionServiceAccess.save(p2);

        SystemAction p3 = ActionFactory.create("testDeleteByListOfIdsFirstName3",2L);
        actionServiceAccess.save(p3);

        List<Long> actionIds = Arrays.asList(p1.getId(), p2.getId());
        actionServiceAccess.delete(actionIds);
        List<SystemAction> result = actionServiceAccess.get(actionIds);
        assertEquals(0, result.size());
        SystemAction resultExistentOne = actionServiceAccess.get(p3.getId());
        assertNotNull(result);
        assertEquals(p3.getName(), resultExistentOne.getName());
    }


    /**
     * Test updates the Action information. 
     * Should be a success in this test case, if the name attribute from the Action one, 
     * should have been updated to the Action three information.
     * @throws Exception in case of Action to be updated not found
     */
    @Test
    public void testUpdateSuccess() throws Exception {
        SystemAction p1 = ActionFactory.create("testUpdateActionName1",2L);
        actionServiceAccess.save(p1);

        SystemAction p2 = ActionFactory.create("testUpdateActionName2",2L);
        actionServiceAccess.save(p2);

        SystemAction p3 = ActionFactory.create("testUpdateActionName1",2L);

        p3.setId(p1.getId());

        actionServiceAccess.save(p3);

        p1 = actionServiceAccess.get(p1.getId());

        assertEquals(p1.getName(), p3.getName());
        SystemAction u4 = ActionFactory.create("testUpdateActionName4",2L);

        u4.setId(p1.getId());

        actionServiceAccess.save(u4);

        p1 = actionServiceAccess.get(p1.getId());

        assertEquals(p1.getName(), u4.getName());

    }

    /**
     * Test that will try to update multiple actions but should throw a failure exception
     * @throws Exception to be throw
     */
    @Test
    public void testUpdateFailureMultipleRecords() throws Exception {
        ActionEntity p1 = ActionFactory.create("actionName1", 2L);
        actionServiceAccess.save(p1);

        ActionEntity p2 = ActionFactory.create("actionName2", 2L);
        actionServiceAccess.save(p2);

        ActionEntity p3 = ActionFactory.create("actionName3", 2L);
        actionServiceAccess.save(p3);

        ActionEntity u4 = ActionFactory.create("actionName1", 2L);

        Exception exceptionForRepeatedName = assertThrows(Exception.class, () -> actionServiceAccess.save(u4));
        String exceptionForRepeatedNameMessage = exceptionForRepeatedName.getMessage();
        String expectedMessage = GenericErrorCodeMessage.DUPLICATED_FIELD.toString("Name");
        assertTrue(exceptionForRepeatedNameMessage.contains(expectedMessage));

    }

    /**
     * Test updates the Action information. Should be a failure in this test case and no information should be updated,
     * since that we are updating Action one with the information from Action three, but using a duplicated email address.
     * @throws UniquenessConstraintException in case of Action to be updated not found
     */
    @Test
    public void testUpdateFailureDuplicatedName() throws UniquenessConstraintException {
        String expectedMessageName = GenericErrorCodeMessage.DUPLICATED_FIELD.toString("Name");

        ActionEntity p1 = ActionFactory.create("actionNamePerm1", 2L);
        actionServiceAccess.save(p1);

        ActionEntity p2 = ActionFactory.create("actionNamePerm2", 2L);
        actionServiceAccess.save(p2);

        ActionEntity p3 = ActionFactory.create("actionNamePerm1", 2L);

        Exception exceptionForFieldName = assertThrows(Exception.class, () -> actionServiceAccess.save(p3));
        String actualMessage = exceptionForFieldName.getMessage();
        assertTrue(actualMessage.contains(expectedMessageName));

        ActionEntity u4 = ActionFactory.create("actionNamePerm2", 2L);

        Exception exceptionName2 = assertThrows(Exception.class, () -> actionServiceAccess.save(u4));
        String messageFromException = exceptionName2.getMessage();
        assertTrue(messageFromException.contains(expectedMessageName));
    }

    /**
     * Test to get all the actions but with a specific given sort criteria
     * @throws UniquenessConstraintException in case of one or multiple fields with incorrect or invalid data
     */
    @Test
    public void testGetAllSort() throws UniquenessConstraintException {
        SystemAction actionA = ActionFactory.create("a", 2L);
        actionServiceAccess.save(actionA);
        SystemAction actionB = ActionFactory.create("zzz", 2L);
        actionServiceAccess.save(actionB);
        SystemAction actionC = ActionFactory.create("d", 2L);
        actionServiceAccess.save(actionC);

        List<String> orderby = new ArrayList<>();
        orderby.add("name");

        Page<? extends SystemAction> actionPage = actionServiceAccess.getAll(null, 1, 10,
                orderby, true);
  
        assertTrue(actionPage.getTotalResults()>=3);

        assertEquals("a",actionPage.getResults().get(0).getName());

        actionPage = actionServiceAccess.getAll(null, 1, 10, orderby, false);
        assertTrue(actionPage.getTotalResults()>=3);
        assertEquals("zzz",actionPage.getResults().get(0).getName());

        Page<? extends SystemAction> actionPageWhere = actionServiceAccess.getAll("a", 1, 10, null, true);
        assertEquals(1, actionPageWhere.getTotalResults());

        assertEquals("a",actionPageWhere.getResults().get(0).getName());
    }

    /**
     * Test to try to get actions by id with exact and without exact logical search (and or or)
     * @throws UniquenessConstraintException in case of one or multiple fields with incorrect or invalid data
     */
    @Test
    public void testGetByIsExactOrLogical() throws UniquenessConstraintException {
        SystemAction testById1 = ActionFactory.create("zz", 1L);
        SystemAction testById2 = ActionFactory.create("aa", 1L);
        SystemAction testById3 = ActionFactory.create("aabb", 1L);
        SystemAction testById4 = ActionFactory.create("aabaco", 1L);
        SystemAction testById5 = ActionFactory.create("aabaco0", 1L);
        SystemAction testById6 = ActionFactory.create("ddd", 1L);
        SystemAction testById7 = ActionFactory.create("xxx", 1L);

        actionServiceAccess.save(testById1);
        actionServiceAccess.save(testById2);
        actionServiceAccess.save(testById3);
        actionServiceAccess.save(testById4);
        actionServiceAccess.save(testById5);
        actionServiceAccess.save(testById6);
        actionServiceAccess.save(testById7);

        List<? extends SystemAction> actionsAnd = actionServiceAccess.getActions(
                new ActionSearchFilter("zz",true,true));
        assertEquals(1, actionsAnd.size());

        List<? extends SystemAction> actionsOr = actionServiceAccess.getActions(
                new ActionSearchFilter("aa", true,false));
        assertEquals(1, actionsOr.size());

        List<? extends SystemAction> actionsNotExact = actionServiceAccess.getActions(
                new ActionSearchFilter("aa", false,true));
        assertEquals(4, actionsNotExact.size());

        List<? extends SystemAction> actions = actionServiceAccess.getActions(
                new ActionSearchFilter("aabac", false,false));

        assertEquals(2, actions.size());

        ActionSearchFilter actionSearchFilter = new ActionSearchFilter();
        actionSearchFilter.setName("aabac");
        actionSearchFilter.setExact(false);
        actionSearchFilter.setLogicConjunction(true);
        actions = actionServiceAccess.getActions(actionSearchFilter);

        assertEquals(2, actions.size());

        actions = actionServiceAccess.getActions(
                new ActionSearchFilter(null, false,true));

        // In necessary to count with the first ever inserted (variable "actionTest")
        assertEquals(8, actions.size());

        actions = actionServiceAccess.getActions(new ActionSearchFilter("xxx", true,true));

        assertEquals(1, actions.size());
    }
}
