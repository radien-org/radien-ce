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
import io.radien.api.model.tenantrole.SystemTenantRoleUser;
import io.radien.api.model.user.SystemUser;
import io.radien.api.service.tenantrole.TenantRoleUserRESTServiceAccess;
import io.radien.api.service.user.UserRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.usermanagement.client.entities.User;
import io.radien.webapp.LazyAbstractDataModel;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LazyDataModel implemented specifically to attend the exhibition of TenantRoleUser
 * objects (Association between Tenant, Role and USER) under a Lazy Loading approach
 *
 * The idea behind a LazyDataModel is to allow working with considerable hugh datasets,
 * taking in consideration pagination parameters like page size, page number and so on,
 * allowing to load only chunks of data that are really required in a specific moment
 *
 * @author Newton Carvalho
 */
public class LazyTenantRoleUserDataModel extends LazyAbstractDataModel<SystemTenantRoleUser> {

    private static final long serialVersionUID = 7026624480203201435L;

    private final transient TenantRoleUserRESTServiceAccess service;
    private final transient UserRESTServiceAccess userService;
    private final Map<Long, SystemUser> userMapRef;

    private Long tenantId = null;
    private Long roleId = null;

    private static Logger log = LoggerFactory.getLogger(LazyTenantRoleUserDataModel.class);

    private static final SystemUser DEFAULT_USER = new User();

    /**
     * The idea for this constructor is to allow fetching TenantRoleUser relations
     * existent for a tenantRole id
     * @param tenantRoleUserRESTServiceAccess TenantRoleUserRESTServiceAccess rest client to perform the role of a
     *                DataService, providing (or retrieving) data to assembly the datasource
     * @param userRESTServiceAccess auxiliary service that must be use to retrieve information
     *                              regarding the user contained in the TenantRoleUser collection
     */
    public LazyTenantRoleUserDataModel(TenantRoleUserRESTServiceAccess tenantRoleUserRESTServiceAccess,
                                       UserRESTServiceAccess userRESTServiceAccess) {
        this.service = tenantRoleUserRESTServiceAccess;
        this.userService = userRESTServiceAccess;
        this.userMapRef = new HashMap<>();
    }

    @Override
    public SystemTenantRoleUser getRowData(String rowKey) {
        return !rowKey.equals("null") ? super.getRowData(rowKey) : null;
    }

    /**
     * Core method that performs the page loading taking in consideration
     * the parameters described bellow.
     * @param offset parameter that helps to calculate the page number
     * @param pageSize corresponds to the page size.
     * @param sortBy map containing references to properties/columns that may guide the data sort
     * @param filterBy map containing references to properties/columns that may guide a filtering process
     * @return list containing TenantRoleUser associations corresponding to a page
     */
    @Override
    public Page<? extends SystemTenantRoleUser> getData(int offset, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) throws SystemException {
        Page<? extends SystemTenantRoleUser> page = new Page<>();
        // Just retrieve if we have tenant or role information
        if (tenantId != null || roleId != null) {
            page = service.getUsers(tenantId, roleId,
                    (offset/pageSize)+1, pageSize);
            // Get the user ids
            List<Long> userIds = page.getResults().stream().map(SystemTenantRoleUser::getUserId).
                    distinct().collect(Collectors.toList());
            updateUserReferencesForExhibition(userIds);
        }
        return page;
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
     * If necessary, retrieve the Users that will be shown in the data grid for the current page
     * @param userIds List containing user identifiers (Obtained from TenantRoleUser pagination)
     */
    protected void updateUserReferencesForExhibition(List<Long> userIds) throws SystemException {
        // Filtering non mapped user ids
        List<Long> ids = getNonMappedIds(userIds, userMapRef);
        if (!ids.isEmpty()) {
            userService.getUsersByIds(ids).forEach(u -> userMapRef.put(u.getId(), u));
        }
    }

    /**
     * Retrieve the user taking in consideration the informed id
     * @param userId user identifier
     * @return the user (if it exists), otherwise returns an empty pojo
     */
    public SystemUser getUser(Long userId) {
        if (!userMapRef.containsKey(userId)) {
            log.warn("User not found for id {}", userId);
            return DEFAULT_USER;
        }
        return userMapRef.get(userId);
    }

    /**
     * Getter for tenantId property
     * @return long value that corresponds to the tenantRoleId property
     */
    public Long getTenantId() {
        return tenantId;
    }

    /**
     * Setter for tenantId property
     * @param tenantId  long value that corresponds to the tenantId property
     */
    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * Getter for roleId property
     * @return long value that corresponds to the roleId property
     */
    public Long getRoleId() { return roleId; }

    /**
     * Setter for roleId property
     * @param roleId long value that corresponds to the roleId property
     */
    public void setRoleId(Long roleId) { this.roleId = roleId; }
}