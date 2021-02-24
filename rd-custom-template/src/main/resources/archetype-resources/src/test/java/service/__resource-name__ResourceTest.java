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
package ${package}.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.Response;

import io.radien.api.service.${resource-name-variable}.${resource-name}ServiceAccess;
import ${package}.entities.${resource-name};
import io.radien.exception.SystemException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 *
 * @author Rajesh Gavvala
 * @author Nuno Santana
 * @author Bruno Gama
 */
public class ${resource-name}ResourceTest {

    @InjectMocks
    ${resource-name}Resource ${resource-name-variable}Resource;

    @Mock
    ${resource-name}ServiceAccess ${resource-name-variable}Service;

    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreate() {
        Response response = ${resource-name-variable}Resource.create(new ${resource-name}());
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }

    @Test
    public void testCreateInvalid() throws SystemException {
        Response response = ${resource-name-variable}Resource.create(new ${resource-name}());
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),response.getStatus());
    }

    @Test
    public void testCreateGenericError() throws SystemException {
        doThrow(new RuntimeException()).when(${resource-name-variable}Service).create(any());
        Response response = ${resource-name-variable}Resource.create(new ${resource-name}());
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),response.getStatus());
    }

    @Test
    public void testGetById404() {
        Response response = ${resource-name-variable}Resource.get(9L);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(),response.getStatus());
    }

    @Test
    public void testGetById() {
        when(${resource-name-variable}Service.get(1L)).thenReturn(new ${resource-name}());
        Response response = ${resource-name-variable}Resource.get(1L);
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }

    @Test
    public void testGetByIdGenericException() {
        when(${resource-name-variable}Service.get(1L)).thenThrow(new RuntimeException());
        Response response = ${resource-name-variable}Resource.get(1L);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),response.getStatus());
    }

    @Test
    public void testUpdate() {
        Response response = ${resource-name-variable}Resource.update(1l,new ${resource-name}());
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }


    @Test
    public void testUpdateInvalid() throws SystemException {
        Response response = ${resource-name-variable}Resource.update(1l,new ${resource-name}());
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),response.getStatus());
    }

    @Test
    public void testUpdateGenericError() throws SystemException {
        doThrow(new RuntimeException()).when(${resource-name-variable}Service).update(any());
        Response response = ${resource-name-variable}Resource.update(1l,new ${resource-name}());
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),response.getStatus());
    }

    @Test
    public void testDelete() {
        Response response = ${resource-name-variable}Resource.delete(1L);
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }

    @Test
    public void testDeleteGenericError() throws SystemException {
        doThrow(new RuntimeException()).when(${resource-name-variable}Service).delete(1L);
        Response response = ${resource-name-variable}Resource.delete(1L);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),response.getStatus());
    }
}