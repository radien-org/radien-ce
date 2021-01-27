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
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.*;

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

        final Context context = EJBContainer.createEJBContainer(p).getContext();

        roleServiceAccess = (RoleServiceAccess) context.lookup("java:global/rd-ms-rolemanagement//RoleService");

        Page<? extends SystemRole> rolePage = roleServiceAccess.getAll(1, 10);
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
        Exception exception = assertThrows(UniquenessConstraintException.class, () -> roleServiceAccess.save(role));
        String expectedMessage = "{\"code\":101, \"key\":\"error.duplicated.field\", \"message\":\"There is more than" +
                " one role with the same value for the field: Name\"}";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
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
}
