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
package io.radien.ms.usermanagement.client;

import io.radien.exception.NotFoundException;
import io.radien.exception.TokenExpiredException;
import io.radien.ms.usermanagement.client.exceptions.BadRequestException;
import io.radien.ms.usermanagement.client.exceptions.InternalServerErrorException;
import io.radien.ms.usermanagement.client.exceptions.ForbiddenException;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/**
 * User mapper for the exceptions
 * @author Bruno Gama
 */
@Provider
public class UserResponseExceptionMapper implements
        ResponseExceptionMapper<Exception> {

    /**
     * Validates if by a given status code the error message can be handle by the following mapper
     * @param statusCode to be validated
     * @param headers
     * @return true in case handler can handle exception
     */
    @Override
    public boolean handles(int statusCode, MultivaluedMap<String, Object> headers) {
        return statusCode == 400        // Bad Request
                || statusCode == 401
                || statusCode == 403
                || statusCode == 404    // Not Found
                || statusCode == 500    // Internal Server Error
                ;
    }

    /**
     * Throws the correct permission exception by the given response
     * @param response message to be validated
     * @return a exception
     */
    @Override
    public Exception toThrowable(Response response) {
        switch(response.getStatus()) {
            case 400: return new BadRequestException(response.readEntity(String.class));
            case 401: return new TokenExpiredException(response.readEntity(String.class));
            case 403: return new ForbiddenException(response.readEntity(String.class));
            case 404: return new NotFoundException(response.readEntity(String.class));
            case 500: return new InternalServerErrorException(response.readEntity(String.class));
        }
        return null;
    }

}