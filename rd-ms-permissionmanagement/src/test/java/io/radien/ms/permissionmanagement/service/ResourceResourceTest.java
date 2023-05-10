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
package io.radien.ms.permissionmanagement.service;

import io.radien.api.service.permission.exception.ResourceNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.permissionmanagement.entities.ResourceEntity;
import io.radien.ms.permissionmanagement.resource.ResourceResource;
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
    ResourceBusinessService resourceBusinessService;
    
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
     * Get by ID with success should return a 200 code message
     * @throws ResourceNotFoundException in case of resource not found
     */
    @Test
    public void testGetById() throws ResourceNotFoundException {
        when(resourceBusinessService.get(1L)).thenReturn(new ResourceEntity());
        Response response = resourceResource.getById(1L);
        assertEquals(200,response.getStatus());
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
     * Deletion of the record with success, should return a 200 code message
     */
    @Test
    public void testDelete() {
        Response response = resourceResource.delete(1l);
        assertEquals(200,response.getStatus());
    }

    /**
     * Creation with success of a record. Should return a 200 code message
     */
    @Test
    public void testSave() {
        Response response = resourceResource.create(new io.radien.ms.permissionmanagement.client.entities.Resource());
        assertEquals(200,response.getStatus());
    }

    /**
     * Update with successful status. Returning 200 code message
     */
    @Test
    public void testUpdate() {
        Response response = resourceResource.update(1L, new io.radien.ms.permissionmanagement.client.entities.Resource());
        assertEquals(200,response.getStatus());
    }

}