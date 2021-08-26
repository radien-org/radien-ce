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
package io.radien.api.entity;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
/**
 * Class that aggregates UnitTest cases for Page
 *
 * @author Rajesh Gavvala
 */
public class PageTest {
    @InjectMocks
    private Page page;

    /**
     * Asserts Page
     */
    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        new Page<>();
        new Page(new ArrayList<>(), 1, 1, 1);
    }

    /**
     * Assert method getJsonValues()
     */
    @Test
    public void testGetAndSetJsonValues(){
        page.setJsonValues(null);
        assertNull(page.getJsonValues());
    }

    /**
     * Assert method getResults()
     */
    @Test
    public void testSetResults(){
        page.setResults(new ArrayList<>());
        assertEquals(0L, page.getResults().size());
    }

    /**
     * Assert method getTotalPages()
     */
    @Test
    public void testTotalPages(){
        page.setTotalPages(1);
        assertEquals(1, page.getTotalPages());
    }

}