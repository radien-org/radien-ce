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

package io.radien.webapp.doctypemanagement.mixindefinition;

import io.radien.api.entity.Page;
import io.radien.api.model.docmanagement.mixindefinition.SystemMixinDefinition;
import io.radien.api.service.docmanagement.mixindefinition.MixinDefinitionRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.doctypemanagement.client.entities.MixinDefinitionDTO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;

public class MixinDefinitionDataModelTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private MixinDefinitionRESTServiceAccess mixinServiceAccess;

    private MixinDefinitionDataModel dataModel;

    @Before
    public void init() throws SystemException {
        dataModel = new MixinDefinitionDataModel(mixinServiceAccess);

        Page<SystemMixinDefinition<Long>> resultPage = new Page<>();
        resultPage.setCurrentPage(1);
        resultPage.setTotalResults(1);
        resultPage.setTotalPages(1);
        resultPage.setResults(setupGetAll());

        doReturn(resultPage).
                when(mixinServiceAccess).getAll(any(), anyInt(), anyInt(), any(), anyBoolean());
    }

    @Test
    public void testGetRowData() throws SystemException {
        dataModel.load(1, 10, new HashMap<>(), new HashMap<>());
        assertNotNull(dataModel.getRowData("1"));
    }

    @Test
    public void testGetRowDataNotFound() throws SystemException {
        dataModel.load(1, 10, new HashMap<>(), new HashMap<>());
        assertNull(dataModel.getRowData("3"));
    }

    @Test
    public void testGetRowKey() {
        SystemMixinDefinition<Long> object = new MixinDefinitionDTO();
        object.setId(1L);
        assertEquals("1", dataModel.getRowKey(object));
    }

    @Test
    public void testLoad() throws SystemException {
        List<SystemMixinDefinition<Long>> result = dataModel.load(1, 10, new HashMap<>(), new HashMap<>());
        assertEquals(1, result.size());
        assertEquals(1L, (long) result.get(0).getId());
    }


    private List<? extends SystemMixinDefinition<Long>> setupGetAll() {
        List<SystemMixinDefinition<Long>> resultList = new ArrayList<>();
        SystemMixinDefinition<Long> resultObj = new MixinDefinitionDTO();
        resultObj.setId(1L);
        resultList.add(resultObj);

        return resultList;
    }

}