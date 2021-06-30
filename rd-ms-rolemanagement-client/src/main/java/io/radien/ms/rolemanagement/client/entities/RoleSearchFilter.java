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
package io.radien.ms.rolemanagement.client.entities;

import io.radien.api.model.role.SystemRoleSearchFilter;
import io.radien.api.search.SearchableByIds;

import java.util.Collection;

/**
 * Role search object constructor
 *
 * @author Bruno Gama
 */
public class RoleSearchFilter extends SearchableByIds implements SystemRoleSearchFilter {

    private String name;
    private String description;

    private boolean isExact;
    private boolean isLogicConjunction;

    /**
     * Role search filter empty constructor
     */
    public RoleSearchFilter() {}

    /**
     * Role search filter constructor with specified fields
     * @param name to be search
     * @param description to be search
     * @param ids to be search
     * @param isExact to be search
     * @param isLogicConjunction true in case search option is and conjunction
     */
    public RoleSearchFilter(String name, String description, Collection<Long> ids, boolean isExact, boolean isLogicConjunction) {
        super(ids);
        this.name = name;
        this.description = description;
        this.isExact = isExact;
        this.isLogicConjunction = isLogicConjunction;
    }

    /**
     * Role search filter get name
     * @return name for search filter
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Role search filter name setter
     * @param name to be set and replace
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Role search filter get description
     * @return name for search filter
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Role search filter description setter
     * @param description to be set and replace
     */
    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Role search filter get is exact
     * @return true or false
     */
    @Override
    public boolean isExact() {
        return isExact;
    }

    /**
     * Role search filter is exact setter
     * @param exact to be set and replace
     */
    @Override
    public void setExact(boolean exact) {
        isExact = exact;
    }

    /**
     * Role search filter get is logical conjunction
     * @return true or false
     */
    @Override
    public boolean isLogicConjunction() {
        return isLogicConjunction;
    }

    /**
     * Role search filter is logicConjunction setter
     * @param logicConjunction to be set and replace
     */
    @Override
    public void setLogicConjunction(boolean logicConjunction) {
        isLogicConjunction = logicConjunction;
    }
}
