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

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import io.radien.api.model.user.SystemUser;
import io.radien.api.model.user.SystemUserSearchFilter;
import io.radien.api.service.user.UserServiceAccess;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.usermanagement.client.entities.UserSearchFilter;
import io.radien.ms.usermanagement.client.exceptions.ErrorCodeMessage;
import io.radien.ms.usermanagement.client.services.UserResourceClient;

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
public class UserResource implements UserResourceClient {

	@Inject
	private UserServiceAccess userService;

	private static final Logger log = LoggerFactory.getLogger(UserResource.class);

	@Override
	public Response getAll(String search, int pageNo, int pageSize,
						   List<String> sortBy, boolean isAscending) {
		try {
			return Response.ok(userService.getAll(search, pageNo, pageSize, sortBy, isAscending)).build();
		} catch (Exception e) {
			return getGenericError(e);
		}
	}

	@Override
	public Response getUsersBy(String sub, String email, String logon, boolean isExact, boolean isLogicalConjunction) {
		try {
			SystemUserSearchFilter filter = new UserSearchFilter(sub,email,logon,isExact,isLogicalConjunction);
			return Response.ok(userService.getUsersBy(filter)).build();
		} catch (Exception e) {
			return getGenericError(e);
		}
	}

	/**
	 * Returns JSON message with the specific required information search by the user ID.
	 * @param id to be search
	 * @return Ok message if it has success. Returns error 404 Code to the user in case of resource is not existent.
	 */
	public Response getById(Long id) {
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
	public Response delete(long id)  {
		try {
			userService.delete(id);
		} catch (Exception e){
			return getGenericError(e);
		}
		return Response.ok().build();
	}


	/**
	 * Save user to the DB.
	 *
	 * @param user to be added
	 * @return Ok message if it has success. Returns error 400 Code to the user in case of invalid request.
	 */
	public Response save(io.radien.ms.usermanagement.client.entities.User user) {
		try {
			userService.save(new User(user));
			return Response.ok().build();
		} catch (UniquenessConstraintException e) {
			return getInvalidRequestResponse(e);
		} catch (Exception e) {
			return getGenericError(e);
		}
	}

	/**
	 * Invalid Request error exception. Launches a 400 Error Code to the user.
	 * @param e exception to be throw
	 * @return code 400 message Generic Exception
	 */
	private Response getInvalidRequestResponse(UniquenessConstraintException e) {
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
