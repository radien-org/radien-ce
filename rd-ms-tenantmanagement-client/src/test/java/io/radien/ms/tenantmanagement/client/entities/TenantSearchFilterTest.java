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
package io.radien.ms.tenantmanagement.client.entities;

import junit.framework.TestCase;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Bruno Gama
 */
public class TenantSearchFilterTest extends TestCase {

    private final TenantSearchFilter searchFilter;
    private final List<Long> ids = Arrays.asList(3L, 4L);

    public TenantSearchFilterTest() {
        this.searchFilter = new TenantSearchFilter("name", "root", ids,true, true);
    }

    @Test
    public void testGetName() {
        assertNotNull(searchFilter.getName());
        assertEquals("name", searchFilter.getName());
    }

    @Test
    public void testSetName() {
        searchFilter.setName("newName");
        assertNotNull(searchFilter.getName());
        assertEquals("newName", searchFilter.getName());
    }

    @Test
    public void testGetType() {
        assertNotNull(searchFilter.getTenantType());
        assertEquals(TenantType.ROOT_TENANT, searchFilter.getTenantType());
    }

    @Test
    public void testSetType() {
        searchFilter.setTenantType(TenantType.CLIENT_TENANT);
        assertNotNull(searchFilter.getTenantType());
        assertEquals(TenantType.CLIENT_TENANT, searchFilter.getTenantType());
    }

    @Test
    public void testGetIds() {
        assertNotNull(searchFilter.getIds());
        assertEquals(ids, searchFilter.getIds());
    }

    @Test
    public void testSetIds() {
        List<Long> identifiers = Collections.singletonList(99L);
        searchFilter.setIds(identifiers);
        assertNotNull(searchFilter.getIds());
        assertEquals(identifiers, searchFilter.getIds());
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