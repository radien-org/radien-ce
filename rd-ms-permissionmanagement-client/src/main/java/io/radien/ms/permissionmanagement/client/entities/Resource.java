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
package io.radien.ms.permissionmanagement.client.entities;

import io.radien.api.model.permission.AbstractResourceModel;

/**
 * Entity that corresponds to the Resource
 *
 * @author Newton Carvalho
 */
public class Resource extends AbstractResourceModel {

    private Long id;
    private String name;

    /**
     * Resource empty constructor
     */
    public Resource() {}

    /**
     * Resource constructor
     * @param a resource to be added or created
     */
    public Resource(Resource a) {
        this.id = a.getId();
        this.name = a.getName();
    }

    /**
     * Resource id getter
     * @return resource id
     */
    @Override
    public Long getId() {
        return id;
    }

    /**
     * Resource id setter
     * @param id to be set
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Resource name getter
     * @return resource name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Resource name setter
     * @param name to be set
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }
}