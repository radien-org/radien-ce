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
package io.rd.microservice.ms.client.services;

import io.rd.microservice.api.util.FactoryUtilService;
import io.rd.microservice.ms.client.entities.Microservice;
import io.rd.microservice.ms.client.util.MicroserviceModelMapper;

import org.junit.Before;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class MicroserviceFactoryTest {
    Microservice microservice;
    JsonObject json;

    @Before
    public void before(){
        microservice = new Microservice();
        microservice.setName("factoryTestName");

        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("id", 1L);
        builder.add("name", "testName");
        json = builder.build();
    }

    @Test
    public void create_test(){
        String actual = MicroserviceFactory.create("factoryTestName").getName();
        assertEquals(microservice.getName(),actual);
    }

    @Test
    public void convert_test() {
        Microservice testMicroservice = MicroserviceFactory.convert(json);
        Long id = getId();
        String testName = getName();

        assertNotNull(testMicroservice);
        assertEquals(id,testMicroservice.getId());
        assertEquals(testName,testMicroservice.getName());
    }

    @Test
    public void convert_test_null() {
        JsonObject jsonObject;
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.addNull("id");
        builder.addNull("name");
        jsonObject = builder.build();
        Microservice testMicroservice = MicroserviceFactory.convert(jsonObject);

        assertNull(testMicroservice.getId());
        assertNull(testMicroservice.getName());
    }

    @Test
    public void convert_jsonArray_list_test() {
        String name = "aa";
        Microservice microservice2 = MicroserviceFactory.create(name);
        JsonArray jsonArray = MicroserviceModelMapper.map(Collections.singletonList(microservice2));

        List<Microservice> listMicroservice = MicroserviceFactory.convert(jsonArray);
        assertEquals(1L, listMicroservice.size());
    }

    @Test
    public void convertToJsonObject_test(){
        Microservice convertMicroserviceToJson = new Microservice();
        convertMicroserviceToJson.setId(1L);
        convertMicroserviceToJson.setName("testName");
        JsonObject convertToJsonObject = MicroserviceFactory.convertToJsonObject(convertMicroserviceToJson);
        assertEquals(json.toString(), convertToJsonObject.toString());
    }

    private Long getId(){
        return FactoryUtilService.getLongFromJson("id", json);
    }

    private String getName(){
        return FactoryUtilService.getStringFromJson("name", json);
    }
}
