package io.radien.ms.usermanagement.client.services;

import io.radien.ms.usermanagement.client.entities.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.List;

@Path("usermanagement/v1/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface UserResourceClient {

    @GET
    public Response getAll(@QueryParam("sub") List<String> subs,
                           @QueryParam("userEmail") List<String> emails,
                           @QueryParam("logon") List<String> logons,
                           @QueryParam("pageNo") int pageNo,
                           @QueryParam("pageSize") int pageSize,
                           @QueryParam("sortBy") List<String> sortBy,
                           @QueryParam("asc") Boolean isAscending,
                           @QueryParam("isConjunction") Boolean isConjunction);

    @GET
    public Response getById(Long id);

    @PUT
    public Response updateUser(long id, User newUserInformation);

    @DELETE
    public Response deleteOrganization(Long id);

    @POST
    public Response create(User user);
}
