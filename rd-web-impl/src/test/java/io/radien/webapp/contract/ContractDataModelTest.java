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
package io.radien.webapp.contract;


import io.radien.api.model.tenant.SystemContract;
import io.radien.api.service.tenant.ContractRESTServiceAccess;


import io.radien.ms.tenantmanagement.client.entities.Contract;

import io.radien.webapp.JSFUtilAndFaceContextMessagesTest;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;

import static org.junit.Assert.assertEquals;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Class that aggregates UnitTest cases
 * for ContractDataModel
 *
 * @author Rajesh Gavvala
 */
public class ContractDataModelTest extends JSFUtilAndFaceContextMessagesTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @InjectMocks
    private ContractDataModel contractDataModel;

    @Mock
    private ContractRESTServiceAccess service;

    @Mock
    private LazyDataModel<? extends SystemContract> lazyModel;

    private SystemContract systemContract;

    @BeforeClass
    public static void init() {
        handleJSFUtilAndFaceContextMessages();
    }

    @AfterClass
    public static void end() {
        destroy();
    }

    /**
     * Constructs mock object
     */
    @Before
    public void before(){
        systemContract = new Contract();
        systemContract.setId(1L);
        systemContract.setName("myContract1");
    }

    @Test
    public void testInit(){
        contractDataModel.init();
        contractDataModel.setService(service);
        assertEquals(service, contractDataModel.getService());

        contractDataModel.onload();

        contractDataModel.setLazyModel(lazyModel);
        assertEquals(lazyModel, contractDataModel.getLazyModel());

        SelectEvent<SystemContract> event = mock(SelectEvent.class);
        when(event.getObject()).thenReturn(systemContract);
        contractDataModel.onRowSelect(event);
    }

    @Test
    public void testGetActionAndSetAction(){
        contractDataModel.setSelectedContract(systemContract);
        assertEquals(systemContract, contractDataModel.getSelectedContract());
    }



}