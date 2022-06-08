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

import io.radien.api.service.permission.exception.PermissionIllegalArgumentException;
import io.radien.api.service.permission.exception.PermissionNotFoundException;
import io.radien.ms.permissionmanagement.entities.PermissionEntity;
import io.radien.ms.permissionmanagement.resource.PermissionResource;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.Response;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import static org.junit.Assert.assertEquals;

/**
 * Permission Resource Test for future requests and responses validation
 * {@link PermissionResource}
 *
 * @author Nuno Santana
 * @author Bruno Gama
 */
public class PermissionResourceTest {

    @InjectMocks
    PermissionResource permissionResource;
    @Mock
    PermissionBusinessService permissionBusinessService;

    /**
     * Method for test preparation
     */
    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test the Get All request which will return a success message code 200.
     */
    @Test
    public void testGetAll() {
        Response response = permissionResource.getAll(null,1,10,null,true);
        assertEquals(200,response.getStatus());
    }

    /**
     * Test the Get total records count request which will return a success message code 200.
     */
    @Test
    public void testGetTotalRecordsCount() {
        Response response = permissionResource.getTotalRecordsCount();
        assertEquals(200,response.getStatus());
    }

    /**
     * Get by ID with success should return a 200 code message
     * @throws PermissionNotFoundException in case of permission not found
     */
    @Test
    public void testGetById() throws PermissionNotFoundException {
        when(permissionBusinessService.get(1L)).thenReturn(new PermissionEntity());
        Response response = permissionResource.getById(1L);
        assertEquals(200,response.getStatus());
    }

    /**
     * Test Get permissions by should return success with a 200 code
     */
    @Test
    public void testGetPermissionsBy() {
        Response response = permissionResource.getPermissions("permission", 11L, 100L,null,true,true);
        assertEquals(200,response.getStatus());
    }

    /**
     * Deletion of the record with success, should return a 200 code message
     */
    @Test
    public void testDelete() {
        Response response = permissionResource.delete(1L);
        assertEquals(200,response.getStatus());
    }

    /**
     * Creation with success of a record. Should return a 200 code message
     */
    @Test
    public void testSave() {
        Response response = permissionResource.create(new PermissionEntity());
        assertEquals(200,response.getStatus());
    }

    /**
     * Update with success of a record. Should return a 200 code message
     */
    @Test
    public void testUpdate() {
        Response response = permissionResource.update(1L, new PermissionEntity());
        assertEquals(200,response.getStatus());
    }

    /**
     * Test the  endpoint ("exists") using just Id parameter
     */
    @Test
    public void testExistsUsingPermissionId() {
        when(permissionBusinessService.exists(anyLong(), nullable(String.class))).thenReturn(true);
        Response response = permissionResource.exists(2L, null);
        assertEquals(204,response.getStatus());
    }

    /**
     * Test the  endpoint ("exists") using just Name parameter
     */
    @Test
    public void testExistsUsingPermissionName() {
        when(permissionBusinessService.exists(nullable(Long.class), anyString())).thenReturn(true);
        Response response = permissionResource.exists(null, "add-user");
        assertEquals(204,response.getStatus());
    }

    /**
     * Test the  endpoint ("exists") using id and permission name as parameter
     * (All possible parameters)
     */
    @Test
    public void testExistsUsingPermissionIdAndName()  {
        // case 1: Returning true
        when(permissionBusinessService.exists(anyLong(), anyString())).thenReturn(true);
        Response response = permissionResource.exists(2L, "add-user");
        assertEquals(204, response.getStatus());

        // case 2: Returning false
        when(permissionBusinessService.exists(anyLong(), anyString())).thenReturn(false);
        response = permissionResource.exists(2L, "add-user");
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    /**
     * Test the endpoint {@link PermissionResource#getIdByResourceAndAction(String, String)}
     * for a successful situation where the Permission Id can be retrieved by an Action and Resource (Names)
     * @throws PermissionIllegalArgumentException thrown when mandatory parameters are not informed
     */
    @Test
    public void testGetIdSuccessCase() throws PermissionIllegalArgumentException {
        String action = "Create", resource = "User";
        Long id = 111L;
        when(permissionBusinessService.getIdByActionAndResource(resource, action)).
                thenReturn(id);
        Response response = permissionResource.getIdByResourceAndAction(resource, action);
        assertEquals(Response.Status.OK, response.getStatusInfo().toEnum());
    }
}