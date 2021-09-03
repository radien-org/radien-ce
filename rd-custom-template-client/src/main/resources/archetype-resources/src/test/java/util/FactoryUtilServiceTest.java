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
package ${package}.util;

import junit.framework.TestCase;
import org.junit.Test;

import javax.json.*;

public class FactoryUtilServiceTest extends TestCase {

    JsonObject json;

    public FactoryUtilServiceTest() {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        builder.add("id", 1L);
        builder.add("message", "testMessage");
        json = builder.build();
    }

    @Test
    public void testGetStringFromJson() {
        String message = FactoryUtilService.getStringFromJson("message", json);

        assertEquals("testMessage", message);
    }

    @Test
    public void testGetLongFromJson() {
        Long id = FactoryUtilService.getLongFromJson("id", json);

        assertEquals((Long) 1L, id);
    }

    @Test
    public void testAddValue() {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        FactoryUtilService.addValue(builder, "id", "idLong");
        FactoryUtilService.addValue(builder, "message", "messageString");

        JsonObject json = builder.build();

        String id = FactoryUtilService.getStringFromJson("id", json);
        String contact = FactoryUtilService.getStringFromJson("message", json);

        assertEquals("idLong", id);
        assertEquals("messageString", contact);
    }

    @Test
    public void testAddValueLong() {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        FactoryUtilService.addValueLong(builder, "createdId", 2L);
        FactoryUtilService.addValueLong(builder, "updatedId", 3L);

        JsonObject json = builder.build();

        Long createdId = FactoryUtilService.getLongFromJson("createdId", json);
        Long updatedId = FactoryUtilService.getLongFromJson("updatedId", json);

        assertEquals((Long) 2L, createdId);
        assertEquals((Long) 3L, updatedId);
    }

    @Test
    public void testAddValueLongNull(){
        JsonObjectBuilder builder = Json.createObjectBuilder();
        FactoryUtilService.addValueLong(builder, "id",null);
        JsonObject json = builder.build();
        assertNull(FactoryUtilService.getLongFromJson("id" ,json));
    }

    @Test
    public void testAddValueNull(){
        JsonObjectBuilder builder = Json.createObjectBuilder();
        FactoryUtilService.addValue(builder, "id",null);
        JsonObject json = builder.build();
        assertNull(FactoryUtilService.getStringFromJson("id" ,json));
    }

}