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
package io.rd.microservice.ms.client.util;

import io.rd.microservice.api.entity.Page;
import io.rd.microservice.ms.client.entities.Microservice;
import io.rd.microservice.ms.client.services.MicroserviceFactory;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.InputStream;
import java.util.List;

/**
 * @author Rajesh Gavvala
 * @author Marco Weiland
 */
public class MicroserviceModelMapper {

    private static final long serialVersionUID = 6812608123262000051L;
    public static JsonObject map(Microservice model) {
        return MicroserviceFactory.convertToJsonObject(model);
    }

    public static JsonArray map(List<Microservice> models) {
        final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        models.forEach(model -> {
            JsonObject jsonObject = map(model);
            arrayBuilder.add(jsonObject);
        });
        return arrayBuilder.build();
    }

    public static Microservice map(InputStream is) {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonObject jsonObject = jsonReader.readObject();

            return MicroserviceFactory.convert(jsonObject);
        }
    }

    public static Page<Microservice> mapToPage(InputStream is) {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonObject jsonObject = jsonReader.readObject();

            return MicroserviceFactory.convertJsonToPage(jsonObject);
        }
    }
}
