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

import io.radien.ms.rolemanagement.client.entities.LinkedAuthorization;
import io.radien.ms.rolemanagement.client.services.LinkedAuthorizationFactory;

import javax.json.*;
import java.io.InputStream;
import java.util.List;

/**
 * @author Bruno Gama
 */
public class LinkedAuthorizationModelMapper {

    /**
     * Maps into a Json Object a Linked Authorization
     * @param model linked authorization that has the information to be converted
     * @return a json object created based the linked authorization
     */
    public static JsonObject map(LinkedAuthorization model) {
        return LinkedAuthorizationFactory.convertToJsonObject(model);
    }

    /**
     * Maps into a Json Object array based on a Linked Authorization array list
     * @param models linked authorizations that have the information to be converted
     * @returna json array created based on the multiple linked authorization
     */
    public static JsonArray map(List<LinkedAuthorization> models) {
        final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        models.forEach(model -> {
            JsonObject jsonObject = map(model);
            arrayBuilder.add(jsonObject);
        });
        return arrayBuilder.build();
    }

    /**
     * Creates a linked authorization based a received inputted information
     * @param is inputted information to be converted into the object
     * @return a linked authorization object based in the received information
     */
    public static LinkedAuthorization map(InputStream is) {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonObject jsonObject = jsonReader.readObject();
            return LinkedAuthorizationFactory.convert(jsonObject);
        }
    }
}