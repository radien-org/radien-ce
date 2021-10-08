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
package io.radien.ms.permissionmanagement.client.util;

import io.radien.api.entity.Page;
import io.radien.ms.permissionmanagement.client.entities.Resource;
import io.radien.ms.permissionmanagement.client.services.ResourceFactory;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;

import java.io.InputStream;
import java.util.List;

/**
 * This class maps the Resource pojos into Json objects an vice-versa
 * @author Newton Carvalho
 */
public class ResourceModelMapper {

    /**
     * Maps one Resource into a Json Object
     * @param model resource object to be converted
     * @return json object with the given resource information
     */
    public static JsonObject map(Resource model) {
        return ResourceFactory.convertToJsonObject(model);
    }

    /**
     * Maps a Resource Collection into a Json Array
     * @param models list of resource to be converted
     * @return a json array with all the given list information
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
     * @param is to be mapped
     * @return the converted and mapped resource
     */
    public static Resource map(InputStream is) {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonObject jsonObject = jsonReader.readObject();
            return ResourceFactory.convert(jsonObject);
        }
    }

    /**
     * Obtains a Resource Page from a Json input stream
     * @param is to be mapped
     * @return a page of resource mapped from the input stream
     */
    public static Page<Resource> mapToPage(InputStream is) {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonObject jsonObject = jsonReader.readObject();

            return ResourceFactory.convertJsonToPage(jsonObject);
        }
    }
}
