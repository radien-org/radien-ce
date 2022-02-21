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

package io.radien.ms.ecm.client.services;

import io.radien.api.model.i18n.SystemI18NProperty;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.json.simple.parser.ParseException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class I18NPropertyMapperTest {
    @Test
    public void testMap() throws IOException, ParseException {
        String result = "{\"" +
                "key\":\"keyValue\"," +
                "\"application\": \"radien\"," +
                "\"translations\": [" +
                "{" +
                     "\"language\": \"en\"," +
                     "\"value\": \"value\"" +
                "}]}";
        InputStream in = new ByteArrayInputStream(result.getBytes());
        SystemI18NProperty resultProperty = I18NPropertyMapper.map(in);
        assertEquals("keyValue", resultProperty.getKey());
        assertEquals("radien", resultProperty.getApplication());
        assertFalse(resultProperty.getTranslations().isEmpty());
        assertEquals(1, resultProperty.getTranslations().size());
        assertEquals("en", resultProperty.getTranslations().get(0).getLanguage());
        assertEquals("value", resultProperty.getTranslations().get(0).getValue());
    }

    @Test
    public void testMapArray() throws IOException, ParseException {
        String result = "[" +
                    "{\"" +
                    "key\":\"keyValue\"," +
                    "\"application\": \"radien\"," +
                    "\"translations\": [" +
                    "{" +
                         "\"language\": \"en\"," +
                         "\"value\": \"value\"" +
                    "}]" +
                "}," +
                "{\"" +
                    "key\":\"keyValue1\"," +
                    "\"application\": \"radien\"," +
                    "\"translations\": [" +
                    "{" +
                    "\"language\": \"en\"," +
                    "\"value\": \"value1\"" +
                    "}]" +
                "}]";
        InputStream in = new ByteArrayInputStream(result.getBytes());
        List<SystemI18NProperty> resultProperty = I18NPropertyMapper.mapArray(in);
        assertFalse(resultProperty.isEmpty());
        assertEquals(2, resultProperty.size());

        assertEquals("keyValue", resultProperty.get(0).getKey());
        assertEquals("keyValue1", resultProperty.get(1).getKey());
        assertEquals("radien", resultProperty.get(0).getApplication());
        assertEquals("radien", resultProperty.get(1).getApplication());
        assertFalse(resultProperty.get(0).getTranslations().isEmpty());
        assertFalse(resultProperty.get(1).getTranslations().isEmpty());
        assertEquals(1, resultProperty.get(0).getTranslations().size());
        assertEquals(1, resultProperty.get(1).getTranslations().size());
        assertEquals("en", resultProperty.get(0).getTranslations().get(0).getLanguage());
        assertEquals("en", resultProperty.get(1).getTranslations().get(0).getLanguage());
        assertEquals("value", resultProperty.get(0).getTranslations().get(0).getValue());
        assertEquals("value1", resultProperty.get(1).getTranslations().get(0).getValue());
    }
}
