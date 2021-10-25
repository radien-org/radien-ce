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
package io.radien.ms.usermanagement.client.exceptions;

/**
 * A runtime exception indicating an internal server error.
 * The server encountered an unexpected condition which prevented it from fulfilling the request.
 *
 * @author Bruno Gama
 */
public class InternalServerErrorException extends Exception{

    /**
     * Internal server error exception empty constructor
     */
    public InternalServerErrorException() {
        super();
    }

    /**
     * Internal server error exception message constructor
     * @param message specific message to be added into the exception when being throw
     */
    public InternalServerErrorException(String message) {
        super(message);
    }
}
