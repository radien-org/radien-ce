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

import io.radien.api.model.tenantrole.SystemTenantRoleUserSearchFilter;

/**
 * Filter bean that encapsulates the necessary parameters to search/retrieve
 * Tenant role user Associations
 * @author Newton Carvalho
 */
public class TenantRoleUserSearchFilter implements SystemTenantRoleUserSearchFilter {

    private Long tenantRoleId;
    private Long userId;
    private boolean isExact;
    private boolean isLogicConjunction;

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
        this.tenantRoleId = tenantRoleId;
        this.userId = userId;
        this.isExact = isExact;
        this.isLogicConjunction = isLogicalConjunction;
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

    /**
     * Tenant Role User Search Filter get is exact
     * @return true or false
     */
    @Override
    public boolean isExact() {
        return isExact;
    }

    /**
     * Tenant Role User Search Filter is exact setter
     * @param exact to be set and replace
     */
    @Override
    public void setExact(boolean exact) {
        isExact = exact;
    }

    /**
     * Tenant Role User Search Filter get is logical conjunction
     * @return true or false
     */
    @Override
    public boolean isLogicConjunction() {
        return isLogicConjunction;
    }

    /**
     * Tenant Role User Search Filter is logicConjunction setter
     * @param logicConjunction to be set and replace
     */
    @Override
    public void setLogicConjunction(boolean logicConjunction) {
        isLogicConjunction = logicConjunction;
    }
}
