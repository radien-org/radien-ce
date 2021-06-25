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
package io.radien.ms.permissionmanagement.client.util;

import io.radien.ms.permissionmanagement.client.services.PermissionFactory;
import org.junit.Test;

import io.radien.ms.permissionmanagement.client.entities.Permission;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

/**
 * Permission Model Mapper Test
 * Test to verify the mappers and the converters between an object(permission, input stream, json) and a JSON
 * or Permission
 * {@link io.radien.ms.permissionmanagement.client.util.PermissionModelMapper}
 */
public class PermissionModelMapperTest {

    private final String nullStringValueForTesting = "NULL";

    /**
     * Test for the conversion from a input stream into a permission object
     */
    @Test
    public void testMapInputStream() {
        String example = "{\n" +
                "\"name\": \"a\",\n" +
                "\"id\": 28\n" +
                "}";
        InputStream in = new ByteArrayInputStream(example.getBytes());
        Permission permission = PermissionModelMapper.map(in);
        assertEquals("a",permission.getName());
        assertEquals(28L,(long)permission.getId());
    }

    /**
     * Method to validate the permission fields
     * @param perm to be validated
     * @param jsonObject to be used
     */
    private void validatePermissionJsonObject(Permission perm, JsonObject jsonObject){
        assertEquals(perm.getName(), jsonObject.getString("name"));

        if (perm.getCreateUser() == null) {
            assertEquals(nullStringValueForTesting, jsonObject.get("createUser").getValueType().toString());
        }
        else {
            assertEquals(perm.getCreateUser(),
                    Long.valueOf(jsonObject.getJsonNumber("createUser").longValue()));
        }

        if (perm.getActionId() == null) {
            assertEquals(nullStringValueForTesting, jsonObject.get("actionId").getValueType().toString());
        }
        else {
            assertEquals(perm.getActionId(),
                    Long.valueOf(jsonObject.getJsonNumber("actionId").longValue()));
        }

        if (perm.getResourceId() == null) {
            assertEquals(nullStringValueForTesting, jsonObject.get("resourceId").getValueType().toString());
        }
        else {
            assertEquals(perm.getActionId(),
                    Long.valueOf(jsonObject.getJsonNumber("resourceId").longValue()));
        }
    }

    /**
     * Test a conversion from a permission object into a json one
     */
    @Test
    public void testMapJsonObject() {
        String firstName = "aa";
        Permission permission = PermissionFactory.create(firstName, null, null,100L);
        JsonObject jsonObject = PermissionModelMapper.map(permission);
        validatePermissionJsonObject(permission, jsonObject);
    }

    /**
     * Test the conversion between a permission array into a json array
     */
    @Test
    public void testMapList() {
        String firstName = "aa";
        Permission permission = PermissionFactory.create(firstName, null, null,null);
        JsonArray jsonArray = PermissionModelMapper.map(Collections.singletonList(permission));
        assertEquals(1,jsonArray.size());
        JsonObject jsonObject = jsonArray.getJsonObject(0);
        validatePermissionJsonObject(permission,jsonObject);
    }
}