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
import io.radien.api.model.role.SystemRole;
import io.radien.api.service.role.RoleServiceAccess;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.RoleNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.rolemanagement.client.entities.RoleSearchFilter;
import io.radien.ms.rolemanagement.entities.RoleEntity;
import io.radien.ms.rolemanagement.factory.RoleFactory;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Bruno Gama
 */
public class RoleServiceTest {
    static Properties p;
    static RoleServiceAccess roleServiceAccess;
    static SystemRole systemRole;
    static EJBContainer container;

    @BeforeAll
    public static void start() throws NamingException, RoleNotFoundException, UniquenessConstraintException {
        p = new Properties();
        p.put("appframeDatabase", "new://Resource?type=DataSource");
        p.put("appframeDatabase.JdbcDriver", "org.hsqldb.jdbcDriver");
        p.put("appframeDatabase.JdbcUrl", "jdbc:hsqldb:mem:radien");
        p.put("appframeDatabase.userName", "sa");
        p.put("appframeDatabase.password", "");
        p.put("openejb.exclude-include.order", "include-exclude"); // Defines the processing order
        p.put("openejb.deployments.classpath.include", ".*rolemanagement.*");
        p.put("openejb.deployments.classpath.exclude", ".*client.*");
        p.put("openejb.cdi.activated-on-ejb", "false");
        container = EJBContainer.createEJBContainer(p);

        final Context context = container.getContext();

        roleServiceAccess = (RoleServiceAccess) context.lookup("java:global/rd-ms-rolemanagement//RoleService");

        Page<? extends SystemRole> rolePage = roleServiceAccess.getAll(null,1, 10, null, false);
        if(rolePage.getTotalResults()>0) {
            systemRole = rolePage.getResults().get(0);
        } else {
            systemRole = RoleFactory.create("name", "description", 2L);
            roleServiceAccess.save(systemRole);
        }
    }

    @AfterAll
    public static void stop() {
        if (container != null) {
            container.close();
        }
    }

    @Test
    public void testAddUser() throws RoleNotFoundException {
        SystemRole result = roleServiceAccess.get(systemRole.getId());
        assertNotNull(result);
    }

    @Test
    public void testAddDuplicatedUserEmail() {
        RoleEntity role = RoleFactory.create("name", "description", 2L);
        RoleEntity roleDuplicated = RoleFactory.create("name", "description", 2L);

        boolean success = false;
        try{
            roleServiceAccess.save(role);
            roleServiceAccess.save(roleDuplicated);
            success = false;
            roleServiceAccess.delete(role.getId());
            roleServiceAccess.delete(roleDuplicated.getId());
        } catch (UniquenessConstraintException e){
            success = true;
        } catch(RoleNotFoundException e) {
            success = false;
        }
        assertTrue(success);
    }

    @Test
    public void testGetById() throws RoleNotFoundException, UniquenessConstraintException {
        RoleEntity role = RoleFactory.create("nameGetByID", "descriptionGetByID", 2L);
        roleServiceAccess.save(role);
        SystemRole result = roleServiceAccess.get(role.getId());
        assertNotNull(result);
        assertEquals(role.getName(), result.getName());
        assertEquals(role.getDescription(), result.getDescription());

        roleServiceAccess.delete(role.getId());
    }

    @Test
    public void testGetByIdException() {
        Exception exception = assertThrows(RoleNotFoundException.class, () -> roleServiceAccess.get(99L));

        String expectedMessage = GenericErrorCodeMessage.RESOURCE_NOT_FOUND.toString();
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testDeleteById() throws RoleNotFoundException {
        SystemRole result = roleServiceAccess.get(systemRole.getId());
        assertNotNull(result);
        assertEquals(systemRole.getDescription(), result.getDescription());
        roleServiceAccess.delete(systemRole.getId());

        Exception exception = assertThrows(RoleNotFoundException.class, () -> roleServiceAccess.get((systemRole.getId())));
        String expectedMessage = GenericErrorCodeMessage.RESOURCE_NOT_FOUND.toString();
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testGetByIsExactOrLogical() throws RoleNotFoundException, UniquenessConstraintException {
        SystemRole testById1 = RoleFactory.create("name1", "description1", 2L);
        SystemRole testById2 = RoleFactory.create("name2Find", "description2Find", 2L);
        SystemRole testById3 = RoleFactory.create("name3Find", "description3Find", 2L);

        roleServiceAccess.save(testById1);
        roleServiceAccess.save(testById2);
        roleServiceAccess.save(testById3);

        List<? extends SystemRole> roleAnd = roleServiceAccess.getSpecificRoles(
                new RoleSearchFilter("name1","description1",null,true,true));
        assertEquals(1,roleAnd.size());

        List<? extends SystemRole> roleOr = roleServiceAccess.getSpecificRoles(
                new RoleSearchFilter("name1","description2Find",new ArrayList<>(),true,false));
        assertEquals(2,roleOr.size());

        List<? extends SystemRole> rolesNotExact = roleServiceAccess.getSpecificRoles(
                new RoleSearchFilter("Find","Find",null,false,true));

        assertEquals(2,rolesNotExact.size());

        List<? extends SystemRole> rolesByIds = roleServiceAccess.getSpecificRoles(
                new RoleSearchFilter(null,null,
                        Arrays.asList(testById1.getId(), testById2.getId(), testById3.getId()),
                        true,true));

        assertEquals(3,rolesByIds.size());

        rolesByIds = roleServiceAccess.getSpecificRoles(
                new RoleSearchFilter("nonexistent","nonexistent",
                        Arrays.asList(testById1.getId(), testById2.getId(), testById3.getId()),
                        true,false));

        assertEquals(3,rolesByIds.size());

        roleServiceAccess.delete(testById1.getId());
        roleServiceAccess.delete(testById2.getId());
        roleServiceAccess.delete(testById3.getId());
    }

    @Test
    public void testUpdate() throws RoleNotFoundException, UniquenessConstraintException {
        SystemRole testUpdate1 = RoleFactory.create("nameUpdate1", "descriptionUpdate1", 2L);
        SystemRole testUpdate2 = RoleFactory.create("nameUpdate2", "descriptionUpdate2", 2L);

        roleServiceAccess.save(testUpdate1);
        long id = testUpdate1.getId();

        testUpdate2.setId(id);
        roleServiceAccess.save(testUpdate2);

        roleServiceAccess.delete(testUpdate1.getId());
        roleServiceAccess.delete(testUpdate2.getId());

        Exception exception = assertThrows(RoleNotFoundException.class, () -> roleServiceAccess.get((testUpdate1.getId())));
        Exception exception2 = assertThrows(RoleNotFoundException.class, () -> roleServiceAccess.get((testUpdate2.getId())));

        String expectedMessage = GenericErrorCodeMessage.RESOURCE_NOT_FOUND.toString();
        String actualMessage1 = exception.getMessage();
        String actualMessage2 = exception2.getMessage();

        assertTrue(actualMessage1.contains(expectedMessage));
        assertTrue(actualMessage2.contains(expectedMessage));
    }

    @Test
    public void testGetAllSearchNotNullSort() throws UniquenessConstraintException, RoleNotFoundException {
        SystemRole testById1 = RoleFactory.create("name12", "description12", 2L);
        roleServiceAccess.save(testById1);

        List<String> sortBy = new ArrayList<>();
        sortBy.add("name");

        Page<SystemRole> result = roleServiceAccess.getAll("testGetAll2",1,10,sortBy,false);
        assertNotNull(result);

        Page<SystemRole> result2 = roleServiceAccess.getAll("testGetAll2",1,10,sortBy,true);
        assertNotNull(result2);

        roleServiceAccess.delete(testById1.getId());
    }

    @Test
    public void testGetTotalRecordsCount() {
        long result = roleServiceAccess.getTotalRecordsCount();
        assertTrue(result >= 1);
    }
}
