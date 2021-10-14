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
package io.radien.ms.tenantmanagement.client.entities;

import io.radien.api.model.AbstractModel;
import io.radien.api.model.tenant.SystemActiveTenant;

/**
 * Active Tenant object constructor class and fields
 *
 * @author Bruno Gama
 */
public class ActiveTenant extends AbstractModel implements SystemActiveTenant {

	private Long id;
    private Long userId;
    private Long tenantId;

    public ActiveTenant(){
    }

    /**
     * Single and independent field for active tenant constructor
     * @param id to be used
     * @param userId user id to whom this tenant is active
     * @param tenantId tenant id which the user has activated
     * @param tenantName tenant name which the user has activated
     * @param isTenantActive in case the following user has the record tenant active this value should be return true
     */
    public ActiveTenant(Long id, Long userId, Long tenantId) {
        this.id = id;
        this.userId = userId;
        this.tenantId = tenantId;
    }

    /**
     * Active Tenant constructor
     * @param activeTenant information to be created
     */
    public ActiveTenant(ActiveTenant activeTenant) {
        this.id = activeTenant.getId();
        this.userId = activeTenant.getUserId();
        this.tenantId = activeTenant.getTenantId();
    }

    /**
     * Getter for the active tenant id
     * @return id to be retrieved
     */
    @Override
    public Long getId() {
        return id;
    }

    /**
     * Setter for the active tenant id
     * @param id to be set
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Getter for the user id
     * @return the user id in the active tenant information
     */
    @Override
    public Long getUserId() {
        return userId;
    }

    /**
     * Setter for the user id
     * @param userId to be set
     */
    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * Getter for the tenant id
     * @return the tenant id in the active tenant information
     */
    @Override
    public Long getTenantId() {
        return tenantId;
    }

    /**
     * Setter for the tenant id
     * @param tenantId to be set
     */
    @Override
    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }
}
