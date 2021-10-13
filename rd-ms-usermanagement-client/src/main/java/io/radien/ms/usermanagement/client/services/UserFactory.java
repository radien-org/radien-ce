/*
 * Copyright (c) 2021-present radien GmbH & its legal owners. All rights reserved.
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

import io.radien.api.SystemVariables;
import io.radien.api.entity.Page;
import io.radien.ms.usermanagement.client.entities.User;
import io.radien.api.util.FactoryUtilService;

import io.radien.ms.usermanagement.client.util.UserFactoryUtil;
import java.util.Map;

import javax.json.JsonArray;
import javax.json.JsonObject;

import java.util.List;
import java.util.stream.Collectors;
/**
 * User Management object factory. Converts and maps information from Json or to Json into a User or from a User object
 *
 * @author Bruno Gama
 */
public class UserFactory extends UserFactoryUtil {

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
        User user = new User();
        user.setEnabled(true);
        return (User) createUser(user, firstname, lastname, logon, sub, email, createdUser);
    }

    /**
     * Converts a JSONObject to a SystemUser object Used by the Application
     * DataInit to seed Data in the database
     *
     * @param person the JSONObject to convert
     * @return the SystemUserObject
     */
    public static User convert(JsonObject person) {
        Map<String, Object> userMappedValues = convertJsonObject(person);
        Boolean delegatedCreation = FactoryUtilService.getBooleanFromJson(SystemVariables.USER_DELEGATION.getFieldName(),person);
        Boolean enabled = FactoryUtilService.getBooleanFromJson(SystemVariables.USER_ENABLED.getFieldName(),person);

        User user = new User();
        // TODO: Set password protected
        // TODO: user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()))
        user.setId((Long) userMappedValues.get(SystemVariables.ID.getFieldName()));
        //If not present is set to boolean default
        if(enabled != null) {
            user.setEnabled(enabled);
        }
        if(delegatedCreation != null){
            user.setDelegatedCreation(delegatedCreation);
        }
        return (User) createUser(user,
                (String) userMappedValues.get( SystemVariables.FIRST_NAME.getFieldName()),
                (String) userMappedValues.get(SystemVariables.LAST_NAME.getFieldName()),
                (String) userMappedValues.get(SystemVariables.LOGON.getFieldName()),
                (String) userMappedValues.get(SystemVariables.SUB.getFieldName()),
                (String) userMappedValues.get(SystemVariables.USER_EMAIL.getFieldName()),
                (Long)   userMappedValues.get(SystemVariables.CREATE_USER.getFieldName()));
    }

    /**
     * Converts a System user to a Json Object
     *
     * @param user system user to be converted to json
     * @return json object with keys and values constructed
     */
    public static JsonObject convertToJsonObject(User user) {
        return convertSystemUserToJsonObject(user);
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
        return convertToUserPageObject(page);
    }
}
