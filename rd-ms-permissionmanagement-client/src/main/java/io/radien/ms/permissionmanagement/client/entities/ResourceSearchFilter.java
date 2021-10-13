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
package io.radien.ms.permissionmanagement.client.entities;

import io.radien.api.model.permission.SystemResourceSearchFilter;
import io.radien.api.search.SearchableByIds;
import java.util.Collection;

/**
 * Encapsulates the parameters applied to search for actions
 *
 * @author Newton Carvalho
 */
public class ResourceSearchFilter extends SearchableByIds implements SystemResourceSearchFilter {

    private String name;

    /**
     * Resource search filter empty constructor
     */
    public ResourceSearchFilter(){}

    /**
     * Resource search filter constructor
     * @param name to be searched
     * @param ids to be searched and found
     * @param isExact should the search value match exactly or not
     * @param isLogicConjunction true in case search option is and conjunction
     */
    public ResourceSearchFilter(String name,
                                Collection<Long> ids,
                                boolean isExact, boolean isLogicConjunction) {
        super(ids, isExact, isLogicConjunction);
        this.name = name;
    }

    /**
     * Resource search filter name getter
     * @return the resource search filter name
     */
    @Override
    public String getName() { return name;}

    /**
     * Resource search filter name setter
     * @param name to be set
     */
    @Override
    public void setName(String name) { this.name = name; }
}
