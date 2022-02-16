package io.radien.ms.ecm.client.controller;

import java.io.Serializable;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;

import io.radien.ms.ecm.client.entities.GlobalHeaders;

@Path("i18n")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RegisterClientHeaders(GlobalHeaders.class)
public interface I18NResource extends Serializable{

    @GET
    @Path("/localize")
    Response getMessage(@QueryParam("key") String key,
             @QueryParam("application") String application,
             @QueryParam("language") String language);

    @GET
    Response getProperty(@QueryParam("key") String key,
                         @QueryParam("application") String application);
}
