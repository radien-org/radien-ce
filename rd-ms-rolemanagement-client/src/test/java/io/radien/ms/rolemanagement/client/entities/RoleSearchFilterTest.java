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

import java.util.Arrays;
import java.util.List;
import java.util.Collections;

public class RoleSearchFilterTest extends TestCase {

    private final RoleSearchFilter roleSearch;
    private final List<Long> ids = Arrays.asList(1L, 2L);

    public RoleSearchFilterTest() {
        roleSearch = new RoleSearchFilter("name", "description", ids,true, true);
    }

    @Test
    public void testEmptyConstructor() {
        RoleSearchFilter roleSearchFilter = new RoleSearchFilter();
        assertNull(roleSearchFilter.getName());
        assertNull(roleSearchFilter.getDescription());
        assertNull(roleSearchFilter.getIds());
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
    public void testGetIds() {
        assertNotNull(roleSearch.getIds());
        assertEquals(ids, roleSearch.getIds());
    }

    @Test
    public void testSetIds() {
        List<Long> identifiers = Collections.singletonList(99L);
        roleSearch.setIds(identifiers);
        assertNotNull(roleSearch.getIds());
        assertEquals(identifiers, roleSearch.getIds());
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