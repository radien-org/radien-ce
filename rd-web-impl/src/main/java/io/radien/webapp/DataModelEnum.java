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
    SAVE_ERROR_MESSAGE("rd_save_error"),
    EDIT_ERROR_MESSAGE("rd_edit_error"),
    SAVE_SUCCESS_MESSAGE("rd_save_success"),
    RETRIEVE_ERROR_MESSAGE("rd_retrieve_error"),
    ROW_SELECTED("rowSelected"),
    PRETTY_PROFILE("pretty:profile"),
    PRETTY_INDEX("pretty:index"),

    /**
     * Active Tenant Messages and redirects
     */
    NO_ACTIVE_TENANT_MESSAGE("rd_no_active_tenant"),
    ACTIVE_TENANT_CHANGED_TO_NULL_VALUE("rd_active_tenant_changed_value_to_null"),
    ACTIVE_TENANT_CHANGED_VALUE("rd_active_tenant_changed_value"),

    /**
     * Permission Messages and redirects
     */
    PERMISSION_MESSAGE("rd_permission"),
    PERMISSIONS_MESSAGE("rd_permissions"),
    PERMISSION_NOT_FOUND_MESSAGE("rd_permission_not_found"),

    /**
     * Role Messages and redirects
     */
    ROLE_MESSAGE("rd_role"),
    ROLES_MESSAGE("rd_roles"),
    ROLE_NOT_FOUND_MESSAGE("rd_role_not_found"),

    /**
     * User Messages and redirects
     */
    USER_MESSAGE("rd_user"),
    USER_NOT_FOUND_MESSAGE("rd_user_not_found"),

    /**
     * Linked Authorization Messages and redirects
     */
    LINKED_AUTHORIZATION_MESSAGE("rd_linkedauthorization"),
    LINKED_AUTHORIZATION_PATH("linkedauthorization"),

    /**
     * TenantRole associations domain Messages and redirects
     */
    TRP_ASSOCIATION_NO_PERMISSION_SELECT_MESSAGE("rd_tenant_role_permission_association_no_permission_select"),
    TRP_ASSOCIATION_SUCCESS_MESSAGE("rd_tenant_role_permission_association_success"),
    TRP_ASSOCIATION_ERROR_MESSAGE("rd_tenant_role_permission_association_error"),
    TRP_DISSOCIATION_NO_PERMISSION_SELECT_MESSAGE("rd_tenant_role_permission_dissociation_no_permission_select"),

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

    /**
     * User messages and redirects
     */
    USERS_PATH("pretty:users"),
    USER_ASSIGNING_TENANT_ASSOCIATION_PATH("pretty:userTenantAssociation"),
    USER_ASSIGNING_TENANT_SUCCESS("rd_tenant_association_creation_success"),
    USER_ASSIGNING_TENANT_ERROR("rd_tenant_association_creation_error"),

    /*
     * Tenant Management Pages
     */
    TENANT_MAIN_PAGE("tenants"),
    TENANT_CREATION_PAGE("tenant"),
    TENANT_DETAIL_PAGE("tenantDetails"),
    TENANT_RD_TENANT("rd_tenant"),
    TENANT_RD_TENANTS("rd_tenants"),
    TENANT_NOT_FOUND_MESSAGE("rd_tenant_not_found"),

    /**
     * Tenant Management Info Messages
     */
    TENANT_SELECTED_TENANT("rd_tenantSelected"),
    TENANT_DELETE_SUCCESS("rd_delete_success"),
    TENANT_SAVE_SUCCESS("rd_save_success"),

    /**
     * Tenant Management Error Messages
     */
    TENANT_CLIENT_ADDRESS_IS_MANDATORY("rd_tenant_client_address_is_mandatory"),
    TENANT_CLIENT_ZIP_CODE_IS_MANDATORY("rd_tenant_client_zip_code_is_mandatory"),
    TENANT_CLIENT_CITY_IS_MANDATORY("rd_tenant_client_city_is_mandatory"),
    TENANT_CLIENT_COUNTRY_IS_MANDATORY("rd_tenant_client_country_is_mandatory"),
    TENANT_CLIENT_PHONE_IS_MANDATORY("rd_tenant_client_phone_is_mandatory"),
    TENANT_CLIENT_EMAIL_IS_MANDATORY("rd_tenant_client_email_is_mandatory"),
    TENANT_EDIT_ERROR("rd_edit_error"),
    TENANT_DELETE_ERROR("rd_delete_error"),
    TENANT_SELECT_RECORD_FIRST("rd_select_record_first"),
    TENANT_NOT_FOUND("rd_tenant_tenant_not_found"),
    TENANT_USER_SESSION("rd_tenant_user_session"),
    TENANT_SAVE_ERROR_MESSAGE("rd_save_error"),
    TENANT_GENERIC_ERROR_MESSAGE("rd_generic_error_message"),

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