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
 * Exception to be throw when there is any issue while storing some new information
 *
 * @author Bruno Gama
 */
public class ProcessingException extends Exception {

    /**
     * Processing exception empty constructor
     */
    public ProcessingException() {
        super();
    }

    /**
     * Processing exception constructor by a given message
     * @param message to create the processing exception with
     */
    public ProcessingException(String message) {
        super(message);
    }
}
