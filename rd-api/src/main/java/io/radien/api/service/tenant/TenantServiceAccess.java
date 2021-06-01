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
package io.radien.api.service.tenant;

import io.radien.api.entity.Page;
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.model.tenant.SystemTenantSearchFilter;
import io.radien.api.service.ServiceAccess;
import io.radien.exception.NotFoundException;
import io.radien.exception.TenantException;
import io.radien.exception.UniquenessConstraintException;

import java.util.Collection;
import java.util.List;

/**
 * Tenant Service Access interface with all the possible tenant requests
 *
 * @author Santana
 */
public interface TenantServiceAccess extends ServiceAccess {

    /**
     * Gets all the tenants into a pagination mode.
     * @param search name description for some tenant
     * @param pageNo of the requested information. Where the tenant is.
     * @param pageSize total number of pages returned in the request.
     * @param sortBy sort filter criteria.
     * @param isAscending ascending filter criteria.
     * @return a page of system tenants.
     */
    public Page<SystemTenant> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending);

    /**
     * Gets specific tenant by the id
     * @param tenantId to be searched for
     * @return the requested system tenant
     */
    public SystemTenant get(Long tenantId) ;

    /**
     * Gest a list of system tenants requested by a search filter
     * @param filter information to search
     * @return a list of system tenants
     */
    public List<? extends SystemTenant> get(SystemTenantSearchFilter filter);

    /**
     * Creates a system tenant based on the given informations
     * @param tenant information to be created
     * @throws UniquenessConstraintException in case of duplicates
     * @throws TenantException in case of any data issues
     */
    public void create(SystemTenant tenant) throws UniquenessConstraintException, TenantException;

    /**
     * Updates a required tenant based on the given information
     * @param tenant information to update
     * @throws UniquenessConstraintException in case of duplicates
     * @throws TenantException in case of any data issues
     */
    public void update(SystemTenant tenant) throws UniquenessConstraintException, TenantException;

    /**
     * Deletes a requested tenant
     * @param tenantId to be deleted
     * @return true in case of success false in case of any error
     */
    public boolean delete(Long tenantId);

    /**
     * Deletes a requested tenant and all the tenants bellow him
     * @param tenantId to be deleted and all his children
     * @return true in case of success
     */
    public boolean deleteTenantHierarchy(Long tenantId);

    /**
     * Deletes a collection of tenants
     * @param tenantIds to be deleted
     */
    public void delete(Collection<Long> tenantIds);

    /**
     * Validates if specific requested tenant exists
     * @param tenantId to be searched
     * @return response true if it exists
     */
    public boolean exists(Long tenantId) throws NotFoundException;

}
