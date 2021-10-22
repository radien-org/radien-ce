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
package io.radien.api.model.tenant;

/**
 * System Active Tenant search filter interface class
 *
 * @author Bruno Gama
 */
public interface SystemActiveTenantSearchFilter {

    /**
     * System active tenant search filter user id getter
     * @return the system active tenant search filter user id
     */
    public Long getUserId();

    /**
     * System active tenant search filter user id setter
     * @param userId to be set
     */
    public void setUserId(Long userId);

    /**
     * System active tenant search filter tenant id getter
     * @return the system active tenant search filter tenant id
     */
    public Long getTenantId();

    /**
     * System active tenant search filter tenant id setter
     * @param tenantId to be set
     */
    public void setTenantId(Long tenantId);

    /**
     * System active tenant search filter tenant name getter
     * @return the system active tenant search filter tenant name
     */
    public String getTenantName();

    /**
     * System active tenant search filter tenant name setter
     * @param tenantName to be set
     */
    public void setTenantName(String tenantName);

    /**
     * System active tenant is tenant active
     * @return system active tenant is tenant active
     */
    public boolean getIsTenantActive();

    /**
     * System active tenant is tenant active setter
     * @param isTenantActive to be set
     */
    public void setIsTenantActive(boolean isTenantActive);
}
