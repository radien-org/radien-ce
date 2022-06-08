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

package io.radien.exception.generic;

import javax.ws.rs.core.Response;

public abstract class AbstractRestException extends RuntimeException {
    private static final long serialVersionUID = -1568199281340528650L;
    private final Response.Status status;

    protected AbstractRestException() {
        super();
        status = Response.Status.INTERNAL_SERVER_ERROR;
    }

    protected AbstractRestException(Response.Status status) {
        super();
        this.status = status;
    }

    protected AbstractRestException(String message) {
        super(message);
        status = Response.Status.INTERNAL_SERVER_ERROR;
    }

    protected AbstractRestException(String message, Response.Status status) {
        super(message);
        this.status = status;
    }

    protected AbstractRestException(String message, Exception e) {
        super(message, e);
        status = Response.Status.INTERNAL_SERVER_ERROR;
    }

    protected AbstractRestException(Exception e, Response.Status status) {
        super(e);
        this.status = status;
    }

    protected AbstractRestException(String message, Exception e, Response.Status status) {
        super(message, e);
        this.status = status;
    }

    public Response.Status getStatus() {
        return status;
    }
}
