/**
 * 
 */
package io.radien.ms.usermanagement.service;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import io.radien.api.model.user.SystemUser;
import io.radien.ms.usermanagement.client.UserResponseExceptionMapper;
import io.radien.ms.usermanagement.client.services.UserServiceClient;
import io.radien.ms.usermanagement.client.exceptions.ErrorCodeMessage;
import io.radien.ms.usermanagement.client.exceptions.InvalidRequestException;
import io.radien.ms.usermanagement.legacy.UserService;
import io.radien.persistence.entities.user.User;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;

/**
 * @author mawe
 *
 */
@Path("user")
@RequestScoped
public class UserEndpoint {

	@Inject
	private UserService userService;

	private static final Logger log = LoggerFactory.getLogger(UserEndpoint.class);

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAll(@DefaultValue("1")  @QueryParam("pageNo") int pageNo,
						   @DefaultValue("10") @QueryParam("pageSize") int pageSize,
						   @QueryParam("sortBy") List<String> sortBy, @DefaultValue("true") @QueryParam("asc") boolean isAscending) {
		try {
			return Response.ok(userService.getAll(pageNo, pageSize, sortBy, isAscending)).build();
		} catch (Exception e) {
			return getGenericError(e);
		}
	}

	//TODO: Error Handling

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getById(@PathParam("id") Long id) {
		try {
			SystemUser systemUser = userService.get(id);
			if(systemUser == null){
				return Response.status(Response.Status.NOT_FOUND).entity(ErrorCodeMessage.RESOURCE_NOT_FOUND.toString()).build();
			}
			return Response.ok(systemUser).build();
		} catch(Exception e) {
			return getGenericError(e);
		}
	}

	@POST
	@Transactional
	@Consumes(MediaType.APPLICATION_JSON)
	public Response add(User user) {
		try {
			Long id = userService.save(user);
			return Response.created(UriBuilder.fromResource(this.getClass()).path(id.toString()).build()).build();
		} catch (InvalidRequestException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		} catch (Exception e) {
			return getGenericError(e);
		}
	}

	private Response getGenericError(Exception e) {
		String message = ErrorCodeMessage.GENERIC_ERROR.toString();
		log.error(message, e);
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
	}

	@Path("clientTest")
	@GET
	public String clientTest(){
		try {
			URL url = new URL("http://localhost:8080/usermanagement");
			UserServiceClient client = RestClientBuilder.
					newBuilder()
					.baseUrl(url)
					.register(UserResponseExceptionMapper.class)
					.build(UserServiceClient.class);

			client.getAll(1,1,null,true);
		} catch ( MalformedURLException e) {
			log.error(e.getMessage(),e);
		}
		return "I don't have your super seeds. Look elsewhere";
	}
}
