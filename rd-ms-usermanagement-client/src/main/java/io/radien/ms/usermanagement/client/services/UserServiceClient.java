package io.radien.ms.usermanagement.client.services;

import io.radien.ms.usermanagement.client.entities.Page;
import io.radien.ms.usermanagement.client.entities.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("v1/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface UserServiceClient {

    @GET
    Response getAll(@QueryParam("pageNo") int pageNo,
                           @QueryParam("pageSize") int pageSize,
                           @QueryParam("sortBy") List<String> sortBy, @QueryParam("asc") boolean isAscending) ;
}
