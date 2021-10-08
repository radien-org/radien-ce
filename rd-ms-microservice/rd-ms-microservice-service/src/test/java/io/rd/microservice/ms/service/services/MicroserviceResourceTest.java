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
package io.rd.microservice.ms.service.services;

import io.rd.microservice.exception.MicroserviceNotFoundException;
import io.rd.microservice.ms.client.entities.Microservice;
import io.rd.microservice.ms.client.exceptions.RemoteResourceException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MicroserviceResourceTest {

    @InjectMocks
    MicroserviceResource microserviceResource;

    @Mock
    MicroserviceBusinessService microserviceBusinessService;


    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getAll_test() {
        Response response = microserviceResource.getAll(null,1,10,null,true);
        assertEquals(200,response.getStatus());
    }

    @Test
    public void getAll_exception_test() {
        when(microserviceBusinessService.getAll(null,1,10,null,true))
                .thenThrow(new RuntimeException());
        Response response = microserviceResource.getAll(null,1,10,null,true);
        assertEquals(500,response.getStatus());
    }

    @Test
    public void getById_test() throws MicroserviceNotFoundException {
        when(microserviceBusinessService.get(1L)).thenReturn(new Microservice());
        Response response = microserviceResource.getById(1L);
        assertEquals(200,response.getStatus());
    }

    @Test
    public void getById_notFoundException_test() throws MicroserviceNotFoundException {
        when(microserviceBusinessService.get(1L)).thenThrow(new MicroserviceNotFoundException("1"));
        Response response = microserviceResource.getById(1L);
        assertEquals(404,response.getStatus());
    }

    @Test
    public void getById_exception_test() throws MicroserviceNotFoundException {
        when(microserviceBusinessService.get(1L)).thenThrow(new RuntimeException());
        Response response = microserviceResource.getById(1L);
        assertEquals(500,response.getStatus());
    }

    @Test
    public void delete_test() throws MicroserviceNotFoundException {
        when(microserviceBusinessService.get(1L)).thenReturn(new Microservice());
        Response response = microserviceResource.delete(1L);
        assertEquals(200,response.getStatus());
    }

    @Test
    public void delete_notFoundException_test() throws MicroserviceNotFoundException, RemoteResourceException {
        doThrow(new MicroserviceNotFoundException("test-error")).when(microserviceBusinessService).delete(1l);
        Response response = microserviceResource.delete(1L);
        assertEquals(404,response.getStatus());
    }

    @Test
    public void delete_exception_test() throws MicroserviceNotFoundException, RemoteResourceException {
        doThrow(new RemoteResourceException()).when(microserviceBusinessService).delete(1l);
        Response response = microserviceResource.delete(1L);
        assertEquals(500,response.getStatus());
    }

    @Test
    public void save_test() {
        Response response = microserviceResource.save(new Microservice());
        assertEquals(200,response.getStatus());
    }

    @Test
    public void save_exception_test() throws MicroserviceNotFoundException, RemoteResourceException {
        doThrow(new RemoteResourceException()).when(microserviceBusinessService).save(any());
        Response response = microserviceResource.save(new Microservice());
        assertEquals(500,response.getStatus());

    }


}
