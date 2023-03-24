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
package io.radien.ms.usermanagement.service;

import io.radien.api.entity.Page;
import io.radien.api.model.user.SystemPagedUserSearchFilter;
import io.radien.api.model.user.SystemUser;
import io.radien.api.model.user.SystemUserSearchFilter;
import io.radien.api.service.batch.BatchSummary;
import io.radien.api.service.mail.model.OperationType;
import io.radien.api.service.notification.SQSProducerAccess;
import io.radien.api.service.user.UserServiceAccess;
import io.radien.exception.*;
import io.radien.ms.usermanagement.client.entities.User;
import io.radien.ms.usermanagement.client.entities.UserPasswordChanging;
import io.radien.ms.usermanagement.client.exceptions.RemoteResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.radien.ms.usermanagement.entities.UserEntity;

import java.text.MessageFormat;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.radien.api.SystemVariables.LOGON;
import static io.radien.api.SystemVariables.NEW_PASSWORD;
import static io.radien.api.SystemVariables.OLD_PASSWORD;
import static io.radien.exception.GenericErrorCodeMessage.INVALID_VALUE_FOR_PARAMETER;


/**
 * User service requests between the rest services and the db
 *
 * @author Nuno Santana
 */
@Stateless
public class UserBusinessService implements Serializable {
	private static final long serialVersionUID = 9136599710056928804L;

	Logger log = LoggerFactory.getLogger(UserBusinessService.class);
	@Inject
	private UserServiceAccess userServiceAccess;
	@Inject
	private KeycloakBusinessService keycloakBusinessService;
	@Inject
	private SQSProducerAccess notificationService;

	/**
	 * Method to request a specific user id by a given subject
	 * @param sub to be search
	 * @return the user id
	 */
	public Long getUserId(String sub) {
		Long userId = userServiceAccess.getUserId(sub);
		if(userId != null) {
			return userId;
		}
		throw new UserNotFoundException(MessageFormat.format("User for sub {0} not found.", sub));
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
	public Page<? extends SystemUser> getAll(SystemPagedUserSearchFilter search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending){
		return userServiceAccess.getAll(search,pageNo,pageSize,sortBy,isAscending);
	}

	/**
	 * Retrieves multiple users into a response in base of a search filter criteria
	 * @param filter of the fields to be search
	 * @return Ok message if it has success. Returns error 500 Code to the user in case of resource is not existent.
	 */
	public List<? extends SystemUser> getUsers(SystemUserSearchFilter filter){
		return userServiceAccess.getUsers(filter);
	}

	/**
	 * Returns the system user with the specific required information search by the user ID.
	 * @param id to be search
	 * @return Ok message if it has success.
	 * @throws UserNotFoundException Returns error 404 Code to the user in case of resource is not existent.
	 */
	public SystemUser get(long id) {
		SystemUser systemUser = userServiceAccess.get(id);
		if(systemUser == null){
			throw new UserNotFoundException(MessageFormat.format("User for ID {0} not fount", id));
		}
		return systemUser;
	}

	/**
	 * Returns a list of system users with the specific required information search by the user IDs.
	 * @param ids to be search
	 * @return Ok message if it has success. Returns error 404 Code to the user in case of resource is not existent.
	 */
	public List<SystemUser> get(List<Long> ids) {
		return userServiceAccess.get(ids);
	}

	/**
	 * Deletes a unique user selected by his id.
	 * @param id of the user to be deleted.
	 */
	public void delete(long id) {
		SystemUser u = userServiceAccess.get(id);
		if (!u.isProcessingLocked()) {
			if(u.getSub() != null) {
				keycloakBusinessService.deleteUser(u.getSub());
			}
			userServiceAccess.delete(id);
			sendNotification(u, OperationType.DELETION);
		}  else {
			throw new ProcessingLockedException();
		}
	}

	/**
	 * Creates an User
	 * @param user resource to be created
	 * @param skipKeycloak boolean to skip creation on keycloak
	 * @throws BadRequestException on empty logon, email, first and last name
	 * @throws UniquenessConstraintException if duplicated info already present
	 */
	public void create(User user, boolean skipKeycloak) throws UniquenessConstraintException {
		basicValidation(user);
		userServiceAccess.create(user);
		if(!skipKeycloak) {
			try {
				user.setSub(keycloakBusinessService.createUser(user));
				userServiceAccess.update(user);
			} catch (RemoteResourceException e) {
				userServiceAccess.delete(user.getId());
				throw e;
			} catch(SystemException e) {
				throw new UserNotFoundException(e.getMessage());
			}
		}
	}

	/**
	 * Updates an User
	 * @param user resource to be updated
	 * @param skipKeycloak boolean to skip updating on keycloak
	 * @throws RemoteResourceException on empty logon, email, first and last name
	 */
	public void update(User user,boolean skipKeycloak) throws UniquenessConstraintException {
		if (!userServiceAccess.get(user.getId()).isProcessingLocked()) {
			user.setProcessingLocked(false);
			handleChanges(user, skipKeycloak);
		} else {
			throw new ProcessingLockedException();
		}
	}

	private void handleChanges(User user, boolean skipKeycloak) throws UniquenessConstraintException{
		basicValidation(user);
		try {
			userServiceAccess.update(user);
		} catch (SystemException e) {
			throw new UserNotFoundException(e.getMessage());
		}
		if(!skipKeycloak){
			try{
				keycloakBusinessService.updateUser(user);
			} catch (RemoteResourceException e) {
				delete(user.getId());
				throw e;
			}
		}
	}

	public void processingLockChange(long id, boolean processingLock) throws UniquenessConstraintException{
		User user = new UserEntity((User) userServiceAccess.get(id));
		user.setProcessingLocked(processingLock);
		handleChanges(user, user.isDelegatedCreation());
	}

	/**
	 *
	 * @param user object to be validated
	 * @throws BadRequestException in case passed object does not contain all necessary information
	 */
	protected void basicValidation(SystemUser user) {
		if(user.getLogon().isEmpty()){
			throw new BadRequestException("logon cannot be empty");
		}
		if(user.getUserEmail().isEmpty()){
			throw new BadRequestException("email cannot be empty");
		}

		if(user.getFirstname()!=null && user.getFirstname().isEmpty()){
			throw new BadRequestException("firstname cannot be empty");
		}

		if(user.getLastname()!=null && user.getLastname().isEmpty()){
			throw new BadRequestException("lastname cannot be empty");
		}
	}

	/**
	 * Creates in a batch mode all the requested given users existent in the given list
	 * @param users to be created
	 * @return a batch summary with the resumed information with success or failure
	 */
	public BatchSummary create(List<? extends SystemUser> users) {
		return userServiceAccess.create(users);
	}

	/**
	 * Method to request the keycloak client to send new updated password to the specific user
	 * @param user to be reset password and sent email
	 * @throws RemoteResourceException exceptions that may occur during the execution of a remote method call.
	 */
	public void sendUpdatePasswordEmail(User user) {
		boolean creation = user.getId() == null;
		if(!creation){
			keycloakBusinessService.sendUpdatePasswordEmail(user);
		}
	}

	/**
	 * Method to request the keycloak client to send new updated email to verify for the specific user and
	 * Updates user email attribute in the db
	 * @param user user object update email info
	 * @param emailVerify boolean flag
	 * @throws RemoteResourceException exceptions that may occur during the execution of a remote method call.
	 * @throws UserNotFoundException if user identity cannot be found in database
	 * @throws UniquenessConstraintException if relevant user data already exists in database
	 */
	public void updateEmailAndExecuteActionEmailVerify(User user, boolean emailVerify) throws UniquenessConstraintException {
		try {
			userServiceAccess.update(user);
			keycloakBusinessService.updateEmailAndExecuteActionEmailVerify(user.getUserEmail(), user.getSub(), emailVerify);
		} catch (SystemException e) {
			throw new UserNotFoundException(e.getMessage());
		}
	}

	/**
	 * Request to refresh the user access token
	 * @param refreshToken for validation if the access token can be refresh
	 * @return the new access token
	 * @throws RemoteResourceException exceptions that may occur during the execution of a remote method call.
	 */
	public String refreshToken(String refreshToken) {
		return keycloakBusinessService.refreshToken(refreshToken);
	}

	/**
	 * Perform business logic for changing password process
	 * @param subject user identifier from the perspective of Identity provider (keycloak)
	 * @param change contains information necessary to perform password changing
	 * @throws BadRequestException thrown in case of any issue regarding changing password business rules
	 * @throws RemoteResourceException thrown in case of any issue regarding communication with KeyCloak service
	 */
	public void changePassword(String subject, UserPasswordChanging change) {
		if (change.getLogin() == null) {
			throw new BadRequestException(INVALID_VALUE_FOR_PARAMETER.
					toString(LOGON.getLabel()));
		}
		if (change.getOldPassword() == null) {
			throw new BadRequestException(INVALID_VALUE_FOR_PARAMETER.
					toString(OLD_PASSWORD.getLabel()));
		}
		if (change.getNewPassword() == null) {
			throw new BadRequestException(INVALID_VALUE_FOR_PARAMETER.
					toString(NEW_PASSWORD.getLabel()));
		}

		if (!change.validatePassword()) {
			throw new BadRequestException(change.getValidationErrors().toString());
		}

		keycloakBusinessService.validateChangeCredentials(change.getLogin(), subject,
				change.getOldPassword(), change.getNewPassword());
	}

	public Long count() {
		return userServiceAccess.count();
	}


	private void sendNotification(SystemUser user, OperationType operation){
		String email = user.getUserEmail();
		try {
			Map<String, String> args = new HashMap<>();
			args.put("operation", operation.getOperation());
			args.put("operation_complete", operation.getOperationPasteSimple());
			notificationService.emailNotification(email, "email-11", "en", args);
		} catch (Exception e) {
			log.error("An exception has occurred when attempting to send an email change request. Stack trace: {}", e.toString());
		}
	}
}
