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
 * @author Newton Carvalho
 */
public class TenantRoleSearchFilter implements SystemTenantRoleSearchFilter {

    private Long tenantId;
    private Long roleId;
    private boolean isExact;
    private boolean isLogicConjunction;

    public TenantRoleSearchFilter() {}

    public TenantRoleSearchFilter(Long tenantId, Long roleId, boolean isExact, boolean isLogicalConjunction) {
        this.tenantId = tenantId;
        this.roleId = roleId;
        this.isExact = isExact;
        this.isLogicConjunction = isLogicalConjunction;
    }

    @Override
    public Long getTenantId() {
        return tenantId;
    }

    @Override
    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public Long getRoleId() {
        return roleId;
    }

    @Override
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    @Override
    public boolean isExact() {
        return isExact;
    }

    @Override
    public void setExact(boolean exact) {
        this.isExact = exact;
    }

    @Override
    public boolean isLogicConjunction() {
        return isLogicConjunction;
    }

    @Override
    public void setLogicConjunction(boolean logicConjunction) {
        this.isLogicConjunction = logicConjunction;
    }
}
