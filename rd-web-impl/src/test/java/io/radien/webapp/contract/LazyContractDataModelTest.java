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
package io.radien.webapp.contract;

import io.radien.exception.SystemException;

import io.radien.api.entity.Page;
import io.radien.api.model.permission.SystemAction;
import io.radien.api.model.tenant.SystemContract;
import io.radien.api.service.tenant.ContractRESTServiceAccess;

import io.radien.webapp.action.LazyActionsDataModel;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;
/**
 * Class that aggregates UnitTest cases
 * for LazyContractDataModel
 *
 * @author Rajesh Gavvala
 */
public class LazyContractDataModelTest {
    @InjectMocks
    private LazyContractDataModel lazyContractDataModel;

    @Mock
    private ContractRESTServiceAccess service;

    Map<String, SortMeta> sortBy;
    Map<String, FilterMeta> filterBy;
    List<? extends SystemContract> systemActions;

    /**
     * Constructs mock object
     */
    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
        new LazyContractDataModel(service);
    }

    /**
     * Test method getData()
     * Asserts receives an object
     * @throws SystemException if any error
     */
    @Test
    public void testGetData() throws SystemException {
        doReturn(new Page<SystemContract>(systemActions, 1, 1, 1))
                .when(service).getAll(1, 1);

        assertNotNull(lazyContractDataModel.getData(0, 1, sortBy, filterBy));
    }

    /**
     * Test method getData() expects an
     * ArithmeticException
     * @throws SystemException if any error
     */
    @Test(expected = ArithmeticException.class)
    public void testGetDataException() throws SystemException {
        lazyContractDataModel.getData(1, 0, null, null);
    }

}