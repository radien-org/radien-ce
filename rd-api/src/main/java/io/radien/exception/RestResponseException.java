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

package io.radien.exception;

import java.text.MessageFormat;

public class RestResponseException extends SystemException {
    private final int statusCode;

    public RestResponseException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String getMessage() {
        StringBuilder messageString = new StringBuilder(MessageFormat.format("[Status - {0}] Message(s): ", statusCode));

        if (super.getMessages().size() == 1) {
            return messageString + super.getMessages().iterator().next();
        }
        for (String message : super.getMessages()) {
            messageString.append("[").append(message).append("]");
        }

        return messageString.toString();
    }
}
