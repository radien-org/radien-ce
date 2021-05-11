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


import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import io.radien.api.model.user.SystemUser;
import io.radien.api.service.batch.BatchSummary;
import io.radien.api.model.user.SystemUserSearchFilter;
import io.radien.api.service.role.SystemRolesEnum;
import io.radien.exception.SystemException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.exception.UserNotFoundException;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.usermanagement.batch.BatchResponse;
import io.radien.ms.usermanagement.client.entities.UserSearchFilter;
import io.radien.ms.usermanagement.client.exceptions.ErrorCodeMessage;
import io.radien.ms.usermanagement.client.exceptions.RemoteResourceException;
import io.radien.ms.usermanagement.client.services.UserResourceClient;

import io.radien.ms.usermanagement.entities.User;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Nuno Santana
 * @author Bruno Gama
 *
 */
@Path("user")
@RequestScoped
public class UserResource extends AuthorizationChecker implements UserResourceClient {

	@Inject
	private UserBusinessService userBusinessService;

	private static final Logger log = LoggerFactory.getLogger(UserResource.class);

	/**
	 * Will create request that by a given user sub will try to retrieve the user id
	 * @param sub that identifies the user
	 * @return Ok message if it has success. Returns error 500 Code to the user in case of resource is not existent.
	 */
	@Override
	public Response getUserIdBySub(String sub) {
		try {
			Long id = userBusinessService.getUserId(sub);
			if (id == null) {
				return getResourceNotFoundException();
			}
			return Response.ok(id).build();
		} catch (Exception e) {
			return getResponseFromException(e);
		}
	}

	/**
	 * Will request the service to retrieve all the users into a paginated response.
	 *
	 * @param search criteria to be found, can be used to multiple fields
	 * @param pageNo page number to show the first records
	 * @param pageSize max number of pages of results
	 * @param sortBy criteria field to be sorted
	 * @param isAscending boolean value to show the values ascending or descending way
	 * @return Ok message if it has success. Returns error 500 Code to the user in case of resource is not existent.
	 */
	@Override
	public Response getAll(String search, int pageNo, int pageSize,
						   List<String> sortBy, boolean isAscending) {
		try {
			if (!checkUserRoles()) {
				return getForbiddenResponse();
			}
			return Response.ok(userBusinessService.getAll(search, pageNo, pageSize, sortBy, isAscending)).build();
		} catch (Exception e) {
			return getResponseFromException(e);
		}
	}

	/**
	 * Retrieves multiple users into a response in base of a search filter criteria
	 * @param sub to be found
	 * @param email to be found
	 * @param logon to be found
	 * @param isExact should the search fields be exact or not as given
	 * @param isLogicalConjunction should the query use a and or a or criteria
	 * @return Ok message if it has success. Returns error 500 Code to the user in case of resource is not existent.
	 */
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
			if (!checkUserRoles()) {
				return getForbiddenResponse();
			}
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
			if (!checkUserRoles()) {
				return getForbiddenResponse();
			}
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
			if (!isSelfOnboard(user) && !checkUserRoles()) {
				return getForbiddenResponse();
			}
			userBusinessService.save(new User(user),user.isDelegatedCreation());
		} catch (Exception e) {
			return getResponseFromException(e);
		}
		return Response.ok().build();
	}

	/**
	 * Check if the the current logged user is trying to register
	 * himself into the radien repository
	 * @param user user to be register into radien repository
	 * @return true  or false
	 */
	public boolean isSelfOnboard(SystemUser user) {
		SystemUser invoker = getInvokerUser();
		return invoker != null && invoker.getSub().equals(user.getSub());
	}

	/**
	 * Gets the correct user id based on the given subject
	 * @param sub sub from the current logged logged user
	 * @return user id
	 * @throws SystemException SystemException is thrown by the common language runtime when errors occur
	 * that are nonfatal and recoverable by user programs.
	 */
	@Override
	protected Long getCurrentUserIdBySub(String sub) throws SystemException {
		Long userId = this.userBusinessService.getUserId(sub);
		if (userId == null) {
			throw new SystemException("No user available for sub " + sub);
		}
		return userId;
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
	 * Returns Forbidden response type in case of missing certain user roles
	 * @return code 403 message Forbidden response
	 */
	private Response getForbiddenResponse() {
		return Response.status(HttpStatus.SC_FORBIDDEN).
				entity("No Role available to perform this task").build();
	}

	/**
	 * Validates if the requester user has one of the specific roles either
	 * System Administrator or User Administrator
	 * @return true in case of neither of the roles are matching the user ones
	 * @throws SystemException throw exception in case of any issue validating the roles
	 */
	public boolean checkUserRoles() throws SystemException {
		List<String> roleNames = new ArrayList<>();
		roleNames.add(SystemRolesEnum.SYSTEM_ADMINISTRATOR.getRoleName());
		roleNames.add(SystemRolesEnum.USER_ADMINISTRATOR.getRoleName());
		return hasGrantMultipleRoles(roleNames);
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

	/**
	 * Will validate the given generic exception into a specific one and specific message to be throw or show to the user
	 * @param e exception to be validated
	 * @return Ok message if it has success. Returns error 500 Code to the user in case of any issue.
	 */
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

	/**
	 * Will send the updated password via email to the user in case of success will return a 200 code message
	 * @param id of the user that should the email be sent to
	 * @return
	 */
	@Override
	public Response sendUpdatePasswordEmail(long id){
		try {
			SystemUser user = userBusinessService.get(id);
			userBusinessService.sendUpdatePasswordEmail(new User((io.radien.ms.usermanagement.client.entities.User) user));
			return Response.ok().build();
		} catch(Exception e) {
			return getResponseFromException(e);
		}
	}

	/**
	 * Will update the refresh token, to update the access of the specific user
	 * @param refreshToken to be used
	 * @return Ok message if it has success. Returns error 500 Code to the user in case of any issue.
	 */
	@Override
	public Response refreshToken(String refreshToken) {
		try {
			return Response.ok(userBusinessService.refreshToken(refreshToken)).build();
		} catch(Exception e) {
			return getGenericError(e);
		}
	}
}
