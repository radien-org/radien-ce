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
package ${package}.web.impl;

import ${package}.api.entity.Page;
import ${package}.api.model.System${entityResourceName};
import ${package}.api.service.${entityResourceName}RESTServiceAccess;
import ${package}.ms.client.entities.${entityResourceName};
import ${package}.web.impl.Lazy${entityResourceName}DataModel;

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
public class Lazy${entityResourceName}DataModelTest {

    @InjectMocks
    private Lazy${entityResourceName}DataModel lazy${entityResourceName}DataModel;
    @Mock
    private ${entityResourceName}RESTServiceAccess service;

    private System${entityResourceName} system${entityResourceName};
    private List<System${entityResourceName}> data;
    private List<? extends System${entityResourceName}> datasource;

    @Before
    public void before() throws MalformedURLException {
        MockitoAnnotations.initMocks(this);
        new Lazy${entityResourceName}DataModel(service);

        int currentPage = 1;
        int totalResults = 5;
        int totalPages = 1;
        int pageNo = 1;
        int pageSize = 10;
        int offSet = 0;

        system${entityResourceName} = new ${entityResourceName}();
        initData(totalResults);

        datasource = new ArrayList<>();
        Page<? extends System${entityResourceName}> page = new Page(data,currentPage,totalResults,totalPages);
        when(service.getAll(null,pageNo,pageSize,null,true)).then(i -> page);
        datasource = lazy${entityResourceName}DataModel.load(offSet, pageSize, null,  null);
    }

    @Test
    public void getRowData_test() {
        System${entityResourceName} app = lazy${entityResourceName}DataModel.getRowData("1");
        assertEquals("Name-1", app.getName());
    }

    @Test
    public void getRowData_null_test() {
        System${entityResourceName} app = lazy${entityResourceName}DataModel.getRowData("10");
        assertNull(app);
    }

    @Test
    public void getRowKey_test(){
        String id = lazy${entityResourceName}DataModel.getRowKey(data.get(1));
        assertEquals(id, "1");
    }

    @Test
    public void getErrorMsg_test(){
        String errorMsg = "error!";
        lazy${entityResourceName}DataModel.setErrorMsg(errorMsg);
        assertEquals(lazy${entityResourceName}DataModel.getErrorMsg(), errorMsg);
    }

    @Test(expected = NullPointerException.class)
    public void load_error_test() throws MalformedURLException {
        assertNull(lazy${entityResourceName}DataModel.load(0, -1, null,  null));
    }

    private void initData(int size) {
        data = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            System${entityResourceName} ${entityResourceName.toLowerCase()} = new ${entityResourceName}();
            ${entityResourceName.toLowerCase()}.setId(new Long(i));
            ${entityResourceName.toLowerCase()}.setName("Name-" + i);
            data.add(${entityResourceName.toLowerCase()});
        }
    }

}
