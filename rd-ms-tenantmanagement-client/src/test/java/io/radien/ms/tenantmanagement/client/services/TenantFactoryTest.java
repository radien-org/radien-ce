/*
 * Copyright (c) 2021-present radien GmbH. All rights reserved.
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

import io.radien.exception.GenericErrorCodeMessage;
import io.radien.ms.tenantmanagement.client.entities.Tenant;
import io.radien.ms.tenantmanagement.client.entities.TenantType;
import junit.framework.TestCase;
import org.junit.Test;

import javax.json.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Bruno Gama
 */
public class TenantFactoryTest extends TestCase {

    Tenant tenant = new Tenant();
    JsonObject json;

    public TenantFactoryTest() {
        tenant.setName("nameValue");
        tenant.setCreateUser(2L);
        tenant.setTenantKey("tenantKey");
        tenant.setTenantType(TenantType.ROOT_TENANT.getName());
        tenant.setTenantStart(null);
        tenant.setTenantEnd(null);
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
        builder.add("tenantKey", "tenantKey");
        builder.add("tenantType", TenantType.ROOT_TENANT.getName());
        builder.addNull("tenantStart");
        builder.addNull("tenantEnd");
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
    public void testConvertIllegalArgumentException() throws ParseException {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.addNull("id");
        builder.add("name", "nameValue");
        builder.add("tenantKey", "tenantkey");
        builder.add("tenantType", "TEST");
        builder.addNull("tenantStart");
        builder.addNull("tenantEnd");
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
        boolean success = false;
        try{
            TenantFactory.convert(json);
        } catch (IllegalStateException e) {
            success=true;
        }

        assertTrue(success);
    }

    @Test
    public void testConvertNonNullDates() throws ParseException {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.addNull("id");
        builder.add("name", "nameValue");
        builder.add("tenantKey", "tenantKey");
        builder.add("tenantType", TenantType.ROOT_TENANT.getName());
        builder.add("tenantStart", "2020-03-03");
        builder.add("tenantEnd", "2021-03-03");
        builder.addNull("clientAddress");
        builder.addNull("clientZipCode");
        builder.addNull("clientCity");
        builder.addNull("clientCountry");
        builder.addNull("clientPhoneNumber");
        builder.addNull("clientEmail");
        builder.addNull("parentId");
        builder.addNull("clientId");
        builder.addNull("lastUpdateUser");
        builder.add("createDate", "03-03-2020 10:10:10");
        builder.add("lastUpdate", "03-03-2020 10:10:10");
        builder.add("createUser", 2L);

        json = builder.build();
        Tenant newJsonRole = TenantFactory.convert(json);

        assertEquals(tenant.getName(), newJsonRole.getName());
        assertEquals(tenant.getCreateUser(), newJsonRole.getCreateUser());
    }

    @Test
    public void testConvertList() throws ParseException {
        List<Tenant> listOfCreatedTenants = new ArrayList<>();

        Tenant tenant = TenantFactory.create("name", "tenantKey", TenantType.ROOT_TENANT.getName(),
                null, null, null, null, null, null, null,
                null, null, null, null);

        JsonArrayBuilder builder = Json.createArrayBuilder();
        builder.add(TenantFactory.convertToJsonObject(tenant));

        JsonArray array = builder.build();

        List<Tenant> listOfTenant = new ArrayList<>();
        listOfTenant.add(tenant);

        assertEquals(listOfTenant.size(), TenantFactory.convert(array).size());
    }

    @Test
    public void testConvertToJsonObject() {
        JsonObject constructedNewJson = TenantFactory.convertToJsonObject(tenant);

        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.addNull("id");
        builder.add("name", "nameValue");
        builder.add("tenantKey", "tenantKey");
        builder.add("tenantType", TenantType.ROOT_TENANT.getName());
        builder.addNull("tenantStart");
        builder.addNull("tenantEnd");
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

    @Test
    public void testConvertEmptyType() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.addNull("id");
        builder.add("name", "nameValue");
        builder.add("tenantKey", "tenantKey");

        json = builder.build();

        Exception exception = assertThrows(IllegalStateException.class, () -> TenantFactory.convert(json));
        assertEquals(exception.getMessage(), "Field type is mandatory");
    }

    @Test
    public void testConvertNullType() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.addNull("id");
        builder.add("name", "nameValue");
        builder.add("tenantKey", "tenantKey");
        builder.add("tenantType", "test");

        json = builder.build();

        Exception exception = assertThrows(IllegalStateException.class, () -> TenantFactory.convert(json));
        assertEquals(exception.getMessage(), GenericErrorCodeMessage.TENANT_TYPE_NOT_FOUND.toString());
    }
}