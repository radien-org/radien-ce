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
package io.radien.ms.ticketmanagement.service;

import io.radien.api.service.ticket.TicketServiceAccess;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.SystemException;
import io.radien.exception.TicketException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.ticketmanagement.client.entities.Ticket;
import io.radien.ms.ticketmanagement.entities.TicketEntity;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.Response;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * @author Rui Soares
 */
public class TicketResourceTest {

    @InjectMocks
    TicketResource ticketResource;

    @Mock
    TicketServiceAccess ticketServiceAccess;


    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test the Get request which will return a success message code 200.
     */
    @Test
    public void testGet() {
        Response response = ticketResource.getAll(null, 1, 10,new ArrayList<>(), true);
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }


    /**
     * Test the Get All request which will return a success message code 200.
     */
    @Test
    public void testGetAll() {
        Response response = ticketResource.getAll(null, 1, 10, null, false);
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }

    /**
     * Test the Get All request Exception which will return a generic error message code 500.
     */
    @Test
    public void testGetAllGenericException() {
        when(ticketResource.getAll(null,1, 10, null, false))
                .thenThrow(new RuntimeException());
        Response response = ticketResource.getAll(null,1, 10, null, false);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),response.getStatus());
    }

    /**
     * Test that will test the error message 404 User Not Found
     */
    @Test
    public void testGetById404() {
        Response response = ticketResource.getById(9L);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(),response.getStatus());
    }

    /**
     * Get by ID with success should return a 200 code message
     */
    @Test
    public void testGetById() throws SystemException {
        when(ticketServiceAccess.get(1L)).thenReturn(new TicketEntity());
        Response response = ticketResource.getById(1L);
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }

    /**
     * Test Get by ID exception which will return a 500 error code message
     */
    @Test
    public void testGetByIdGenericException() throws SystemException {
        when(ticketServiceAccess.get(1L)).thenThrow(new RuntimeException());
        Response response = ticketResource.getById(1L);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),response.getStatus());
    }

    /**
     * Test Get Contract by should return success with a 200 code
     */
    @Test
    public void testGetContractByName() {
        Response response = ticketResource.getAll("data", 1, 10,new ArrayList<>(), true);
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }


    @Test
    public void testGetContractById() throws SystemException {
        when(ticketServiceAccess.get(anyLong())).thenReturn(new TicketEntity());
        Response response = ticketResource.getById(1L);

        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }

    /**
     * Deletion of the record with success, should return a 200 code message
     */
    @Test
    public void testDelete() {
        Response response = ticketResource.delete(1L);
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }

    /**
     * Deletion of the record with error, should return a generic 500 error code message
     */
    @Test
    public void testDeleteGenericError() {
        doThrow(new RuntimeException()).when(ticketServiceAccess).delete(1L);
        Response response = ticketResource.delete(1L);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),response.getStatus());
    }

    /**
     * Creation with success of a record. Should return a 200 code message
     */
    @Test
    public void testCreate() {
        Response response = ticketResource.create(new TicketEntity());
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }

   /* *//**
     * Creation with error of a record. Should return a 400 code message Invalid Requested Exception
     * @throws UniquenessConstraintException in case of request could not be performed by any specific and justified in the
     * message reason
     */
    @Test
    public void testCreateInvalid() throws TicketException, UniquenessConstraintException {
        Ticket ticket = new Ticket();
        ticket.setToken(null);
        doThrow(new TicketException(GenericErrorCodeMessage.TICKET_FIELD_NOT_PROVIDED.toString("token"))).when(ticketServiceAccess).create(any());
        Response response = ticketResource.create(new TicketEntity(ticket));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),response.getStatus());
    }

    /**
     * Creation of a record with error. Should return a generic error message 500
     * @throws TicketException in case of request could not be performed by any specific and justified in the
     * message reason
     */
    @Test
    public void testCreateGenericError() throws TicketException, UniquenessConstraintException {
        doThrow(new RuntimeException()).when(ticketServiceAccess).create(any());
        Response response = ticketResource.create(new TicketEntity());
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),response.getStatus());
    }


    /**
     * Creation with success of a record. Should return a 200 code message
     */
    @Test
    public void testUpdate() {
        Response response = ticketResource.update(1L,new TicketEntity());
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }

    /**
     * Creation with error of a record. Should return a 400 code message Invalid Requested Exception
     * @throws UniquenessConstraintException in case of request could not be performed by any specific and justified in the
     * message reason
     */
    @Test
    public void testUpdateInvalid() throws TicketException, UniquenessConstraintException {
        doThrow(new UniquenessConstraintException()).when(ticketServiceAccess).update(any());
        Response response = ticketResource.update(1l,new TicketEntity());
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),response.getStatus());
    }

    /**
     * Creation of a record with error. Should return a generic error message 500
     * @throws UniquenessConstraintException in case of request could not be performed by any specific and justified in the
     * message reason
     */
    @Test
    public void testUpdateGenericError() throws UniquenessConstraintException, TicketException {
        doThrow(new RuntimeException()).when(ticketServiceAccess).update(any());
        Response response = ticketResource.update(1l,new TicketEntity());
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),response.getStatus());
    }
}