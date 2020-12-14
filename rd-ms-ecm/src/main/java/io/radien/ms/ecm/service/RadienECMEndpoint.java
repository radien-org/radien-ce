package io.radien.ms.ecm.service;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.radien.api.service.ecm.ContentServiceAccess;
import io.radien.api.service.ecm.model.EnterpriseContent;


@Path("content")
@RequestScoped
public class RadienECMEndpoint {

    private static final Logger log = LoggerFactory.getLogger(RadienECMEndpoint.class);

    @Inject
    private ContentServiceAccess contentService;

    @GET
	@Path("{viewId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getModel(@PathParam("viewId") String viewId) {
		List<EnterpriseContent> content = contentService.getByViewIdLanguage(viewId, true, "en");
		return Response.ok(content).build();
	}
    
    @POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response add(EnterpriseContent model) {
    	String viewId = "ERROR";
		try {
			contentService.save(model);
			viewId = model.getViewId();
			return Response.created(UriBuilder.fromResource(this.getClass()).path(viewId).build()).build();
		} catch (Exception e) {
			log.error("error saving content",e);
		}
		return Response.serverError().build();
		
	}
}
