/*
 * Copyright (c) 2021-present radien GmbH & its legal owners. All rights reserved.
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
package io.radien.ms.permissionmanagement.service;

import io.radien.api.service.permission.ResourceServiceAccess;
import io.radien.exception.ResourceNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.permissionmanagement.model.ResourceEntity;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.Response;
import java.net.MalformedURLException;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * @author Nuno Santana
 * @author Bruno Gama
 */
public class ResourceResourceTest {

    @InjectMocks
    ResourceResource resourceResource;

    @Mock
    ResourceServiceAccess resourceServiceAccess;
    
    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test the Get All request which will return a success message code 200.
     */
    @Test
    public void testGetAll() {
        Response response = resourceResource.getAll(null,1,10,null,true);
        assertEquals(200,response.getStatus());
    }

    /**
     * Test the Get All request Exception which will return a generic error message code 500.
     */
    @Test
    public void testGetAllGenericException() throws MalformedURLException {
        when(resourceResource.getAll(null,1,10,null,true))
                .thenThrow(new RuntimeException());
        Response response = resourceResource.getAll(null,1,10,null,true);
        assertEquals(500,response.getStatus());
    }

    /**
     * Test that will test the error message 404 permission Not Found
     */
    @Test
    public void testGetById404() {
        Response response = resourceResource.getById(1L);
        assertEquals(404,response.getStatus());
    }

    /**
     * Get by ID with success should return a 200 code message
     * @throws ResourceNotFoundException in case of resource not found
     */
    @Test
    public void testGetById() throws ResourceNotFoundException {
        when(resourceServiceAccess.get(1L)).thenReturn(new ResourceEntity());
        Response response = resourceResource.getById(1L);
        assertEquals(200,response.getStatus());
    }

    /**
     * Test Get by ID exception which will return a 500 error code message
     * @throws ResourceNotFoundException in case of resource not found
     */
    @Test
    public void testGetByIdGenericException() throws ResourceNotFoundException {
        when(resourceServiceAccess.get(1L)).thenThrow(new RuntimeException());
        Response response = resourceResource.getById(1L);
        assertEquals(500,response.getStatus());
    }

    /**
     * Test Get Resources by should return success with a 200 code
     */
    @Test
    public void testGetResourcesBy() {
        Response response = resourceResource.getResources("resource-name",null,true,true);
        assertEquals(200,response.getStatus());
    }

    /**
     * Test Get Resources by should return error with a 500 error code message
     */
    @Test
    public void testGetPermissionsByException() {
        doThrow(new RuntimeException()).when(resourceServiceAccess).getResources(any());
        Response response = resourceResource.getResources("resource-name",null,true,true);
        assertEquals(500,response.getStatus());
    }

    /**
     * Deletion of the record with success, should return a 200 code message
     */
    @Test
    public void testDelete() {
        Response response = resourceResource.delete(1l);
        assertEquals(200,response.getStatus());
    }

    /**
     * Deletion of the record with error, should return a generic 500 error code message
     */
    @Test
    public void testDeleteGenericError() {
        doThrow(new RuntimeException()).when(resourceServiceAccess).delete(1l);
        Response response = resourceResource.delete(1l);
        assertEquals(500,response.getStatus());
    }

    /**
     * Creation with success of a record. Should return a 200 code message
     */
    @Test
    public void testSave() {
        Response response = resourceResource.save(new io.radien.ms.permissionmanagement.client.entities.Resource());
        assertEquals(200,response.getStatus());
    }

    /**
     * Creation with error of a record. Should return a 400 code message Invalid Requested Exception
     * @throws UniquenessConstraintException in case of request could not be performed by any specific and justified in the
     * message reason
     */
    @Test
    public void testCreateInvalid() throws UniquenessConstraintException {
        doThrow(new UniquenessConstraintException()).when(resourceServiceAccess).save(any());
        Response response = resourceResource.save(new io.radien.ms.permissionmanagement.client.entities.Resource());
        assertEquals(400,response.getStatus());
    }

    /**
     * Creation of a record with error. Should return a generic error message 500
     * @throws UniquenessConstraintException in case of request could not be performed by any specific and justified in the
     * message reason
     */
    @Test
    public void testCreateGenericError() throws UniquenessConstraintException {
        doThrow(new RuntimeException()).when(resourceServiceAccess).save(any());
        Response response = resourceResource.save(new io.radien.ms.permissionmanagement.client.entities.Resource());
        assertEquals(500,response.getStatus());
    }


}