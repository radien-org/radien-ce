package io.radien.ms.usermanagement.client.exceptions;

import junit.framework.TestCase;
import org.junit.Test;

public class BadRequestExceptionTest extends TestCase {

    @Test
    public void testBadRequestException(){
        BadRequestException exception = new BadRequestException();
        assertNotNull(exception);
        BadRequestException exception2 = new BadRequestException("message");
        assertEquals("message",exception2.getMessage());
    }

}