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
package io.radien.api.model.tenant;

import java.util.Collection;

/**
 * System Tenant search filter interface class
 *
 * @author Bruno Gama
 */
public interface SystemTenantSearchFilter {

    /**
     * Tenant search filter get ids
     * @return ids for search filter
     */
    Collection<Long> getIds();

    /**
     * Tenant search filter ids setter
     * @param ids to be set and replace
     */
    void setIds(Collection<Long> ids);

    /**
     * System tenant search filter name getter
     * @return the system tenant search filter name
     */
    String getName();

    /**
     * System tenant search filter name setter
     * @param name to be set
     */
    void setName(String name);

    /**
     * System tenant search filter tenant type getter
     * @return the system tenant search filter tenant type
     */
    SystemTenantType getTenantType();

    /**
     * System tenant search filter tenant type setter
     * @param tenantType to be set
     */
    void setTenantType(SystemTenantType tenantType);
}
