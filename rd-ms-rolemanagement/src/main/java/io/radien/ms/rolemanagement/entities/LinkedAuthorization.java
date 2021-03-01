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
package io.radien.ms.rolemanagement.entities;

import javax.persistence.*;

/**
 * Linked Authorization between Tenants, Roles and Permissions Entity
 *
 * @author Bruno Gama
 */
@Entity
@Table(name = "LINKAUTH01", uniqueConstraints = @UniqueConstraint(columnNames = {"tenantId, roleId, permissionId, userId"}))
public class LinkedAuthorization extends io.radien.ms.rolemanagement.client.entities.LinkedAuthorization {

    private static final long serialVersionUID = 3141678141603574471L;

    public LinkedAuthorization(){ }

    public LinkedAuthorization(io.radien.ms.rolemanagement.client.entities.LinkedAuthorization tenancyCtrl){
        super(tenancyCtrl);
    }

    @Id
    @TableGenerator(name = "GEN_SEQ_LINKAUTH01", allocationSize = 100)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "GEN_SEQ_LINKAUTH01")
    @Override
    public Long getId() {
        return super.getId();
    }

    @Column
    @Override
    public Long getPermissionId() {
        return super.getPermissionId();
    }

    @Column
    @Override
    public Long getTenantId() {
        return super.getTenantId();
    }

    @Column
    @Override
    public Long getRoleId() {
        return super.getRoleId();
    }

    @Column
    @Override
    public Long getUserId() { return super.getUserId(); }

}
