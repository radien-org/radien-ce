/*
 * Copyright (c) 2021-present radien GmbH & its legal owners. All rights reserved.
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
package io.radien.api.service.tenantrole;

import io.radien.api.entity.Page;
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.model.tenantrole.SystemTenantRoleUser;
import io.radien.exception.SystemException;
import java.util.Collection;
import java.util.List;

/**
 * Rest service client responsible to Deal with TenantRoleUser endpoint
 *
 * @author Newton Carvalho
 */
public interface TenantRoleUserRESTServiceAccess {

    /**
     * Under a pagination approach, retrieves the Users associations that currently exist
     * @param tenantId tenant identifier for a TenantRole (Acting as filter)
     * @param roleId role identifier for a TenantRole (Acting as filter)
     * @param pageNo page number
     * @param pageSize page size
     * @return Page containing TenantRoleUser instances
     * @throws SystemException in case of any error
     */
    Page<? extends SystemTenantRoleUser> getUsers(Long tenantId, Long roleId, int pageNo, int pageSize) throws SystemException;

    /**
     * Under a pagination approach, retrieves the Ids for Users associations that exist
     * for a TenantRole
     * (Invokes the core method counterpart and handles TokenExpiration error)
     * @param tenantId tenant identifier for a TenantRole (Acting as filter)
     * @param roleId role identifier for a TenantRole (Acting as filter)
     * @param pageNo page number
     * @param pageSize page size
     * @return Page containing TenantRoleUser instances
     * @throws SystemException in case of any error
     */
    Page<Long> getUsersIds(Long tenantId, Long roleId, int pageNo, int pageSize) throws SystemException;


    /**
     * Assign/associate/add user to a TenantRole domain
     * The association will always be under a specific role
     * @param tenantRoleUser association between Tenant, Role and User
     * @return Boolean indicating if operation was concluded successfully
     * @throws SystemException in case of any error
     */
    Boolean assignUser(SystemTenantRoleUser tenantRoleUser) throws SystemException;

    /**
     * (Un)Assign/Dissociate/remove user from a TenantRole domain
     * @param tenantId Tenant identifier (Mandatory)
     * @param roleIds Roles identifiers
     * @param userId User identifier (Mandatory)
     * @return Boolean indicating if operation was concluded successfully
     * @throws SystemException in case of any error
     */
    Boolean unAssignUser(Long tenantId, Collection<Long> roleIds, Long userId) throws SystemException;

    /**
     * (Un)Assign/Dissociate/remove user from a TenantRole domain
     * Simply deletes a TenantRoleUser that eventually exists
     * @param tenantRoleUserId identifier that maps a TenantRoleUser entity
     * @throws SystemException in case of any error
     */
    Boolean delete(Long tenantRoleUserId) throws SystemException;

    /**
     * Retrieves the existent Tenants for a User (Optionally for a specific role)
     * @param userId User identifier
     * @param roleId Role identifier (Optional)
     * @return List containing tenants
     * @throws SystemException in case of any error
     */
    List<? extends SystemTenant> getTenants(Long userId, Long roleId) throws SystemException;
}
