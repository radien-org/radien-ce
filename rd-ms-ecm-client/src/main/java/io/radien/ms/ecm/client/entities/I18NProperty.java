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

package io.radien.ms.ecm.client.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

public class I18NProperty implements Serializable {

    @JsonProperty("key")
    private String key;

    @NotNull
    @JsonProperty("type")
    private LabelTypeEnum type;

    @JsonProperty("translations")
    private List<Translation> translations;

    /**
     * I18N property key getter method
     * @return the I18N key
     */
    public String getKey() {
        return key;
    }

    /**
     * I18N property key setter method
     * @param key to be set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * I18N property type getter
     * @return a label type enumerate of the I18N property type
     */
    public LabelTypeEnum getType() {
        return type;
    }

    /**
     * I18N property type setter
     * @param type to be set
     */
    public void setType(LabelTypeEnum type) {
        this.type = type;
    }

    /**
     * I18N property translation getter
     * @return a list of possible translations for the I18N property
     */
    public List<Translation> getTranslations() {
        return translations;
    }

    /**
     * I18N property translation setter
     * @param translations to be set
     */
    public void setTranslations(List<Translation> translations) {
        this.translations = translations;
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
        I18NProperty that = (I18NProperty) o;
        return key.equals(that.key) && type == that.type && translations.equals(that.translations);
    }

}
