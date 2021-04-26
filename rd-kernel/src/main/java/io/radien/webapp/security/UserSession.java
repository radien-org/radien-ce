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

import io.radien.api.security.TokensPlaceHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.radien.api.OAFAccess;
import io.radien.api.model.user.SystemUser;
import io.radien.api.security.UserSessionEnabled;
import io.radien.api.service.user.UserRESTServiceAccess;
import io.radien.ms.usermanagement.client.services.UserFactory;

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
	private OAFAccess oaf;

	private SystemUser user;

	private String accessToken;

	private String refreshToken;

	private SystemUser selectedUser;
	private boolean validationTrue;

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

	/**
	 * @return the user id
	 */
	public Long getUserId() { return user != null ? user.getId() : null; }

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
}
