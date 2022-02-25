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
 *
 */

package io.radien.ms.ticketmanagement.service;

import io.radien.api.model.ticket.SystemTicket;
import io.radien.api.model.ticket.SystemTicketSearchFilter;
import io.radien.api.service.ticket.TicketServiceAccess;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.GenericErrorMessagesToResponseMapper;
import io.radien.exception.TicketException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.openid.entities.Authenticated;
import io.radien.ms.ticketmanagement.client.entities.Ticket;
import io.radien.ms.ticketmanagement.client.entities.TicketSearchFilter;
import io.radien.ms.ticketmanagement.client.services.TicketResourceClient;
import io.radien.ms.ticketmanagement.entities.TicketEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.List;

@Authenticated
public class TicketResource implements TicketResourceClient {

    private static final Logger log = LoggerFactory.getLogger(TicketResource.class);

    @Inject
    TicketServiceAccess ticketService;


    @Override
    public Response getById(Long id) {
        try {
            SystemTicket ticket = ticketService.get(id);
            if(ticket == null){
                return GenericErrorMessagesToResponseMapper.getResourceNotFoundException();
            }
            return Response.ok(ticket).build();
        }catch (Exception e){
            log.error(GenericErrorCodeMessage.ERROR_RETRIEVING_PROVIDED_TICKET.toString(String.valueOf(id)), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(GenericErrorCodeMessage.ERROR_RETRIEVING_PROVIDED_TICKET.toString(String.valueOf(id)) + e.getMessage())
                    .build();
        }
    }

    @Override
    public Response create(Ticket ticket) {
        try {
            ticketService.create(new TicketEntity(ticket));
            return Response.ok(ticket.getId()).build();
        } catch (TicketException | UniquenessConstraintException u){
            return GenericErrorMessagesToResponseMapper.getInvalidRequestResponse(u.getMessage());
        } catch (Exception e){
            log.error(GenericErrorCodeMessage.ERROR_CREATING_TICKET.toString(ticket.toString()), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(GenericErrorCodeMessage.ERROR_CREATING_TICKET.toString(ticket.toString()) + e.getMessage())
                    .build();
        }
    }

    @Override
    public Response update(long id, Ticket ticket) {
        try {
            TicketEntity ticketEntity = new TicketEntity(ticket);
            ticketEntity.setId(id);
            ticketService.update(ticketEntity);
            return Response.ok().build();
        }catch (TicketException | UniquenessConstraintException u){
            return GenericErrorMessagesToResponseMapper.getInvalidRequestResponse(u.getMessage());
        }catch (Exception e){
            log.error(GenericErrorCodeMessage.ERROR_UPDATING_TICKET.toString(String.valueOf(id)), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(GenericErrorCodeMessage.ERROR_UPDATING_TICKET.toString(String.valueOf(id)) + e.getMessage())
                    .build();
        }
    }

    @Override
    public Response delete(Long id) {
        try {
            return Response.ok(ticketService.delete(id)).build();
        }catch (Exception e){
            log.error(GenericErrorCodeMessage.ERROR_DELETING_TICKET.toString(String.valueOf(id)), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(GenericErrorCodeMessage.ERROR_DELETING_TICKET.toString(String.valueOf(id)) + e.getMessage())
                    .build();
        }
    }

    @Override
    public Response getAll(Long userId, Long ticketType, LocalDate expireDate, String token, String data, boolean isLogicalConjunction,
                           int pageNo, int pageSize, List<String> sortBy, boolean isAscending) {
        try {
            log.info("Will get all the ticket information I can find!");
            SystemTicketSearchFilter filter = new TicketSearchFilter(userId, ticketType, expireDate, token, data, isLogicalConjunction);
            return Response.ok(ticketService.getAll(filter, pageNo, pageSize, sortBy, isAscending)).build();
        } catch(Exception e) {
            log.error(GenericErrorCodeMessage.ERROR_RETRIEVING_TICKETS.toString(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(GenericErrorCodeMessage.ERROR_RETRIEVING_TICKETS + e.getMessage())
                    .build();
        }
    }
}
