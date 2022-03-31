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

package io.radien.ms.doctypemanagement.client.entities;

import io.radien.api.model.AbstractModel;
import io.radien.api.model.docmanagement.mixindefinition.SystemMixinDefinition;
import java.util.List;

public class MixinDefinitionDTO extends AbstractModel implements SystemMixinDefinition<Long> {
    private Long id;
    private String name;
    private String namespace = "rd";
    private List<Long> propertyDefinitions;
    private boolean abstrakt;
    private boolean queryable;
    private boolean mixin = true;

    public MixinDefinitionDTO() {}

    public MixinDefinitionDTO(MixinDefinitionDTO mixinDefinition) {
        this.id = mixinDefinition.id;
        this.name = mixinDefinition.name;
        this.namespace = mixinDefinition.namespace;
        this.propertyDefinitions = mixinDefinition.propertyDefinitions;
        this.abstrakt = mixinDefinition.abstrakt;
        this.queryable = mixinDefinition.queryable;
        this.mixin = mixinDefinition.mixin;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public List<Long> getPropertyDefinitions() {
        return propertyDefinitions;
    }

    @Override
    public void setPropertyDefinitions(List<Long> propertyDefinitions) {
        this.propertyDefinitions = propertyDefinitions;
    }

    @Override
    public boolean isAbstract() {
        return abstrakt;
    }

    @Override
    public void setAbstract(boolean abstrakt) {
        this.abstrakt = abstrakt;
    }

    @Override
    public boolean isQueryable() {
        return queryable;
    }

    @Override
    public void setQueryable(boolean queryable) {
        this.queryable = queryable;
    }

    @Override
    public boolean isMixin() {
        return mixin;
    }

    @Override
    public void setMixin(boolean mixin) {
        this.mixin = mixin;
    }

}
