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
package io.radien.ms.permissionmanagement.client.util;

import io.radien.api.entity.Page;
import io.radien.ms.permissionmanagement.client.entities.Permission;
import io.radien.ms.permissionmanagement.client.services.PermissionFactory;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;


import java.io.InputStream;
import java.util.List;

/**
 * This class maps the Permission pojos into Json objects an vice-versa
 * @author Newton Carvalho
 */
public class PermissionModelMapper {

    /**
     * Maps one Permission into a Json Object
     * @param model permission object to be converted
     * @return json object with the given permission information
     */
    public static JsonObject map(Permission model) {
        return PermissionFactory.convertToJsonObject(model);
    }

    /**
     * Maps a Permission Collection into a Json Array
     * @param models list of permissions to be converted
     * @return a json array with all the given list information
     */
    public static JsonArray map(List<Permission> models) {
        final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        models.forEach(model -> {
            JsonObject jsonObject = map(model);
            arrayBuilder.add(jsonObject);
        });
        return arrayBuilder.build();
    }

    /**
     * Maps a Json stream into a Permission
     * @param is to be mapped
     * @return the converted and mapped permission
     */
    public static Permission map(InputStream is) {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonObject jsonObject = jsonReader.readObject();
            return PermissionFactory.convert(jsonObject);
        }
    }

    /**
     * Obtains a Permission Page from a Json input stream
     * @param is to be mapped
     * @return a page of permissions mapped from the input stream
     */
    public static Page<Permission> mapToPage(InputStream is) {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonObject jsonObject = jsonReader.readObject();

            return PermissionFactory.convertJsonToPage(jsonObject);
        }
    }
}
