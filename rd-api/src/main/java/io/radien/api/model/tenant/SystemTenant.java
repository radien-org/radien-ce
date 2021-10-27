/*
 * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
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
package io.radien.api.model.tenant;

import io.radien.api.Model;

import java.time.LocalDate;

/**
 * Class that represents an tenant
 *
 * @author Santana
 */
public interface SystemTenant extends Model {

	/**
	 * System tenant key getter
	 * @return system tenant key
	 */
	public String getTenantKey();

	/**
	 * System tenant key setter
	 * @param tenantKey to be set
	 */
	public void setTenantKey(String tenantKey);

	/**
	 * System tenant name getter
	 * @return system tenant name
	 */
	public String getName();

	/**
	 * System tenant name setter
	 * @param name to be set
	 */
	public void setName(String name);

	/**
	 * System tenant start date getter
	 * @return system tenant start date
	 */
	public LocalDate getTenantStart();

	/**
	 * System tenant start date setter
	 * @param tenantStart to be set
	 */
	public void setTenantStart(LocalDate tenantStart);

	/**
	 * System tenant end date getter
	 * @return system tenant end date
	 */
	public LocalDate getTenantEnd();

	/**
	 * System tenant end date setter
	 * @param tenantEnd to be set
	 */
	public void setTenantEnd(LocalDate tenantEnd);

	/**
	 * System tenant type getter
	 * @return system tenant type
	 */
	public SystemTenantType getTenantType();

	/**
	 * System tenant type setter
	 * @param tenantType to be set
	 */
	public void setTenantType(SystemTenantType tenantType);

	/**
	 * System tenant client address getter
	 * @return system tenant client address
	 */
	public String getClientAddress();

	/**
	 * System tenant client address setter
	 * @param clientAddress to be set
	 */
	public void setClientAddress(String clientAddress);

	/**
	 * System tenant client zip code getter
	 * @return system tenant client zip code
	 */
	public String getClientZipCode();

	/**
	 * System tenant client zip code setter
	 * @param clientZipCode to be set
	 */
	public void setClientZipCode(String clientZipCode);

	/**
	 * System tenant client city getter
	 * @return system tenant client city
	 */
	public String getClientCity();

	/**
	 * System tenant client city setter
	 * @param clientCity to be set
	 */
	public void setClientCity(String clientCity);

	/**
	 * System tenant client country getter
	 * @return system tenant client country
	 */
	public String getClientCountry();

	/**
	 * System tenant client country setter
	 * @param clientCountry to be set
	 */
	public void setClientCountry(String clientCountry);

	/**
	 * System tenant client phone number getter
	 * @return system tenant client phone number
	 */
	public Long getClientPhoneNumber();

	/**
	 * System tenant client phone number setter
	 * @param clientPhoneNumber to be set
	 */
	public void setClientPhoneNumber(Long clientPhoneNumber);

	/**
	 * System tenant client email getter
	 * @return system tenant client email
	 */
	public String getClientEmail();

	/**
	 * System tenant client email setter
	 * @param clientEmail to be set
	 */
	public void setClientEmail(String clientEmail);

	/**
	 * System tenant parent id getter
	 * @return system tenant parent id
	 */
	public Long getParentId();

	/**
	 * System tenant parent id setter
	 * @param parentId to be set
	 */
	public void setParentId(Long parentId);

	/**
	 * System tenant client id getter
	 * @return system tenant client id
	 */
	public Long getClientId();

	/**
	 * System tenant client id setter
	 * @param clientId to be set
	 */
	public void setClientId(Long clientId);
}
