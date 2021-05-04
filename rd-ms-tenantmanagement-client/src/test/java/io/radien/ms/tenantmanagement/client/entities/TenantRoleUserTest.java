package io.radien.ms.tenantmanagement.client.entities;

import io.radien.ms.tenantmanagement.client.services.TenantRoleUserFactory;
import org.junit.Test;
import static org.junit.Assert.*;

public class TenantRoleUserTest {
    
    private TenantRoleUser tenantRoleUser;
    private Long tenantRoleId = 33L;
    private Long userId = 44L;

    public TenantRoleUserTest() {
        tenantRoleUser = TenantRoleUserFactory.create(tenantRoleId, userId, 9L);
        tenantRoleUser.setId(80L);
    }


    @Test
    public void testConstructor() {
        TenantRoleUser newTenantRoleUser = new TenantRoleUser(tenantRoleUser);
        assertEquals((Long) 80L, newTenantRoleUser.getId());
        assertEquals(tenantRoleId, newTenantRoleUser.getTenantRoleId());
        assertEquals(userId, newTenantRoleUser.getUserId());
    }

    @Test
    public void testGetId() {
        assertNotNull(tenantRoleUser.getId());
        assertEquals((Long) 80L, tenantRoleUser.getId());
    }

    @Test
    public void testSetId() {
        tenantRoleUser.setId(8L);
        assertNotNull(tenantRoleUser.getId());
        assertEquals((Long) 8L, tenantRoleUser.getId());
    }

    @Test
    public void testGetTenantRoleId() {
        assertNotNull(tenantRoleUser.getTenantRoleId());
        assertEquals(tenantRoleId, tenantRoleUser.getTenantRoleId());
    }

    @Test
    public void testSetTenantRoleId() {
        Long newTenantRoleId = 901L;
        tenantRoleUser.setTenantRoleId(newTenantRoleId);
        assertNotNull(tenantRoleUser.getTenantRoleId());
        assertEquals(newTenantRoleId, tenantRoleUser.getTenantRoleId());
    }

    @Test
    public void testGetUserId() {
        assertNotNull(tenantRoleUser.getUserId());
        assertEquals(userId, tenantRoleUser.getUserId());
    }

    @Test
    public void testSetUserId() {
        Long newUserId = 456L;
        tenantRoleUser.setUserId(newUserId);
        assertNotNull(tenantRoleUser.getUserId());
        assertEquals(newUserId, tenantRoleUser.getUserId());
    }
}
