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
package io.radien.ms.usermanagement.legacy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Model;
import javax.inject.Inject;
import java.io.Serializable;

/**
 * Class responsible for managing the current user session
 *
 * @author Bruno Gama
 */
public @Model @SessionScoped class UserSession implements Serializable {

	private static final long serialVersionUID = 1198636791261091733L;
	private static final Logger log = LoggerFactory.getLogger(UserSession.class);
	private static final Marker audit = MarkerFactory.getMarker("AUDIT");

	@Inject
	protected SessionHandler sessionHandler;

	protected SystemUser user;

	public boolean isLoggedIn() {
		return user != null;
	}

	/**
	 * Verifies if this is a valid session by validating if a user exists on it
	 *
	 * @return true if this session is valid
	 * @throws SessionNotValidException
	 * thrown if session is not valid
	 */
	public boolean isValid() throws SessionNotValidException {
		try {
			return isLoggedIn();
		} catch (Exception e) {
			log.error("Error verifying user validity", e);
			throw new SessionNotValidException(e);
		}

	}
	public SystemUser getUser(){
		return getUser(false);
	}

	public SystemUser getUser(boolean forLog) {
		if ( user == null ) {
			try {
				user = sessionHandler.getUser();
				//TODO : CHECK, IN SOME CASES; USEROBJECT DOES NOT HAVE CORRECT LOGON
				if ( user == null && !forLog) {
					log.info("USER IS NULL");
				} else if(!forLog){
					log.info("USER LOGGED IN {}", user);
					if ( user.getLogon() == null ) {
						log.info("USER LOGON IS NULL");
					} else {
						log.info("usersession initialized for {}", user.getLogon());
					}
				}
			} catch (Exception e) {
				log.error("error on initialization", e);
			}
		}

		return user;
	}
}
