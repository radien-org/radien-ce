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
package io.radien.ms.ecm.service;

import io.radien.ms.ecm.client.entities.I18NProperty;
import io.radien.ms.ecm.client.entities.LabelTypeEnum;
import io.radien.ms.ecm.entities.I18NPropertyEntity;
import io.radien.ms.ecm.factory.I18NPropertyEntityFactory;
import io.radien.ms.ecm.util.I18NPropertyEntityMapper;
import junit.framework.TestCase;
import org.jnosql.artemis.document.DocumentTemplate;
import org.jnosql.diana.api.document.DocumentQuery;
import org.jnosql.diana.api.document.query.DocumentSelect;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

public class I18NPropertyServiceTest extends TestCase {
    @InjectMocks
    private I18NPropertyService i18NPropertyService;

    @Mock
    private DocumentTemplate documentTemplate;
    @Mock
    private DocumentSelect documentSelect;

    private I18NPropertyEntity mockPropertyEntity;
    private I18NProperty mockProperty;
    private List<I18NPropertyEntity> mockPropertyEntityList;
    private List<I18NProperty> mockPropertyList;

    public I18NPropertyServiceTest() {
        MockitoAnnotations.initMocks(this);
        mockPropertyEntityList = new ArrayList<>();
        mockPropertyList = new ArrayList<>();
        mockPropertyEntity = I18NPropertyEntityFactory.createWithDefaults("test", LabelTypeEnum.MESSAGE, "en", "test description");
        mockProperty = I18NPropertyEntityMapper.mapToDTO(mockPropertyEntity);
        for(int i = 0; i < 5; i++) {
            I18NPropertyEntity entity = I18NPropertyEntityFactory.createWithDefaults(String.format("test_%o", i), LabelTypeEnum.MESSAGE, "en", String.format("test_%o", i));
            mockPropertyEntityList.add(entity);
            mockPropertyList.add(I18NPropertyEntityMapper.mapToDTO(entity));
        }
    }

    @Test
    public void testGetKey_available() {
        when(documentTemplate.find(any(), any())).thenReturn(Optional.of(mockPropertyEntity));

        I18NProperty returnedProperty = i18NPropertyService.getByKey("anyKey");
        assertEquals(mockPropertyEntity.getKey(), returnedProperty.getKey());
        assertEquals(mockPropertyEntity.getType(), returnedProperty.getType());
        assertEquals(mockPropertyEntity.getTranslations().size(), returnedProperty.getTranslations().size());
        assertEquals(mockPropertyEntity.getTranslations().get(0).getLanguage(), returnedProperty.getTranslations().get(0).getLanguage());
        assertEquals(mockPropertyEntity.getTranslations().get(0).getDescription(), returnedProperty.getTranslations().get(0).getDescription());
    }

    @Test
    public void testGetKey_not_available() {
        when(documentTemplate.find(any(), any())).thenReturn(Optional.empty());
        assertNull(i18NPropertyService.getByKey("anyKey"));
    }

    @Test
    public void testGetAll() {
        Mockito.<List<I18NPropertyEntity>>when(documentTemplate.select(any(DocumentQuery.class))).thenReturn(mockPropertyEntityList);
        List<I18NProperty> returnedList = i18NPropertyService.getAll();

        assertEquals(returnedList.size(), mockPropertyEntityList.size());
    }

    @Test
    public void testGetKeys() {
        Mockito.<List<I18NPropertyEntity>>when(documentTemplate.select(any(DocumentQuery.class))).thenReturn(mockPropertyEntityList);
        List<String> keys = i18NPropertyService.getKeys();
        List<String> mockedKeys = mockPropertyEntityList.stream().map(I18NPropertyEntity::getKey).collect(Collectors.toList());

        assertEquals(keys, mockedKeys);
    }

    @Test
    public void testDelete() {
        mockPropertyEntityList.add(mockPropertyEntity);
        doAnswer(invocationOnMock -> {
            I18NPropertyEntity param = invocationOnMock.getArgument(1);

            mockPropertyEntityList.remove(param);
            return null;
        }).when(documentTemplate).delete(any(), any());
        i18NPropertyService.delete(mockProperty);

        assertFalse(mockPropertyEntityList.contains(mockProperty));
    }

    @Test
    public void testSave() {
        when(documentTemplate.insert(any(I18NPropertyEntity.class))).thenReturn(mockPropertyEntity);
        I18NProperty returnedProperty = i18NPropertyService.save(mockProperty);

        assertEquals(mockPropertyEntity.getKey(), returnedProperty.getKey());
        assertEquals(mockPropertyEntity.getType(), returnedProperty.getType());
        assertEquals(mockPropertyEntity.getTranslations().size(), returnedProperty.getTranslations().size());
        assertEquals(mockPropertyEntity.getTranslations().get(0).getLanguage(), returnedProperty.getTranslations().get(0).getLanguage());
        assertEquals(mockPropertyEntity.getTranslations().get(0).getDescription(), returnedProperty.getTranslations().get(0).getDescription());
    }

    @Test
    public void testBulkSave() {
        when(documentTemplate.insert(any(Iterable.class))).thenReturn(mockPropertyEntityList);
        List<I18NProperty> returnedList = i18NPropertyService.save(mockPropertyList);

        assertEquals(returnedList.size(), mockPropertyList.size());
    }

    @Test
    public void testGetLocalizedMessage() {
        when(documentTemplate.find(any(), any())).thenReturn(Optional.of(mockPropertyEntity));

        assertEquals(i18NPropertyService.getLocalizedMessage("test"), "test description");
    }
}
