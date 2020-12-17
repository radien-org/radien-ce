/*

	Copyright (c) 2016-present openappframe.org & its legal owners. All rights reserved.

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.

 */
package io.radien.persistence.entities.user;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import io.radien.api.model.identity.SystemIdentity;
import io.radien.api.model.user.AbstractUserModel;
import io.radien.api.model.user.SystemUser;
import io.radien.persistence.entities.identity.Identity;

/**
 * JPA entity representing a user in the openappframe system
 *
 * @author Marco Weiland
 */

@Entity
@Table(name = "USR01")
public class User extends AbstractUserModel implements SystemUser {

	private static final long serialVersionUID = -3532886874455311100L;

	@Id
	@SequenceGenerator(name = "GEN_SEQ_USR01", sequenceName = "SEQ_USR01", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_SEQ_USR01")
	private Long id;

	@Column(unique = true)
	private String logon;
	@Column(unique = true)
	private String userEmail;
	@Column
	private String password;

	@Column
	private Date terminationDate;
	@Column
	private Date createDate;
	@Column
	private Long createUser;
	@Column
	private Date lastUpdate;
	@Column
	private Long lastUpdateUser;
	@Column
	private boolean acceptedTermsAndConditions;
	@Column
	private boolean enabled;
	
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "identityId")
	private Identity identityId;

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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getTerminationDate() {
		return terminationDate;
	}

	public void setTerminationDate(Date terminationDate) {
		this.terminationDate = terminationDate;
	}

	@Override
	public SystemIdentity getIdentity() {
		return identityId;
	}

	@Override
	public void setIdentity(SystemIdentity systemIdentity) {
		identityId = (Identity) systemIdentity;
	}

	public void setIdentityId(Identity identity) {
		this.identityId = identity;
	}

	@Transient
	public String getFullName() {
		return identityId.getFullname();
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public boolean isAcceptedTermsAndConditions() {
		return acceptedTermsAndConditions;
	}

	public void setAcceptedTermsAndConditions(boolean acceptedTermsAndConditions) {
		this.acceptedTermsAndConditions = acceptedTermsAndConditions;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
