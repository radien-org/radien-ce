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
package io.radien.ms.ecm.util;

import io.radien.ms.ecm.client.entities.I18NProperty;
import io.radien.ms.ecm.entities.I18NPropertyEntity;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import java.util.List;
import java.util.stream.Collectors;

/**
 * I18N Property Entity Mapper and converter
 *
 * @author andresousa
 */
public class I18NPropertyEntityMapper {

    /**
     * Converts a I18N property entity that has been given into a Json Object
     * @param model to be converted
     * @return a json object with all the I18N information
     */
    public static JsonObject map(I18NPropertyEntity model) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        addValue(builder, "key", model.getKey());
        addValue(builder, "type", model.getType());
        addValue(builder, "translations", TranslationEntityMapper.getJsonArrayFromTranslations(model.getTranslations()));
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
     * Converts a given list of I18N property entities into a Json Array
     * @param models of all the I18N information to be converted
     * @return a json array with all the I18N information
     */
    public static JsonArray map(List<I18NPropertyEntity> models) {
        final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        models.forEach(model -> {
            JsonObject jsonObject = map(model);
            arrayBuilder.add(jsonObject);
        });
        return arrayBuilder.build();
    }

    /**
     * Converter from a I18N Property into a I18N property entity
     * @param property to be converted
     * @return the converted I18N Property Entity
     */
    public static I18NPropertyEntity mapToEntity(I18NProperty property) {
        I18NPropertyEntity entity = new I18NPropertyEntity();
        entity.setKey(property.getKey());
        entity.setType(property.getType());
        entity.setTranslations(TranslationEntityMapper.mapToEntity(property.getTranslations()));

        return entity;
    }

    /**
     * Method to convert a list of I18N properties into a list of I18N properties Entities
     * @param propertyList to be converted
     * @return a list of converted I18N property entities
     */
    public static List<I18NPropertyEntity> mapToEntity(List<I18NProperty> propertyList) {
        return propertyList.stream().map(I18NPropertyEntityMapper::mapToEntity).collect(Collectors.toList());
    }

    /**
     * Method to convert and map into a data transfer object the given I18N property entity into a I18N property
     * @param entity to be converted
     * @return the I18N property that has been converted
     */
    public static I18NProperty mapToDTO(I18NPropertyEntity entity) {
        I18NProperty dto = new I18NProperty();
        dto.setKey(entity.getKey());
        dto.setType(entity.getType());
        dto.setTranslations(TranslationEntityMapper.mapToDTO(entity.getTranslations()));

        return dto;
    }

    /**
     * Method to convert and map into a data transfer object the given I18N property entity list into
     * a I18N property list
     * @param propertyList list of I18N property entities to be converted
     * @return the I18N property list that has been converted
     */
    public static List<I18NProperty> mapToDTO(List<I18NPropertyEntity> propertyList) {
        return propertyList.stream().map(I18NPropertyEntityMapper::mapToDTO).collect(Collectors.toList());
    }
}
