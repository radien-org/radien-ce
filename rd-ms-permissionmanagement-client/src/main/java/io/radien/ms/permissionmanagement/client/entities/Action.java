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
import io.radien.api.model.permission.SystemActionType;

public class Action extends AbstractActionModel {
    private Long id;
    private String name;
    private ActionType actionType = ActionType.READ;

    public Action() {}

    public Action(Action a) {
        this.id = a.getId();
        this.name = a.getName();
        this.actionType = a.getType();
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
    public ActionType getType() {
        return actionType;
    }

    @Override
    public void setType(SystemActionType actionType) {
        this.actionType = (ActionType) actionType;
    }
}
