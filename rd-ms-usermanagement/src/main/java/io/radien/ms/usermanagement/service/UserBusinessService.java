/*
 * Copyright (c) 2021-present radien GmbH. All rights reserved.
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
import io.radien.api.model.user.SystemUser;
import io.radien.api.model.user.SystemUserSearchFilter;
import io.radien.api.service.batch.BatchSummary;
import io.radien.api.service.user.UserServiceAccess;
import io.radien.exception.UniquenessConstraintException;
import io.radien.exception.UserNotFoundException;
import io.radien.ms.usermanagement.client.entities.User;
import io.radien.ms.usermanagement.client.exceptions.RemoteResourceException;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

/**
 * User service requests between the rest services and the db
 *
 * @author Nuno Santana
 */
@Stateless
public class UserBusinessService implements Serializable {

	private static final long serialVersionUID = 9136599710056928804L;

	@Inject
	private UserServiceAccess userServiceAccess;
	@Inject
	private KeycloakService keycloakService;

	/**
	 * Method to request a specific user id by a given subject
	 * @param sub to be search
	 * @return the user id
	 */
	public Long getUserId(String sub) {
		return userServiceAccess.getUserId(sub);
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
	public Page<? extends SystemUser> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending){
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
	 * @return Ok message if it has success. Returns error 404 Code to the user in case of resource is not existent.
	 */
	public SystemUser get(long id) throws UserNotFoundException {
		return userServiceAccess.get(id);
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
	public void delete(long id) throws UserNotFoundException, RemoteResourceException {
		SystemUser u =userServiceAccess.get(id);
		if(u.getSub() != null){
			keycloakService.deleteUser(u.getSub());
		}
		userServiceAccess.delete(id);
	}

	/**
	 * Saves an User (Creation or Update).
	 * @param user resource to be created or update
	 * @param skipKeycloak boolean to skip creation on keycloak
	 * @throws RemoteResourceException on empty logon, email, first and last name
	 */
	public void save(User user,boolean skipKeycloak) throws UniquenessConstraintException, UserNotFoundException, RemoteResourceException {
		if(user.getLogon().isEmpty()){
			//according to current keycloak config
			throw new RemoteResourceException("logon cannot be empty");
		}
		if(user.getUserEmail().isEmpty()){
			//for the user to be able to login he needs to be able to set password
			throw new RemoteResourceException("email cannot be empty");
		}

		if(user.getFirstname()!=null && user.getFirstname().isEmpty()){
			throw new RemoteResourceException("firstname cannot be empty");
		}

		if(user.getLastname()!=null && user.getLastname().isEmpty()){
			throw new RemoteResourceException("lastname cannot be empty");
		}

		boolean creation = user.getId() == null;
		userServiceAccess.save(user);
		if(creation && !skipKeycloak){
			try {
				user.setSub(keycloakService.createUser(user));
			} catch (RemoteResourceException e) {
				userServiceAccess.delete(user.getId());
				throw e;
			}
		} else if(!skipKeycloak){
			try{
				keycloakService.updateUser(user);
			}catch (RemoteResourceException e){
				delete(user.getId());
				throw e;
			}
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
	public void sendUpdatePasswordEmail(User user) throws RemoteResourceException {
		boolean creation = user.getId() == null;
		if(!creation){
			keycloakService.sendUpdatePasswordEmail(user);
		}
	}

	/**
	 * Method to request the keycloak client to send new updated email to verify for the specific user
	 * @param user to be update email
	 * @throws RemoteResourceException exceptions that may occur during the execution of a remote method call.
	 */
	public void updateUserEmailAndExecuteActionEmailVerify(User user) throws RemoteResourceException {
		boolean creation = user.getId() == null;
		if(!creation){
			keycloakService.updateUserEmailAndExecuteActionEmailVerify(user);
		}
	}

	/**
	 * Request to refresh the user access token
	 * @param refreshToken for validation if the access token can be refresh
	 * @return the new access token
	 * @throws RemoteResourceException exceptions that may occur during the execution of a remote method call.
	 */
	public String refreshToken(String refreshToken) throws RemoteResourceException {
		return keycloakService.refeshToken(refreshToken);
	}

}
