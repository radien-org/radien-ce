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
package io.radien.ms.rolemanagement.client.util;

import io.radien.api.entity.Page;
import io.radien.ms.rolemanagement.client.entities.TenantRole;
import io.radien.ms.rolemanagement.client.services.TenantRoleFactory;
import org.junit.Test;

import javax.json.JsonObject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

/**
 * @author Newton Carvalho
 **/
public class TenantRoleModelMapperTest {
    Long tenantId = 22L;
    Long roleId = 25L;
    @Test
    public void testMapInputStream() throws ParseException {
        String example = "{\n" +
                "\"tenantId\": 1,\n" +
                "\"roleId\": 2\n" +
                "}";
        InputStream in = new ByteArrayInputStream(example.getBytes());
        TenantRole tenantRole = TenantRoleModelMapper.map(in);
        assertEquals((Long) 1L, tenantRole.getTenantId());
        assertEquals((Long) 2L, tenantRole.getRoleId());
    }

    @Test
    public void testMapJsonObject() {
        TenantRole tenantRole = TenantRoleFactory.create(tenantId, roleId, 2L);
        JsonObject jsonObject = TenantRoleModelMapper.map(tenantRole);
        assertEquals(tenantRole.getTenantId(), (Long)jsonObject.getJsonNumber("tenantId").longValue());
        assertEquals(tenantRole.getRoleId(), (Long)jsonObject.getJsonNumber("roleId").longValue());
    }

    @Test
    public void testMapInputStreamToPage() {
        String example = "{\n" +
                "\"currentPage\": 1,\n" +
                "\"results\": [\n" +
                "{\n" +
                "\"tenantId\": 1,\n" +
                "\"roleId\": 2\n" +
                "}\n" +
                "],\n" +
                "\"totalPages\": 1,\n" +
                "\"totalResults\": 4\n" +
                "}";
        InputStream in = new ByteArrayInputStream(example.getBytes());
        Page<TenantRole> tenantRole = TenantRoleModelMapper.mapToPage(in);
        assertEquals(1, tenantRole.getCurrentPage());
        assertEquals(1, tenantRole.getTotalPages());
        assertEquals(4, tenantRole.getTotalResults());
    }

    @Test
    public void testConversionToCollection() {
        String example = "[\n" +
                "{\n" +
                "\"id\": 1,\n" +
                "\"tenantId\": 2,\n" +
                "\"roleId\": 3\n" +
                "}\n" +
                "]";
        InputStream in = new ByteArrayInputStream(example.getBytes());
        List<? extends TenantRole> tenantRoles = TenantRoleModelMapper.mapList(in);
        assertNotNull(tenantRoles);
        assertTrue(tenantRoles.size() == 1);
    }
}