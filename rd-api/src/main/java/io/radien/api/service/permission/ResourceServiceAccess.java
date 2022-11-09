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
package io.radien.api.service.permission;

import io.radien.api.entity.Page;
import io.radien.api.model.permission.SystemResource;
import io.radien.api.model.permission.SystemResourceSearchFilter;
import io.radien.api.service.ServiceAccess;
import io.radien.api.service.permission.exception.ResourceNotFoundException;
import io.radien.exception.UniquenessConstraintException;

import java.util.Collection;
import java.util.List;

/**
 * Contract description for the Data Service responsible for handle Resources (CRUD)
 *
 * @author Newton Carvalho
 */
public interface ResourceServiceAccess extends ServiceAccess {

    /**
     * Retrieve an Resource by an identifier
     * @param resourceId action identifier
     * @return the requested system resource
     */
    SystemResource get(Long resourceId);

    /**
     * Retrieves a collection of Resources by its identifiers
     * @param resourceIds list of identifiers
     * @return a list of requested resources
     */
    List<SystemResource> get(List<Long> resourceIds);

    /**
     * Fetches all resources
     * @param search value to be filtered
     * @param pageNo of the information to be checked
     * @param pageSize max page numbers for the necessary requested data
     * @param sortBy list of values to sort request
     * @param isAscending in case of true data will come ascending mode if false descending
     * @return list of resources
     */
    Page<SystemResource> getAll(String search, int pageNo, int pageSize,
                              List<String> sortBy, boolean isAscending);

    /**
     * Create a resource
     * @param resource to be created
     * @throws UniquenessConstraintException in case the requested record already exists or has duplicated information
     */
    void create(SystemResource resource) throws UniquenessConstraintException;

    /**
     * Update a resource
     * @param resource to be updated
     * @throws ResourceNotFoundException in case of not existent resource
     * @throws UniquenessConstraintException in case of duplicated information
     */
    void update(SystemResource resource) throws ResourceNotFoundException, UniquenessConstraintException;

    /**
     * Delete a resource
     * @param resourceId resource identifier
     */
    boolean delete(Long resourceId);

    /**
     * Deletes a set of actions
     * @param resourceIds action identifiers
     */
    boolean delete(Collection<Long> resourceIds);

    /**
     * Retrieve Resources using a search filter
     * @param filter with the fields that should be looked into to find the correct system resource
     * @return a list of system resources
     */
    List<SystemResource> getResources(SystemResourceSearchFilter filter);

}
