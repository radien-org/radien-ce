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
package io.radien.ms.tenantmanagement.client.entities;

import io.radien.api.model.tenantrole.SystemTenantRolePermissionSearchFilter;

/**
 * Filter bean that encapsulates the necessary parameters to search/retrieve
 * Tenant role permission Associations
 * @author Newton Carvalho
 */
public class TenantRolePermissionSearchFilter implements SystemTenantRolePermissionSearchFilter {

    private Long tenantRoleId;
    private Long permissionId;
    private boolean isExact;
    private boolean isLogicConjunction;

    public TenantRolePermissionSearchFilter(){}

    public TenantRolePermissionSearchFilter(Long tenantRoleId, Long permissionId, boolean isExact, boolean isLogicalConjunction) {
        this.tenantRoleId = tenantRoleId;
        this.permissionId = permissionId;
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
    public Long getPermissionId() {
        return permissionId;
    }

    @Override
    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
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
