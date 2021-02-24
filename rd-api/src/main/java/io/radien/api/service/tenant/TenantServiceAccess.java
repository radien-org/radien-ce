/*
 * Copyright (c) 2016-present radien.io & its legal owners. All rights reserved.
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
package io.radien.api.service.tenant;

import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.service.ServiceAccess;
import io.radien.exception.NotFoundException;
import io.radien.exception.UniquenessConstraintException;


import java.util.Collection;
import java.util.List;

/**
 * @author Santana
 */
public interface TenantServiceAccess extends ServiceAccess {

    public SystemTenant get(Long tenantId) ;

    public List<? extends SystemTenant> get(String name);

    public void create(SystemTenant tenant) throws UniquenessConstraintException;

    public void update(SystemTenant tenant) throws UniquenessConstraintException;

    public boolean delete(Long tenantId);

    public void delete(Collection<Long> tenantIds);

    /**
     * Validates if specific requested tenant exists
     * @param tenantId to be searched
     * @return response true if it exists
     */
    public boolean exists(Long tenantId) throws NotFoundException;

}
