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
package io.radien.ms.usermanagement.client.util;

import io.radien.api.entity.Page;
import io.radien.ms.usermanagement.client.entities.User;
import io.radien.ms.usermanagement.client.services.UserFactory;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.InputStream;
import java.util.List;

/**
 * This class maps the User pojos into Json objects an vice-versa
 * @author Marco Weiland
 */
public class UserModelMapper {

    /**
     * Maps one user object into a Json Object
     * @param model to be converted into Json
     * @return the converted json object
     */
    public static JsonObject map(User model) {
        return UserFactory.convertToJsonObject(model);
    }

    /**
     * Maps an user list into a Json Array
     * @param models list of users to be converted and sent
     * @return json array with all the given user information
     */
    public static JsonArray map(List<User> models) {
        final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        models.forEach(model -> {
            JsonObject jsonObject = map(model);
            arrayBuilder.add(jsonObject);
        });
        return arrayBuilder.build();
    }

    /**
     * Maps a Json stream into a user object
     * @param is input stream to be converted into user
     * @return user object retrieved from the input stream
     */
    public static User map(InputStream is) {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonObject jsonObject = jsonReader.readObject();

            return UserFactory.convert(jsonObject);
        }
    }

    /**
     * Obtains a User Page from a Json input stream
     * @param is to be mapped
     * @return a page of user mapped from the input stream
     */
    public static Page<User> mapToPage(InputStream is) {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonObject jsonObject = jsonReader.readObject();

            return UserFactory.convertJsonToPage(jsonObject);
        }
    }
}
