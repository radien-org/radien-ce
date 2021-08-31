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
package io.radien.api.util;

import java.time.LocalDateTime;
import java.util.Date;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
/**
 * Class that aggregates UnitTest FactoryUtilService
 *
 */
public class FactoryUtilServiceTest {

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
        builder.add("booleanTest", false);
        builder.add("doubleTest", 2.0);
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

    /**
     * Asserts getIntFromJson()
     */
    @Test
    public void testGetIntFromJson() {
        int integerTest = FactoryUtilService.getIntFromJson("integerTest", json);

        assertEquals(123, integerTest);
    }

    /**
     * Asserts getLongFromJson()
     */
    @Test
    public void testGetLongFromJson() {
        Long createUser = FactoryUtilService.getLongFromJson("createUser", json);

        assertEquals((Long) 2L, createUser);
    }

    /**
     * Asserts getBooleanFromJson()
     */
    @Test
    public void testGetBooleanFromJson() {
        boolean booleanValue = FactoryUtilService.getBooleanFromJson("booleanTest", json);
        assertFalse(booleanValue);
    }

    /**
     * Asserts getDoubleFromJson()
     */
    @Test
    public void testGetDoubleFromJson() {
        Double doubleValue = FactoryUtilService.getDoubleFromJson("doubleTest", json);
        assertEquals(java.util.Optional.of(2.0), java.util.Optional.of(doubleValue));
    }

    /**
     * Asserts getArrayFromJson()
     */
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

    /**
     * Asserts getArrayFromJson()
     */
    @Test
    public void testGetArrayFromJsonNonExistingKey() {
        JsonArray array = FactoryUtilService.getArrayFromJson("mandalorian", json);
        assertNull(array);
    }

    /**
     * Asserts addValueBoolean()
     */
    @Test
    public void testAddValueBoolean(){
        JsonObjectBuilder builder = Json.createObjectBuilder();
        FactoryUtilService.addValueBoolean(builder, "newBooleanTest",false);
        JsonObject json = builder.build();
        assertFalse(FactoryUtilService.getBooleanFromJson("newBooleanTest" ,json));
    }

    /**
     * Asserts addValueDouble()
     */
    @Test
    public void testAddValueDouble(){
        JsonObjectBuilder builder = Json.createObjectBuilder();
        FactoryUtilService.addValueDouble(builder, "newDoubleTest",3.0);
        JsonObject json = builder.build();
        assertEquals(java.util.Optional.of(3.0), java.util.Optional.of(FactoryUtilService.getDoubleFromJson("newDoubleTest" ,json)));
    }

    /**
     * Asserts addValue()
     */
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

    /**
     * Asserts AddValue()
     * parameter as date
     */
    @Test
    public void testAddValueDate() {
        Date now = new Date();
        JsonObjectBuilder builder = Json.createObjectBuilder();
        FactoryUtilService.addValue(builder, "anyKey", now);

        assertNull(FactoryUtilService.getStringFromJson("anyKey", json));

    }

    /**
     * Asserts AddValueLong()
     */
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

    /**
     * Asserts addValueLong()
     */
    @Test
    public void testAddValueLongNull(){
        JsonObjectBuilder builder = Json.createObjectBuilder();
        FactoryUtilService.addValueLong(builder, "id",null);
        JsonObject json = builder.build();
        assertNull(FactoryUtilService.getLongFromJson("id" ,json));
    }

    /**
     * Asserts addValue()
     */
    @Test
    public void testAddValueNull(){
        JsonObjectBuilder builder = Json.createObjectBuilder();
        FactoryUtilService.addValue(builder, "id",null);
        JsonObject json = builder.build();
        assertNull(FactoryUtilService.getStringFromJson("id" ,json));
    }

    /**
     * Asserts addValueDouble()
     */
    @Test
    public void testAddValueDoubleNull(){
        JsonObjectBuilder builder = Json.createObjectBuilder();
        FactoryUtilService.addValueDouble(builder, "id",null);
        JsonObject json = builder.build();
        assertNull(FactoryUtilService.getStringFromJson("id" ,json));
    }

    /**
     * Asserts addValueInt()
     */
    @Test
    public void testAddValueIntNull(){
        JsonObjectBuilder builder = Json.createObjectBuilder();
        FactoryUtilService.addValueInt(builder, "id",null);
        JsonObject json = builder.build();
        assertNull(FactoryUtilService.getIntFromJson("id" ,json));
    }

    /**
     * Asserts addValueArray()
     */
    @Test
    public void testAddValueArrayNull(){
        JsonObjectBuilder builder = Json.createObjectBuilder();
        FactoryUtilService.addValueArray(builder, "elements",null);
        JsonObject json = builder.build();
        assertNull(FactoryUtilService.getIntFromJson("elements" ,json));
    }

    /**
     * Asserts addValueArray()
     */
    @Test
    public void testAddValueArray(){
        JsonObjectBuilder wrapperObj = Json.createObjectBuilder();
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        JsonObjectBuilder arrayElement =  Json.createObjectBuilder();
        arrayElement.add("val","value");
        arrayBuilder.add(arrayElement);
        FactoryUtilService.addValueArray(wrapperObj, "elements",arrayBuilder.build());
        assertEquals(1,FactoryUtilService.getArrayFromJson("elements" ,wrapperObj.build()).size());
    }

    /**
     * Asserts getLocalDateTimeFromJson()
     */
    @Test
    public void testGetLocalDateTimeFromJson(){
        LocalDateTime now = LocalDateTime.now();
        JsonObjectBuilder builder = Json.createObjectBuilder();
        FactoryUtilService.addValue(builder, "anyKey", now);
        JsonObject json = builder.build();

        assertEquals(now, FactoryUtilService.getLocalDateTimeFromJson("anyKey", json));
        FactoryUtilService.addValue(builder, "anyKey", null);
        JsonObject json1 = builder.build();
        assertNull(FactoryUtilService.getLocalDateTimeFromJson("anyKey", json1));
    }
}