package io.radien.exception;

import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

public class LinkedAuthorizationExceptionTest {
    @Test
    public void testBadRequestException(){
        LinkedAuthorizationException exception = new LinkedAuthorizationException();
        assertNotNull(exception);
        LinkedAuthorizationException exception2 = new LinkedAuthorizationException("message");
        assertEquals("message",exception2.getMessage());
    }

}
