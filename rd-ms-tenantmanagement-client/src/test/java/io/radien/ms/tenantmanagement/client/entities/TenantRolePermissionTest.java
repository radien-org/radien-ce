/*
 * Copyright (c) 2016-present openappframe.org & its legal owners. All rights reserved.
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
