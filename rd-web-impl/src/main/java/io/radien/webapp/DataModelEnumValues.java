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
 * Data model enumerate class for prefixed error messages and pages to be show or user redirection
 *
 * @author Bruno Gama
 **/
public enum DataModelEnumValues {

    /**
     * Tenant Management Pages
     */
    TENANT_MAIN_PAGE("tenants"),
    TENANT_CREATION_PAGE("tenant"),
    TENANT_DETAIL_PAGE("tenantDetails"),
    TENANT_RD_TENANT("rd_tenant"),

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
    TENANT_GENERIC_ERROR_MESSAGE("rd_generic_error_message");

    private String request;

    /**
     * Data Model enum type constructor
     * @param request request
     */
    DataModelEnumValues(String request) {
        this.request = request;
    }

    /**
     * Data Model request getter
     * @return data model requested action
     */
    public String request() {
        return request;
    }

}
