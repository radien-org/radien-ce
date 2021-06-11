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
package io.radien.ms.permissionmanagement.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.TableGenerator;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.UniqueConstraint;

/**
 * Contains the definitions of a Permission described on
 * {@link io.radien.ms.permissionmanagement.client.entities.Permission}
 * plus the JPA entity mapping
 *
 * @author Newton Carvalho
 */
@Entity
@Table(name = "PERM01", uniqueConstraints = @UniqueConstraint(columnNames = {"actionId", "resourceId"}))
public class Permission extends io.radien.ms.permissionmanagement.client.entities.Permission {

	/**
	 * Entity Permission empty constructor
	 */
	public Permission() {}

	/**
	 * Entity Permission constructor
	 * @param p {@link io.radien.ms.permissionmanagement.client.entities.Permission} permission to be created
	 */
	public Permission(io.radien.ms.permissionmanagement.client.entities.Permission p) {
		super(p);
	}

	/**
	 * Permission id table field
	 * @return permission id
	 */
	@Override
	@Id
	@TableGenerator(name = "GEN_SEQ_PERM01", allocationSize = 2000)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "GEN_SEQ_PERM01")
	public Long getId() {
		return super.getId();
	}

	/**
	 * Permission name table field
	 * @return permission name
	 */
	@Override
	@Column(name = "NAME", nullable = false)
	public String getName() {
		return super.getName();
	}

	/**
	 * Permission action id table field
	 * @return the permission action id
	 */
	@Override
	@Column
	public Long getActionId() {
		return super.getActionId();
	}

	/**
	 * Permission resource id table field
	 * @return permission resource id
	 */
	@Override
	@Column
	public Long getResourceId() { return super.getResourceId(); }
}
