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
package io.radien.ms.tenantmanagement.client.entities;

import io.radien.api.model.tenant.SystemActiveTenantSearchFilter;
import io.radien.api.search.SearchFilterCriteria;

/**
 * Encapsulates the parameters applied to search for active tenants
 * @author Bruno Gama
 */
public class ActiveTenantSearchFilter extends SearchFilterCriteria implements SystemActiveTenantSearchFilter {

    private Long userId;
    private Long tenantId;

    /**
     * Active Tenant search filter constructor
     * @param userId of the active tenant to be found
     * @param tenantId of the active tenant to be found
     * @param tenantName of the active tenant to be found
     * @param isTenantActive true if the user has the following tenant has active
     * @param isLogicalConjunction true in case search option is and conjunction
     */
    public ActiveTenantSearchFilter(Long userId, Long tenantId, boolean isLogicalConjunction) {
        super(isLogicalConjunction);
        this.userId = userId;
        this.tenantId = tenantId;
    }

    /**
     * System active tenant search filter user id getter
     * @return the system active tenant search filter user id
     */
    @Override
    public Long getUserId() {
        return userId;
    }

    /**
     * System active tenant search filter user id setter
     * @param userId to be set
     */
    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * System active tenant search filter tenant id getter
     * @return the system active tenant search filter tenant id
     */
    @Override
    public Long getTenantId() {
        return tenantId;
    }

    /**
     * System active tenant search filter tenant id setter
     * @param tenantId to be set
     */
    @Override
    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }
}
