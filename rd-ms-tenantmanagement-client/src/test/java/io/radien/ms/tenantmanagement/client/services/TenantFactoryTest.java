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

import io.radien.ms.tenantmanagement.client.entities.Tenant;
import io.radien.ms.tenantmanagement.client.entities.TenantType;
import junit.framework.TestCase;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.text.ParseException;

/**
 * @author Bruno Gama
 */
public class TenantFactoryTest extends TestCase {

    Tenant tenant = new Tenant();
    JsonObject json;

    public TenantFactoryTest() {
        tenant.setName("nameValue");
        tenant.setCreateUser(2L);
        tenant.setKey("key");
        tenant.setType(TenantType.ROOT_TENANT);
        tenant.setStart(null);
        tenant.setEnd(null);
    }

    @Test
    public void testCreate() {
        TenantFactory roleFactory = new TenantFactory();
        Tenant newRoleConstructed = roleFactory.create("nameValue", null, null, null, null, null, null, null, null, null, null, null, null, 2L);

        assertEquals(tenant.getName(), newRoleConstructed.getName());
        assertEquals(tenant.getCreateUser(), newRoleConstructed.getCreateUser());
    }

    @Test
    public void testConvert() throws ParseException {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.addNull("id");
        builder.add("name", "nameValue");
        builder.add("key", "key");
        builder.add("type", TenantType.ROOT_TENANT.getName());
        builder.addNull("start");
        builder.addNull("end");
        builder.addNull("clientAddress");
        builder.addNull("clientZipCode");
        builder.addNull("clientCity");
        builder.addNull("clientCountry");
        builder.addNull("clientPhoneNumber");
        builder.addNull("clientEmail");
        builder.addNull("parentId");
        builder.addNull("clientId");
        builder.addNull("lastUpdateUser");
        builder.addNull("createDate");
        builder.addNull("lastUpdate");
        builder.add("createUser", 2L);

        json = builder.build();
        Tenant newJsonRole = TenantFactory.convert(json);

        assertEquals(tenant.getName(), newJsonRole.getName());
        assertEquals(tenant.getCreateUser(), newJsonRole.getCreateUser());
    }

    @Test
    public void testConvertToJsonObject() {
        JsonObject constructedNewJson = TenantFactory.convertToJsonObject(tenant);

        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.addNull("id");
        builder.add("name", "nameValue");
        builder.add("key", "key");
        builder.add("type", TenantType.ROOT_TENANT.getName());
        builder.addNull("start");
        builder.addNull("end");
        builder.addNull("clientAddress");
        builder.addNull("clientZipCode");
        builder.addNull("clientCity");
        builder.addNull("clientCountry");
        builder.addNull("clientPhoneNumber");
        builder.addNull("clientEmail");
        builder.addNull("parentId");
        builder.addNull("clientId");
        builder.add("createUser", 2L);
        builder.addNull("lastUpdateUser");
        builder.addNull("createDate");
        builder.addNull("lastUpdate");

        json = builder.build();

        assertEquals(json.toString(), constructedNewJson.toString());
    }
}