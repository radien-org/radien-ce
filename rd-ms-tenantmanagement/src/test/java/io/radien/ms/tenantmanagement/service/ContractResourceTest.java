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
package io.radien.ms.tenantmanagement.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;

import javax.ws.rs.core.Response;

import io.radien.api.service.tenant.ContractServiceAccess;
import io.radien.exception.NotFoundException;
import io.radien.ms.tenantmanagement.entities.ContractEntity;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.radien.exception.SystemException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.exception.UserNotFoundException;

/**
 * @author Nuno Santana
 * @author Bruno Gama
 */
public class ContractResourceTest {

    @InjectMocks
    ContractResource contractResource;

    @Mock
    ContractServiceAccess contractService;


    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test the Get request which will return a success message code 200.
     */
    @Test
    public void testGet() {
        Response response = contractResource.get(null);
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }

    /**
     * Test the Get request Exception which will return a generic error message code 500.
     */
    @Test
    public void testGetGenericException() throws MalformedURLException {
        when(contractResource.get(null))
                .thenThrow(new RuntimeException());
        Response response = contractResource.get(null);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),response.getStatus());
    }

    /**
     * Test the Get All request which will return a success message code 200.
     */
    @Test
    public void testGetAll() {
        Response response = contractResource.getAll(0, 10);
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }

    /**
     * Test the Get All request Exception which will return a generic error message code 500.
     */
    @Test
    public void testGetAllGenericException() {
        when(contractResource.getAll(0, 10))
                .thenThrow(new RuntimeException());
        Response response = contractResource.getAll(0, 10);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),response.getStatus());
    }

    /**
     * Test the Exists request which will return a success message code 200.
     */
    @Test
    public void testExists() {
        Response response = contractResource.exists(1L);
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }

    /**
     * Test the Get All request Exception which will return a generic error message code 500.
     */
    @Test
    public void testExistsGenericException() {
        when(contractResource.exists(any()))
                .thenThrow(new NotFoundException());
        Response response = contractResource.exists(100L);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(),response.getStatus());
    }

    /**
     * Test that will test the error message 404 User Not Found
     */
    @Test
    public void testGetById404() {
        Response response = contractResource.getById(9L);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(),response.getStatus());
    }

    /**
     * Get by ID with success should return a 200 code message
     * @throws UserNotFoundException in case of user not found
     */
    @Test
    public void testGetById() throws UserNotFoundException {
        when(contractService.get(1L)).thenReturn(new ContractEntity());
        Response response = contractResource.getById(1L);
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }

    /**
     * Test Get by ID exception which will return a 500 error code message
     * @throws UserNotFoundException in case of user not found
     */
    @Test
    public void testGetByIdGenericException() throws UserNotFoundException {
        when(contractService.get(1L)).thenThrow(new RuntimeException());
        Response response = contractResource.getById(1L);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),response.getStatus());
    }

    /**
     * Test Get Contract by should return success with a 200 code
     */
    @Test
    public void testGetContractByName() {
        Response response = contractResource.get("subj");
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }


    @Test
    public void testGetContractById() throws UserNotFoundException {
        when(contractService.get(anyLong())).thenReturn(new ContractEntity());
        Response response = contractResource.getById(1L);

        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }

    /**
     * Deletion of the record with success, should return a 200 code message
     */
    @Test
    public void testDelete() {
        Response response = contractResource.delete(1L);
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }

    /**
     * Deletion of the record with error, should return a generic 500 error code message
     */
    @Test
    public void testDeleteGenericError() throws UserNotFoundException, SystemException {
        doThrow(new RuntimeException()).when(contractService).delete(1L);
        Response response = contractResource.delete(1L);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),response.getStatus());
    }

    /**
     * Creation with success of a record. Should return a 200 code message
     */
    @Test
    public void testCreate() {
        Response response = contractResource.create(new ContractEntity());
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }

    /**
     * Creation with error of a record. Should return a 400 code message Invalid Requested Exception
     * @throws UniquenessConstraintException in case of request could not be performed by any specific and justified in the
     * message reason
     * @throws UserNotFoundException in case of contract not found
     */
    @Test
    public void testCreateInvalid() throws UniquenessConstraintException, UserNotFoundException, SystemException {
        doThrow(new UniquenessConstraintException()).when(contractService).create(any());
        Response response = contractResource.create(new ContractEntity());
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),response.getStatus());
    }

    /**
     * Creation of a record with error. Should return a generic error message 500
     * @throws UniquenessConstraintException in case of request could not be performed by any specific and justified in the
     * message reason
     * @throws UserNotFoundException in case of contact not found
     */
    @Test
    public void testCreateGenericError() throws UniquenessConstraintException, UserNotFoundException, SystemException {
        doThrow(new RuntimeException()).when(contractService).create(any());
        Response response = contractResource.create(new ContractEntity());
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),response.getStatus());
    }


    /**
     * Creation with success of a record. Should return a 200 code message
     */
    @Test
    public void testUpdate() {
        Response response = contractResource.update(1l,new ContractEntity());
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }

    /**
     * Creation with error of a record. Should return a 400 code message Invalid Requested Exception
     * @throws UniquenessConstraintException in case of request could not be performed by any specific and justified in the
     * message reason
     * @throws UserNotFoundException in case of contract not found
     */
    @Test
    public void testUpdateInvalid() throws UniquenessConstraintException, UserNotFoundException, SystemException {
        doThrow(new UniquenessConstraintException()).when(contractService).update(any());
        Response response = contractResource.update(1l,new ContractEntity());
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),response.getStatus());
    }

    /**
     * Creation of a record with error. Should return a generic error message 500
     * @throws UniquenessConstraintException in case of request could not be performed by any specific and justified in the
     * message reason
     * @throws UserNotFoundException in case of contact not found
     */
    @Test
    public void testUpdateGenericError() throws UniquenessConstraintException, UserNotFoundException, SystemException {
        doThrow(new RuntimeException()).when(contractService).update(any());
        Response response = contractResource.update(1l,new ContractEntity());
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),response.getStatus());
    }

    /**
     * Test the Get total records count request which will return a success message code 200.
     */
    @Test
    public void testGetTotalRecordsCount() {
        Response response = contractResource.getTotalRecordsCount();
        assertEquals(200,response.getStatus());
    }

    /**
     * Test the Get total records count request which will return a error message code 500.
     */
    @Test
    public void testGetTotalRecordsCountException() {
        when(contractResource.getTotalRecordsCount())
                .thenThrow(new RuntimeException());
        Response response = contractResource.getTotalRecordsCount();
        assertEquals(500,response.getStatus());
    }
}