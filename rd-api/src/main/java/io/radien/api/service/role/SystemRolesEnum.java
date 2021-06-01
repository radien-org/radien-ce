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
package io.radien.api.service.role;

/**
 * Possible pre defined roles and information. More roles can appear but this ones will be used for system purposes
 *
 * @author Bruno Gama
 **/
public enum SystemRolesEnum {

    SYSTEM_ADMINISTRATOR("System Administrator"),
    PERMISSION_ADMINISTRATOR("Permission Administrator"),
    ROLE_ADMINISTRATOR("Role Administrator"),
    USER_ADMINISTRATOR("User Administrator"),
    TENANT_ADMINISTRATOR("Tenant Administrator"),
    CLIENT_TENANT_ADMINISTRATOR("Client Tenant Administrator"),
    SUB_TENANT_ADMINISTRATOR("Sub Tenant Administrator"),
    GUEST("Guest"),
    APPROVER("Approver"),
    AUTHOR("Author"),
    PUBLISHER("Publisher");

    private String roleName;

    /**
     * System Role Enumeration constructor
     * @param roleName Role Identifier
     */
    SystemRolesEnum(String roleName) {
        this.roleName = roleName;
    }

    /**
     * Get Role specific name
     * @return role name as string value
     */
    public String getRoleName() {
        return roleName;
    }

    /**
     * Set new role name to the existent one
     * @param roleName to be updated
     */
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
