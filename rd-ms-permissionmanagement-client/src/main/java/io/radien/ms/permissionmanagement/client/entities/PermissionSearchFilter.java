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
package io.radien.ms.permissionmanagement.client.entities;

import io.radien.api.model.permission.SystemPermissionSearchFilter;
import io.radien.api.search.SearchableByIds;

import java.util.Collection;

/**
 * Encapsulates the parameters applied to search for actions
 *
 * @author Newton Carvalho
 */
public class PermissionSearchFilter extends SearchableByIds implements SystemPermissionSearchFilter {

    private String name;
    private Long actionId;
    private Long resourceId;

    /**
     * Permission search filter empty constructor
     */
    public PermissionSearchFilter() {
    }

    /**
     * Permission search filter object constructor
     *
     * @param name               to be searched and found
     * @param actionId           to be searched and found
     * @param resourceId         to be searched and found
     * @param ids                to be searched and found
     * @param isExact            should the requested value be exact to the given one
     * @param isLogicConjunction true in case search option is and conjunction
     */
    public PermissionSearchFilter(String name, Long actionId, Long resourceId, Collection<Long> ids,
                                  boolean isExact, boolean isLogicConjunction) {
        super(ids, isExact, isLogicConjunction);
        this.name = name;
        this.actionId = actionId;
        this.resourceId = resourceId;
    }

    /**
     * Permission search filter name getter
     *
     * @return the permission search filter name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Permission search filter name setter
     *
     * @param name to be set
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Permission search filter action id getter
     *
     * @return the permission search filter action id
     */
    @Override
    public Long getActionId() {
        return actionId;
    }

    /**
     * Permission search filter action id setter
     *
     * @param actionId to be set
     */
    @Override
    public void setActionId(Long actionId) {
        this.actionId = actionId;
    }

    /**
     * Permission search filter resource id getter
     *
     * @return the permission search filter resource id
     */
    @Override
    public Long getResourceId() {
        return resourceId;
    }

    /**
     * Permission search filter resource id setter
     *
     * @param resourceId to be set
     */
    @Override
    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }
}
