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
import io.radien.exception.RoleNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.rolemanagement.client.entities.RoleSearchFilter;
import io.radien.ms.rolemanagement.entities.Role;
import io.radien.ms.rolemanagement.factory.RoleFactory;
import org.junit.Test;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 * @author Bruno Gama
 */
public class RoleServiceTest {
    Properties p;
    RoleServiceAccess roleServiceAccess;
    SystemRole systemRole;


    public RoleServiceTest() throws Exception {
        p = new Properties();
        p.put("appframeDatabase", "new://Resource?type=DataSource");
        p.put("appframeDatabase.JdbcDriver", "org.hsqldb.jdbcDriver");
        p.put("appframeDatabase.JdbcUrl", "jdbc:hsqldb:mem:radien");
        p.put("appframeDatabase.userName", "sa");
        p.put("appframeDatabase.password", "");
        p.put("openejb.deployments.classpath.include",".*");
        p.put("openejb.deployments.classpath.exclude",".*rd-ms-usermanagement-client.*");

        final Context context = EJBContainer.createEJBContainer(p).getContext();

        roleServiceAccess = (RoleServiceAccess) context.lookup("java:global/rd-ms-rolemanagement//RoleService");

        Page<? extends SystemRole> rolePage = roleServiceAccess.getAll(null,1, 10, null, false);
        if(rolePage.getTotalResults()>0) {
            systemRole = rolePage.getResults().get(0);
        } else {
            systemRole = RoleFactory.create("name", "description", 2L);
            roleServiceAccess.save(systemRole);
        }
    }

    @Test
    public void testAddUser() throws RoleNotFoundException {
        SystemRole result = roleServiceAccess.get(systemRole.getId());
        assertNotNull(result);
    }

    @Test
    public void testAddDuplicatedUserEmail() {
        Role role = RoleFactory.create("name", "description", 2L);
        Role roleDuplicated = RoleFactory.create("name", "description", 2L);

        boolean success = false;
        try{
            roleServiceAccess.save(role);
            roleServiceAccess.save(roleDuplicated);
            success = false;
        } catch (UniquenessConstraintException e){
            success = true;
        } catch(RoleNotFoundException e) {
            success = false;
        }
        assertTrue(success);
    }

    @Test
    public void testGetById() throws RoleNotFoundException, UniquenessConstraintException {
        Role role = RoleFactory.create("nameGetByID", "descriptionGetByID", 2L);
        roleServiceAccess.save(role);
        SystemRole result = roleServiceAccess.get(role.getId());
        assertNotNull(result);
        assertEquals(role.getName(), result.getName());
        assertEquals(role.getDescription(), result.getDescription());
    }

    @Test
    public void testGetByIdException() {
        Exception exception = assertThrows(RoleNotFoundException.class, () -> roleServiceAccess.get(99L));

        String expectedMessage = "{\"code\":100, \"key\":\"error.role.not.found\", \"message\":\"Role was not found.\"}";
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
        String expectedMessage = "{\"code\":100, \"key\":\"error.role.not.found\", \"message\":\"Role was not found.\"}";
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

        List<? extends SystemRole> roleAnd = roleServiceAccess.getSpecificRoles(new RoleSearchFilter("name1","description1",true,true));
        assertEquals(1,roleAnd.size());

        List<? extends SystemRole> roleOr = roleServiceAccess.getSpecificRoles(new RoleSearchFilter("name1","description2Find",true,false));
        assertEquals(2,roleOr.size());

        List<? extends SystemRole> rolesNotExact = roleServiceAccess.getSpecificRoles(new RoleSearchFilter("Find","Find",false,true));
        assertEquals(2,rolesNotExact.size());
    }

    @Test
    public void testUpdate() throws RoleNotFoundException, UniquenessConstraintException {
        SystemRole testUpdate1 = RoleFactory.create("nameUpdate1", "descriptionUpdate1", 2L);
        SystemRole testUpdate2 = RoleFactory.create("nameUpdate2", "descriptionUpdate2", 2L);

        roleServiceAccess.save(testUpdate1);
        long id = testUpdate1.getId();

        testUpdate2.setId(id);
        roleServiceAccess.save(testUpdate2);
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
    }

    @Test
    public void testGetTotalRecordsCount() {
        long result = roleServiceAccess.getTotalRecordsCount();
        assertEquals(5, result);
    }
}
