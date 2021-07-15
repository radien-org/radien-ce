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
package io.radien.ms.rolemanagement.services;

import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.TenantRoleException;

import io.radien.api.service.tenantrole.TenantRoleServiceAccess;
import io.radien.api.service.tenantrole.TenantRoleUserServiceAccess;

import io.radien.exception.TenantRoleUserException;
import java.io.Serializable;

import java.util.Collection;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
/**
 * TenantRoleUserBusinessService bridge between REST Services and
 * Persistence layer of TenantRoleUserService
 *
 * @author Rajesh Gavvala
 */
@Stateless
public class TenantRoleUserBusinessService implements Serializable {
    private static final long serialVersionUID = -5658845596283229172L;

    @Inject
    private TenantRoleServiceAccess tenantRoleServiceAccess;

    @Inject
    private TenantRoleUserServiceAccess tenantRoleUserServiceAccess;

    /**
     * Given a tenant and a role ids, retrieves the existent Tenant Role ids
     * @param tenantId Tenant id
     * @param roleIds Collection of Role ids
     * @return Collection of TenantRole ids
     */
    private List<Long> getTenantRoleIds(Long tenantId, Collection<Long> roleIds) throws TenantRoleException {
        List<Long> tenantRoleIds = tenantRoleServiceAccess.getTenantRoleIds(tenantId, roleIds);
        if(tenantRoleIds.isEmpty()){
            throw new TenantRoleException(GenericErrorCodeMessage.TENANT_ROLE_ASSOCIATION_TENANT_ROLES.toString());
        }
        return tenantRoleIds;
    }

    /**
     * Unassigned UserTenant Role(s)
     * @param userId User id
     * @param tenantId Tenant id
     * @param roleIds Collection of Role ids
     * @throws TenantRoleException if any error
     */
    public void unAssignUserTenantRoles(Long userId, Long tenantId, Collection<Long> roleIds) throws TenantRoleException, TenantRoleUserException
    {
        List<Long> tenantRoleIds = getTenantRoleIds(tenantId, roleIds);
        Collection<Long> tenantRoleUserIds = tenantRoleUserServiceAccess.getTenantRoleUserIds(tenantRoleIds, userId);
        if(tenantRoleUserIds.isEmpty()){
            throw new TenantRoleUserException(GenericErrorCodeMessage.TENANT_ROLE_ASSOCIATION_TENANT_ROLES.toString());
        }
        this.tenantRoleUserServiceAccess.delete(tenantRoleUserIds);
    }
}
