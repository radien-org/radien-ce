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
    SAVE_ERROR_MESSAGE("rd_save_error"),
    EDIT_SUCCESS("rd_edit_success"),
    EDIT_ERROR_MESSAGE("rd_edit_error"),
    SAVE_SUCCESS_MESSAGE("rd_save_success"),
    EDIT_SUCCESS_MESSAGE("rd_edit_success"),
    RETRIEVE_ERROR_MESSAGE("rd_retrieve_error"),
    ROW_SELECTED("rowSelected"),
    PRETTY_PROFILE("pretty:profile"),
    PRETTY_INDEX("pretty:index"),
    UPDATE_SUCCESS_MESSAGE("rd_update_success"),
    UPDATE_ERROR_MESSAGE("rd_update_error"),
    SELECT_RECORD_FIRST("rd_select_record_first"),
    DELETE_SUCCESS("rd_delete_success"),
    ERROR_SELECT_RECORD_TO_DELETE("rd_delete_select_record_first"),
    DELETE_ERROR("rd_delete_error"),
    ACTIVE_TENANT_HAS_BEEN_REMOVED("rd_removed_active_tenant"),
    ERROR_REDIRECTING("rd_error_in_redirecting"),
    TENANT_USER_ASSOCIATION("pretty:userTenantAssociation"),

    /**
     * Active Management
     */
    NO_ACTIVE_TENANT_MESSAGE("rd_no_active_tenant"),
    CHOOSE_ACTIVE_TENANT_MESSAGE("choose_active_tenant"),
    ACTIVE_TENANT_CHANGED_TO_NULL_VALUE("rd_active_tenant_changed_value_to_null"),
    ACTIVE_TENANT_CHANGED_VALUE("rd_active_tenant_changed_value"),

    /**
     * Action Management
     */
    ACTION_MESSAGE("rd_action"),
    ACTIONS_MESSAGE("rd_actions"),
    ACTION_MAIN_PAGE("actions"),
    ACTION_CREATION_PAGE("action"),
    ACTION_DETAIL_PAGE("actionDetails"),
    ACTION_NAME_IS_MANDATORY("rd_action_name_is_mandatory"),
    ACTION_SELECTED("rd_action_selected"),
    ACTION_NOT_FOUND_MESSAGE("rd_action_not_found"),

    /**
     * Permission Management
     */
    PERMISSION_MAIN_PAGE("permissions"),
    PERMISSION_DETAIL_PAGE("permission"),
    PERMISSION_MESSAGE("rd_permission"),
    PERMISSIONS_MESSAGE("rd_permissions"),
    PERMISSION_NOT_FOUND_MESSAGE("rd_permission_not_found"),
    PERMISSION_NOT_FOUND_FOR_ACTION_RESOURCE_MESSAGE("rd_permission_not_found_for_action_resource"),

    /**
     * Role Management
     */
    ROLE_MESSAGE("rd_role"),
    ROLES_MESSAGE("rd_roles"),
    ROLE_MAIN_PAGE("roles"),
    ROLE_CREATION_PAGE("role"),
    ROLE_DETAIL_PAGE("roleDetails"),
    ROLE_NOT_FOUND_MESSAGE("rd_role_not_found"),
    ON_ROW_ROLE_EXPAND_ERROR("rd_on_row_role_expand_error"),
    ROLE_NAME_MANDATORY("rd_role_name_is_mandatory"),
    ROLE_SELECTED("rd_role_selected"),


    /**
     * User Management
     */
    USER_MESSAGE("rd_user"),
    USERS_MESSAGE("rd_users"),
    USERS_SELECTED_MESSAGE("rd_user_selected"),
    USER_NOT_FOUND_MESSAGE("rd_user_not_found"),
    USER_SEND_UPDATE_PASSWORD_EMAIL_SUCCESS("rd_send_update_password_email_success"),
    USER_SEND_UPDATE_PASSWORD_EMAIL_ERROR("rd_send_update_password_email_error"),
    USER_RESET_PASSWORD_CONFIRM_MESSAGE("userResetPasswordConfirmation"),
    USER_PATH("pretty:user"),
    PRETTY_USER("pretty:user"),
    USERS_PATH("pretty:users"),
    USERS_DISPATCH_PATH("/module/users"),
    USERS_ROLES_PATH("pretty:userRoles"),
    USER_DELETE_PATH("pretty:userDelete"),
    USER_EMAIL_UPDATE("pretty:userEmailUpdate"),
    USER_UN_ASSIGN_PATH("pretty:userUnAssign"),
    USER_ASSIGNING_TENANT_ASSOCIATION_PATH("pretty:userTenantAssociation"),
    USER_ASSIGNING_TENANT_SUCCESS("rd_tenant_association_creation_success"),
    USER_ASSIGNING_TENANT_ERROR("rd_tenant_association_creation_error"),
    USERS_PROFILE_PATH("pretty:userProfile"),
    SENT_UPDATE_PASSWORD_EMAIL_SUCCESS("rd_send_update_password_email_success"),
    SENT_UPDATE_PASSWORD_EMAIL_ERROR("rd_send_update_password_email_error"),
    SENT_UPDATED_EMAIL_VERIFY_SUCCESS("rd_send_updated_email_verify_success"),
    SENT_UPDATED_EMAIL_VERIFY_ERROR("rd_send_updated_email_verify_error"),

    /**
     * Tenant Role
     */
    TRP_ASSOCIATION_NO_PERMISSION_SELECT_MESSAGE("rd_tenant_role_permission_association_no_permission_select"),
    TRP_ASSOCIATION_SUCCESS_MESSAGE("rd_tenant_role_permission_association_success"),
    TRP_ASSOCIATION_ERROR_MESSAGE("rd_tenant_role_permission_association_error"),
    TRP_DISSOCIATION_NO_PERMISSION_SELECT_MESSAGE("rd_tenant_role_permission_dissociation_no_permission_select"),
    TRP_NO_ACTIVE_TENANT("rd_tenant_role_permission_no_active_tenant_selected"),

    TRP_DISSOCIATION_SUCCESS_MESSAGE("rd_tenant_role_permission_dissociation_success"),
    TRP_DISSOCIATION_ERROR_MESSAGE("rd_tenant_role_permission_dissociation_error"),

    TRU_ASSOCIATION_NO_USER_SELECT_MESSAGE("rd_tenant_role_user_association_no_user_select"),

    TRU_ASSOCIATION_SUCCESS_MESSAGE("rd_tenant_role_user_association_success"),
    TRU_ASSOCIATION_ERROR_MESSAGE( "rd_tenant_role_user_association_error"),

    TRU_DISSOCIATION_NO_USER_SELECT_MESSAGE("rd_tenant_role_user_dissociation_no_user_select"),
    TRU_DISSOCIATION_SUCCESS_MESSAGE("rd_tenant_role_user_dissociation_success"),
    TRU_DISSOCIATION_ERROR_MESSAGE( "rd_tenant_role_user_dissociation_error"),

    TR_TENANTS_FROM_USER("rd_tenants_from_user"),
    TR_ASSOCIATION("tenant_role_association"),
    TR_ASSOCIATIONS("tenant_role_associations"),
    TR_ASSOCIATION_ID("tenant_role_association_id"),
    TR_PATH("tenantrole"),
    TR_ERROR_RETRIEVING_USER("rd_tenant_association_error_retrieve_user"),

    /*
     * Tenant Management
     */
    TENANT_MAIN_PAGE("tenants"),
    TENANT_CREATION_PAGE("tenant"),
    TENANT_DETAIL_PAGE("tenantDetails"),
    TENANT_RD_TENANT("rd_tenant"),
    TENANT_RD_TENANTS("rd_tenants"),
    TENANT_NOT_FOUND_MESSAGE("rd_tenant_not_found"),
    ROOT_TENANT("rd_root_tenant_create_edit"),

    /**
     * Tenant Management
     */
    TENANT_SELECTED_TENANT("rd_tenantSelected"),

    /**
     * Resource Management
     */
    RESOURCE_RD_TENANT("rd_resource"),
    RESOURCE_DATA_TABLE_PAGE("resources"),
    RESOURCE_CREATION_PAGE("resource"),
    RESOURCE_DETAIL_PAGE("resourceDetails"),
    RESOURCE_NAME_IS_MANDATORY("rd_resource_name_is_mandatory"),
    RESOURCE_SELECTED("rd_resource_selection"),
    RESOURCE_NOT_FOUND_MESSAGE("rd_resource_not_found"),

    /**
     * Tenant Management
     */
    TENANT_CLIENT_ADDRESS_IS_MANDATORY("rd_tenant_client_address_is_mandatory"),
    TENANT_CLIENT_ZIP_CODE_IS_MANDATORY("rd_tenant_client_zip_code_is_mandatory"),
    TENANT_CLIENT_CITY_IS_MANDATORY("rd_tenant_client_city_is_mandatory"),
    TENANT_CLIENT_COUNTRY_IS_MANDATORY("rd_tenant_client_country_is_mandatory"),
    TENANT_CLIENT_PHONE_IS_MANDATORY("rd_tenant_client_phone_is_mandatory"),
    TENANT_CLIENT_EMAIL_IS_MANDATORY("rd_tenant_client_email_is_mandatory"),
    TENANT_NOT_FOUND("rd_tenant_tenant_not_found"),
    TENANT_USER_SESSION("rd_tenant_user_session"),

    /**
     * User Active Tenant Role Permission
     */
    USER_ACTIVE_TENANT_ROLE_PERMISSION_ASSIGNED_SUCCESS("rd_user_active_tenant_role_permission_assigned_success"),
    USER_ACTIVE_TENANT_ROLE_PERMISSION_ASSIGNED_ERROR("rd_user_active_tenant_role_permission_assigned_error"),
    USER_ACTIVE_TENANT_ROLE_PERMISSION_UNASSIGNED_SUCCESS("rd_user_active_tenant_role_permission_unassigned_success"),
    USER_ACTIVE_TENANT_ROLE_PERMISSION_UNASSIGNED_ERROR("rd_user_active_tenant_role_permission_unassigned_error"),

    /**
     * User Tenant Role Messages
     */

    USER_RD_TENANT_ROLE("rd_user_tenant_roles"),
    USER_RD_TENANT_ROLE_ASSIGNED_SUCCESS("rd_user_tenant_roles_association_assigned_success"),
    USER_RD_TENANT_ROLE_ASSIGNED_ERROR("rd_user_tenant_roles_association_assigned_error"),
    USER_RD_TENANT_ROLE_UNASSIGNED_SUCCESS("rd_user_tenant_roles_association_unassigned_success"),
    USER_RD_TENANT_ROLE_UNASSIGNED_ERROR("rd_user_tenant_roles_association_unassigned_error"),
    USER_RD_ROOT_TENANT_ROLE_ADMINISTRATOR_ACCESS_ERROR("rd_user_root_tenant_role_administrator_access_error");
  
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