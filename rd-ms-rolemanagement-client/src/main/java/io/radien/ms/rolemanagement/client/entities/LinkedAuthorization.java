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

import io.radien.api.model.linked.authorization.AbstractLinkedAuthorizationModel;
import io.radien.api.model.linked.authorization.SystemLinkedAuthorization;

import java.util.Objects;

/**
 * Linked Authorization Object
 *
 * @author Bruno Gama
 */
public class LinkedAuthorization extends AbstractLinkedAuthorizationModel implements SystemLinkedAuthorization {

    private static final long serialVersionUID = -68109365455864899L;

    private Long id;

    private Long tenantId;
    private Long permissionId;
    private Long roleId;
    private Long userId;

    /**
     * Linked Authorization empty constructor
     */
    public LinkedAuthorization() {}

    /**
     * Linked Authorization constructor
     * @param tenancyCtrl fields to create a new Linked authorization object
     */
    public LinkedAuthorization(LinkedAuthorization tenancyCtrl) {
        this.id=tenancyCtrl.getId();
        this.tenantId=tenancyCtrl.getTenantId();
        this.permissionId=tenancyCtrl.getPermissionId();
        this.roleId = tenancyCtrl.getRoleId();
        this.userId = tenancyCtrl.getUserId();
        this.setCreateDate(tenancyCtrl.getCreateDate());
        this.setCreateUser(tenancyCtrl.getCreateUser());
        this.setLastUpdateUser(tenancyCtrl.getLastUpdateUser());
        this.setLastUpdate(tenancyCtrl.getLastUpdate());
    }

    /**
     * Linked authorization id getter
     * @return linked authorization id
     */
    @Override
    public Long getId() {
        return id;
    }

    /**
     * Linked authorization id setter
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Linked authorization tenant id getter
     * @return linked authorization tenant id
     */
    @Override
    public Long getTenantId() {
        return tenantId;
    }

    /**
     * Linked authorization tenant id setter
     */
    @Override
    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * Linked authorization permission id getter
     * @return linked authorization permission id
     */
    @Override
    public Long getPermissionId() {
        return permissionId;
    }

    /**
     * Linked authorization permission id setter
     */
    @Override
    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
    }

    /**
     * Linked authorization role id getter
     * @return linked authorization role id
     */
    @Override
    public Long getRoleId() {
        return roleId;
    }

    /**
     * Linked authorization role id setter
     */
    @Override
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    /**
     * Linked authorization user id getter
     * @return linked authorization user id
     */
    @Override
    public Long getUserId() {
        return userId;
    }

    /**
     * Linked authorization user id setter
     */
    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinkedAuthorization that = (LinkedAuthorization) o;
        return id.equals(that.id) && tenantId.equals(that.tenantId) && permissionId.equals(that.permissionId) && roleId.equals(that.roleId) && userId.equals(that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tenantId, permissionId, roleId, userId);
    }
}
