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
package io.radien.ms.usermanagement.client.services;

import io.radien.api.entity.Page;
import io.radien.ms.usermanagement.client.entities.User;
import io.radien.api.util.FactoryUtilService;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class UserFactory {

    /**
     * Create a user with already predefine fields.
     *
     * @param firstname user first name
     * @param lastname user last name
     * @param logon user logon
     * @param sub user subject
     * @param createdUser the user which has created the user
     * @param email user email
     * @return a User object to be used
     */
    public static User create(String firstname, String lastname, String logon, String sub, String email, Long createdUser){
        User u = new User();
        u.setFirstname(firstname);
        u.setLastname(lastname);
        u.setLogon(logon);
        u.setEnabled(true);
        u.setSub(sub);
        u.setCreateUser(createdUser);
        Date now = new Date();
        u.setLastUpdate(now);
        u.setCreateDate(now);
        u.setUserEmail(email);
        return u;
    }

    /**
     * Converts a JSONObject to a SystemUser object Used by the Application
     * DataInit to seed Data in the database
     *
     * @param person the JSONObject to convert
     * @return the SystemUserObject
     */
    //TODO: Complete the object conversion fields missing
    public static User convert(JsonObject person) {
        Long id = FactoryUtilService.getLongFromJson("id", person);
        String logon = FactoryUtilService.getStringFromJson("logon", person);
        String userEmail = FactoryUtilService.getStringFromJson("userEmail", person);
        Long createUser = FactoryUtilService.getLongFromJson("createUser", person);
        Long lastUpdateUser = FactoryUtilService.getLongFromJson("lastUpdateUser", person);
        String sub = FactoryUtilService.getStringFromJson("sub", person);
        String firstname = FactoryUtilService.getStringFromJson("firstname", person);
        String lastname = FactoryUtilService.getStringFromJson("lastname",person);
        Boolean enabled = FactoryUtilService.getBooleanFromJson("enabled",person);
        Boolean delegatedCreation = FactoryUtilService.getBooleanFromJson("delegatedCreation",person);


        User user = new User();
        user.setId(id);
        user.setLogon(logon);
        user.setUserEmail(userEmail);
        user.setCreateUser(createUser);
        // TODO: Set password protected
//		user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        user.setCreateDate(new Date());
        user.setLastUpdate(new Date());
        user.setSub(sub);
        user.setFirstname(firstname);
        user.setLastname(lastname);
        //If not present is set to boolean default
        if(enabled != null) {
            user.setEnabled(enabled);
        }
        if(delegatedCreation != null){
            user.setDelegatedCreation(delegatedCreation);
        }
        return user;
    }

    /**
     * Converts a System user to a Json Object
     *
     * @param person system user to be converted to json
     * @return json object with keys and values constructed
     */
    //TODO: Complete the object conversion fields missing
    public static JsonObject convertToJsonObject(User person) {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        FactoryUtilService.addValueLong(builder, "id", person.getId());
        FactoryUtilService.addValue(builder, "logon", person.getLogon());
        FactoryUtilService.addValue(builder, "userEmail", person.getUserEmail());
        FactoryUtilService.addValueLong(builder, "createUser", person.getCreateUser());
        FactoryUtilService.addValueLong(builder, "lastUpdateUser", person.getLastUpdateUser());
        FactoryUtilService.addValue(builder, "sub", person.getSub());
        FactoryUtilService.addValue(builder, "firstname", person.getFirstname());
        FactoryUtilService.addValue(builder, "lastname", person.getLastname());
        FactoryUtilService.addValueBoolean(builder, "delegatedCreation", person.isDelegatedCreation());
        FactoryUtilService.addValueBoolean(builder, "enabled", person.isEnabled());
        return  builder.build();
    }

    public static List<User> convert(JsonArray jsonArray) {
        return jsonArray.stream().map(i->convert(i.asJsonObject())).collect(Collectors.toList());
    }

    /**
     * Converts a JsonObject into a User Page object
     * @param page the JsonObject to convert
     * @return the Page encapsulating information regarding Action entities
     */
    public static Page<User> convertJsonToPage(JsonObject page) {
        int currentPage = FactoryUtilService.getIntFromJson("currentPage", page);
        JsonArray results = FactoryUtilService.getArrayFromJson("results", page);
        int totalPages = FactoryUtilService.getIntFromJson("totalPages", page);
        int totalResults = FactoryUtilService.getIntFromJson("totalResults", page);

        ArrayList<User> pageResults = new ArrayList();

        if(results != null){
            for(int i = 0;i<results.size();i++){
                pageResults.add(convert(results.getJsonObject(i)));
            }
        }
        return new Page<>(pageResults, currentPage, totalResults, totalPages);
    }
}
