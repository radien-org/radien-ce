/*
 * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
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
package io.radien.ms.usermanagement.resource;

import io.radien.api.service.permission.SystemActionsEnum;
import io.radien.api.service.permission.SystemResourcesEnum;
import io.radien.exception.UserNotFoundException;
import io.radien.ms.openid.entities.Authenticated;
import io.radien.ms.openid.entities.Public;
import io.radien.ms.usermanagement.client.entities.User;
import io.radien.ms.usermanagement.client.entities.UserPasswordChanging;
import io.radien.ms.usermanagement.entities.UserEntity;
import io.radien.ms.usermanagement.service.UserBusinessService;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import io.radien.api.model.user.SystemUser;
import io.radien.api.service.batch.BatchSummary;
import io.radien.api.model.user.SystemUserSearchFilter;
import io.radien.api.service.role.SystemRolesEnum;
import io.radien.exception.GenericErrorMessagesToResponseMapper;
import io.radien.exception.SystemException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.usermanagement.util.BatchResponseMapper;
import io.radien.ms.usermanagement.client.entities.UserSearchFilter;
import io.radien.ms.usermanagement.client.services.UserResourceClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Resource class with all the requests to be performed inside the user management
 *
 * @author Nuno Santana
 * @author Bruno Gama
 */
@Path("user")
@RequestScoped
@Authenticated
public class UserResource extends AuthorizationChecker implements UserResourceClient {
	protected static final Logger log = LoggerFactory.getLogger(UserResource.class);

	@Inject
	private UserBusinessService userBusinessService;

	/**
	 * Will create request that by a given user sub will try to retrieve the user id
	 * @param sub that identifies the user
	 * @return Ok message if it has success. Returns error 500 Code to the user in case of resource is not existent.
	 */
	@Override
	public Response getUserIdBySub(String sub) {
		Long id = userBusinessService.getUserId(sub);
		return Response.ok(id).build();
	}

	@Override
	public Response getUserInSession() {
		return Response.ok(
				userBusinessService.get(
						userBusinessService.getUserId(getInvokerUser().getSub())
				)
		).build();
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
				return GenericErrorMessagesToResponseMapper.getForbiddenResponse();
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
	 * @param ids to be found
	 * @param isExact should the search fields be exact or not as given
	 * @param isLogicalConjunction should the query use a and or a or criteria
	 * @return Ok message if it has success. Returns error 500 Code to the user in case of resource is not existent.
	 */
	@Override
	public Response getUsers(String sub, String email, String logon, Collection<Long> ids, boolean isExact, boolean isLogicalConjunction) {
		SystemUserSearchFilter filter = new UserSearchFilter(sub,email,logon,ids,isExact,isLogicalConjunction);
		return Response.ok(userBusinessService.getUsers(filter)).build();
	}

	/**
	 * Returns JSON message with the specific required information search by the user ID.
	 * @param id to be search
	 * @return Ok message if it has success. Returns error 404 Code to the user in case of resource is not existent.
	 */
	public Response getById(Long id) {
		try {
			if (!checkUserRoles()) {
				return GenericErrorMessagesToResponseMapper.getForbiddenResponse();
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
				return GenericErrorMessagesToResponseMapper.getForbiddenResponse();
			}
			userBusinessService.delete(id);
		}  catch (Exception e){
			return getResponseFromException(e);
		}
		return Response.ok().build();
	}

	/**
	 * Creates user into the DB.
	 *
	 * @param user to be added
	 * @return Ok message if it has success. Returns error 400 Code to the user in case of invalid request.
	 */
	public Response create(io.radien.ms.usermanagement.client.entities.User user) {
		try {
			if (userBusinessService.count() != 0 && !isSelfOnboard(user.getSub()) && !hasPermission(null,
					SystemActionsEnum.ACTION_CREATE.getActionName(),
					SystemResourcesEnum.USER.getResourceName())) {
				return GenericErrorMessagesToResponseMapper.getForbiddenResponse();
			}
			userBusinessService.create(new UserEntity(user),user.isDelegatedCreation());
		} catch (Exception e) {
			return getResponseFromException(e);
		}
		return Response.ok().build();
	}

	/**
	 * Updates user to the DB.
	 *
	 * @param user to be added
	 * @return Ok message if it has success. Returns error 400 Code to the user in case of invalid request.
	 */
	public Response update(long id, io.radien.ms.usermanagement.client.entities.User user) {
		try {
			if (!isSelfOnboard(user.getSub()) && !hasPermission(null,
					SystemActionsEnum.ACTION_UPDATE.getActionName(),
					SystemResourcesEnum.USER.getResourceName())) {
				return GenericErrorMessagesToResponseMapper.getForbiddenResponse();
			}
			UserEntity userEntity = new UserEntity(user);
			userEntity.setId(id);
			userBusinessService.update(userEntity,userEntity.isDelegatedCreation());
		}
		catch (Exception e) {
			return getResponseFromException(e);
		}
		return Response.ok().build();
	}


	/**
	 * Will send the updated password via email to the user in case of success will return a 200 code message
	 * @param id of the user that should the email be sent to
	 * @return ok in case the email has been sent with the refreshed password
	 */
	@Override
	public Response sendUpdatePasswordEmail(long id) {
		SystemUser user = userBusinessService.get(id);
		userBusinessService.sendUpdatePasswordEmail(new UserEntity((io.radien.ms.usermanagement.client.entities.User) user));
		return Response.ok().build();
	}

	/**
	 * Will updated email and also send email to verify to the user in case of success will return a 200 code message
	 * @param userId userId of the user to update and execute action email verify
	 * @param user of the user to update an email
	 * @return ok in case the email has been updated with the email verification sent
	 */
	@Override
	public Response updateEmailAndExecuteActionEmailVerify(long userId, User user, boolean emailVerify){
		try {
			SystemUser systemUser = userBusinessService.get(userId);
			systemUser.setUserEmail(user.getUserEmail());
			userBusinessService.updateEmailAndExecuteActionEmailVerify((User) systemUser, emailVerify);
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
	@Public
	public Response refreshToken(String refreshToken) {
		return Response.ok(userBusinessService.refreshToken(refreshToken)).build();
	}

	/**
	 * Endpoint to change user password
	 * @param sub OpenId user identifier (subject)
	 * @param change pojo/bean containing credential information (Not plain text, data encoded on base64)
	 * @return OK if changing process is concluded with success. Status 400 in case of any
	 * business logic inconsistency found. Return error 500 in case of technical issues
	 */
	public Response updatePassword(String sub, UserPasswordChanging change) {
		if (!isSelfOnboard(sub)) {
			return GenericErrorMessagesToResponseMapper.getForbiddenResponse();
		}
		userBusinessService.changePassword(sub, change);
		return Response.ok().build();
	}

	/**
	 * Check if the the current logged user is trying to register
	 * himself into the radien repository
	 * @param sub subject/identifier regarding the current user
	 * @return true  or false
	 */
	public boolean isSelfOnboard(String sub) {
		SystemUser invoker = getInvokerUser();
		return invoker != null && invoker.getSub().equals(sub);
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
	 * Adds multiple users into the DB.
	 *
	 * @param userList of users to be added
	 * @return returns
	 *
	 * OK (Http status 200):
	 * All users were added Some users were not added due found issues
	 * BAD REQUEST (Http status 400): None users were added, were found issues for all them
	 *
	 * For all cases the response must contains the quantity of not added users (not-processed-items),
	 * the found issues and an internal status as well (SUCCESS, PARTIAL_SUCCESS and FAIL).
	 */
	@Override
	public Response create(List<io.radien.ms.usermanagement.client.entities.User> userList) {
		try {
			List<UserEntity> users = userList.stream().map(UserEntity::new).collect(Collectors.toList());
			BatchSummary batchSummary = this.userBusinessService.create(users);
			return BatchResponseMapper.mapToResponse(batchSummary);
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
		try {
			log.error("ERROR: ",e);
			throw e;
		} catch (UserNotFoundException unfe){
			response = GenericErrorMessagesToResponseMapper.getResourceNotFoundException();
		} catch (UniquenessConstraintException uce) {
			return GenericErrorMessagesToResponseMapper.getInvalidRequestResponse(uce.getMessage());
		} catch (Exception et){
			response = GenericErrorMessagesToResponseMapper.getGenericError(et);
		}
		return response;
	}
}
