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

import java.net.MalformedURLException;
import java.text.ParseException;
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
     * Fetches all resources
     * @param search value to be filtered
     * @param pageNo of the information to be checked
     * @param pageSize max page numbers for the necessary requested data
     * @param sortBy list of values to sort request
     * @param isAscending in case of true data will come ascending mode if false descending
     * @return list of resources
     * @throws MalformedURLException in case of URL exception construction
     * @throws ParseException in case of any issue parsing the response information
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
