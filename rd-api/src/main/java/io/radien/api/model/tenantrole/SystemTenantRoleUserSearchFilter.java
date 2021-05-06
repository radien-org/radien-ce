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
 * Contract that describes a filter to be applied
 * for Tenant (vs Role) vs User associations
 * @author Newton Carvalho
 */
public interface SystemTenantRoleUserSearchFilter {

    Long getTenantRoleId();
    void setTenantRoleId(Long tenantRoleId);

    Long getUserId();
    void setUserId(Long userId);

    boolean isExact();
    void setExact(boolean exact);

    boolean isLogicConjunction();
    void setLogicConjunction(boolean logicConjunction);
}
