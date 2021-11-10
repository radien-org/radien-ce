/*
 * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
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
package io.radien.api.util;

import io.radien.api.entity.Page;

import org.junit.Test;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * PageFactory Test class
 *
 * @author Nuno Santana
 * @author Rajesh Gavvala
 */
public class PageFactoryTest {

    /**
     * Test Method convertToPageObject()
     * Asserts JSONObjectBuilder
     */
    @Test
    public void testConvertToPageObject() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        builder.add("currentPage",-1);
        builder.add("results",jsonArrayBuilder);
        builder.add("totalPages",1);
        builder.add("totalResults",4);
        Page<?> page = PageFactory.convertToPageObject(builder.build());
        assertEquals(-1,page.getCurrentPage());
        assertNull(page.getResults());
        assertEquals(1,page.getTotalPages());
        assertEquals(4,page.getTotalResults());
    }
}