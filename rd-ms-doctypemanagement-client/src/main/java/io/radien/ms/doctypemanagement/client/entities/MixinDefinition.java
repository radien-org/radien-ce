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
import io.radien.api.model.docmanagement.propertydefinition.SystemPropertyDefinition;
import java.util.List;
import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeTypeDefinition;
import javax.jcr.nodetype.PropertyDefinition;

public class MixinDefinition extends AbstractModel implements SystemMixinDefinition<SystemPropertyDefinition>, NodeTypeDefinition {
    private Long id;
    private String name;
    private String namespace;
    private List<SystemPropertyDefinition> propertyDefinitions;
    private boolean abstrakt;
    private boolean queryable;
    private boolean mixin;

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
    public List<SystemPropertyDefinition> getPropertyDefinitions() {
        return propertyDefinitions;
    }

    @Override
    public void setPropertyDefinitions(List<SystemPropertyDefinition> propertyDefinitions) {
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
    public void setAbstrakt(boolean abstrakt) {
        setAbstract(abstrakt);
    }

    @Override
    public boolean isAbstrakt() {
        return isAbstract();
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

    @Override
    public String[] getDeclaredSupertypeNames() {
        return new String[0];
    }

    @Override
    public boolean hasOrderableChildNodes() {
        return true;
    }

    @Override
    public String getPrimaryItemName() {
        return null;
    }

    @Override
    public PropertyDefinition[] getDeclaredPropertyDefinitions() {
        return propertyDefinitions.toArray(new SystemPropertyDefinition[0]);
    }

    @Override
    public NodeDefinition[] getDeclaredChildNodeDefinitions() {
        return null;
    }

}
