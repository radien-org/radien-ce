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

package io.radien.api;

/**
 * Declaration of all the entities variables names and types
 * @author Bruno Gama
 **/
public enum SystemVariables {

    ID("id"),
    NAME("name"),
    LOGON("logon"),
    USER_EMAIL("userEmail"),
    SUB("sub"),
    TENANT_ID("tenantId"),
    DESCRIPTION("description"),
    ACTION_ID("actionId"),
    RESOURCE_ID("resourceId"),
    ROLE_ID("roleId"),
    USER_ID("userId"),
    TENANT_ROLE_ID("tenantRoleId"),
    TENANT_NAME("tenantName"),
    TENANT_TYPE("tenantType"),
    PARENT_ID("parentId"),
    PERMISSION_ID("permissionId");

    private final String fieldName;

    /**
     * Tenant error code messages constructor
     *
     * @param fieldName of the variable
     */
    SystemVariables(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * Converts to string the requested value of the variable
     * @return a string value of the variable
     */
    public String getFieldName() {
        return fieldName;
    }
}
