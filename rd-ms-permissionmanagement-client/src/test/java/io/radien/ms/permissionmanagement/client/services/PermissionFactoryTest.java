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
package io.radien.ms.permissionmanagement.client.services;

import org.junit.Test;

import io.radien.ms.permissionmanagement.client.entities.Permission;
import io.radien.ms.permissionmanagement.client.services.PermissionFactory;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import static org.junit.Assert.*;

public class PermissionFactoryTest {

    Permission permission;
    JsonObject json;

    /**
     * Constructor class method were we are going to create the JSON and the permission for
     * testing purposes.
     */
    public PermissionFactoryTest() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.addNull("id");
        builder.add("name", "permission-bbb");
        builder.add("createUser", 2L);

        json = builder.build();
        permission = PermissionFactory.create("permission-bbb", 2L);
    }

    /**
     * Test method to validate the creation of a Permission using a Json
     */
    @Test
    public void create() {
        PermissionFactory permissionFactory = new PermissionFactory();
        Permission constructedNewPermission = permissionFactory.create("permission-bbb", 2L);

        assertEquals(permission.getId(), constructedNewPermission.getId());
        assertEquals(permission.getCreateUser(), constructedNewPermission.getCreateUser());
        assertEquals(permission.getLastUpdateUser(), constructedNewPermission.getLastUpdateUser());
    }

    /**
     * Test method to validate the conversion of a Permission using a Json
     */
    @Test
    public void convert() {
        Permission newPermission = PermissionFactory.convert(json);

        assertEquals(permission.getId(), newPermission.getId());
        assertEquals(permission.getCreateUser(), newPermission.getCreateUser());
        assertEquals(permission.getLastUpdateUser(), newPermission.getLastUpdateUser());
        assertEquals(permission.getName(), newPermission.getName());
    }


}
