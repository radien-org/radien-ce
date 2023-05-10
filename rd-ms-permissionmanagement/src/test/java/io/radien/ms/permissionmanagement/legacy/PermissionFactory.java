/*
 * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
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

import io.radien.api.model.permission.SystemAction;
import io.radien.api.model.permission.SystemPermission;
import io.radien.api.util.FactoryUtilService;
import io.radien.ms.permissionmanagement.entities.ActionEntity;
import io.radien.ms.permissionmanagement.entities.PermissionEntity;

import javax.enterprise.context.RequestScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.Date;

/**
 * Factory class responsible for producing Permission related objects
 *
 * @author Bruno Gama
 * @author Nuno Santana
 */
@RequestScoped
public class PermissionFactory {
    /**
     * Create a permission with already predefine fields.
     *
     * @param name description for the permission
     * @return a User object to be used
     */
    public static PermissionEntity create(String name, Long action, Long createdUser) {
        PermissionEntity p = new PermissionEntity();
        p.setName(name);
        p.setCreateUser(createdUser);
        Date now = new Date();
        p.setLastUpdate(now);
        p.setCreateDate(now);
        p.setActionId(action);
        return p;
    }

    /**
     * Converts a JSONObject to a SystemPermission object Used by the Application
     * DataInit to seed Data in the database
     *
     * @param permission the JSONObject to convert
     * @return the SystemUserObject
     */
    public static PermissionEntity convert(JsonObject permission) {
        Long id = FactoryUtilService.getLongFromJson("id", permission);
        String name = FactoryUtilService.getStringFromJson("name", permission);
        Long createUser = FactoryUtilService.getLongFromJson("createUser", permission);
        Long lastUpdateUser = FactoryUtilService.getLongFromJson("lastUpdateUser", permission);
        Long actionId = FactoryUtilService.getLongFromJson("actionId", permission);

        PermissionEntity p = new PermissionEntity();
        p.setId(id);
        p.setName(name);
        p.setCreateDate(new Date());
        p.setLastUpdate(new Date());
        p.setCreateUser(createUser);
        p.setLastUpdateUser(lastUpdateUser);
        p.setActionId(actionId);
        return p;
    }

    private static ActionEntity createActionFromJson(JsonObject actionAsJsonObject) {
        Long id = FactoryUtilService.getLongFromJson("id", actionAsJsonObject);
        String name = FactoryUtilService.getStringFromJson("name", actionAsJsonObject);
        Long createAction = FactoryUtilService.getLongFromJson("createUser", actionAsJsonObject);
        Long updateAction = FactoryUtilService.getLongFromJson("lastUpdateUser", actionAsJsonObject);

        ActionEntity action = new ActionEntity();
        action.setId(id);
        action.setName(name);
        action.setCreateUser(createAction);
        action.setLastUpdateUser(updateAction);
        return action;
    }

    /**
     * Converts a System Permission to a Json Object
     *
     * @param permission system permission to be converted to json
     * @return json object with keys and values constructed
     */
    public static JsonObject convertToJsonObject(SystemPermission permission) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        FactoryUtilService.addValue(builder, "id", permission.getId());
        FactoryUtilService.addValue(builder, "name", permission.getName());
        FactoryUtilService.addValueLong(builder, "createUser", permission.getCreateUser());
        FactoryUtilService.addValueLong(builder, "lastUpdateUser", permission.getLastUpdateUser());
        FactoryUtilService.addValueLong(builder, "actionId", permission.getActionId());
        return  builder.build();
    }

    private static JsonObject convertActionToJson(SystemAction sa) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        FactoryUtilService.addValueLong(builder, "id", sa.getId());
        FactoryUtilService.addValue(builder, "name", sa.getName());
        FactoryUtilService.addValueLong(builder, "createUser", sa.getCreateUser());
        FactoryUtilService.addValueLong(builder, "lastUpdateUser", sa.getLastUpdateUser());
        return builder.build();
    }
}
