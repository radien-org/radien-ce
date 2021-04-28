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

import io.radien.exception.LinkedAuthorizationNotFoundException;
import io.radien.exception.TokenExpiredException;
import junit.framework.TestCase;
import org.junit.Test;

import javax.ws.rs.core.Response;

/**
 * @author Bruno Gama
 */
public class LinkedAuthorizationResponseExceptionMapperTest extends TestCase {

    @Test
    public void testHandles() {
        LinkedAuthorizationResponseExceptionMapper linkedAuthorizationRexceptionMapper = new LinkedAuthorizationResponseExceptionMapper();
        boolean handlesException200 = linkedAuthorizationRexceptionMapper.handles(200, null);
        boolean handlesException400 = linkedAuthorizationRexceptionMapper.handles(400, null);
        boolean handlesException401 = linkedAuthorizationRexceptionMapper.handles(401, null);
        boolean handlesException404 = linkedAuthorizationRexceptionMapper.handles(404, null);
        boolean handlesException500 = linkedAuthorizationRexceptionMapper.handles(500, null);

        assertFalse(handlesException200);
        assertTrue(handlesException400);
        assertTrue(handlesException401);
        assertTrue(handlesException404);
        assertTrue(handlesException500);

    }

    @Test
    public void testThrowable() {
        String msg = "messageException";
        LinkedAuthorizationResponseExceptionMapper target = new LinkedAuthorizationResponseExceptionMapper();

        Response responseOk = Response.status(Response.Status.OK).entity(msg).build();
        Exception exceptionOk = target.toThrowable(responseOk);

        Response responseBadRequest = Response.status(Response.Status.BAD_REQUEST).entity(msg).build();
        Exception exceptionBadRequest = target.toThrowable(responseBadRequest);

        Response responseUnauthorized = Response.status(Response.Status.UNAUTHORIZED).entity(msg).build();
        Exception exceptionUnauthorized = target.toThrowable(responseUnauthorized);

        Response responseNotFound = Response.status(Response.Status.NOT_FOUND).entity(msg).build();
        Exception exceptionNotFound = target.toThrowable(responseNotFound);

        Response responseInternalServerError = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(msg).build();
        Exception exceptionInternalServerError = target.toThrowable(responseInternalServerError);

        assertTrue(exceptionBadRequest instanceof BadRequestException);
        assertEquals(msg,exceptionBadRequest.getMessage());

        assertTrue(exceptionUnauthorized instanceof TokenExpiredException);
        assertEquals(msg,exceptionUnauthorized.getMessage());

        assertTrue(exceptionNotFound instanceof LinkedAuthorizationNotFoundException);
        assertEquals(msg,exceptionNotFound.getMessage());

        assertNull(exceptionOk);

        assertTrue(exceptionInternalServerError instanceof InternalServerErrorException);
        assertEquals(msg,exceptionInternalServerError.getMessage());
    }
}