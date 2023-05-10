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

import io.radien.api.service.permission.exception.ActionNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.permissionmanagement.entities.ActionEntity;
import io.radien.ms.permissionmanagement.resource.ActionResource;
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
public class ActionResourceTest {

    @InjectMocks
    ActionResource actionResource;

    @Mock
    ActionBusinessService actionService;
    
    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test the Get All request which will return a success message code 200.
     */
    @Test
    public void testGetAll() {
        Response response = actionResource.getAll(null,1,10,null,true);
        assertEquals(200,response.getStatus());
    }

    /**
     * Get by ID with success should return a 200 code message
     * @throws ActionNotFoundException in case of action not found
     */
    @Test
    public void testGetById() throws ActionNotFoundException {
        when(actionService.get(1L)).thenReturn(new ActionEntity());
        Response response = actionResource.getById(1L);
        assertEquals(200,response.getStatus());
    }

    /**
     * Test Get Actions by should return success with a 200 code
     */
    @Test
    public void testGetActionsBy() {
        Response response = actionResource.getActions("action-name",null,true,true);
        assertEquals(200,response.getStatus());
    }

    /**
     * Deletion of the record with success, should return a 200 code message
     */
    @Test
    public void testDelete() {
        Response response = actionResource.delete(1l);
        assertEquals(200,response.getStatus());
    }

    /**
     * Creation with success of a record. Should return a 200 code message
     */
    @Test
    public void testCreate() {
        Response response = actionResource.create(new io.radien.ms.permissionmanagement.client.entities.Action());
        assertEquals(200,response.getStatus());
    }

    /**
     * Updating with success of a record. Should return a 200 code message
     */
    @Test
    public void testUpdate() {
        Response response = actionResource.update(1L, new io.radien.ms.permissionmanagement.client.entities.Action());
        assertEquals(200,response.getStatus());
    }
}