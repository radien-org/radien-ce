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
package io.radien.ms.rolemanagement.services;

import io.radien.api.entity.Page;
import io.radien.api.model.linked.authorization.SystemLinkedAuthorization;
import io.radien.api.model.linked.authorization.SystemLinkedAuthorizationSearchFilter;
import io.radien.api.model.role.SystemRole;
import io.radien.api.service.linked.authorization.LinkedAuthorizationServiceAccess;
import io.radien.api.service.role.RoleServiceAccess;
import io.radien.exception.LinkedAuthorizationNotFoundException;
import io.radien.exception.RoleNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.rolemanagement.client.entities.LinkedAuthorizationSearchFilter;
import io.radien.ms.rolemanagement.entities.LinkedAuthorization;
import io.radien.ms.rolemanagement.factory.LinkedAuthorizationFactory;
import io.radien.ms.rolemanagement.factory.RoleFactory;
import org.junit.Test;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 * @author Bruno Gama
 */
public class LinkedAuthorizationServiceTest {
    Properties p;
    LinkedAuthorizationServiceAccess linkedAuthorizationServiceAccess;
    RoleServiceAccess roleServiceAccess;
    SystemLinkedAuthorization systemLinkedAuthorization;


    public LinkedAuthorizationServiceTest() throws Exception {
        p = new Properties();
        p.put("appframeDatabase", "new://Resource?type=DataSource");
        p.put("appframeDatabase.JdbcDriver", "org.hsqldb.jdbcDriver");
        p.put("appframeDatabase.JdbcUrl", "jdbc:hsqldb:mem:radien");
        p.put("appframeDatabase.userName", "sa");
        p.put("appframeDatabase.password", "");

        final Context context = EJBContainer.createEJBContainer(p).getContext();

        linkedAuthorizationServiceAccess = (LinkedAuthorizationServiceAccess) context.lookup("java:global/rd-ms-rolemanagement//LinkedAuthorizationService");
        roleServiceAccess = (RoleServiceAccess) context.lookup("java:global/rd-ms-rolemanagement//RoleService");

        Page<? extends SystemLinkedAuthorization> rolePage = linkedAuthorizationServiceAccess.getAll(1, 10);
        if(rolePage.getTotalResults()>0) {
            systemLinkedAuthorization = rolePage.getResults().get(0);
        } else {
            systemLinkedAuthorization = LinkedAuthorizationFactory.create(2L, 2L, 2L, 2L, 2L);
            linkedAuthorizationServiceAccess.save(systemLinkedAuthorization);
        }
    }

    @Test
    public void testGetAssociationById() throws LinkedAuthorizationNotFoundException, UniquenessConstraintException {
        LinkedAuthorization linkedAuthorization = LinkedAuthorizationFactory.create(3L, 3L, 3L, 3L, 3L);
        linkedAuthorizationServiceAccess.save(linkedAuthorization);
        SystemLinkedAuthorization result = linkedAuthorizationServiceAccess.getAssociationById(linkedAuthorization.getId());
        assertNotNull(result);
        assertEquals(linkedAuthorization.getTenantId(), result.getTenantId());
        assertEquals(linkedAuthorization.getPermissionId(), result.getPermissionId());
        assertEquals(linkedAuthorization.getRoleId(), result.getRoleId());
    }

    @Test
    public void testGetAll() throws LinkedAuthorizationNotFoundException, UniquenessConstraintException {
        LinkedAuthorization linkedAuthorization4 = LinkedAuthorizationFactory.create(4L, 4L, 4L, 4L, 4L);
        LinkedAuthorization linkedAuthorization5 = LinkedAuthorizationFactory.create(5L, 5L, 5L, 5L, 5L);
        LinkedAuthorization linkedAuthorization6 = LinkedAuthorizationFactory.create(6L, 6L, 6L, 6L, 6L);
        linkedAuthorizationServiceAccess.save(linkedAuthorization4);
        linkedAuthorizationServiceAccess.save(linkedAuthorization5);
        linkedAuthorizationServiceAccess.save(linkedAuthorization6);

        Page<? extends SystemLinkedAuthorization> linkedAuthorizationPage = linkedAuthorizationServiceAccess.getAll(1, 10);

        assertTrue(linkedAuthorizationPage.getTotalResults()>=3);
    }

    @Test
    public void getSpecificAssociationLogicConjunction() throws LinkedAuthorizationNotFoundException, UniquenessConstraintException {
        SystemLinkedAuthorization linkedAuthorizationLogicConjunction = LinkedAuthorizationFactory.create(100L, 200L, 100L, 100L, 100L);
        SystemLinkedAuthorization linkedAuthorizationLogicConjunction2 = LinkedAuthorizationFactory.create(100L, 200L, 300L, 200L, 200L);

        linkedAuthorizationServiceAccess.save(linkedAuthorizationLogicConjunction);
        linkedAuthorizationServiceAccess.save(linkedAuthorizationLogicConjunction2);

        LinkedAuthorizationSearchFilter filter = new LinkedAuthorizationSearchFilter(100L, 200L, 100L, 100L, true);

        List<? extends SystemLinkedAuthorization> list = linkedAuthorizationServiceAccess.getSpecificAssociation(filter);

        assertTrue(list.size() == 1);
    }

    @Test
    public void getSpecificAssociationNonLogicConjunction() throws LinkedAuthorizationNotFoundException, UniquenessConstraintException {
        LinkedAuthorization linkedAuthorization7 = LinkedAuthorizationFactory.create(7L, 7L, 7L, 7L, 7L);
        LinkedAuthorization linkedAuthorization8 = LinkedAuthorizationFactory.create(8L, 8L, 8L, 8L, 8L);
        LinkedAuthorization linkedAuthorization9 = LinkedAuthorizationFactory.create(9L, 9L, 9L, 9L, 9L);

        linkedAuthorizationServiceAccess.save(linkedAuthorization7);
        linkedAuthorizationServiceAccess.save(linkedAuthorization8);
        linkedAuthorizationServiceAccess.save(linkedAuthorization9);

        LinkedAuthorizationSearchFilter filter = new LinkedAuthorizationSearchFilter(7L, 8L, 8L, null, false);

        List<? extends SystemLinkedAuthorization> list = linkedAuthorizationServiceAccess.getSpecificAssociation(filter);

        assertTrue(list.size() == 2);
    }

    @Test
    public void testGetSpecificAssociation() throws LinkedAuthorizationNotFoundException, UniquenessConstraintException {
        SystemLinkedAuthorization testById1 = LinkedAuthorizationFactory.create(21L, 21L, 21L, 21L, 21L);
        SystemLinkedAuthorization testById2 = LinkedAuthorizationFactory.create(31L, 31L, 31L, 31L, 31L);
        SystemLinkedAuthorization testById3 = LinkedAuthorizationFactory.create(41L, 21L, 41L, 41L, 41L);

        linkedAuthorizationServiceAccess.save(testById1);
        linkedAuthorizationServiceAccess.save(testById2);
        linkedAuthorizationServiceAccess.save(testById3);

        List<? extends SystemLinkedAuthorization> andConjunction = linkedAuthorizationServiceAccess.getSpecificAssociation(new LinkedAuthorizationSearchFilter(41L,21L,41L, 41L,true));
        assertEquals(1,andConjunction.size());

        List<? extends SystemLinkedAuthorization> orConjunction = linkedAuthorizationServiceAccess.getSpecificAssociation(new LinkedAuthorizationSearchFilter(21L,31L,21L, 21L,false));
        assertEquals(2,orConjunction.size());
    }

    @Test
    public void testDeleteAssociation() throws LinkedAuthorizationNotFoundException {
        SystemLinkedAuthorization result = linkedAuthorizationServiceAccess.getAssociationById(systemLinkedAuthorization.getId());
        assertNotNull(result);
        assertEquals(systemLinkedAuthorization.getRoleId(), result.getRoleId());
        linkedAuthorizationServiceAccess.deleteAssociation(systemLinkedAuthorization.getId());

        Exception exception = assertThrows(LinkedAuthorizationNotFoundException.class, () -> linkedAuthorizationServiceAccess.getAssociationById((systemLinkedAuthorization.getId())));
        String expectedMessage = "{\"code\":100, \"key\":\"error.role.not.found\", \"message\":\"Association was not found.\"}";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }


    @Test
    public void testCheckIfRolesExist() throws UniquenessConstraintException, RoleNotFoundException {
        SystemRole checkIfIExists = RoleFactory.create("test", "description", 11L);
        roleServiceAccess.save(checkIfIExists);
        Long id = checkIfIExists.getId();

        assertTrue(roleServiceAccess.checkIfRolesExist(id, null));
    }

    @Test
    public void testCheckIfRolesExistUsingName() throws UniquenessConstraintException, RoleNotFoundException {
        SystemRole checkIfIExists = RoleFactory.create("testCheckIfRolesExistUsingName", "description", 11L);
        roleServiceAccess.save(checkIfIExists);

        assertTrue(roleServiceAccess.checkIfRolesExist(null, "testCheckIfRolesExistUsingName"));
    }

    @Test
    public void testCheckIfRolesExistNotFound() {
        assertFalse(roleServiceAccess.checkIfRolesExist(null, null));
    }

    @Test
    public void testCheckIfRolesNotExist(){
        assertFalse(roleServiceAccess.checkIfRolesExist(200L, null));
    }

    @Test
    public void testSaveUniquenessError() throws LinkedAuthorizationNotFoundException, UniquenessConstraintException {
        SystemLinkedAuthorization testSaveSuccess = LinkedAuthorizationFactory.create(30L, 30L, 30L, 30L, 30L);
        linkedAuthorizationServiceAccess.save(testSaveSuccess);
        long id = testSaveSuccess.getId();
        assertEquals((Long) 30L, linkedAuthorizationServiceAccess.getAssociationById(id).getRoleId());

        SystemLinkedAuthorization testSaveWithoutSuccess = LinkedAuthorizationFactory.create(30L, 30L, 30L, 30L, 30L);
        Exception exception = assertThrows(UniquenessConstraintException.class, () -> linkedAuthorizationServiceAccess.save(testSaveWithoutSuccess));
        String expectedMessage = "{\"code\":101, \"key\":\"error.duplicated.field\", \"message\":\"There is more than one association role with the same value for the field: Tenant Id, Permission Id, Role Id and User Id\"}";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testUpdateError() throws LinkedAuthorizationNotFoundException, UniquenessConstraintException {
        SystemLinkedAuthorization testSave50 = LinkedAuthorizationFactory.create(50L, 50L, 50L, 50L, 50L);
        linkedAuthorizationServiceAccess.save(testSave50);
        long id50 = testSave50.getId();
        assertEquals((Long) 50L, linkedAuthorizationServiceAccess.getAssociationById(id50).getRoleId());

        SystemLinkedAuthorization testSave51 = LinkedAuthorizationFactory.create(51L, 51L, 51L, 51L, 51L);
        linkedAuthorizationServiceAccess.save(testSave51);
        long id51 = testSave51.getId();
        assertEquals((Long) 51L, linkedAuthorizationServiceAccess.getAssociationById(id51).getRoleId());

        SystemLinkedAuthorization testUpdate = LinkedAuthorizationFactory.create(51L, 51L, 51L, 51L, 51L);
        testUpdate.setId(id50);
        Exception exception = assertThrows(UniquenessConstraintException.class, () -> linkedAuthorizationServiceAccess.save(testUpdate));
        String expectedMessage = "{\"code\":101, \"key\":\"error.duplicated.field\", \"message\":\"There is more than one association role with the same value for the field: Tenant Id, Permission Id, Role Id and User Id\"}";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testValidateRole() throws LinkedAuthorizationNotFoundException, UniquenessConstraintException {
        systemLinkedAuthorization = LinkedAuthorizationFactory.create(400L, 400L, 400L, 400L, 400L);
        linkedAuthorizationServiceAccess.save(systemLinkedAuthorization);


        SystemLinkedAuthorizationSearchFilter filter = new LinkedAuthorizationSearchFilter(400L, null, 400L, null, true);
        assertTrue(linkedAuthorizationServiceAccess.exists(filter));
    }
}