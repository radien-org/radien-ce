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

public class RoleSearchFilterTest extends TestCase {

    private final RoleSearchFilter roleSearch;

    public RoleSearchFilterTest() {
        roleSearch = new RoleSearchFilter("name", "description", true, true);
    }

    @Test
    public void testEmptyConstructor() {
        RoleSearchFilter roleSearchFilter = new RoleSearchFilter();
        assertNull(roleSearchFilter.getName());
        assertNull(roleSearchFilter.getDescription());
        assertFalse(roleSearchFilter.isExact());
        assertFalse(roleSearchFilter.isLogicConjunction());
    }

    @Test
    public void testGetName() {
        assertNotNull(roleSearch.getName());
        assertEquals("name", roleSearch.getName());
    }

    @Test
    public void testSetName() {
        roleSearch.setName("newName");
        assertNotNull(roleSearch.getName());
        assertEquals("newName", roleSearch.getName());
    }

    @Test
    public void testGetDescription() {
        assertNotNull(roleSearch.getDescription());
        assertEquals("description", roleSearch.getDescription());
    }

    @Test
    public void testSetDescription() {
        roleSearch.setDescription("newDescription");
        assertNotNull(roleSearch.getDescription());
        assertEquals("newDescription", roleSearch.getDescription());
    }

    @Test
    public void testIsExact() {
        assertTrue(roleSearch.isExact());
    }

    @Test
    public void testSetExact() {
        roleSearch.setExact(false);
        assertFalse(roleSearch.isExact());
    }

    @Test
    public void testIsLogicConjunction() {
        assertTrue(roleSearch.isLogicConjunction());
    }

    @Test
    public void testSetLogicConjunction() {
        roleSearch.setLogicConjunction(false);
        assertFalse(roleSearch.isLogicConjunction());
    }
}