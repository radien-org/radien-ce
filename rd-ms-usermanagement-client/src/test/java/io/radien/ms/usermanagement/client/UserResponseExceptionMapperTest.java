package io.radien.ms.usermanagement.client;

import io.radien.ms.usermanagement.client.exceptions.BadRequestException;
import io.radien.ms.usermanagement.client.exceptions.InternalServerErrorException;
import io.radien.ms.usermanagement.client.exceptions.NotFoundException;
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
        boolean handlesException404 = uRexceptionMapper.handles(404, null);
        boolean handlesException500 = uRexceptionMapper.handles(500, null);

        assertFalse(handlesException200);
        assertTrue(handlesException400);
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
