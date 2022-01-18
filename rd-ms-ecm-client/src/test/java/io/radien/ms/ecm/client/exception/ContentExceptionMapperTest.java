/*
 *
 *  * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
 *  * <p>
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  * <p>
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  * <p>
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package io.radien.ms.ecm.client.exception;

import io.radien.exception.NotFoundException;
import io.radien.exception.TokenExpiredException;
import javax.ws.rs.core.Response;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ContentExceptionMapperTest {
    @Test
    public void handles() {
        ContentExceptionMapper uRexceptionMapper = new ContentExceptionMapper();
        boolean handlesException401 = uRexceptionMapper.handles(401, null);
        assertTrue(handlesException401);

        boolean handlesException404 = uRexceptionMapper.handles(404, null);
        assertTrue(handlesException404);
    }

    @Test
    public void toThrowable() {
        String msg = "messageException";
        ContentExceptionMapper target = new ContentExceptionMapper();

        Response responseUnauthorized = Response.status(Response.Status.UNAUTHORIZED).entity(msg).build();
        Exception exceptionUnauthorized = target.toThrowable(responseUnauthorized);

        assertTrue(exceptionUnauthorized instanceof TokenExpiredException);
        assertEquals(msg,exceptionUnauthorized.getMessage());

        Response responseNotFound = Response.status(Response.Status.NOT_FOUND).entity(msg).build();
        Exception exceptionNotFound = target.toThrowable(responseNotFound);

        assertTrue(exceptionNotFound instanceof NotFoundException);
        assertEquals(msg,exceptionNotFound.getMessage());

        Response responseOk = Response.status(Response.Status.OK).entity(msg).build();
        Exception exceptionOk = target.toThrowable(responseOk);

        assertNull(exceptionOk);
    }
}
