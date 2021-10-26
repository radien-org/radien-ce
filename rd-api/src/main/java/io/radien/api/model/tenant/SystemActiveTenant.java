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

/**
 * Class that represents an active tenant
 *
 * @author Bruno Gama
 */
public interface SystemActiveTenant extends Model {

	/**
	 * System active tenant user id getter
	 * @return system active tenant user id
	 */
	public Long getUserId();

	/**
	 * System active tenant user id setter
	 * @param userId to be set
	 */
	public void setUserId(Long userId);

	/**
	 * System active tenant tenant id getter
	 * @return system active tenant tenant id
	 */
	public Long getTenantId();

	/**
	 * System active tenant tenant id setter
	 * @param tenantId to be set
	 */
	public void setTenantId(Long tenantId);
}
