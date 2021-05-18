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
package ${package}.ms.service.util;

import ${package}.ms.service.entities.${entityResourceName};
import ${package}.ms.service.legacy.${entityResourceName}Factory;

import org.junit.Test;

import javax.json.JsonArray;
import javax.json.JsonObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class ${entityResourceName}ModelMapperTest {
    @Test
    public void testMapInputStream() {
        String example = "{\n" +
                "\"name\": \"name\"\n" +
                "}";
        InputStream in = new ByteArrayInputStream(example.getBytes());
        ${entityResourceName} ${entityResourceName.toLowerCase()} = ${entityResourceName}ModelMapper.map(in);
        assertEquals("name",${entityResourceName.toLowerCase()}.getName());
    }

    @Test
    public void testMapJsonObject() {
        String name = "name-1";
        ${entityResourceName} ${entityResourceName.toLowerCase()}1 = ${entityResourceName}Factory.create(name);
        JsonObject jsonObject = ${entityResourceName}ModelMapper.map(${entityResourceName.toLowerCase()}1);

        assertEquals(${entityResourceName.toLowerCase()}1.getName(),jsonObject.getString("name"));

    }

    @Test
    public void testMapList() {
        String name = "aa";
        ${entityResourceName} ${entityResourceName.toLowerCase()}2 = ${entityResourceName}Factory.create(name);
        JsonArray jsonArray = ${entityResourceName}ModelMapper.map(Collections.singletonList(${entityResourceName.toLowerCase()}2));
        assertEquals(1,jsonArray.size());
        JsonObject jsonObject = jsonArray.getJsonObject(0);

        assertEquals(${entityResourceName.toLowerCase()}2.getName(),jsonObject.getString("name"));
    }
}
