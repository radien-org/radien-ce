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
package io.radien.ms.tenantmanagement.client.services;

import io.radien.api.entity.Page;
import io.radien.ms.tenantmanagement.client.entities.TenantRole;
import org.junit.Test;

import javax.json.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Newton Carvalho
 */
public class TenantRoleFactoryTest {

    TenantRole tenantRole = new TenantRole();
    JsonObject json;

    Long tenantId = 22L;
    Long roleId = 25L;

    public TenantRoleFactoryTest() {
        tenantRole.setTenantId(tenantId);
        tenantRole.setRoleId(roleId);
        tenantRole.setCreateUser(2L);
        tenantRole.setLastUpdateUser(6L);
    }

    @Test
    public void testCreate() {
        TenantRole newRoleConstructed = TenantRoleFactory.create(tenantId, roleId, 2L);
        assertEquals(tenantRole.getTenantId(), newRoleConstructed.getTenantId());
        assertEquals(tenantRole.getRoleId(), newRoleConstructed.getRoleId());
        assertEquals(tenantRole.getCreateUser(), newRoleConstructed.getCreateUser());
    }

    @Test
    public void testConvert() throws ParseException {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.addNull("id");
        builder.add("tenantId", tenantId);
        builder.add("roleId", roleId);

        builder.add("createUser", 2L);
        builder.add("lastUpdateUser", 6L);

        json = builder.build();
        TenantRole newJsonRole = TenantRoleFactory.convert(json);

        assertEquals(tenantRole.getRoleId(), newJsonRole.getRoleId());
        assertEquals(tenantRole.getTenantId(), newJsonRole.getTenantId());
        assertEquals(tenantRole.getCreateUser(), newJsonRole.getCreateUser());
        assertEquals(tenantRole.getLastUpdateUser(), newJsonRole.getLastUpdateUser());
    }

    @Test
    public void testConvertList() throws ParseException {
        List<TenantRole> listOfCreatedTenantRoles = new ArrayList<>();

        TenantRole tenant = TenantRoleFactory.create(1L, 2L, 3L);

        JsonArrayBuilder builder = Json.createArrayBuilder();
        builder.add(TenantRoleFactory.convertToJsonObject(tenant));

        JsonArray array = builder.build();

        List<TenantRole> listOfTenantRole = new ArrayList<>();
        listOfTenantRole.add(tenant);

        assertEquals(listOfTenantRole.size(), TenantRoleFactory.convert(array).size());
    }

    @Test
    public void testConvertToJsonObject() {
        JsonObject constructedNewJson = TenantRoleFactory.convertToJsonObject(tenantRole);

        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.addNull("id");
        builder.add("tenantId", tenantId);
        builder.add("roleId", roleId);
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
                "\"tenantId\": 2,\n" +
                "\"roleId\": 3\n" +
                "}\n" +
                "],\n" +
                "\"totalPages\": 1,\n" +
                "\"totalResults\": 4\n" +
                "}";
        InputStream in = new ByteArrayInputStream(example.getBytes());

        try(JsonReader reader = Json.createReader(in)) {
            JsonObject jsonObject = reader.readObject();
            Page<TenantRole> tenant = TenantRoleFactory.convertJsonToPage(jsonObject);
            assertEquals(1, tenant.getCurrentPage());
            assertEquals(1, tenant.getTotalPages());
            assertEquals(4, tenant.getTotalResults());
        }
    }
}