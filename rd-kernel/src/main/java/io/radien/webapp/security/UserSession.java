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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.radien.api.OAFAccess;
import io.radien.api.model.user.SystemUser;
import io.radien.api.security.UserSessionEnabled;
import io.radien.api.service.user.UserRESTServiceAccess;
import io.radien.ms.usermanagement.client.services.UserFactory;


/**
 * Class responsible for managing the current user session
 *
 * @author Marco Weiland
 */
public @SessionScoped class UserSession implements UserSessionEnabled {

	private static final long serialVersionUID = 1198636791261091733L;
	private static final Logger log = LoggerFactory.getLogger(UserSession.class);

	@Inject
	private UserRESTServiceAccess userClientService;

	@Inject
	private OAFAccess oaf;
	
	private SystemUser user;

	
	@PostConstruct
	private void init() {

	}
	
	public void login(String userIdSubject,String email, String preferredUserName, String givenname,String familyName) throws Exception {
		log.info("user logged in: {}", userIdSubject);
//		Optional<SystemUser> existingUser = userClientService.getUserBySub(userIdSubject);
		SystemUser user;
//		if(!existingUser.isPresent()){
			user = UserFactory.create(givenname,familyName, preferredUserName,userIdSubject,email,getOAF().getSystemAdminUserId());
			userClientService.create(user);
//		} else {
//			user = existingUser.get();
//		}
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
		return user.getLogon() != null ? user.getLogon() : user.getUserEmail();
	}


	/**
	 * @return the userFullName
	 */
	public String getUserFullName() {
		return user.getFirstname() + " " + user.getLastname();
	}

	@Override
	public OAFAccess getOAF() {
		return oaf;
	}

}
