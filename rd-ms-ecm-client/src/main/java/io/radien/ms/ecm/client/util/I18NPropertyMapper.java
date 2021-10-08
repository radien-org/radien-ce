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

package io.radien.ms.ecm.client.util;

import io.radien.ms.ecm.client.entities.I18NProperty;
import io.radien.ms.ecm.client.entities.LabelTypeEnum;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonReader;
import javax.json.JsonString;
import java.io.InputStream;
import java.util.List;

/**
 * I18N Property Object Mapper and converter
 *
 * @author André Sousa
 */
public class I18NPropertyMapper {

    /**
     * Converts a I18N property into a Json Object
     * @param model to be converted
     * @return a json object filled with the given I18N keys and values
     */
    public static JsonObject map(I18NProperty model) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        addValue(builder, "key", model.getKey());
        addValue(builder, "type", model.getType());
        addValue(builder, "translations", TranslationMapper.getJsonArrayFromTranslations(model.getTranslations()));
        return builder.build();
    }

    /**
     * Adds a given value into a json object
     * @param builder json object to be added the information
     * @param key of the field
     * @param value of the field to be added
     */
    private static void addValue(JsonObjectBuilder builder, String key, Object value) {
        if (value != null) {
            builder.add(key, value.toString());
        } else {
            builder.addNull(key);
        }
    }

    /**
     * Maps a list of I18N properties into an json array
     * @param models list of I18N to be converted
     * @return json array of the given information
     */
    public static JsonArray map(List<I18NProperty> models) {
        final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        models.forEach(model -> {
            JsonObject jsonObject = map(model);
            arrayBuilder.add(jsonObject);
        });
        return arrayBuilder.build();
    }

    /**
     * Converts a given Input Stream into a I18N property
     * @param is to be converted
     * @return a I18N object with the given information
     */
    public static I18NProperty map(InputStream is) {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonObject jsonObject = jsonReader.readObject();
            I18NProperty model = new I18NProperty();
            model.setKey(getStringFromJson("key", jsonObject));
            model.setType(LabelTypeEnum.valueOf(getStringFromJson("type", jsonObject)));
            model.setTranslations(TranslationMapper.getTranslationFromJson("translations", jsonObject));
            return model;
        }
    }

    /**
     * Retrieves specific value from a given json
     * @param key of the field to be retrieved
     * @param json with the complete information
     * @return the string value of the requested field
     */
    private static String getStringFromJson(String key, JsonObject json) {
        String returnedString = null;
        if (json.containsKey(key)) {
            JsonString value = json.getJsonString(key);
            if (value != null) {
                returnedString = value.getString();
            }
        }
        return returnedString;
    }
}
