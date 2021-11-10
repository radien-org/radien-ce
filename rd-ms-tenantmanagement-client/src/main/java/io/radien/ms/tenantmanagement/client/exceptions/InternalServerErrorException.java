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
package io.radien.ms.tenantmanagement.client.exceptions;

/**
 * Tenant and contracts specific Internal server error exception
 * this exception is to be thrown when the user requests something and the server was not able to
 * process it
 *
 * @author Bruno Gama
 */
public class InternalServerErrorException extends RuntimeException {

    private static final long serialVersionUID = 8676945014432052159L;

    /**
     * Internal server error exception empty constructor
     */
    public InternalServerErrorException() {
        super();
    }

    /**
     * Internal server error exception message constructor
     * @param message to be added into the Internal server error exception
     */
    public InternalServerErrorException(String message) {
        super(message);
    }
}
