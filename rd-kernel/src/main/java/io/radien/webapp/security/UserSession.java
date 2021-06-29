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
import io.radien.exception.SystemException;
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
	/**
	 * Initialization of the user session
	 */
	@PostConstruct
	private void init() {
		log.info("Session initiated");
	}

	/**
	 * Method for taking care and processing the login of the current active user and his session
	 * @param userIdSubject user subject identifier
	 * @param email user email
	 * @param preferredUserName preferred user selected name
	 * @param givenname user name
	 * @param familyName user last name
	 * @param accessToken public access token for authorization purposes
	 * @param refreshToken private access token for refreshing the public access token
	 * @throws Exception in case of any issue while performing the login, starting the user session or validating the
	 * user information
	 */
	public void login(String userIdSubject,String email, String preferredUserName, String givenname,String familyName,String accessToken, String refreshToken) throws Exception {
		log.info("User session login starting");
		log.info("user logged in: {}", userIdSubject);
		//TODO:		refresh access token if needed
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		try {
			Optional<SystemUser> existingUser = userClientService.getUserBySub(userIdSubject);
			SystemUser user;
			if (!existingUser.isPresent()) {
				user = UserFactory.create(givenname, familyName, preferredUserName, userIdSubject, email, getOAF().getSystemAdminUserId());
				userClientService.create(user, true);
				Optional<SystemUser> userBySub = userClientService.getUserBySub(userIdSubject);
				if(userBySub.isPresent()) {
					user = userBySub.get();
				}
			} else {
				user = existingUser.get();
			}
			this.user = user;
		} catch (SystemException exception){
			log.error(exception.getMessage());
		}
		if (this.user == null){
			this.user = UserFactory.create(givenname,familyName,preferredUserName, userIdSubject,email,-1L);
		}

	}

	/**
	 * User session user object getter
	 * @return the active logged in user
	 */
	@Override
	public SystemUser getUser() {
		return user;
	}

	/**
	 * User session setter for systemUser
	 * @param user systemUser object
	 */
	public void setUser(SystemUser user) {
		this.user = user;
	}

	/**
	 * User session id getter
	 * @return the user id
	 */
	public Long getUserId() { return user != null ? user.getId() : null; }

	/**
	 * Validates if the user session is still active or not
	 * @return true if user is still active
	 */
	public boolean isActive() {
		return user!=null && user.isEnabled();
	}

	/**
	 * User session subject getter
	 * @return the userIdSubject
	 */
	public String getUserIdSubject() {
		return user.getSub();
	}

	/**
	 * User session email getter
	 * @return the email
	 */
	public String getEmail() {
		return user.getUserEmail();
	}

	/**
	 * User session prefered user name getter
	 * @return the preferredUserName
	 */
	public String getPreferredUserName() {
		log.info("get preferred user Name");
		return user.getLogon() != null ? user.getLogon() : user.getUserEmail();
	}

	/**
	 * User session first name getter
	 * @return the userFirstName
	 */
	public String getUserFirstName() {
		log.info("get First Name");
		return user.getFirstname();
	}

	/**
	 * User session user last name
	 * @return the userLastName
	 */
	public String getUserLastName() {
		log.info("get Last Name");
		return user.getLastname();
	}

	/**
	 * User session user full name getter
	 * @return the userFullName
	 */
	public String getUserFullName() {
		log.info("get Full Name");
		return user.getFirstname() + " " +user.getLastname();
	}

	/**
	 * Gets the current OAF access
	 * @return the oaf object
	 */
	@Override
	public OAFAccess getOAF() {
		return oaf;
	}

	/**
	 * User session access token getter
	 * @return the user access token
	 */
	public String getAccessToken() {
		return accessToken;
	}

	/**
	 * User session access token setter
	 * @param accessToken to be set
	 */
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	/**
	 * User session refresh token getter
	 * @return user refresh token
	 */
	public String getRefreshToken() {
		return refreshToken;
	}

	/**
	 * User session refresh token setter
	 * @param refreshToken to be set
	 */
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}
