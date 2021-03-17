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

import io.radien.api.entity.Page;
import io.radien.ms.permissionmanagement.client.entities.Resource;
import io.radien.ms.permissionmanagement.client.services.ResourceFactory;

import javax.json.*;
import java.io.InputStream;
import java.util.List;

/**
 * @author Newton Carvalho
 * This class maps the Resource pojos into Json objects an vice-versa
 */
public class ResourceModelMapper {

    /**
     * Maps one Resource into a Json Object
     * @param model
     * @return
     */
    public static JsonObject map(Resource model) {
        return ResourceFactory.convertToJsonObject(model);
    }

    /**
     * Maps an Resource Collection into a Json Array
     * @param models
     * @return
     */
    public static JsonArray map(List<Resource> models) {
        final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        models.forEach(model -> {
            JsonObject jsonObject = map(model);
            arrayBuilder.add(jsonObject);
        });
        return arrayBuilder.build();
    }

    /**
     * Maps a Json stream into a Resource
     * @param is
     * @return
     */
    public static Resource map(InputStream is) {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonObject jsonObject = jsonReader.readObject();
            return ResourceFactory.convert(jsonObject);
        }
    }

    /**
     * Obtains a Resource Page from a Json input stream
     * @param is
     * @return
     */
    public static Page<Resource> mapToPage(InputStream is) {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonObject jsonObject = jsonReader.readObject();

            return ResourceFactory.convertJsonToPage(jsonObject);
        }
    }
}
