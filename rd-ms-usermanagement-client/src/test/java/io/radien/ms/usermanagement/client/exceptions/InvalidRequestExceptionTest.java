package io.radien.ms.usermanagement.client.exceptions;

import junit.framework.TestCase;
import org.junit.Test;

import static org.junit.Assert.*;

public class InvalidRequestExceptionTest extends TestCase {
    @Test
    public void testInvalidRequestException(){
        InvalidRequestException exception = new InvalidRequestException();
        assertNotNull(exception);
        InvalidRequestException exception2 = new InvalidRequestException("message");
        assertEquals("message",exception2.getMessage());
    }
}