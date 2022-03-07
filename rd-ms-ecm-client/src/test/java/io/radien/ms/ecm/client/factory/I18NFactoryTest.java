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

import io.radien.api.entity.Page;
import io.radien.api.model.i18n.SystemI18NProperty;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class I18NFactoryTest {
    @Test
    public void testConvertJSONObject() {
        JSONObject obj = new JSONObject();
        obj.put("key", "keyVal");
        obj.put("application", "radienTest");

        SystemI18NProperty property = I18NFactory.convertJSONObject(obj);
        assertEquals("keyVal", property.getKey());
        assertEquals("radienTest", property.getApplication());
        assertTrue(property.getTranslations().isEmpty());
    }

    @Test
    public void testConvertJSONObjectWithTranslation() {
        JSONObject obj = new JSONObject();
        obj.put("key", "keyVal");
        obj.put("application", "radienTest");
        JSONObject translation = new JSONObject();
        translation.put("language", "en");
        translation.put("value", "value");
        JSONArray translationArray = new JSONArray();
        translationArray.add(translation);
        obj.put("translations", translationArray);

        SystemI18NProperty property = I18NFactory.convertJSONObject(obj);
        assertEquals("keyVal", property.getKey());
        assertEquals("radienTest", property.getApplication());
        assertFalse(property.getTranslations().isEmpty());
        assertEquals(1, property.getTranslations().size());
        assertEquals("en", property.getTranslations().get(0).getLanguage());
        assertEquals("value", property.getTranslations().get(0).getValue());
    }

    @Test
    public void testConvertJSONArrayWithTranslation() {
        JSONArray propertyList = new JSONArray();
        for(int i = 0; i < 10; i++) {
            JSONObject obj = new JSONObject();
            obj.put("key", "keyVal");
            obj.put("application", "radienTest");
            JSONObject translation = new JSONObject();
            translation.put("language", "en");
            translation.put("value", "value");
            JSONArray translationArray = new JSONArray();
            translationArray.add(translation);
            obj.put("translations", translationArray);
            propertyList.add(obj);
        }

        List<SystemI18NProperty> property = I18NFactory.convertJSONArray(propertyList);
        assertFalse(property.isEmpty());
        assertEquals(10, property.size());
        assertEquals("keyVal", property.get(0).getKey());
        assertEquals("radienTest", property.get(0).getApplication());
        assertEquals("en", property.get(0).getTranslations().get(0).getLanguage());
        assertEquals("value", property.get(0).getTranslations().get(0).getValue());
    }

    @Test
    public void testConvertJsonToPage() {
        JSONObject object = new JSONObject();
        object.put("currentPage", "1");
        object.put("totalPages", "1");
        object.put("totalResults", "1");
        JSONArray resultArray = new JSONArray();
        JSONObject obj = new JSONObject();
        obj.put("key", "keyVal");
        obj.put("application", "radienTest");
        JSONObject translation = new JSONObject();
        translation.put("language", "en");
        translation.put("value", "value");
        JSONArray translationArray = new JSONArray();
        translationArray.add(translation);
        obj.put("translations", translationArray);
        resultArray.add(obj);
        object.put("results", resultArray);

        Page<SystemI18NProperty> page = I18NFactory.convertJsonToPage(object);
        assertEquals(1, page.getCurrentPage());
        assertEquals(1, page.getTotalResults());
        assertEquals(1, page.getTotalPages());
        assertEquals(1, page.getResults().size());
    }
}
