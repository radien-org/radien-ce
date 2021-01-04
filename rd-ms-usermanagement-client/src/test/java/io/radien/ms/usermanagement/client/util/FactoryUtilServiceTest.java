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
package io.radien.ms.usermanagement.client.util;

import junit.framework.TestCase;
import org.junit.Test;

import javax.json.*;

public class FactoryUtilServiceTest extends TestCase {

    JsonObject json;

    public FactoryUtilServiceTest() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        JsonObjectBuilder builder2 = Json.createObjectBuilder();
        builder2.add("testeArray", "testeArray");
        builder2.add("testeArray2", "testeArray2");

        arrayBuilder.add(builder2.build());

        builder.addNull("id");
        builder.add("logon", "logonTest");
        builder.add("userEmail", "emailtest@emailtest.pt");
        builder.add("createUser", 2L);
        builder.addNull("lastUpdateUser");
        builder.add("sub","sub");
        builder.add("firstname", "testFirstName");
        builder.add("lastname", "testLastname");
        builder.add("integerTest", 123);
        builder.add("arrayTest", arrayBuilder.build());
        json = builder.build();
    }

    /**
     * Test to retrieve correctly a String from a JSON
     */
    @Test
    public void testGetStringFromJson() {
        String logon = FactoryUtilService.getStringFromJson("logon", json);
        String userEmail = FactoryUtilService.getStringFromJson("userEmail", json);
        String sub = FactoryUtilService.getStringFromJson("sub", json);
        String firstname = FactoryUtilService.getStringFromJson("firstname", json);
        String lastname = FactoryUtilService.getStringFromJson("lastname",json);

        assertEquals("logonTest", logon);
        assertEquals("emailtest@emailtest.pt", userEmail);
        assertEquals("sub", sub);
        assertEquals("testFirstName", firstname);
        assertEquals("testLastname", lastname);
    }

    @Test
    public void testGetIntFromJson() {
        int integerTest = FactoryUtilService.getIntFromJson("integerTest", json);

        assertEquals(123, integerTest);
    }

    @Test
    public void testGetLongFromJson() {
        Long createUser = FactoryUtilService.getLongFromJson("createUser", json);

        assertEquals((Long) 2L, createUser);
    }

    @Test
    public void testGetArrayFromJson() {
        JsonArray array = FactoryUtilService.getArrayFromJson("arrayTest", json);

        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        JsonObjectBuilder builder2 = Json.createObjectBuilder();
        builder2.add("testeArray", "testeArray");
        builder2.add("testeArray2", "testeArray2");

        arrayBuilder.add(builder2.build());

        assertEquals(arrayBuilder.build(), array);
    }

    @Test
    public void testGetArrayFromJsonNonExistingKey() {
        JsonArray array = FactoryUtilService.getArrayFromJson("mandalorian", json);
        assertNull(array);
    }

    @Test
    public void testAddValue() {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        FactoryUtilService.addValue(builder, "id", "personID");
        FactoryUtilService.addValue(builder, "contact", "personContact");

        JsonObject json = builder.build();

        String id = FactoryUtilService.getStringFromJson("id", json);
        String contact = FactoryUtilService.getStringFromJson("contact", json);

        assertEquals("personID", id);
        assertEquals("personContact", contact);
    }

    @Test
    public void testAddValueLong() {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        FactoryUtilService.addValueLong(builder, "createdUser", 2L);
        FactoryUtilService.addValueLong(builder, "updatedUser", 3L);

        JsonObject json = builder.build();

        Long createdUser = FactoryUtilService.getLongFromJson("createdUser", json);
        Long updatedUser = FactoryUtilService.getLongFromJson("updatedUser", json);

        assertEquals((Long) 2L, createdUser);
        assertEquals((Long) 3L, updatedUser);
    }
}