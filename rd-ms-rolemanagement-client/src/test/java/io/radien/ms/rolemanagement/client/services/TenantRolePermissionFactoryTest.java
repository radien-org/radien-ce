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
package io.radien.ms.rolemanagement.client.services;

import io.radien.api.entity.Page;
import io.radien.ms.rolemanagement.client.entities.TenantRolePermission;
import org.junit.Test;

import javax.json.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Newton Carvalho
 */
public class TenantRolePermissionFactoryTest {

    TenantRolePermission tenantRolePermission = new TenantRolePermission();
    JsonObject json;

    Long tenantRoleId = 33L;
    Long permissionId = 34L;

    public TenantRolePermissionFactoryTest() {
        tenantRolePermission.setTenantRoleId(tenantRoleId);
        tenantRolePermission.setPermissionId(permissionId);
        tenantRolePermission.setCreateUser(2L);
        tenantRolePermission.setLastUpdateUser(6L);
    }

    @Test
    public void testCreate() {
        TenantRolePermissionFactory roleFactory = new TenantRolePermissionFactory();
        TenantRolePermission newRoleConstructed = roleFactory.create(tenantRoleId, permissionId, 2L);
        assertEquals(tenantRolePermission.getTenantRoleId(), newRoleConstructed.getTenantRoleId());
        assertEquals(tenantRolePermission.getPermissionId(), newRoleConstructed.getPermissionId());
        assertEquals(tenantRolePermission.getCreateUser(), newRoleConstructed.getCreateUser());
    }

    @Test
    public void testConvert() throws ParseException {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.addNull("id");
        builder.add("tenantRoleId", tenantRoleId);
        builder.add("permissionId", permissionId);

        builder.add("createUser", 2L);
        builder.add("lastUpdateUser", 6L);

        json = builder.build();
        TenantRolePermission newJsonRole = TenantRolePermissionFactory.convert(json);

        assertEquals(tenantRolePermission.getPermissionId(), newJsonRole.getPermissionId());
        assertEquals(tenantRolePermission.getTenantRoleId(), newJsonRole.getTenantRoleId());
        assertEquals(tenantRolePermission.getCreateUser(), newJsonRole.getCreateUser());
        assertEquals(tenantRolePermission.getLastUpdateUser(), newJsonRole.getLastUpdateUser());
    }

    @Test
    public void testConvertList() {
        List<TenantRolePermission> listOfCreatedTenantRolePermissions = new ArrayList<>();

        TenantRolePermission tenant = TenantRolePermissionFactory.create(1L, 2L, 3L);

        JsonArrayBuilder builder = Json.createArrayBuilder();
        builder.add(TenantRolePermissionFactory.convertToJsonObject(tenant));

        JsonArray array = builder.build();

        List<TenantRolePermission> listOfTenantRolePermission = new ArrayList<>();
        listOfTenantRolePermission.add(tenant);

        assertEquals(listOfTenantRolePermission.size(), TenantRolePermissionFactory.convert(array).size());
    }

    @Test
    public void testConvertToJsonObject() {
        JsonObject constructedNewJson = TenantRolePermissionFactory.convertToJsonObject(tenantRolePermission);

        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.addNull("id");
        builder.add("tenantRoleId", tenantRoleId);
        builder.add("permissionId", permissionId);
        builder.add("createUser", 2L);
        builder.add("lastUpdateUser", 6L);
        builder.addNull("createDate");
        builder.addNull("lastUpdate");

        json = builder.build();

        assertEquals(json.toString(), constructedNewJson.toString());
    }

    @Test
    public void testConversionToPage() {
        String example = "{\n" +
                "\"currentPage\": 1,\n" +
                "\"results\": [\n" +
                "{\n" +
                "\"id\": 1,\n" +
                "\"tenantRoleId\": 2,\n" +
                "\"permissionId\": 3\n" +
                "}\n" +
                "],\n" +
                "\"totalPages\": 1,\n" +
                "\"totalResults\": 4\n" +
                "}";
        InputStream in = new ByteArrayInputStream(example.getBytes());

        try(JsonReader reader = Json.createReader(in)) {
            JsonObject jsonObject = reader.readObject();
            Page<TenantRolePermission> tenant = TenantRolePermissionFactory.convertJsonToPage(jsonObject);
            assertEquals(1, tenant.getCurrentPage());
            assertEquals(1, tenant.getTotalPages());
            assertEquals(4, tenant.getTotalResults());
        }
    }

}