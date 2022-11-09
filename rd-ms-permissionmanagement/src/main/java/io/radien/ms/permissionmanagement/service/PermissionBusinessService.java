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

package io.radien.ms.permissionmanagement.service;

import io.radien.api.entity.Page;
import io.radien.api.model.permission.SystemPermission;
import io.radien.api.model.permission.SystemPermissionSearchFilter;
import io.radien.api.service.permission.PermissionServiceAccess;
import io.radien.api.service.permission.exception.PermissionIllegalArgumentException;
import io.radien.api.service.permission.exception.PermissionNotFoundException;
import io.radien.exception.BadRequestException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.permissionmanagement.client.entities.Permission;
import io.radien.ms.permissionmanagement.entities.PermissionEntity;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.hsqldb.lib.StringUtil;

@Stateless
public class PermissionBusinessService {

    @Inject
    private PermissionServiceAccess permissionService;

    /**
     * Looks for the Permission corresponding to the permissionId
     * @param permissionId to look for
     * @return corresponding permission
     * @throws PermissionNotFoundException if permission is not found
     */
    public SystemPermission get(Long permissionId) {
        SystemPermission result = permissionService.get(permissionId);
        if(result == null) {
            throw new PermissionNotFoundException(MessageFormat.format("No permission found for id {0}", permissionId));
        }
        return result;
    }

    /**
     * Retrieves permissions matching with given ids
     * @param permissionIds list of ids
     * @return list of permissions matching given ids
     * @throws BadRequestException if input parameter is not valid
     * @throws PermissionNotFoundException if no resources match the given ids
     */
    public List<SystemPermission> get(List<Long> permissionIds) {
        if(permissionIds == null || permissionIds.isEmpty()) {
            throw new BadRequestException("Mandatory parameter missing");
        }
        List<SystemPermission> results = permissionService.get(permissionIds);
        if(results.isEmpty()) {
            throw new PermissionNotFoundException("No permissions found for given ids");
        }
        return results;
    }

    /**
     * Returns a page of System Permissions
     * @param search name filter for permissions
     * @param pageNo page number
     * @param pageSize page size
     * @param sortBy sort parameters
     * @param isAscending ascending or descending order
     * @return page of System Permissions matching the given parameters
     */
    public Page<SystemPermission> getAll(String search, int pageNo, int pageSize,
                                       List<String> sortBy, boolean isAscending) {
        return permissionService.getAll(search, pageNo, pageSize, sortBy, isAscending);
    }

    public List<SystemPermission> getFiltered(SystemPermissionSearchFilter filter) {
        return permissionService.getPermissions(filter);
    }

    /**
     * Retrieve the permission Id using the combination of resource and action as parameters
     * @param resource resource name (Mandatory)
     * @param action action name (Mandatory)
     * @return Found permissiong id
     * @throws BadRequestException in case of parameters not correctly informed
     * @throws PermissionNotFoundException if no permission matched given parameters
     */
    public Long getIdByActionAndResource(String resource, String action) {
        try {
            return permissionService.getIdByActionAndResource(resource, action)
                    .orElseThrow(() -> new PermissionNotFoundException(MessageFormat.format("No permission found for {0} and {1}", resource, action)));
        } catch (PermissionIllegalArgumentException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    /**
     * Creates the requested Permission information into the DB.
     * @param permission to be added/inserted
     * @throws BadRequestException in case of duplicated name (or combination of action and resource)
     */
    public void create(Permission permission) {
        try {
            permissionService.create(new PermissionEntity(permission));
        } catch (UniquenessConstraintException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    /**
     * Updates the requested Permission information into the DB.
     * @param permission to be updated
     * @throws BadRequestException in case of duplicated name (or combination of action and resource)
     */
    public void update(Long id, Permission permission) {
        try {
            permission.setId(id);
            permissionService.update(permission);
        } catch (UniquenessConstraintException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    /**
     * Deletes a unique Permission selected by his id.
     * @param id to be deleted.
     * @throws PermissionNotFoundException if no permission is found for given id
     */
    public void delete(Long id) {
        if(!permissionService.delete(id)) {
            throw new PermissionNotFoundException(MessageFormat.format("No permission found for id {0}", id));
        }
    }

    /**
     * Deletes a list of Permissions selected by his id.
     * @param permissionIds to be deleted.
     * @throws PermissionNotFoundException if no permissions match the given ids
     */
    public void delete(Collection<Long> permissionIds) {
        if(!permissionService.delete(permissionIds)) {
            throw new PermissionNotFoundException(MessageFormat.format("No permission found for id {0}", permissionIds));
        }
    }

    /**
     * Verifies if some Permission exists for a referred Id (or alternatively for a name)
     * @param permissionId Identifier to guide the search be searched (Primary parameter)
     * @param permissionName Permission name, an alternative parameter that will be used ONLY
     * if Id is omitted
     * @return response true if it exists
     * @throws BadRequestException if parameters are missing
     */
    public boolean exists(Long permissionId, String permissionName) {
        if(permissionId ==  null && StringUtil.isEmpty(permissionName)) {
            throw new BadRequestException("Mandatory parameter missing");
        }

        return permissionService.exists(permissionId, permissionName);
    }

    /**
     * Retrieves a SystemPermission taking in account Resource and Action
     * @param action Action name
     * @param resource Resource Name
     * @return a SystemPermission linked with Resource and Action
     * @throws BadRequestException is parameters are missing
     * @throws PermissionNotFoundException if no permission matches given parameter
     */
    public SystemPermission getPermissionByActionNameAndResourceName(String action, String resource) {
        if(StringUtil.isEmpty(action) || StringUtil.isEmpty(resource)) {
            throw new BadRequestException("Mandatory parameter missing");
        }

        SystemPermission result = permissionService.getPermissionByActionAndResourceNames(action, resource);
        if(result == null) {
            throw new PermissionNotFoundException(MessageFormat.format("No permission found for {0} and {1}", action, resource));
        }

        return result;
    }

    public long getCount() {
        return permissionService.getTotalRecordsCount();
    }

}
