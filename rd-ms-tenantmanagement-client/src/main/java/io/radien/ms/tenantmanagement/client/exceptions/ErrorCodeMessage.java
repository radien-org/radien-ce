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
package io.radien.ms.tenantmanagement.client.exceptions;

public enum ErrorCodeMessage {

    /**
     * Business Error Code Messages
     */
    RESOURCE_NOT_FOUND(100, "error.resource.not.found","Resource not found."),
    DUPLICATED_FIELD(101, "error.duplicated.field", "There is more than one resource with the same value for the field: %s"),

    TENANT_FIELD_NOT_INFORMED(102, "error.tenant.field.not.informed", "Tenant %s was not informed."),

    TENANT_PARENT_NOT_INFORMED(103, "error.tenant.parent.not.informed", "Parent information not informed."),
    TENANT_CLIENT_NOT_INFORMED(104, "error.tenant.client.not.informed", "Client information not informed."),

    TENANT_PARENT_NOT_FOUND(105, "error.tenant.parent.not.found", "Parent registry not found."),
    TENANT_CLIENT_NOT_FOUND(106, "error.tenant.client.not.found", "Client registry not found."),

    TENANT_PARENT_TYPE_IS_INVALID(107, "error.tenant.parent.type.invalid", "Parent type is invalid."),

    TENANT_END_DATE_IS_IS_INVALID(108, "error.tenant.end.date.invalid", "End date is invalid."),

    TENANT_ROOT_ALREADY_INSERTED(109, "error.tenant.root.already.inserted", "There must be only one Root Tenant."),
    TENANT_ROOT_WITH_PARENT(110, "error.tenant.root.with.parent", "Tenant root cannot have parent associated."),
    TENANT_ROOT_WITH_CLIENT(111, "error.tenant.root.with.client", "Tenant root cannot have client associated."),

    /**
     * System Error Code Messages
     */
    GENERIC_ERROR(500, "error.generic.error", "Generic Error.");

    private final int code;
    private final String key;
    private final String fallBackMessage;

    ErrorCodeMessage(int code, String key, String fallBackMessage) {
        this.code = code;
        this.key=key;
        this.fallBackMessage = fallBackMessage;
    }

    @Override
    public String toString() {
        return "{" +
                "\"code\":" + code +
                ", \"key\":\"" + key + "\"" +
                ", \"message\":\"" + fallBackMessage + "\"" +
                "}";
    }

    public String toString(String... args) {
        String message = String.format(fallBackMessage, args);
        return "{" +
                "\"code\":" + code +
                ", \"key\":\"" + key + "\"" +
                ", \"message\":\"" + message + "\"" +
                "}";
    }
}
