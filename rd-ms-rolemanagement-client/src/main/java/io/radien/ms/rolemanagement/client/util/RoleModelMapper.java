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
package io.radien.ms.rolemanagement.client.util;

import io.radien.api.entity.Page;
import io.radien.ms.rolemanagement.client.entities.Role;
import io.radien.ms.rolemanagement.client.services.RoleFactory;

import javax.json.JsonObject;
import javax.json.JsonArray;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonReader;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;

/**
 * Mapper from a given information into a JSON or a Role
 *
 * @author Bruno Gama
 */
public class RoleModelMapper {

    private RoleModelMapper() {
        // empty constructor
    }

    /**
     * Maps into a Json Object a Role
     * @param model role that has the information to be converted
     * @return a json object created based the role
     */
    public static JsonObject map(Role model) {
        return RoleFactory.convertToJsonObject(model);
    }

    /**
     * Maps into a Json Object array based on a role array list
     * @param models role that have the information to be converted
     * @return json array created based on the multiple roles
     */
    public static JsonArray map(List<Role> models) {
        final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        models.forEach(model -> {
            JsonObject jsonObject = map(model);
            arrayBuilder.add(jsonObject);
        });
        return arrayBuilder.build();
    }

    /**
     * Creates a role based a received inputted information
     * @param is inputted information to be converted into the object
     * @return a role object based in the received information
     */
    public static Role map(InputStream is) {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonObject jsonObject = jsonReader.readObject();
            return RoleFactory.convert(jsonObject);
        }
    }

    /**
     * Obtains a Permission Page from a Json input stream
     * @param is inputted information to be converted into the object
     * @return a page of roles with the requested information
     */
    public static Page<Role> mapToPage(InputStream is) {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonObject jsonObject = jsonReader.readObject();

            return RoleFactory.convertJsonToPage(jsonObject);
        }
    }

    /**
     * Converts Input Stream (JSON array) into List
     * @param is inputted information to be converted into the object List
     * @return a list of roles with the requested information
     */
    public static List<Role> mapList(InputStream is) throws ParseException {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonArray jsonArray = jsonReader.readArray();
            return RoleFactory.convert(jsonArray);
        }
    }
}
