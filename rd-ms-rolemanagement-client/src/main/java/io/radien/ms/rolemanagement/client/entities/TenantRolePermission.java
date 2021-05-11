/*
	Copyright (c) 2016-present openappframe.org & its legal owners. All rights reserved.

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
 */
package io.radien.ms.rolemanagement.client.entities;

import io.radien.api.model.AbstractModel;
import io.radien.api.model.tenantrole.SystemTenantRolePermission;

/**
 * Entity that corresponds to an association between Tenant, Role and Permission
 * @author Newton Carvalho
 */
public class TenantRolePermission extends AbstractModel implements SystemTenantRolePermission {

    private Long id;
    private Long tenantRoleId;
    private Long permissionId;

    /**
     * Tenant Role Permission empty constructor
     */
    public TenantRolePermission(){
    }

    /**
     * Tenant role permission constructor
     * @param tenantRolePermission information to be created
     */
    public TenantRolePermission(TenantRolePermission tenantRolePermission) {
        this.id = tenantRolePermission.id;
        this.tenantRoleId = tenantRolePermission.tenantRoleId;
        this.permissionId = tenantRolePermission.permissionId;
    }

    /**
     * Tenant role permission id getter
     * @return tenant role permission id
     */
    @Override
    public Long getId() {
        return id;
    }

    /**
     * Tenant role permission id setter
     * @param id to be set and updated
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Tenant role permission tenant role id getter
     * @return tenant role permission tenant role id
     */
    @Override
    public Long getTenantRoleId() {
        return tenantRoleId;
    }

    /**
     * Tenant role permission tenant role id setter
     * @param tenantRoleId to be set or updated
     */
    @Override
    public void setTenantRoleId(Long tenantRoleId) {
        this.tenantRoleId = tenantRoleId;
    }

    /**
     * Tenant role permission permission id getter
     * @return tenant role permission permission id
     */
    @Override
    public Long getPermissionId() {
        return permissionId;
    }

    /**
     * Tenant role permission permission id setter
     * @param permissionId to be set or updated
     */
    @Override
    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
    }
}
