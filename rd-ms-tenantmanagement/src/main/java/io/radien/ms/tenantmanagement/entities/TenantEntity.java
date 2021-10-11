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
package io.radien.ms.tenantmanagement.entities;

import io.radien.ms.tenantmanagement.client.entities.TenantType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Convert;
import java.time.LocalDate;

/**
 * JPA entity representing a Tenant
 *
 * @author Santana
 */
@Entity
@Table(name = "TNT01")
public class TenantEntity extends io.radien.ms.tenantmanagement.client.entities.Tenant {

	private static final long serialVersionUID = 307978393054123750L;

	/**
	 * Tenant entity empty constructor
	 */
	public TenantEntity(){ }

	/**
	 * Tenant entity constructor
	 * @param tenant {@link io.radien.ms.tenantmanagement.client.entities.Tenant} to be created/added
	 */
	public TenantEntity(io.radien.ms.tenantmanagement.client.entities.Tenant tenant){
		super(tenant);
	}

	/**
	 * Tenant id table field getter
	 * @return the tenant id
	 */
	@Id
	@TableGenerator(name = "GEN_SEQ_TNT01", allocationSize = 100)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "GEN_SEQ_TNT01")
	@Override
	public Long getId() {
		return super.getId();
	}

	/**
	 * Tenant name table field getter
	 * @return the tenant name
	 */
	@Column(unique = true)
	@Override
	public String getName() {
		return super.getName();
	}

	/**
	 * Tenant key table field getter
	 * @return the tenant key
	 */
	@Column
	@Override
	public String getTenantKey() {
		return super.getTenantKey();
	}

	/**
	 * Tenant type table field getter
	 * @return the tenant type
	 */
	@Column
	@Override
	@Convert(converter = TenantTypeConverter.class)
	public TenantType getTenantType() {
		return super.getTenantType();
	}

	/**
	 * Tenant start date table field getter
	 * @return the tenant start date
	 */
	@Column
	@Override
	public LocalDate getTenantStart() {
		return super.getTenantStart();
	}

	/**
	 * Tenant end date table field getter
	 * @return the tenant end date
	 */
	@Column
	@Override
	public LocalDate getTenantEnd() {
		return super.getTenantEnd();
	}

	/**
	 * Tenant client address table field getter
	 * @return the tenant client address
	 */
	@Column
	@Override
	public String getClientAddress() {
		return super.getClientAddress();
	}

	/**
	 * Tenant client zip code table field getter
	 * @return the tenant client zip code
	 */
	@Column
	@Override
	public String getClientZipCode() {
		return super.getClientZipCode();
	}

	/**
	 * Tenant client city table field getter
	 * @return the tenant client city
	 */
	@Column
	@Override
	public String getClientCity() {
		return super.getClientCity();
	}

	/**
	 * Tenant Client country table field getter
	 * @return the tenant client country
	 */
	@Column
	@Override
	public String getClientCountry() {
		return super.getClientCountry();
	}

	/**
	 * Tenant Client phone number table field getter
	 * @return tenant client phone number
	 */
	@Column
	@Override
	public Long getClientPhoneNumber() {
		return super.getClientPhoneNumber();
	}

	/**
	 * Tenant client email table field getter
	 * @return the tenant client email
	 */
	@Column
	@Override
	public String getClientEmail() {
		return super.getClientEmail();
	}

	/**
	 * Tenant parent id table field getter
	 * @return the tenant parent id
	 */
	@Column
	@Override
	public Long getParentId() {
		return super.getParentId();
	}

	/**
	 * Tenant client id table field getter
	 * @return the tenant client id
	 */
	@Column
	@Override
	public Long getClientId() {
		return super.getClientId();
	}
}
