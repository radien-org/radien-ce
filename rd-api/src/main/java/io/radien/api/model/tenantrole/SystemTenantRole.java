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
package io.radien.api.model.tenantrole;

import io.radien.api.Model;

/**
 * Contract describing the association between a Role and Tenant
 *
 * @author Newton Carvalho
 */
public interface SystemTenantRole extends Model {

    /**
     * System Tenant role role id getter
     * @return system tenant role id
     */
    Long getRoleId();

    /**
     * System tenant role role id setter
     * @param roleId to be set
     */
    void setRoleId(Long roleId);

    /**
     * System tenant role tenant id getter
     * @return system tenant role tenant id
     */
    Long getTenantId();

    /**
     * System tenant role tenant id setter
     * @param tenantId to be set
     */
    void setTenantId(Long tenantId);
}
