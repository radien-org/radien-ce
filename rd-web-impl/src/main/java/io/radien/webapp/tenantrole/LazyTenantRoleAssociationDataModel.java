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
package io.radien.webapp.tenantrole;

import io.radien.api.entity.Page;
import io.radien.api.model.role.SystemRole;
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.model.tenantrole.SystemTenantRole;
import io.radien.api.service.role.RoleRESTServiceAccess;
import io.radien.api.service.tenant.TenantRESTServiceAccess;
import io.radien.api.service.tenantrole.TenantRoleRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.tenantmanagement.client.entities.Tenant;
import io.radien.webapp.LazyAbstractDataModel;
import org.apache.commons.lang3.RandomStringUtils;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * LazyDataModel implemented specifically to attend the exhibition of TenantRole associations
 * using Lazy loading fashion.
 *
 * The idea behind a LazyDataModel is to allow working with considerable hugh datasets,
 * taking in consideration pagination parameters like page size, page number and so on,
 * allowing to load only chunks of data that are really required in a specific moment
 *
 * @author Newton Carvalho
 */
public class LazyTenantRoleAssociationDataModel extends LazyAbstractDataModel<SystemTenantRole> {

    private static final long serialVersionUID = -8405561198062161809L;
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private TenantRoleRESTServiceAccess service;

    private RoleRESTServiceAccess roleService;
    private TenantRESTServiceAccess tenantService;

    private Map<Long, SystemTenant> tenantMapRef;
    private Map<Long, SystemRole> roleMapRef;

    /**
     * Main constructor
     * @param service TenantRoleRESTServiceAccess rest client to perform the role of a DataService,
     *                providing data to assembly the datasource
     */
    public LazyTenantRoleAssociationDataModel(TenantRoleRESTServiceAccess service,
                                              TenantRESTServiceAccess tenantService,
                                              RoleRESTServiceAccess roleRESTServiceAccess) {
        this.service=service;
        this.tenantService = tenantService;
        this.roleService = roleRESTServiceAccess;
        this.roleMapRef = new HashMap<>();
        this.tenantMapRef = new HashMap<>();
    }

    @Override
    public Page<? extends SystemTenantRole> getData(int offset, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) throws SystemException {
        Page<? extends SystemTenantRole> page = service.getAll((offset/pageSize)+1, pageSize);
        // Get the role ids
        List<Long> roleIds = page.getResults().stream().map(SystemTenantRole::getRoleId).
                distinct().collect(Collectors.toList());
        updateRoleReferencesForExhibition(roleIds);
        // Get the tenant ids
        List<Long> tenantIds = page.getResults().stream().map(SystemTenantRole::getTenantId).
                distinct().collect(Collectors.toList());
        updateTenantReferencesForExhibition(tenantIds);
        return page;
    }

    /**
     * If necessary, retrieve the Roles that will be shown
     * in the data grid for the current page
     * @param roleIds List containing role identifiers (Obtained from TenantRole pagination)
     */
    protected void updateRoleReferencesForExhibition(List<Long> roleIds) throws SystemException {
        // Filtering non mapped role ids
        List<Long> ids = roleIds.stream().filter(id -> !roleMapRef.containsKey(id)).
                collect(Collectors.toList());
        if (!ids.isEmpty()) {
            // TODO: Necessary service to retrieve Roles based on a list of Ids
            for (Long id: ids) {
                try {
                    roleService.getRoleById(id).ifPresent(r -> roleMapRef.put(id, r));
                }
                catch(Exception e) {
                    throw new SystemException(e);
                }
            }
        }
    }

    /**
     * If necessary, retrieve the Tenants that will be shown
     * in the data grid for the current page
     * @param tenantIds List containing tenant identifiers (Obtained from TenantRole pagination)
     */
    protected void updateTenantReferencesForExhibition(List<Long> tenantIds) throws SystemException {
        // Filtering non mapped role ids
        List<Long> ids = getNonMappedIds(tenantIds, tenantMapRef);
        if (!ids.isEmpty()) {
            // TODO: Necessary service to retrieve Tenants based on a list of Ids
            for (Long id: ids) {
                tenantService.getTenantById(id).ifPresent(t -> tenantMapRef.put(id, t));
            }
        }
    }

    /**
     * Retrieve the non mapped ids (either for role or tenant)
     * @param originalIds A list of ids (for tenant or for roles)
     * @param map eventually already contains reference of mapped object for the mentioned ids
     * @return a filtered list of ids
     */
    protected List<Long> getNonMappedIds(List<Long> originalIds, Map<Long, ?> map) {
        return originalIds.stream().filter(id -> !map.containsKey(id)).
                collect(Collectors.toList());
    }


    /**
     * Retrieve the tenant name taking in consideration a tenant identifier
     * @param tenantId tenant identifier
     * @return the tenant name (if the tenant exists), otherwise returns null
     */
    public String getTenantName(Long tenantId) {
        return tenantMapRef.containsKey(tenantId) ? tenantMapRef.get(tenantId).getName() : "";
    }

    /**
     * Retrieve the role name taking in consideration a role identifier
     * @param roleId role identifier
     * @return the role name (if the role exists), otherwise returns null
     */
    public String getRoleName(Long roleId) {
        return roleMapRef.containsKey(roleId) ? roleMapRef.get(roleId).getName() : "";
    }
}