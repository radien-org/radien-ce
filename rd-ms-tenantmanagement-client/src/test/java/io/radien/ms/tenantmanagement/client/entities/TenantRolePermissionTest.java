package io.radien.ms.tenantmanagement.client.entities;

import io.radien.ms.tenantmanagement.client.services.TenantRolePermissionFactory;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Newton Carvalho
 */
public class TenantRolePermissionTest {


    private TenantRolePermission tenantRolePermission;
    private Long tenantRoleId = 86L;
    private Long permissionId = 42L;

    public TenantRolePermissionTest() {
        tenantRolePermission = TenantRolePermissionFactory.create(tenantRoleId, permissionId, 9L);
        tenantRolePermission.setId(55L);
    }


    @Test
    public void testConstructor() {
        TenantRolePermission newTenantRolePermission = new TenantRolePermission(tenantRolePermission);
        assertEquals((Long) 55L, newTenantRolePermission.getId());
        assertEquals(tenantRoleId, newTenantRolePermission.getTenantRoleId());
        assertEquals(permissionId, newTenantRolePermission.getPermissionId());
    }

    @Test
    public void testGetId() {
        assertNotNull(tenantRolePermission.getId());
        assertEquals((Long) 55L, tenantRolePermission.getId());
    }

    @Test
    public void testSetId() {
        tenantRolePermission.setId(8L);
        assertNotNull(tenantRolePermission.getId());
        assertEquals((Long) 8L, tenantRolePermission.getId());
    }

    @Test
    public void testGetTenantRoleId() {
        assertNotNull(tenantRolePermission.getTenantRoleId());
        assertEquals(tenantRoleId, tenantRolePermission.getTenantRoleId());
    }

    @Test
    public void testSetTenantRoleId() {
        Long newTenantRoleId = 901L;
        tenantRolePermission.setTenantRoleId(newTenantRoleId);
        assertNotNull(tenantRolePermission.getTenantRoleId());
        assertEquals(newTenantRoleId, tenantRolePermission.getTenantRoleId());
    }

    @Test
    public void testGetPermissionId() {
        assertNotNull(tenantRolePermission.getPermissionId());
        assertEquals(permissionId, tenantRolePermission.getPermissionId());
    }

    @Test
    public void testSetPermissionId() {
        Long newPermissionId = 456L;
        tenantRolePermission.setPermissionId(newPermissionId);
        assertNotNull(tenantRolePermission.getPermissionId());
        assertEquals(newPermissionId, tenantRolePermission.getPermissionId());
    }    
    
}
