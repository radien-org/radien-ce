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
package io.radien.api.model.linked.authorization;

/**
 * System Linked Authorization search filter interface methods
 *
 * @author Bruno Gama
 */
public interface SystemLinkedAuthorizationSearchFilter {

    /**
     * System Linked Authorization filter tenant id getter
     * @return the tenant id
     */
    Long getTenantId();

    /**
     * System Linked Authorization filter tenant id setter
     * @param tenantId to be set
     */
    void setTenantId(Long tenantId);

    /**
     * System Linked Authorization filter permission id getter
     * @return the permission id
     */
    Long getPermissionId();

    /**
     * System Linked Authorization filter permission id setter
     * @param permissionId to be set
     */
    void setPermissionId(Long permissionId);

    /**
     * System Linked Authorization filter role id getter
     * @return the role id
     */
    Long getRoleId();

    /**
     * System Linked Authorization filter role id setter
     * @param roleId to be set
     */
    void setRoleId(Long roleId);

    /**
     * System Linked Authorization filter user id getter
     * @return the user id
     */
    Long getUserId();

    /**
     * System Linked Authorization filter user id setter
     * @param userId to be set
     */
    void setUserId(Long userId);

    /**
     * System Linked Authorization is logical conjunction getter
     * @return the logical conjunction value if true is an and if false is a or
     */
    boolean isLogicConjunction();

    /**
     * System Linked Authorization filter logical conjunction setter
     * @param logicConjunction the logical conjunction value if true is an and if false is a or
     */
    void setLogicConjunction(boolean logicConjunction);
}
