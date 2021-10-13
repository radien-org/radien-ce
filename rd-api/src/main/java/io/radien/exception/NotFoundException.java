/*
 * Copyright (c) 2021-present radien GmbH & its legal owners. All rights reserved.
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
 * Exception to be throw when no record has been found
 *
 * @author Bruno Gama
 */
public class NotFoundException extends RuntimeException {

    /**
     * Not Found exception empty constructor
     */
    public NotFoundException() {
        super();
    }

    /**
     * Not found exception constructor by a given message
     * @param message to create the not found exception with
     */
    public NotFoundException(String message) {
        super(message);
    }
}
