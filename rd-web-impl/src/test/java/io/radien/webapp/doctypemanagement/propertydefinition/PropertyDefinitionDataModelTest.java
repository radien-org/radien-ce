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

package io.radien.webapp.doctypemanagement.propertydefinition;

import io.radien.api.entity.Page;
import io.radien.api.model.docmanagement.propertydefinition.SystemPropertyDefinition;
import io.radien.api.service.docmanagement.propertydefinition.PropertyDefinitionRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.doctypemanagement.client.entities.PropertyDefinition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;

public class PropertyDefinitionDataModelTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private PropertyDefinitionRESTServiceAccess serviceAccess;

    @Test
    public void testConstructor() {
        PropertyDefinitionDataModel model = new PropertyDefinitionDataModel(serviceAccess);
        assertNotNull(model);
    }

    @Test
    public void testLoad() throws SystemException {
        Page<SystemPropertyDefinition> resultPage = new Page<>();
        resultPage.setTotalResults(2);
        resultPage.setResults(setupGetAll());
        doReturn(resultPage).when(serviceAccess)
                .getAll(any(), anyInt(), anyInt(), any(), anyBoolean());

        PropertyDefinitionDataModel model = new PropertyDefinitionDataModel(serviceAccess);
        List<SystemPropertyDefinition> result = model.load(0, 10, new HashMap<>(), new HashMap<>());
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
    }

    @Test
    public void testGetRowKey() {
        SystemPropertyDefinition def1 = new PropertyDefinition();
        def1.setId(1L);
        def1.setName("name1");
        PropertyDefinitionDataModel model = new PropertyDefinitionDataModel(serviceAccess);
        assertEquals(String.valueOf(def1.getId()), model.getRowKey(def1));
    }

    @Test
    public void testGetRowData() throws SystemException {
        Page<SystemPropertyDefinition> resultPage = new Page<>();
        resultPage.setTotalResults(2);
        resultPage.setResults(setupGetAll());
        doReturn(resultPage).when(serviceAccess)
                .getAll(any(), anyInt(), anyInt(), any(), anyBoolean());

        PropertyDefinitionDataModel model = new PropertyDefinitionDataModel(serviceAccess);
        model.load(0, 10, new HashMap<>(), new HashMap<>());
        assertEquals(resultPage.getResults().get(0), model.getRowData("1"));
    }

    @Test
    public void testGetRowDataNotFound() {
        PropertyDefinitionDataModel model = new PropertyDefinitionDataModel(serviceAccess);
        assertNull(model.getRowData("1"));
    }

    private List<? extends SystemPropertyDefinition> setupGetAll() {
        List<SystemPropertyDefinition> results = new ArrayList<>();
        SystemPropertyDefinition def1 = new PropertyDefinition();
        def1.setId(1L);
        def1.setName("name1");
        SystemPropertyDefinition def2 = new PropertyDefinition();
        def2.setId(2L);
        def2.setName("name2");
        results.add(def1);
        results.add(def2);
        return results;
    }


}
