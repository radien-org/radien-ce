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
package io.radien.ms.authz.client.exception;

/**
 * Specific Authorization Not Found Exception to be throw when no Authorization can be found
 *
 * @author Newton Carvalho
 */
public class NotFoundException extends RuntimeException {

    /**
     * Authorization Not Found Exception message constructor
     * @param message to be added into the authorization not found exception
     */
    public NotFoundException(String message) {
        super(message);
    }

    /**
     * Authorization Not Found Exception message and cause constructor
     * @param message to be added into the authorization not found exception
     * @param cause to be added into the authorization not found exception
     */
    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
