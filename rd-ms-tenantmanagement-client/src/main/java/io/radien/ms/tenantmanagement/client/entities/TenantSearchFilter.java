/*
 * Copyright (c) 2021-present radien GmbH & its legal owners. All rights reserved.
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
package io.radien.ms.tenantmanagement.client.entities;

import io.radien.api.model.tenant.SystemTenantSearchFilter;
import io.radien.api.model.tenant.SystemTenantType;
import io.radien.api.search.SearchFilterCriteria;

import java.util.Collection;

/**
 * Encapsulates the parameters applied to search for tenants
 * @author Bruno Gama
 */
public class TenantSearchFilter extends SearchFilterCriteria implements SystemTenantSearchFilter {

    private String name;
    private SystemTenantType tenantType;
    private Collection<Long> ids;

    /**
     * Tenant search filter constructor
     * @param name of the tenant to be found
     * @param type of the tenant to be found
     * @param ids to be search
     * @param isExact true in case search option should be exact
     * @param isLogicalConjunction true in case search option is and conjunction
     */
    public TenantSearchFilter(String name, String type, Collection<Long> ids, boolean isExact, boolean isLogicalConjunction) {
        super(isExact, isLogicalConjunction);
        this.name = name;
        this.tenantType = TenantType.getByDescription(type);
        this.ids = ids;
    }

    /**
     * Tenant search filter name getter
     * @return the tenant search filter name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Tenant search filter name setter
     * @param name to be set
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Tenant search filter type getter
     * @return the tenant search filter type
     */
    @Override
    public SystemTenantType getTenantType() {
        return tenantType;
    }

    /**
     * Tenant search filter type setter
     * @param tenantType to be set
     */
    @Override
    public void setTenantType(SystemTenantType tenantType) {
        this.tenantType = tenantType;
    }

    /**
     * Tenant search filter get ids
     * @return ids for search filter
     */
    @Override
    public Collection<Long> getIds() { return ids; }

    /**
     * Tenant search filter ids setter
     * @param ids to be set and replace
     */
    @Override
    public void setIds(Collection<Long> ids) { this.ids = ids; }

}
