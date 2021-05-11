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
package io.radien.ms.authz.client.exception;

import io.radien.exception.TokenExpiredException;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

/**
 * Exception Mapper for the authorizations
 *
 * @author Newton Carvalho
 */
@Provider
public class ExceptionMapper implements ResponseExceptionMapper<Exception> {

    private static final Logger log = LoggerFactory.getLogger(ExceptionMapper.class);

    /**
     * By a given response will check the error status code and convert it to the
     * correct exception
     * @param response to be validated
     * @return exception
     */
    @Override
    public Exception toThrowable(Response response) {
        Response.Status status = Response.Status.fromStatusCode(response.getStatus());
        switch (status) {
            case UNAUTHORIZED:
                log.info("Received status code 401 aligned with Unauthorized Exception");
                return new TokenExpiredException(response.readEntity(String.class));
            case NOT_FOUND:
                log.info("Received status code 404 aligned with Not Found Exception");
                return new NotFoundException(response.readEntity(String.class));
        }
        return null;
    }

    /**
     * Handles by a received status code the correct Error Message can be process or not
     * @param status code to be found
     * @param headers
     * @return true in case a received status code can be processed or not
     */
    @Override
    public boolean handles(int status, MultivaluedMap<String, Object> headers) {
        log.info("Received status code: {}", status);
        return status == UNAUTHORIZED.getStatusCode()  ||
               status == NOT_FOUND.getStatusCode(); // Not Found
    }
}
