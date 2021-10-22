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
package io.radien.ms.rolemanagement.client.entities;

import io.radien.api.model.tenantrole.SystemTenantRoleUserSearchFilter;
import io.radien.api.search.SearchFilterCriteria;

/**
 * Filter bean that encapsulates the necessary parameters to search/retrieve
 * Tenant role user Associations
 * @author Newton Carvalho
 */
public class TenantRoleUserSearchFilter extends SearchFilterCriteria implements SystemTenantRoleUserSearchFilter {

    private Long tenantRoleId;
    private Long userId;

    /**
     * Tenant Role User Search Filter empty constructor
     */
    public TenantRoleUserSearchFilter(){}

    /**
     * Tenant Role User Search Filter constructor with fields
     * @param tenantRoleId to be search for
     * @param userId to be search for
     * @param isExact true in case search option should be exact
     * @param isLogicalConjunction true in case search option is and conjunction
     */
    public TenantRoleUserSearchFilter(Long tenantRoleId, Long userId, boolean isExact, boolean isLogicalConjunction) {
        super(isExact, isLogicalConjunction);
        this.tenantRoleId = tenantRoleId;
        this.userId = userId;
    }

    /**
     * Tenant Role User Search Filter tenant role get id
     * @return tenant role id value
     */
    @Override
    public Long getTenantRoleId() {
        return tenantRoleId;
    }

    /**
     * Tenant Role User Search Filter getter of role id
     * @param tenantRoleId to be set and update
     */
    @Override
    public void setTenantRoleId(Long tenantRoleId) {
        this.tenantRoleId = tenantRoleId;
    }

    /**
     * Tenant Role User Search Filter user get id
     * @return user id value
     */
    @Override
    public Long getUserId() {
        return userId;
    }

    /**
     * Tenant Role User Search Filter user id setter
     * @param userId to be set and update
     */
    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
