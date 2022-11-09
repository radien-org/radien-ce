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

import io.radien.api.model.tenant.SystemActiveTenantSearchFilter;
import io.radien.api.service.tenant.exception.ActiveTenantException;
import io.radien.api.service.tenant.exception.ActiveTenantNotFoundException;
import io.radien.exception.BadRequestException;
import io.radien.exception.SystemException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.tenantmanagement.client.entities.ActiveTenant;
import io.radien.ms.tenantmanagement.service.ActiveTenantBusinessService;
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
 * {@link ActiveTenantResource}
 *
 * @author Bruno Gama
 */
public class ActiveTenantResourceTest {

    @InjectMocks
    ActiveTenantResource activeTenantResource;

    @Mock
    ActiveTenantBusinessService activeTenantServiceAccess;


    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test the Get All request which will return a success message code 200.
     */
    @Test
    public void testGetAll() {
        Response response = activeTenantResource.getAll(null, null,1, 10, null, false);
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }

    /**
     * Test that will test the error message 404 record Not Found
     */
    @Test(expected = ActiveTenantNotFoundException.class)
    public void testGetById404() {
        when(activeTenantServiceAccess.get(anyLong()))
                .thenThrow(new ActiveTenantNotFoundException("not found"));
        activeTenantResource.getById(9L);
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
     * Get by ID with success should return a 200 code message
     */
    @Test
    public void testGetById() {
        when(activeTenantServiceAccess.get(1L)).thenReturn(new ActiveTenant());
        Response response = activeTenantResource.getById(1L);
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
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
     * Deletion of the record with success, should return a 200 code message
     */
    @Test
    public void testDeleteByTenantAndUser() {
        Response response = activeTenantResource.delete(1L,1L);
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
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
     */
    @Test(expected = BadRequestException.class)
    public void testCreateInvalid() {
        doThrow(new BadRequestException()).when(activeTenantServiceAccess).create(any());
        activeTenantResource.create(new ActiveTenant());
    }

    /**
     * Creation with success of a record. Should return a 200 code message
     */
    @Test
    public void testUpdate() {
        Response response = activeTenantResource.update(1L,new ActiveTenant());
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }

    /**
     * Creation with error of a record. Should return a 400 code message Invalid Requested Exception
     * @throws UniquenessConstraintException in case of request could not be performed by any specific and justified in the
     * message reason
     * @throws ActiveTenantException in case of any issue in the validation of the data of the active tenant
     * @throws ActiveTenantNotFoundException if there is no ActiveTenant for the informed id
     */
    @Test(expected = BadRequestException.class)
    public void testUpdateInvalid()  {
        doThrow(new BadRequestException()).when(activeTenantServiceAccess).update(anyLong(), any());
        activeTenantResource.update(1L,new ActiveTenant());
    }


    /**
     * Tries to update an ActiveTenant that does not exist
     * @throws UniquenessConstraintException in case of request could not be performed by any specific and justified in the
     * message reason
     * @throws ActiveTenantException in case of any issue in the validation of the data of the active tenant
     * @throws ActiveTenantNotFoundException if there is no ActiveTenant for the informed id
     */
    @Test(expected = ActiveTenantNotFoundException.class)
    public void testUpdateNotFoundActiveTenant() throws UniquenessConstraintException, ActiveTenantException, ActiveTenantNotFoundException, SystemException {
        doThrow(new ActiveTenantNotFoundException("error")).when(activeTenantServiceAccess).update(anyLong(), any());
        activeTenantResource.update(1L,new ActiveTenant());
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
    @Test(expected = ActiveTenantNotFoundException.class)
    public void testExistsException() {
        when(activeTenantResource.exists(anyLong(), anyLong()))
                .thenThrow(new ActiveTenantNotFoundException("not found"));
        activeTenantResource.exists(100L, 100L);
    }
}