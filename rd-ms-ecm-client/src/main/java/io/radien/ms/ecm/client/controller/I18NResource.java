package io.radien.ms.ecm.client.controller;

import io.radien.api.model.i18n.SystemI18NProperty;
import io.radien.ms.ecm.client.entities.i18n.DeletePropertyFilter;
import io.radien.ms.ecm.client.entities.i18n.I18NProperty;
import java.io.Serializable;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
    Response getProperty(@QueryParam("key") String key,
                         @QueryParam("application") String application);

    @DELETE
    Response deleteProperties(DeletePropertyFilter filter);

    @POST
    Response saveProperty(I18NProperty property);

    @GET
    @Path("/localize")
    Response getMessage(@QueryParam("key") String key,
             @QueryParam("application") String application,
             @QueryParam("language") String language);


}
