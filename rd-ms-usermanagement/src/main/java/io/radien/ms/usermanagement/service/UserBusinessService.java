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

import io.radien.api.OAFProperties;
import io.radien.api.entity.Page;
import io.radien.api.model.user.SystemUser;
import io.radien.api.model.user.SystemUserSearchFilter;
import io.radien.api.service.batch.BatchSummary;
import io.radien.api.service.user.UserServiceAccess;
import io.radien.exception.SystemException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.exception.UserNotFoundException;
import io.radien.ms.usermanagement.client.entities.User;
import io.radien.ms.usermanagement.client.exceptions.RemoteResourceException;
import org.keycloak.admin.client.Keycloak;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;

/**
 * @author Nuno Santana
 */

@Stateless
public class UserBusinessService implements Serializable {

	private static final long serialVersionUID = 9136599710056928804L;

	private static final Logger log = LoggerFactory.getLogger(UserBusinessService.class);

	@Inject
	private UserServiceAccess userServiceAccess;
	@Inject
	private KeycloakService keycloakService;

	public Long getUserId(String sub) {
		return userServiceAccess.getUserId(sub);
	}

	public Page<? extends SystemUser> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending){
		return userServiceAccess.getAll(search,pageNo,pageSize,sortBy,isAscending);
	}

	public List<? extends SystemUser> getUsers(SystemUserSearchFilter filter){
		return userServiceAccess.getUsers(filter);
	}

	public SystemUser get(long id) throws UserNotFoundException {
		return userServiceAccess.get(id);
	}
	public List<SystemUser> get(List<Long> ids) {
		return userServiceAccess.get(ids);
	}

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

		if(user.getFirstname().isEmpty()){
			throw new RemoteResourceException("firstname cannot be empty");
		}

		if(user.getLastname().isEmpty()){
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

	public BatchSummary create(List<? extends SystemUser> users) {
		BatchSummary summary = userServiceAccess.create(users);
		return summary;
	}

	public void sendUpdatePasswordEmail(User user) throws RemoteResourceException {
		boolean creation = user.getId() == null;
		if(!creation){
			keycloakService.sendUpdatePasswordEmail(user);
		}
	}

	public String refreshToken(String refreshToken) throws RemoteResourceException {
		return keycloakService.refeshToken(refreshToken);
	}

}
