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
package io.radien.ms.rolemanagement.datalayer;

import io.radien.api.entity.Page;
import io.radien.api.model.role.SystemRole;
import io.radien.api.service.role.RoleServiceAccess;
import io.radien.api.service.role.exception.RoleNotFoundException;
import io.radien.exception.InvalidArgumentException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.rolemanagement.client.entities.RoleSearchFilter;
import io.radien.ms.rolemanagement.entities.RoleEntity;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 * @author Bruno Gama
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RoleServiceTest {
    static Properties p;
    static RoleServiceAccess roleServiceAccess;
    static SystemRole systemRole;
    static EJBContainer container;

    @BeforeClass
    public static void start() throws NamingException, RoleNotFoundException, UniquenessConstraintException, InvalidArgumentException {
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
            systemRole = createRoleEntity("name", "description", 1L);
            roleServiceAccess.create(systemRole);
        }
    }

    @AfterClass
    public static void stop() {
        if (container != null) {
            container.close();
        }
    }

    @Test
    public void test001AddUser() throws RoleNotFoundException {
        SystemRole result = roleServiceAccess.get(systemRole.getId());
        assertNotNull(result);
    }

    @Test
    public void test002AddDuplicatedUserEmail() {
        RoleEntity role = createRoleEntity("name", "description", 2L);
        RoleEntity roleDuplicated = createRoleEntity("name", "description", 2L);

        boolean success = false;
        try{
            roleServiceAccess.create(role);
            roleServiceAccess.create(roleDuplicated);
            success = false;
            roleServiceAccess.delete(role.getId());
            roleServiceAccess.delete(roleDuplicated.getId());
        } catch (UniquenessConstraintException e){
            success = true;
        } catch(RoleNotFoundException | InvalidArgumentException e) {
            success = false;
        }
        assertTrue(success);
    }

    @Test
    public void test003GetById() throws RoleNotFoundException, UniquenessConstraintException, InvalidArgumentException {
        RoleEntity role = createRoleEntity("nameGetByID", "descriptionGetByID", 2L);
        roleServiceAccess.create(role);
        SystemRole result = roleServiceAccess.get(role.getId());
        assertNotNull(result);
        assertEquals(role.getName(), result.getName());
        assertEquals(role.getDescription(), result.getDescription());

        roleServiceAccess.delete(role.getId());
    }

    @Test
    public void test004GetByIdException() {
        assertNull(roleServiceAccess.get(99L));
    }

    @Test
    public void test005DeleteById() throws RoleNotFoundException {
        SystemRole result = roleServiceAccess.get(systemRole.getId());
        assertNotNull(result);
        assertEquals(systemRole.getDescription(), result.getDescription());
        roleServiceAccess.delete(systemRole.getId());

        assertNull(roleServiceAccess.get((systemRole.getId())));
    }

    @Test
    public void test006GetByIsExactOrLogical() throws RoleNotFoundException, UniquenessConstraintException, InvalidArgumentException {
        SystemRole testById1 = createRoleEntity("name1", "description1", 2L);
        SystemRole testById2 = createRoleEntity("name2Find", "description2Find", 3L);
        SystemRole testById3 = createRoleEntity("name3Find", "description3Find", 4L);

        roleServiceAccess.create(testById1);
        roleServiceAccess.create(testById2);
        roleServiceAccess.create(testById3);

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

    /**
     * Test for method {@link RoleService#update(SystemRole)}
     * @throws RoleNotFoundException in case of update and the requested role does not exist
     * @throws UniquenessConstraintException in case of information duplicated (already existent in other records)
     */
    @Test
    public void test007Update() throws RoleNotFoundException, UniquenessConstraintException, InvalidArgumentException {
        SystemRole testUpdate1 = createRoleEntity("nameUpdate1", "descriptionUpdate1", 2L);
        SystemRole testUpdate2 = createRoleEntity("nameUpdate2", "descriptionUpdate2", 2L);

        roleServiceAccess.create(testUpdate1);
        long id = testUpdate1.getId();

        testUpdate2.setId(id);
        roleServiceAccess.update(testUpdate2);

        roleServiceAccess.delete(testUpdate1.getId());
        roleServiceAccess.delete(testUpdate2.getId());

        assertNull(roleServiceAccess.get((testUpdate1.getId())));
        assertNull(roleServiceAccess.get((testUpdate2.getId())));
    }

    @Test
    public void test008GetAllSearchNotNullSort() throws UniquenessConstraintException, RoleNotFoundException, InvalidArgumentException {
        SystemRole testById1 = createRoleEntity("name12", "description12", 2L);
        roleServiceAccess.create(testById1);

        List<String> sortBy = new ArrayList<>();
        sortBy.add("name");

        Page<SystemRole> result = roleServiceAccess.getAll("testGetAll2",1,10,sortBy,false);
        assertNotNull(result);

        Page<SystemRole> result2 = roleServiceAccess.getAll("testGetAll2",1,10,sortBy,true);
        assertNotNull(result2);

        roleServiceAccess.delete(testById1.getId());
    }

    @Test
    public void test009GetTotalRecordsCount() {
        long result = roleServiceAccess.getTotalRecordsCount();
        assertEquals(0, result);
    }

    private static RoleEntity createRoleEntity(String name, String description, Long id) {
        RoleEntity entity = new RoleEntity();
        entity.setName(name);
        entity.setDescription(description);
        entity.setId(id);

        return entity;
    }
}
