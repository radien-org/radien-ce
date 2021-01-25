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
package io.radien.ms.rolemanagement.client.services;

import io.radien.api.util.FactoryUtilService;
import io.radien.ms.rolemanagement.client.entities.Role;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Bruno Gama
 */
public class RoleFactory {

    /**
     * Create a role with already predefined fields.
     *
     * @param name role name
     * @param description role type
     * @return a Role object to be used.
     */
    public static Role create(String name, String description, Long createdUser) {
        Role role = new Role();

        role.setName(name);
        role.setDescription(description);
        role.setCreateUser(createdUser);
        Date now = new Date();
        role.setLastUpdate(now);
        role.setCreateDate(now);

        return role;
    }

    /**
     * Converts a JSONObject to a Role object that will be used by the Application
     *
     * @param jsonRole json message to be converted
     * @return the System Role Object
     */
    public static Role convert(JsonObject jsonRole) {
        Long id = FactoryUtilService.getLongFromJson("id", jsonRole);
        String name = FactoryUtilService.getStringFromJson("name", jsonRole);
        String description = FactoryUtilService.getStringFromJson("description", jsonRole);
        Long createUser = FactoryUtilService.getLongFromJson("createUser", jsonRole);

        Role role = new Role();
        role.setId(id);
        role.setName(name);
        role.setDescription(description);
        role.setCreateUser(createUser);
        role.setCreateDate(new Date());
        role.setLastUpdate(new Date());

        return role;
    }

    /**
     * Converts a System role to a Json Object
     *
     * @param role system user to be converted to json
     * @return json object with keys and values constructed
     */
    public static JsonObject convertToJsonObject(Role role) {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        FactoryUtilService.addValue(builder, "id", role.getId());
        FactoryUtilService.addValue(builder, "name", role.getName());
        FactoryUtilService.addValue(builder, "description", role.getDescription());
        FactoryUtilService.addValueLong(builder, "createUser", role.getCreateUser());
        FactoryUtilService.addValueLong(builder, "lastUpdateUser", role.getLastUpdateUser());

        return builder.build();
    }

    public static List<Role> convert(JsonArray jsonArray) {
        return jsonArray.stream().map(i->convert(i.asJsonObject())).collect(Collectors.toList());
    }
}
