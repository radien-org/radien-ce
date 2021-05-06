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

    public TenantRoleUserSearchFilter(){}

    public TenantRoleUserSearchFilter(Long tenantRoleId, Long userId, boolean isExact, boolean isLogicalConjunction) {
        this.tenantRoleId = tenantRoleId;
        this.userId = userId;
        this.isExact = isExact;
        this.isLogicConjunction = isLogicalConjunction;
    }

    @Override
    public Long getTenantRoleId() {
        return tenantRoleId;
    }

    @Override
    public void setTenantRoleId(Long tenantRoleId) {
        this.tenantRoleId = tenantRoleId;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public boolean isExact() {
        return isExact;
    }

    @Override
    public void setExact(boolean exact) {
        isExact = exact;
    }

    @Override
    public boolean isLogicConjunction() {
        return isLogicConjunction;
    }

    @Override
    public void setLogicConjunction(boolean logicConjunction) {
        isLogicConjunction = logicConjunction;
    }
}
