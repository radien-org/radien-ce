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

/**
 * @author Bruno Gama
 */
public class RoleSearchFilter implements SystemRoleSearchFilter {

    private String name;
    private String description;

    private boolean isExact;
    private boolean isLogicConjunction;

    public RoleSearchFilter() {}

    public RoleSearchFilter(String name, String description, boolean isExact, boolean isLogicConjunction) {
        this.name = name;
        this.description = description;
        this.isExact = isExact;
        this.isLogicConjunction = isLogicConjunction;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean isExact() {
        return isExact;
    }

    @Override
    public void setExact(boolean exact) {
        isExact = exact;
    }

    @Override
    public boolean isLogicConjunction() {
        return isLogicConjunction;
    }

    @Override
    public void setLogicConjunction(boolean logicConjunction) {
        isLogicConjunction = logicConjunction;
    }
}
