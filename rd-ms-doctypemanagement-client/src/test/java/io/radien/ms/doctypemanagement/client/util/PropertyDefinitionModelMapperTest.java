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

package io.radien.ms.doctypemanagement.client.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.radien.api.entity.Page;
import io.radien.api.model.docmanagement.propertydefinition.SystemPropertyDefinition;
import io.radien.ms.doctypemanagement.client.entities.PropertyDefinition;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.json.JsonArray;
import javax.json.JsonObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PropertyDefinitionModelMapperTest {
    @Test
    public void testMap() {
        PropertyDefinition object = new PropertyDefinition();
        object.setId(1L);
        object.setName("name");
        object.setMultiple(true);
        object.setMandatory(true);
        object.setProtected(true);
        object.setRequiredType(1);

        JsonObject result = PropertyDefinitionModelMapper.map(object);
        assertEquals(1L, result.getJsonNumber("id").longValue());
        assertEquals("name", result.getJsonString("name").getString());
        assertTrue(result.getBoolean("mandatory"));
        assertTrue(result.getBoolean("multiple"));
        assertTrue(result.getBoolean("protected"));
    }

    @Test
    public void testMapObjectList() {
        List<PropertyDefinition> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            PropertyDefinition object = new PropertyDefinition();
            object.setId((long) i);
            object.setName("name");
            object.setMultiple(true);
            object.setMandatory(true);
            object.setProtected(true);
            object.setRequiredType(1);
            list.add(object);
        }

        JsonArray result = PropertyDefinitionModelMapper.map(list);
        assertEquals(10, result.size());
    }

    @Test
    public void testMapStream() throws JsonProcessingException {
        PropertyDefinition object = new PropertyDefinition();
        object.setId(1L);
        object.setName("name");
        object.setMultiple(true);
        object.setMandatory(true);
        object.setProtected(true);
        object.setRequiredType(1);
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = new ByteArrayInputStream(mapper.writeValueAsBytes(object));

        PropertyDefinition result = PropertyDefinitionModelMapper.map(is);
        assertEquals(object.getId(), result.getId());
        assertEquals(object.getName(), result.getName());
        assertEquals(object.isProtected(), result.isProtected());
    }

    @Test
    public void testMapToPage() throws JsonProcessingException {
        Page<PropertyDefinition> page = new Page<>();
        page.setTotalPages(10);
        page.setTotalResults(10);
        page.setCurrentPage(1);
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = new ByteArrayInputStream(mapper.writeValueAsBytes(page));

        Page<PropertyDefinition> result = PropertyDefinitionModelMapper.mapToPage(is);
        assertEquals(page.getTotalPages(), result.getTotalPages());
        assertEquals(page.getTotalResults(), result.getTotalResults());
        assertEquals(page.getCurrentPage(), result.getCurrentPage());
    }

    @Test
    public void testMapStreamList() throws JsonProcessingException {
        List<PropertyDefinition> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            PropertyDefinition object = new PropertyDefinition();
            object.setId((long) i);
            object.setName("name");
            object.setMultiple(true);
            object.setMandatory(true);
            object.setProtected(true);
            object.setRequiredType(1);
            list.add(object);
        }
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = new ByteArrayInputStream(mapper.writeValueAsBytes(list));

        List<? extends SystemPropertyDefinition> result = PropertyDefinitionModelMapper.mapList(is);
        assertEquals(list.size(), result.size());
    }
}
