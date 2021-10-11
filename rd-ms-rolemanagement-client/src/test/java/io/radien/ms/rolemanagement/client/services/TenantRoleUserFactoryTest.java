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
import io.radien.ms.rolemanagement.client.entities.TenantRoleUser;
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
public class TenantRoleUserFactoryTest {

    TenantRoleUser tenantRoleUser = new TenantRoleUser();
    JsonObject json;

    Long tenantRoleId = 33L;
    Long userId = 34L;

    public TenantRoleUserFactoryTest() {
        tenantRoleUser.setTenantRoleId(tenantRoleId);
        tenantRoleUser.setUserId(userId);
        tenantRoleUser.setCreateUser(2L);
        tenantRoleUser.setLastUpdateUser(6L);
    }

    @Test
    public void testCreate() {
        TenantRoleUserFactory roleFactory = new TenantRoleUserFactory();
        TenantRoleUser newRoleConstructed = roleFactory.create(tenantRoleId, userId, 2L);
        assertEquals(tenantRoleUser.getTenantRoleId(), newRoleConstructed.getTenantRoleId());
        assertEquals(tenantRoleUser.getUserId(), newRoleConstructed.getUserId());
        assertEquals(tenantRoleUser.getCreateUser(), newRoleConstructed.getCreateUser());
    }

    @Test
    public void testConvert() throws ParseException {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.addNull("id");
        builder.add("tenantRoleId", tenantRoleId);
        builder.add("userId", userId);

        builder.add("createUser", 2L);
        builder.add("lastUpdateUser", 6L);

        json = builder.build();
        TenantRoleUser newJsonRole = TenantRoleUserFactory.convert(json);

        assertEquals(tenantRoleUser.getUserId(), newJsonRole.getUserId());
        assertEquals(tenantRoleUser.getTenantRoleId(), newJsonRole.getTenantRoleId());
        assertEquals(tenantRoleUser.getCreateUser(), newJsonRole.getCreateUser());
        assertEquals(tenantRoleUser.getLastUpdateUser(), newJsonRole.getLastUpdateUser());
    }

    @Test
    public void testConvertList() {
        List<TenantRoleUser> listOfCreatedTenantRoleUsers = new ArrayList<>();

        TenantRoleUser tenant = TenantRoleUserFactory.create(1L, 2L, 3L);

        JsonArrayBuilder builder = Json.createArrayBuilder();
        builder.add(TenantRoleUserFactory.convertToJsonObject(tenant));

        JsonArray array = builder.build();

        List<TenantRoleUser> listOfTenantRoleUser = new ArrayList<>();
        listOfTenantRoleUser.add(tenant);

        assertEquals(listOfTenantRoleUser.size(), TenantRoleUserFactory.convert(array).size());
    }

    @Test
    public void testConvertToJsonObject() {
        JsonObject constructedNewJson = TenantRoleUserFactory.convertToJsonObject(tenantRoleUser);

        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.addNull("id");
        builder.add("tenantRoleId", tenantRoleId);
        builder.add("userId", userId);
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
                "\"userId\": 3\n" +
                "}\n" +
                "],\n" +
                "\"totalPages\": 1,\n" +
                "\"totalResults\": 4\n" +
                "}";
        InputStream in = new ByteArrayInputStream(example.getBytes());

        try(JsonReader reader = Json.createReader(in)) {
            JsonObject jsonObject = reader.readObject();
            Page<TenantRoleUser> tenant = TenantRoleUserFactory.convertJsonToPage(jsonObject);
            assertEquals(1, tenant.getCurrentPage());
            assertEquals(1, tenant.getTotalPages());
            assertEquals(4, tenant.getTotalResults());
        }
    }

}