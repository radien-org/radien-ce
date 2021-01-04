package io.radien.ms.usermanagement.client.exceptions;

import junit.framework.TestCase;
import org.junit.Test;

import static org.junit.Assert.*;

public class NotFoundExceptionTest extends TestCase {

    @Test
    public void testNotFoundException(){
        NotFoundException exception = new NotFoundException();
        assertNotNull(exception);
        NotFoundException exception2 = new NotFoundException("message");
        assertEquals("message",exception2.getMessage());
    }

}