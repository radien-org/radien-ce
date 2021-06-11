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
package io.radien.ms.rolemanagement.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.UniqueConstraint;
import javax.persistence.Id;
import javax.persistence.TableGenerator;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;

/**
 * Tenant Role User association with JPA descriptors
 * @author Newton Carvalho
 */
@Entity
@Table(name = "TNT_ROL_USR01", uniqueConstraints = @UniqueConstraint(columnNames = {"tenantRoleId, userId"}))
public class TenantRoleUser extends io.radien.ms.rolemanagement.client.entities.TenantRoleUser {

	/**
	 * Tenant Role User entity empty constructor
	 */
	public TenantRoleUser(){ }

	/**
	 * Tenant Role User entity constructor
	 * @param tenantRoleUser {@link io.radien.ms.rolemanagement.client.entities.TenantRoleUser} to be added/created
	 */
	public TenantRoleUser(io.radien.ms.rolemanagement.client.entities.TenantRoleUser tenantRoleUser){
		super(tenantRoleUser);
	}

	/**
	 * Tenant Role User id table field
	 * @return tenant role user id
	 */
	@Id
	@TableGenerator(name = "GEN_SEQ_TNT_ROL_USR01", allocationSize = 100)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "GEN_SEQ_TNT_ROL_USR01")
	@Override
	public Long getId() {
		return super.getId();
	}

	/**
	 * Tenant Role User tenant role id table field
	 * @return tenant role user tenant role id
	 */
	@Column
	@Override
	public Long getTenantRoleId() {
		return super.getTenantRoleId();
	}

	/**
	 * Tenant Role User user id table field
	 * @return tenant role user user id
	 */
	@Column
	@Override
	public Long getUserId() {
		return super.getUserId();
	}
}
