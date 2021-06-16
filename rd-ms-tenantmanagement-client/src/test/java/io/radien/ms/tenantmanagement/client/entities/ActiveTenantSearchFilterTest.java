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

import junit.framework.TestCase;
import org.junit.Test;

/**
 * Active Tenant Search Filter Test
 * {@link io.radien.ms.tenantmanagement.client.entities.ActiveTenantSearchFilter}
 *
 * @author Bruno Gama
 */
public class ActiveTenantSearchFilterTest extends TestCase {

    private final ActiveTenantSearchFilter searchFilter;

    public ActiveTenantSearchFilterTest() {
        this.searchFilter = new ActiveTenantSearchFilter(2L, 2L, true, true);
    }

    /**
     * Test the search filter user id getter
     */
    @Test
    public void testGetUserId() {
        assertNotNull(searchFilter.getUserId());
        assertEquals((Long) 2L, searchFilter.getUserId());
    }

    /**
     * Test the search filter user id setter
     */
    @Test
    public void testSetUserId() {
        searchFilter.setUserId(3L);
        assertNotNull(searchFilter.getUserId());
        assertEquals((Long) 3L, searchFilter.getUserId());
    }

    /**
     * Test the search filter tenant id getter
     */
    @Test
    public void testGetTenantId() {
        assertNotNull(searchFilter.getTenantId());
        assertEquals((Long) 2L, searchFilter.getTenantId());
    }

    /**
     * Test the search filter tenant id setter
     */
    @Test
    public void testSetTenantId() {
        searchFilter.setTenantId(3L);
        assertNotNull(searchFilter.getTenantId());
        assertEquals((Long) 3L, searchFilter.getTenantId());
    }

    /**
     * Test the search filter is exact value getter
     */
    @Test
    public void testIsExact() {
        assertTrue(searchFilter.isExact());
    }

    /**
     * Test the search filter is exact value setter
     */
    @Test
    public void testSetExact() {
        searchFilter.setExact(false);
        assertFalse(searchFilter.isExact());
    }

    /**
     * Test the search filter is logical conjunction value getter
     */
    @Test
    public void testIsLogicConjunction() {
        assertTrue(searchFilter.isLogicConjunction());
    }

    /**
     * Test the search filter is logical conjunction value setter
     */
    @Test
    public void testSetLogicConjunction() {
        searchFilter.setLogicConjunction(false);
        assertFalse(searchFilter.isLogicConjunction());
    }
}