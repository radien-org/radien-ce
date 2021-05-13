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
package io.radien.api.model.tenant;

import io.radien.api.Model;

import java.time.LocalDate;

/**
 * Class that represents an tenant
 *
 * @author Santana
 */
public interface SystemTenant extends Model {

	public String getTenantKey();
	public void setTenantKey(String tenantKey);

	public String getName();
	public void setName(String name);

	public LocalDate getTenantStart();
	public void setTenantStart(LocalDate tenantStart);

	public LocalDate getTenantEnd();
	public void setTenantEnd(LocalDate tenantEnd);

	public SystemTenantType getTenantType();
	public void setTenantType(SystemTenantType tenantType);

	public String getClientAddress();
	public void setClientAddress(String clientAddress);

	public String getClientZipCode();
	public void setClientZipCode(String clientZipCode);

	public String getClientCity();
	public void setClientCity(String clientCity);

	public String getClientCountry();
	public void setClientCountry(String clientCountry);

	public Long getClientPhoneNumber();
	public void setClientPhoneNumber(Long clientPhoneNumber);

	public String getClientEmail();
	public void setClientEmail(String clientEmail);

	public Long getParentId();
	public void setParentId(Long parentId);

	public Long getClientId();
	public void setClientId(Long clientId);
}
