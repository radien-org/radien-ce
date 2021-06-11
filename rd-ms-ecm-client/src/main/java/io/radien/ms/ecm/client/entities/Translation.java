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

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Translation {

    private String language;

    private String description;

    /**
     * Translation language getter
     * @return the translation string value
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Translation language setter
     * @param language to be set
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Translation description getter
     * @return the translation description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Translation description setter
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
        Translation that = (Translation) o;
        return language.equals(that.language) && description.equals(that.description);
    }

}
