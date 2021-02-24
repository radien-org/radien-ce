package io.radien.ms.ecm.client.services;


import io.radien.ms.ecm.client.entities.I18NProperty;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("i18n")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface I18NPropertyResourceClient {
    @GET
    @Path("")
    Response getMessage(@QueryParam("msg") String msg);

    @GET
    @Path("/{key}")
    @Produces(MediaType.APPLICATION_JSON)
    Response getProperty(@PathParam("key") String key);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Response add(I18NProperty property);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Response addAll(List<I18NProperty> propertyList);

    @GET
    @Path("/keys")
    Response getKeys();

    @GET
    @Path("/properties")
    Response getProperties();

    @POST
    @Path("/initialize/{secret}")
    Response initializeProperties(@PathParam("secret") String secret);
}
