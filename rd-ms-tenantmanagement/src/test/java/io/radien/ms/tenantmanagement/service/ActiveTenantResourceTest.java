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
package io.radien.ms.tenantmanagement.service;

import io.radien.api.model.tenant.SystemActiveTenantSearchFilter;
import io.radien.api.service.tenant.ActiveTenantServiceAccess;
import io.radien.exception.ActiveTenantException;
import io.radien.exception.NotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.tenantmanagement.client.entities.ActiveTenant;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * Active Tenant Resource Test
 * {@link io.radien.ms.tenantmanagement.service.ActiveTenantResource}
 *
 * @author Bruno Gama
 */
public class ActiveTenantResourceTest {

    @InjectMocks
    ActiveTenantResource activeTenantResource;

    @Mock
    ActiveTenantServiceAccess activeTenantServiceAccess;


    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test the Get All request which will return a success message code 200.
     */
    @Test
    public void testGetAll() {
        Response response = activeTenantResource.getAll(null, 1, 10, null, false);
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }

    /**
     * Test the Get All request Exception which will return a generic error message code 500.
     */
    @Test
    public void testGetAllGenericException() {
        when(activeTenantResource.getAll(null,1, 10, null, false))
                .thenThrow(new RuntimeException());
        Response response = activeTenantResource.getAll(null,1, 10, null, false);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),response.getStatus());
    }

    /**
     * Test that will test the error message 404 record Not Found
     */
    @Test
    public void testGetById404() {
        Response response = activeTenantResource.getById(9L);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(),response.getStatus());
    }

    /**
     * Tests response of the get specific association
     */
    @Test
    public void testGet() {
        Response response = activeTenantResource.get(2L, 2L, true);
        assertEquals(200,response.getStatus());
    }

    /**
     * Tests exception from the get specific association
     */
    @Test
    public void testGetException() {
        when(activeTenantServiceAccess.get((SystemActiveTenantSearchFilter) any())).thenThrow(new RuntimeException());
        Response response = activeTenantResource.get(2L ,2L, true);
        assertEquals(500,response.getStatus());
    }

    /**
     * Tests response of the get specific association
     */
    @Test
    public void testGetByUserAndTenant() {
        Response response = activeTenantResource.getByUserAndTenant(2L, 2L);
        assertEquals(200,response.getStatus());
    }

    /**
     * Tests exception from the get specific association
     */
    @Test
    public void testGetByUserAndTenantException() {
        when(activeTenantServiceAccess.getByUserAndTenant(anyLong(), anyLong())).thenThrow(new RuntimeException());
        Response response = activeTenantResource.getByUserAndTenant(2L,2L);
        assertEquals(500,response.getStatus());
    }

    /**
     * Get by ID with success should return a 200 code message
     */
    @Test
    public void testGetById() {
        when(activeTenantServiceAccess.get(1L)).thenReturn(new ActiveTenant());
        Response response = activeTenantResource.getById(1L);
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }

    /**
     * Test Get by ID exception which will return a 500 error code message
     */
    @Test
    public void testGetByIdGenericException() {
        when(activeTenantServiceAccess.get(1L)).thenThrow(new RuntimeException());
        Response response = activeTenantResource.getById(1L);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),response.getStatus());
    }

    /**
     * Test the get active tenant by id with 200 code message
     */
    @Test
    public void testGetActiveTenantById() {
        when(activeTenantServiceAccess.get(anyLong())).thenReturn(new ActiveTenant());
        Response response = activeTenantResource.getById(1L);

        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }

    /**
     * Deletion of the record with success, should return a 200 code message
     */
    @Test
    public void testDelete() {
        Response response = activeTenantResource.delete(1L);
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }

    /**
     * Deletion of the record with error, should return a generic 500 error code message
     */
    @Test
    public void testDeleteGenericError() {
        doThrow(new RuntimeException()).when(activeTenantServiceAccess).delete(1L);
        Response response = activeTenantResource.delete(1L);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),response.getStatus());
    }

    /**
     * Deletion of the record with success, should return a 200 code message
     */
    @Test
    public void testDeleteByTenantAndUser() {
        Response response = activeTenantResource.delete(1L,1L);
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }

    /**
     * Deletion of the record with error, should return a generic 500 error code message
     */
    @Test
    public void testDeleteByTenantAndUserGenericError() {
        doThrow(new RuntimeException()).when(activeTenantServiceAccess).delete(1L,1L);
        Response response = activeTenantResource.delete(1L,1L);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),response.getStatus());
    }



    /**
     * Creation with success of a record. Should return a 200 code message
     */
    @Test
    public void testCreate() {
        Response response = activeTenantResource.create(new ActiveTenant());
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }

    /**
     * Creation with error of a record. Should return a 400 code message Invalid Requested Exception
     * @throws UniquenessConstraintException in case of request could not be performed by any specific and justified in the
     * message reason
     * @throws ActiveTenantException in case of any issue in the validation of the data of the active tenant
     */
    @Test
    public void testCreateInvalid() throws UniquenessConstraintException, ActiveTenantException {
        doThrow(new UniquenessConstraintException()).when(activeTenantServiceAccess).create(any());
        Response response = activeTenantResource.create(new ActiveTenant());
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),response.getStatus());
    }

    /**
     * Creation of a record with error. Should return a generic error message 500
     * @throws UniquenessConstraintException in case of request could not be performed by any specific and justified in the
     * message reason
     * @throws ActiveTenantException in case of any issue in the validation of the data of the active tenant
     */
    @Test
    public void testCreateGenericError() throws UniquenessConstraintException, ActiveTenantException {
        doThrow(new RuntimeException()).when(activeTenantServiceAccess).create(any());
        Response response = activeTenantResource.create(new ActiveTenant());
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),response.getStatus());
    }


    /**
     * Creation with success of a record. Should return a 200 code message
     */
    @Test
    public void testUpdate() {
        Response response = activeTenantResource.update(1l,new ActiveTenant());
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }

    /**
     * Creation with error of a record. Should return a 400 code message Invalid Requested Exception
     * @throws UniquenessConstraintException in case of request could not be performed by any specific and justified in the
     * message reason
     * @throws ActiveTenantException in case of any issue in the validation of the data of the active tenant
     */
    @Test
    public void testUpdateInvalid() throws UniquenessConstraintException, ActiveTenantException {
        doThrow(new UniquenessConstraintException()).when(activeTenantServiceAccess).update(any());
        Response response = activeTenantResource.update(1l,new ActiveTenant());
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),response.getStatus());
    }

    /**
     * Creation of a record with error. Should return a generic error message 500
     * @throws UniquenessConstraintException in case of request could not be performed by any specific and justified in the
     * message reason
     * @throws ActiveTenantException in case of any issue in the validation of the data of the active tenant
     */
    @Test
    public void testUpdateGenericError() throws UniquenessConstraintException, ActiveTenantException {
        doThrow(new RuntimeException()).when(activeTenantServiceAccess).update(any());
        Response response = activeTenantResource.update(1l,new ActiveTenant());
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),response.getStatus());
    }

    /**
     * Test the exists request which will return a success message code 200.
     */
    @Test
    public void testExists() {
        Response response = activeTenantResource.exists(1L, 1L);
        assertEquals(200,response.getStatus());
    }

    /**
     * Test the exists request which will return a error message code 500.
     */
    @Test
    public void testExistsException() {
        when(activeTenantResource.exists(anyLong(), anyLong()))
                .thenThrow(new NotFoundException());
        Response response = activeTenantResource.exists(100L, 100L);
        assertEquals(404,response.getStatus());
    }
}