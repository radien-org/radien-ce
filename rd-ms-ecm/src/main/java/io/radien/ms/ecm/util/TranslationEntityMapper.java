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
package io.radien.ms.ecm.util;

import io.radien.ms.ecm.client.entities.Translation;
import io.radien.ms.ecm.entities.TranslationEntity;

import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Translation Entity Mapper and converter
 *
 * @author andresousa
 */
public class TranslationEntityMapper {

    /**
     * Converts a list of given translations entities into a Json Array
     * @param translations list of translations to be converted
     * @return a json array with all the given information from the translation
     */
    public static JsonArray getJsonArrayFromTranslations(List<TranslationEntity> translations) {
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
     * To a given json object string builder this method allows to add a new and nonexistent field
     * @param builder json object to be added the information
     * @param key of the field
     * @param value of the field
     */
    private static void addValue(JsonObjectBuilder builder, String key, Object value) {
        if (value != null) {
            builder.add(key, value.toString());
        } else {
            builder.addNull(key);
        }
    }

    /**
     * Converter from a translation into a translation entity
     * @param translation to be converted
     * @return the converted translation entity
     */
    public static TranslationEntity mapToEntity(Translation translation) {
        TranslationEntity entity = new TranslationEntity();
        entity.setDescription(translation.getDescription());
        entity.setLanguage(translation.getLanguage());

        return entity;
    }

    /**
     * Converts a list of translations into a translation entity
     * @param translations list of translations to be converted
     * @return a list of translation of entities
     */
    public static List<TranslationEntity> mapToEntity(List<Translation> translations) {
        return translations.stream().map(TranslationEntityMapper::mapToEntity).collect(Collectors.toList());
    }

    /**
     * Converts a translation entity into a translation
     * @param translation to be converted
     * @return a converted translation
     */
    public static Translation mapToDTO(TranslationEntity translation) {
        Translation dto = new Translation();
        dto.setDescription(translation.getDescription());
        dto.setLanguage(translation.getLanguage());

        return dto;
    }

    /**
     * Converts a list of translation entity into a list of translation
     * @param translations to be converted
     * @return a list of translations
     */
    public static List<Translation> mapToDTO(List<TranslationEntity> translations) {
        return translations.stream().map(TranslationEntityMapper::mapToDTO).collect(Collectors.toList());
    }
}
