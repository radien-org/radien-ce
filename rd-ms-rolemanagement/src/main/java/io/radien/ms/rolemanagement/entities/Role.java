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
import java.util.Date;

/**
 * Role Entity
 *
 * @author Bruno Gama
 */
@Entity
@Table(name = "ROL01")
public class Role extends io.radien.ms.rolemanagement.client.entities.Role {

    private static final long serialVersionUID = -725339588872652151L;

    public Role(){ }

    public Role(io.radien.ms.rolemanagement.client.entities.Role role){
        super(role);
    }

    @Id
    @TableGenerator(name = "GEN_SEQ_ROL01", allocationSize = 100)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "GEN_SEQ_ROL01")
    @Override
    public Long getId() {
        return super.getId();
    }

    @Column(unique = true)
    @Override
    public String getName() {
        return super.getName();
    }

    @Column
    @Override
    public String getDescription() {
        return super.getDescription();
    }

    @Column
    @Override
    public Date getTerminationDate() {
        return super.getTerminationDate();
    }
}
