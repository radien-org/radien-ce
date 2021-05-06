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
package io.radien.api.service.tenantrole;

import io.radien.api.model.tenantrole.SystemTenantRolePermission;
import io.radien.api.model.tenantrole.SystemTenantRolePermissionSearchFilter;
import io.radien.api.service.ServiceAccess;
import io.radien.exception.UniquenessConstraintException;

import java.util.List;

/**
 * Describes a contract for a Repository responsible
 * to perform operations over Tenant, Role and Permission association
 * @author Newton Carvalho
 */
public interface TenantRolePermissionServiceAccess extends ServiceAccess {

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
}
