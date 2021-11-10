/*
 * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
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
import io.radien.api.model.tenantrole.SystemTenantRole;
import io.radien.api.model.tenantrole.SystemTenantRoleSearchFilter;
import io.radien.api.service.ServiceAccess;

import io.radien.exception.TenantRoleException;
import io.radien.exception.UniquenessConstraintException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Describes a contract for a Repository responsible to perform operations over Tenant and Role association
 *
 * @author Newton Carvalho
 */
public interface TenantRoleServiceAccess extends ServiceAccess {

    /**
     * Gets all the tenant role associations into a pagination mode.
     * @param pageNo of the requested information. Where the tenant role association is.
     * @param pageSize total number of pages returned in the request.
     * @return a page of system tenant role associations.
     */
    Page<SystemTenantRole> getAll(int pageNo, int pageSize);

    /**
     * Gets specific tenant role association by the id
     * @param tenantRoleId to be searched for
     * @return the requested system tenant role association
     */
    SystemTenantRole get(Long tenantRoleId) ;

    /**
     * Gets a list of system tenant role associations requested by a search filter
     * @param filter information to search
     * @return a list of system tenants
     */
    List<? extends SystemTenantRole> get(SystemTenantRoleSearchFilter filter);

    /**
     * Saves (Creates/Update) a system tenant role association based on the given information
     * @param tenantRole role association information to be created
     * @throws UniquenessConstraintException in case of duplicates
     */
    void save(SystemTenantRole tenantRole) throws UniquenessConstraintException;

    /**
     * Check if a role is already assigned/associated with a tenant
     * @param roleId Role identifier
     * @param tenantId Tenant Identifier
     * @return true if already exists, otherwise returns false
     */
    boolean isAssociationAlreadyExistent(Long roleId, Long tenantId);

    /**
     * Deletes a requested tenant role association
     * @param tenantRoleId tenant role to be deleted
     * @return true in case of success, false otherwise
     * @throws TenantRoleException in case of any inconsistency found
     */
    boolean delete(Long tenantRoleId) throws TenantRoleException;

    /**
     * Retrieves the Permissions that exists for a Tenant Role Association (Optionally taking in account user)
     * @param tenantId Tenant identifier (Mandatory)
     * @param roleId Role identifier (Mandatory)
     * @param userId User identifier (Optional)
     * @return permission ids
     */
    List<Long> getPermissions(Long tenantId, Long roleId, Long userId);

    /**
     * Retrieves the existent Tenants for a User (Optionally for a specific role)
     * @param userId User identifier
     * @param roleId Role identifier (Optional)
     * @return List containing tenant ids
     */
    List<Long> getTenants(Long userId, Long roleId);

    /**
     * Retrieves the existent Roles for a User of a specific Tenant
     * @param userId User identifier
     * @param tenantId Tenant identifier
     * @return List containing role ids
     */
    List<Long> getRoleIdsForUserTenant(Long userId, Long tenantId);

    /**
     * Check if a User has some Role (Optionally for a specific Tenant)
     * @param userId User identifier
     * @param roleNames Role name identifier
     * @param tenantId Tenant identifier
     * @return true if has some of the informed roles, otherwise fase
     */
    boolean hasAnyRole(Long userId, List<String> roleNames, Long tenantId);

    /**
     * Check if a User has a Permission (Optionally for a specific Tenant)
     * @param userId User identifier
     * @param permissionId Permission identifier
     * @param tenantId Tenant identifier (Optional)
     * @return true if has some of the informed roles, otherwise fase
     */
    boolean hasPermission(Long userId, Long permissionId, Long tenantId);

    /**
     * Retrieves strictly the TenantRole id basing on tenant and role
     * @param tenant tenant identifier
     * @param  role role identifier
     * @return TenantRole id
     */
    Optional<Long> getTenantRoleId(Long tenant, Long role);

    /**
     * Retrieves strictly the TenantRole ids based on tenant and role(s)
     * @param tenantId Tenant id
     * @param roleIds Collection Role ids
     * @return List of TenantRole ids
     * @throws TenantRoleException in case of any inconsistency found
     */
    List<Long> getTenantRoleIds(Long tenantId, Collection<Long> roleIds) throws TenantRoleException;
}
