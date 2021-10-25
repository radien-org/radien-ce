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
package io.radien.ms.permissionmanagement.client.entities;

import io.radien.api.model.permission.SystemActionSearchFilter;
import io.radien.api.search.SearchableByIds;
import java.util.Collection;

/**
 * Encapsulates the parameters applied to search for actions
 *
 * @author Newton Carvalho
 */
public class ActionSearchFilter extends SearchableByIds implements SystemActionSearchFilter {

    private String name;

    /**
     * Action search filter object empty constructor
     */
    public ActionSearchFilter(){}

    /**
     * Action search filter object constructor
     * @param name to be found
     * @param ids to be searched and found
     * @param isExact should the requested value be exact to the given one
     * @param isLogicConjunction true in case search option is and conjunction
     */
    public ActionSearchFilter(String name,
                              Collection<Long> ids,
                              boolean isExact, boolean isLogicConjunction) {
        super(ids, isExact, isLogicConjunction);
        this.name = name;
    }

    /**
     * Action search filter name getter
     * @return the action search filter name
     */
    @Override
    public String getName() { return name;}

    /**
     * Action search filter name setter
     * @param name to be set
     */
    @Override
    public void setName(String name) { this.name = name; }
}
