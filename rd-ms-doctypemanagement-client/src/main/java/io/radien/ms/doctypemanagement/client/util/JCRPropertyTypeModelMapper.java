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
package io.radien.ms.doctypemanagement.client.util;

import io.radien.api.entity.Page;

import io.radien.api.model.docmanagement.propertytype.SystemJCRPropertyType;

import io.radien.ms.doctypemanagement.client.entities.JCRPropertyType;
import io.radien.ms.doctypemanagement.client.services.JCRPropertyTypeFactory;

import java.io.InputStream;
import java.text.ParseException;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;
public class JCRPropertyTypeModelMapper {

    private JCRPropertyTypeModelMapper() {
        // empty constructor
    }

    public static JsonObject map(JCRPropertyType model) {
        return JCRPropertyTypeFactory.convertToJsonObject(model);
    }

    public static JsonArray map(List<JCRPropertyType> models) {
        final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        models.forEach(model -> {
            JsonObject jsonObject = map(model);
            arrayBuilder.add(jsonObject);
        });
        return arrayBuilder.build();
    }

    public static JCRPropertyType map(InputStream is) {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonObject jsonObject = jsonReader.readObject();
            return JCRPropertyTypeFactory.convert(jsonObject);
        }
    }

    public static Page<JCRPropertyType> mapToPage(InputStream is) {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonObject jsonObject = jsonReader.readObject();

            return JCRPropertyTypeFactory.convertJsonToPage(jsonObject);
        }
    }

    public static List<? extends SystemJCRPropertyType> mapList(InputStream is) throws ParseException {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonArray jsonArray = jsonReader.readArray();
            return JCRPropertyTypeFactory.convert(jsonArray);
        }
    }
}
