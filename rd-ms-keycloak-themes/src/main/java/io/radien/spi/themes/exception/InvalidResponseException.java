/*
 *
 *  * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
 *  * <p>
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  * <p>
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  * <p>
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package io.radien.spi.themes.exception;

import java.util.HashSet;
import java.util.Set;

public class InvalidResponseException extends Exception {
    private final Set<String> messages = new HashSet<>();

    /**
     * System Exception constructor by a given message
     * @param message to create the system exception with
     */
    public InvalidResponseException(String message) {
        super();
        messages.add(message);
    }
}
