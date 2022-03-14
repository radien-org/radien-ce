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

package io.radien.api.service.ecm.exception;

import io.radien.exception.SystemException;

public class InvalidClientException extends SystemException {
    private static final long serialVersionUID = -1568199281340528650L;

    /**
     * Content Not Available exception empty constructor
     */
    public InvalidClientException() {
        super();
    }

    /**
     * Content Not Available exception message constructor
     * @param message to be added
     */
    public InvalidClientException(String message) {
        super(message);
    }

    public InvalidClientException(String message, Exception e) {
        super(message, e);
    }
}
