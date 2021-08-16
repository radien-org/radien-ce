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
package io.rd.web.impl;

import io.rd.api.entity.Page;
import io.rd.api.model.SystemDemo;
import io.rd.api.service.DemoRESTServiceAccess;
import io.rd.exception.SystemException;
import io.rd.ms.client.entities.Demo;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

/**
 *
 * @author Rajesh Gavvala
 */
@RunWith(PowerMockRunner.class)
public class LazyDemoDataModelTest {

    @InjectMocks
    private LazyDemoDataModel lazyDemoDataModel;
    @Mock
    private DemoRESTServiceAccess service;

    private SystemDemo systemDemo;
    private List<SystemDemo> data;
    private List<? extends SystemDemo> datasource;

    @Before
    public void before() throws SystemException {
        MockitoAnnotations.initMocks(this);
        new LazyDemoDataModel(service);

        int currentPage = 1;
        int totalResults = 5;
        int totalPages = 1;
        int pageNo = 1;
        int pageSize = 10;
        int offSet = 0;

        systemDemo = new Demo();
        initData(totalResults);

        datasource = new ArrayList<>();
        Page<? extends SystemDemo> page = new Page(data,currentPage,totalResults,totalPages);
        when(service.getAll(null,pageNo,pageSize,null,true)).then(i -> page);
        datasource = lazyDemoDataModel.load(offSet, pageSize, null,  null);
    }

    @Test
    public void getRowData_test() {
        SystemDemo app = lazyDemoDataModel.getRowData("1");
        assertEquals("Name-1", app.getName());
    }

    @Test
    public void getRowData_null_test() {
        SystemDemo app = lazyDemoDataModel.getRowData("10");
        assertNull(app);
    }

    @Test
    public void getRowKey_test(){
        String id = lazyDemoDataModel.getRowKey(data.get(1));
        assertEquals("1", id);
    }

    @Test
    public void getErrorMsg_test(){
        String errorMsg = "error!";
        lazyDemoDataModel.setErrorMsg(errorMsg);
        assertEquals(lazyDemoDataModel.getErrorMsg(), errorMsg);
    }

    @Test(expected = Exception.class)
    public void load_error_test() {
        lazyDemoDataModel.load(0, -1, null,  null);
    }

    private void initData(int size) {
        data = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            SystemDemo demo = new Demo();
            demo.setId(new Long(i));
            demo.setName("Name-" + i);
            data.add(demo);
        }
    }

}
