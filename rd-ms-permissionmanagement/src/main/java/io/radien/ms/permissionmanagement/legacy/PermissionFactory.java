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

import io.radien.api.model.permission.SystemAction;
import io.radien.api.model.permission.SystemPermission;
import io.radien.api.util.FactoryUtilService;
import io.radien.ms.permissionmanagement.client.entities.ActionType;
import io.radien.ms.permissionmanagement.model.Action;
import io.radien.ms.permissionmanagement.model.Permission;

import javax.enterprise.context.RequestScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
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
    public static Permission create(String name, Action action, Long createdUser) {
        Permission p = new Permission();
        p.setName(name);
        p.setCreateUser(createdUser);
        Date now = new Date();
        p.setLastUpdate(now);
        p.setCreateDate(now);
        p.setAction(action);
        return p;
    }

    /**
     * Converts a JSONObject to a SystemPermission object Used by the Application
     * DataInit to seed Data in the database
     *
     * @param permission the JSONObject to convert
     * @return the SystemUserObject
     */
    public static Permission convert(JsonObject permission) {
        Long id = FactoryUtilService.getLongFromJson("id", permission);
        String name = FactoryUtilService.getStringFromJson("name", permission);
        Long createUser = FactoryUtilService.getLongFromJson("createUser", permission);
        Long lastUpdateUser = FactoryUtilService.getLongFromJson("lastUpdateUser", permission);

        Permission p = new Permission();
        p.setId(id);
        p.setName(name);
        p.setCreateDate(new Date());
        p.setLastUpdate(new Date());
        p.setCreateUser(createUser);
        p.setLastUpdateUser(lastUpdateUser);

        JsonValue jsonValue = permission.get("action");
        if (jsonValue != null && JsonValue.ValueType.OBJECT == jsonValue.getValueType()) {
            p.setAction(createActionFromJson(jsonValue.asJsonObject()));
        }
        return p;
    }

    private static Action createActionFromJson(JsonObject actionAsJsonObject) {
        Long id = FactoryUtilService.getLongFromJson("id", actionAsJsonObject);
        String name = FactoryUtilService.getStringFromJson("name", actionAsJsonObject);
        Long createAction = FactoryUtilService.getLongFromJson("createUser", actionAsJsonObject);
        Long updateAction = FactoryUtilService.getLongFromJson("lastUpdateUser", actionAsJsonObject);
        String actionTypeAsString = FactoryUtilService.getStringFromJson("type", actionAsJsonObject);

        Action action = new Action();
        action.setId(id);
        action.setName(name);
        action.setCreateUser(createAction);
        action.setLastUpdateUser(updateAction);

        if (actionTypeAsString != null) {
            ActionType type = ActionType.getByName(actionTypeAsString);
            if (type == null)
                throw new IllegalStateException("Unknown action type");
            action.setActionType(type);
        }
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
        if (permission.getAction() != null) {
            builder.add("action", convertActionToJson(permission.getAction()));
        } else {
            builder.addNull("action");
        }
        return  builder.build();
    }

    private static JsonObject convertActionToJson(SystemAction sa) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        FactoryUtilService.addValueLong(builder, "id", sa.getId());
        FactoryUtilService.addValue(builder, "name", sa.getName());
        FactoryUtilService.addValueLong(builder, "createUser", sa.getCreateUser());
        FactoryUtilService.addValueLong(builder, "lastUpdateUser", sa.getLastUpdateUser());
        FactoryUtilService.addValue(builder, "type", sa.getActionType());
        return builder.build();
    }
}
