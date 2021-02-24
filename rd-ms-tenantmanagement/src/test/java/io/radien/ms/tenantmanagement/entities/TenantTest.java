package io.radien.ms.tenantmanagement.entities;

import org.junit.Test;

import static org.junit.Assert.*;

public class TenantTest {
    Tenant tenant;
    Tenant tenant2;

    public TenantTest(){
        tenant2 = new Tenant(new io.radien.ms.tenantmanagement.client.entities.Tenant());
        tenant = new Tenant();
        tenant.setName("a");
        tenant.setId(1L);
        tenant2.setName("b");
        tenant2.setId(2L);

    }

    @Test
    public void getId() {
        assertEquals((Long) 1L,tenant.getId());
        assertEquals((Long) 2L,tenant2.getId());
    }

    @Test
    public void getName() {
        assertEquals("a",tenant.getName());
        assertEquals("b",tenant2.getName());
    }
}