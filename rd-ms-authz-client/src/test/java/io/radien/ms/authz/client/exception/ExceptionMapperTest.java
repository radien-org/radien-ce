package io.radien.ms.authz.client.exception;

import io.radien.exception.TokenExpiredException;
import io.radien.ms.authz.client.exception.ExceptionMapper;
import io.radien.ms.authz.client.exception.NotFoundException;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class ExceptionMapperTest {

    @Test
    public void handles() {
        ExceptionMapper uRexceptionMapper = new ExceptionMapper();
        boolean handlesException401 = uRexceptionMapper.handles(401, null);
        assertTrue(handlesException401);

        boolean handlesException404 = uRexceptionMapper.handles(404, null);
        assertTrue(handlesException404);
    }

    @Test
    public void toThrowable() {
        String msg = "messageException";
        ExceptionMapper target = new ExceptionMapper();

        Response responseUnauthorized = Response.status(Response.Status.UNAUTHORIZED).entity(msg).build();
        Exception exceptionUnauthorized = target.toThrowable(responseUnauthorized);

        assertTrue(exceptionUnauthorized instanceof TokenExpiredException);
        assertEquals(msg,exceptionUnauthorized.getMessage());

        Response responseNotFound = Response.status(Response.Status.NOT_FOUND).entity(msg).build();
        Exception exceptionNotFound = target.toThrowable(responseNotFound);

        assertTrue(exceptionNotFound instanceof NotFoundException);
        assertEquals(msg,exceptionNotFound.getMessage());
    }
}
