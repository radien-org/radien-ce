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
package io.radien.ms.rolemanagement.services;

import io.radien.exception.*;
import io.radien.ms.rolemanagement.client.entities.Role;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * @author Bruno Gama
 */
public class RoleResourceTest {

    @InjectMocks
    RoleResource roleResource;
    @Mock
    RoleService roleService;
    @Mock
    RoleBusinessService roleBusinessService;


    @BeforeEach
    public void before(){
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test response of the get all method
     */
    @Test
    public void testGetAll() {
        Response response = roleResource.getAll(null,1,10,null,false);
        assertEquals(200,response.getStatus());
    }

    /**
     * Tests exception from the get all
     */
    @Test
    public void testGetAllGenericException() {
        when(roleResource.getAll(null,1,10, null, false))
                .thenThrow(new RuntimeException());
        Response response = roleResource.getAll(null,1,10, null, false);
        assertEquals(500,response.getStatus());
    }

    /**
     * Tests response of the get specific role
     */
    @Test
    public void testGetSpecificAssociation() {
        Response response = roleResource.getSpecificRoles("name","description",null,false,true);
        assertEquals(200,response.getStatus());
    }

    /**
     * Tests exception from the get specific role
     */
    @Test
    public void testGetSpecificRolesException() {
        when(roleBusinessService.getSpecificRoles(any())).thenThrow(new RuntimeException());
        Response response = roleResource.getSpecificRoles("name","description",null,false,true);
        assertEquals(500,response.getStatus());
    }

    /**
     * Tests return message from the get by id
     * @throws RoleNotFoundException in case record not found
     */
    @Test
    public void testGetById() throws RoleNotFoundException {
        when(roleService.get(1L)).thenReturn(new Role());
        Response response = roleResource.getById(1L);
        assertEquals(200,response.getStatus());
    }

    /**
     * Tests return message from the get by id
     * @throws RoleNotFoundException in case record not found
     */
    @Test
    public void testGetByIdException() throws RoleNotFoundException {
        when(roleBusinessService.getById(any())).thenThrow(new RuntimeException());
        Response response = roleResource.getById(1L);
        assertEquals(500,response.getStatus());
    }

    /**
     * Tests exception from the get by id
     * @throws RoleNotFoundException in case record not found
     */
    @Test
    public void testGetByIdNotFoundError() throws RoleNotFoundException{
        when(roleBusinessService.getById(any())).thenThrow(new RoleNotFoundException("Not Found"));
        Response response = roleResource.getById(1L);
        assertEquals(404,response.getStatus());
    }

    /**
     * Tests delete return message
     */
    @Test
    public void testDelete() {
        Response response = roleResource.delete(1L);
        assertEquals(200,response.getStatus());
    }

    /**
     * Tests Runtime exception from the delete method
     * @throws RoleNotFoundException in case object was not found
     */
    @Test
    public void testDeleteGenericError() throws RoleNotFoundException {
        when(roleBusinessService.getById(any())).thenThrow(new RuntimeException());
        Response response = roleResource.delete(1L);
        assertEquals(500,response.getStatus());
    }

    /**
     * Tests the delete not found exception record
     * @throws RoleNotFoundException in case record not found
     */
    @Test
    public void testDeleteNotFoundError() throws RoleNotFoundException {
        when(roleBusinessService.getById(any())).thenThrow(new RoleNotFoundException("Not found"));
        Response response = roleResource.delete(1L);
        assertEquals(404,response.getStatus());
    }

    /**
     * Tests save return message
     */
    @Test
    public void testSave() {
        Response response = roleResource.create(new Role());
        assertEquals(200,response.getStatus());
    }

    /**
     * Tests Runtime exception from the save method
     * @throws UniquenessConstraintException in case of repeated information
     */
    @Test
    public void testSaveGenericError() throws UniquenessConstraintException {
        doThrow(new RuntimeException()).when(roleBusinessService).create(any());
        Response response = roleResource.create(new Role());
        assertEquals(500,response.getStatus());
    }

    /**
     * Tests the save not found exception record
     * @throws RoleNotFoundException in case record not found
     * @throws UniquenessConstraintException in case of repeated information
     */
    @Test
    public void testUpdateNotFoundError() throws RoleNotFoundException, UniquenessConstraintException {
        doThrow(new RoleNotFoundException("Not found")).when(roleBusinessService).update(any());
        Response response = roleResource.update(1L, new Role());
        assertEquals(404,response.getStatus());
    }

    /**
     * Tests the save Uniqueness Record exception record
     * @throws UniquenessConstraintException in case of repeated information
     */
    @Test
    public void testSaveUniquenessException() throws UniquenessConstraintException {
        doThrow(new UniquenessConstraintException()).when(roleBusinessService).create(any());
        Response response = roleResource.create(new Role());
        assertEquals(400,response.getStatus());
    }

    /**
     * Tests the update for a scenario where Uniqueness Record exception occurs
     * @throws UniquenessConstraintException in case of repeated information
     */
    @Test
    public void testUpdateUniquenessException() throws RoleNotFoundException, UniquenessConstraintException {
        doThrow(new UniquenessConstraintException()).when(roleBusinessService).update(any());
        Response response = roleResource.update(1L, new Role());
        assertEquals(400,response.getStatus());
    }

    /**
     * Tests the update for a scenario where generic exception occurs
     * @throws UniquenessConstraintException in case of repeated information
     */
    @Test
    public void testUpdateGenericException() throws RoleNotFoundException, UniquenessConstraintException {
        doThrow(new RuntimeException("Toplink error")).when(roleBusinessService).update(any());
        Response response = roleResource.update(1L, new Role());
        assertEquals(500,response.getStatus());
    }

}