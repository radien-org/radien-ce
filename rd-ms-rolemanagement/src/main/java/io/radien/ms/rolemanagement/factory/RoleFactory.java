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

import io.radien.api.model.role.SystemRole;
import io.radien.api.util.FactoryUtilService;
import io.radien.ms.rolemanagement.entities.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.Serializable;
import java.util.Date;

/**
 * Role Factory where we can construct a role object by a given set of elements
 *
 * @author Bruno Gama
 */
public class RoleFactory implements Serializable {

    private static final long serialVersionUID = -4078040120809206776L;

    private static final Logger log = LoggerFactory.getLogger(RoleFactory.class);

    /**
     * Create a role with already predefined fields.
     *
     * @param name role name.
     * @param description role type.
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

        log.info("Will begin to create a new Role object with the specific values" +
                " Name: {}, Description: {}, Created User: {}", name, description, createdUser);

        return role;
    }

    /**
     * Converts a JSONObject to a Role object that will be used by the Application.
     *
     * @param jsonRole receives a json object with all the information.
     * @return a Role object constructed by the given json.
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
        Date date = new Date();
        role.setCreateDate(date);
        role.setLastUpdate(date);

        log.info("Will begin to create a new Role object with the specific values received in the json" +
                " ID: {}, Name: {}, Description: {}, Created User: {}", id, name, description, createUser);

        return role;
    }

    /**
     * Converts a System Role to a Json Object.
     *
     * @param role system role to be converted to json.
     * @return json object with the keys and values constructed by the given object.
     */
    public static JsonObject convertToJsonObject(SystemRole role) {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        FactoryUtilService.addValue(builder, "id", role.getId());
        FactoryUtilService.addValue(builder, "name", role.getName());
        FactoryUtilService.addValue(builder, "description", role.getDescription());
        FactoryUtilService.addValueLong(builder, "createUser", role.getCreateUser());
        FactoryUtilService.addValueLong(builder, "lastUpdateUser", role.getLastUpdateUser());

        log.info("Will begin to create a new json object with the specific values received in the give role" +
                " ID: {}, Name: {}, Description: {}, Created User: {}", role.getId(), role.getName(), role.getDescription(), role.getCreateUser());

        return builder.build();
    }
}
