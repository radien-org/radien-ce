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
package io.radien.ms.usermanagement.client.entities;

import io.radien.api.model.user.AbstractUserModel;
import io.radien.api.model.user.SystemUser;

import java.util.Date;

public class User extends AbstractUserModel implements SystemUser {

	private static final long serialVersionUID = -3532886874455311100L;

	private Long id;

	private String logon;
	private String userEmail;
	private String firstname;
	private String lastname;
	private String sub;
	private Date terminationDate;
	private boolean enabled;
	private boolean delegatedCreation;

	public User(){}

	public User(User u) {
		this.id = u.getId();
		this.logon = u.getLogon();
		this.userEmail = u.getUserEmail();
		this.firstname = u.getFirstname();
		this.lastname = u.getLastname();
		this.sub = u.getSub();
		this.terminationDate = u.getTerminationDate();
		this.enabled = u.enabled;
		this.setCreateDate(u.getCreateDate());
		this.setCreateUser(u.getCreateUser());
		this.setLastUpdateUser(u.getLastUpdateUser());
		this.setLastUpdate(u.getLastUpdate());
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public String getLogon() {
		return logon;
	}

	public void setLogon(String logon) {
		this.logon = logon;
	}

	public Date getTerminationDate() {
		return terminationDate;
	}

	public void setTerminationDate(Date terminationDate) {
		this.terminationDate = terminationDate;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public String getSub() {
		return sub;
	}

	@Override
	public void setSub(String sub) {
		this.sub = sub;
	}

	public boolean isDelegatedCreation() {
		return delegatedCreation;
	}

	public void setDelegatedCreation(boolean delegatedCreation) {
		this.delegatedCreation = delegatedCreation;
	}
}
