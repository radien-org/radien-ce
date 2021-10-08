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
package io.radien.ms.ecm.factory;

import io.radien.ms.ecm.client.entities.LabelTypeEnum;
import io.radien.ms.ecm.entities.I18NPropertyEntity;
import io.radien.ms.ecm.entities.TranslationEntity;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.Arrays;

public class I18NPropertyEntityFactoryTest extends TestCase {
    @Test
    public void testCreateWithDefaults() {
        I18NPropertyEntity expectedEntity = new I18NPropertyEntity();
        expectedEntity.setKey("key");
        expectedEntity.setType(LabelTypeEnum.MESSAGE);
        expectedEntity.setTranslations(
                Arrays.asList(TranslationEntityFactory.create("language", "description"))
        );
        I18NPropertyEntity createdEntity = I18NPropertyEntityFactory.createWithDefaults("key", LabelTypeEnum.MESSAGE, "language", "description");

        assertEquals(expectedEntity, createdEntity);
    }

    @Test
    public void testCreateSingleTranslation() {
        TranslationEntity translationEntity = TranslationEntityFactory.create("language", "description");
        I18NPropertyEntity expectedEntity = new I18NPropertyEntity();
        expectedEntity.setKey("key");
        expectedEntity.setType(LabelTypeEnum.MESSAGE);
        expectedEntity.setTranslations(
                Arrays.asList(translationEntity)
        );
        I18NPropertyEntity createdEntity = I18NPropertyEntityFactory.create("key", LabelTypeEnum.MESSAGE, translationEntity);

        assertEquals(expectedEntity, createdEntity);
    }

    @Test
    public void testCreateMultipleTranslation() {
        TranslationEntity translationEntity = TranslationEntityFactory.create("language", "description");
        TranslationEntity translationEntity1 = TranslationEntityFactory.create("language1", "description1");
        I18NPropertyEntity expectedEntity = new I18NPropertyEntity();
        expectedEntity.setKey("key");
        expectedEntity.setType(LabelTypeEnum.MESSAGE);
        expectedEntity.setTranslations(
                Arrays.asList(translationEntity, translationEntity1)
        );
        I18NPropertyEntity createdEntity = I18NPropertyEntityFactory.create("key", LabelTypeEnum.MESSAGE, Arrays.asList(translationEntity, translationEntity1));

        assertEquals(expectedEntity, createdEntity);
    }
}
