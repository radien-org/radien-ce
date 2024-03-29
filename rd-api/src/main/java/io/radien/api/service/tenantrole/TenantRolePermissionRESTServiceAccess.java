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
import io.radien.api.model.permission.SystemPermission;
import io.radien.api.model.tenantrole.SystemTenantRolePermission;
import io.radien.exception.SystemException;
import java.util.List;
import java.util.Optional;

/**
 * Rest service client responsible to Deal with TenantRole Permission endpoint
 *
 * @author Newton Carvalho
 */
public interface TenantRolePermissionRESTServiceAccess {

    /**
     * Under a pagination approach, retrieves the Tenant Role Permissions associations that currently exist
     * @param tenantRoleId tenant role identifier(Acting as filter)
     * @param permissionId permission identifier (Acting as filter)
     * @param pageNo page number
     * @param pageSize page size
     * @param sortBy criteria field to be sorted
     * @param isAscending boolean value to show the values ascending or descending way
     * @return Page containing TenantRolePermission instances
     * @throws SystemException in case of any error
     */
    Page<? extends SystemTenantRolePermission> getAll(Long tenantRoleId, Long permissionId,
                                                      int pageNo, int pageSize,
                                                      List<String> sortBy, boolean isAscending) throws SystemException;

    /**
     * Retrieves TenantRolePermission associations that met the following parameter
     * @param tenantRoleId TenantRole identifier
     * @param permissionId Permission identifier
     * @param isLogicalConjunction specifies if the parameters will be unified by AND (true) or OR (false)
     * @return In case of successful operation returns a Collection containing TenantRole associations.
     * @throws SystemException in case of Any error
     */
    List<? extends SystemTenantRolePermission> getTenantRolePermissions(Long tenantRoleId, Long permissionId,
                                                                        boolean isLogicalConjunction) throws SystemException;

    /**
     * Assign/associate/add permission to a TenantRole domain
     * (In a practical way: this functionality is designated to CRATE a TenantRolePermission)
     * The association will always be under a specific role
     * @param tenantRolePermission association between Tenant, Role and Permission
     * @return Boolean indicating if operation was concluded successfully
     * @throws SystemException in case of any error
     */
    Boolean assignPermission(SystemTenantRolePermission tenantRolePermission) throws SystemException;

    /**
     * Updates a TenantRolePermission previously crated (When a permission was assigned into a TenantRole)
     * @param tenantRolePermission association between Tenant, Role and Permission
     * @return Boolean indicating if operation was concluded successfully
     * @throws SystemException in case of any error
     */
    Boolean update(SystemTenantRolePermission tenantRolePermission) throws SystemException;

    /**
     * (Un)Assign/Dissociate/remove permission from a TenantRole domain
     * @param tenantId Tenant identifier (Mandatory)
     * @param roleId Role identifier (Mandatory)
     * @param permissionId Permission identifier (Mandatory)
     * @return Boolean indicating if operation was concluded successfully
     * @throws SystemException in case of any error
     */
    Boolean unAssignPermission(Long tenantId, Long roleId, Long permissionId) throws SystemException;

    /**
     * (Un)Assign/Dissociate/remove permission from a TenantRole domain
     * Simply deletes a TenantRolePermission that eventually exists
     * @param tenantRolePermissionId identifier that maps a TenantRolePermission entity
     * @throws SystemException in case of any error
     */
    Boolean unAssignPermission(Long tenantRolePermissionId) throws SystemException;

    /**
     * Retrieve a Tenant Role Permission using the id as search parameter.
     * (Invokes the core method counterpart and handles TokenExpiration error)
     * @param id Tenant Role Permission id association to guide the search process
     * @return Optional containing Tenant Role Permission found.
     * @throws SystemException in case of any error
     */
    Optional<SystemTenantRolePermission> getTenantRolePermissionById(Long id) throws SystemException;

    /**
     * Retrieves the Permissions that exists for a Tenant Role Association (Optionally taking in account user)
     * @param tenantId Tenant identifier (Mandatory)
     * @param roleId Role identifier (Mandatory)
     * @param userId User identifier (Optional)
     * @return List containing permissions.
     * @throws SystemException in case of any error
     */
    List<? extends SystemPermission> getPermissions(Long tenantId, Long roleId, Long userId) throws SystemException;
}
