/*
 * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
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

    ID("id", "ID"),
    NAME("name", "Name"),
    ACTION_ID("actionId", "Action Id"),
    RESOURCE_ID("resourceId", "Resource Id"),
    ACTION_NAME("actionName", "Action Name"),
    RESOURCE_NAME("resourceName", "Resource Name"),
    FIRST_NAME("firstname", "Firstname"),
    LAST_NAME("lastname", "Lastname"),
    LOGON("logon", "Logon"),
    USER_EMAIL("userEmail", "User Email"),
    MOBILE_NUMBER("mobileNumber", "Mobile Number"),
    SUB("sub", "Subject"),
    PROCESSING_LOCKED("processingLocked", "Processing Locked"),
    USER_ENABLED("enabled", "Enabled"),
    USER_DELEGATION("delegatedCreation", "Delegated Creation"),
    CREATE_USER("createUser", "Create User"),
    LAST_UPDATE_USER("lastUpdateUser", "Last Update User"),
    PAGE_CURRENT("currentPage", "Current Page"),
    PAGE_TOTALS("totalPages", "Total Pages"),
    PAGE_TOTAL_RESULTS("totalResults", "Total Results"),
    PAGE_RESULTS("results", "Results"),
    TENANT_ID("tenantId", "Tenant ID"),
    TENANT_NAME("tenantName", "Tenant Name"),
    IS_TENANT_ACTIVE("isTenantActive", "Is Tenant Active"),
    ROLE_ID("roleId", "Role ID"),
    ROLE_NAME("roleName", "Role Name"),
    USER_ID("userId", "User ID"),
    TENANT_ROLE_ID("tenantRoleId", "Tenant Role ID"),
    PERMISSION_ID("permissionId", "Permission ID"),
    ROLE_TENANT_ID("roleTenantId", "Role Tenant ID"),
    ACCESS_TOKEN("accessToken", "Access Token"),
    REFRESH_TOKEN("refreshToken", "Refresh Token"),
    TICKET_TYPE("ticketType", "Ticket Type"),
    TOKEN("token", "Token"),
    EXPIRATION_DATE("expireDate", "Expiration Date"),
    DATA("data", "Data"),
    OLD_PASSWORD("oldPassword", "Old Password"),
    NEW_PASSWORD("newPassword", "New Password"),
    CONFIRM_NEW_PASSWORD("confirmNewPassword", "Confirm New Password"),
    KEY("key", "Key"),
    APPLICATION("application", "Application"),
    TRANSLATIONS("translations", "Translations"),
    LANGUAGE("language", "Language"),
    VALUE("value", "Value"),
    GENERIC_ERROR_MESSAGE_CODE("code", "code"),
    GENERIC_ERROR_MESSAGE_KEY("key", "key"),
    GENERIC_ERROR_MESSAGE_MESSAGE("message", "message");

    private final String fieldName;
    private final String label;

    /**
     * Tenant error code messages constructor
     *
     * @param fieldName of the variable
     */
    SystemVariables(String fieldName, String label) {
        this.fieldName = fieldName;
        this.label=label;
    }

    /**
     * Converts to string the requested value of the variable
     * @return a string value of the variable
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Converts to string the requested value of the variable
     * @return a string value of the variable lable
     */
    public String getLabel() {
        return label;
    }
}
