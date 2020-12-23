package io.radien.ms.usermanagement.client.services;

import io.radien.ms.usermanagement.client.entities.User;


import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.List;

@Path("usermanagement/v1/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface UserServiceClient {

    @GET
    public Response getAll(@QueryParam("pageNo") int pageNo,
                    @QueryParam("pageSize") int pageSize,
                    @QueryParam("sortBy") List<String> sortBy,
                    @QueryParam("asc") boolean isAscending,
                    @QueryParam("sub") List<String> subs) ;

    @POST
    public Response create(User user);
}
