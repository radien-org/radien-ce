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

import io.radien.api.model.permission.SystemPermissionSearchFilter;

/**
 * @author Newton Carvalho
 * Encapsulates the parameters applied to search for actions
 */
public class PermissionSearchFilter implements SystemPermissionSearchFilter {

    private String name;
    private Long actionId;
    private Long resourceId;
    private boolean isExact;
    private boolean isLogicConjunction;

    public PermissionSearchFilter(){}

    public PermissionSearchFilter(String name, Long actionId, Long resourceId,
                                  boolean isExact, boolean isLogicConjunction) {
        this.name = name;
        this.actionId = actionId;
        this.resourceId = resourceId;
        this.isExact = isExact;
        this.isLogicConjunction = isLogicConjunction;
    }

    @Override
    public String getName() { return name;}

    @Override
    public void setName(String name) { this.name = name; }

    @Override
    public Long getActionId() { return actionId; }

    @Override
    public void setActionId(Long actionId) { this.actionId = actionId; }

    @Override
    public Long getResourceId() { return resourceId; }

    @Override
    public void setResourceId(Long resourceId) { this.resourceId = resourceId; }

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
