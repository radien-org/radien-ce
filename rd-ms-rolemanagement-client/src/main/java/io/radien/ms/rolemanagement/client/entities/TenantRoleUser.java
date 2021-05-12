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
import io.radien.api.model.tenantrole.SystemTenantRoleUser;

/**
 * Entity that corresponds to an association between Tenant, Role and User
 * @author Newton Carvalho
 */
public class TenantRoleUser extends AbstractModel implements SystemTenantRoleUser {

    private Long id;
    private Long tenantRoleId;
    private Long userId;

    /**
     * Tenant Role user empty constructor
     */
    public TenantRoleUser(){
    }

    /**
     * Tenant Role User constructor
     * @param tenantRoleUser information to be created
     */
    public TenantRoleUser(TenantRoleUser tenantRoleUser) {
        this.id = tenantRoleUser.id;
        this.tenantRoleId = tenantRoleUser.tenantRoleId;
        this.userId = tenantRoleUser.userId;
    }

    /**
     * Tenant role user id getter
     * @return the tenant role user id
     */
    @Override
    public Long getId() {
        return id;
    }

    /**
     * Tenant role user id setter
     * @param id to be set or updated
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Tenant role user tenant role id getter
     * @return the tenant role user role id
     */
    @Override
    public Long getTenantRoleId() {
        return tenantRoleId;
    }

    /**
     * Tenant role user Tenant Role Id setter
     * @param tenantRoleId to be set or updated
     */
    @Override
    public void setTenantRoleId(Long tenantRoleId) {
        this.tenantRoleId = tenantRoleId;
    }

    /**
     * Tenant role user user id getter
     * @return the tenant role user user id
     */
    @Override
    public Long getUserId() {
        return userId;
    }

    /**
     * Tenant role user user id setter
     * @param userId to be set or updated
     */
    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
