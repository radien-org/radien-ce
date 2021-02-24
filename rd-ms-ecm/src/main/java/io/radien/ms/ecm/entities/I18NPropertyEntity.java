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
package io.radien.ms.ecm.entities;

import io.radien.ms.ecm.client.entities.LabelTypeEnum;
import org.jnosql.artemis.Column;
import org.jnosql.artemis.Entity;
import org.jnosql.artemis.Id;

import java.io.Serializable;
import java.util.List;

/**
 * @author andresousa
 */

@Entity
public class I18NPropertyEntity implements Serializable {
    @Id
    private String key;

    @Column
    private LabelTypeEnum type;

    @Column
    private List<TranslationEntity> translations;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public LabelTypeEnum getType() {
        return type;
    }

    public void setType(LabelTypeEnum type) {
        this.type = type;
    }

    public List<TranslationEntity> getTranslations() {
        return translations;
    }

    public void setTranslations(List<TranslationEntity> translationEntities) {
        this.translations = translationEntities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        I18NPropertyEntity entity = (I18NPropertyEntity) o;
        return key.equals(entity.key) && type == entity.type && translations.equals(entity.translations);
    }
}
