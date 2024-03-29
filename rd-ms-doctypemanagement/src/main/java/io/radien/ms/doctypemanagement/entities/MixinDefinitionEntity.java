/*
 * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
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

package io.radien.ms.doctypemanagement.entities;

import io.radien.ms.doctypemanagement.client.entities.MixinDefinitionDTO;
import java.util.List;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Table(name = "MIX_TYP01")
public class MixinDefinitionEntity extends MixinDefinitionDTO {
    public MixinDefinitionEntity(){ }

    public MixinDefinitionEntity(MixinDefinitionDTO mixinDefinition){
        super(mixinDefinition);
    }

    @Id
    @TableGenerator(name = "GEN_SEQ_MIX_TYP01", allocationSize = 2000)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "GEN_SEQ_MIX_TYP01")
    @Override
    public Long getId() {
        return super.getId();
    }

    @Column
    @Override
    public String getName() {
        return super.getName();
    }

    @Column
    @Override
    public String getNamespace() {
        return super.getNamespace();
    }

    @ElementCollection
    @CollectionTable(name = "MIX_TYP01_PROP_TYP01", joinColumns = @JoinColumn(name = "ID"))
    @Override
    public List<Long> getPropertyDefinitions() {
        return super.getPropertyDefinitions();
    }

    @Column
    @Override
    public boolean isAbstract() {
        return super.isAbstract();
    }

    @Column
    @Override
    public boolean isQueryable() {
        return super.isQueryable();
    }

    @Column
    @Override
    public boolean isMixin() {
        return super.isMixin();
    }
}
