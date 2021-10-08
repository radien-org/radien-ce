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
package io.rd.microservice.ms.client.providers;

import io.rd.microservice.api.entity.Page;
import io.rd.microservice.ms.client.entities.Microservice;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PageMessageBodyReaderTest {
    @Test
    public void testIsReadable() {
        PageMessageBodyReader target = new PageMessageBodyReader();
        assertTrue(target.isReadable(Page.class,null,null,null));
    }

    @Test
    public void testReadFrom() throws IOException {
        String example = "{\n" +
                "\"currentPage\": -1,\n" +
                "\"results\": [\n" +
                "{\n" +
                "\"enabled\": false,\n" +
                "\"name\": \"a\",\n" +
                "\"id\": 28\n" +
                "}\n" +
                "],\n" +
                "\"totalPages\": 1,\n" +
                "\"totalResults\": 4\n" +
                "}";
        PageMessageBodyReader target = new PageMessageBodyReader();
        InputStream in = new ByteArrayInputStream(example.getBytes());
        Page<Microservice> page = target.readFrom(null,null,null,null,null,in);
        assertEquals(1,page.getTotalPages());
        assertEquals(4,page.getTotalResults());
        assertEquals(-1,page.getCurrentPage());
        assertEquals(1,page.getResults().size());
        Microservice microservice = page.getResults().get(0);
        assertEquals("a",microservice.getName());
        assertEquals(28L,(long) microservice.getId());
    }
}