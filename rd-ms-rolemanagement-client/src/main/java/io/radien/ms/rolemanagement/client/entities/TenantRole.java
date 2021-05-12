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
import io.radien.api.model.tenantrole.SystemTenantRole;

/**
 * Entity that corresponds to an association between Tenant and Role
 * @author Newton Carvalho
 */
public class TenantRole extends AbstractModel implements SystemTenantRole {

    private Long id;
    private Long tenantId;
    private Long roleId;

    /**
     * Tenant Role empty constructor
     */
    public TenantRole(){
    }

    /**
     * Tenant role constructor
     * @param tenantRole information to be created
     */
    public TenantRole(TenantRole tenantRole) {
        this.id = tenantRole.getId();
        this.roleId = tenantRole.getRoleId();
        this.tenantId = tenantRole.getTenantId();
    }

    /**
     * Tenant Role id getter
     * @return the tenant role id
     */
    @Override
    public Long getId() {
        return id;
    }

    /**
     * Tenant Role id setter
     * @param id to be set and update
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Tenant Role tenant id getter
     * @return the tenant role tenant id
     */
    @Override
    public Long getTenantId() {
        return tenantId;
    }

    /**
     * Tenant Role tenant id setter
     * @param tenantId to be set and updated
     */
    @Override
    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * Tenant Role role id getter
     * @return the tenant role role id
     */
    @Override
    public Long getRoleId() {
        return roleId;
    }

    /**
     * Tenant Role role id setter
     * @param roleId to be set and updated
     */
    @Override
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}
