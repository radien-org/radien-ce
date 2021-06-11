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
package io.radien.ms.permissionmanagement.client.entities;

import io.radien.api.model.permission.SystemResourceSearchFilter;

/**
 * Encapsulates the parameters applied to search for actions
 *
 * @author Newton Carvalho
 */
public class ResourceSearchFilter implements SystemResourceSearchFilter {

    private String name;
    private boolean isExact;
    private boolean isLogicConjunction;

    /**
     * Resource search filter empty constructor
     */
    public ResourceSearchFilter(){}

    /**
     * Resource search filter constructor
     * @param name to be searched
     * @param isExact should the search value match exactly or not
     * @param isLogicConjunction true in case search option is and conjunction
     */
    public ResourceSearchFilter(String name,
                                boolean isExact, boolean isLogicConjunction) {
        this.name = name;
        this.isExact = isExact;
        this.isLogicConjunction = isLogicConjunction;
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

    /**
     * Resource search filter is search to be exact getter
     * @return the resource search filter value that indicates if the search should be exact or not
     */
    @Override
    public boolean isExact() {
        return isExact;
    }

    /**
     * Resource search filter is search to be exact setter
     * @param exact to be set
     */
    @Override
    public void setExact(boolean exact) {
        isExact = exact;
    }

    /**
     * Resource search filter is logical conjunction getter
     * @return Resource search filter is logical conjunction
     */
    @Override
    public boolean isLogicConjunction() {
        return isLogicConjunction;
    }

    /**
     * Resource search filter is logical conjunction setter
     * @param logicConjunction the logical conjunction value if true is an and if false is a or
     */
    @Override
    public void setLogicConjunction(boolean logicConjunction) {
        isLogicConjunction = logicConjunction;
    }
}
