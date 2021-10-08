/*
 * Copyright (c) 2021-present radien GmbH & its legal owners. All rights reserved.
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

package io.radien.ms.permissionmanagement.client.entities;

import org.junit.Assert;
import org.junit.Test;

/**
 * Resource Search filter test and filtering fields
 * {@link io.radien.ms.permissionmanagement.client.entities.ResourceSearchFilter}
 */
public class ResourceSearchFilterTest {

    private static final String act1 = "act1";

    /**
     * Test creating a resource search filter with empty filtering options
     */
    @Test
    public void testEmptyFilter() {
        ResourceSearchFilter resourceSearchFilter = new ResourceSearchFilter();
        Assert.assertFalse(resourceSearchFilter.isExact());
        Assert.assertFalse(resourceSearchFilter.isLogicConjunction());
        Assert.assertNull(resourceSearchFilter.getName());
    }

    /**
     * Test creating a resource search filter
     */
    @Test
    public void testSettingValues() {
        ResourceSearchFilter resourceSearchFilter = new ResourceSearchFilter();
        Assert.assertFalse(resourceSearchFilter.isExact());
        Assert.assertFalse(resourceSearchFilter.isLogicConjunction());
        Assert.assertNull(resourceSearchFilter.getName());

        resourceSearchFilter.setLogicConjunction(true);
        Assert.assertTrue(resourceSearchFilter.isLogicConjunction());
        resourceSearchFilter.setExact(true);
        Assert.assertTrue(resourceSearchFilter.isExact());
        resourceSearchFilter.setName(act1);
        Assert.assertEquals(act1, resourceSearchFilter.getName());
    }

    /**
     * Test the permission search filter constructor
     */
    @Test
    public void testConstructor() {
        ResourceSearchFilter resourceSearchFilter = new ResourceSearchFilter(act1,null,true, true);
        Assert.assertTrue(resourceSearchFilter.isExact());
        Assert.assertTrue(resourceSearchFilter.isLogicConjunction());
        Assert.assertNotNull(resourceSearchFilter.getName());
        Assert.assertEquals(act1, resourceSearchFilter.getName());
    }

}
