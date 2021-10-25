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
package ${package}.ms.client.services;

import ${package}.api.util.FactoryUtilService;
import ${package}.ms.client.entities.${entityResourceName};
import ${package}.ms.client.util.${entityResourceName}ModelMapper;

import org.junit.Before;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class ${entityResourceName}FactoryTest {
    ${entityResourceName} ${entityResourceName.toLowerCase()};
    JsonObject json;

    @Before
    public void before(){
        ${entityResourceName.toLowerCase()} = new ${entityResourceName}();
        ${entityResourceName.toLowerCase()}.setName("factoryTestName");

        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("id", 1L);
        builder.add("name", "testName");
        json = builder.build();
    }

    @Test
    public void create_test(){
        String actual = ${entityResourceName}Factory.create("factoryTestName").getName();
        assertEquals(${entityResourceName.toLowerCase()}.getName(),actual);
    }

    @Test
    public void convert_test() {
        ${entityResourceName} test${entityResourceName} = ${entityResourceName}Factory.convert(json);
        Long id = getId();
        String testName = getName();

        assertNotNull(test${entityResourceName});
        assertEquals(id,test${entityResourceName}.getId());
        assertEquals(testName,test${entityResourceName}.getName());
    }

    @Test
    public void convert_test_null() {
        JsonObject jsonObject;
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.addNull("id");
        builder.addNull("name");
        jsonObject = builder.build();
        ${entityResourceName} test${entityResourceName} = ${entityResourceName}Factory.convert(jsonObject);

        assertNull(test${entityResourceName}.getId());
        assertNull(test${entityResourceName}.getName());
    }

    @Test
    public void convert_jsonArray_list_test() {
        String name = "aa";
        ${entityResourceName} ${entityResourceName.toLowerCase()}2 = ${entityResourceName}Factory.create(name);
        JsonArray jsonArray = ${entityResourceName}ModelMapper.map(Collections.singletonList(${entityResourceName.toLowerCase()}2));

        List<${entityResourceName}> list${entityResourceName} = ${entityResourceName}Factory.convert(jsonArray);
        assertEquals(1L, list${entityResourceName}.size());
    }

    @Test
    public void convertToJsonObject_test(){
        ${entityResourceName} convert${entityResourceName}ToJson = new ${entityResourceName}();
        convert${entityResourceName}ToJson.setId(1L);
        convert${entityResourceName}ToJson.setName("testName");
        JsonObject convertToJsonObject = ${entityResourceName}Factory.convertToJsonObject(convert${entityResourceName}ToJson);
        assertEquals(json.toString(), convertToJsonObject.toString());
    }

    private Long getId(){
        return FactoryUtilService.getLongFromJson("id", json);
    }

    private String getName(){
        return FactoryUtilService.getStringFromJson("name", json);
    }
}
