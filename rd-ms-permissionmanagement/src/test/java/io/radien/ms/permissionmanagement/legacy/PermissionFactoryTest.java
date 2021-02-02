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
package io.radien.ms.permissionmanagement.legacy;

import io.radien.ms.permissionmanagement.client.entities.ActionType;
import io.radien.ms.permissionmanagement.model.Permission;
import io.radien.ms.permissionmanagement.model.Action;
import junit.framework.TestCase;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class PermissionFactoryTest extends TestCase {

    JsonObject json;
    Permission perm;
    Action action;

    /**
     * Constructor class method were we are going to create the JSON and the permission for
     * testing purposes.
     */
    public PermissionFactoryTest() {
        action = new Action();
        action.setType(ActionType.WRITE);
        action.setName("WRITE-PERMISSION-ON-SOME_RADIEN_MODULE");
        action.setId(2L);
        action.setCreateUser(4L);

        JsonObject jObj = Json.createObjectBuilder().
                add("id", action.getId()).
                add("name", action.getName()).
                add("createUser", action.getCreateUser()).
                addNull("lastUpdateUser").
                add("type", action.getType().getName()).build();

        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.addNull("id");
        builder.add("name", "permissionTest");
        builder.add("createUser", 2L);
        builder.addNull("lastUpdateUser");
        builder.add("action", jObj);
        json = builder.build();
        perm = PermissionFactory.create("permissionTest",
                action,2L);
    }

    /**
     * Test method to validate the creation of a Permission using a Json
     */
    @Test
    public void testCreate() {
        assertEquals("permissionTest", perm.getName());
        assertEquals((Long) 2L, perm.getCreateUser());
        //assertEquals(ActionType.EXECUTION, perm.getActionType());
    }

    /**
     * Test method to validate the conversion of a Permission using a Json
     */
    @Test
    public void testConvert() {
        Permission constructedNewPermission = PermissionFactory.convert(json);
        assertEquals(perm.getId(), constructedNewPermission.getId());
        assertEquals(perm.getName(), constructedNewPermission.getName());
        assertEquals(perm.getCreateUser(), constructedNewPermission.getCreateUser());
        assertEquals(perm.getLastUpdateUser(), constructedNewPermission.getLastUpdateUser());
//        assertEquals(perm.getAction(), constructedNewPermission.getAction());
    }

    /**
     * Test method to validate the conversion of a Json Object using a User
     */
    @Test
    public void testConvertToJsonObject() {
        JsonObject constructedNewJson = PermissionFactory.convertToJsonObject(perm);
        assertEquals(json.toString(), constructedNewJson.toString());
    }
}
