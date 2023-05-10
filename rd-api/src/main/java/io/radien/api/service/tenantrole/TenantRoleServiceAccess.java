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

import io.radien.exception.InvalidArgumentException;
import io.radien.exception.UniquenessConstraintException;

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
     * @param tenantId tenant identifier (Optional)
     * @param roleId role identifier (Optional)
     * @param pageNo of the requested information. Where the tenant role association is.
     * @param pageSize total number of pages returned in the request.
     * @param sortBy sort filter criteria (Optional).
     * @param isAscending ascending filter criteria.
     * @return a page of system tenant role associations.
     */
    Page<SystemTenantRole> getAll(Long tenantId, Long roleId,
                                  int pageNo, int pageSize, List<String> sortBy,
                                  boolean isAscending);

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
     * Creates a Tenant Role association
     * @param tenantRole role association information to be created
     * @throws UniquenessConstraintException in case of duplicated combination of tenant and role
     */
    void create(SystemTenantRole tenantRole) throws UniquenessConstraintException, InvalidArgumentException;

    /**
     * Updates a Tenant Role association
     * @param tenantRole role association information to be updated
     * @throws UniquenessConstraintException in case of duplicated combination of tenant and role
     */
    void update(SystemTenantRole tenantRole) throws UniquenessConstraintException, InvalidArgumentException;

    /**
     * Check if a role is already assigned/associated with a tenant
     * @param roleId Role identifier
     * @param tenantId Tenant Identifier
     * @return true if already exists, otherwise returns false
     */
    boolean isAssociationAlreadyExistent(Long roleId, Long tenantId) throws InvalidArgumentException;

    /**
     * Deletes a requested tenant role association
     * @param tenantRoleId tenant role to be deleted
     * @return true in case of success, false otherwise
     * @throws InvalidArgumentException in case of any inconsistency found
     */
    boolean delete(Long tenantRoleId) throws InvalidArgumentException;

    /**
     * Retrieves the existent Roles for a User of a specific Tenant
     * @param userId User identifier
     * @param tenantId Tenant identifier
     * @return List containing role ids
     */
    List<Long> getRoleIdsForUserTenant(Long userId, Long tenantId) throws InvalidArgumentException;

    /**
     * Check if a User has some Role (Optionally for a specific Tenant)
     * @param userId User identifier
     * @param roleNames Role name identifier
     * @param tenantId Tenant identifier
     * @return true if has some of the informed roles, otherwise fase
     */
    boolean hasAnyRole(Long userId, List<String> roleNames, Long tenantId) throws InvalidArgumentException;

    /**
     * Check if a User has a Permission (Optionally for a specific Tenant)
     * @param userId User identifier
     * @param permissionId Permission identifier
     * @param tenantId Tenant identifier (Optional)
     * @return true if has some of the informed roles, otherwise fase
     */
    boolean hasPermission(Long userId, Long permissionId, Long tenantId) throws InvalidArgumentException;

    /**
     * Retrieves strictly the TenantRole id basing on tenant and role
     * @param tenant tenant identifier
     * @param  role role identifier
     * @return TenantRole id
     */
    Optional<Long> getTenantRoleId(Long tenant, Long role) throws InvalidArgumentException;

    long count();
}
