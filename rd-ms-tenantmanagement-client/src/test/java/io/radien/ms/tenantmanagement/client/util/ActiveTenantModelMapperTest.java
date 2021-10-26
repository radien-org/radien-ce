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
package io.radien.ms.tenantmanagement.client.util;

import io.radien.api.entity.Page;
import io.radien.ms.tenantmanagement.client.entities.ActiveTenant;
import io.radien.ms.tenantmanagement.client.services.ActiveTenantFactory;
import org.junit.Test;

import javax.json.JsonObject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Active Tenant Model Mapper Test
 * {@link io.radien.ms.tenantmanagement.client.util.ActiveTenantModelMapper}
 *
 * @author Bruno Gama
 **/
public class ActiveTenantModelMapperTest {

    /**
     * Tests the conversion between a input stream into a object
     * @throws ParseException in case a issue while parsing the json
     */
    @Test
    public void testMapInputStream() throws ParseException {
        String result = "{\"" +
                "id\":null," +
                "\"userId\": 2," +
                "\"tenantId\": 2," +
                "\"tenantName\": \"test\"," +
                "\"isTenantActive\": true" +
                "}";
        InputStream in = new ByteArrayInputStream(result.getBytes());
        ActiveTenant activeTenant = ActiveTenantModelMapper.map(in);
        assertEquals((Long) 2L, activeTenant.getUserId());
    }

    /**
     * Tests the conversion between a input stream into a json
     */
    @Test
    public void testMapJsonObject() {
        ActiveTenant activeTenant = ActiveTenantFactory.create(2L, 2L);
        JsonObject jsonObject = ActiveTenantModelMapper.map(activeTenant);

        assertEquals(activeTenant.getUserId().toString(),jsonObject.get("userId").toString());
        assertEquals(activeTenant.getTenantId().toString(),jsonObject.get("tenantId").toString());
    }

    /**
     * Tests the conversion between a input stream into a page
     */
    @Test
    public void testMapInputStreamToPage() {
        String example = "{\n" +
                "\"currentPage\": 0,\n" +
                "\"results\": [\n" +
                "{\n" +
                "\"id\": null,\n" +
                "\"userId\": 2,\n" +
                "\"tenantId\": 2,\n" +
                "\"tenantName\": \"test\"\n," +
                "\"isTenantActive\": true\n" +
                "}\n" +
                "],\n" +
                "\"totalPages\": 1,\n" +
                "\"totalResults\": 4\n" +
                "}";
        InputStream in = new ByteArrayInputStream(example.getBytes());
        Page<ActiveTenant> activeTenantPage = ActiveTenantModelMapper.mapToPage(in);
        assertEquals(0, activeTenantPage.getCurrentPage());
        assertEquals(1, activeTenantPage.getTotalPages());
        assertEquals(4, activeTenantPage.getTotalResults());
    }

    /**
     * Tests the conversion from a input stream into a list of active tenants
     * @throws ParseException in case of issue while parsing the inputted stream
     */
    @Test
    public void testConversionToList() throws ParseException {
        String result = "[\n" +
                "{\n" +
                "\"id\": null,\n" +
                "\"userId\": 2,\n" +
                "\"tenantId\": 2,\n" +
                "\"tenantName\": \"test\"\n," +
                "\"isTenantActive\": true\n" +
                "}\n" +
                "]";
        InputStream in = new ByteArrayInputStream(result.getBytes());
        List<? extends ActiveTenant> activeTenants = ActiveTenantModelMapper.mapList(in);
        assertNotNull(activeTenants);
        assertEquals(1, activeTenants.size());
    }
}