package io.radien.ms.tenantmanagement.client.entities;

import org.junit.Test;

import static org.junit.Assert.*;

public class TenantRoleSearchFilterTest {

    private final TenantRoleSearchFilter searchFilter;
    private final Long tenantId = 22L;
    private final Long roleId = 33L;

    public TenantRoleSearchFilterTest() {
        searchFilter = new TenantRoleSearchFilter(tenantId, roleId, true, true);
    }

    @Test
    public void testEmptyConstructor() {
        TenantRoleSearchFilter tenantRoleSearchFilter = new TenantRoleSearchFilter();
        assertNull(tenantRoleSearchFilter.getTenantId());
        assertNull(tenantRoleSearchFilter.getRoleId());
        assertFalse(tenantRoleSearchFilter.isExact());
        assertFalse(tenantRoleSearchFilter.isLogicConjunction());
    }

    @Test
    public void testGetTenantId() {
        assertNotNull(searchFilter.getTenantId());
        assertEquals(tenantId, searchFilter.getTenantId());
    }

    @Test
    public void testSetTenantId() {
        Long newTenantId = 64L;
        searchFilter.setTenantId(newTenantId);
        assertNotNull(searchFilter.getTenantId());
        assertEquals(newTenantId, searchFilter.getTenantId());
    }

    @Test
    public void testGetRoleId() {
        assertNotNull(searchFilter.getRoleId());
        assertEquals(roleId, searchFilter.getRoleId());
    }

    @Test
    public void testSetRoleId() {
        Long newRoleId = 77L;
        searchFilter.setRoleId(newRoleId);
        assertNotNull(searchFilter.getRoleId());
        assertEquals(newRoleId, searchFilter.getRoleId());
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