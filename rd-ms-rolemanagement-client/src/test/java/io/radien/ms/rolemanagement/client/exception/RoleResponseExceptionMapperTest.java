package io.radien.ms.rolemanagement.client.exception;

import junit.framework.TestCase;
import org.junit.Test;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class RoleResponseExceptionMapperTest extends TestCase {

    @Test
    public void testHandles() {
        RoleResponseExceptionMapper rRexceptionMapper = new RoleResponseExceptionMapper();
        boolean handlesException200 = rRexceptionMapper.handles(200, null);
        boolean handlesException400 = rRexceptionMapper.handles(400, null);
        boolean handlesException404 = rRexceptionMapper.handles(404, null);
        boolean handlesException500 = rRexceptionMapper.handles(500, null);

        assertFalse(handlesException200);
        assertTrue(handlesException400);
        assertTrue(handlesException404);
        assertTrue(handlesException500);

    }

    @Test
    public void testThrowable() {
        String msg = "messageException";
        RoleResponseExceptionMapper target = new RoleResponseExceptionMapper();

        Response responseOk = Response.status(Response.Status.OK).entity(msg).build();
        Exception exceptionOk = target.toThrowable(responseOk);

        Response responseBadRequest = Response.status(Response.Status.BAD_REQUEST).entity(msg).build();
        Exception exceptionBadRequest = target.toThrowable(responseBadRequest);

        Response responseNotFound = Response.status(Response.Status.NOT_FOUND).entity(msg).build();
        Exception exceptionNotFound = target.toThrowable(responseNotFound);

        Response responseInternalServerError = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(msg).build();
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