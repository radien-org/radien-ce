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

package io.radien.ms.permissionmanagement.client.entities;

import org.junit.Assert;
import org.junit.Test;

/**
 * Action search filter constructor and search fields test
 * {@link io.radien.ms.permissionmanagement.client.entities.ActionSearchFilter}
 */
public class ActionSearchFilterTest {

    private static final String act1 = "act1";

    /**
     * Test creating a action search filter with empty filtering options
     */
    @Test
    public void testEmptyFilter() {
        ActionSearchFilter actionSearchFilter = new ActionSearchFilter();
        Assert.assertFalse(actionSearchFilter.isExact());
        Assert.assertFalse(actionSearchFilter.isLogicConjunction());
        Assert.assertNull(actionSearchFilter.getName());
    }

    /**
     * Test creating a action search filter
     */
    @Test
    public void testSettingValues() {
        ActionSearchFilter actionSearchFilter = new ActionSearchFilter();
        Assert.assertFalse(actionSearchFilter.isExact());
        Assert.assertFalse(actionSearchFilter.isLogicConjunction());
        Assert.assertNull(actionSearchFilter.getName());

        actionSearchFilter.setLogicConjunction(true);
        Assert.assertTrue(actionSearchFilter.isLogicConjunction());
        actionSearchFilter.setExact(true);
        Assert.assertTrue(actionSearchFilter.isExact());
        actionSearchFilter.setName(act1);
        Assert.assertEquals(act1, actionSearchFilter.getName());
    }

    /**
     * Test the action search filter constructor
     */
    @Test
    public void testConstructor() {
        ActionSearchFilter actionSearchFilter = new ActionSearchFilter(act1,null,true, true);
        Assert.assertTrue(actionSearchFilter.isExact());
        Assert.assertTrue(actionSearchFilter.isLogicConjunction());
        Assert.assertNotNull(actionSearchFilter.getName());
        Assert.assertEquals(act1, actionSearchFilter.getName());
        Assert.assertNull(actionSearchFilter.getIds());
    }

}
