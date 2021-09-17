/*
 * Copyright (c) 2021-present radien GmbH. All rights reserved.
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

import static io.radien.api.service.permission.SystemActionsEnum.ACTION_ALL;
import static io.radien.api.service.permission.SystemActionsEnum.ACTION_CREATE;
import static io.radien.api.service.permission.SystemActionsEnum.ACTION_DELETE;
import static io.radien.api.service.permission.SystemActionsEnum.ACTION_READ;
import static io.radien.api.service.permission.SystemActionsEnum.ACTION_UPDATE;
import static io.radien.api.service.permission.SystemResourcesEnum.ACTION;
import static io.radien.api.service.permission.SystemResourcesEnum.PERMISSION;
import static io.radien.api.service.permission.SystemResourcesEnum.RESOURCE;
import static io.radien.api.service.permission.SystemResourcesEnum.ROLES;
import static io.radien.api.service.permission.SystemResourcesEnum.TENANT;
import static io.radien.api.service.permission.SystemResourcesEnum.TENANT_ROLE;
import static io.radien.api.service.permission.SystemResourcesEnum.TENANT_ROLE_PERMISSION;
import static io.radien.api.service.permission.SystemResourcesEnum.TENANT_ROLE_USER;
import static io.radien.api.service.permission.SystemResourcesEnum.THIRD_PARTY_EMAIL;
import static io.radien.api.service.permission.SystemResourcesEnum.THIRD_PARTY_PASSWORD;
import static io.radien.api.service.permission.SystemResourcesEnum.USER;

/**
 * Possible pre defined permissions and information.
 * More permissions can appear but this ones will be used for system purposes
 * @author Newton Carvalho
 */
public enum SystemPermissionsEnum {

    USER_MANAGEMENT_CREATE("User Management - Create", USER, ACTION_CREATE),
    USER_MANAGEMENT_READ("User Management - Read", USER, ACTION_READ),
    USER_MANAGEMENT_UPDATE("User Management - Update", USER, ACTION_UPDATE),
    USER_MANAGEMENT_DELETE("User Management - Delete", USER, ACTION_DELETE),
    USER_MANAGEMENT_ALL("User Management - All", USER, ACTION_ALL),

    ROLES_MANAGEMENT_CREATE("Roles Management - Create", ROLES, ACTION_CREATE),
    ROLES_MANAGEMENT_READ("Roles Management - Read", ROLES, ACTION_READ),
    ROLES_MANAGEMENT_UPDATE("Roles Management - Update", ROLES, ACTION_UPDATE),
    ROLES_MANAGEMENT_DELETE("Roles Management - Delete", ROLES, ACTION_DELETE),
    ROLES_MANAGEMENT_ALL("Roles Management - All", ROLES, ACTION_ALL),

    PERMISSION_MANAGEMENT_CREATE("Permission Management - Create", PERMISSION, ACTION_CREATE),
    PERMISSION_MANAGEMENT_READ("Permission Management - Read", PERMISSION, ACTION_READ),
    PERMISSION_MANAGEMENT_UPDATE("Permission Management - Update", PERMISSION, ACTION_UPDATE),
    PERMISSION_MANAGEMENT_DELETE("Permission Management - Delete", PERMISSION, ACTION_DELETE),
    PERMISSION_MANAGEMENT_ALL("Permission Management - All", PERMISSION, ACTION_ALL),

    RESOURCE_MANAGEMENT_READ("Resource Management - Read", RESOURCE, ACTION_READ),
    RESOURCE_MANAGEMENT_CREATE("Resource Management - Create", RESOURCE, ACTION_CREATE),
    RESOURCE_MANAGEMENT_UPDATE("Resource Management - Update", RESOURCE, ACTION_UPDATE),
    RESOURCE_MANAGEMENT_DELETE("Resource Management - Delete", RESOURCE, ACTION_DELETE),
    RESOURCE_MANAGEMENT_ALL("Resource Management - All", RESOURCE, ACTION_ALL),

    ACTION_MANAGEMENT_CREATE("Action Management - Create", ACTION, ACTION_CREATE),
    ACTION_MANAGEMENT_READ("Action Management - Read", ACTION, ACTION_READ),
    ACTION_MANAGEMENT_UPDATE("Action Management - Update", ACTION, ACTION_UPDATE),
    ACTION_MANAGEMENT_DELETE("Action Management - Delete", ACTION, ACTION_DELETE),
    ACTION_MANAGEMENT_ALL("Action Management - All", ACTION, ACTION_ALL),

    TENANT_MANAGEMENT_CREATE("Tenant Management - Create", TENANT, ACTION_CREATE),
    TENANT_MANAGEMENT_READ("Tenant Management - Read", TENANT, ACTION_READ),
    TENANT_MANAGEMENT_UPDATE("Tenant Management - Update", TENANT, ACTION_UPDATE),
    TENANT_MANAGEMENT_DELETE("Tenant Management - Delete", TENANT, ACTION_DELETE),
    TENANT_MANAGEMENT_ALL("Tenant Management - All", TENANT, ACTION_ALL),

    TENANT_ROLE_MANAGEMENT_CREATE("Tenant Role Management - Create", TENANT_ROLE, ACTION_CREATE),
    TENANT_ROLE_MANAGEMENT_READ("Tenant Role Management - Read", TENANT_ROLE, ACTION_READ),
    TENANT_ROLE_MANAGEMENT_DELETE("Tenant Role Management - Delete", TENANT_ROLE, ACTION_DELETE),
    TENANT_ROLE_MANAGEMENT_ALL("Tenant Role Management - All", TENANT_ROLE, ACTION_ALL),
    TENANT_ROLE_MANAGEMENT_UPDATE("Tenant Role Management - Update", TENANT_ROLE, ACTION_UPDATE),

    TENANT_ROLE_PERMISSION_MANAGEMENT_CREATE("Tenant Role Permission Management - Create",
            TENANT_ROLE_PERMISSION, ACTION_CREATE),
    TENANT_ROLE_PERMISSION_MANAGEMENT_READ("Tenant Role Permission Management - Read",
            TENANT_ROLE_PERMISSION, ACTION_READ),
    TENANT_ROLE_PERMISSION_MANAGEMENT_UPDATE("Tenant Role Permission Management - Update",
            TENANT_ROLE_PERMISSION, ACTION_UPDATE),
    TENANT_ROLE_PERMISSION_MANAGEMENT_DELETE("Tenant Role Permission Management - Delete",
            TENANT_ROLE_PERMISSION, ACTION_DELETE),
    TENANT_ROLE_PERMISSION_MANAGEMENT_ALL("Tenant Role Permission Management - All",
            TENANT_ROLE_PERMISSION, ACTION_ALL),

    TENANT_ROLE_USER_MANAGEMENT_CREATE("Tenant Role User Management - Create",
            TENANT_ROLE_USER, ACTION_CREATE),
    TENANT_ROLE_USER_MANAGEMENT_READ("Tenant Role User Management - Read",
            TENANT_ROLE_USER, ACTION_READ),
    TENANT_ROLE_USER_MANAGEMENT_UPDATE("Tenant Role User Management - Update",
            TENANT_ROLE_USER, ACTION_UPDATE),
    TENANT_ROLE_USER_MANAGEMENT_DELETE("Tenant Role User Management - Delete",
            TENANT_ROLE_USER, ACTION_DELETE),
    TENANT_ROLE_USER_MANAGEMENT_ALL("Tenant Role User Management - All",
            TENANT_ROLE_USER, ACTION_ALL),

    THIRD_PARTY_PASSWORD_MANAGEMENT_CREATE("Third Party Password Management - Create",
            THIRD_PARTY_PASSWORD, ACTION_CREATE),
    THIRD_PARTY_PASSWORD_MANAGEMENT_READ("Third Party Password Management - Read",
            THIRD_PARTY_PASSWORD, ACTION_READ),
    THIRD_PARTY_PASSWORD_MANAGEMENT_UPDATE("Third Party Password Management - Update",
            THIRD_PARTY_PASSWORD, ACTION_UPDATE),
    THIRD_PARTY_PASSWORD_MANAGEMENT_DELETE("Third Party Password Management - Delete",
            THIRD_PARTY_PASSWORD, ACTION_DELETE),
    THIRD_PARTY_PASSWORD_MANAGEMENT_ALL("Third Party Password Management - All",
            THIRD_PARTY_PASSWORD, ACTION_ALL),

    THIRD_PARTY_EMAIL_MANAGEMENT_CREATE("Third Party Email Management - Create",
                                        THIRD_PARTY_EMAIL, ACTION_CREATE),
    THIRD_PARTY_EMAIL_MANAGEMENT_READ("Third Party Password Management - Read",
                                         THIRD_PARTY_EMAIL, ACTION_READ),
    THIRD_PARTY_EMAIL_MANAGEMENT_UPDATE("Third Party Password Management - Update",
                                           THIRD_PARTY_EMAIL, ACTION_UPDATE),
    THIRD_PARTY_EMAIL_MANAGEMENT_DELETE("Third Party Password Management - Delete",
                                           THIRD_PARTY_EMAIL, ACTION_DELETE),
    THIRD_PARTY_EMAIL_MANAGEMENT_ALL("Third Party Password Management - All",
                                        THIRD_PARTY_EMAIL, ACTION_ALL);

    private final String permissionName;
    private final SystemActionsEnum action;
    private final SystemResourcesEnum resource;

    /**
     * System Permission Enumeration constructor
     * @param permissionName Permission (name) Identifier
     * @param resource Resource Identifier
     * @param action Action Identifier
     */
    SystemPermissionsEnum(String permissionName, SystemResourcesEnum resource, SystemActionsEnum action) {
        this.permissionName = permissionName;
        this.resource = resource;
        this.action = action;
    }

    /**
     * Get Permission specific name
     * @return permission name as string value
     */
    public String getPermissionName() {
        return permissionName;
    }

    /**
     * Get Resource identifier
     * @return resource enum instance
     */
    public SystemResourcesEnum getResource() { return resource; }

    /**
     * Get Action specific identifier
     * @return action enum instance
     */
    public SystemActionsEnum getAction() { return action; }
}
