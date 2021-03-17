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

import io.radien.api.Appframeable;
import io.radien.api.entity.Page;
import io.radien.api.model.permission.SystemPermission;
import io.radien.exception.SystemException;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;

/**
 * @author Newton Carvalho
 * Contract for Rest Service Client regarding SystemPermission domain object
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
     * @throws MalformedURLException in case of URL exception construction
     * @throws SystemException in case of any communication error
     */
    public Page<? extends SystemPermission> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) throws MalformedURLException, SystemException;

    /**
     * Fetches list of specific permissions
     * @return list of Permissions
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
     * Retrieves a Permission by Id
     * @param id to be searched
     * @return Optional of permissions
     * @throws SystemException in case of not being able to find the record
     */
    public Optional<SystemPermission> getPermissionById(Long id) throws SystemException ;

    /**
     * Deletes given permission
     * @param permissionId id of the permissions to be deleted
     * @return true in case of success
     */
    public boolean delete(long permissionId) throws SystemException;

    /**
     * Creates given permission
     * @param permission to be created
     * @return true in case of success
     */
    public boolean create(SystemPermission permission) throws SystemException;

    /**
     * Checks if permission is existent in the db
     * @param permissionId to be found
     * @return true in case of success
     */
    public boolean isPermissionExistent(Long permissionId , String permissionName) throws SystemException;

    /**
     * Will calculate how many records are existent in the db
     * @return the count of existent permissions.
     */
    public Long getTotalRecordsCount() throws SystemException;
}
