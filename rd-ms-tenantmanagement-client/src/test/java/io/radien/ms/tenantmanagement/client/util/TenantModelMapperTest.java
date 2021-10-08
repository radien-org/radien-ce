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
package io.radien.ms.tenantmanagement.client.util;

import io.radien.api.entity.Page;
import io.radien.ms.tenantmanagement.client.entities.Tenant;
import io.radien.ms.tenantmanagement.client.entities.TenantType;
import io.radien.ms.tenantmanagement.client.services.TenantFactory;
import org.junit.Test;
import javax.json.JsonObject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.time.LocalDate;

import static org.junit.Assert.*;

/**
 * @author Bruno Gama
 **/
public class TenantModelMapperTest {

    @Test
    public void testMapInputStream() throws ParseException {
        String example = "{\n" +
                "\"name\": \"a\",\n" +
                "\"tentKey\": \"key\",\n" +
                "\"tenantType\": \"root\",\n" +
                "\"start\": \"2021-01-29\",\n" +
                "\"end\": \"2021-01-29\"\n" +
                "}";
        InputStream in = new ByteArrayInputStream(example.getBytes());
        Tenant tenant = TenantModelMapper.map(in);
        assertEquals("a", tenant.getName());
    }

    @Test
    public void testMapJsonObject() {
        Tenant tenant = TenantFactory.create("a", "tenantKey", TenantType.ROOT, LocalDate.now(), null,
                null, null, null, null, null, null, null, null, 1L);
        JsonObject jsonObject = TenantModelMapper.map(tenant);

        assertEquals(tenant.getName(),jsonObject.getString("name"));
        assertEquals(tenant.getTenantStart().toString(),jsonObject.getString("tenantStart"));
    }

    @Test
    public void testMapInputStreamToPage() {
        String example = "{\n" +
                "\"currentPage\": 0,\n" +
                "\"results\": [\n" +
                "{\n" +
                "\"name\": \"a\",\n" +
                "\"tenantKey\": \"key\",\n" +
                "\"tenantType\": \"root\",\n" +
                "\"tenantStart\": \"2021-01-29\",\n" +
                "\"tenantEnd\": \"2021-01-29\"\n" +
                "}\n" +
                "],\n" +
                "\"totalPages\": 1,\n" +
                "\"totalResults\": 4\n" +
                "}";
        InputStream in = new ByteArrayInputStream(example.getBytes());
        Page<Tenant> tenant = TenantModelMapper.mapToPage(in);
        assertEquals(0, tenant.getCurrentPage());
        assertEquals(1, tenant.getTotalPages());
        assertEquals(4, tenant.getTotalResults());
    }

    @Test
    public void testMapInputStreamToPageParseException() {
        String example = "{\n" +
                "\"currentPage\": 0,\n" +
                "\"results\": [\n" +
                "{\n" +
                "\"name\": \"a\",\n" +
                "\"tenantKey\": \"key\",\n" +
                "\"tenantType\": \"root\",\n" +
                "\"createDate\": \"a\",\n" +
                "\"start\": \"2021-01-29\",\n" +
                "\"end\": \"2021-01-29\"\n" +
                "}\n" +
                "],\n" +
                "\"totalPages\": 1,\n" +
                "\"totalResults\": 4\n" +
                "}";
        InputStream in = new ByteArrayInputStream(example.getBytes());
        Page<Tenant> tenant = TenantModelMapper.mapToPage(in);

        assertNull(tenant);
    }
}