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
package io.radien.persistence.entities.identity;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import io.radien.api.model.identity.AbstractIdentityModel;
import io.radien.api.model.identity.SystemIdentity;
import io.radien.persistence.entities.user.User;


/**
 * JPA class representing a user identity, (name information, if the account has
 * been verified, etc)
 *
 * @author Marco Weiland
 */

@Entity
@Table(name = "IDM01")
public class Identity extends AbstractIdentityModel implements SystemIdentity { 

	private static final long serialVersionUID = 7879310637451612484L;

	@Id
	@SequenceGenerator(name = "GEN_SEQ_IDM01", sequenceName = "SEQ_IDM01", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_SEQ_IDM01")
	private Long id;

	@Column
	private String firstname;
	@Column
	private String lastname;
	@Column
	private Date createDate;
	@Column
	private Date lastUpdate;
	@Column
	private Long createUser;
	@Column
	private Long lastUpdateUser;
	
	
	@OneToMany(mappedBy = "identityId")
	private List<User> users;

	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	/**
	 * @return the createUser
	 */
	public Long getCreateUser() {
		return createUser;
	}

	/**
	 * @param createUser the createUser to set
	 */
	public void setCreateUser(Long createUser) {
		this.createUser = createUser;
	}

	/**
	 * @return the lastUpdateUser
	 */
	public Long getLastUpdateUser() {
		return lastUpdateUser;
	}

	/**
	 * @param lastUpdateUser the lastUpdateUser to set
	 */
	public void setLastUpdateUser(Long lastUpdateUser) {
		this.lastUpdateUser = lastUpdateUser;
	}

}
