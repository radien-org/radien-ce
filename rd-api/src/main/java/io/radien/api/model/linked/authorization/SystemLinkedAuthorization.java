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

import io.radien.api.Model;

/**
 * System linked authorization interface methods
 *
 * @author Bruno Gama
 */
public interface SystemLinkedAuthorization extends Model {

    /**
     * System Linked Authorization role id getter
     * @return the role id
     */
    public Long getRoleId();

    /**
     * System Linked Authorization role id setter
     * @param roleId to be set
     */
    public void setRoleId(Long roleId);

    /**
     * System Linked Authorization permission id getter
     * @return the permission id
     */
    public Long getPermissionId();

    /**
     * System Linked Authorization permission id setter
     * @param permissionId to be set
     */
    public void setPermissionId(Long permissionId);

    /**
     * System Linked Authorization tenant id getter
     * @return the tenant id
     */
    public Long getTenantId();

    /**
     * System Linked Authorization tenant id setter
     * @param tenantId to be set
     */
    public void setTenantId(Long tenantId);

    /**
     * System Linked Authorization user id getter
     * @return the user id
     */
    public Long getUserId();

    /**
     * System Linked Authorization user id setter
     * @param userId to be set
     */
    public void setUserId(Long userId);
}
