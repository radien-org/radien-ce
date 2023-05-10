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
import io.radien.api.model.docmanagement.mixindefinition.SystemMixinDefinition;
import io.radien.api.model.docmanagement.propertydefinition.SystemPropertyDefinition;
import io.radien.api.service.docmanagement.exception.DocumentTypeException;
import io.radien.api.service.docmanagement.exception.MixinDefinitionNotFoundException;
import io.radien.api.service.docmanagement.exception.PropertyDefinitionNotFoundException;
import io.radien.api.service.docmanagement.mixindefinition.MixinDefinitionDataAccessLayer;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.doctypemanagement.client.entities.MixinDefinitionDTO;
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

public class MixinDefinitionServiceTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();
    
    @InjectMocks
    private MixinDefinitionService service;
    @Mock
    private MixinDefinitionDataAccessLayer dataAccessLayer;

    @Test
    public void testGetAll() {
        Page<SystemMixinDefinition<Long>> page = new Page<>();
        page.setCurrentPage(1);
        page.setTotalResults(10);
        page.setTotalPages(10);
        when(dataAccessLayer.getAll(anyString(), anyInt(), anyInt(), anyList(), anyBoolean()))
                .thenReturn(page);

        Page<? extends SystemMixinDefinition<Long>> result = service.getAll("", 1, 10, new ArrayList<>(), false);
        assertEquals(page, result);
    }

    @Test
    public void testGetById() {
        SystemMixinDefinition<Long> mixinDefinition = new MixinDefinitionDTO();
        mixinDefinition.setName("aName");
        when(dataAccessLayer.get(anyLong()))
                .thenReturn(mixinDefinition);

        assertEquals("aName", service.getById(1L).getName());
    }

    @Test(expected = MixinDefinitionNotFoundException.class)
    public void testGetByIdNotFound() {
        when(dataAccessLayer.get(anyLong()))
                .thenReturn(null);
        service.getById(1L);
    }

    @Test
    public void testDelete() {
        service.delete(1L);
        verify(dataAccessLayer).delete(1L);
    }

    @Test
    public void testSave() throws UniquenessConstraintException {
        MixinDefinitionDTO mixinDefinition = new MixinDefinitionDTO();
        mixinDefinition.setName("aName");
        service.save(mixinDefinition);
        verify(dataAccessLayer).save(mixinDefinition);
    }

    @Test(expected = DocumentTypeException.class)
    public void testSaveDuplicate() throws UniquenessConstraintException {
        doThrow(new UniquenessConstraintException())
                .when(dataAccessLayer).save(any());
        service.save(new MixinDefinitionDTO());
    }

    @Test
    public void testGetTotalRecordsCount() {
        when(dataAccessLayer.getTotalRecordsCount())
                .thenReturn(10L);
        assertEquals(10L, service.getTotalRecordsCount());
    }

}