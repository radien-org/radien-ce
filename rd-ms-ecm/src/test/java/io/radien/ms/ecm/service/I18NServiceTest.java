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
package io.radien.ms.ecm.service;

import io.radien.api.model.i18n.SystemI18NProperty;
import io.radien.exception.SystemException;
import io.radien.ms.ecm.client.entities.i18n.I18NProperty;
import io.radien.ms.ecm.config.ConfigHandler;
import io.radien.ms.ecm.datalayer.I18NRepository;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.microprofile.config.Config;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class I18NServiceTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @InjectMocks
    private I18NService service;
    @Mock
    private I18NRepository repository;
    @Mock
    private ConfigHandler configHandler;

    @Before
    public void init() {
        when(configHandler.getDefaultLanguage()).thenReturn("en");
    }

    @Test
    public void testGetTranslation() throws SystemException {
        when(repository.getTranslation("key", "en", "application"))
                .thenReturn("value");
        assertEquals("value", service.getTranslation("key", "en", "application"));
    }

    @Test
    public void testGetTranslationFallback() throws SystemException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        getInitMethod().invoke(service);
        when(repository.getTranslation("key", "en", "application"))
                .thenReturn("value");
        when(repository.getTranslation("key", "en-US", "application"))
                .thenReturn("key");
        assertEquals("value", service.getTranslation("key", "en-US", "application"));
    }

    @Test
    public void testSave() throws SystemException {
        service.save(new I18NProperty());
        verify(repository).save(any(I18NProperty.class));
    }

    @Test
    public void testDelete() throws SystemException {
        List<SystemI18NProperty> propertyList = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            propertyList.add(new I18NProperty());
        }
        service.deleteProperties(propertyList);
        verify(repository, times(10)).deleteProperty(any(I18NProperty.class));
    }

    @Test
    public void testDeleteApplicationProperties() throws SystemException {
        service.deleteApplicationProperties("application");
        verify(repository).deleteApplication("application");
    }

    @Test
    public void testFindByKeyAndApplication() throws SystemException {
        SystemI18NProperty property = new I18NProperty();
        property.setKey("key");
        property.setApplication("app");
        when(repository.findByKeyAndApplication("key", "app"))
                .thenReturn(property);
        SystemI18NProperty result = service.findByKeyAndApplication("key", "app");
        assertEquals("key", result.getKey());
        assertEquals("app", result.getApplication());
    }

    @Test
    public void testFindByKeyAndApplicationNotExists() throws SystemException {
        when(repository.findByKeyAndApplication("key", "app"))
                .thenReturn(null);
        SystemI18NProperty result = service.findByKeyAndApplication("key", "property");
        assertNull(result);
    }

    @Test
    public void testFindAllByApplication() throws SystemException {
        List<SystemI18NProperty> propertyList = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            propertyList.add(new I18NProperty());
        }
        when(repository.findAllByApplication("app"))
                .thenReturn(propertyList);
        List<SystemI18NProperty> app = service.findAllByApplication("app");
        assertFalse(app.isEmpty());
        assertEquals(10, app.size());
    }

    private Method getInitMethod() throws NoSuchMethodException {
        Method initMethod = service.getClass()
                .getDeclaredMethod("init");
        initMethod.setAccessible(true);
        return initMethod;
    }
}
