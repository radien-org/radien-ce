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
import io.radien.api.model.permission.SystemResource;
import io.radien.api.model.permission.SystemResourceSearchFilter;
import io.radien.api.service.ServiceAccess;
import io.radien.exception.UniquenessConstraintException;

import java.util.Collection;
import java.util.List;

/**
 * @author Newton Carvalho
 * Contract description for the Data Service responsible for handle Resources (CRUD)
 */
public interface ResourceServiceAccess extends ServiceAccess {

    /**
     * Retrieve an Resource by an identifier
     * @param resourceId action identifier
     * @return
     */
    public SystemResource get(Long resourceId);

    /**
     * Retrieves a collection of Resources by its identifiers
     * @param resourceIds list of identifiers
     * @return
     */
    public List<SystemResource> get(List<Long> resourceIds);

    /**
     * Retrieves resources using pagination approach
     * @param search
     * @param pageNo Page number
     * @param pageSize Page size
     * @param sortBy Sorting fields
     * @param isAscending Defines if ascending or descending in relation of sorting fields
     * @return
     */
    public Page<SystemResource> getAll(String search, int pageNo, int pageSize,
                              List<String> sortBy, boolean isAscending);

    /**
     * Save an resource (Create or Update)
     * @param resource
     * @throws UniquenessConstraintException
     */
    public void save(SystemResource resource) throws UniquenessConstraintException;

    /**
     * Delete a resource
     * @param resourceId resource identifier
     */
    public void delete(Long resourceId);

    /**
     * Deletes a set of actions
     * @param resourceIds action identifiers
     */
    public void delete(Collection<Long> resourceIds);

    /**
     * Retrieve Resources using a search filter
     * @param filter
     * @return
     */
    public List<? extends SystemResource> getResources(SystemResourceSearchFilter filter);

}
