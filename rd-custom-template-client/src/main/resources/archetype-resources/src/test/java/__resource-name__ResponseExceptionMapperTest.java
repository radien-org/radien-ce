/*
 * Copyright (c) 2021-present radien GmbH & its legal owners. All rights reserved.
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
package ${package};

import ${package}.exceptions.BadRequestException;
import ${package}.exceptions.InternalServerErrorException;
import ${package}.exceptions.NotFoundException;

import org.junit.Test;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import static org.junit.Assert.*;

@Provider
public class ${resource-name}ResponseExceptionMapperTest {

    @Test
    public void handles() {
        ${resource-name}ResponseExceptionMapper ${resource-name-variable}RexceptionMapper = new ${resource-name}ResponseExceptionMapper();
        boolean handlesException200 = ${resource-name-variable}RexceptionMapper.handles(200, null);
        boolean handlesException400 = ${resource-name-variable}RexceptionMapper.handles(400, null);
        boolean handlesException404 = ${resource-name-variable}RexceptionMapper.handles(404, null);
        boolean handlesException500 = ${resource-name-variable}RexceptionMapper.handles(500, null);

        assertFalse(handlesException200);
        assertTrue(handlesException400);
        assertTrue(handlesException404);
        assertTrue(handlesException500);
    }

    @Test
    public void toThrowable() {
        String msg = "messageException";
        ${resource-name}ResponseExceptionMapper target = new ${resource-name}ResponseExceptionMapper();

        Response responseOk = Response.status(Status.OK).entity(msg).build();
        Exception exceptionOk = target.toThrowable(responseOk);

        Response responseBadRequest = Response.status(Status.BAD_REQUEST).entity(msg).build();
        Exception exceptionBadRequest = target.toThrowable(responseBadRequest);

        Response responseNotFound = Response.status(Status.NOT_FOUND).entity(msg).build();
        Exception exceptionNotFound = target.toThrowable(responseNotFound);

        Response responseInternalServerError = Response.status(Status.INTERNAL_SERVER_ERROR).entity(msg).build();
        Exception exceptionInternalServerError = target.toThrowable(responseInternalServerError);

        assertTrue(exceptionBadRequest instanceof BadRequestException);
        assertEquals(msg,exceptionBadRequest.getMessage());

        assertTrue(exceptionNotFound instanceof NotFoundException);
        assertEquals(msg,exceptionNotFound.getMessage());

        assertNull(exceptionOk);

        assertTrue(exceptionInternalServerError instanceof InternalServerErrorException);
        assertEquals(msg,exceptionInternalServerError.getMessage());
    }

}
