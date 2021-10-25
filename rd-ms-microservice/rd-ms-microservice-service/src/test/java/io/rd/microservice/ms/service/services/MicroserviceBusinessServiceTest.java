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
package io.rd.microservice.ms.service.services;

import io.rd.microservice.api.entity.Page;
import io.rd.microservice.api.model.SystemMicroservice;
import io.rd.microservice.api.service.MicroserviceServiceAccess;
import io.rd.microservice.exception.MicroserviceNotFoundException;
import io.rd.microservice.ms.client.entities.Microservice;
import io.rd.microservice.ms.client.exceptions.RemoteResourceException;
import io.rd.microservice.ms.client.services.MicroserviceFactory;

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

public class MicroserviceBusinessServiceTest {

    @InjectMocks
    MicroserviceBusinessService microserviceBusinessService;
    @Mock
    MicroserviceServiceAccess microserviceServiceAccess;

    SystemMicroservice systemMicroservice;

    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
        systemMicroservice = MicroserviceFactory.create("a");
    }

    @Test
    public void Save_test() throws MicroserviceNotFoundException {
        boolean success;
        try{
            microserviceBusinessService.save((Microservice) systemMicroservice);
            success = true;
        } catch (MicroserviceNotFoundException | RemoteResourceException e){
            success = false;
        }
        assertTrue(success);
    }

    @Test
    public void Save_exception_test() throws MicroserviceNotFoundException {
        doThrow(new MicroserviceNotFoundException("")).when(microserviceServiceAccess).save(systemMicroservice);
        boolean success = false;
        try{
            microserviceBusinessService.save((Microservice) systemMicroservice);
        } catch (MicroserviceNotFoundException | RemoteResourceException e){
            success = true;
        }
        assertTrue(success);
    }

    @Test
    public void get_test() throws MicroserviceNotFoundException {
        when(microserviceServiceAccess.get(2L)).thenReturn(systemMicroservice);
        SystemMicroservice result = microserviceBusinessService.get(2L);
        assertEquals(systemMicroservice,result);

    }

    @Test
    public void delete_test() throws MicroserviceNotFoundException {
        when(microserviceServiceAccess.get((Long) any())).thenReturn(systemMicroservice);
        boolean success = false;
        try{
            microserviceBusinessService.delete(1l);
        } catch (Exception e){
            success = true;
        }
        assertFalse(success);
    }

    @Test
    public void getAll_test() {
        String search = "";
        Page<SystemMicroservice> p = new Page<>(new ArrayList<>(),-1,0,0);
        when(microserviceServiceAccess.getAll(search,1,2,null,true)).thenReturn(p);
        Page<? extends SystemMicroservice> result = microserviceBusinessService.getAll(search,1,2,null,true);
        assertEquals(p,result);
    }
}
