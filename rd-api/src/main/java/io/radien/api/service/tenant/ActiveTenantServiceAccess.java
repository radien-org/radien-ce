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
package io.radien.api.service.tenant;

import io.radien.api.entity.Page;
import io.radien.api.model.tenant.SystemActiveTenant;
import io.radien.api.model.tenant.SystemActiveTenantSearchFilter;
import io.radien.api.service.ServiceAccess;
import io.radien.api.service.tenant.exception.ActiveTenantException;
import io.radien.api.service.tenant.exception.ActiveTenantNotFoundException;
import io.radien.exception.NotFoundException;
import io.radien.exception.SystemException;
import io.radien.exception.UniquenessConstraintException;

import java.util.Collection;
import java.util.List;

/**
 * Active Tenant Service Access interface with all the possible active tenant requests
 *
 * @author Bruno Gama
 */
public interface ActiveTenantServiceAccess extends ServiceAccess {

    /**
     * Gets all the active tenants into a pagination mode.
     * @param tenantId tenant identifier (Optional)
     * @param userId user identifier (Optional)
     * @param pageNo of the requested information. Where the active tenant is.
     * @param pageSize total number of pages returned in the request.
     * @param sortBy sort filter criteria.
     * @param isAscending ascending filter criteria.
     * @return a page of system active tenants.
     */
    public Page<SystemActiveTenant> getAll(Long tenantId, Long userId, int pageNo, int pageSize, List<String> sortBy, boolean isAscending);

    /**
     * Gets specific active tenant by the id
     * @param activeTenantId to be searched for
     * @return the requested system active tenant
     */
    public SystemActiveTenant get(Long activeTenantId);

    /**
     * Gets a list of system active tenants requested by a search filter
     * @param filter information to search
     * @return a list of system active tenants
     */
    public List<? extends SystemActiveTenant> get(SystemActiveTenantSearchFilter filter);

    /**
     * Creates a system active tenant based on the given information
     * @param activeTenant information to be created
     * @throws UniquenessConstraintException in case of duplicates
     * @throws ActiveTenantException in case of any data issues
     */
    public void create(SystemActiveTenant activeTenant) throws UniquenessConstraintException, SystemException;

    /**
     * Updates a required active tenant based on the given information
     * @param activeTenant information to update
     * @throws UniquenessConstraintException in case of duplicates
     * @throws ActiveTenantException in case of any data issues
     */
    public void update(SystemActiveTenant activeTenant) throws UniquenessConstraintException, ActiveTenantException, ActiveTenantNotFoundException, SystemException;

    /**
     * Deletes a requested active tenant
     * @param activeTenantId to be deleted
     * @return true in case of success false in case of any error
     */
    public boolean delete(Long activeTenantId);

    /**
     * Delete ActiveTenants that exist for following parameters
     * @param tenantId tenant identifier
     * @param userId user identifier
     * @return true in case of success (records founds and removed), otherwise false
     */
    public boolean delete(Long tenantId, Long userId) throws SystemException;

    /**
     * Deletes a collection of active tenants
     *
     * @param activeTenantIds to be deleted
     * @return
     */
    public boolean delete(Collection<Long> activeTenantIds);

    /**
     * Validates if specific requested active tenant exists
     * @param userId to be searched
     * @param tenantId to be searched
     * @return response true if it exists
     */
    public boolean exists(Long userId, Long tenantId) throws NotFoundException;
}
