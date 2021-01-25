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
package io.radien.ms.rolemanagement.client.exception;

import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

/**
 * @author Bruno Gama
 */
public class RoleResponseExceptionMapper implements
        ResponseExceptionMapper<Exception> {

    // TODO: Bruno Gama - Make your own exceptions

    @Override
    public boolean handles(int statusCode, MultivaluedMap<String, Object> headers) {
        return statusCode == 400        // Bad Request
                || statusCode == 404    // Not Found
                || statusCode == 500;   // Internal Server Error
    }

    @Override
    public Exception toThrowable(Response response) {
        switch(response.getStatus()) {
            case 400: return new BadRequestException(response.readEntity(String.class));
            case 404: return new NotFoundException(response.readEntity(String.class));
            case 500: return new InternalServerErrorException(response.readEntity(String.class));
        }
        return null;
    }
}
