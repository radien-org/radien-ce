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

package io.radien.ms.tenantmanagement.service;

import io.radien.api.entity.Page;
import io.radien.api.model.ModelValueId;
import io.radien.api.model.tenant.SystemActiveTenant;
import io.radien.api.model.tenant.SystemActiveTenantSearchFilter;
import io.radien.api.service.tenant.ActiveTenantServiceAccess;
import io.radien.api.service.tenant.exception.ActiveTenantException;
import io.radien.api.service.tenant.exception.ActiveTenantNotFoundException;
import io.radien.exception.BadRequestException;
import io.radien.exception.SystemException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.tenantmanagement.client.entities.ActiveTenant;
import io.radien.ms.tenantmanagement.entities.ActiveTenantEntity;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class ActiveTenantBusinessService implements Serializable {
    @Inject
    private ActiveTenantServiceAccess activeTenantService;

    /**
     * Gets all the active tenants into a pagination mode.
     *
     * @param tenantId    tenant identifier
     * @param userId      user identifier
     * @param pageNo      of the requested information. Where the active tenant is.
     * @param pageSize    number of records to be returned in the response page.
     * @param sortBy      sort filter criteria.
     * @param isAscending ascending filter criteria.
     * @return a page of system active tenants.
     */
    public Page<SystemActiveTenant> getAll(Long tenantId, Long userId, int pageNo,
                                           int pageSize, List<String> sortBy, boolean isAscending) {
        return activeTenantService.getAll(tenantId, userId, pageNo, pageSize, sortBy, isAscending);
    }

    /**
     * Returns the system active tenant with the specific required information search by the ID.
     * @param id to be search
     * @return Ok message if it has success.
     * @throws ActiveTenantNotFoundException Returns error 404 Code to the user in case of resource is not existent.
     */
    public SystemActiveTenant get(Long id) {
        SystemActiveTenant activeTenant = activeTenantService.get(id);
        if(activeTenant == null) {
            throw new ActiveTenantNotFoundException(MessageFormat.format("Active Tenant for ID {0} not fount", id));
        }
        return activeTenant;
    }

    /**
     * Gets all the active tenants matching the given filter information
     *
     * @param filter information to search
     * @return a list o found system active tenants
     */
    public List<? extends SystemActiveTenant> getFiltered(SystemActiveTenantSearchFilter filter) {
        return activeTenantService.get(filter);
    }

    /**
     * Creates the requested and given active tenant information into the DB.
     *
     * @param activeTenant to be added
     * @throws BadRequestException thrown in case of repeated information
     * @throws ActiveTenantNotFoundException thrown in case of inconsistencies
     */
    public ModelValueId create(ActiveTenant activeTenant) {
        try {
            ActiveTenantEntity activeTenantEntity = new ActiveTenantEntity(activeTenant);
            activeTenantService.create(activeTenantEntity);

            ModelValueId modelValueId = new ModelValueId();
            modelValueId.setId(activeTenantEntity.getId());
            return modelValueId;
        } catch (UniquenessConstraintException e) {
            throw new BadRequestException(e.getMessage());
        } catch (SystemException e) {
            throw new ActiveTenantNotFoundException(e.getMessage());
        }
    }

    /**
     * Updates the requested and given Active tenant information into the DB.
     *
     * @param id target activeTenant id
     * @param activeTenant to be updated
     * @throws BadRequestException thrown in case of inconsistencies
     * @throws ActiveTenantNotFoundException thrown in case of not existent active tenant
     */
    public void update(long id, ActiveTenant activeTenant) {
        try {
            activeTenant.setId(id);
            activeTenantService.update(new ActiveTenantEntity(activeTenant));
        } catch (UniquenessConstraintException | SystemException e) {
            throw new BadRequestException(e.getMessage());
        } catch (ActiveTenantException e) {
            throw new ActiveTenantNotFoundException(e.getMessage());
        }
    }

    /**
     * Deletes a unique active tenant selected by his id.
     *
     * @param activeTenantId to be deleted.
     * @throws ActiveTenantNotFoundException if activeTenantId does not exist
     */
    public void delete(Long activeTenantId) {
        if(!activeTenantService.delete(activeTenantId)) {
            throw new ActiveTenantNotFoundException(
                    MessageFormat.format("Active Tenant {0} not found", activeTenantId)
            );
        }
    }

    /**
     * Delete ActiveTenants that exist for following parameters
     *
     * @param tenantId tenant identifier
     * @param userId   user identifier
     * @throws ActiveTenantNotFoundException if no activeTenants exist for passed parameters
     * @throws BadRequestException if request parameters are not correct
     */
    public void delete(Long tenantId, Long userId) {
        try {
            if(!activeTenantService.delete(tenantId, userId)) {
                throw new ActiveTenantNotFoundException(
                        MessageFormat.format("No active tenants found for {0} and {1}", tenantId, userId)
                );
            }
        } catch (SystemException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    /**
     * Deletes a list of active tenants selected by his id.
     *
     * @param activeTenantId to be deleted.
     * @throws ActiveTenantNotFoundException if no active tenants were deleted as result of the operation
     */
    public void delete(Collection<Long> activeTenantId) {
        if(!activeTenantService.delete(activeTenantId)) {
            throw new ActiveTenantNotFoundException(
                    MessageFormat.format("Not active tenant found for {0}",
                            activeTenantId.stream().map(String::valueOf).collect(Collectors.joining(", ")))
            );
        }
    }

    /**
     * Validates if specific requested Active Tenant exists
     *
     * @param userId   to be searched
     * @param tenantId to be search
     * @return response true if it exists
     */
    public boolean exists(Long userId, Long tenantId) {
        return activeTenantService.exists(userId, tenantId);
    }
}
