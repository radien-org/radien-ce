/*
 *
 *  * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
 *  * <p>
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  * <p>
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  * <p>
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package io.radien.ms.ecm.client.entities.i18n;

import io.radien.api.model.i18n.SystemI18NProperty;
import io.radien.api.model.i18n.SystemI18NTranslation;
import java.util.List;

public class I18NProperty implements SystemI18NProperty {
    private String key;
    private String application;
    private List<SystemI18NTranslation> translations;

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String getApplication() {
        return application;
    }

    @Override
    public void setApplication(String application) {
        this.application = application;
    }

    public List<SystemI18NTranslation> getTranslations() {
        return translations;
    }

    public void setTranslations(List<SystemI18NTranslation> translations) {
        this.translations = translations;
    }
}
