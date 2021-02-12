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

import io.radien.api.model.tenant.SystemContract;
import io.radien.api.model.tenant.SystemTenant;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

/**
 * @author Santana
 *
 */
public interface TenantRESTServiceAccess {

    /**
     * Search for a tenant with given name
     * @param name of the tenant to be retrieved
     * @return true if contract has been created with success or false if not
     * @throws MalformedURLException in case of URL specification
     */
    public Optional<SystemTenant> getTenantByName(String name) throws MalformedURLException, ParseException, Exception ;

    /**
     * Fetches all tenants
     * @return List of tenants
     */
    public List<? extends SystemTenant> getAll() throws MalformedURLException, ParseException;

    /**
     * Creates given tenant
     * @param contract to be created
     * @return true if tenant has been created with success or false if not
     * @throws MalformedURLException in case of URL specification
     */
    public boolean create(SystemTenant contract) throws MalformedURLException;

    /**
     * deletes given tenant
     * @param tenantId id of the tenant to be deleted
     * @return true if tenant has been deleted with success or false if not
     * @throws MalformedURLException in case of URL specification
     */
    public boolean delete(long tenantId) throws MalformedURLException;

    /**
     * updates given tenant
     * @param contract to be updated
     * @return true if tenant has been updated with success or false if not
     * @throws MalformedURLException in case of URL specification
     */
     public boolean update(SystemTenant contract) throws MalformedURLException;
}