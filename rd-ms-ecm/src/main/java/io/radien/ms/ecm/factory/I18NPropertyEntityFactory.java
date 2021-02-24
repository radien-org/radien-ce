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
package io.radien.ms.ecm.factory;

import io.radien.ms.ecm.client.entities.LabelTypeEnum;
import io.radien.ms.ecm.entities.I18NPropertyEntity;
import io.radien.ms.ecm.entities.TranslationEntity;

import java.util.Arrays;
import java.util.List;

/**
 * @author andresousa
 */

public class I18NPropertyEntityFactory {

    public static I18NPropertyEntity createWithDefaults(String key, LabelTypeEnum type, String language, String description) {
        I18NPropertyEntity entity = new I18NPropertyEntity();
        entity.setKey(key);
        entity.setType(type);
        entity.setTranslations(Arrays.asList(TranslationEntityFactory.create(language, description)));

        return entity;
    }

    public static I18NPropertyEntity create(String key, LabelTypeEnum type, TranslationEntity translationEntity) {
        I18NPropertyEntity entity = new I18NPropertyEntity();
        entity.setKey(key);
        entity.setType(type);
        entity.setTranslations(Arrays.asList(translationEntity));

        return entity;
    }

    public static I18NPropertyEntity create(String key, LabelTypeEnum type, List<TranslationEntity> translationEntity) {
        I18NPropertyEntity entity = new I18NPropertyEntity();
        entity.setKey(key);
        entity.setType(type);
        entity.setTranslations(translationEntity);

        return entity;
    }
}
