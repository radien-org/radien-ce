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
package io.radien.ms.usermanagement.util;

import io.radien.ms.usermanagement.entities.UserEntity;
import java.io.InputStream;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;


import io.radien.ms.usermanagement.legacy.UserFactory;

/**
 * This class maps the Permission pojos into Json objects an vice-versa
 * @author Marco Weiland 
 */
public class UserModelMapper {

    private UserModelMapper(){}

    /**
     * Maps one user information into a Json Object
     * @param model user object to be converted
     * @return json object with the given user information
     */
    public static JsonObject map(UserEntity model) {
        return UserFactory.convertToJsonObject(model);
    }

    /**
     * Maps a user Collection into a Json Array
     * @param models list of users to be converted
     * @return a json array with all the given list information
     */
    public static JsonArray map(List<UserEntity> models) {
        final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        models.forEach(model -> {
            JsonObject jsonObject = map(model);
            arrayBuilder.add(jsonObject);
        });
        return arrayBuilder.build();
    }

    /**
     * Maps a Json stream into a user
     * @param is to be mapped
     * @return the converted and mapped user
     */
    public static UserEntity map(InputStream is) {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonObject jsonObject = jsonReader.readObject();

            return UserFactory.convert(jsonObject);
        }
    }
}
