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

import io.radien.api.model.role.AbstractRoleModel;
import io.radien.api.model.role.SystemRole;

import java.util.Date;

/**
 * Client Role constructor
 *
 * @author Bruno Gama
 */
public class Role extends AbstractRoleModel implements SystemRole {

    private static final long serialVersionUID = -4647715622707104314L;

    private Long id;

    private String name;
    private String description;

    private Date terminationDate;

    public Role(){}

    public Role(Role role) {
        this.id=role.getId();
        this.name=role.getName();
        this.description=role.getDescription();
        this.terminationDate = role.getTerminationDate();
        this.setCreateDate(role.getCreateDate());
        this.setCreateUser(role.getCreateUser());
        this.setLastUpdateUser(role.getLastUpdateUser());
        this.setLastUpdate(role.getLastUpdate());
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id=id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name=name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description=description;
    }

    public Date getTerminationDate() {
        return terminationDate;
    }

    public void setTerminationDate(Date terminationDate) {
        this.terminationDate = terminationDate;
    }
}
