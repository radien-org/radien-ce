/**
 * 
 */
package io.radien.ms.usermanagement.service;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


import io.radien.api.model.user.SystemUser;
import io.radien.ms.usermanagement.client.UserResponseExceptionMapper;
import io.radien.ms.usermanagement.client.services.UserServiceClient;
import io.radien.ms.usermanagement.client.exceptions.ErrorCodeMessage;
import io.radien.ms.usermanagement.client.exceptions.InvalidRequestException;
import io.radien.ms.usermanagement.entities.User;
import io.radien.ms.usermanagement.legacy.UserService;


import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * @author Nuno Santana
 * @author Bruno Gama
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
						   @QueryParam("sortBy") List<String> sortBy, @DefaultValue("true") @QueryParam("asc") boolean isAscending,
						   @QueryParam("sub") List<String> subs) {
		try {
			return Response.ok(userService.getAll(pageNo, pageSize, sortBy, isAscending, subs)).build();
		} catch (Exception e) {
			return getGenericError(e);
		}
	}

	/**
	 * Returns JSON message with the specific required information search by th user ID.
	 * @param id to be search
	 * @return Ok message if it has success. Returns error 404 Code to the user in case of resource is not existent.
	 */
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getById(@PathParam("id") Long id) {
		try {
			SystemUser systemUser = userService.get(id);
			if(systemUser == null){
				return getResourceNotFoundException();
			}
			return Response.ok(systemUser).build();
		} catch(Exception e) {
			return getGenericError(e);
		}
	}

	/**
	 * Deletes requested user from the DB
	 * @param id of the user to be deleted
	 * @return error 404 Code to the user in case of resource is not existent.
	 */
	@DELETE
	@Path("/{id}")
	@Transactional
	public Response deleteOrganization(@NotNull @PathParam("id") Long id)  {
		try {
			userService.delete(id);
		} catch (NotFoundException e) {
			return getResourceNotFoundException();
		}
		return Response.ok().build();
	}

	/**
	 * Adds user to the DB.
	 *
	 * @param user to be added
	 * @return Ok message if it has success. Returns error 400 Code to the user in case of invalid request.
	 */
	@POST
	@Transactional
	@Consumes(MediaType.APPLICATION_JSON)
	public Response add(User user) {
		try {
			if(user.getId()!=null){
				return Response.status(Response.Status.BAD_REQUEST).entity(ErrorCodeMessage.ID_SHOULD_BE_NULL).build();
			}
			userService.save(user);
			return Response.ok().build();
		} catch (InvalidRequestException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		} catch (Exception e) {
			return getGenericError(e);
		}
	}

	@Path("clientTest")
	@GET
	public String clientTest(){
		try {
			URL url = new URL("http://localhost:9080/rd-ms-usermanagement");

			UserServiceClient client = RestClientBuilder.
					newBuilder()
					.baseUrl(url)
					.register(UserResponseExceptionMapper.class)
					.build(UserServiceClient.class);
			client.getAll(1,1,null,true,null);
		} catch ( MalformedURLException e) {
			log.error(e.getMessage(),e);
		}
		return "I don't have your super seeds. Look elsewhere";
	}

	/**
	 * Generic error exception. Launches a 500 Error Code to the user.
	 * @param e
	 * @return code 500 message Generic Exception
	 */
	private Response getGenericError(Exception e) {
		String message = ErrorCodeMessage.GENERIC_ERROR.toString();
		log.error(message, e);
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
	}

	/**
	 * Generic error exception to when the user could not be found in DB. Launches a 404 Error Code to the user.
	 * @return code 100 message Resource not found.
	 */
	private Response getResourceNotFoundException() {
		return Response.status(Response.Status.NOT_FOUND).entity(ErrorCodeMessage.RESOURCE_NOT_FOUND.toString()).build();
	}
}
