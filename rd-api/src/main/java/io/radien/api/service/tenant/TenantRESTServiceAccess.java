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
import io.radien.exception.SystemException;

import java.util.List;
import java.util.Optional;

/**
 * Tenant REST Service Access interface for future requests
 *
 * @author Santana
 */
public interface TenantRESTServiceAccess {

    /**
     * Search for a tenant with given id
     * @param id of the tenant to be retrieved
     * @return true if contract has been created with success or false if not
     * @throws SystemException in case of token expiration or any issue on the application
     */
    public Optional<SystemTenant> getTenantById(Long id) throws SystemException ;

    /**
     * Search for a tenant with given name
     * @param name of the tenant to be retrieved
     * @return true if contract has been created with success or false if not
     * @throws SystemException in case of token expiration or any issue on the application
     */
    public List<? extends SystemTenant> getTenantByName(String name) throws SystemException ;

    /**
     * Fetches all the existent tenants
     * @param search specific value to be found
     * @param pageNo where the user currently is
     * @param pageSize number of records to be show by page
     * @param sortBy column to be sorted
     * @param isAscending true in case values should come sorted in ascending way
     * @return a page of system tenants
     * @throws SystemException in case of token expiration or any issue on the application
     */
    public Page<? extends SystemTenant> getAll(String search,
                                               int pageNo,
                                               int pageSize,
                                               List<String> sortBy,
                                               boolean isAscending) throws SystemException;

    /**
     * Creates given tenant
     * @param contract to be created
     * @return true if tenant has been created with success or false if not
     * @throws SystemException in case of token expiration or any issue on the application
     */
    public boolean create(SystemTenant contract) throws SystemException;

    /**
     * deletes given tenant
     * @param tenantId id of the tenant to be deleted
     * @return true if tenant has been deleted with success or false if not
     * @throws SystemException in case of token expiration or any issue on the application
     */
    public boolean delete(long tenantId) throws SystemException;

    /**
     * deletes given tenant hierarchy/tenant
     * @param tenantId id of the tenant and if exists under the parent tenants to be deleted
     * @return true if tenant has been deleted with success or false if not
     * @throws SystemException in case of token expiration or any issue on the application
     */
    public boolean deleteTenantHierarchy(long tenantId) throws SystemException;

    /**
     * updates given tenant
     * @param contract to be updated
     * @return true if tenant has been updated with success or false if not
     * @throws SystemException in case of token expiration or any issue on the application
     */
     public boolean update(SystemTenant contract) throws SystemException;

    /**
     * Checks if tenant is existent in the db
     * @param tenantId to be found
     * @return true in case of success
     * @throws SystemException in case of token expiration or any issue on the application
     */
    public boolean isTenantExistent(Long tenantId) throws  SystemException;

}
