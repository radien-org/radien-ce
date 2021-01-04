package io.radien.ms.usermanagement.client.exceptions;

import junit.framework.TestCase;
import org.junit.Test;

public class InternalServerErrorExceptionTest extends TestCase {

    @Test
    public void testInternalServerErrorException(){
        InternalServerErrorException exception = new InternalServerErrorException();
        assertNotNull(exception);
        InternalServerErrorException exception2 = new InternalServerErrorException("message");
        assertEquals("message",exception2.getMessage());
    }

}