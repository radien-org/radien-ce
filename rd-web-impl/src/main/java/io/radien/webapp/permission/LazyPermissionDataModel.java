/*
 * Copyright (c) 2021-present radien GmbH. All rights reserved.
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
package io.radien.webapp.permission;

import io.radien.api.entity.Page;
import io.radien.api.model.permission.SystemAction;
import io.radien.api.model.permission.SystemPermission;
import io.radien.api.model.permission.SystemResource;
import io.radien.api.model.tenantrole.SystemTenantRole;
import io.radien.api.service.permission.ActionRESTServiceAccess;
import io.radien.api.service.permission.PermissionRESTServiceAccess;
import io.radien.api.service.permission.ResourceRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.permissionmanagement.client.entities.Action;
import io.radien.ms.permissionmanagement.client.entities.Resource;
import io.radien.webapp.LazyAbstractDataModel;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;

import java.util.Map;

/**
 * @author Bruno Gama
 */
public class LazyPermissionDataModel extends LazyAbstractDataModel<SystemPermission> {

    private static final long serialVersionUID = -7135039336902887393L;
    private final transient PermissionRESTServiceAccess service;
    private final transient ActionRESTServiceAccess actionRESTServiceAccess;
    private final transient ResourceRESTServiceAccess resourceRESTServiceAccess;

    private final Map<Long, SystemAction> actionMapRef;
    private final Map<Long, SystemResource> resourceMapRef;

    private static final SystemResource EMPTY_RESOURCE = new Resource();
    private static final SystemAction EMPTY_ACTION = new Action();

    /**
     * Injects the core components necessary to retrieve the information to be shown
     * on the data table. Due the fact that {@link SystemTenantRole} contains ids referring
     * Action and Resource, we need auxiliary components to retrieve (textual) information regarding
     * Action and Resource as well
     * @param service Rest client responsible for retrieving {@link SystemPermission} instances.
     * @param actionRESTServiceAccess auxiliary rest client to provide information regarding action
     * @param resourceRESTServiceAccess auxiliary rest client to provide information regarding resource.     *
     */
    public LazyPermissionDataModel(PermissionRESTServiceAccess service,
                                   ActionRESTServiceAccess actionRESTServiceAccess,
                                   ResourceRESTServiceAccess resourceRESTServiceAccess) {
        this.service=service;
        this.actionRESTServiceAccess = actionRESTServiceAccess;
        this.resourceRESTServiceAccess = resourceRESTServiceAccess;
        this.actionMapRef = new HashMap<>();
        this.resourceMapRef = new HashMap<>();
    }

    @Override
    public SystemPermission getRowData(String rowKey) {
        if (rowKey.equals("null")) {
            return null;
        }
        return super.getRowData(rowKey);
    }

    /**
     * Core method responsible for retrieving a page containing {@link SystemPermission} instances
     * accordingly to the following parameters.
     *
     * Due the nature of {@link SystemPermission} - which only contains ids - once the page
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
     * @return Page containing {@link SystemPermission} instances, or an empty page
     * (in case of no objects found).
     * @throws SystemException in case of issues during the request processing
     */
    @Override
    public Page<? extends SystemPermission> getData(int offset, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) throws SystemException {
        Page<? extends SystemPermission> page = service.getAll(null, (offset/pageSize) + 1, pageSize, null, true);
        // Get the action ids
        List<Long> actionIds = page.getResults().stream().map(SystemPermission::getActionId).
                distinct().collect(Collectors.toList());
        updateActionReferencesForExhibition(actionIds);
        // Get the resource ids
        List<Long> resourceIds = page.getResults().stream().map(SystemPermission::getResourceId).
                distinct().collect(Collectors.toList());
        updateResourceReferencesForExhibition(resourceIds);
        return page;
    }

    /**
     * Check if the actions are already mapped in the internal cache.
     * If not, retrieve the Actions that will be shown in the data grid for the current page
     * @param actionIds List containing action identifiers (Obtained from ResourceAction pagination)
     */
    protected void updateActionReferencesForExhibition(List<Long> actionIds) throws SystemException {
        // Filtering non mapped action ids
        List<Long> ids = actionIds.stream().filter(id -> !actionMapRef.containsKey(id)).
                collect(Collectors.toList());
        if (!ids.isEmpty()) {
            actionRESTServiceAccess.getActionsByIds(ids).forEach(r -> actionMapRef.put(r.getId(), r));
        }
    }

    /**
     * Check if the resources are already mapped in the internal cache.
     * If not, retrieve the Resources that will be shown in the data grid for the current page
     * @param resourceIds List containing resource identifiers (Obtained from ResourceAction pagination)
     */
    protected void updateResourceReferencesForExhibition(List<Long> resourceIds) throws SystemException {
        // Filtering non mapped action ids
        List<Long> ids = getNonMappedIds(resourceIds, resourceMapRef);
        if (!ids.isEmpty()) {
            resourceRESTServiceAccess.getResourcesByIds(ids).forEach(t -> resourceMapRef.put(t.getId(), t));
        }
    }

    /**
     * Retrieve the non mapped ids (either for action or resource)
     * @param originalIds A list of ids (for resource or for actions)
     * @param map eventually already contains reference of mapped object for the mentioned ids
     * @return a filtered list of ids
     */
    protected List<Long> getNonMappedIds(List<Long> originalIds, Map<Long, ?> map) {
        return originalIds.stream().filter(id -> !map.containsKey(id)).
                collect(Collectors.toList());
    }

    /**
     * Retrieve the resource name taking in consideration a resource identifier
     * @param resourceId resource identifier
     * @return the resource name (if the resource exists), otherwise returns null
     */
    public String getResourceName(Long resourceId) {
        return resourceMapRef.getOrDefault(resourceId, EMPTY_RESOURCE).getName();
    }

    /**
     * Retrieve the action name taking in consideration a action identifier
     * @param actionId action identifier
     * @return the action name (if the action exists), otherwise returns null
     */
    public String getActionName(Long actionId) {
        return actionMapRef.getOrDefault(actionId, EMPTY_ACTION).getName();
    }

}
