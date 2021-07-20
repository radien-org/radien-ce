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

package io.radien.exception;

/**
 * Enumerate for the error code messages
 *
 * @author Bruno Gama
 **/
public enum GenericErrorCodeMessage {

    /**
     * Generic Business Error Code Messages
     */
    RESOURCE_NOT_FOUND("G1", "error.resource.not.found","Resource not found."),

    DUPLICATED_FIELD("G2", "error.duplicated.field", "There is more than one resource with the same value for the field: %s"),

    ERROR_SAVING_ENTITY("G3", "error.saving.entity", "Error saving entity: %s"),

    ERROR_DELETING_ENTITY("G4", "error.deleting.entity", "Error deleting entity: %s"),

    ERROR_IN_CONFIGURE("G5", "error.in.configure", "Error in configure: %s"),

    RESOURCE_URL_PATH("G6", "resource.url.path", "Resource URL path: %s"),

    HTTP_SESSION_INITIATED("G7", "http.session.initiated", "HTTP session initiated: %s"),

    INFO_CORS_FILTER("G8", "cors.filter.method", "Cors filter method: %s"),

    INFO_ENTITY_SAVED("G9", "info.entity.saved", "An entity of class {0} was persisted in the DB"),

    LIST_ENTITY_SAVED("G10", "list.entity.saved", "A List of entities of class {0} was persisted in the DB"),

    INFO_ENTITY_DELETED("G11", "info.entity.deleted", "An entity of class {0} was deleted in the DB"),

    INFO_SYSTEM_START("G12", "info.system.start", "START ID: {0} |Class: | {1} | Method: | {2}"),
    INFO_SYSTEM_END("G13", "info.system.end", "END ID: {0} |Class: | {1} | Method: | {2} | Milliseconds: | {3} | Result: | {4}"),
    ERROR_METHOD_INTERCEPTION("G14", "error.method.interception", "Exception during method interception: %s"),
    INIT_CONNECTION_TIME_REBOOT("G15", "init.connection.time.reboot", "Connection was opened {0} since last reboot {1}"),
    ENTITY_ID_NULL("G16", "entity.id.null", "{0} id was null. Skipped"),

    /**
     * Tenant Business Error Code Messages
     */
    TENANT_FIELD_NOT_INFORMED("T1", "error.tenant.field.not.informed", "Tenant %s was not informed."),
    TENANT_PARENT_NOT_INFORMED("T2", "error.tenant.parent.not.informed", "Parent information not informed."),
    TENANT_CLIENT_NOT_INFORMED("T3", "error.tenant.client.not.informed", "Client information not informed."),
    TENANT_PARENT_NOT_FOUND("T4", "error.tenant.parent.not.found", "Parent registry not found."),
    TENANT_CLIENT_NOT_FOUND("T5", "error.tenant.client.not.found", "Client registry not found."),
    TENANT_PARENT_TYPE_IS_INVALID("T6", "error.tenant.parent.type.invalid", "Parent type is invalid."),
    TENANT_END_DATE_IS_IS_INVALID("T7", "error.tenant.end.date.invalid", "End date is invalid."),
    TENANT_ROOT_ALREADY_INSERTED("T8", "error.tenant.root.already.inserted", "There must be only one Root Tenant."),
    TENANT_ROOT_WITH_PARENT("T9", "error.tenant.root.with.parent", "Tenant root cannot have parent associated."),
    TENANT_ROOT_WITH_CLIENT("T10", "error.tenant.root.with.client", "Tenant root cannot have client associated."),
    TENANT_TYPE_NOT_FOUND("T11", "tenant.type.not.found", "No tenant type found: %s"),

    /**
     * Tenant Role Error Code Messages
     */

    TENANT_ROLE_NO_TENANT_ROLE_FOUND("TR1", "error.no.tenant.role.found.id", "No Tenant Role found for id %s."),
    TENANT_ROLE_FIELD_MANDATORY("TR2", "error.tenant.role.field.mandatory", "Tenant Role %s is mandatory."),
    TENANT_ROLE_ASSOCIATION_TENANT_ROLE("TR3", "error.no.association.tenant.role", "There is no association between tenant %s and role %s."),
    TENANT_ROLE_USER_IS_ALREADY_ASSOCIATED("TR4", "error.user.already.associated", "User is already associated with tenant %s and role %s."),
    TENANT_ROLE_NO_ASSOCIATION_FOUND_FOR_USER("TR5", "error.no.association.found.for.user", "No association found for user %s."),
    TENANT_ROLE_PERMISSION_EXISTENT_FOR_TENANT_ROLE("TR6", "error.permission.already.associated", "Permission is already associated with tenant %s and role %s."),
    TENANT_ROLE_NO_ASSOCIATION_FOR_PERMISSION("TR7", "error.no.permission.found", "No association found for permission %s."),
    TENANT_ROLE_PERMISSIONS_ASSOCIATED_WITH_TENANT_ROLE("TR8", "error.permission.associated.with.tenant.role", "There are permissions associated with tenant role "),
    TENANT_ROLE_USERS_ASSOCIATED_WITH_TENANT_ROLE("TR9", "error.user.associated.with.tenant.role","There are users associated with tenant role "),

    /**
     * Active Tenant Error Code messages
     */
    ACTIVE_TENANT_DELETE_WITHOUT_TENANT_AND_USER("AC1", "error.delete.without.tenant.and.user",
            "Insufficient params to perform delete. Is necessary at least tenant or user id"),
    ACTIVE_TENANT_ERROR_VALIDATING("AC2", "error.validating.active.tenant",
            "Error checking active tenants"),

    /**
     * Permission Business Error Code Messages
     */

    PERMISSION_PARAMETERS_NOT_INFORMED("P1", "error.mandatory.parameters.not.informed", "Mandatory parameters not informed: %s."),

    /**
     * Linked Authorization Business Error Code Messages
     */

    NOT_INFORMED_PARAMETERS_FOR_DISSOCIATION("LA1", "error.dissociation.no.params", "Parameters for dissociation not informed."),
    INFO_CREATE_NEW_LINKED_AUTHORIZATION("LA2", "info.create.new.linked.authorization",
            "Create a new Linked Authorization with the values of Tenant Id: {0}, Permission Id: {1}, Role Id: {2}, User Id: {3}."),
    INFO_COVERT_JSON_OBJECT_TO_LINKED_AUTHORIZATION("LA3", "info.convert.json.object.to.linked.authorization",
            "Convert JSON object to Linked Authorization with the values of id: {0} Tenant Id: {1}, Permission Id: {2}, Role Id: {3}, User Id: {4}."),

    /**
     * User Business Error Code Messages
     */
    USER_FIELD_MANDATORY("U1", "error.user.field.mandatory", "User %s is mandatory."),

    /**
     * System Error Code Messages
     */
    EXPIRED_ACCESS_TOKEN("SYS1", "error.expired.token", "Unable to recover expiredToken."),
    GENERIC_ERROR("SYS2", "error.generic.error", "Generic Error."),
    AUTHORIZATION_ERROR("SYS3", "error.authorization.checker", "Error checking authorization"),
    NO_CURRENT_USER_AVAILABLE("SYS4", "error.no.current.user.available", "No current user available");

    private final String code;
    private final String key;
    private final String fallBackMessage;

    /**
     * Tenant error code messages constructor
     *
     * @param code of the error message
     * @param key of the error message
     * @param fallBackMessage of the error message
     */
    GenericErrorCodeMessage(String code, String key, String fallBackMessage) {
        this.code = code;
        this.key=key;
        this.fallBackMessage = fallBackMessage;
    }

    /**
     * Converts to string the requested message without parameters
     * @return a string error message
     */
    @Override
    public String toString() {
        return "{" +
                "\"code\":" + code +
                ", \"key\":\"" + key + "\"" +
                ", \"message\":\"" + fallBackMessage + "\"" +
                "}";
    }

    /**
     * Converts to string the requested message with parameters
     * @return a string error message with defined parameters
     */
    public String toString(String... args) {
        String message = String.format(fallBackMessage, args);
        return "{" +
                "\"code\":" + code +
                ", \"key\":\"" + key + "\"" +
                ", \"message\":\"" + message + "\"" +
                "}";
    }
}
