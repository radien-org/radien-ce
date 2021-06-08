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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jnosql.artemis.Column;
import org.jnosql.artemis.Embeddable;

import javax.validation.constraints.NotNull;

/**
 * The translation entity class object composed by a language and a description
 *
 * @author andresousa
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Embeddable
public class TranslationEntity {

    @JsonProperty("language")
    @NotNull
    @Column
    private String language;

    @JsonProperty("description")
    @Column
    private String description;

    /**
     * Translation entity language getter method
     * @return the translation entity language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Translation entity language setter method
     * @param language to be set
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Translation entity description getter
     * @return the translation entity description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Translation entity description setter
     * @param description to be set
     */
    public void setDescription(String description) {
        this.description = description;
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
        TranslationEntity that = (TranslationEntity) o;
        return language.equals(that.language) && description.equals(that.description);
    }
}
