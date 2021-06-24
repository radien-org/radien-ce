/*
 * Copyright (c) 2016-present openappframe.org & its legal owners. All rights reserved.
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
import io.radien.exception.SystemException;

import java.util.List;
import java.util.Optional;

/**
 * Active Tenant REST Service Access interface for future requests
 *
 * @author Santana
 */
public interface ActiveTenantRESTServiceAccess {

    /**
     * Search for a active tenant with given id
     * @param id of the active tenant to be retrieved
     * @return true if contract has been created with success or false if not
     * @throws SystemException in case of token expiration or any issue on the application
     */
    public Optional<SystemActiveTenant> getActiveTenantById(Long id) throws SystemException;

    /**
     * Fetches all the existent active tenants
     * @param search specific value to be found
     * @param pageNo where the user currently is
     * @param pageSize number of records to be show by page
     * @param sortBy column to be sorted
     * @param isAscending true in case values should come sorted in ascending way
     * @return a page of system active tenants
     * @throws SystemException in case of token expiration or any issue on the application
     */
    public Page<? extends SystemActiveTenant> getAll(String search,
                                               int pageNo,
                                               int pageSize,
                                               List<String> sortBy,
                                               boolean isAscending) throws SystemException;

    /**
     * Creates given active tenant
     * @param activeTenant to be created
     * @return true if active tenant has been created with success or false if not
     * @throws SystemException in case of token expiration or any issue on the application
     */
    public boolean create(SystemActiveTenant activeTenant) throws SystemException;

    /**
     * Deletes given active tenant
     * @param activeTenantId id of the active tenant to be deleted
     * @return true if active tenant has been deleted with success or false if not
     * @throws SystemException in case of token expiration or any issue on the application
     */
    public boolean delete(long activeTenantId) throws SystemException;

    /**
     * Updates given active tenant
     * @param activeTenant to be updated
     * @return true if active tenant has been updated with success or false if not
     * @throws SystemException in case of token expiration or any issue on the application
     */
     public boolean update(SystemActiveTenant activeTenant) throws SystemException;

    /**
     * Checks if active tenant is existent in the db
     * @param activeTenantId to be found
     * @return true in case of success
     * @throws SystemException in case of token expiration or any issue on the application
     */
    public boolean isActiveTenantExistent(Long activeTenantId) throws  SystemException;

}
