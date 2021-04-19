/*
 * Copyright (c) 2016-present openappframe.org & its legal owners. All rights reserved.
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
package io.radien.ms.openid.client.exception;

import io.radien.exception.TokenExpiredException;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

@Provider
public class ExceptionMapper implements
        ResponseExceptionMapper<Exception> {

    @Override
    public Exception toThrowable(Response response) {
        Response.Status status = Response.Status.fromStatusCode(response.getStatus());
        switch (status) {
            case UNAUTHORIZED:
                return new TokenExpiredException(response.readEntity(String.class));
            case NOT_FOUND:
                return new NotFoundException(response.readEntity(String.class));
        }
        return null;
    }

    @Override
    public boolean handles(int status, MultivaluedMap<String, Object> headers) {
        return status == UNAUTHORIZED.getStatusCode()  ||
               status == NOT_FOUND.getStatusCode(); // Not Found
    }
}
