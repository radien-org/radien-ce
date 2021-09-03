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
package io.radien.webapp.tenantrole;

import io.radien.api.entity.Page;
import io.radien.api.model.user.SystemUser;
import io.radien.api.service.tenantrole.TenantRoleUserRESTServiceAccess;
import io.radien.api.service.user.UserRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.webapp.LazyAbstractDataModel;
import java.util.List;
import java.util.Map;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;

/**
 * LazyDataModel implemented specifically to attend the exhibition of tenanting users
 * objects (Users that are associated with Tenants) under a Lazy Loading approach
 *
 * The idea behind a LazyDataModel is to allow working with considerable hugh datasets,
 * taking in consideration pagination parameters like page size, page number and so on,
 * allowing to load only chunks of data that are really required in a specific moment
 *
 * @author Newton Carvalho
 */
public class LazyTenantingUserDataModel extends LazyAbstractDataModel<SystemUser> {

    private static final long serialVersionUID = 7026624480203201435L;

    private final transient TenantRoleUserRESTServiceAccess service;
    private final transient UserRESTServiceAccess userService;

    private Long tenantId = null;


    /**
     * The idea for this constructor is to allow fetching User relations
     * existent for a tenant
     * @param tenantRoleUserRESTServiceAccess TenantRoleUserRESTServiceAccess rest client to perform the role of a
     *                DataService, providing (or retrieving) data to assembly the datasource
     * @param userRESTServiceAccess auxiliary service that must be use to retrieve information
     *                              regarding the user contained in the TenantRoleUser collection
     */
    public LazyTenantingUserDataModel(TenantRoleUserRESTServiceAccess tenantRoleUserRESTServiceAccess,
                                      UserRESTServiceAccess userRESTServiceAccess) {
        this.service = tenantRoleUserRESTServiceAccess;
        this.userService = userRESTServiceAccess;
    }

    @Override
    public SystemUser getRowData(String rowKey) {
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
    public Page<? extends SystemUser> getData(int offset, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) throws SystemException {
        Page<SystemUser> page = new Page<>();
        // Just retrieve if we have tenant or role information
        if (tenantId != null ) {
            Page<Long> pageIds = service.getUsersIds(tenantId, null,
                    (offset/pageSize)+1, pageSize);

            page.setTotalResults(pageIds.getTotalResults());
            page.setCurrentPage(pageIds.getCurrentPage());
            page.setTotalPages(pageIds.getTotalPages());

            List<? extends SystemUser> systemUsers = userService.
                    getUsersByIds((List<Long>) pageIds.getResults());

            page.setResults(systemUsers);
        }
        return page;
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
}