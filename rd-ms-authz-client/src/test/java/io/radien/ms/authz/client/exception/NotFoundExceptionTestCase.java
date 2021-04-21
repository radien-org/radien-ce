package io.radien.ms.authz.client.exception;

import io.radien.ms.authz.client.exception.NotFoundException;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class NotFoundExceptionTestCase {

    @Test
    public void testBadRequestException(){
        NotFoundException exception = new NotFoundException("message");
        assertNotNull(exception);
        assertEquals("message", exception.getMessage());

        NotFoundException exception2 = new NotFoundException("message",
                new RuntimeException("test"));
        assertNotNull(exception2);
        assertEquals("message", exception2.getMessage());
        assertNotNull(exception2.getCause());
    }
}
