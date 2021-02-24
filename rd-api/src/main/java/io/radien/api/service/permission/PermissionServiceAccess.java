/*
 * Copyright (c) 2016-present radien.io & its legal owners. All rights reserved.
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
package io.radien.api.service.permission;

import io.radien.api.entity.Page;
import io.radien.api.model.permission.SystemPermission;
import io.radien.api.model.permission.SystemPermissionSearchFilter;
import io.radien.api.service.ServiceAccess;
import io.radien.exception.NotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.exception.PermissionNotFoundException;

import java.util.Collection;
import java.util.List;

/**
 * @author Newton Carvalho
 * Contract description for the Data Service responsible for handle Permissions (CRUD)
 */
public interface PermissionServiceAccess extends ServiceAccess {

    /**
     * Gets the System Permission searching by the PK (id).
     * @param permissionId to be searched.
     * @return the system Permission requested to be found.
     */
    public SystemPermission get(Long permissionId) throws PermissionNotFoundException;

    /**
     * Gets a list of System Permissions searching by multiple PK's (id) requested in a list.
     * @param permissionId to be searched.
     * @return the list of system Permissions requested to be found.
     */
    public List<SystemPermission> get(List<Long> permissionId);

    /**
     * Gets all the Permissions into a pagination mode.
     * Can be filtered by name Permission.
     * @param search name description for some permission
     * @param pageNo of the requested information. Where the Permission is.
     * @param pageSize total number of pages returned in the request.
     * @param sortBy sort filter criteria.
     * @param isAscending ascending filter criteria.
     * @return a page of system Permissions.
     */
    public Page<SystemPermission> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending);

    /**
     * Saves or updates the requested and given Permission information into the DB.
     * @param permission to be added/inserted or updated
     * @throws UniquenessConstraintException in case of duplicated name
     */
    public void save(SystemPermission permission) throws UniquenessConstraintException;

    /**
     * Deletes a unique Permission selected by his id.
     * @param permissionId to be deleted.
     */
    public void delete(Long permissionId);

    /**
     * Deletes a list of Permissions selected by his id.
     * @param permissionIds to be deleted.
     */
    public void delete(Collection<Long> permissionIds);

    /**
     * Get PermissionsBy unique columns
     * @param filter entity with available filters to search Permission
     */
    public List<? extends SystemPermission> getPermissions(SystemPermissionSearchFilter filter);

    /**
     * Validates if specific requested permission exists
     * @param permissionId to be searched
     * @return response true if it exists
     */
    public boolean exists(Long permissionId);
}
