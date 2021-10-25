/*
 * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
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
package io.rd.ms.client.util;

import io.rd.api.entity.Page;
import io.rd.ms.client.entities.Demo;
import io.rd.ms.client.services.DemoFactory;

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
public class DemoModelMapper {

    private static final long serialVersionUID = 6812608123262000051L;
    public static JsonObject map(Demo model) {
        return DemoFactory.convertToJsonObject(model);
    }

    public static JsonArray map(List<Demo> models) {
        final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        models.forEach(model -> {
            JsonObject jsonObject = map(model);
            arrayBuilder.add(jsonObject);
        });
        return arrayBuilder.build();
    }

    public static Demo map(InputStream is) {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonObject jsonObject = jsonReader.readObject();

            return DemoFactory.convert(jsonObject);
        }
    }

    public static Page<Demo> mapToPage(InputStream is) {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonObject jsonObject = jsonReader.readObject();

            return DemoFactory.convertJsonToPage(jsonObject);
        }
    }
}
