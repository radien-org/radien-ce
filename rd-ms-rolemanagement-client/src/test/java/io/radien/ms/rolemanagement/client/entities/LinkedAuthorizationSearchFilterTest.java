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
package io.radien.ms.rolemanagement.client.entities;

import junit.framework.TestCase;
import org.junit.Test;

/**
 * @author Bruno Gama
 */
public class LinkedAuthorizationSearchFilterTest extends TestCase {

    private final LinkedAuthorizationSearchFilter searchFilter;

    public LinkedAuthorizationSearchFilterTest() {
        searchFilter = new LinkedAuthorizationSearchFilter(2L, 2L, 2L, true);
    }

    @Test
    public void testEmptyConstructor() {
        LinkedAuthorizationSearchFilter linkedSearchFilter = new LinkedAuthorizationSearchFilter();
        assertNull(linkedSearchFilter.getTenantId());
        assertNull(linkedSearchFilter.getPermissionId());
        assertNull(linkedSearchFilter.getRoleId());
        assertFalse(linkedSearchFilter.isLogicConjunction());
    }

    @Test
    public void testGetTenantId() {
        assertNotNull(searchFilter.getTenantId());
        assertEquals((Long) 2L, searchFilter.getTenantId());
    }

    @Test
    public void testSetTenantId() {
        searchFilter.setTenantId(3L);
        assertNotNull(searchFilter.getTenantId());
        assertEquals((Long) 3L, searchFilter.getTenantId());
    }

    @Test
    public void testGetPermissionId() {
        assertNotNull(searchFilter.getPermissionId());
        assertEquals((Long) 2L, searchFilter.getPermissionId());
    }

    @Test
    public void testSetPermissionId() {
        searchFilter.setPermissionId(3L);
        assertNotNull(searchFilter.getPermissionId());
        assertEquals((Long) 3L, searchFilter.getPermissionId());
    }

    @Test
    public void testGetRoleId() {
        assertNotNull(searchFilter.getRoleId());
        assertEquals((Long) 2L, searchFilter.getRoleId());
    }

    @Test
    public void testSetRoleId() {
        searchFilter.setRoleId(3L);
        assertNotNull(searchFilter.getRoleId());
        assertEquals((Long) 3L, searchFilter.getRoleId());
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