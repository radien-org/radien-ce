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
import io.radien.api.model.permission.SystemResource;
import io.radien.api.model.permission.SystemResourceSearchFilter;
import io.radien.api.service.permission.ResourceServiceAccess;
import io.radien.api.service.permission.exception.ActionNotFoundException;
import io.radien.api.service.permission.exception.ResourceNotFoundException;
import io.radien.exception.BadRequestException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.permissionmanagement.client.entities.Resource;
import io.radien.ms.permissionmanagement.entities.ResourceEntity;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.hsqldb.lib.StringUtil;

@Stateless
public class ResourceBusinessService {
    @Inject
    private ResourceServiceAccess resourceService;

    /**
     * Looks for the Resource corresponding to the resourceId
     * @param resourceId to look for
     * @return corresponding resource
     * @throws ResourceNotFoundException if resource is not found
     */
    public SystemResource get(Long resourceId) {
        SystemResource result = resourceService.get(resourceId);
        if(result == null) {
            throw new ResourceNotFoundException(MessageFormat.format("No resource found for id {0}", resourceId));
        }
        return result;
    }

    /**
     * Retrieves resources matching with given ids
     * @param resourceIds list of ids
     * @return list of resources matching given ids
     * @throws BadRequestException if input parameter is not valid
     * @throws ResourceNotFoundException if no resources match the given ids
     */
    public List<SystemResource> get(List<Long> resourceIds) {
        if(resourceIds == null || resourceIds.isEmpty()) {
            throw new BadRequestException("Mandatory parameter missing");
        }
        List<SystemResource> results = resourceService.get(resourceIds);
        if(results.isEmpty()) {
            throw new ResourceNotFoundException("No resources found for given ids");
        }
        return results;
    }

    /**
     * Returns a page of System Resources
     * @param search name filter for resources
     * @param pageNo page number
     * @param pageSize page size
     * @param sortBy sort parameters
     * @param isAscending ascending or descending order
     * @return page of System Resources matching the given parameters
     */
    public Page<SystemResource> getAll(String search, int pageNo, int pageSize,
                                       List<String> sortBy, boolean isAscending) {
        return resourceService.getAll(search, pageNo, pageSize, sortBy, isAscending);
    }

    public List<SystemResource> getFiltered(SystemResourceSearchFilter filter) {
        return resourceService.getResources(filter);
    }

    /**
     * Persists given resource into database
     * @param resource value to be persisted
     * @throws BadRequestException if resource name is missing or duplicate value already exists
     */
    public void create(Resource resource) {
        if(StringUtil.isEmpty(resource.getName())) {
            throw new BadRequestException("Mandatory parameter missing");
        }
        try {
            resourceService.create(new ResourceEntity(resource));
        } catch (UniquenessConstraintException e) {
            throw new BadRequestException("Duplicated resource already exists");
        }
    }

    /**
     * Updates given resource to datasource
     * @param id updated id for resource
     * @param resource resource to be updated
     * @throws BadRequestException if resource name is missing or duplicate value already exists
     */
    public void update(Long id, Resource resource) {
        if(id == null || resource == null || resource.getName() == null) {
            throw new BadRequestException("Mandatory parameter missing");
        }
        try {
            resource.setId(id);
            resourceService.update(resource);
        } catch (UniquenessConstraintException e) {
            throw new BadRequestException("Duplicated resource already exists");
        }
    }

    /**
     * Deletes resource matching given id
     * @param resourceId id to delete
     * @throws ResourceNotFoundException if no resource was found for given id
     */
    public void delete(Long resourceId) {
        if(!resourceService.delete(resourceId)) {
            throw new ResourceNotFoundException(MessageFormat.format("No resource found for id {0}", resourceId));
        }
    }

    /**
     * Deletes action matching given ids
     * @param resourceIds ids to delete
     * @throws ActionNotFoundException if no action was found for given ids
     */
    public void delete(Collection<Long> resourceIds) {
        if(!resourceService.delete(resourceIds)) {
            throw new ResourceNotFoundException("No resource found for id given id list");
        }
    }
}
