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
package io.radien.ms.rolemanagement.client.util;

import io.radien.api.entity.Page;
import io.radien.ms.rolemanagement.client.entities.TenantRoleUser;
import io.radien.ms.rolemanagement.client.services.TenantRoleUserFactory;
import org.junit.Test;

import javax.json.JsonObject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test the mapping information from and to a Json or a Tenant Role User object association
 * @author Newton Carvalho
 **/
public class TenantRoleUserModelMapperTest {
    Long tenantRoleId = 22L;
    Long userId = 25L;

    /**
     * Test the conversion from a input stream to a Tenant Role User Association
     */
    @Test
    public void testMapInputStream() {
        String example = "{\n" +
                "\"tenantRoleId\": 1,\n" +
                "\"userId\": 2\n" +
                "}";
        InputStream in = new ByteArrayInputStream(example.getBytes());
        TenantRoleUser tenantRole = TenantRoleUserModelMapper.map(in);
        assertEquals((Long) 1L, tenantRole.getTenantRoleId());
        assertEquals((Long) 2L, tenantRole.getUserId());
    }

    /**
     * Test the mapping conversion from a tenant role user object into a json
     */
    @Test
    public void testMapJsonObject() {
        TenantRoleUser tenantRole = TenantRoleUserFactory.create(tenantRoleId, userId, 2L);
        JsonObject jsonObject = TenantRoleUserModelMapper.map(tenantRole);
        assertEquals(tenantRole.getTenantRoleId(), (Long)jsonObject.getJsonNumber("tenantRoleId").longValue());
        assertEquals(tenantRole.getUserId(), (Long)jsonObject.getJsonNumber("userId").longValue());
    }

    /**
     * Test the conversion of a input stream page into a page of objects
     */
    @Test
    public void testMapInputStreamToPage() {
        String example = "{\n" +
                "\"currentPage\": 1,\n" +
                "\"results\": [\n" +
                "{\n" +
                "\"tenantRoleId\": 1,\n" +
                "\"userId\": 2\n" +
                "}\n" +
                "],\n" +
                "\"totalPages\": 1,\n" +
                "\"totalResults\": 4\n" +
                "}";
        InputStream in = new ByteArrayInputStream(example.getBytes());
        Page<TenantRoleUser> tenantRolePermission = TenantRoleUserModelMapper.mapToPage(in);
        assertEquals(1, tenantRolePermission.getCurrentPage());
        assertEquals(1, tenantRolePermission.getTotalPages());
        assertEquals(4, tenantRolePermission.getTotalResults());
    }

    /**
     * Test the conversion of a collection of tenant roles
     */
    @Test
    public void testConversionToCollection() {
        String example = "[\n" +
                "{\n" +
                "\"id\": 1,\n" +
                "\"tenantRoleId\": 2,\n" +
                "\"userId\": 3\n" +
                "}\n" +
                "]";
        InputStream in = new ByteArrayInputStream(example.getBytes());
        List<? extends TenantRoleUser> tenantRoles = TenantRoleUserModelMapper.mapList(in);
        assertNotNull(tenantRoles);
        assertEquals(1, tenantRoles.size());
    }
}