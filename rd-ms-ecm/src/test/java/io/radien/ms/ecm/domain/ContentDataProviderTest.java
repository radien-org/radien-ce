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

package io.radien.ms.ecm.domain;

import io.radien.api.service.ecm.exception.NameNotValidException;
import io.radien.api.service.ecm.model.EnterpriseContent;
import io.radien.api.service.ecm.model.GenericEnterpriseContent;
import io.radien.ms.ecm.util.ContentMappingUtils;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.List;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.FieldSetter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ContentDataProviderTest {
    @InjectMocks
    private ContentDataProvider contentDataProvider;

    @Mock
    private ContentMappingUtils contentMappingUtils;

    @Before
    public void init() throws NoSuchFieldException {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetContentsWithValues() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException, ParseException, NameNotValidException, NoSuchFieldException {
        Field supportedLanguagesField = contentDataProvider.getClass().getDeclaredField("supportedLanguages");
        FieldSetter.setField(contentDataProvider, supportedLanguagesField, "en");
        EnterpriseContent mappedContent = new GenericEnterpriseContent("name");
        when(contentMappingUtils.convertSeederJSONObject(any(JSONObject.class)))
                .thenReturn(mappedContent);
        getInitMethod().invoke(contentDataProvider);
        List<EnterpriseContent> mappedList = contentDataProvider.getContents();
        assertNotNull(mappedList);
        assertFalse(mappedList.isEmpty());
        assertEquals(mappedContent.getName(), mappedList.get(0).getName());
    }

    @Test
    public void testGetContentsError() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException, ParseException, NameNotValidException, NoSuchFieldException {
        Field supportedLanguagesField = contentDataProvider.getClass().getDeclaredField("supportedLanguages");
        FieldSetter.setField(contentDataProvider, supportedLanguagesField, "en");
        EnterpriseContent mappedContent = new GenericEnterpriseContent("name");
        when(contentMappingUtils.convertSeederJSONObject(any(JSONObject.class)))
                .thenThrow(new IOException());
        getInitMethod().invoke(contentDataProvider);
        List<EnterpriseContent> mappedList = contentDataProvider.getContents();
        assertNotNull(mappedList);
        assertTrue(mappedList.isEmpty());
    }

    @Test
    public void testSupportedLanguages() throws NoSuchFieldException {
        Field supportedLanguagesField = contentDataProvider.getClass().getDeclaredField("supportedLanguages");
        FieldSetter.setField(contentDataProvider, supportedLanguagesField, "en");
        List<String> supportedLanguages = contentDataProvider.getSupportedLanguages();
        assertEquals(1, supportedLanguages.size());
        assertTrue(supportedLanguages.contains("en"));
    }


    private Method getInitMethod() throws NoSuchMethodException {
        Method initMethod = contentDataProvider.getClass()
                .getDeclaredMethod("init");
        initMethod.setAccessible(true);
        return initMethod;
    }
}
