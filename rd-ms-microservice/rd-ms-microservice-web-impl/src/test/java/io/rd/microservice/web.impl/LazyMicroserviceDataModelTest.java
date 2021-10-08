/*
 * Copyright (c) 2021-present radien GmbH. All rights reserved.
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
package io.rd.microservice.web.impl;

import io.rd.microservice.api.entity.Page;
import io.rd.microservice.api.model.SystemMicroservice;
import io.rd.microservice.api.service.MicroserviceRESTServiceAccess;
import io.rd.microservice.ms.client.entities.Microservice;
import io.rd.microservice.web.impl.LazyMicroserviceDataModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.powermock.modules.junit4.PowerMockRunner;

import java.net.MalformedURLException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 *
 * @author Rajesh Gavvala
 */
@RunWith(PowerMockRunner.class)
public class LazyMicroserviceDataModelTest {

    @InjectMocks
    private LazyMicroserviceDataModel lazyMicroserviceDataModel;
    @Mock
    private MicroserviceRESTServiceAccess service;

    private SystemMicroservice systemMicroservice;
    private List<SystemMicroservice> data;
    private List<? extends SystemMicroservice> datasource;

    @Before
    public void before() throws MalformedURLException {
        MockitoAnnotations.initMocks(this);
        new LazyMicroserviceDataModel(service);

        int currentPage = 1;
        int totalResults = 5;
        int totalPages = 1;
        int pageNo = 1;
        int pageSize = 10;
        int offSet = 0;

        systemMicroservice = new Microservice();
        initData(totalResults);

        datasource = new ArrayList<>();
        Page<? extends SystemMicroservice> page = new Page(data,currentPage,totalResults,totalPages);
        when(service.getAll(null,pageNo,pageSize,null,true)).then(i -> page);
        datasource = lazyMicroserviceDataModel.load(offSet, pageSize, null,  null);
    }

    @Test
    public void getRowData_test() {
        SystemMicroservice app = lazyMicroserviceDataModel.getRowData("1");
        assertEquals("Name-1", app.getName());
    }

    @Test
    public void getRowData_null_test() {
        SystemMicroservice app = lazyMicroserviceDataModel.getRowData("10");
        assertNull(app);
    }

    @Test
    public void getRowKey_test(){
        String id = lazyMicroserviceDataModel.getRowKey(data.get(1));
        assertEquals(id, "1");
    }

    @Test
    public void getErrorMsg_test(){
        String errorMsg = "error!";
        lazyMicroserviceDataModel.setErrorMsg(errorMsg);
        assertEquals(lazyMicroserviceDataModel.getErrorMsg(), errorMsg);
    }

    @Test(expected = NullPointerException.class)
    public void load_error_test() throws MalformedURLException {
        assertNull(lazyMicroserviceDataModel.load(0, -1, null,  null));
    }

    private void initData(int size) {
        data = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            SystemMicroservice microservice = new Microservice();
            microservice.setId(new Long(i));
            microservice.setName("Name-" + i);
            data.add(microservice);
        }
    }

}
