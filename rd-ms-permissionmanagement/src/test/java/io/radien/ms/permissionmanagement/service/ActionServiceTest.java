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
import io.radien.exception.ActionNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.permissionmanagement.client.entities.ActionType;
import io.radien.ms.permissionmanagement.client.entities.ActionSearchFilter;
import io.radien.ms.permissionmanagement.legacy.ActionFactory;
import io.radien.ms.permissionmanagement.model.Action;
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
public class ActionServiceTest {

    ActionServiceAccess actionServiceAccess;
    SystemAction uTest;

    public ActionServiceTest() throws Exception {
        final Context context = EJBContainer.createEJBContainer(new Properties()).getContext();

        actionServiceAccess = (ActionServiceAccess) 
                context.lookup("java:global/rd-ms-permissionmanagement//ActionService");

        Page<? extends SystemAction> actionPage =
                actionServiceAccess.getAll(null, 1, 10, null, true);
        if (actionPage.getTotalResults() > 0) {
            uTest = actionPage.getResults().get(0);
        } else {
            uTest = ActionFactory.create("actionName", ActionType.CREATE, 2L);
            actionServiceAccess.save(uTest);
        }
    }

    /**
     * Add Action test.
     * Will create and save the Action.
     * Expected result: Success.
     * Tested methods: void save(Action Action)
     *
     * @throws UniquenessConstraintException in case of requested action is not well constructed
     * @throws io.radien.ms.permissionmanagement.client.exceptions.NotFoundException in case no Action was found after the save in the DB
     */
    @Test
    public void testAddAction() throws ActionNotFoundException {
        SystemAction result = actionServiceAccess.get(uTest.getId());
        assertNotNull(result);
    }

    @Test
    public void testGetNotExistentAction() throws ActionNotFoundException {
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
        Action u = ActionFactory.create("actionNameXXX", ActionType.LIST,2L);
        actionServiceAccess.save(u);

        Action u2 = ActionFactory.create("actionNameXXX", ActionType.LIST,2L);
        Exception exception = assertThrows(UniquenessConstraintException.class, () -> actionServiceAccess.save(u2));
        String expectedMessage = "{\"code\":101, \"key\":\"error.duplicated.field\", \"message\":\"There is more than" +
                " one resource with the same value for the field: Name\"}";
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
     * @throws ActionNotFoundException in case no Action was found after the save in the DB
     */
    @Test
    public void testGetById() throws ActionNotFoundException, UniquenessConstraintException {
        Action u = ActionFactory.create("testGetIdFirstName", ActionType.CREATE, 2L);
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
    public void testGetByListOfIds() throws UniquenessConstraintException, ActionNotFoundException {
        Action p1 = ActionFactory.create("testGetByListOfIdsFirstName1", ActionType.LIST, 2L);
        actionServiceAccess.save(p1);

        Action p2 = ActionFactory.create("testGetByListOfIdsFirstName2", ActionType.DELETE,2L);
        actionServiceAccess.save(p2);

        List<Long> ActionIds = Arrays.asList(p1.getId(), p2.getId());
        List<SystemAction> result = actionServiceAccess.get(ActionIds);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

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
     *
     * @throws ActionNotFoundException in case no Action was found after the save in the DB
     */
    @Test
    public void testDeleteById() throws ActionNotFoundException {
        SystemAction result = actionServiceAccess.get(uTest.getId());
        assertNotNull(result);
        assertEquals(uTest.getName(), result.getName());
        actionServiceAccess.delete(uTest.getId());
        result = actionServiceAccess.get(uTest.getId());
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
     * @throws ActionNotFoundException in case no Action was found after the save in the DB
     */
    @Test
    public void testDeleteByListOfIds() throws ActionNotFoundException, UniquenessConstraintException {
        SystemAction p1 = ActionFactory.create("testDeleteByListOfIdsFirstName1",
                ActionType.WRITE, 2L);
        actionServiceAccess.save(p1);

        SystemAction p2 = ActionFactory.create("testDeleteByListOfIdsFirstName2",
                ActionType.WRITE, 2L);
        actionServiceAccess.save(p2);

        SystemAction p3 = ActionFactory.create("testDeleteByListOfIdsFirstName3",
                ActionType.WRITE, 2L);
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
        SystemAction p1 = ActionFactory.create("testUpdateActionName1",
                ActionType.WRITE, 2L);
        actionServiceAccess.save(p1);

        SystemAction p2 = ActionFactory.create("testUpdateActionName2",
                ActionType.WRITE, 2L);
        actionServiceAccess.save(p2);

        SystemAction p3 = ActionFactory.create("testUpdateActionName1",
                ActionType.WRITE, 2L);

        p3.setId(p1.getId());

        actionServiceAccess.save(p3);

        p1 = actionServiceAccess.get(p1.getId());

        assertEquals(p1.getName(), p3.getName());
        SystemAction u4 = ActionFactory.create("testUpdateActionName4",
                ActionType.WRITE, 2L);

        u4.setId(p1.getId());

        actionServiceAccess.save(u4);

        p1 = actionServiceAccess.get(p1.getId());

        assertEquals(p1.getName(), u4.getName());

    }

    @Test
    public void testUpdateFailureMultipleRecords() throws Exception {
        Action p1 = ActionFactory.create("actionName1", ActionType.WRITE, 2L);
        actionServiceAccess.save(p1);

        Action p2 = ActionFactory.create("actionName2", ActionType.WRITE, 2L);
        actionServiceAccess.save(p2);

        Action p3 = ActionFactory.create("actionName3", ActionType.WRITE, 2L);
        actionServiceAccess.save(p3);

        Action u4 = ActionFactory.create("actionName1", ActionType.WRITE, 2L);

        Exception exceptionForRepeatedName = assertThrows(Exception.class, () -> actionServiceAccess.save(u4));
        String exceptionForRepeatedNameMessage = exceptionForRepeatedName.getMessage();
        String expectedMessage = "{\"code\":101, \"key\":\"error.duplicated.field\", \"message\":\"There is more than one resource with the same value for the field: Name\"}";
        assertTrue(exceptionForRepeatedNameMessage.contains(expectedMessage));

    }

    /**
     * Test updates the Action information. Should be a failure in this test case and no information should be updated,
     * since that we are updating Action one with the information from Action three, but using a duplicated email address.
     * @throws UniquenessConstraintException in case of Action to be updated not found
     */
    @Test
    public void testUpdateFailureDuplicatedName() throws UniquenessConstraintException {
        String expectedMessageName = "{\"code\":101, \"key\":\"error.duplicated.field\", " +
                "\"message\":\"There is more than one resource with the same value for the field: Name\"}";

        Action p1 = ActionFactory.create("actionNamePerm1", ActionType.WRITE, 2L);
        actionServiceAccess.save(p1);

        Action p2 = ActionFactory.create("actionNamePerm2", ActionType.WRITE, 2L);
        actionServiceAccess.save(p2);

        Action p3 = ActionFactory.create("actionNamePerm1", ActionType.WRITE, 2L);

        Exception exceptionForFieldName = assertThrows(Exception.class, () -> actionServiceAccess.save(p3));
        String actualMessage = exceptionForFieldName.getMessage();
        assertTrue(actualMessage.contains(expectedMessageName));

        Action u4 = ActionFactory.create("actionNamePerm2", ActionType.WRITE, 2L);

        Exception exceptionName2 = assertThrows(Exception.class, () -> actionServiceAccess.save(u4));
        String messageFromException = exceptionName2.getMessage();
        assertTrue(messageFromException.contains(expectedMessageName));
    }

    @Test
    public void testGetAllSort() throws UniquenessConstraintException, ActionNotFoundException {
        SystemAction actionA = ActionFactory.create("a", ActionType.WRITE, 2L);
        actionServiceAccess.save(actionA);
        SystemAction actionB = ActionFactory.create("zzz", ActionType.WRITE, 2L);
        actionServiceAccess.save(actionB);
        SystemAction actionC = ActionFactory.create("d", ActionType.WRITE, 2L);
        actionServiceAccess.save(actionC);

        List<String> orderby = new ArrayList<>();
        orderby.add("name");

        Page<? extends SystemAction> actionPage = actionServiceAccess.getAll(null, 1, 10, orderby, true);
  
        assertTrue(actionPage.getTotalResults()>=3);

        assertEquals("a",actionPage.getResults().get(0).getName());

        actionPage = actionServiceAccess.getAll(null, 1, 10, orderby, false);
        assertTrue(actionPage.getTotalResults()>=3);
        assertEquals("zzz",actionPage.getResults().get(0).getName());

        Page<? extends SystemAction> actionPageWhere = actionServiceAccess.getAll("a", 1, 10, null, true);
        assertTrue(actionPageWhere.getTotalResults() == 1);

        assertEquals("a",actionPageWhere.getResults().get(0).getName());
    }
    @Test
    public void testGetByIsExactOrLogical() throws UniquenessConstraintException, ActionNotFoundException {
        SystemAction testById1 = ActionFactory.create("zz", ActionType.WRITE, 1L);

        SystemAction testById2 = ActionFactory.create("aa", ActionType.READ, 1L);

        SystemAction testById3 = ActionFactory.create("aabb", ActionType.EXECUTION, 1L);

        SystemAction testById4 = ActionFactory.create("aabaco", ActionType.READ, 1L);

        SystemAction testById5 = ActionFactory.create("aabaco0", ActionType.READ, 1L);

        SystemAction testById6 = ActionFactory.create("ddd", ActionType.READ, 1L);

        SystemAction testById7 = ActionFactory.create("xxx", ActionType.EXECUTION, 1L);

        actionServiceAccess.save(testById1);
        actionServiceAccess.save(testById2);
        actionServiceAccess.save(testById3);
        actionServiceAccess.save(testById4);
        actionServiceAccess.save(testById5);
        actionServiceAccess.save(testById6);
        actionServiceAccess.save(testById7);

        List<? extends SystemAction> actionsAnd = actionServiceAccess.getActions(
                new ActionSearchFilter("zz", null, true,true));
        assertEquals(1, actionsAnd.size());

        List<? extends SystemAction> actionsOr = actionServiceAccess.getActions(
                new ActionSearchFilter("aa", null, true,false));
        assertEquals(1, actionsOr.size());

        List<? extends SystemAction> actionsNotExact = actionServiceAccess.getActions(
                new ActionSearchFilter("aa", null, false,true));
        assertEquals(4, actionsNotExact.size());

        List<? extends SystemAction> actions = actionServiceAccess.getActions(
                new ActionSearchFilter("aabac", ActionType.READ, false,false));


        for (SystemAction sa:actions) {
            System.out.println(sa.getName() + " " + sa.getType());
        }

        assertEquals(4, actions.size());

        ActionSearchFilter actionSearchFilter = new ActionSearchFilter();
        actionSearchFilter.setName("aabac");
        actionSearchFilter.setActionType(ActionType.READ);
        actionSearchFilter.setExact(false);
        actionSearchFilter.setLogicConjunction(true);
        actions = actionServiceAccess.getActions(actionSearchFilter);

        assertEquals(2, actions.size());

        actions = actionServiceAccess.getActions(
                new ActionSearchFilter(null, ActionType.EXECUTION, false,true));

        assertEquals(2, actions.size());

        actions = actionServiceAccess.getActions(
                new ActionSearchFilter("xxx", ActionType.EXECUTION, true,true));

        assertEquals(1, actions.size());

    }


}
