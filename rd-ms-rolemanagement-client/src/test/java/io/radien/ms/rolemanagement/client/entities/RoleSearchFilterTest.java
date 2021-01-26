package io.radien.ms.rolemanagement.client.entities;

import junit.framework.TestCase;
import org.junit.Test;

import static org.junit.Assert.*;

public class RoleSearchFilterTest extends TestCase {

    private final RoleSearchFilter roleSearch;

    public RoleSearchFilterTest() {
        roleSearch = new RoleSearchFilter("name", "description", true, true);
    }

    @Test
    public void testEmptyConstructor() {
        RoleSearchFilter roleSearchFilter = new RoleSearchFilter();
        assertNull(roleSearchFilter.getName());
        assertNull(roleSearchFilter.getDescription());
        assertFalse(roleSearchFilter.isExact());
        assertFalse(roleSearchFilter.isLogicConjunction());
    }

    @Test
    public void testGetName() {
        assertNotNull(roleSearch.getName());
        assertEquals("name", roleSearch.getName());
    }

    @Test
    public void testSetName() {
        roleSearch.setName("newName");
        assertNotNull(roleSearch.getName());
        assertEquals("newName", roleSearch.getName());
    }

    @Test
    public void testGetDescription() {
        assertNotNull(roleSearch.getDescription());
        assertEquals("description", roleSearch.getDescription());
    }

    @Test
    public void testSetDescription() {
        roleSearch.setDescription("newDescription");
        assertNotNull(roleSearch.getDescription());
        assertEquals("newDescription", roleSearch.getDescription());
    }

    @Test
    public void testIsExact() {
        assertTrue(roleSearch.isExact());
    }

    @Test
    public void testSetExact() {
        roleSearch.setExact(false);
        assertFalse(roleSearch.isExact());
    }

    @Test
    public void testIsLogicConjunction() {
        assertTrue(roleSearch.isLogicConjunction());
    }

    @Test
    public void testSetLogicConjunction() {
        roleSearch.setLogicConjunction(false);
        assertFalse(roleSearch.isLogicConjunction());
    }
}