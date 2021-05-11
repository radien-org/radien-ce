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
package io.radien.ms.rolemanagement.client.entities;

import io.radien.api.model.tenantrole.SystemTenantRoleSearchFilter;

/**
 * Filter bean that encapsulates the necessary parameters to search/retrieve
 * Tenant role Associations
 *
 * @author Newton Carvalho
 */
public class TenantRoleSearchFilter implements SystemTenantRoleSearchFilter {

    private Long tenantId;
    private Long roleId;
    private boolean isExact;
    private boolean isLogicConjunction;

    /**
     * Tenant Role Search Filter empty constructor
     */
    public TenantRoleSearchFilter() {}

    /**
     * Tenant Role Search Filter constructor with fields
     * @param tenantId to be search
     * @param roleId to be search
     * @param isExact true in case search option should be exact
     * @param isLogicalConjunction true in case search option is and conjunction
     */
    public TenantRoleSearchFilter(Long tenantId, Long roleId, boolean isExact, boolean isLogicalConjunction) {
        this.tenantId = tenantId;
        this.roleId = roleId;
        this.isExact = isExact;
        this.isLogicConjunction = isLogicalConjunction;
    }

    /**
     * Tenant Role Search Filter getter of tenant id
     * @return the tenant id value
     */
    @Override
    public Long getTenantId() {
        return tenantId;
    }

    /**
     * Tenant Role Search Filter tenant id setter
     * @param tenantId to be set and update
     */
    @Override
    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * Tenant Role Search Filter getter of role id
     * @return the role id value
     */
    @Override
    public Long getRoleId() {
        return roleId;
    }

    /**
     * Tenant Role Search Filter role id setter
     * @param roleId to be set and update
     */
    @Override
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    /**
     * Tenant Role Search Filter get is exact
     * @return true or false
     */
    @Override
    public boolean isExact() {
        return isExact;
    }

    /**
     * Tenant Role Search Filter is exact setter
     * @param exact to be set and replace
     */
    @Override
    public void setExact(boolean exact) {
        this.isExact = exact;
    }

    /**
     * Tenant Role Search Filter get is logical conjunction
     * @return true or false
     */
    @Override
    public boolean isLogicConjunction() {
        return isLogicConjunction;
    }

    /**
     * Tenant Role Search Filter is logicConjunction setter
     * @param logicConjunction to be set and replace
     */
    @Override
    public void setLogicConjunction(boolean logicConjunction) {
        this.isLogicConjunction = logicConjunction;
    }
}
