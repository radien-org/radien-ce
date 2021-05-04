package io.radien.ms.tenantmanagement.client.entities;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Newton Carvalho
 */
public class TenantRolePermissionSearchFilterTest {

    private final TenantRolePermissionSearchFilter searchFilter;
    private final Long tenantRoleId = 5L;
    private final Long permissionId = 7L;

    public TenantRolePermissionSearchFilterTest() {
        searchFilter = new TenantRolePermissionSearchFilter(tenantRoleId, permissionId, true, true);
    }

    @Test
    public void testEmptyConstructor() {
        TenantRolePermissionSearchFilter tenantRoleSearchFilter = new TenantRolePermissionSearchFilter();
        assertNull(tenantRoleSearchFilter.getTenantRoleId());
        assertNull(tenantRoleSearchFilter.getPermissionId());
        assertFalse(tenantRoleSearchFilter.isExact());
        assertFalse(tenantRoleSearchFilter.isLogicConjunction());
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
    public void testGetPermissionId() {
        assertNotNull(searchFilter.getPermissionId());
        assertEquals(permissionId, searchFilter.getPermissionId());
    }

    @Test
    public void testSetPermissionId() {
        Long newPermissionId = 77L;
        searchFilter.setPermissionId(newPermissionId);
        assertNotNull(searchFilter.getPermissionId());
        assertEquals(newPermissionId, searchFilter.getPermissionId());
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