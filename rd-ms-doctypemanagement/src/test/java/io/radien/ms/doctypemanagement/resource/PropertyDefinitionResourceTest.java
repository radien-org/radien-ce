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

package io.radien.ms.doctypemanagement.resource;

import io.radien.api.entity.Page;
import io.radien.ms.doctypemanagement.client.entities.PropertyDefinition;
import io.radien.ms.doctypemanagement.service.PropertyDefinitionService;
import java.util.ArrayList;
import java.util.Collections;
import javax.ws.rs.core.Response;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class PropertyDefinitionResourceTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @InjectMocks
    private PropertyDefinitionResource resource;

    @Mock private PropertyDefinitionService service;

    @Test
    public void testGetAll() {
        when(service.getAll(anyString(), anyInt(), anyInt(), anyList(), anyBoolean()))
                .thenReturn(new Page<>());
        Response result = resource.getAll("", 1, 10, new ArrayList<>(), false);
        assertEquals(200, result.getStatusInfo().getStatusCode());
        assertNotNull(result.getEntity());
    }

    @Test
    public void testGetById() {
        when(service.getById(anyLong()))
                .thenReturn(new PropertyDefinition());
        Response result = resource.getById(1L);
        assertEquals(200, result.getStatusInfo().getStatusCode());
        assertNotNull(result.getEntity());
    }

    @Test
    public void testGetNameListByIds() {
        String resultString = "Property Name 1, Property Name 2";
        when(service.getNames(anyList()))
                .thenReturn(resultString);
        Response result = resource.getNameListByIds(Collections.singletonList(1L));
        assertEquals(200, result.getStatusInfo().getStatusCode());
        assertEquals(resultString, result.readEntity(String.class));
    }

    @Test
    public void testDelete() {
        Response result = resource.delete(1L);
        assertEquals(200, result.getStatusInfo().getStatusCode());
        assertNull(result.getEntity());
    }

    @Test
    public void testSave() {
        Response result = resource.save(new PropertyDefinition());
        assertEquals(200, result.getStatusInfo().getStatusCode());
        assertNull(result.getEntity());
    }

    @Test
    public void testGetTotalRecordsCount() {
        when(service.getTotalRecordsCount())
                .thenReturn(10L);
        Response result = resource.getTotalRecordsCount();
        assertEquals(200, result.getStatusInfo().getStatusCode());
        assertNotNull(result.getEntity());
    }
}
