package io.radien.ms.openid.client.exception;

import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class ExceptionMapperTest {

    @Test
    public void handles() {
        ExceptionMapper uRexceptionMapper = new ExceptionMapper();
        boolean handlesException404 = uRexceptionMapper.handles(404, null);

        assertTrue(handlesException404);
    }

    @Test
    public void toThrowable() {
        String msg = "messageException";
        ExceptionMapper target = new ExceptionMapper();

        Response responseNotFound = Response.status(Response.Status.NOT_FOUND).entity(msg).build();
        Exception exceptionNotFound = target.toThrowable(responseNotFound);

        assertTrue(exceptionNotFound instanceof NotFoundException);
        assertEquals(msg,exceptionNotFound.getMessage());
    }
}
