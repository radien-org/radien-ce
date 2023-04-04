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
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.model.tenant.SystemTenantSearchFilter;
import io.radien.api.service.tenant.TenantServiceAccess;
import io.radien.api.service.tenant.exception.TenantException;
import io.radien.api.service.tenant.exception.TenantNotFoundException;
import io.radien.exception.BadRequestException;
import io.radien.exception.SystemException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.tenantmanagement.client.entities.Tenant;
import io.radien.ms.tenantmanagement.entities.TenantEntity;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

@Stateless
public class TenantBusinessService implements Serializable {
    private static final long serialVersionUID = 7586604224207722126L;

    @Inject
    private TenantServiceAccess tenantService;

    /**
     * Gets all the tenants into a pagination mode.
     * @param search name description for some tenant
     * @param pageNo of the requested information. Where the tenant is.
     * @param pageSize total number of pages returned in the request.
     * @param sortBy sort filter criteria.
     * @param isAscending ascending filter criteria.
     * @return a page of system tenants.
     */
    public Page<SystemTenant> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) {
        return tenantService.getAll(search, pageNo, pageSize, sortBy, isAscending);
    }

    /**
     * Method to get all the requested tenant children tenants
     * @param tenantId of the parent tenant
     * @return a list of all the tenant children ids
     */
    public List<SystemTenant> getChildren(Long tenantId) {
        return tenantService.getChildren(tenantId);
    }

    /**
     * Finds tenant by specified id
     * @param tenantId to be searched
     * @return requested system tenant
     * @throws TenantNotFoundException if passed id does not exist
     */
    public SystemTenant get(Long tenantId) {
        SystemTenant tenant = tenantService.get(tenantId);
        if(tenant == null) {
            throw new TenantNotFoundException(MessageFormat.format("Tenant for ID {0} not found", tenantId));
        }
        return tenant;
    }

    /**
     * Gets all the tenants matching the given filte information
     * @param filter information to search
     * @return a list o found system tenants
     */
    public List<? extends SystemTenant> getFiltered(SystemTenantSearchFilter filter) {
        return tenantService.get(filter);
    }

    /**
     * Creates the requested and given Tenant information into the DB.
     *
     * @param tenant to be added
     * @throws BadRequestException in case of duplicated email/duplicated logon
     * @throws TenantException in case of invalid data
     */
    public void create(Tenant tenant) {
        try {
            TenantEntity entity = new TenantEntity(tenant);
            tenantService.create(entity);
        } catch(SystemException e) {
            throw new TenantException(e.getMessage(), Response.Status.BAD_REQUEST);
        } catch(UniquenessConstraintException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    /**
     * Updates the requested and given Contract information into the DB.
     *
     * @param tenant to be updated
     * @throws BadRequestException in case of duplicated name
     * @throws TenantException in case of invalid data
     */
    public void update(long id, Tenant tenant) {
        try {
            tenant.setId(id);
            tenantService.update(new TenantEntity(tenant));
        } catch(SystemException e) {
            throw new TenantException(e.getMessage(), Response.Status.BAD_REQUEST);
        } catch(UniquenessConstraintException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    /**
     * Deletes a unique contract selected by his id.
     *
     * @param tenantId to be deleted.
     * @throws TenantNotFoundException if tenantId does not exist
     */
    public void delete(Long tenantId) {
        if(!tenantService.delete(tenantId)) {
            throw new TenantNotFoundException(MessageFormat.format("Tenant {0} not found", tenantId));
        }
    }

    /**
     * Deletes a list of contracts selected by his id.
     *
     * @param tenantIds to be deleted.
     * @throws TenantNotFoundException if no entry in tenantIds exists
     */
    public void delete(Collection<Long> tenantIds) {
        if(!tenantService.delete(tenantIds)) {
            throw new TenantNotFoundException(MessageFormat.format("No tenants found for {0}",
                            tenantIds.stream().map(String::valueOf).collect(Collectors.joining(", ")))
            );
        }
    }

    /**
     * Requests the DB to delete all the children tenants of the requested tenant
     * @param tenantId to be deleted and all his children
     * @throws TenantNotFoundException if there was an issue with the deletion
     */
    public void deleteTenantHierarchy(Long tenantId) {
        if(!tenantService.deleteTenantHierarchy(tenantId)) {
            throw new TenantNotFoundException(MessageFormat.format("Some tenants in the hierarchy of {0} have not been deleted", tenantId));
        }
    }

    /**
     * Validates if specific requested Tenant exists
     * @param tenantId to be searched
     * @return response true if it exists
     */
    public boolean exists(Long tenantId) {
        return tenantService.exists(tenantId);
    }
}
