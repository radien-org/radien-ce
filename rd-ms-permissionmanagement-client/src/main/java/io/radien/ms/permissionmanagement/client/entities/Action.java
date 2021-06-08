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
package io.radien.ms.permissionmanagement.client.entities;

import io.radien.api.model.permission.AbstractActionModel;

/**
 * Entity that corresponds to the Action
 *
 * @author Newton Carvalho
 */
public class Action extends AbstractActionModel {
    private Long id;
    private String name;

    /**
     * Action empty constructor
     */
    public Action() {}

    /**
     * Action constructor
     * @param a to be created
     */
    public Action(Action a) {
        this.id = a.getId();
        this.name = a.getName();
    }

    /**
     * Action id getter
     * @return the action id
     */
    @Override
    public Long getId() {
        return id;
    }

    /**
     * Action id setter
     * @param id to be set
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Action name getter
     * @return the action name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Action name setter
     * @param name to be set
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns a string representation of the object. In general, the
     * {@code toString} method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * @return  a string representation of the object.
     */
    @Override
    public String toString() {
        return name;
    }
}