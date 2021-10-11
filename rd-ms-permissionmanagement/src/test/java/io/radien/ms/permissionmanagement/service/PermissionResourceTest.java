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

import io.radien.api.service.permission.PermissionServiceAccess;
import io.radien.exception.PermissionIllegalArgumentException;
import io.radien.exception.PermissionNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.permissionmanagement.model.PermissionEntity;
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
 * {@link io.radien.ms.permissionmanagement.service.PermissionResource}
 *
 * @author Nuno Santana
 * @author Bruno Gama
 */
public class PermissionResourceTest {

    @InjectMocks
    PermissionResource permissionResource;
    @Mock
    PermissionServiceAccess permissionServiceAccess;

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
     * Test the Get All request Exception which will return a generic error message code 500.
     */
    @Test
    public void testGetAllGenericException() {
        when(permissionResource.getAll(null,1,10,null,true))
                .thenThrow(new RuntimeException());
        Response response = permissionResource.getAll(null,1,10,null,true);
        assertEquals(500,response.getStatus());
    }

    /**
     * Test that will test the error message 404 permission Not Found
     */
    @Test
    public void testGetById404() {
        Response response = permissionResource.getById(1L);
        assertEquals(404,response.getStatus());
    }

    /**
     * Get by ID with success should return a 200 code message
     * @throws PermissionNotFoundException in case of permission not found
     */
    @Test
    public void testGetById() throws PermissionNotFoundException {
        when(permissionServiceAccess.get(1L)).thenReturn(new PermissionEntity());
        Response response = permissionResource.getById(1L);
        assertEquals(200,response.getStatus());
    }

    /**
     * Test Get by ID exception which will return a 500 error code message
     * @throws PermissionNotFoundException in case of permission not found
     */
    @Test
    public void testGetByIdGenericException() throws PermissionNotFoundException {
        when(permissionServiceAccess.get(1L)).thenThrow(new RuntimeException());
        Response response = permissionResource.getById(1L);
        assertEquals(500,response.getStatus());
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
     * Test Get permissions by should return error with a 500 error code message
     */
    @Test
    public void testGetPermissionsByException() {
        doThrow(new RuntimeException()).when(permissionServiceAccess).getPermissions(any());
        Response response = permissionResource.getPermissions("perm", 1L, 2L,null,true,true);
        assertEquals(500,response.getStatus());
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
     * Deletion of the record with error, should return a generic 500 error code message
     */
    @Test
    public void testDeleteGenericError() {
        doThrow(new RuntimeException()).when(permissionServiceAccess).delete(1L);
        Response response = permissionResource.delete(1L);
        assertEquals(500,response.getStatus());
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
     * Creation with error of a record. Should return a 400 code message Invalid Requested Exception
     * @throws UniquenessConstraintException in case of request could not be performed by any specific and justified in the
     * message reason
     */
    @Test
    public void testCreateInvalid() throws UniquenessConstraintException {
        doThrow(new UniquenessConstraintException()).when(permissionServiceAccess).create(any());
        Response response = permissionResource.create(new PermissionEntity());
        assertEquals(400,response.getStatus());
    }

    /**
     * Creation of a record with error. Should return a generic error message 500
     * @throws UniquenessConstraintException in case of request could not be performed by any specific and justified in the
     * message reason
     */
    @Test
    public void testCreateGenericError() throws UniquenessConstraintException {
        doThrow(new RuntimeException()).when(permissionServiceAccess).create(any());
        Response response = permissionResource.create(new PermissionEntity());
        assertEquals(500,response.getStatus());
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
     * Simulates a scenario in which is tried to update a permission containing repeated information.
     * @throws UniquenessConstraintException in case of repeated information (name or combination of action and resource)
     * @throws PermissionNotFoundException in case of Permission not found for a given id
     */
    @Test
    public void testUpdateInvalid() throws UniquenessConstraintException, PermissionNotFoundException {
        doThrow(new UniquenessConstraintException()).when(permissionServiceAccess).update(any());
        Response response = permissionResource.update(1L, new PermissionEntity());
        assertEquals(400,response.getStatus());
    }

    /**
     * Simulates a scenario in which is tried to update a permission that does not exists (taking in consideration
     * the informed id)
     * @throws UniquenessConstraintException in case of repeated information (name or combination of action and resource)
     * @throws PermissionNotFoundException in case of Permission not found for a given id
     */
    @Test
    public void testUpdateWhenPermissionNotFound() throws UniquenessConstraintException, PermissionNotFoundException {
        doThrow(new PermissionNotFoundException("not found")).when(permissionServiceAccess).update(any());
        Response response = permissionResource.update(1L, new PermissionEntity());
        assertEquals(404,response.getStatus());
    }

    /**
     * Update of a record with error. Should return a generic error message 500
     * @throws UniquenessConstraintException in case of repeated information (name or combination of action and resource)
     * @throws PermissionNotFoundException in case of Permission not found for a given id
     */
    @Test
    public void testUpdateGenericError() throws UniquenessConstraintException, PermissionNotFoundException {
        doThrow(new RuntimeException()).when(permissionServiceAccess).update(any());
        Response response = permissionResource.update(1L, new PermissionEntity());
        assertEquals(500,response.getStatus());
    }

    /**
     * Test the  endpoint ("exists") using just Id parameter
     */
    @Test
    public void testExistsUsingPermissionId() {
        when(permissionServiceAccess.exists(anyLong(), nullable(String.class))).thenReturn(true);
        Response response = permissionResource.exists(2L, null);
        assertEquals(204,response.getStatus());
    }

    /**
     * Test the  endpoint ("exists") using just Name parameter
     */
    @Test
    public void testExistsUsingPermissionName() {
        when(permissionServiceAccess.exists(nullable(Long.class), anyString())).thenReturn(true);
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
        when(permissionServiceAccess.exists(anyLong(), anyString())).thenReturn(true);
        Response response = permissionResource.exists(2L, "add-user");
        assertEquals(204, response.getStatus());

        // case 2: Returning false
        when(permissionServiceAccess.exists(anyLong(), anyString())).thenReturn(false);
        response = permissionResource.exists(2L, "add-user");
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    /**
     * Test the  endpoint ("exists") omitting all required parameters
     */
    @Test
    public void testExistsWithNoParameters() {
        Response response = permissionResource.exists(null, null);
        assertEquals(400,response.getStatus());
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
        when(permissionServiceAccess.getIdByActionAndResource(resource, action)).
                thenReturn(Optional.of(id));
        Response response = permissionResource.getIdByResourceAndAction(resource, action);
        assertEquals(Response.Status.OK, response.getStatusInfo().toEnum());
    }

    /**
     * Test the endpoint {@link PermissionResource#getIdByResourceAndAction(String, String)}
     * for a unsuccessful situation where the Permission Id cannot be retrieved by an Action and Resource (Names)
     * @throws PermissionIllegalArgumentException thrown when mandatory parameters are not informed
     */
    @Test
    public void testGetIdUnSuccessCase() throws PermissionIllegalArgumentException {
        String action = "Create", resource = "User";
        when(permissionServiceAccess.getIdByActionAndResource(resource, action)).
                thenReturn(Optional.empty());
        Response response = permissionResource.getIdByResourceAndAction(resource, action);
        assertEquals(Response.Status.NOT_FOUND, response.getStatusInfo().toEnum());
    }

    /**
     * Test the endpoint {@link PermissionResource#getIdByResourceAndAction(String, String)}
     * for a unsuccessful situation where Action or Resource were not informed
     * @throws PermissionIllegalArgumentException thrown when mandatory parameters are not informed
     */
    @Test
    public void testGetIdInsufficientParametersCase() throws PermissionIllegalArgumentException {
        String action = "", resource = "User";
        when(permissionServiceAccess.getIdByActionAndResource(resource, action)).
                thenThrow(new PermissionIllegalArgumentException("invalid"));
        Response response = permissionResource.getIdByResourceAndAction(resource, action);
        assertEquals(Response.Status.BAD_REQUEST, response.getStatusInfo().toEnum());
    }


    /**
     * Test the endpoint {@link PermissionResource#getIdByResourceAndAction(String, String)}
     * for a unsuccessful situation where an internal server error is triggered by an exception
     * @throws PermissionIllegalArgumentException thrown when mandatory parameters are not informed
     */
    @Test
    public void testGetIdWhenInternalServerErrorCase() throws PermissionIllegalArgumentException {
        String action = "Create", resource = "User";
        when(permissionServiceAccess.getIdByActionAndResource(resource, action)).
                thenThrow(new RuntimeException("error"));
        Response response = permissionResource.getIdByResourceAndAction(resource, action);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR, response.getStatusInfo().toEnum());
    }


}