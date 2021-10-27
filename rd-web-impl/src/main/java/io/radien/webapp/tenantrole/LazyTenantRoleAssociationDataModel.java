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
package io.radien.webapp.tenantrole;

import io.radien.api.entity.Page;
import io.radien.api.model.role.SystemRole;
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.model.tenantrole.SystemTenantRole;
import io.radien.api.service.role.RoleRESTServiceAccess;
import io.radien.api.service.tenant.TenantRESTServiceAccess;
import io.radien.api.service.tenantrole.TenantRoleRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.rolemanagement.client.entities.Role;
import io.radien.ms.tenantmanagement.client.entities.Tenant;
import io.radien.webapp.LazyAbstractDataModel;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;

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

    private static final long serialVersionUID = -8033465113875345606L;

    private final transient TenantRoleRESTServiceAccess service;
    private final transient RoleRESTServiceAccess roleService;
    private final transient TenantRESTServiceAccess tenantService;

    private final Map<Long, SystemTenant> tenantMapRef;
    private final Map<Long, SystemRole> roleMapRef;

    private static final SystemTenant EMPTY_TENANT = new Tenant();
    private static final SystemRole EMPTY_ROLE = new Role();

    /**
     * Injects the core components necessary to retrieve the information to be shown
     * on the data table. Due the fact that {@link SystemTenantRole} only contains ids referring
     * Tenant and Role, we need auxiliary components to retrieve (textual) information regarding
     * Tenant and Role as well
     * @param service Rest client responsible for retrieving {@link SystemTenantRole} instances.
     * @param tenantService auxiliary rest client to provide information regarding tenant
     * @param roleRESTServiceAccess auxiliary rest client to provide information regarding role.     *
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

    /**
     * Core method responsible for retrieving a page containing {@link SystemTenantRole} instances
     * accordingly to the following parameters.
     *
     * Due the nature of {@link SystemTenantRole} - which only contains ids - once the page
     * is retrieved, the information regarding the related tenants and roles must be retrieved
     * as well (And stored in internal cache to avoid multiple requests to the backend).
     *
     * @param offset corresponds to the page number or page index
     * @param pageSize corresponds to page length or page size
     * @param sortBy map that contains references for {@link SortMeta} instances,
     *               parameters to be used to perform sorting operations for the existent rows
     * @param filterBy map that contains references for {@link FilterMeta} instances,
     *                 parameters to be used to perform filtering operation over the
     *                 existent rows.
     * @return Page containing {@link SystemTenantRole} instances, or an empty page
     * (in case of no objects found).
     * @throws SystemException in case of issues during the request processing
     */
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
     * Check if the roles are already mapped in the internal cache.
     * If not, retrieve the Roles that will be shown in the data grid for the current page
     * @param roleIds List containing role identifiers (Obtained from TenantRole pagination)
     */
    protected void updateRoleReferencesForExhibition(List<Long> roleIds) throws SystemException {
        // Filtering non mapped role ids
        List<Long> ids = roleIds.stream().filter(id -> !roleMapRef.containsKey(id)).
                collect(Collectors.toList());
        if (!ids.isEmpty()) {
            roleService.getRolesByIds(ids).forEach(r -> roleMapRef.put(r.getId(), r));
        }
    }

    /**
     * Check if the tenants are already mapped in the internal cache.
     * If not, retrieve the Tenants that will be shown in the data grid for the current page
     * @param tenantIds List containing tenant identifiers (Obtained from TenantRole pagination)
     */
    protected void updateTenantReferencesForExhibition(List<Long> tenantIds) throws SystemException {
        // Filtering non mapped role ids
        List<Long> ids = getNonMappedIds(tenantIds, tenantMapRef);
        if (!ids.isEmpty()) {
            tenantService.getTenantsByIds(ids).forEach(t -> tenantMapRef.put(t.getId(), t));
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
        return tenantMapRef.getOrDefault(tenantId, EMPTY_TENANT).getName();
    }

    /**
     * Retrieve the role name taking in consideration a role identifier
     * @param roleId role identifier
     * @return the role name (if the role exists), otherwise returns null
     */
    public String getRoleName(Long roleId) {
        return roleMapRef.getOrDefault(roleId, EMPTY_ROLE).getName();
    }
}