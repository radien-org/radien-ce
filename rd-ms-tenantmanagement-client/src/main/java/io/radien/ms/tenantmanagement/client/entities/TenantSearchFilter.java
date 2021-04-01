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
package io.radien.ms.tenantmanagement.client.entities;

import io.radien.api.model.tenant.SystemTenantSearchFilter;
import io.radien.api.model.tenant.SystemTenantType;

/**
 * @author Bruno Gama
 */
public class TenantSearchFilter implements SystemTenantSearchFilter {

    private String name;
    private SystemTenantType tenantType;
    private boolean isExact;
    private boolean isLogicConjunction;

    public TenantSearchFilter(String name, String type, boolean isExact, boolean isLogicalConjunction) {
        this.name = name;
        SystemTenantType tenantType = TenantType.getByName(type);
        this.tenantType = tenantType;
        this.isExact = isExact;
        this.isLogicConjunction = isLogicalConjunction;
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
    public SystemTenantType getType() {
        return tenantType;
    }

    @Override
    public void setType(SystemTenantType tenantType) {
        this.tenantType = tenantType;
    }

    @Override
    public boolean isExact() {
        return isExact;
    }

    @Override
    public void setExact(boolean exact) {
        this.isExact = exact;
    }

    @Override
    public boolean isLogicConjunction() {
        return isLogicConjunction;
    }

    @Override
    public void setLogicConjunction(boolean logicConjunction) {
        this.isLogicConjunction = logicConjunction;
    }
}
