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

/**
 * Possible pre defined resources and information.
 * More resources can appear but this ones will be used for system purposes
 * @author Newton Carvalho
 */
public enum SystemResourcesEnum {

    USER("User"),
    ROLES("Roles"),
    PERMISSION("Permission"),
    RESOURCE("Resource"),
    ACTION("Action"),
    TENANT("Tenant"),
    TENANT_ROLE("Tenant Role"),
    TENANT_ROLE_PERMISSION("Tenant Role Permission"),
    TENANT_ROLE_USER("Tenant Role User"),
    THIRD_PARTY_PASSWORD("Third Party Password"),
    THIRD_PARTY_EMAIL("Third Party Email");

    private String resourceName;

    /**
     * System Resource Enumeration constructor
     * @param resourceName Role Identifier
     */
    SystemResourcesEnum(String resourceName) {
        this.resourceName = resourceName;
    }

    /**
     * Get Resource specific name
     * @return resource name as string value
     */
    public String getResourceName() {
        return resourceName;
    }
}
