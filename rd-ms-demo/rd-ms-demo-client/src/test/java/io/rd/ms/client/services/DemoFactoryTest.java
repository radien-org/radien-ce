/*
 * Copyright (c) 2016-present radien.io & its legal owners. All rights reserved.
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
package io.rd.ms.client.services;

import io.rd.api.util.FactoryUtilService;
import io.rd.ms.client.entities.Demo;
import io.rd.ms.client.util.DemoModelMapper;

import org.junit.Before;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class DemoFactoryTest {
    Demo demo;
    JsonObject json;

    @Before
    public void before(){
        demo = new Demo();
        demo.setName("factoryTestName");

        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("id", 1L);
        builder.add("name", "testName");
        json = builder.build();
    }

    @Test
    public void create_test(){
        String actual = DemoFactory.create("factoryTestName").getName();
        assertEquals(demo.getName(),actual);
    }

    @Test
    public void convert_test() {
        Demo testDemo = DemoFactory.convert(json);
        Long id = getId();
        String testName = getName();

        assertNotNull(testDemo);
        assertEquals(id,testDemo.getId());
        assertEquals(testName,testDemo.getName());
    }

    @Test
    public void convert_test_null() {
        JsonObject jsonObject;
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.addNull("id");
        builder.addNull("name");
        jsonObject = builder.build();
        Demo testDemo = DemoFactory.convert(jsonObject);

        assertNull(testDemo.getId());
        assertNull(testDemo.getName());
    }

    @Test
    public void convert_jsonArray_list_test() {
        String name = "aa";
        Demo demo2 = DemoFactory.create(name);
        JsonArray jsonArray = DemoModelMapper.map(Collections.singletonList(demo2));

        List<Demo> listDemo = DemoFactory.convert(jsonArray);
        assertEquals(1L, listDemo.size());
    }

    @Test
    public void convertToJsonObject_test(){
        Demo convertDemoToJson = new Demo();
        convertDemoToJson.setId(1L);
        convertDemoToJson.setName("testName");
        JsonObject convertToJsonObject = DemoFactory.convertToJsonObject(convertDemoToJson);
        assertEquals(json.toString(), convertToJsonObject.toString());
    }

    private Long getId(){
        return FactoryUtilService.getLongFromJson("id", json);
    }

    private String getName(){
        return FactoryUtilService.getStringFromJson("name", json);
    }
}
