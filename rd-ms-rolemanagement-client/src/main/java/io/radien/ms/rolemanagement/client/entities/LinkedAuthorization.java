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

    /**
     * Indicates whether some other object is "equal to" this one.
     * The equals method implements an equivalence relation on non-null object references:
     * @param o the reference object with which to compare.
     * @return if this object is the same as the obj argument; false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinkedAuthorization that = (LinkedAuthorization) o;
        return id.equals(that.id) && tenantId.equals(that.tenantId) && permissionId.equals(that.permissionId) && roleId.equals(that.roleId) && userId.equals(that.userId);
    }

    /**
     * Returns a hash code value for the object. This method is supported for the benefit of hash tables such
     * as those provided by HashMap.
     * The general contract of hashCode is:
     * Whenever it is invoked on the same object more than once during an execution of a Java application,
     * the hashCode method must consistently return the same integer, provided no information used in equals
     * comparisons on the object is modified. This integer need not remain consistent from one execution of an
     * application to another execution of the same application.
     * If two objects are equal according to the equals(Object) method, then calling the hashCode method on each of
     * the two objects must produce the same integer result.
     * It is not required that if two objects are unequal according to the equals(java.lang.Object) method, then
     * calling the hashCode method on each of the two objects must produce distinct integer results. However, the
     * programmer should be aware that producing distinct integer results for unequal objects may improve the
     * performance of hash tables.
     * As much as is reasonably practical, the hashCode method defined by class Object does return distinct integers
     * for distinct objects. (This is typically implemented by converting the internal address of the object into an
     * integer, but this implementation technique is not required by the JavaTM programming language.)
     * @return hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, tenantId, permissionId, roleId, userId);
    }
}
