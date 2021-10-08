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
package io.radien.ms.rolemanagement.entities;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.TableGenerator;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import java.util.Date;

/**
 * Role Entity table fields
 * @author Bruno Gama
 */
@Entity
@Table(name = "ROL01")
public class RoleEntity extends io.radien.ms.rolemanagement.client.entities.Role {

    private static final long serialVersionUID = -725339588872652151L;

    /**
     * Role entity emty constructor
     */
    public RoleEntity(){ }

    /**
     * Role entity constructor
     * @param role {@link io.radien.ms.rolemanagement.client.entities.Role} to be added/created
     */
    public RoleEntity(io.radien.ms.rolemanagement.client.entities.Role role){
        super(role);
    }

    /**
     * Role entity id table field
     * @return the role id
     */
    @Id
    @TableGenerator(name = "GEN_SEQ_ROL01", allocationSize = 100)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "GEN_SEQ_ROL01")
    @Override
    public Long getId() {
        return super.getId();
    }

    /**
     * Role entity name table field
     * @return the role name
     */
    @Column(unique = true)
    @Override
    public String getName() {
        return super.getName();
    }

    /**
     * Role entity description table field
     * @return the role description
     */
    @Column
    @Override
    public String getDescription() {
        return super.getDescription();
    }

    /**
     * Role entity termination table field
     * @return the role termination date
     */
    @Column
    @Override
    public Date getTerminationDate() {
        return super.getTerminationDate();
    }
}
