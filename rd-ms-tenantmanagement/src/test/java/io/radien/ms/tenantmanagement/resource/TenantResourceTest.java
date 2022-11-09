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
package io.radien.ms.tenantmanagement.resource;

import io.radien.api.model.tenant.SystemTenantSearchFilter;
import io.radien.api.service.tenant.exception.TenantException;
import io.radien.api.service.tenant.exception.TenantNotFoundException;
import io.radien.exception.*;
import io.radien.ms.tenantmanagement.entities.TenantEntity;
import io.radien.ms.tenantmanagement.service.TenantBusinessService;
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
    TenantBusinessService tenantBusinessService;


    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test the Get request which will return a success message code 200.
     */
    @Test
    public void testGet() {
        Response response = tenantResource.get(null, null, null,true, true);
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
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
     * Test that will test the error message 404 User Not Found
     */
    @Test(expected = TenantNotFoundException.class)
    public void testGetById404() {
        when(tenantBusinessService.get(9L)).thenThrow(new TenantNotFoundException("Not found"));
        Response response = tenantResource.getById(9L);
    }

    /**
     * Get by ID with success should return a 200 code message
     */
    @Test
    public void testGetById() {
        when(tenantBusinessService.get(1L)).thenReturn(new TenantEntity());
        Response response = tenantResource.getById(1L);
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }

    /**
     * Test Get Contract by should return success with a 200 code
     */
    @Test
    public void testGetContractByName() {
        Response response = tenantResource.get("subj", null, null,true, true);
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }


    @Test
    public void testGetContractById() {
        when(tenantBusinessService.get(anyLong())).thenReturn(new TenantEntity());
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
     * Deletion of the record(s) with success, should return a 200 code message
     */
    @Test
    public void testDeleteTenantHierarchy() {
        Response response = tenantResource.deleteTenantHierarchy(1L);
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }

    /**
     * Creation with success of a record. Should return a 200 code message
     */
    @Test
    public void testCreate() {
        Response response = tenantResource.create(new TenantEntity());
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }

    /**
     * Creation with error of a record. Should return a 400 code message Invalid Requested Exception
     * @throws UniquenessConstraintException in case of request could not be performed by any specific and justified in the
     * message reason
     */
    @Test(expected = TenantException.class)
    public void testCreateInvalid() throws UniquenessConstraintException, SystemException {
        doThrow(new TenantException("test")).when(tenantBusinessService).create(any());
        Response response = tenantResource.create(new TenantEntity());
    }

    /**
     * Creation with success of a record. Should return a 200 code message
     */
    @Test
    public void testUpdate() {
        Response response = tenantResource.update(1l,new TenantEntity());
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }

    /**
     * Creation with error of a record. Should return a 400 code message Invalid Requested Exception
     * @throws UniquenessConstraintException in case of request could not be performed by any specific and justified in the
     * message reason
     */
    @Test(expected = BadRequestException.class)
    public void testUpdateInvalid() throws UniquenessConstraintException, SystemException {
        doThrow(new BadRequestException()).when(tenantBusinessService).update(anyLong(), any());
        Response response = tenantResource.update(1l,new TenantEntity());
    }

    /**
     * Test the exists request which will return a success message code 200.
     */
    @Test
    public void testExists() {
        when(tenantBusinessService.exists(anyLong())).thenReturn(Boolean.TRUE);
        Response response = tenantResource.exists(1L);
        assertEquals(204,response.getStatus());
    }

    /**
     * Test the exists request which will return a error message code 404.
     */
    @Test
    public void testExistsFalse() {
        when(tenantBusinessService.exists(anyLong())).thenReturn(Boolean.FALSE);
        Response response = tenantResource.exists(100L);
        assertEquals(404,response.getStatus());
    }
}