package io.radien.ms.ticketmanagement.client.services;

import io.radien.ms.openid.entities.GlobalHeaders;
import io.radien.ms.ticketmanagement.client.entities.Ticket;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("ticket")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RegisterClientHeaders(GlobalHeaders.class)
public interface TicketResourceClient {

    @GET
    @Path("/{id}")
    public Response getById(@NotNull @PathParam("id") Long id);

    @POST
    public Response create(Ticket ticket);

    @PUT
    @Path("/{id}")
    public Response update(@NotNull @PathParam("id") long id,Ticket ticket);

    @DELETE
    @Path("/{id}")
    public Response delete(@NotNull @PathParam("id") Long id);

    @GET
    public Response getAll(@QueryParam("search") String search,
                           @DefaultValue("1")  @QueryParam("pageNo") int pageNo,
                           @DefaultValue("10") @QueryParam("pageSize") int pageSize,
                           @QueryParam("sortBy") List<String> sortBy,
                           @DefaultValue("true") @QueryParam("asc") boolean isAscending);


}
