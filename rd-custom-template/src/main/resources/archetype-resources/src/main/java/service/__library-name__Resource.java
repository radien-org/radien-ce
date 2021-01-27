package ${package}.service;


import ${package}.model.System${library-name};
import ${package}.model.${library-name};
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Rajesh Gavvala
 *
 */

@Path("${resource-path}")
@RequestScoped
public class ${library-name}Resource {
    private static final Logger log = LoggerFactory.getLogger(${library-name}Resource.class);

    @Inject
            ${library-name}Service templateService;

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@PathParam("id") Long id) {
        System${library-name} system${library-name} = templateService.get(id);
        return Response.ok(system${library-name}).build();
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(${library-name} template) {
        templateService.create(template);
        return Response.status(201).entity("Template created successfully !!").build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") long id, ${library-name} template) {
        templateService.update(template);
        return Response.status(200).entity("Template updated successfully !!").build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") long id) {
        templateService.delete(id);
        return Response.status(202).entity("Template deleted successfully !!").build();
    }
}
