/*
 * Copyright (c) 2021-present radien GmbH. All rights reserved.
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
package ${package}.service;

import ${package}.entities.${resource-name};
import ${package}.services.${resource-name}Factory;

import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.text.ParseException;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author Rajesh Gavvala
 * @author Nuno Santana
 * @author Bruno Gama
 */

public class ${resource-name}FactoryTest {

    ${resource-name} ${resource-name-variable};
    JsonObject json;
    JsonObjectBuilder builder;

    public ${resource-name}FactoryTest() {

    }

    @Test
    public void convert() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.addNull("id");
        builder.add("message", "testMessage");
        json = builder.build();

        ${resource-name-variable} = ${resource-name}Factory.create("testMessage");
        ${resource-name} converted${resource-name} = ${resource-name}Factory.convert(json);

        assertEquals(${resource-name-variable}.getId(), converted${resource-name}.getId());
        assertEquals(${resource-name-variable}.getMessage(), converted${resource-name}.getMessage());
    }
}
