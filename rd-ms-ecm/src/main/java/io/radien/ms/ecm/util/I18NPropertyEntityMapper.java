/*
 * Copyright (c) 2006-present openappframe.org & its legal owners. All rights reserved.
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

import javax.json.*;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author andresousa
 */
public class I18NPropertyEntityMapper {

    public static JsonObject map(I18NPropertyEntity model) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        addValue(builder, "key", model.getKey());
        addValue(builder, "type", model.getType());
        addValue(builder, "translations", TranslationEntityMapper.getJsonArrayFromTranslations(model.getTranslations()));
        return builder.build();
    }

    private static void addValue(JsonObjectBuilder builder, String key, Object value) {
        if (value != null) {
            builder.add(key, value.toString());
        } else {
            builder.addNull(key);
        }
    }

    public static JsonArray map(List<I18NPropertyEntity> models) {
        final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        models.forEach(model -> {
            JsonObject jsonObject = map(model);
            arrayBuilder.add(jsonObject);
        });
        return arrayBuilder.build();
    }

    public static I18NPropertyEntity mapToEntity(I18NProperty property) {
        I18NPropertyEntity entity = new I18NPropertyEntity();
        entity.setKey(property.getKey());
        entity.setType(property.getType());
        entity.setTranslations(TranslationEntityMapper.mapToEntity(property.getTranslations()));

        return entity;
    }

    public static List<I18NPropertyEntity> mapToEntity(List<I18NProperty> propertyList) {
        return propertyList.stream().map(I18NPropertyEntityMapper::mapToEntity).collect(Collectors.toList());
    }

    public static I18NProperty mapToDTO(I18NPropertyEntity entity) {
        I18NProperty dto = new I18NProperty();
        dto.setKey(entity.getKey());
        dto.setType(entity.getType());
        dto.setTranslations(TranslationEntityMapper.mapToDTO(entity.getTranslations()));

        return dto;
    }

    public static List<I18NProperty> mapToDTO(List<I18NPropertyEntity> propertyList) {
        return propertyList.stream().map(I18NPropertyEntityMapper::mapToDTO).collect(Collectors.toList());
    }
}
