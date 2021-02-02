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

import io.radien.api.util.FactoryUtilService;
import io.radien.ms.permissionmanagement.client.entities.ActionType;
import io.radien.ms.permissionmanagement.client.entities.Action;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ActionFactory {

    /**
     * Create a Action with already predefine fields.
     *
     * @param name action name
     * @param createUser the user which has created the permission
     * @return a Action object to be used
     */
    public static Action create(String name, ActionType actionType, Long createUser){
        Action u = new Action();
        u.setName(name);
        u.setCreateUser(createUser);
        Date now = new Date();
        u.setLastUpdate(now);
        u.setCreateDate(now);
        if (actionType != null)
            u.setActionType(actionType);
        return u;
    }

    /**
     * Converts a JSONObject to a SystemAction object Used by the Application
     * DataInit to seed Data in the database
     *
     * @param actionAsJsonObject the JSONObject to convert
     * @return the SystemAction object
     */
    public static Action convert(JsonObject actionAsJsonObject) {
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
        action.setCreateDate(new Date());
        action.setLastUpdate(new Date());

        if (actionTypeAsString != null) {
            ActionType type = ActionType.getByName(actionTypeAsString);
            if (type == null)
                throw new IllegalStateException("Unknown action type");
            action.setActionType(type);
        }
        return action;
    }

    /**
     * Converts a System action to a Json Object
     *
     * @param perm system action to be converted to json
     * @return json object with keys and values constructed
     */
    public static JsonObject convertToJsonObject(Action perm) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        FactoryUtilService.addValueLong(builder, "id", perm.getId());
        FactoryUtilService.addValue(builder, "name", perm.getName());
        FactoryUtilService.addValueLong(builder, "createUser", perm.getCreateUser());
        FactoryUtilService.addValueLong(builder, "lastUpdateUser", perm.getLastUpdateUser());
        FactoryUtilService.addValue(builder, "type", perm.getActionType());
        return builder.build();
    }

    public static List<Action> convert(JsonArray jsonArray) {
        return jsonArray.stream().map(i->convert(i.asJsonObject())).collect(Collectors.toList());
    }
}
