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
package io.radien.ms.permissionmanagement.client.entities;

import io.radien.ms.permissionmanagement.client.services.ActionFactory;
import org.junit.Test;

import io.radien.ms.permissionmanagement.client.services.PermissionFactory;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import static org.junit.Assert.*;

public class PermissionFactoryTest2 {

    Permission permission;
    Action action;
    JsonObject json;

    /**
     * Constructor class method were we are going to create the JSON and the permission for
     * testing purposes.
     */
    public PermissionFactoryTest2() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.addNull("id");
        builder.add("name", "permission-bbb");
        builder.add("createUser", 2L);

        //builder.add("actionType", String.valueOf(ActionType.WRITE));
        action = ActionFactory.create("Add new user to radien", ActionType.WRITE, 28L);
        action.setId(100L);
        JsonObject actionJsonObject = Json.createObjectBuilder().
                add("id", action.getId()).
                add("name", action.getName()).
                add("type", action.getType().getName()).build();
        builder.add("action", actionJsonObject);

        json = builder.build();
        permission = PermissionFactory.create("permission-bbb",
                action, 2L);
    }

    /**
     * Test method to validate the creation of a Permission using a Json
     */
    @Test
    public void create() {
        Permission constructedNewPermission = PermissionFactory.create("permission-bbb",
                        action, 2L);

        assertEquals(permission.getId(), constructedNewPermission.getId());
        assertEquals(permission.getCreateUser(), constructedNewPermission.getCreateUser());
        assertEquals(permission.getLastUpdateUser(), constructedNewPermission.getLastUpdateUser());
        assertEquals(permission.getAction().getName(), constructedNewPermission.getAction().getName());
        assertEquals(permission.getAction().getType(), constructedNewPermission.getAction().getType());
        assertEquals(permission.getAction().getId(), constructedNewPermission.getAction().getId());
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

        assertEquals(permission.getAction().getName(), newPermission.getAction().getName());
        assertEquals(permission.getAction().getType(), newPermission.getAction().getType());
        assertEquals(permission.getAction().getId(), newPermission.getAction().getId());
    }
}
