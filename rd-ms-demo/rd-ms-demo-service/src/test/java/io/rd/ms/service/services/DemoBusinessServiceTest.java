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
package io.rd.ms.service.services;

import io.rd.api.entity.Page;
import io.rd.api.model.SystemDemo;
import io.rd.api.service.DemoServiceAccess;
import io.rd.exception.DemoNotFoundException;
import io.rd.ms.client.entities.Demo;
import io.rd.ms.client.exceptions.RemoteResourceException;
import io.rd.ms.client.services.DemoFactory;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class DemoBusinessServiceTest {

    @InjectMocks
    DemoBusinessService demoBusinessService;
    @Mock
    DemoServiceAccess demoServiceAccess;

    SystemDemo systemDemo;

    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
        systemDemo = DemoFactory.create("a");
    }

    @Test
    public void Save_test() throws DemoNotFoundException {
        boolean success;
        try{
            demoBusinessService.save((Demo) systemDemo);
            success = true;
        } catch (DemoNotFoundException | RemoteResourceException e){
            success = false;
        }
        assertTrue(success);
    }

    @Test
    public void Save_exception_test() throws DemoNotFoundException {
        doThrow(new DemoNotFoundException("")).when(demoServiceAccess).save(systemDemo);
        boolean success = false;
        try{
            demoBusinessService.save((Demo) systemDemo);
        } catch (DemoNotFoundException | RemoteResourceException e){
            success = true;
        }
        assertTrue(success);
    }

    @Test
    public void get_test() throws DemoNotFoundException {
        when(demoServiceAccess.get(2L)).thenReturn(systemDemo);
        SystemDemo result = demoBusinessService.get(2L);
        assertEquals(systemDemo,result);

    }

    @Test
    public void delete_test() throws DemoNotFoundException {
        when(demoServiceAccess.get((Long) any())).thenReturn(systemDemo);
        boolean success = false;
        try{
            demoBusinessService.delete(1l);
        } catch (Exception e){
            success = true;
        }
        assertFalse(success);
    }

    @Test
    public void getAll_test() {
        String search = "";
        Page<SystemDemo> p = new Page<>(new ArrayList<>(),-1,0,0);
        when(demoServiceAccess.getAll(search,1,2,null,true)).thenReturn(p);
        Page<? extends SystemDemo> result = demoBusinessService.getAll(search,1,2,null,true);
        assertEquals(p,result);
    }
}
