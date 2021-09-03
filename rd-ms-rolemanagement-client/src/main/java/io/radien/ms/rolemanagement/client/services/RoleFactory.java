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
package io.radien.ms.rolemanagement.client.services;

import io.radien.api.entity.Page;
import io.radien.api.util.FactoryUtilService;
import io.radien.ms.rolemanagement.client.entities.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Client Role Factory conversions
 * Factory that will create based on given fields the role or convert the role into a json object
 *
 * @author Bruno Gama
 */
public class RoleFactory {

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

        log.info("Client will begin to create a new Role object with the specific values" +
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
        String terminationDateAsString = FactoryUtilService.getStringFromJson("terminationDate", jsonRole);

        Role role = new Role();
        role.setId(id);
        role.setName(name);
        role.setDescription(description);
        if (terminationDateAsString != null) {
            try {
                role.setTerminationDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(terminationDateAsString));
            } catch (ParseException e) {
                throw new RuntimeException("Error parsing terminationDate", e);
            }
        }
        role.setCreateUser(createUser);
        role.setCreateDate(new Date());
        role.setLastUpdate(new Date());

        log.info("Client will begin to create a new Role object with the specific values received in the json" +
                " ID: {}, Name: {}, Description: {}, Termination Date: {}, Created User: {}", id, name,
                description, role.getTerminationDate(), createUser);

        return role;
    }

    /**
     * Converts a System Role to a Json Object.
     *
     * @param role system role to be converted to json.
     * @return json object with the keys and values constructed by the given object.
     */
    public static JsonObject convertToJsonObject(Role role) {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        FactoryUtilService.addValueLong(builder, "id", role.getId());
        FactoryUtilService.addValue(builder, "name", role.getName());
        FactoryUtilService.addValue(builder, "description", role.getDescription());
        FactoryUtilService.addValueLong(builder, "createUser", role.getCreateUser());
        FactoryUtilService.addValueLong(builder, "lastUpdateUser", role.getLastUpdateUser());
        if (role.getTerminationDate() != null) {
            FactoryUtilService.addValue(builder, "terminationDate", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(role.getTerminationDate()));
        }
        log.info("Will begin to create a new json object with the specific values received in the give role" +
                " ID: {}, Name: {}, Description: {}, Termination Date: {}, Created User: {}", role.getId(), role.getName(),
                role.getDescription(), role.getTerminationDate(), role.getCreateUser());

        return builder.build();
    }

    /**
     * Converts a JsonObject into a Permission Page object
     * @param page the JsonObject to convert
     * @return the Page encapsulating information regarding permissions
     */
    public static Page<Role> convertJsonToPage(JsonObject page) {
        int currentPage = FactoryUtilService.getIntFromJson("currentPage", page);
        JsonArray results = FactoryUtilService.getArrayFromJson("results", page);
        int totalPages = FactoryUtilService.getIntFromJson("totalPages", page);
        int totalResults = FactoryUtilService.getIntFromJson("totalResults", page);

        ArrayList<Role> pageResults = new ArrayList<>();

        if(results != null){
            for(int i = 0;i<results.size();i++){
                pageResults.add(convert(results.getJsonObject(i)));
            }
        }
        return new Page<>(pageResults,currentPage,totalResults,totalPages);
    }

    /**
     * Method to convert a given json array with relevant information into role objects that will be stored into a list
     * @param jsonArray to be mapped
     * @return a list of all the role information
     */
    public static List<Role> convert(JsonArray jsonArray) {
        return jsonArray.stream().map(i->convert(i.asJsonObject())).collect(Collectors.toList());
    }
}
