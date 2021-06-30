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
package io.radien.search;

import io.radien.api.search.SearchableByIds;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

/**
 * Test class to infer the behaviour of {@link SearchableByIds}
 * @author Newton Carvalho
 */
public class SearchableByIdsTest {

    /**
     * Test for default constructor
     */
    @Test
    public void testDefaultConstruct() {
        SearchableByIds searchableByIds = new SearchableByIds();
        assertNull(searchableByIds.getIds());
    }

    /**
     * Test for the parameterized constructor
     */
    @Test
    public void testParameterizedConstruct() {
        List<Long> ids = Arrays.asList(1L, 2L, 3L, 4L);
        SearchableByIds searchableByIds = new SearchableByIds(ids);
        assertEquals(ids, searchableByIds.getIds());
    }

    /**
     * Test to the getter and setter methods regarding
     * ids property
     */
    @Test
    public void testGetterSetterForIdsProperty() {
        List<Long> ids = new ArrayList<>();
        SearchableByIds searchableByIds = new SearchableByIds();
        searchableByIds.setIds(ids);
        assertEquals(ids, searchableByIds.getIds());
    }
}
