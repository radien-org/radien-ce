package io.radien.ms.permissionmanagement.client.services;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("action")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ActionResourceClient {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    Response getAll(@QueryParam("search") String search,
                    @DefaultValue("1") @QueryParam("pageNo") int pageNo,
                    @DefaultValue("10") @QueryParam("pageSize") int pageSize,
                    @QueryParam("sortBy") List<String> sortBy,
                    @DefaultValue("true") @QueryParam("asc") boolean isAscending);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("find")
    Response getActions(@QueryParam("name") String name,
                        @DefaultValue("true") @QueryParam("isExact") boolean isExact,
                        @DefaultValue("true") @QueryParam("isLogicalConjunction") boolean isLogicalConjunction);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    Response getById(@PathParam("id") Long id);

    @DELETE
    @Path("/{id}")
    Response delete(@NotNull @PathParam("id") long id);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Response save(io.radien.ms.permissionmanagement.client.entities.Action action);

}
