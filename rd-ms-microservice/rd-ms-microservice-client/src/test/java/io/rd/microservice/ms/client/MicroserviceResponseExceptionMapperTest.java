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
package io.rd.microservice.ms.client;

import io.rd.microservice.exception.NotFoundException;
import io.rd.microservice.ms.client.exceptions.BadRequestException;
import io.rd.microservice.ms.client.exceptions.InternalServerErrorException;

import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.junit.Assert.*;

public class MicroserviceResponseExceptionMapperTest {
    MicroserviceResponseExceptionMapper microserviceResponseExceptionMapper;

    @Before
    public void before() {
        microserviceResponseExceptionMapper = new MicroserviceResponseExceptionMapper();
    }

    @Test
    public void handles() {
        boolean handlesException200 = microserviceResponseExceptionMapper.handles(200, null);
        boolean handlesException400 = microserviceResponseExceptionMapper.handles(400, null);
        boolean handlesException401 = microserviceResponseExceptionMapper.handles(401, null);
        boolean handlesException404 = microserviceResponseExceptionMapper.handles(404, null);
        boolean handlesException500 = microserviceResponseExceptionMapper.handles(500, null);

        assertFalse(handlesException200);
        assertTrue(handlesException400);
        assertTrue(handlesException401);
        assertTrue(handlesException404);
        assertTrue(handlesException500);
    }

    @Test
    public void toThrowable() {
        String msg = "messageException";

        Response responseOk = Response.status(Response.Status.OK).entity(msg).build();
        Exception exceptionOk = microserviceResponseExceptionMapper.toThrowable(responseOk);

        Response responseBadRequest = Response.status(Response.Status.BAD_REQUEST).entity(msg).build();
        Exception exceptionBadRequest = microserviceResponseExceptionMapper.toThrowable(responseBadRequest);

        Response responseNotFound = Response.status(Response.Status.NOT_FOUND).entity(msg).build();
        Exception exceptionNotFound = microserviceResponseExceptionMapper.toThrowable(responseNotFound);

        Response responseInternalServerError = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(msg).build();
        Exception exceptionInternalServerError = microserviceResponseExceptionMapper.toThrowable(responseInternalServerError);

        assertTrue(exceptionBadRequest instanceof BadRequestException);
        assertEquals(msg,exceptionBadRequest.getMessage());


        assertTrue(exceptionNotFound instanceof NotFoundException);
        assertEquals(msg,exceptionNotFound.getMessage());

        assertNull(exceptionOk);

        assertTrue(exceptionInternalServerError instanceof InternalServerErrorException);
        assertEquals(msg,exceptionInternalServerError.getMessage());
    }
}
