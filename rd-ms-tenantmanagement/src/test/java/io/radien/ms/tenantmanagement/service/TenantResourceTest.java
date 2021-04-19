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
package io.radien.ms.tenantmanagement.service;

import io.radien.api.model.tenant.SystemTenantSearchFilter;
import io.radien.api.service.tenant.TenantServiceAccess;
import io.radien.exception.*;
import io.radien.ms.tenantmanagement.entities.Tenant;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * @author Nuno Santana
 */
public class TenantResourceTest {

    @InjectMocks
    TenantResource tenantResource;

    @Mock
    TenantServiceAccess tenantServiceAccess;


    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test the Get request which will return a success message code 200.
     */
    @Test
    public void testGet() {
        Response response = tenantResource.get(null, null, true, true);
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }

    /**
     * Test the Get request Exception which will return a generic error message code 500.
     */
    @Test
    public void testGetGenericException() {
        doThrow(new RuntimeException()).when(tenantServiceAccess).get((SystemTenantSearchFilter) any());
        Response response = tenantResource.get(null, null, true, true);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),response.getStatus());
    }

    /**
     * Test the Get All request which will return a success message code 200.
     */
    @Test
    public void testGetAll() {
        Response response = tenantResource.getAll(null, 1, 10, null, false);
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }

    /**
     * Test the Get All request Exception which will return a generic error message code 500.
     */
    @Test
    public void testGetAllGenericException() {
        when(tenantResource.getAll(null,1, 10, null, false))
                .thenThrow(new RuntimeException());
        Response response = tenantResource.getAll(null,1, 10, null, false);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),response.getStatus());
    }

    /**
     * Test that will test the error message 404 User Not Found
     */
    @Test
    public void testGetById404() {
        Response response = tenantResource.getById(9L);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(),response.getStatus());
    }

    /**
     * Get by ID with success should return a 200 code message
     */
    @Test
    public void testGetById() {
        when(tenantServiceAccess.get(1L)).thenReturn(new Tenant());
        Response response = tenantResource.getById(1L);
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }

    /**
     * Test Get by ID exception which will return a 500 error code message
     */
    @Test
    public void testGetByIdGenericException() {
        when(tenantServiceAccess.get(1L)).thenThrow(new RuntimeException());
        Response response = tenantResource.getById(1L);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),response.getStatus());
    }

    /**
     * Test Get Contract by should return success with a 200 code
     */
    @Test
    public void testGetContractByName() {
        Response response = tenantResource.get("subj", null, true, true);
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }


    @Test
    public void testGetContractById() {
        when(tenantServiceAccess.get(anyLong())).thenReturn(new Tenant());
        Response response = tenantResource.getById(1L);

        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }

    /**
     * Deletion of the record with success, should return a 200 code message
     */
    @Test
    public void testDelete() {
        Response response = tenantResource.delete(1L);
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }

    /**
     * Deletion of the record with error, should return a generic 500 error code message
     */
    @Test
    public void testDeleteGenericError() {
        doThrow(new RuntimeException()).when(tenantServiceAccess).delete(1L);
        Response response = tenantResource.delete(1L);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),response.getStatus());
    }

    /**
     * Deletion of the record(s) with success, should return a 200 code message
     */
    @Test
    public void testDeleteTenantHierarchy() {
        Response response = tenantResource.deleteTenantHierarchy(1L);
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }

    /**
     * Deletion of the record with error, should return a generic 500 error code message
     */
    @Test
    public void testDeleteTenantHierarchyGenericError() {
        doThrow(new RuntimeException()).when(tenantServiceAccess).deleteTenantHierarchy(1L);
        Response response = tenantResource.deleteTenantHierarchy(1L);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),response.getStatus());
    }

    /**
     * Creation with success of a record. Should return a 200 code message
     */
    @Test
    public void testCreate() {
        Response response = tenantResource.create(new Tenant());
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }

    /**
     * Creation with error of a record. Should return a 400 code message Invalid Requested Exception
     * @throws UniquenessConstraintException in case of request could not be performed by any specific and justified in the
     * message reason
     */
    @Test
    public void testCreateInvalid() throws UniquenessConstraintException, TenantException {
        doThrow(new UniquenessConstraintException()).when(tenantServiceAccess).create(any());
        Response response = tenantResource.create(new Tenant());
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),response.getStatus());
    }

    /**
     * Creation of a record with error. Should return a generic error message 500
     * @throws UniquenessConstraintException in case of request could not be performed by any specific and justified in the
     * message reason
     */
    @Test
    public void testCreateGenericError() throws UniquenessConstraintException, TenantException {
        doThrow(new RuntimeException()).when(tenantServiceAccess).create(any());
        Response response = tenantResource.create(new Tenant());
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),response.getStatus());
    }


    /**
     * Creation with success of a record. Should return a 200 code message
     */
    @Test
    public void testUpdate() {
        Response response = tenantResource.update(1l,new Tenant());
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }

    /**
     * Creation with error of a record. Should return a 400 code message Invalid Requested Exception
     * @throws UniquenessConstraintException in case of request could not be performed by any specific and justified in the
     * message reason
     */
    @Test
    public void testUpdateInvalid() throws UniquenessConstraintException, TenantException {
        doThrow(new UniquenessConstraintException()).when(tenantServiceAccess).update(any());
        Response response = tenantResource.update(1l,new Tenant());
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),response.getStatus());
    }

    /**
     * Creation of a record with error. Should return a generic error message 500
     * @throws UniquenessConstraintException in case of request could not be performed by any specific and justified in the
     * message reason
     */
    @Test
    public void testUpdateGenericError() throws UniquenessConstraintException, TenantException {
        doThrow(new RuntimeException()).when(tenantServiceAccess).update(any());
        Response response = tenantResource.update(1l,new Tenant());
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),response.getStatus());
    }

    /**
     * Test the exists request which will return a success message code 200.
     */
    @Test
    public void testExists() {
        Response response = tenantResource.exists(1L);
        assertEquals(200,response.getStatus());
    }

    /**
     * Test the exists request which will return a error message code 500.
     */
    @Test
    public void testExistsException() {
        when(tenantResource.exists(any()))
                .thenThrow(new NotFoundException());
        Response response = tenantResource.exists(100L);
        assertEquals(404,response.getStatus());
    }
}