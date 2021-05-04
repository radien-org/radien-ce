package io.radien.ms.tenantmanagement.client.entities;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Newton Carvalho
 */
public class TenantRoleUserSearchFilterTest {

    private final TenantRoleUserSearchFilter searchFilter;
    private final Long tenantRoleId = 44444L;
    private final Long userId = 333L;

    public TenantRoleUserSearchFilterTest() {
        searchFilter = new TenantRoleUserSearchFilter(tenantRoleId, userId, true, true);
    }

    @Test
    public void testEmptyConstructor() {
        TenantRoleUserSearchFilter tenantRoleUserSearchFilter = new TenantRoleUserSearchFilter();
        assertNull(tenantRoleUserSearchFilter.getTenantRoleId());
        assertNull(tenantRoleUserSearchFilter.getUserId());
        assertFalse(tenantRoleUserSearchFilter.isExact());
        assertFalse(tenantRoleUserSearchFilter.isLogicConjunction());
    }

    @Test
    public void testGetTenantRoleId() {
        assertNotNull(searchFilter.getTenantRoleId());
        assertEquals(tenantRoleId, searchFilter.getTenantRoleId());
    }

    @Test
    public void testSetTenantRoleId() {
        Long newTenantRoleId = 64L;
        searchFilter.setTenantRoleId(newTenantRoleId);
        assertNotNull(searchFilter.getTenantRoleId());
        assertEquals(newTenantRoleId, searchFilter.getTenantRoleId());
    }

    @Test
    public void testGetUserId() {
        assertNotNull(searchFilter.getUserId());
        assertEquals(userId, searchFilter.getUserId());
    }

    @Test
    public void testSetUserId() {
        Long newUserId = 77L;
        searchFilter.setUserId(newUserId);
        assertNotNull(searchFilter.getUserId());
        assertEquals(newUserId, searchFilter.getUserId());
    }

    @Test
    public void testIsExact() {
        assertTrue(searchFilter.isExact());
    }

    @Test
    public void testSetExact() {
        searchFilter.setExact(false);
        assertFalse(searchFilter.isExact());
    }

    @Test
    public void testIsLogicConjunction() {
        assertTrue(searchFilter.isLogicConjunction());
    }

    @Test
    public void testSetLogicConjunction() {
        searchFilter.setLogicConjunction(false);
        assertFalse(searchFilter.isLogicConjunction());
    }
}