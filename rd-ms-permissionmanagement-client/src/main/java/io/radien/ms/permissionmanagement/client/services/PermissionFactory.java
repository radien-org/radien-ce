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

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import io.radien.ms.permissionmanagement.client.entities.Permission;
import io.radien.ms.permissionmanagement.client.util.FactoryUtilService;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class PermissionFactory {

    /**
     * Create a Permission with already predefine fields.
     *
     * @param name permission name
     * @param createUser the user which has created the permission
     * @return a Permission object to be used
     */
    public static Permission create(String name, Long createUser){
        Permission u = new Permission();
        u.setName(name);
        u.setCreateUser(createUser);
        Date now = new Date();
        u.setLastUpdate(now);
        u.setCreateDate(now);
        return u;
    }

    /**
     * Converts a JSONObject to a SystemPermission object Used by the Application
     * DataInit to seed Data in the database
     *
     * @param permission the JSONObject to convert
     * @return the SystemPermission object
     */
    public static Permission convert(JsonObject permission) {
        Long id = FactoryUtilService.getLongFromJson("id", permission);
        String name = FactoryUtilService.getStringFromJson("name", permission);
        Long createPermission = FactoryUtilService.getLongFromJson("createUser", permission);
        
        Permission perm = new Permission();
        perm.setId(id);
        perm.setName(name);
        perm.setCreateUser(createPermission);
        perm.setCreateDate(new Date());
        perm.setLastUpdate(new Date());
        return perm;
    }

    /**
     * Converts a System permission to a Json Object
     *
     * @param perm system permission to be converted to json
     * @return json object with keys and values constructed
     */
    public static JsonObject convertToJsonObject(Permission perm) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        FactoryUtilService.addValue(builder, "id", perm.getId());
        FactoryUtilService.addValue(builder, "name", perm.getName());
        FactoryUtilService.addValueLong(builder, "createUser", perm.getCreateUser());
        FactoryUtilService.addValueLong(builder, "lastUpdateUser", perm.getLastUpdateUser());
        return  builder.build();
    }

    public static List<Permission> convert(JsonArray jsonArray) {
        return jsonArray.stream().map(i->convert(i.asJsonObject())).collect(Collectors.toList());
    }
}