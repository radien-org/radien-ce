/*
 * Copyright (c) 2021-present radien GmbH. All rights reserved.
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
package io.radien.ms.tenantmanagement.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * JPA entity representing a Active Tenant
 *
 * @author Bruno Gama
 */
@Entity
@Table(name = "ACTTNT01")
public class ActiveTenantEntity extends io.radien.ms.tenantmanagement.client.entities.ActiveTenant {

	private static final long serialVersionUID = -4526437514769444576L;

	/**
	 * Active Tenant entity empty constructor
	 */
	public ActiveTenantEntity(){ }

	/**
	 * Active Tenant entity constructor
	 * @param activeTenant {@link io.radien.ms.tenantmanagement.client.entities.ActiveTenant} to be created/added
	 */
	public ActiveTenantEntity(io.radien.ms.tenantmanagement.client.entities.ActiveTenant activeTenant){
		super(activeTenant);
	}

	/**
	 * Active Tenant id table field getter
	 * @return the active tenant id
	 */
	@Id
	@TableGenerator(name = "GEN_SEQ_ACTTNT01", allocationSize = 100)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "GEN_SEQ_ACTTNT01")
	@Override
	public Long getId() {
		return super.getId();
	}

	/**
	 * Active Tenant user id table field getter
	 * @return active tenant user id
	 */
	@Column
	@Override
	public Long getUserId() {
		return super.getUserId();
	}

	/**
	 * Active Tenant tenant id table field getter
	 * @return active tenant tenant id
	 */
	@Column
	@Override
	public Long getTenantId() {
		return super.getTenantId();
	}
}
