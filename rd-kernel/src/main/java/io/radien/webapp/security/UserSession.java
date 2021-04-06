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
package io.radien.webapp.security;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.security.TokensPlaceHolder;
import io.radien.api.service.linked.authorization.LinkedAuthorizationRESTServiceAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.radien.api.OAFAccess;
import io.radien.api.model.user.SystemUser;
import io.radien.api.security.UserSessionEnabled;
import io.radien.api.service.user.UserRESTServiceAccess;
import io.radien.ms.usermanagement.client.services.UserFactory;

import java.util.List;
import java.util.Optional;


/**
 * Class responsible for managing the current user session
 *
 * @author Marco Weiland
 */
public @Named @SessionScoped class UserSession implements UserSessionEnabled, TokensPlaceHolder {

	private static final long serialVersionUID = 1198636791261091733L;
	private static final Logger log = LoggerFactory.getLogger(UserSession.class);

	@Inject
	private UserRESTServiceAccess userClientService;

	@Inject
	private LinkedAuthorizationRESTServiceAccess authorizationRESTServiceAccess;

	@Inject
	private OAFAccess oaf;

	private SystemUser user;

	private String accessToken;

	private String refreshToken;

	private SystemUser selectedUser;
	private boolean validationTrue;

	private SystemTenant currentTenant;
	private List<String> roles;

	@PostConstruct
	private void init() {
		log.info("Session initiated");
	}
	
	public void login(String userIdSubject,String email, String preferredUserName, String givenname,String familyName,String accessToken, String refreshToken) throws Exception {
		log.info("User session login starting");
		log.info("user logged in: {}", userIdSubject);
		//TODO:		refresh access token if needed
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;

		Optional<SystemUser> existingUser = userClientService.getUserBySub(userIdSubject);
		SystemUser user;
		if(!existingUser.isPresent()){
			user = UserFactory.create(givenname,familyName, preferredUserName,userIdSubject,email,getOAF().getSystemAdminUserId());
			userClientService.create(user,true);
		} else {
			user = existingUser.get();
		}
		this.user = user;

	}
	
	public boolean isActive() {
		return user!=null && user.isEnabled();
	}

	/**
	 * @return the userIdSubject
	 */
	public String getUserIdSubject() {
		return user.getSub();
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return user.getUserEmail();
	}

	/**
	 * @return the preferredUserName
	 */
	public String getPreferredUserName() {
		log.info("get preferred user Name");
		return user.getLogon() != null ? user.getLogon() : user.getUserEmail();
	}

	/**
	 * @return the userFirstName
	 */
	public String getUserFirstName() {
		log.info("get First Name");
		return user.getFirstname();
	}

	/**
	 * @return the userLastName
	 */
	public String getUserLastName() {
		log.info("get Last Name");
		return user.getLastname();
	}

	/**
	 * @return the userLastName
	 */
	public String getUserFullName() {
		log.info("get Full Name");
		return user.getFirstname() + " " +user.getLastname();
	}

	@Override
	public OAFAccess getOAF() {
		return oaf;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}


	public SystemUser getSelectedUser() {
		return selectedUser;
	}

	public void setSelectedUser(SystemUser selectedUser) {
		this.selectedUser = selectedUser;
	}

	public boolean isValidationTrue() {
		return validationTrue;
	}

	public void setValidationTrue(boolean validationTrue) {
		this.validationTrue = validationTrue;
	}

	public SystemTenant getCurrentTenant() {
		return currentTenant;
	}

	public void setCurrentTenant(SystemTenant currentTenant) {
		this.currentTenant = currentTenant;
	}

	public String getCurrentTenantDescription() {
		StringBuilder sb = new StringBuilder();
		if (currentTenant != null) {
			sb.append(currentTenant.getName());
			sb.append(" - ");
			sb.append(currentTenant.getKey());
			sb.append(" - ");
			sb.append(currentTenant.getType().getName());
		}
		return sb.toString();
	}

	public List<String> getRoles() {
		return this.roles;
	}

	public boolean hasRole(SystemTenant tenant, String roleName) {
		Long tenantId = tenant != null ? tenant.getId() : null;
		try {
			return this.authorizationRESTServiceAccess.isRoleExistentForUser(this.user.getId(), tenantId, roleName);
		} catch (Exception e) {
			log.error("Error during role checking", e);
			return false;
		}
	}

	public boolean hasRole(String roleName) {
		return hasRole(getCurrentTenant(), roleName);
	}

	public boolean hasGrant(Long roleId, Long permissionId, Long tenantId) {
		try {
			return this.authorizationRESTServiceAccess.checkIfLinkedAuthorizationExists(tenantId,
					permissionId, roleId, this.user.getId());
		} catch (Exception e) {
			log.error("Error during granting checking", e);
			return false;
		}
	}
}
