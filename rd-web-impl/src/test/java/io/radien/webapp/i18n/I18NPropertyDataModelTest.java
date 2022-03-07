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

package io.radien.webapp.i18n;

import io.radien.api.entity.Page;
import io.radien.api.model.i18n.SystemI18NProperty;
import io.radien.api.service.i18n.I18NRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.ecm.client.entities.i18n.I18NProperty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

public class I18NPropertyDataModelTest {

    @Mock
    private I18NRESTServiceAccess i18NRESTServiceAccess;

    private I18NPropertyDataModel dataModel;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        dataModel = new I18NPropertyDataModel(i18NRESTServiceAccess);
    }

    @Test
    public void testLoad() throws SystemException {
        setupGetAll();
        List<SystemI18NProperty> resultList = dataModel.load(0, 10, new HashMap<>(), new HashMap<>());
        assertEquals(2, resultList.size());
    }

    @Test
    public void testLoadException() throws SystemException {
        when(i18NRESTServiceAccess.getAll(any(), anyInt(), anyInt(), any(), anyBoolean()))
                .thenThrow(new SystemException("error"));
        List<SystemI18NProperty> resultList = dataModel.load(0, 10, new HashMap<>(), new HashMap<>());
        assertTrue(resultList.isEmpty());
    }

    @Test
    public void testGetRowKey() {
        SystemI18NProperty property = new I18NProperty();
        property.setKey("key");
        property.setApplication("application");
        assertEquals("keyapplication", dataModel.getRowKey(property));
    }

    @Test
    public void testGetRowData() throws SystemException {
        setupGetAll();
        dataModel.load(0, 10, new HashMap<>(), new HashMap<>());
        SystemI18NProperty result = dataModel.getRowData("key0application");
        assertNotNull(result);
        assertEquals("key0", result.getKey());
        assertEquals("application", result.getApplication());
    }

    @Test
    public void testGetRowDataNotFound() throws SystemException {
        setupGetAll();
        dataModel.load(0, 10, new HashMap<>(), new HashMap<>());
        SystemI18NProperty result = dataModel.getRowData("key0application1234");
        assertNull(result);
    }

    private void setupGetAll() throws SystemException {
        List<SystemI18NProperty> propertyList = new ArrayList<>();
        for(int i = 0; i < 2; i++) {
            SystemI18NProperty property = new I18NProperty();
            property.setKey("key" + i);
            property.setApplication("application");
            propertyList.add(property);

        }
        Page<SystemI18NProperty> resultPage = new Page<>();
        resultPage.setTotalResults(2);
        resultPage.setCurrentPage(1);
        resultPage.setTotalPages(1);
        resultPage.setResults(propertyList);
        when(i18NRESTServiceAccess.getAll(any(), anyInt(), anyInt(), any(), anyBoolean()))
                .thenReturn(resultPage);
    }
}
