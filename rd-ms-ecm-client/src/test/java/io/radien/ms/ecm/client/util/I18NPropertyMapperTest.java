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

import io.radien.ms.ecm.client.entities.I18NProperty;
import io.radien.ms.ecm.client.entities.LabelTypeEnum;
import io.radien.ms.ecm.client.entities.Translation;
import junit.framework.TestCase;
import org.junit.Test;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class I18NPropertyMapperTest extends TestCase {
    @Test
    public void testMap() {
        I18NProperty entity = new I18NProperty();
        entity.setKey("test");
        entity.setType(LabelTypeEnum.MESSAGE);
        List<Translation> translationEntities = new ArrayList<>();
        Translation Translation = new Translation();
        Translation.setLanguage("en");
        Translation.setDescription("test description");
        translationEntities.add(Translation);
        entity.setTranslations(translationEntities);

        JsonObject mappedObject = I18NPropertyMapper.map(entity);
        assertEquals(mappedObject.getString("key"), entity.getKey());
        assertEquals(mappedObject.getString("type"), entity.getType().name());
    }

    @Test
    public void testMapList() {
        I18NProperty entity = new I18NProperty();
        entity.setKey("test");
        entity.setType(LabelTypeEnum.MESSAGE);
        List<Translation> translationEntities = new ArrayList<>();
        Translation Translation = new Translation();
        Translation.setLanguage("en");
        Translation.setDescription("test description");
        translationEntities.add(Translation);
        entity.setTranslations(translationEntities);

        I18NProperty entity_1 = new I18NProperty();
        entity_1.setKey("test_1");
        entity_1.setType(LabelTypeEnum.MESSAGE);
        List<Translation> translationEntities_1 = new ArrayList<>();
        Translation Translation_1 = new Translation();
        Translation_1.setLanguage("en");
        Translation_1.setDescription("test description");
        entity_1.setTranslations(translationEntities_1);
        translationEntities_1.add(Translation_1);

        List<I18NProperty> entityList = Arrays.asList(entity, entity_1);
        JsonArray array = I18NPropertyMapper.map(entityList);
        assertEquals(array.size(), entityList.size());
        assertEquals(array.getJsonObject(0).getString("key"), entity.getKey());
        assertEquals(array.getJsonObject(0).getString("type"), entity.getType().name());
        assertEquals(array.getJsonObject(1).getString("key"), entity_1.getKey());
        assertEquals(array.getJsonObject(1).getString("type"), entity_1.getType().name());

    }

    @Test
    public void testMapInputStream() {
        String object = "{ " +
                "\"key\": \"test\", " +
                "\"type\": \"MESSAGE\", " +
                "\"translations\": [ " +
                "{ \"language\": \"en\", \"description\": \"test description\" } " +
                "] }";
        InputStream in = new ByteArrayInputStream(object.getBytes());

        I18NProperty mapped = I18NPropertyMapper.map(in);
        I18NProperty entity = new I18NProperty();
        entity.setKey("test");
        entity.setType(LabelTypeEnum.MESSAGE);
        List<Translation> translationEntities = new ArrayList<>();
        Translation Translation = new Translation();
        Translation.setLanguage("en");
        Translation.setDescription("test description");
        translationEntities.add(Translation);
        entity.setTranslations(translationEntities);

        assertEquals(entity, mapped);
    }
}
