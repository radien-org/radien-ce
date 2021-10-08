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
package io.rd.microservice.ms.client.exceptions;

public enum ErrorCodeMessage {

    /**
     * Business Error Code Messages
     */
    RESOURCE_NOT_FOUND(100, "error.resource.not.found","Resource not found."),
    DUPLICATED_FIELD(101, "error.duplicated.field", "There is more than one resource with the same value for the field: %s"),
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
