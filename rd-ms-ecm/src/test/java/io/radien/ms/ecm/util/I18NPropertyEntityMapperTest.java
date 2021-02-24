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

import io.radien.ms.ecm.client.entities.I18NProperty;
import io.radien.ms.ecm.client.entities.LabelTypeEnum;
import io.radien.ms.ecm.client.entities.Translation;
import io.radien.ms.ecm.entities.I18NPropertyEntity;
import io.radien.ms.ecm.entities.TranslationEntity;
import junit.framework.TestCase;
import org.junit.Test;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class I18NPropertyEntityMapperTest extends TestCase {

    @Test
    public void testMap() {
        I18NPropertyEntity entity = new I18NPropertyEntity();
        entity.setKey("test");
        entity.setType(LabelTypeEnum.MESSAGE);
        List<TranslationEntity> translationEntities = new ArrayList<>();
        TranslationEntity translationEntity = new TranslationEntity();
        translationEntity.setLanguage("en");
        translationEntity.setDescription("test description");
        translationEntities.add(translationEntity);
        entity.setTranslations(translationEntities);

        JsonObject mappedObject = I18NPropertyEntityMapper.map(entity);
        assertEquals(mappedObject.getString("key"), entity.getKey());
        assertEquals(mappedObject.getString("type"), entity.getType().name());
    }

    @Test
    public void testMapList() {
        I18NPropertyEntity entity = new I18NPropertyEntity();
        entity.setKey("test");
        entity.setType(LabelTypeEnum.MESSAGE);
        List<TranslationEntity> translationEntities = new ArrayList<>();
        TranslationEntity translationEntity = new TranslationEntity();
        translationEntity.setLanguage("en");
        translationEntity.setDescription("test description");
        translationEntities.add(translationEntity);
        entity.setTranslations(translationEntities);

        I18NPropertyEntity entity_1 = new I18NPropertyEntity();
        entity_1.setKey("test_1");
        entity_1.setType(LabelTypeEnum.MESSAGE);
        List<TranslationEntity> translationEntities_1 = new ArrayList<>();
        TranslationEntity translationEntity_1 = new TranslationEntity();
        translationEntity_1.setLanguage("en");
        translationEntity_1.setDescription("test description");
        entity_1.setTranslations(translationEntities_1);
        translationEntities_1.add(translationEntity_1);

        List<I18NPropertyEntity> entityList = Arrays.asList(entity, entity_1);
        JsonArray array = I18NPropertyEntityMapper.map(entityList);
        assertEquals(array.size(), entityList.size());
        assertEquals(array.getJsonObject(0).getString("key"), entity.getKey());
        assertEquals(array.getJsonObject(0).getString("type"), entity.getType().name());
        assertEquals(array.getJsonObject(1).getString("key"), entity_1.getKey());
        assertEquals(array.getJsonObject(1).getString("type"), entity_1.getType().name());

    }

    @Test
    public void testMapToEntityList() {
        I18NProperty property = new I18NProperty();
        property.setKey("test");
        property.setType(LabelTypeEnum.MESSAGE);
        List<Translation> translationEntities = new ArrayList<>();
        Translation Translation = new Translation();
        Translation.setLanguage("en");
        Translation.setDescription("test description");
        translationEntities.add(Translation);
        property.setTranslations(translationEntities);

        I18NProperty property1 = new I18NProperty();
        property1.setKey("test_1");
        property1.setType(LabelTypeEnum.MESSAGE);
        List<Translation> translationEntities_1 = new ArrayList<>();
        Translation Translation_1 = new Translation();
        Translation_1.setLanguage("en");
        Translation_1.setDescription("test description");
        property1.setTranslations(translationEntities_1);
        translationEntities_1.add(Translation_1);

        List<I18NProperty> propertyList = Arrays.asList(property, property1);
        List<I18NPropertyEntity> mappedEntity = I18NPropertyEntityMapper.mapToEntity(propertyList);
        assertEquals(propertyList.size(), mappedEntity.size());
        for(int i = 0; i < mappedEntity.size(); i++) {
            assertEquals(mappedEntity.get(i).getKey(), propertyList.get(i).getKey());
            assertEquals(mappedEntity.get(i).getType(), propertyList.get(i).getType());
            assertEquals(mappedEntity.get(i).getTranslations().size(), propertyList.get(i).getTranslations().size());
        }
    }

    @Test
    public void testMapToPropertyList() {
        I18NPropertyEntity entity = new I18NPropertyEntity();
        entity.setKey("test");
        entity.setType(LabelTypeEnum.MESSAGE);
        List<TranslationEntity> translationEntities = new ArrayList<>();
        TranslationEntity Translation = new TranslationEntity();
        Translation.setLanguage("en");
        Translation.setDescription("test description");
        translationEntities.add(Translation);
        entity.setTranslations(translationEntities);

        I18NPropertyEntity entity_1 = new I18NPropertyEntity();
        entity_1.setKey("test_1");
        entity_1.setType(LabelTypeEnum.MESSAGE);
        List<TranslationEntity> translationEntities_1 = new ArrayList<>();
        TranslationEntity Translation_1 = new TranslationEntity();
        Translation_1.setLanguage("en");
        Translation_1.setDescription("test description");
        entity_1.setTranslations(translationEntities_1);
        translationEntities_1.add(Translation_1);

        List<I18NPropertyEntity> entityList = Arrays.asList(entity, entity_1);
        List<I18NProperty> propertyList = I18NPropertyEntityMapper.mapToDTO(entityList);
        assertEquals(entityList.size(), propertyList.size());
        for(int i = 0; i < propertyList.size(); i++) {
            assertEquals(propertyList.get(i).getKey(), entityList.get(i).getKey());
            assertEquals(propertyList.get(i).getType(), entityList.get(i).getType());
            assertEquals(propertyList.get(i).getTranslations().size(), entityList.get(i).getTranslations().size());
        }
    }

    @Test
    public void testEntityPropertiesSize() {
        I18NProperty property = new I18NProperty();
        I18NPropertyEntity entity = new I18NPropertyEntity();
        List<Field> propertyFields = new ArrayList<>(Arrays.asList(property.getClass().getDeclaredFields()));
        List<Field> entityFields = new ArrayList<>(Arrays.asList(entity.getClass().getDeclaredFields()));
        assertEquals(propertyFields.size(), entityFields.size());
    }

    @Test
    public void testEntityPropertyInDTO() {
        I18NProperty property = new I18NProperty();
        I18NPropertyEntity entity = new I18NPropertyEntity();
        List<String> entityFields = Arrays.stream(entity.getClass().getDeclaredFields()).map(Field::getName).collect(Collectors.toList());
        for(Field f : property.getClass().getDeclaredFields()) {
            assertTrue(entityFields.contains(f.getName()));
        }
    }

    @Test
    public void testDTOPropertyInEntity() {
        I18NProperty property = new I18NProperty();
        I18NPropertyEntity entity = new I18NPropertyEntity();
        List<String> dtoFields = Arrays.stream(property.getClass().getDeclaredFields()).map(Field::getName).collect(Collectors.toList());
        for(Field f : entity.getClass().getDeclaredFields()) {
            assertTrue(dtoFields.contains(f.getName()));
        }
    }
}
