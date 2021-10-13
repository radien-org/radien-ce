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
package io.radien.ms.usermanagement.client.providers;

import io.radien.api.entity.Page;
import io.radien.ms.usermanagement.client.entities.User;
import io.radien.ms.usermanagement.client.services.UserFactory;

import org.junit.Test;

import javax.json.JsonObject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for the class
 * PageMessageBodyReader
 */
public class PageMessageBodyReaderTest {

    /**
     * Test method isReadable()
     * Asserts equality and boolean validations
     */
    @Test
    public void testIsReadable() {
        PageMessageBodyReader target = new PageMessageBodyReader();
        assertTrue(target.isReadable(Page.class,null,null,null));
    }

    /**
     * Test method readForm()
     * Asserts true
     */
    @Test
    public void testReadFrom() throws IOException {
        String example = "{\n" +
                "\"currentPage\": -1,\n" +
                "\"results\": [\n" +
                "{\n" +
                "\"enabled\": false,\n" +
                "\"firstname\": \"a\",\n" +
                "\"id\": 28,\n" +
                "\"lastname\": \"b\",\n" +
                "\"logon\": \"aa34433@email.tt\",\n" +
                "\"userEmail\": \"aa234433@email.tt\"\n" +
                "}\n" +
                "],\n" +
                "\"totalPages\": 1,\n" +
                "\"totalResults\": 4\n" +
                "}";
        PageMessageBodyReader target = new PageMessageBodyReader();
        InputStream in = new ByteArrayInputStream(example.getBytes());
        Page<?> page = target.readFrom(null,null,null,null,null,in);
        assertEquals(1,page.getTotalPages());
        assertEquals(4,page.getTotalResults());
        assertEquals(-1,page.getCurrentPage());
        assertEquals(1,page.getJsonValues().size());
        User u = UserFactory.convert((JsonObject) page.getJsonValues().get(0));
        assertEquals("a",u.getFirstname());
        assertEquals(28L,(long) u.getId());
        assertFalse(u.isEnabled());
    }
}