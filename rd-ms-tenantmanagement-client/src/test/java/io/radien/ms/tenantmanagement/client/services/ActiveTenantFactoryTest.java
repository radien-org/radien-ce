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
package io.radien.ms.tenantmanagement.client.services;

import io.radien.ms.tenantmanagement.client.entities.ActiveTenant;
import junit.framework.TestCase;
import org.junit.Test;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonArray;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Active Tenant Factory test {@link io.radien.ms.tenantmanagement.client.services.ActiveTenantFactory}
 *
 * Will test the conversion between the JSON and the objects
 *
 * @author Bruno Gama
 */
public class ActiveTenantFactoryTest extends TestCase {

    ActiveTenant activeTenant = new ActiveTenant();
    JsonObject json;

    /**
     * Test post constructor
     */
    public ActiveTenantFactoryTest() {
        activeTenant.setUserId(2L);
        activeTenant.setTenantId(2L);
    }

    /**
     * Test the active tenant creation
     */
    @Test
    public void testCreate() {
        ActiveTenantFactory activeTenantFactory = new ActiveTenantFactory();
        ActiveTenant newActiveTenantConstructed = activeTenantFactory.create(2L, 2L);

        assertEquals(activeTenant.getUserId(), newActiveTenantConstructed.getUserId());
        assertEquals(activeTenant.getTenantId(), newActiveTenantConstructed.getTenantId());
    }

    /**
     * Test the active tenant conversion into a object
     * @throws ParseException in case a issue while parsing the json
     */
    @Test
    public void testConvert() throws ParseException {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.addNull("id");
        builder.add("userId", 2L);
        builder.add("tenantId", 2L);

        json = builder.build();
        ActiveTenant newJsonActiveTenant = ActiveTenantFactory.convert(json);

        assertEquals(activeTenant.getUserId(), newJsonActiveTenant.getUserId());
        assertEquals(activeTenant.getTenantId(), newJsonActiveTenant.getTenantId());
    }

    /**
     * Test the illegal argument exception in the json to be converted
     * @throws ParseException in case a issue while parsing the json
     */
    @Test
    public void testConvertIllegalArgumentException() throws ParseException {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.addNull("id");
        builder.add("userId", "abc");
        builder.add("tenantId", "abc");

        json = builder.build();
        boolean success = false;
        try{
            TenantFactory.convert(json);
        } catch (IllegalStateException e) {
            success=true;
        }

        assertTrue(success);
    }

    /**
     * Tests the conversion of a list
     * @throws ParseException in case a issue while parsing the json
     */
    @Test
    public void testConvertList() throws ParseException {
        ActiveTenant activeTenant = ActiveTenantFactory.create(2L,2L);

        JsonArrayBuilder builder = Json.createArrayBuilder();
        builder.add(ActiveTenantFactory.convertToJsonObject(activeTenant));

        JsonArray array = builder.build();

        List<ActiveTenant> listOfTenant = new ArrayList<>();
        listOfTenant.add(activeTenant);

        assertEquals(listOfTenant.size(), ActiveTenantFactory.convert(array).size());
    }

    /**
     * Tests the conversion from a object into a json
     */
    @Test
    public void testConvertToJsonObject() {
        JsonObject constructedNewJson = ActiveTenantFactory.convertToJsonObject(activeTenant);

        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.addNull("id");
        builder.add("userId", 2L);
        builder.add("tenantId", 2L);

        json = builder.build();

        assertEquals(json.toString(), constructedNewJson.toString());
    }
}