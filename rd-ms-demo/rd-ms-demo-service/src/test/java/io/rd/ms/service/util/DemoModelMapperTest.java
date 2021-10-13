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
package io.rd.ms.service.util;

import io.rd.ms.service.entities.Demo;
import io.rd.ms.service.legacy.DemoFactory;

import org.junit.Test;

import javax.json.JsonArray;
import javax.json.JsonObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class DemoModelMapperTest {
    @Test
    public void testMapInputStream() {
        String example = "{\n" +
                "\"name\": \"name\"\n" +
                "}";
        InputStream in = new ByteArrayInputStream(example.getBytes());
        Demo demo = DemoModelMapper.map(in);
        assertEquals("name",demo.getName());
    }

    @Test
    public void testMapJsonObject() {
        String name = "name-1";
        Demo demo1 = DemoFactory.create(name);
        JsonObject jsonObject = DemoModelMapper.map(demo1);

        assertEquals(demo1.getName(),jsonObject.getString("name"));

    }

    @Test
    public void testMapList() {
        String name = "aa";
        Demo demo2 = DemoFactory.create(name);
        JsonArray jsonArray = DemoModelMapper.map(Collections.singletonList(demo2));
        assertEquals(1,jsonArray.size());
        JsonObject jsonObject = jsonArray.getJsonObject(0);

        assertEquals(demo2.getName(),jsonObject.getString("name"));
    }
}
