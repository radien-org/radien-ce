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

import io.radien.api.model.tenantrole.SystemTenantRolePermissionSearchFilter;

/**
 * Filter bean that encapsulates the necessary parameters to search/retrieve
 * Tenant role permission Associations
 *
 * @author Newton Carvalho
 */
public class TenantRolePermissionSearchFilter implements SystemTenantRolePermissionSearchFilter {

    private Long tenantRoleId;
    private Long permissionId;
    private boolean isExact;
    private boolean isLogicConjunction;

    /**
     * Tenant Role Permission Search Filter empty constructor
     */
    public TenantRolePermissionSearchFilter(){}

    /**
     * Tenant Role Permission Search Filter constructor with given fields
     * @param tenantRoleId to be search
     * @param permissionId to be search
     * @param isExact to be search
     * @param isLogicalConjunction true in case search option is and conjunction
     */
    public TenantRolePermissionSearchFilter(Long tenantRoleId, Long permissionId, boolean isExact, boolean isLogicalConjunction) {
        this.tenantRoleId = tenantRoleId;
        this.permissionId = permissionId;
        this.isExact = isExact;
        this.isLogicConjunction = isLogicalConjunction;
    }

    /**
     * Tenant Role search filter get role id
     * @return role id for search filter
     */
    @Override
    public Long getTenantRoleId() {
        return tenantRoleId;
    }

    /**
     * Tenant Role Permission tenant Role id set
     * @param tenantRoleId to be set and replace
     */
    @Override
    public void setTenantRoleId(Long tenantRoleId) {
        this.tenantRoleId = tenantRoleId;
    }

    /**
     * Tenant Role search filter get permission id
     * @return permission id for search filter
     */
    @Override
    public Long getPermissionId() {
        return permissionId;
    }

    /**
     * Tenant Role Permission permission id set
     * @param permissionId to be set and replace
     */
    @Override
    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
    }

    /**
     * Tenant Role Permission search filter get is exact search
     * @return true or false value
     */
    @Override
    public boolean isExact() {
        return isExact;
    }

    /**
     * Tenant Role Permission set exact
     * @param exact to be set and updated
     */
    @Override
    public void setExact(boolean exact) {
        isExact = exact;
    }

    /**
     * Tenant Role Permission search filter get is logical conjunction
     * @return true or false value
     */
    @Override
    public boolean isLogicConjunction() {
        return isLogicConjunction;
    }

    /**
     * Tenant Role Permission set is logic conjunction
     * @param logicConjunction to be set and updated
     */
    @Override
    public void setLogicConjunction(boolean logicConjunction) {
        isLogicConjunction = logicConjunction;
    }
}
