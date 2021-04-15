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
import io.radien.ms.usermanagement.client.exceptions.BadRequestException;
import io.radien.ms.usermanagement.client.exceptions.ForbiddenException;
import io.radien.ms.usermanagement.client.exceptions.InternalServerErrorException;
import org.junit.Test;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import static org.junit.Assert.*;

public class UserResponseExceptionMapperTest {

    @Test
    public void handles() {
        UserResponseExceptionMapper uRexceptionMapper = new UserResponseExceptionMapper();
        boolean handlesException200 = uRexceptionMapper.handles(200, null);
        boolean handlesException400 = uRexceptionMapper.handles(400, null);
        boolean handlesException403 = uRexceptionMapper.handles(403, null);
        boolean handlesException404 = uRexceptionMapper.handles(404, null);
        boolean handlesException500 = uRexceptionMapper.handles(500, null);

        assertFalse(handlesException200);
        assertTrue(handlesException400);
        assertTrue(handlesException403);
        assertTrue(handlesException404);
        assertTrue(handlesException500);
    }

    // TODO: toThrowable Exception validation to be performed
    @Test
    public void toThrowable() {
        String msg = "messageException";
        UserResponseExceptionMapper target = new UserResponseExceptionMapper();

        Response responseOk = Response.status(Status.OK).entity(msg).build();
        Exception exceptionOk = target.toThrowable(responseOk);

        Response responseBadRequest = Response.status(Status.BAD_REQUEST).entity(msg).build();
        Exception exceptionBadRequest = target.toThrowable(responseBadRequest);

        Response responseForbidden = Response.status(Status.FORBIDDEN).entity(msg).build();
        Exception exceptionForbidden = target.toThrowable(responseForbidden);

        Response responseNotFound = Response.status(Status.NOT_FOUND).entity(msg).build();
        Exception exceptionNotFound = target.toThrowable(responseNotFound);

        Response responseInternalServerError = Response.status(Status.INTERNAL_SERVER_ERROR).entity(msg).build();
        Exception exceptionInternalServerError = target.toThrowable(responseInternalServerError);

        assertTrue(exceptionBadRequest instanceof BadRequestException);
        assertEquals(msg,exceptionBadRequest.getMessage());

        assertTrue(exceptionForbidden instanceof ForbiddenException);
        assertEquals(msg,exceptionForbidden.getMessage());

        assertTrue(exceptionNotFound instanceof NotFoundException);
        assertEquals(msg,exceptionNotFound.getMessage());

        assertNull(exceptionOk);

        assertTrue(exceptionInternalServerError instanceof InternalServerErrorException);
        assertEquals(msg,exceptionInternalServerError.getMessage());
    }
}
