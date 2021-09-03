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
package io.radien.ms.permissionmanagement.client.entities;

import io.radien.ms.permissionmanagement.client.services.ActionFactory;
import io.radien.ms.permissionmanagement.client.services.PermissionFactory;
import org.junit.Assert;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class PermissionFactoryTest {

    JsonObject json;
    Permission perm;
    Action action;

    /**
     * Constructor class method were we are going to create the JSON and the permission for
     * testing purposes.
     */
    public PermissionFactoryTest() {
        action = ActionFactory.create("add-new-user", 28L);
        action.setId(100L);

        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.addNull("id");
        builder.add("name", "permissionTest");
        builder.add("createUser", 2L);
        builder.addNull("lastUpdateUser");
        builder.add("actionId", action.getId());
        builder.addNull("resourceId");
        json = builder.build();
        perm = PermissionFactory.create("permissionTest", action.getId(), null, 2L);
    }

    /**
     * Test method to validate the creation of a Permission using a Json
     */
    @Test
    public void testCreate() {
        Assert.assertEquals("permissionTest", perm.getName());
        Assert.assertEquals((Long) 2L, perm.getCreateUser());
        Assert.assertNotNull(perm.getActionId());
        Assert.assertEquals(perm.getActionId(), action.getId());
        Assert.assertNull(perm.getResourceId());
    }

    /**
     * Test method to validate the conversion of a Permission using a Json
     */
    @Test
    public void testConvert() {
        Permission constructedNewPermission = PermissionFactory.convert(json);
        Assert.assertEquals(perm.getId(), constructedNewPermission.getId());
        Assert.assertEquals(perm.getName(), constructedNewPermission.getName());
        Assert.assertEquals(perm.getCreateUser(), constructedNewPermission.getCreateUser());
        Assert.assertEquals(perm.getLastUpdateUser(), constructedNewPermission.getLastUpdateUser());
        Assert.assertEquals(perm.getActionId(), constructedNewPermission.getActionId());
        Assert.assertEquals(perm.getResourceId(), constructedNewPermission.getResourceId());
    }

    /**
     * Test method to validate the conversion of a Json Object using a User
     */
    @Test
    public void testConvertToJsonObject() {
        JsonObject constructedNewJson = PermissionFactory.convertToJsonObject(perm);
        Assert.assertEquals(json.toString(), constructedNewJson.toString());
    }
}
