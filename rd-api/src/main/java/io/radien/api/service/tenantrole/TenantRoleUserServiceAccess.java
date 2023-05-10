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
import io.radien.api.model.tenantrole.SystemTenantRoleUser;
import io.radien.api.model.tenantrole.SystemTenantRoleUserSearchFilter;
import io.radien.api.service.ServiceAccess;

import io.radien.exception.InvalidArgumentException;
import io.radien.api.service.role.exception.TenantRoleUserNotFoundException;
import io.radien.exception.UniquenessConstraintException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Describes a contract for a Repository responsible to perform operations over Tenant Role and user association
 *
 * @author Newton Carvalho
 */
public interface TenantRoleUserServiceAccess extends ServiceAccess {

    /**
     * Gets all the tenant role user associations into a pagination mode.
     * @param tenantRoleId search param that corresponds to the TenantRole id (Optional)
     * @param userId search param that corresponds to the user id (Optional)
     * @param pageNo of the requested information. Where the tenant is.
     * @param pageSize total number of pages returned in the request
     * @param sortBy criteria field to be sorted
     * @param isAscending boolean value to show the values ascending or descending way
     * @return a page containing system tenant role user associations.
     */
    Page<SystemTenantRoleUser> getAll(Long tenantRoleId, Long userId, int pageNo, int pageSize,
                                             List<String> sortBy, boolean isAscending);

    /**
     * Gets all the tenant role user associations into a pagination mode.
     * @param tenant search param that corresponds to the TenantRole.tenantId (Optional)
     * @param role search param that corresponds to the TenantRole.roleId (Optional)
     * @param pageNo of the requested information. Where the tenant is.
     * @param pageSize total number of pages returned in the request.
     * @return a page containing system tenant role user associations.
     */
    Page<Long> getAllUserIds(Long tenant, Long role, int pageNo, int pageSize);

    /**
     * Gets specific tenant role user association by the id
     * @param tenantRoleUserId to be searched for
     * @return the requested system tenant role user association
     */
    SystemTenantRoleUser get(Long tenantRoleUserId) ;

    /**
     * Gets a list of system tenant role user associations requested by a search filter
     * @param filter information to search
     * @return a list of system tenants
     */
    List<? extends SystemTenantRoleUser> get(SystemTenantRoleUserSearchFilter filter);

    /**
     * Creates a system tenant role user association based on the given information
     * @param tenantRolePermission role association information to be created
     * @throws UniquenessConstraintException in case of duplicates
     */
    void create(SystemTenantRoleUser tenantRolePermission) throws UniquenessConstraintException, InvalidArgumentException;

    /**
     * UPDATE a Tenant Role User association
     * @param tenantRoleUser role association information to be updated
     * @throws UniquenessConstraintException in case of duplicated fields or records
     * @throws TenantRoleUserNotFoundException in case of not existing a TenantRoleUser for an id
     */
    void update(SystemTenantRoleUser tenantRoleUser)
            throws UniquenessConstraintException, TenantRoleUserNotFoundException, InvalidArgumentException;

    /**
     * Deletes a requested tenant role user association
     * @param tenantRoleUserId tenant role user to be deleted
     * @return true in case of success false in case of any error
     */
    boolean delete(Long tenantRoleUserId);

    /**
     * Gets Ids (of tenant role user associations) for the given parameters
     * @param tenant tenant identifier (mandatory)
     * @param roles roles identifiers
     * @param user user identifier (mandatory)
     * @return list containing ids
     */
    Collection<Long> getTenantRoleUserIds(Long tenant, Collection<Long> roles, Long user) throws InvalidArgumentException;

    List<Long> getTenants(Long userId, Long roleId) throws InvalidArgumentException;

    /**
     * Delete tenant role user associations for given parameters
     * @param ids list containing tenant role user identifiers (mandatory)
     * @return true in case of success, false if no registers could be fetch the informed ids
     */
    boolean delete(Collection<Long> ids) throws InvalidArgumentException;

    /**
     * Check if an user is already assigned/associated with a tenant role
     * @param userId User identifier
     * @param tenantRoleId TenantRole Identifier
     * @return true if already exists, otherwise returns false
     */
    boolean isAssociationAlreadyExistent(Long userId, Long tenantRoleId) throws InvalidArgumentException;

    /**
     * Check if a user is already assigned/associated with a tenant role
     * @param userId User identifier
     * @param tenantRoleId TenantRole Identifier
     * @param id Tenant
     * @return true if already exists, otherwise returns false
     */
    boolean isAssociationAlreadyExistent(Long userId, Long tenantRoleId, Long id) throws InvalidArgumentException;

    /**
     * Retrieves strictly the TenantRoleUser id basing on tenantRole and user
     * @param tenantRole tenant identifier
     * @param user identifier
     * @return TenantRoleUser id
     */
    Optional<Long> getTenantRoleUserId(Long tenantRole, Long user) throws InvalidArgumentException;

    /**
     * Check if a user is associated with a tenant
     * @param userId User identifier
     * @param tenantId Tenant Identifier
     * @return true if already exists, otherwise returns false
     */
    boolean isAssociatedWithTenant(Long userId, Long tenantId) throws InvalidArgumentException;

    /**
     * Retrieves strictly the TenantRoleUser ids based on tenantRoleIds and user
     * @param tenantRoleIds TenantRoleIds
     * @param userId User id
     * @return List of TenantRoleUserIds
     */
    Collection<Long> getTenantRoleUserIds(List<Long> tenantRoleIds, Long userId);

    long count();
}
