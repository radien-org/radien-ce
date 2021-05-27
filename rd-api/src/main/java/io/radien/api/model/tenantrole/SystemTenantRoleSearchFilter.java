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
package io.radien.api.model.tenantrole;

/**
 * Contract that describes a filter to be applied for Tenant Role associations
 *
 * @author Newton Carvalho
 */
public interface SystemTenantRoleSearchFilter {

    /**
     * System Tenant Role search filter tenant id getter
     * @return System Tenant Role search filter tenant id
     */
    Long getTenantId();

    /**
     * System Tenant Role search filter tenant id setter
     * @param tenantId to be set
     */
    void setTenantId(Long tenantId);

    /**
     * System Tenant Role search filter role id getter
     * @return System Tenant Role search filter role id
     */
    Long getRoleId();

    /**
     * System Tenant Role search filter role id setter
     * @param roleId to be set
     */
    void setRoleId(Long roleId);

    /**
     * System tenant role search filter is exact search getter
     * @return the system tenant role search filter is exact value
     */
    boolean isExact();

    /**
     * System tenant role search filter is exact setter
     * @param exact if true the search needs to be exactly as the given parameters
     */
    void setExact(boolean exact);

    /**
     * System tenant role search filter is logical conjunction getter
     * @return the logical conjunction value if true is an and if false is a or
     */
    boolean isLogicConjunction();

    /**
     * System tenant role search filter logical conjunction setter
     * @param logicConjunction the logical conjunction value if true is an and if false is a or
     */
    void setLogicConjunction(boolean logicConjunction);
}
