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
package io.radien.ms.permissionmanagement.client.util;

import io.radien.ms.permissionmanagement.client.entities.Action;
import io.radien.ms.permissionmanagement.client.services.ActionFactory;
import javax.json.JsonObject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonReader;
import java.io.InputStream;
import java.util.List;

/**
 * @author Newton Carvalho
 * This class maps the Action pojos into Json objects an vice-versa
 */
public class ActionModelMapper {

    /**
     * Maps one Action into a Json Object
     * @param model
     * @return
     */
    public static JsonObject map(Action model) {
        return ActionFactory.convertToJsonObject(model);
    }

    /**
     * Maps an Action Collection into a Json Array
     * @param models
     * @return
     */
    public static JsonArray map(List<Action> models) {
        final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        models.forEach(model -> {
            JsonObject jsonObject = map(model);
            arrayBuilder.add(jsonObject);
        });
        return arrayBuilder.build();
    }

    /**
     * Maps a Json stream into a Action
     * @param is
     * @return
     */
    public static Action map(InputStream is) {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonObject jsonObject = jsonReader.readObject();
            return ActionFactory.convert(jsonObject);
        }
    }
}
