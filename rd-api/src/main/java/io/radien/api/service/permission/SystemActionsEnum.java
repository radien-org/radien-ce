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
package io.radien.api.service.permission;

/**
 * Possible pre defined actions and information.
 * More actions can appear but this ones will be used for system purposes
 * @author Newton Carvalho
 */
public enum SystemActionsEnum {

    ACTION_CREATE("Create"),
    ACTION_READ("Read"),
    ACTION_UPDATE("Update"),
    ACTION_DELETE("Delete"),
    ACTION_ALL("All");

    private String actionName;

    /**
     * System Action Enumeration constructor
     * @param actionName Role Identifier
     */
    SystemActionsEnum(String actionName) {
        this.actionName = actionName;
    }

    /**
     * Get Action specific name
     * @return role name as string value
     */
    public String getActionName() {
        return actionName;
    }

}
