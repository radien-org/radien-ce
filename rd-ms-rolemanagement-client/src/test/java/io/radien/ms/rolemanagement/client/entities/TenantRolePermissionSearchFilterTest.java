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