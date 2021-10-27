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

package io.radien.ms.ecm.client.util;

import io.radien.ms.ecm.client.entities.Translation;

import javax.json.JsonObject;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.Json;
import javax.json.JsonString;
import java.util.ArrayList;
import java.util.List;

/**
 * Translation object mapper and converter
 *
 * @author André Sousa
 */
public class TranslationMapper {

    /**
     * Get the translations from a given json object
     * @param key of the translation
     * @param json to be retrieved the information
     * @return a list of all the existent translations
     */
    public static List<Translation> getTranslationFromJson(String key, JsonObject json) {
        List<Translation> returnedString = new ArrayList<>();
        if (json.containsKey(key)) {
            JsonArray value = json.getJsonArray(key);
            if (value != null) {
                for(int i = 0; i < value.size(); i++) {
                    JsonObject obj = value.getJsonObject(i);
                    Translation t = new Translation();
                    t.setDescription(getStringFromJson("description", obj));
                    t.setLanguage(getStringFromJson("language", obj));
                    returnedString.add(t);
                }
            }
        }
        return returnedString;
    }

    /**
     * Converts a list of translations into an json array
     * @param translations list of translations to be converted
     * @return a json array with all the necessary given information
     */
    public static JsonArray getJsonArrayFromTranslations(List<Translation> translations) {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        translations.forEach(t -> {
            JsonObjectBuilder objBuilder = Json.createObjectBuilder();
            addValue(objBuilder, "description", t.getDescription());
            addValue(objBuilder, "language", t.getLanguage());
            builder.add(objBuilder.build());
        });
        return builder.build();
    }

    /**
     * Retrieves specific field from a given json object
     * @param key of the translation field
     * @param json to be retrieved the information
     * @return the string value of the field
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

    /**
     * Adds a specific field into a json object
     * @param builder json object for the field to be added
     * @param key of the value field
     * @param value of the field
     */
    private static void addValue(JsonObjectBuilder builder, String key, Object value) {
        if (value != null) {
            builder.add(key, value.toString());
        } else {
            builder.addNull(key);
        }
    }
}
