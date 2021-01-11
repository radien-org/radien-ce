/*
 * Copyright (c) 2016-present openappframe.org & its legal owners. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import io.radien.api.service.user.UserServiceAccess;
import io.radien.ms.usermanagement.client.exceptions.ErrorCodeMessage;
import io.radien.ms.usermanagement.client.exceptions.InvalidRequestException;
import io.radien.ms.usermanagement.entities.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Nuno Santana
 * @author Bruno Gama
 *
 */
@Path("user")
@RequestScoped
public class UserResource {

	@Inject
	private UserServiceAccess userService;

	private static final Logger log = LoggerFactory.getLogger(UserResource.class);

	@GET
	@Path("/search/{search}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAll(@PathParam("search") String search,
						   @DefaultValue("1")  @QueryParam("pageNo") int pageNo,
						   @DefaultValue("10") @QueryParam("pageSize") int pageSize,
						   @QueryParam("sortBy") List<String> sortBy,
						   @DefaultValue("true") @QueryParam("asc") boolean isAscending,
						   @DefaultValue("true") @QueryParam("isConjunction") boolean isConjunction) {
		try {
			return Response.ok(userService.getAll(search, pageNo, pageSize, sortBy, isAscending, isConjunction)).build();
		} catch (Exception e) {
			return getGenericError(e);
		}
	}

	/**
	 * Returns JSON message with the specific required information search by the user ID.
	 * @param id to be search
	 * @return Ok message if it has success. Returns error 404 Code to the user in case of resource is not existent.
	 */
	@GET
	@Path("/id/{id}")
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

//	/**
//	 * Will update the requested user in base of his id, with the given user information
//	 *
//	 * @param id of user to be updated
//	 * @param newUserInformation user information to update
//	 * @return Response ok in case of success
//	 */
//	@PUT
//	@Path("/id/{id}")
//	@Consumes(MediaType.APPLICATION_JSON)
//	public Response updateUser(@PathParam("id") long id, User newUserInformation) {
//		try {
//			SystemUser user = null;
//			userService.save(user);
////		} catch (NotFoundException notFoundException){
////			return getResourceNotFoundException();
////		} catch (InvalidRequestException invalidRequestException){
////			return getInvalidRequestResponse(invalidRequestException);
//
//		} catch (Exception e) {
//			return getGenericError(e);
//		}
//		return Response.ok().build();
//	}

	/**
	 * Deletes requested user from the DB
	 * @param id of the user to be deleted
	 * @return error 404 Code to the user in case of resource is not existent.
	 */
	@DELETE
	@Path("/{id}")
	@Transactional
	public Response delete(@NotNull @PathParam("id") Long id)  {
		try {
			userService.delete(id);
		} catch (Exception e){
			return getGenericError(e);
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
	public Response create(User user) {
		try {
			user.setId(null);
			userService.save(user);
			return Response.ok().build();
//		} catch (InvalidRequestException e) {
//			return getInvalidRequestResponse(e);
		} catch (Exception e) {
			return getGenericError(e);
		}
	}

	private Response getInvalidRequestResponse(InvalidRequestException e) {
		return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
	}

	/**
	 * Generic error exception. Launches a 500 Error Code to the user.
	 * @param e exception to be throw
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
