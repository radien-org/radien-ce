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

import io.radien.api.model.linked.authorization.SystemLinkedAuthorizationSearchFilter;

/**
 * Linked Authorization Search filter object for searching criteria
 *
 * @author Bruno Gama
 */
public class LinkedAuthorizationSearchFilter implements SystemLinkedAuthorizationSearchFilter {

    private Long tenantId;
    private Long permissionId;
    private Long roleId;
    private Long userId;

    private boolean isLogicConjunction;

    /**
     * Linked Authorization search filter empty constructor
     */
    public LinkedAuthorizationSearchFilter() {}

    /**
     * Linked Authorization search filter constructor with specified fields
     * @param tenantId to be search
     * @param permissionId to be search
     * @param roleId to be search
     * @param userId to be search
     * @param isLogicConjunction true in case search option is and conjunction
     */
    public LinkedAuthorizationSearchFilter(Long tenantId, Long permissionId, Long roleId, Long userId, boolean isLogicConjunction) {
        this.tenantId = tenantId;
        this.permissionId = permissionId;
        this.roleId = roleId;
        this.userId = userId;
        this.isLogicConjunction = isLogicConjunction;
    }

    /**
     * Linked Authorization search filter get tenant id
     * @return tenant id for search filter
     */
    @Override
    public Long getTenantId() {
        return tenantId;
    }

    /**
     * Linked Authorization tenant id set
     * @param tenantId to be set and replace
     */
    @Override
    public void setTenantId(Long tenantId) {
        this.tenantId=tenantId;
    }

    /**
     * Linked Authorization search filter get permission id
     * @return permission id for search filter
     */
    @Override
    public Long getPermissionId() {
        return permissionId;
    }

    /**
     * Linked Authorization permission id set
     * @param permissionId to be set and replace
     */
    @Override
    public void setPermissionId(Long permissionId) {
        this.permissionId=permissionId;
    }

    /**
     * Linked Authorization search filter get role id
     * @return role id for search filter
     */
    @Override
    public Long getRoleId() {
        return roleId;
    }

    /**
     * Linked Authorization role id set
     * @param roleId to be set and replace
     */
    @Override
    public void setRoleId(Long roleId) {
        this.roleId=roleId;
    }

    /**
     * Linked Authorization search filter get is logical conjunction
     * @return true or false value
     */
    @Override
    public boolean isLogicConjunction() {
        return isLogicConjunction;
    }

    /**
     * Linked Authorization is logical conjunction set
     * @param logicConjunction to be set and replace
     */
    @Override
    public void setLogicConjunction(boolean logicConjunction) {
        isLogicConjunction=logicConjunction;
    }

    /**
     * Linked Authorization search filter get user id
     * @return user id for search filter
     */
    @Override
    public Long getUserId() { return userId; }

    /**
     * Linked Authorization user id set
     * @param userId to be set and replace
     */
    @Override
    public void setUserId(Long userId) { this.userId = userId; }
}
