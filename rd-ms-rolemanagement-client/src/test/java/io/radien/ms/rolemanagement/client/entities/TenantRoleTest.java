/*
 * Copyright (c) 2021-present radien GmbH. All rights reserved.
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
package io.radien.ms.rolemanagement.client.entities;

import io.radien.ms.rolemanagement.client.services.TenantRoleFactory;
import org.junit.Test;
import static org.junit.Assert.*;

public class TenantRoleTest {

    private TenantRole tenantRole;
    private Long tenantId = 11L;
    private Long roleId = 22L;

    public TenantRoleTest() {
        tenantRole = TenantRoleFactory.create(11L, 22L, 3L);
        tenantRole.setId(2L);
    }

    @Test
    public void testConstructor() {
        TenantRole newTenantRole = new TenantRole(tenantRole);
        assertEquals((Long) 2L, newTenantRole.getId());
        assertEquals(roleId, newTenantRole.getRoleId());
        assertEquals(tenantId, newTenantRole.getTenantId());
    }

    @Test
    public void testGetId() {
        assertNotNull(tenantRole.getId());
        assertEquals((Long) 2L, tenantRole.getId());
    }

    @Test
    public void testSetId() {
        tenantRole.setId(3L);
        assertNotNull(tenantRole.getId());
        assertEquals((Long) 3L, tenantRole.getId());
    }

    @Test
    public void testRoleId() {
        assertNotNull(tenantRole.getRoleId());
        assertEquals(roleId, tenantRole.getRoleId());
    }

    @Test
    public void testSetRoleId() {
        Long newRoleId = 9999L;
        tenantRole.setRoleId(newRoleId);
        assertNotNull(tenantRole.getRoleId());
        assertEquals(newRoleId, tenantRole.getRoleId());
    }

    @Test
    public void testGetTenantId() {
        assertNotNull(tenantRole.getTenantId());
        assertEquals(tenantId, tenantRole.getTenantId());
    }

    @Test
    public void testSetTenantId() {
        Long newTenantId = 8888L;
        tenantRole.setTenantId(newTenantId);
        assertNotNull(tenantRole.getTenantId());
        assertEquals(newTenantId, tenantRole.getTenantId());
    }
}
