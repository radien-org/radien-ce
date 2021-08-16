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
 * Tenant Role association with JPA descriptors
 * @author Newton Carvalho
 */
@Entity
@Table(name = "TNT_ROL01", uniqueConstraints = @UniqueConstraint(columnNames = {"tenantId, roleId"}))
public class TenantRoleEntity extends io.radien.ms.rolemanagement.client.entities.TenantRole {

	/**
	 * Tenant Role entity empty constructor
	 */
	public TenantRoleEntity(){ }

	/**
	 * Tenant Role entity constructor
	 * @param tenantRole {@link io.radien.ms.rolemanagement.client.entities.TenantRole} to be added/created
	 */
	public TenantRoleEntity(io.radien.ms.rolemanagement.client.entities.TenantRole tenantRole){
		super(tenantRole);
	}

	/**
	 * Tenant Role id table field
	 * @return the tenant role id
	 */
	@Id
	@TableGenerator(name = "GEN_SEQ_TNT_ROL01", allocationSize = 100)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "GEN_SEQ_TNT_ROL01")
	@Override
	public Long getId() {
		return super.getId();
	}

	/**
	 * Tenant Role tenant id table field
	 * @return tenant role tenant id
	 */
	@Column
	@Override
	public Long getTenantId() {
		return super.getTenantId();
	}

	/**
	 * Tenant Role role id table field
	 * @return tenant role role id
	 */
	@Column
	@Override
	public Long getRoleId() {
		return super.getRoleId();
	}
}
