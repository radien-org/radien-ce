package io.radien.ms.ticketmanagement.service;

import io.radien.api.model.ticket.SystemTicket;
import io.radien.api.service.ticket.TicketServiceAccess;
import io.radien.exception.GenericErrorMessagesToResponseMapper;
import io.radien.exception.TicketException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.ticketmanagement.client.entities.Ticket;
import io.radien.ms.ticketmanagement.client.services.TicketResourceClient;
import io.radien.ms.ticketmanagement.entities.TicketEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.List;

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
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
    }

    @Override
    public Response create(Ticket ticket) {
        try {
            ticketService.create(new TicketEntity(ticket));
            return Response.ok(ticket.getId()).build();
        } catch (TicketException u){
            return GenericErrorMessagesToResponseMapper.getInvalidRequestResponse(u.getMessage());
        } catch (Exception e){
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
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
        } catch (Exception e){
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
    }

    @Override
    public Response delete(Long id) {
        try {
            return Response.ok(ticketService.delete(id)).build();
        }catch (Exception e){
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
    }

    @Override
    public Response getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) {
        try {
            log.info("Will get all the ticket information I can find!");
            return Response.ok(ticketService.getAll(search, pageNo, pageSize, sortBy, isAscending)).build();
        } catch(Exception e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
    }
}
