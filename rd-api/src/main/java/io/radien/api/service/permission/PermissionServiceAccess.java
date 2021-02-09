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
     * Retrieve an Action by an identifier
     * @param permissionId permission identifier
     * @return
     */
    public SystemPermission get(Long permissionId);

    /**
     * Retrieves a collection of Actions by its identifiers
     * @param permissionId list of identifiers
     * @return
     */
    public List<SystemPermission> get(List<Long> permissionId);

    /**
     * Retrieves permissions using pagination approach
     * @param search
     * @param pageNo Page number
     * @param pageSize Page size
     * @param sortBy Sorting fields
     * @param isAscending Defines if ascending or descending in relation of sorting fields
     * @return
     */
    public Page<SystemPermission> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending);

    /**
     * Save an permission (Create or Update)
     * @param permission
     * @throws UniquenessConstraintException
     */
    public void save(SystemPermission permission) throws UniquenessConstraintException;

    /**
     * Delete an permission
     * @param permissionId permission identifier
     */
    public void delete(Long permissionId);

    /**
     * Deletes a set of permissions
     * @param permissionIds permission identifiers
     */
    public void delete(Collection<Long> permissionIds);

    /**
     * Retrieve Actions using a search filter
     * @param filter
     * @return
     */
    public List<? extends SystemPermission> getPermissions(SystemPermissionSearchFilter filter);

}
