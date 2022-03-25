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

import io.radien.ms.doctypemanagement.client.entities.PropertyDefinition;
import io.radien.ms.doctypemanagement.client.services.PropertyDefinitionFactory;

import java.io.InputStream;
import java.text.ParseException;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyDefinitionModelMapper {
    protected static final Logger log = LoggerFactory.getLogger(PropertyDefinitionModelMapper.class);


    private PropertyDefinitionModelMapper() {
        // empty constructor
    }

    public static JsonObject map(PropertyDefinition model) {
        return PropertyDefinitionFactory.convertToJsonObject(model);
    }

    public static JsonArray map(List<PropertyDefinition> models) {
        final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        models.forEach(model -> {
            JsonObject jsonObject = map(model);
            arrayBuilder.add(jsonObject);
        });
        return arrayBuilder.build();
    }

    public static PropertyDefinition map(InputStream is) throws ParseException {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonObject jsonObject = jsonReader.readObject();
            return PropertyDefinitionFactory.convert(jsonObject);
        }
    }

    public static Page<PropertyDefinition> mapToPage(InputStream is) {
        Page<PropertyDefinition> page = null;
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonObject jsonObject = jsonReader.readObject();

            try {
                page = PropertyDefinitionFactory.convertJsonToPage(jsonObject);
            } catch (ParseException e) {
                log.error(e.getMessage(),e);
            }
        }
        return page;
    }
}
