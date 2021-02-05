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

import io.radien.api.model.permission.SystemActionType;

import java.util.Arrays;

public enum ActionType implements SystemActionType {
    CREATE(4l, "CREATE"),

    UPDATE(5l, "UPDATE"),

    DELETE(6L, "DELETE"),

    LIST(7l, "LIST"),

    READ(1L, "READ"),

    WRITE(2l, "WRITE"),

    EXECUTION(3l, "EXECUTION");


    private Long id;
    private String name;

    private ActionType(Long id, String name) {
        setId(id);
        setName(name);
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) { this.id = id; }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) { this.name = name; }

    public static ActionType getByName(String name) {
        return Arrays.stream(ActionType.values()).
                filter(a -> a.getName().equals(name)).findFirst().orElse(null);
    }

    public static ActionType getById(Long id) {
        return Arrays.stream(ActionType.values()).
                filter(a -> a.getId().equals(id)).findFirst().orElse(null);
    }
}
