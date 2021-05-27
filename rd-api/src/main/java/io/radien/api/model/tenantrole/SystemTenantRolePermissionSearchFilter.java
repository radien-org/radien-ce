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
 * Contract that describes a filter to be applied for Tenant vs Role vs Permission associations
 *
 * @author Newton Carvalho
 */
public interface SystemTenantRolePermissionSearchFilter {

    /**
     * System tenant role permission search filter tenant role id getter
     * @return System tenant role permission search filter tenant role id
     */
    Long getTenantRoleId();

    /**
     * System tenant role permission search filter tenant role id setter
     * @param tenantRoleId to be set
     */
    void setTenantRoleId(Long tenantRoleId);

    /**
     * System tenant role permission search filter permission id getter
     * @return System tenant role permission search filter permission id
     */
    Long getPermissionId();

    /**
     * System tenant role permission search filter permission id setter
     * @param permissionId to be set
     */
    void setPermissionId(Long permissionId);

    /**
     * System tenant role permission search filter is exact search getter
     * @return the system tenant role permission search filter is exact value
     */
    public boolean isExact();

    /**
     * System tenant role permission search filter is exact setter
     * @param exact if true the search needs to be exactly as the given parameters
     */
    public void setExact(boolean exact);

    /**
     * System tenant role permission search filter is logical conjunction getter
     * @return the logical conjunction value if true is an and if false is a or
     */
    public boolean isLogicConjunction();

    /**
     * System tenant role permission search filter logical conjunction setter
     * @param logicConjunction the logical conjunction value if true is an and if false is a or
     */
    public void setLogicConjunction(boolean logicConjunction);
}
