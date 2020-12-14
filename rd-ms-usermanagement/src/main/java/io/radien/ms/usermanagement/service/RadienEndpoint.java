/**
 * 
 */
package io.radien.ms.usermanagement.service;

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

import io.radien.ms.usermanagement.model.RadienModel;
import io.radien.ms.usermanagement.repository.RadienManager;

/**
 * @author mawe
 *
 */
@Path("user")
@RequestScoped
public class RadienEndpoint {

	@Inject
	private RadienManager manager;
	

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAll() {
		return Response.ok(manager.getAll()).build();
	}
	
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getModel(@PathParam("id") String id) {
		RadienModel model = manager.getModel(id);
		return Response.ok(model).build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response add(RadienModel model) {
		String id = manager.add(model);
		return Response.created(UriBuilder.fromResource(this.getClass()).path(id).build()).build();
	}

}
