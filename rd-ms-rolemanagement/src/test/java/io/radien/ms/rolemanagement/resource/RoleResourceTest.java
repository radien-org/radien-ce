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
package io.radien.ms.rolemanagement.resource;

import io.radien.api.service.role.exception.RoleException;
import io.radien.api.service.role.exception.RoleNotFoundException;
import io.radien.exception.*;
import io.radien.ms.rolemanagement.client.entities.Role;
import io.radien.ms.rolemanagement.datalayer.RoleService;
import io.radien.ms.rolemanagement.service.RoleBusinessService;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import javax.ws.rs.core.Response;

import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * @author Bruno Gama
 */
public class RoleResourceTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();
    @InjectMocks
    RoleResource roleResource;
    @Mock
    RoleService roleService;
    @Mock
    RoleBusinessService roleBusinessService;

    /**
     * Test response of the get all method
     */
    @Test
    public void testGetAll() {
        Response response = roleResource.getAll(null,1,10,null,false);
        assertEquals(200,response.getStatus());
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
    @Test(expected = RoleNotFoundException.class)
    public void testGetByIdNotFoundException() throws RoleNotFoundException {
        when(roleBusinessService.getById(any())).thenThrow(new RoleNotFoundException("error"));
        roleResource.getById(1L);
    }

    /**
     * Tests delete return message
     */
    @Test
    public void testDelete() {
        when(roleService.delete(anyLong())).thenReturn(true);
        Response response = roleResource.delete(1L);
        assertEquals(200,response.getStatus());
    }

    /**
     * Tests the delete not found exception record
     * @throws RoleNotFoundException in case record not found
     */
    @Test(expected = RoleNotFoundException.class)
    public void testDeleteNotFoundError() throws RoleNotFoundException {
        doThrow(new RoleNotFoundException("error")).when(roleBusinessService).delete(anyLong());
        roleResource.delete(1L);
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
     * Tests the save not found exception record
     * @throws RoleNotFoundException in case record not found
     * @throws UniquenessConstraintException in case of repeated information
     */
    @Test(expected = RoleNotFoundException.class)
    public void testUpdateNotFoundError() throws RoleNotFoundException, UniquenessConstraintException, InvalidArgumentException {
        doThrow(new RoleNotFoundException("Not found")).when(roleBusinessService).update(anyLong(), any());
        roleResource.update(1L, new Role());
    }

    /**
     * Tests the save Uniqueness Record exception record
     * @throws UniquenessConstraintException in case of repeated information
     */
    @Test(expected = RoleException.class)
    public void testSaveUniquenessException() throws UniquenessConstraintException, InvalidArgumentException {
        doThrow(new RoleException("error")).when(roleBusinessService).create(any());
        roleResource.create(new Role());
    }

    /**
     * Tests the update for a scenario where Uniqueness Record exception occurs
     * @throws UniquenessConstraintException in case of repeated information
     */
    @Test(expected = RoleException.class)
    public void testUpdateUniquenessException() throws RoleNotFoundException, UniquenessConstraintException, InvalidArgumentException {
        doThrow(new RoleException("error")).when(roleBusinessService).update(anyLong(), any());
        roleResource.update(1L, new Role());
    }

    /**
     * Tests the update for a scenario where generic exception occurs
     * @throws UniquenessConstraintException in case of repeated information
     */
    @Test(expected = BadRequestException.class)
    public void testUpdateBadRequestException() throws RoleNotFoundException {
        doThrow(new BadRequestException("error")).when(roleBusinessService).update(anyLong(), any());
        roleResource.update(1L, new Role());
    }

}