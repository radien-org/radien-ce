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

import io.radien.ms.ecm.entities.TranslationEntity;

/**
 * Translation entity factory, where we can create by given parameters the needed and required Translation Entity
 * @author andresousa
 */
public class TranslationEntityFactory {

    /**
     * Translation entity creation method
     * @param language of the translation entity
     * @param description of the translation entity
     * @return the created translation
     */
    public static TranslationEntity create(String language, String description) {
        TranslationEntity translationEntity = new TranslationEntity();
        translationEntity.setLanguage(language);
        translationEntity.setDescription(description);

        return translationEntity;
    }
}
