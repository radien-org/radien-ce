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

package io.radien.webapp;

/**
 * Data model enumeration messages and pages
 *
 * @author Bruno Gama
 **/
public enum DataModelEnum {

    /**
     * Generic and Common messages and redirects
     */
    GENERIC_ERROR_MESSAGE("rd_generic_error_message"),
    PUBLIC_INDEX_PATH("/public/index"),

    /**
     * Active Tenant Messages and redirects
     */
    NO_ACTIVE_TENANT_MESSAGE("rd_no_active_tenant"),
    ACTIVE_TENANT_CHANGED_TO_NULL_VALUE("rd_active_tenant_changed_value_to_null"),
    ACTIVE_TENANT_CHANGED_VALUE("rd_active_tenant_changed_value"),

    /**
     * User Active Tenant Role Permission Messages and redirects
     */
    USER_ACTIVE_TENANT_ROLE_PERMISSION_ASSIGNED_SUCCESS("rd_user_active_tenant_role_permission_assigned_success"),
    USER_ACTIVE_TENANT_ROLE_PERMISSION_ASSIGNED_ERROR("rd_user_active_tenant_role_permission_assigned_error"),
    USER_ACTIVE_TENANT_ROLE_PERMISSION_UNASSIGNED_SUCCESS("rd_user_active_tenant_role_permission_unassigned_success"),
    USER_ACTIVE_TENANT_ROLE_PERMISSION_UNASSIGNED_ERROR("rd_user_active_tenant_role_permission_unassigned_error"),

    /**
     * Role Messages and redirects
     */
    ROLE_MAIN_PAGE("roles"),
    ON_ROW_ROLE_EXPAND_ERROR("rd_on_row_role_expand_error");

    private String value;

    /**
     * Data Models enum
     * @param value message identifier
     */
    DataModelEnum(String value) {
        this.value = value;
    }

    /**
     * Get data model specific model description message
     * @return model description message as string value
     */
    public String getValue() {
        return value;
    }

    /**
     * Set new model description message to the existent one
     * @param value to be updated
     */
    public void setValue(String value) {
        this.value = value;
    }
}
