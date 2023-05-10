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

import io.radien.exception.generic.AbstractRestException;
import javax.ws.rs.core.Response;

/**
 * Exception to be throw when no record has been found
 *
 * @author Bruno Gama
 */
public class NotFoundException extends AbstractRestException {

    /**
     * Not Found exception empty constructor
     */
    public NotFoundException() {
        super(Response.Status.NOT_FOUND);
    }

    /**
     * Not found exception constructor by a given message
     * @param message to create the not found exception with
     */
    public NotFoundException(String message) {
        super(message, Response.Status.NOT_FOUND);
    }
}
