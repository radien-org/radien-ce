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
package io.radien.ms.ecm.entities;

import io.radien.ms.ecm.client.entities.LabelTypeEnum;
import org.jnosql.artemis.Column;
import org.jnosql.artemis.Entity;
import org.jnosql.artemis.Id;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Lightweight simple translation module with dynamic JSON storage.
 *
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

    /**
     * I18N translation key value getter
     * @return the I18N key string value
     */
    public String getKey() {
        return key;
    }

    /**
     * I18N translation key value setter
     * @param key to be set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * I18N translation type value getter
     * @return the I18N type
     */
    public LabelTypeEnum getType() {
        return type;
    }

    /**
     * I18N translation type setter
     * @param type to be set
     */
    public void setType(LabelTypeEnum type) {
        this.type = type;
    }

    /**
     * I18N translation entity getter
     * @return a list of translations
     */
    public List<TranslationEntity> getTranslations() {
        return translations;
    }

    /**
     * I18N translation entity setter
     * @param translationEntities to be set
     */
    public void setTranslations(List<TranslationEntity> translationEntities) {
        this.translations = translationEntities;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * The equals method implements an equivalence relation on non-null object references:
     * @param o the reference object with which to compare.
     * @return if this object is the same as the obj argument; false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        I18NPropertyEntity entity = (I18NPropertyEntity) o;
        return key.equals(entity.key) && type == entity.type && translations.equals(entity.translations);
    }

    /**
     * Returns a hash code value for the object. This method is supported for the benefit of hash tables such
     * as those provided by HashMap.
     * The general contract of hashCode is:
     * Whenever it is invoked on the same object more than once during an execution of a Java application,
     * the hashCode method must consistently return the same integer, provided no information used in equals
     * comparisons on the object is modified. This integer need not remain consistent from one execution of an
     * application to another execution of the same application.
     * If two objects are equal according to the equals(Object) method, then calling the hashCode method on each of
     * the two objects must produce the same integer result.
     * It is not required that if two objects are unequal according to the equals(java.lang.Object) method, then
     * calling the hashCode method on each of the two objects must produce distinct integer results. However, the
     * programmer should be aware that producing distinct integer results for unequal objects may improve the
     * performance of hash tables.
     * As much as is reasonably practical, the hashCode method defined by class Object does return distinct integers
     * for distinct objects. (This is typically implemented by converting the internal address of the object into an
     * integer, but this implementation technique is not required by the JavaTM programming language.)
     * @return hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(key, type, translations);
    }
}
