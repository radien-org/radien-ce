/*
 * Copyright (c) 2016-present radien.io & its legal owners. All rights reserved.
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
 * Possible pre defined permissions and information.
 * More permissions can appear but this ones will be used for system purposes
 * @author Newton Carvalho
 */
public enum SystemPermissionsEnum {

    USER_MANAGEMENT_CREATE("User Management - Create"),
    USER_MANAGEMENT_READ("User Management - Read"),
    USER_MANAGEMENT_UPDATE("User Management - Update"),
    USER_MANAGEMENT_DELETE("User Management - Delete"),
    USER_MANAGEMENT_ALL("User Management - All"),

    ROLES_MANAGEMENT_CREATE("Roles Management - Create"),
    ROLES_MANAGEMENT_READ("Roles Management - Read"),
    ROLES_MANAGEMENT_UPDATE("Roles Management - Update"),
    ROLES_MANAGEMENT_DELETE("Roles Management - Delete"),
    ROLES_MANAGEMENT_ALL("Roles Management - All"),

    PERMISSION_MANAGEMENT_CREATE("Permission Management - Create"),
    PERMISSION_MANAGEMENT_READ("Permission Management - Read"),
    PERMISSION_MANAGEMENT_UPDATE("Permission Management - Update"),
    PERMISSION_MANAGEMENT_DELETE("Permission Management - Delete"),
    PERMISSION_MANAGEMENT_ALL("Permission Management - All"),

    RESOURCE_MANAGEMENT_CREATE("Resource Management - Create"),
    RESOURCE_MANAGEMENT_READ("Resource Management - Read"),
    RESOURCE_MANAGEMENT_UPDATE("Resource Management - Update"),
    RESOURCE_MANAGEMENT_DELETE("Resource Management - Delete"),
    RESOURCE_MANAGEMENT_ALL("Resource Management - All"),

    ACTION_MANAGEMENT_CREATE("Action Management - Create"),
    ACTION_MANAGEMENT_READ("Action Management - Read"),
    ACTION_MANAGEMENT_UPDATE("Action Management - Update"),
    ACTION_MANAGEMENT_DELETE("Action Management - Delete"),
    ACTION_MANAGEMENT_ALL("Action Management - All"),

    TENANT_MANAGEMENT_CREATE("Tenant Management - Create"),
    TENANT_MANAGEMENT_READ("Tenant Management - Read"),
    TENANT_MANAGEMENT_UPDATE("Tenant Management - Update"),
    TENANT_MANAGEMENT_DELETE("Tenant Management - Delete"),
    TENANT_MANAGEMENT_ALL("Tenant Management - All"),

    TENANT_ROLE_MANAGEMENT_CREATE("Tenant Role Management - Create"),
    TENANT_ROLE_MANAGEMENT_READ("Tenant Role Management - Read"),
    TENANT_ROLE_MANAGEMENT_UPDATE("Tenant Role Management - Update"),
    TENANT_ROLE_MANAGEMENT_DELETE("Tenant Role Management - Delete"),
    TENANT_ROLE_MANAGEMENT_ALL("Tenant Role Management - All"),

    TENANT_ROLE_PERMISSION_MANAGEMENT_CREATE("Tenant Role Permission Management - Create"),
    TENANT_ROLE_PERMISSION_MANAGEMENT_READ("Tenant Role Permission Management - Read"),
    TENANT_ROLE_PERMISSION_MANAGEMENT_UPDATE("Tenant Role Permission Management - Update"),
    TENANT_ROLE_PERMISSION_MANAGEMENT_DELETE("Tenant Role Permission Management - Delete"),
    TENANT_ROLE_PERMISSION_MANAGEMENT_ALL("Tenant Role Permission Management - All"),

    TENANT_ROLE_USER_MANAGEMENT_CREATE("Tenant Role User Management - Create"),
    TENANT_ROLE_USER_MANAGEMENT_READ("Tenant Role User Management - Read"),
    TENANT_ROLE_USER_MANAGEMENT_UPDATE("Tenant Role User Management - Update"),
    TENANT_ROLE_USER_MANAGEMENT_DELETE("Tenant Role User Management - Delete"),
    TENANT_ROLE_USER_MANAGEMENT_ALL("Tenant Role User Management - All"),

    THIRD_PARTY_PASSWORD_MANAGEMENT_CREATE("Third Party Password Management - Create"),
    THIRD_PARTY_PASSWORD_MANAGEMENT_READ("Third Party Password Management - Read"),
    THIRD_PARTY_PASSWORD_MANAGEMENT_UPDATE("Third Party Password Management - Update"),
    THIRD_PARTY_PASSWORD_MANAGEMENT_DELETE("Third Party Password Management - Delete"),
    THIRD_PARTY_PASSWORD_MANAGEMENT_ALL("Third Party Password Management - All");

    private String permissionName;


    /**
     * System Permission Enumeration constructor
     * @param permissionName Role Identifier
     */
    SystemPermissionsEnum(String permissionName) {
        this.permissionName = permissionName;
    }

    /**
     * Get Permission specific name
     * @return permission name as string value
     */
    public String getPermissionName() {
        return permissionName;
    }

}
