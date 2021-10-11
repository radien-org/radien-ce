/*
 * Copyright (c) 2021-present radien GmbH & its legal owners. All rights reserved.
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
package io.radien.ms.usermanagement.client.entities;

import io.radien.api.model.user.AbstractUserModel;
import io.radien.api.model.user.SystemUser;

import java.util.Date;

/**
 * User object constructor class
 *
 * @author Bruno Gama
 */
public class User extends AbstractUserModel implements SystemUser {

	private static final long serialVersionUID = 8894482343339525951L;

	private Long id;

	private String logon;
	private String userEmail;
	private String firstname;
	private String lastname;
	private String sub;
	private Date terminationDate;
	private boolean enabled;
	private boolean delegatedCreation;

	/**
	 * User empty constructor
	 */
	public User(){}

	/**
	 * User constructor
	 * @param u information to be constructed
	 */
	public User(User u) {
		this.id = u.getId();
		this.logon = u.getLogon();
		this.userEmail = u.getUserEmail();
		this.firstname = u.getFirstname();
		this.lastname = u.getLastname();
		this.sub = u.getSub();
		if (u.getTerminationDate() != null) {
			this.terminationDate = (Date) u.getTerminationDate().clone();
		}
		this.enabled = u.enabled;
		if (u.getCreateDate() != null) {
			this.setCreateDate((Date) u.getCreateDate().clone());
		}
		if (u.getLastUpdate() != null){
			this.setLastUpdate((Date)u.getLastUpdate().clone());
		}
		this.setCreateUser(u.getCreateUser());
		this.setLastUpdateUser(u.getLastUpdateUser());
	}

	/**
	 * User id getter method
	 * @return the user id
	 */
	@Override
	public Long getId() {
		return id;
	}

	/**
	 * User id setter method
	 * @param id to be set
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * User logon getter method
	 * @return the user logon
	 */
	public String getLogon() {
		return logon;
	}

	/**
	 * User logon setter method
	 * @param logon to be set
	 */
	public void setLogon(String logon) {
		this.logon = logon;
	}

	/**
	 * User termination date getter method
	 * @return the user termination date
	 */
	public Date getTerminationDate() {
		return terminationDate;
	}

	/**
	 * User termination date setter method
	 * @param terminationDate to be set
	 */
	public void setTerminationDate(Date terminationDate) {
		this.terminationDate = terminationDate;
	}

	/**
	 * User email getter method
	 * @return the user email
	 */
	public String getUserEmail() {
		return userEmail;
	}

	/**
	 * User email setter method
	 * @param userEmail to be set
	 */
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	/**
	 * User first name getter method
	 * @return the user first name
	 */
	public String getFirstname() {
		return firstname;
	}

	/**
	 * User first name setter method
	 * @param firstname to be set
	 */
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	/**
	 * User last name getter method
	 * @return the user last name
	 */
	public String getLastname() {
		return lastname;
	}

	/**
	 * User last name setter
	 * @param lastname to be set
	 */
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	/**
	 * Is user enable getter
	 * @return if user is or not enable boolean value
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Is user enable setter method
	 * @param enabled to enable or disable user
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * User subject getter method
	 * @return the user subject unique identifier
	 */
	@Override
	public String getSub() {
		return sub;
	}

	/**
	 * User subject setter method
	 * @param sub to be set
	 */
	@Override
	public void setSub(String sub) {
		this.sub = sub;
	}

	/**
	 * Has user been delegated creation getter method
	 * @return true if user has delegated creation
	 */
	public boolean isDelegatedCreation() {
		return delegatedCreation;
	}

	/**
	 * Has user been delegated creation setter method
	 * @param delegatedCreation to be set
	 */
	public void setDelegatedCreation(boolean delegatedCreation) {
		this.delegatedCreation = delegatedCreation;
	}
}
