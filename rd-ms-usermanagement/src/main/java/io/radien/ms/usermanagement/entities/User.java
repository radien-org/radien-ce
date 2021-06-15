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
package io.radien.ms.usermanagement.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.TableGenerator;
import javax.persistence.GeneratedValue;
import javax.persistence.Column;
import javax.persistence.GenerationType;

/**
 * JPA entity representing a user in the open appframe system
 *
 * @author Marco Weiland
 */
@Entity
@Table(name = "USR01")
public class User extends io.radien.ms.usermanagement.client.entities.User {

	private static final long serialVersionUID = -3532886874455311100L;

	/**
	 * User entity empty constructor
	 */
	public User(){ }

	/**
	 * User entity constructor
	 * @param user {@link io.radien.ms.usermanagement.client.entities.User} to be used/created
	 */
	public User(io.radien.ms.usermanagement.client.entities.User user){
		super(user);
	}

	/**
	 * User entity id table field
	 * @return user id
	 */
	@Id
	@TableGenerator(name = "GEN_SEQ_USR01", allocationSize = 2000)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "GEN_SEQ_USR01")
	@Override
	public Long getId() {
		return super.getId();
	}

	/**
	 * User entity logon table field
	 * @return user logon
	 */
	@Column(unique = true)
	@Override
	public String getLogon() {
		return super.getLogon();
	}

	/**
	 * User entity user email table field
	 * @return user email
	 */
	@Column(unique = true)
	@Override
	public String getUserEmail() {
		return super.getUserEmail();
	}

	/**
	 * User entity firstname table field
	 * @return user firstname
	 */
	@Column
	@Override
	public String getFirstname() {
		return super.getFirstname();
	}

	/**
	 * User entity lastname table field
	 * @return user last name
	 */
	@Column
	@Override
	public String getLastname() {
		return super.getLastname();
	}

	/**
	 * User entity is enable table field
	 * @return is user enable or not
	 */
	@Column
	@Override
	public boolean isEnabled() {
		return super.isEnabled();
	}

	/**
	 * User entity subject table field
	 * @return user subject
	 */
	@Column(unique = true)
	@Override
	public String getSub() {
		return super.getSub();
	}

	/**
	 * User entity active tenant table field
	 * @return user active tenant
	 */
	@Override
	public Long getActiveTenant() {
		return super.getActiveTenant();
	}

	/**
	 * User entity termination date table field
	 * @return user termination date
	 */
	@Column
	@Override
	public Date getTerminationDate() {
		return super.getTerminationDate();
	}
}
