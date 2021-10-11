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
package io.radien.api.service.permission;

import io.radien.api.Appframeable;
import io.radien.api.entity.Page;
import io.radien.api.model.permission.SystemPermission;
import io.radien.exception.SystemException;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;

/**
 * Contract for Rest Service Client regarding SystemPermission domain object
 *
 * @author Newton Carvalho
 */
public interface PermissionRESTServiceAccess extends Appframeable {

    /**
     * Fetches all permissions
     * @param search value to be filtered
     * @param pageNo of the information to be checked
     * @param pageSize max page numbers for the necessary requested data
     * @param sortBy list of values to sort request
     * @param isAscending in case of true data will come ascending mode if false descending
     * @return list of permissions
     * @throws SystemException in case of any communication error
     */
    public Page<? extends SystemPermission> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) throws SystemException;

    /**
     * Fetches list of specific permissions
     * @param search search parameter for matching permissions (optional).
     * @param pageNo page number
     * @param pageSize page size
     * @param sortBy Sorting fields
     * @param isAscending Defines if ascending or descending in relation of sorting fields
     * @return list of Permissions
     * @throws SystemException in case of any communication error
     * @throws MalformedURLException in case of URL exception construction
     */
    public List<? extends SystemPermission> getPermissions(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) throws MalformedURLException, SystemException;

    /**
     * Retrieves from DB a collection containing permissions. The retrieval process will be
     * based on action identifier and (plus) a resource identifier
     * @param actionId action identifier
     * @param resourceId resource identifier
     * @return a list of permissions found using the action and the resource id
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    public List<? extends SystemPermission> getPermissionByActionAndResource(Long actionId, Long resourceId) throws SystemException;

    /**
     * Retrieves from DB a collection containing permissions. The retrieval process will be
     * based on a list containing identifiers
     * @param ids list containing permission identifiers
     * @return a list of permissions found using the informed identifiers
     * @throws SystemException in case of any found error
     */
    public List<? extends SystemPermission> getPermissionsByIds(List<Long> ids) throws SystemException;

    /**
     * Retrieve the permission Id using the combination of resource and action as parameters
     * @param resource resource name (Mandatory)
     * @param action action name (Mandatory)
     * @return Optional containing Id (If there is a permission for the informed parameter), otherwise a empty one
     * @throws SystemException  in case of not being able to find the information
     */
    public Optional<Long> getIdByResourceAndAction(String resource, String action) throws SystemException;

    /**
     * Retrieves a Permission by Id
     * @param id to be searched
     * @return Optional of permissions
     * @throws SystemException in case of not being able to find the record
     */
    public Optional<SystemPermission> getPermissionById(Long id) throws SystemException ;

    /**
     * Retrieves a Permission by its name
     * @param name to be searched
     * @return Optional of permissions
     * @throws SystemException in case of not being able to find the record
     */
    public Optional<SystemPermission> getPermissionByName(String name) throws SystemException ;

    /**
     * Deletes given permission
     * @param permissionId id of the permissions to be deleted
     * @return true in case of success
     * @throws SystemException in case of any communication error
     */
    public boolean delete(long permissionId) throws SystemException;

    /**
     * Creates given permission
     * @param permission to be created
     * @return true in case of success
     * @throws SystemException in case of any communication error
     */
    public boolean create(SystemPermission permission) throws SystemException;

    /**
     * Update a given permission
     * @param permission to be updated
     * @return true in case of success
     * @throws SystemException in case of any communication error
     */
    public boolean update(SystemPermission permission) throws SystemException;

    /**
     * Checks if permission is existent in the db
     * @param permissionId to be found
     * @param permissionName to be found
     * @return true in case of success
     * @throws SystemException in case of any communication error
     */
    public boolean isPermissionExistent(Long permissionId , String permissionName) throws SystemException;

}
