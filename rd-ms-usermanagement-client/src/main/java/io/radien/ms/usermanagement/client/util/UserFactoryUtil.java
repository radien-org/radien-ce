/*
 * Copyright (c) 2006-present openappframe.org & its legal owners. All rights reserved.
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
package io.radien.ms.usermanagement.client.util;

import io.radien.api.entity.Page;
import io.radien.api.model.user.SystemUser;
import io.radien.api.util.FactoryUtilService;

import io.radien.ms.usermanagement.client.entities.User;
import io.radien.ms.usermanagement.client.services.UserFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import java.util.HashMap;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
/**
 * Class that holds/manages static methods maintained redundant code blocks of
 * Factory classes UserManagement Service
 *
 * @author Rajesh Gavvala
 */
public class UserFactoryUtil implements Serializable {
    private static final long serialVersionUID = -1770715848784023573L;

    /**
     * Creates User object with defined user attributes
     * @param user user object
     * @param firstname user firstname
     * @param lastname user lastname
     * @param logon user logon
     * @param sub user sub
     * @param email user email
     * @param createdUser user createUser
     * @return user object
     */
    public static SystemUser createUser(SystemUser user, String firstname, String lastname, String logon, String sub, String email, Long createdUser){
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setLogon(logon);
        user.setSub(sub);
        user.setCreateUser(createdUser);
        Date now = new Date();
        user.setLastUpdate(now);
        user.setCreateDate(now);
        user.setUserEmail(email);
        return user;
    }

    /**
     * Converts JsonObject to SystemUser object
     * @param jsonSystemUserObject convert JsonObject to SystemUser map object
     * @return User Object mapped values
     */
    public static Map<String, Object> convertJsonObject(JsonObject jsonSystemUserObject){
        Map<String, Object> systemUserMapValues = new HashMap<>();

        systemUserMapValues.put("id", FactoryUtilService.getLongFromJson("id", jsonSystemUserObject));
        systemUserMapValues.put("firstname", FactoryUtilService.getStringFromJson("firstname", jsonSystemUserObject));
        systemUserMapValues.put("lastname", FactoryUtilService.getStringFromJson("lastname", jsonSystemUserObject));
        systemUserMapValues.put("logon", FactoryUtilService.getStringFromJson("logon", jsonSystemUserObject));
        systemUserMapValues.put("userEmail", FactoryUtilService.getStringFromJson("userEmail", jsonSystemUserObject));
        systemUserMapValues.put("createUser", FactoryUtilService.getLongFromJson("createUser", jsonSystemUserObject));
        systemUserMapValues.put("sub", FactoryUtilService.getStringFromJson("sub", jsonSystemUserObject));
        return systemUserMapValues;
    }

    /**
     * Converts a SystemUser to JsonObject
     * @param systemUser SystemUser object
     * @return converted SystemUser JsonObject
     */
    public static JsonObject convertSystemUserToJsonObject(SystemUser systemUser){
        JsonObjectBuilder builder = Json.createObjectBuilder();

        //TODO: Complete the object conversion fields missing
        FactoryUtilService.addValueLong(builder, "id", systemUser.getId());
        FactoryUtilService.addValue(builder, "logon", systemUser.getLogon());
        FactoryUtilService.addValue(builder, "userEmail", systemUser.getUserEmail());
        FactoryUtilService.addValueLong(builder, "createUser", systemUser.getCreateUser());
        FactoryUtilService.addValueLong(builder, "lastUpdateUser", systemUser.getLastUpdateUser());
        FactoryUtilService.addValue(builder, "sub", systemUser.getSub());
        FactoryUtilService.addValue(builder, "firstname", systemUser.getFirstname());
        FactoryUtilService.addValue(builder, "lastname", systemUser.getLastname());
        FactoryUtilService.addValueBoolean(builder, "delegatedCreation", systemUser.isDelegatedCreation());
        FactoryUtilService.addValueBoolean(builder, "enabled", systemUser.isEnabled());
        return  builder.build();
    }

    /**
     * Converts a User JsonObject to User Page Object
     * @param jsonObject JsonObject of User
     * @return User Page Object
     */
    public static Page<User> convertToUserPageObject(JsonObject jsonObject){
        int currentPage = FactoryUtilService.getIntFromJson("currentPage", jsonObject);
        JsonArray results = FactoryUtilService.getArrayFromJson("results", jsonObject);
        int totalPages = FactoryUtilService.getIntFromJson("totalPages", jsonObject);
        int totalResults = FactoryUtilService.getIntFromJson("totalResults", jsonObject);
        ArrayList<User> pageResults = new ArrayList<>();
        if(results != null){
            for(int i = 0;i<results.size();i++){
                pageResults.add(UserFactory.convert(results.getJsonObject(i)));
            }
        }
        return new Page<>(pageResults,currentPage,totalResults,totalPages);
    }

}
