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
package io.radien.webapp;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Class responsible for managing the current user session
 *
 * @author Marco Weiland
 */
public @Model @SessionScoped class UserSession implements Serializable {

	private static final long serialVersionUID = 1198636791261091733L;
	private static final Logger log = LoggerFactory.getLogger(UserSession.class);
	
	private String userIdSubject;
	private String email;
	private String preferredUserName; 
	private String userFullName;
	
	@PostConstruct
	private void init() {
		userIdSubject = null;
	}
	
	public void login(String userIdSubject,String email, String preferredUserName, String userFullName) {
		log.info("user logged in: {}", userIdSubject);
		this.userIdSubject = userIdSubject;
		this.email = email;
		this.userFullName = userFullName;
		this.preferredUserName = (preferredUserName != null ? preferredUserName : email);
		
	}
	
	public boolean isActive() {
		return (userIdSubject != null && !userIdSubject.equalsIgnoreCase(""));
	}


	/**
	 * @return the userIdSubject
	 */
	public String getUserIdSubject() {
		return userIdSubject;
	}


	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}


	/**
	 * @return the preferredUserName
	 */
	public String getPreferredUserName() {
		return preferredUserName;
	}


	/**
	 * @return the userFullName
	 */
	public String getUserFullName() {
		return userFullName;
	}

}
