package io.radien.ms.rolemanagement.entities;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class RoleTest {
    RoleEntity role;
    Date now = new Date();

    public RoleTest() {
        role = new RoleEntity();
        role.setName("name");
        role.setDescription("description");
        role.setId(44L);
        role.setCreateUser(2L);
        role.setCreateDate(now);
        role.setTerminationDate(now);
        role.setLastUpdate(now);
        role.setLastUpdateUser(3L);
    }

    @Test
    public void testConstructor() {
        RoleEntity newRole = new RoleEntity(io.radien.ms.rolemanagement.client.services.RoleFactory.create("newName", "newDescription", 99L));

        assertNull(newRole.getId());
        assertEquals("newName", newRole.getName());
        assertEquals("newDescription", newRole.getDescription());
        assertEquals((Long) 99L, newRole.getCreateUser());
    }

    @Test
    public void testGetId() {
        assertNotNull(role.getId());
        assertEquals((Long) 44L, role.getId());
    }

    @Test
    public void testSetId() {
        role.setId(4L);

        assertNotNull(role.getId());
        assertEquals((Long) 4L, role.getId());
    }

    @Test
    public void testGetName() {
        assertNotNull(role.getName());
        assertEquals("name", role.getName());
    }

    @Test
    public void testSetName() {
        role.setName("newName");

        assertNotNull(role.getName());
        assertEquals("newName", role.getName());
    }

    @Test
    public void testGetDescription() {
        assertNotNull(role.getDescription());
        assertEquals("description", role.getDescription());
    }

    @Test
    public void testSetDescription() {
        role.setDescription("newDescription");

        assertNotNull(role.getDescription());
        assertEquals("newDescription", role.getDescription());
    }

    @Test
    public void testGetTerminationDate() {
        assertNotNull(role.getTerminationDate());
        assertEquals(now, role.getTerminationDate());
    }

    @Test
    public void testSetTerminationDate() {
        Date terminationDateRefactored = new Date();
        role.setTerminationDate(terminationDateRefactored);

        assertNotNull(role.getTerminationDate());
        assertEquals(terminationDateRefactored, role.getTerminationDate());
    }

    @Test
    public void testGetCreateDate() {
        assertNotNull(role.getCreateDate());
        assertEquals(now, role.getCreateDate());
    }

    @Test
    public void testSetCreateDate() {
        Date terminationDateRefactored = new Date();
        role.setCreateDate(terminationDateRefactored);

        assertNotNull(role.getCreateDate());
        assertEquals(terminationDateRefactored, role.getCreateDate());
    }

    @Test
    public void testGetLastUpdate() {
        assertNotNull(role.getLastUpdate());
        assertEquals(now, role.getLastUpdate());
    }

    @Test
    public void testSetLastUpdate() {
        Date terminationDateRefactored = new Date();
        role.setLastUpdate(terminationDateRefactored);

        assertNotNull(role.getLastUpdate());
        assertEquals(terminationDateRefactored, role.getLastUpdate());
    }

    @Test
    public void testGetCreateUser() {
        assertNotNull(role.getCreateUser());
        assertEquals((Long) 2L, role.getCreateUser());
    }

    @Test
    public void testSetCreateUser() {
        role.setCreateUser(5L);

        assertNotNull(role.getCreateUser());
        assertEquals((Long) 5L, role.getCreateUser());
    }

    @Test
    public void testGetLastUpdateUser() {
        assertNotNull(role.getLastUpdateUser());
        assertEquals((Long) 3L, role.getLastUpdateUser());
    }

    @Test
    public void testSetLastUpdateUser() {
        role.setLastUpdateUser(6L);

        assertNotNull(role.getLastUpdateUser());
        assertEquals((Long) 6L, role.getLastUpdateUser());
    }
}