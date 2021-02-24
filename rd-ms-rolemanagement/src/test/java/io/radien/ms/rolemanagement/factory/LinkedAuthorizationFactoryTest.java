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
package io.radien.ms.rolemanagement.factory;

import io.radien.ms.rolemanagement.entities.LinkedAuthorization;
import io.radien.ms.rolemanagement.factory.LinkedAuthorizationFactory;
import junit.framework.TestCase;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 * @author Bruno Gama
 */
public class LinkedAuthorizationFactoryTest extends TestCase {

    LinkedAuthorization linkedAuthorization = new LinkedAuthorization();
    JsonObject json;

    public LinkedAuthorizationFactoryTest() {
        linkedAuthorization.setTenantId(2L);
        linkedAuthorization.setPermissionId(2L);
        linkedAuthorization.setRoleId(2L);
    }

    @Test
    public void testCreate() {
        LinkedAuthorizationFactory linkedAuthorizationFactory = new LinkedAuthorizationFactory();
        LinkedAuthorization newAuthorizationConstructed = linkedAuthorizationFactory.create(2L, 2L, 2L, null);

        assertEquals(linkedAuthorization.getTenantId(), newAuthorizationConstructed.getTenantId());
        assertEquals(linkedAuthorization.getPermissionId(), newAuthorizationConstructed.getPermissionId());
        assertEquals(linkedAuthorization.getRoleId(), newAuthorizationConstructed.getRoleId());
        assertEquals(linkedAuthorization.getCreateUser(), newAuthorizationConstructed.getCreateUser());
    }

    @Test
    public void testConvert() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.addNull("id");
        builder.add("tenantId", 2L);
        builder.add("permissionId", 2L);
        builder.add("roleId", 2L);
        builder.addNull("createdUser");

        json = builder.build();
        LinkedAuthorization newJsonAuthorization = LinkedAuthorizationFactory.convert(json);

        assertEquals(linkedAuthorization.getTenantId(), newJsonAuthorization.getTenantId());
        assertEquals(linkedAuthorization.getPermissionId(), newJsonAuthorization.getPermissionId());
        assertEquals(linkedAuthorization.getRoleId(), newJsonAuthorization.getRoleId());
        assertEquals(linkedAuthorization.getCreateUser(), newJsonAuthorization.getCreateUser());
    }

    @Test
    public void testConvertToJsonObject() {
        JsonObject constructedNewJson = LinkedAuthorizationFactory.convertToJsonObject(linkedAuthorization);

        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.addNull("id");
        builder.add("tenantId", 2L);
        builder.add("permissionId", 2L);
        builder.add("roleId", 2L);
        builder.addNull("createdUser");

        json = builder.build();

        assertEquals(json.toString(), constructedNewJson.toString());
    }
}