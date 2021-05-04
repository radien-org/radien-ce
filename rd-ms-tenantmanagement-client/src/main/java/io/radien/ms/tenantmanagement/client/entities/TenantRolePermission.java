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
package io.radien.ms.tenantmanagement.client.entities;

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

    public TenantRolePermission(){
    }

    /**
     * Tenant role constructor
     * @param tenantRolePermission information to be created
     */
    public TenantRolePermission(TenantRolePermission tenantRolePermission) {
        this.id = tenantRolePermission.id;
        this.tenantRoleId = tenantRolePermission.tenantRoleId;
        this.permissionId = tenantRolePermission.permissionId;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
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
}
