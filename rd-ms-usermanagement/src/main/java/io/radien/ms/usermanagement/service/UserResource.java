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
import io.radien.api.service.batch.BatchSummary;
import io.radien.api.model.user.SystemUserSearchFilter;
import io.radien.exception.SystemException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.exception.UserNotFoundException;
import io.radien.ms.usermanagement.batch.BatchResponse;
import io.radien.ms.usermanagement.client.entities.UserSearchFilter;
import io.radien.ms.usermanagement.client.exceptions.ErrorCodeMessage;
import io.radien.ms.usermanagement.client.exceptions.RemoteResourceException;
import io.radien.ms.usermanagement.client.services.UserResourceClient;

import io.radien.ms.usermanagement.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Nuno Santana
 * @author Bruno Gama
 *
 */
@Path("user")
@RequestScoped
public class UserResource implements UserResourceClient {

	@Inject
	private UserBusinessService userBusinessService;

	private static final Logger log = LoggerFactory.getLogger(UserResource.class);

	@Override
	public Response getAll(String search, int pageNo, int pageSize,
						   List<String> sortBy, boolean isAscending) {
		try {
			return Response.ok(userBusinessService.getAll(search, pageNo, pageSize, sortBy, isAscending)).build();
		} catch (Exception e) {
			return getResponseFromException(e);
		}
	}

	@Override
	public Response getUsers(String sub, String email, String logon, boolean isExact, boolean isLogicalConjunction) {
		try {
			SystemUserSearchFilter filter = new UserSearchFilter(sub,email,logon,isExact,isLogicalConjunction);
			return Response.ok(userBusinessService.getUsers(filter)).build();
		} catch (Exception e) {
			return getResponseFromException(e);
		}
	}

	/**
	 * Returns JSON message with the specific required information search by the user ID.
	 * @param id to be search
	 * @return Ok message if it has success. Returns error 404 Code to the user in case of resource is not existent.
	 */
	public Response getById(Long id) {
		try {
			SystemUser systemUser = userBusinessService.get(id);
			return Response.ok(systemUser).build();
		} catch(Exception e) {
			return getResponseFromException(e);
		}
	}

	/**
	 * Deletes requested user from the DB
	 * @param id of the user to be deleted
	 * @return error 404 Code to the user in case of resource is not existent.
	 */
	public Response delete(long id)  {
		try {
			userBusinessService.delete(id);
		}  catch (Exception e){
			return getResponseFromException(e);
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
			userBusinessService.save(new User(user),user.isDelegatedCreation());
		} catch (Exception e) {
			return getResponseFromException(e);
		}
		return Response.ok().build();
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
	 * Generic error exception. Launches a 500 Error Code to the user.
	 * @param e exception to be throw
	 * @return code 500 message Generic Exception
	 */
	private Response getRemoteResourceExceptionError(RemoteResourceException e) {
		String message = ErrorCodeMessage.GENERIC_ERROR.toString();
		log.error(message, e);
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
	}

	/**
	 * Generic error exception to when the user could not be found in DB. Launches a 404 Error Code to the user.
	 * @return code 100 message Resource not found.
	 */
	private Response getResourceNotFoundException() {
		return Response.status(Response.Status.NOT_FOUND).entity(ErrorCodeMessage.RESOURCE_NOT_FOUND.toString()).build();
	}

	/**
	 * Adds multiple users into the DB.
	 *
	 * @param userList of users to be added
	 * @return returns
	 *
	 * <ul>
	 *     <li>OK (Http status 200):</li>
	 *     <ul>
	 *         <li>All users were added</li>
	 *         <li>Some users were not added due found issues</li>	 *
	 *     </ul>
	 *     <li>BAD REQUEST (Http status 400): None users were added, were found issues for all them/li>
	 * </ul>
	 *
	 * For all cases the response must contains the quantity of not added users (not-processed-items),
	 * the found issues and an internal status as well (SUCCESS, PARTIAL_SUCCESS and FAIL).
	 */
	@Override
	public Response create(List<io.radien.ms.usermanagement.client.entities.User> userList) {
		try {
			List<User> users = userList.stream().map(u -> new User(u)).collect(Collectors.toList());
			BatchSummary batchSummary = this.userBusinessService.create(users);
			return BatchResponse.get(batchSummary);
		}
		catch (Exception e) {
			return getResponseFromException(e);
		}
	}

	private Response getResponseFromException(Exception e){
		Response response;
		try{
			log.error("ERROR: ",e);
			throw e;
		} catch (RemoteResourceException rre){
			response = getRemoteResourceExceptionError(rre);
		} catch (UserNotFoundException unfe){
			response = getResourceNotFoundException();
		} catch (UniquenessConstraintException uce) {
			return getInvalidRequestResponse(uce);
		}catch (Exception et){
			response = getGenericError(et);
		}
		return response;
	}
}
