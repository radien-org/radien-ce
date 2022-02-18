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

package io.radien.ms.ecm.client.factory;

import io.radien.api.model.i18n.SystemI18NProperty;
import io.radien.api.model.i18n.SystemI18NTranslation;
import io.radien.ms.ecm.client.entities.i18n.I18NProperty;
import io.radien.ms.ecm.client.entities.i18n.I18NTranslation;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class I18NFactory {
    private static final Logger log = LoggerFactory.getLogger(I18NFactory.class);

    private I18NFactory() {
        throw new IllegalStateException("Utility class");
    }

    public static SystemI18NProperty convertJSONObject(JSONObject json) {
        String key = tryGetJsonProperty(json, "key");
        String application = tryGetJsonProperty(json, "application");

        SystemI18NProperty property = new I18NProperty();
        property.setKey(key);
        property.setApplication(application);
        property.setTranslations(convertTranslationJSONArray((JSONArray) json.get("translations")));

        return property;
    }


    public static List<SystemI18NProperty> convertJSONArray(JSONArray array) {
        List<SystemI18NProperty> result = new ArrayList<>();
        if(array != null) {
            array.forEach(json -> result.add(convertJSONObject((JSONObject) json)));
        }

        return result;
    }

    public static SystemI18NTranslation convertTranslationJSONObject(JSONObject json) {
        String language = tryGetJsonProperty(json, "language");
        String value = tryGetJsonProperty(json, "value");

        SystemI18NTranslation translation = new I18NTranslation();
        translation.setLanguage(language);
        translation.setValue(value);
        return translation;
    }

    public static List<SystemI18NTranslation> convertTranslationJSONArray(JSONArray array) {
        List<SystemI18NTranslation> result = new ArrayList<>();
        if(array != null) {
            array.forEach(json -> result.add(convertTranslationJSONObject((JSONObject) json)));
        }

        return result;
    }

    private static String tryGetJsonProperty(JSONObject object, String key) {
        Object value = object.get(key);
        return value != null ? value.toString() : null;
    }
}
