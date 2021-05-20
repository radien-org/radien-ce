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

import javax.persistence.*;

/**
 * JPA entity representing a user in the openappframe system
 *
 * @author Marco Weiland
 */

@Entity
@Table(name = "USR01")
public class User extends io.radien.ms.usermanagement.client.entities.User {

	private static final long serialVersionUID = -3532886874455311100L;

	public User(){ }

	public User(io.radien.ms.usermanagement.client.entities.User user){
		super(user);
	}
	@Id
	@TableGenerator(name = "GEN_SEQ_USR01", allocationSize = 2000)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "GEN_SEQ_USR01")
	@Override
	public Long getId() {
		return super.getId();
	}

	@Column(unique = true)
	@Override
	public String getLogon() {
		return super.getLogon();
	}

	@Column(unique = true)
	@Override
	public String getUserEmail() {
		return super.getUserEmail();
	}

	@Column
	@Override
	public String getFirstname() {
		return super.getFirstname();
	}

	@Column
	@Override
	public String getLastname() {
		return super.getLastname();
	}

	@Column
	@Override
	public boolean isEnabled() {
		return super.isEnabled();
	}

	@Column(unique = true)
	@Override
	public String getSub() {
		return super.getSub();
	}

	@Override
	public Long getActiveTenant() {
		return super.getActiveTenant();
	}

	@Column
	@Override
	public Date getTerminationDate() {
		return super.getTerminationDate();
	}
}
