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
public class Tenant extends io.radien.ms.tenantmanagement.client.entities.Tenant {

	private static final long serialVersionUID = 307978393054123750L;

	public Tenant(){ }

	public Tenant(io.radien.ms.tenantmanagement.client.entities.Tenant tenant){
		super(tenant);
	}
	@Id
	@TableGenerator(name = "GEN_SEQ_TNT01", allocationSize = 100)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "GEN_SEQ_TNT01")
	@Override
	public Long getId() {
		return super.getId();
	}

	@Column(unique = true)
	@Override
	public String getName() {
		return super.getName();
	}

	@Column
	@Override
	public String getKey() {
		return super.getKey();
	}

	@Column
	@Override
	@Convert(converter = TenantTypeConverter.class)
	public TenantType getType() {
		return super.getType();
	}

	@Column
	@Override
	public LocalDate getStart() {
		return super.getStart();
	}

	@Column
	@Override
	public LocalDate getEnd() {
		return super.getEnd();
	}

	@Column
	@Override
	public String getClientAddress() {
		return super.getClientAddress();
	}

	@Column
	@Override
	public String getClientZipCode() {
		return super.getClientZipCode();
	}

	@Column
	@Override
	public String getClientCity() {
		return super.getClientCity();
	}

	@Column
	@Override
	public String getClientCountry() {
		return super.getClientCountry();
	}

	@Column
	@Override
	public Long getClientPhoneNumber() {
		return super.getClientPhoneNumber();
	}

	@Column
	@Override
	public String getClientEmail() {
		return super.getClientEmail();
	}

	@Column
	@Override
	public Long getParentId() {
		return super.getParentId();
	}

	@Column
	@Override
	public Long getClientId() {
		return super.getClientId();
	}
}
