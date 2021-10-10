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

import io.radien.api.model.permission.SystemPermissionSearchFilter;
import org.junit.Assert;
import org.junit.Test;

/**
 * Permission Search filter and filtering options test
 * {@link io.radien.ms.permissionmanagement.client.entities.PermissionSearchFilter}
 */
public class PermissionSearchFilterTest {

    private static final String perm1 = "perm1";

    /**
     * Test creating a permission search filter with empty filtering options
     */
    @Test
    public void testEmptyFilter() {
        PermissionSearchFilter filter = new PermissionSearchFilter();
        Assert.assertFalse(filter.isExact());
        Assert.assertFalse(filter.isLogicConjunction());
        Assert.assertNull(filter.getName());
    }

    /**
     * Test creating a permission search filter
     */
    @Test
    public void testSetting() {
        PermissionSearchFilter filter = new PermissionSearchFilter();
        Assert.assertFalse(filter.isExact());
        Assert.assertFalse(filter.isLogicConjunction());
        Assert.assertNull(filter.getName());

        filter.setExact(true);
        Assert.assertTrue(filter.isExact());

        filter.setLogicConjunction(true);
        Assert.assertTrue(filter.isLogicConjunction());

        filter.setName(perm1);
        Assert.assertNotNull(filter.getName());
        Assert.assertEquals(perm1, filter.getName());

        filter.setActionId(1L);
        filter.setResourceId(2L);

        Assert.assertNotNull(filter.getActionId());
        Assert.assertEquals(filter.getActionId(), new Long(1L));
        Assert.assertNotNull(filter.getResourceId());
        Assert.assertEquals(filter.getResourceId(), new Long(2L));

    }

    /**
     * Test the permission search filter constructor
     */
    @Test
    public void testConstruct() {
        PermissionSearchFilter filter =
                new PermissionSearchFilter(perm1, null, null, null,true, true);
        Assert.assertTrue(filter.isLogicConjunction());
        Assert.assertTrue(filter.isExact());
        filter.setName(perm1);
        Assert.assertNotNull(filter.getName());
        Assert.assertEquals(perm1, filter.getName());
        Assert.assertNull(filter.getActionId());
        Assert.assertNull(filter.getResourceId());
        Assert.assertNull(filter.getIds());
    }



}
