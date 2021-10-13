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
import io.radien.api.model.tenantrole.SystemTenantRolePermission;
import io.radien.api.model.tenantrole.SystemTenantRolePermissionSearchFilter;
import io.radien.api.service.ServiceAccess;
import io.radien.exception.UniquenessConstraintException;

import java.util.List;
import java.util.Optional;

/**
 * Describes a contract for a Repository responsible
 * to perform operations over Tenant, Role and Permission association
 *
 * @author Newton Carvalho
 */
public interface TenantRolePermissionServiceAccess extends ServiceAccess {

    /**
     * Gets all the tenant role permission associations into a pagination mode.
     * @param tenant search param that corresponds to the TenantRole.tenantId (Optional)
     * @param role search param that corresponds to the TenantRole.roleId (Optional)
     * @param pageNo of the requested information. Where the tenant is.
     * @param pageSize total number of pages returned in the request.
     * @return a page containing system tenant role permission associations.
     */
    Page<SystemTenantRolePermission> getAll(Long tenant, Long role, int pageNo, int pageSize);

    /**
     * Gets specific tenant role permission association by the id
     * @param tenantRolePermissionId to be searched for
     * @return the requested system tenant role permission association
     */
    SystemTenantRolePermission get(Long tenantRolePermissionId) ;

    /**
     * Gets a list of system tenant role permission associations requested by a search filter
     * @param filter information to search
     * @return a list of system tenants
     */
    List<? extends SystemTenantRolePermission> get(SystemTenantRolePermissionSearchFilter filter);

    /**
     * Creates a system tenant role permission association based on the given information
     * @param tenantRolePermission role association information to be created
     * @throws UniquenessConstraintException in case of duplicates
     */
    void create(SystemTenantRolePermission tenantRolePermission) throws UniquenessConstraintException;

    /**
     * Deletes a requested tenant role permission association
     * @param tenantRolePermissionId tenant role permission to be deleted
     * @return true in case of success false in case of any error
     */
    boolean delete(Long tenantRolePermissionId);

    /**
     * Check if a permission is already assigned/associated with a tenant role
     * @param permissionId Permission identifier
     * @param tenantRoleId TenantRole Identifier
     * @return true if already exists, otherwise returns false
     */
    boolean isAssociationAlreadyExistent(Long permissionId, Long tenantRoleId);

    /**
     * Retrieves strictly the TenantRolePermission id basing on tenantRole and user
     * @param tenantRoleId tenant identifier
     * @param permission identifier
     * @return TenantRolePermission id
     */
    Optional<Long> getTenantRolePermissionId(Long tenantRoleId, Long permission);
}
