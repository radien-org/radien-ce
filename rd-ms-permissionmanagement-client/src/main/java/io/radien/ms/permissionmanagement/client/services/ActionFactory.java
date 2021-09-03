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
package io.radien.ms.permissionmanagement.client.services;

import io.radien.api.entity.Page;
import io.radien.api.util.FactoryUtilService;
import io.radien.ms.permissionmanagement.client.entities.Action;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class to create Action class instances
 * @author Newton Carvalho
 */
public class ActionFactory {

    /**
     * Create a Action with already predefine fields.
     *
     * @param name action name
     * @param createUser the user which has created the permission
     * @return a Action object to be used
     */
    public static Action create(String name, Long createUser){
        Action u = new Action();
        u.setName(name);
        u.setCreateUser(createUser);
        Date now = new Date();
        u.setLastUpdate(now);
        u.setCreateDate(now);
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
        Action action = new Action();
        action.setId(id);
        action.setName(name);
        action.setCreateUser(createAction);
        action.setLastUpdateUser(updateAction);
        action.setCreateDate(new Date());
        action.setLastUpdate(new Date());
        return action;
    }

    /**
     * Converts a System action to a Json Object
     *
     * @param a system action to be converted to json
     * @return json object with keys and values constructed
     */
    public static JsonObject convertToJsonObject(Action a) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        FactoryUtilService.addValueLong(builder, "id", a.getId());
        FactoryUtilService.addValue(builder, "name", a.getName());
        FactoryUtilService.addValueLong(builder, "createUser", a.getCreateUser());
        FactoryUtilService.addValueLong(builder, "lastUpdateUser", a.getLastUpdateUser());
        return builder.build();
    }

    /**
     * Converts a Json Array into an Action List
     * @param jsonArray to be mapped and extracted the information
     * @return a list of actions extracted from the json array
     */
    public static List<Action> convert(JsonArray jsonArray) {
        return jsonArray.stream().map(i->convert(i.asJsonObject())).collect(Collectors.toList());
    }

    /**
     * Converts a JsonObject into a Action Page object
     * @param page the JsonObject to convert
     * @return the Page encapsulating information regarding Action entities
     */
    public static Page<Action> convertJsonToPage(JsonObject page) {
        int currentPage = FactoryUtilService.getIntFromJson("currentPage", page);
        JsonArray results = FactoryUtilService.getArrayFromJson("results", page);
        int totalPages = FactoryUtilService.getIntFromJson("totalPages", page);
        int totalResults = FactoryUtilService.getIntFromJson("totalResults", page);

        ArrayList<Action> pageResults = new ArrayList<>();
        
        if(results != null){
            for(int i = 0;i<results.size();i++){
                pageResults.add(convert(results.getJsonObject(i)));
            }
        }
        return new Page<>(pageResults, currentPage, totalResults, totalPages);
    }
}
