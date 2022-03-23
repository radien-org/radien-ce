/*
 * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
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

package io.radien.ms.doctypemanagement.service;

import io.radien.api.entity.Page;
import io.radien.api.model.docmanagement.propertydefinition.SystemPropertyDefinition;
import io.radien.api.service.docmanagement.exception.DocumentTypeException;
import io.radien.api.service.docmanagement.exception.PropertyDefinitionNotFoundException;
import io.radien.api.service.docmanagement.propertydefinition.PropertyDefinitionDataAccessLayer;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.doctypemanagement.client.entities.PropertyDefinition;
import java.util.ArrayList;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PropertyDefinitionServiceTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @InjectMocks
    private PropertyDefinitionService service;
    @Mock private PropertyDefinitionDataAccessLayer definitionDataAccessLayer;

    @Test
    public void testGetAll() {
        Page<SystemPropertyDefinition> page = new Page<>();
        page.setCurrentPage(1);
        page.setTotalResults(10);
        page.setTotalPages(10);
        when(definitionDataAccessLayer.getAll(anyString(), anyInt(), anyInt(), anyList(), anyBoolean()))
                .thenReturn(page);

        Page<? extends SystemPropertyDefinition> result = service.getAll("", 1, 10, new ArrayList<>(), false);
        assertEquals(page, result);
    }

    @Test
    public void testGetById() {
        SystemPropertyDefinition propertyDefinition = new PropertyDefinition();
        propertyDefinition.setName("aName");
        when(definitionDataAccessLayer.get(anyLong()))
                .thenReturn(propertyDefinition);

        assertEquals("aName", service.getById(1L).getName());
    }

    @Test(expected = PropertyDefinitionNotFoundException.class)
    public void testGetByIdNotFound() {
        when(definitionDataAccessLayer.get(anyLong()))
                .thenReturn(null);
        service.getById(1L);
    }

    @Test
    public void testDelete() {
        service.delete(1L);
        verify(definitionDataAccessLayer).delete(1L);
    }

    @Test
    public void testSave() throws UniquenessConstraintException {
        PropertyDefinition propertyDefinition = new PropertyDefinition();
        propertyDefinition.setName("aName");
        service.save(propertyDefinition);
        verify(definitionDataAccessLayer).save(propertyDefinition);
    }

    @Test(expected = DocumentTypeException.class)
    public void testSaveDuplicate() throws UniquenessConstraintException {
        doThrow(new UniquenessConstraintException())
                .when(definitionDataAccessLayer).save(any());
        service.save(new PropertyDefinition());
    }

    @Test
    public void testGetTotalRecordsCount() {
        when(definitionDataAccessLayer.getTotalRecordsCount())
                .thenReturn(10L);
        assertEquals(10L, service.getTotalRecordsCount());
    }

}
