package io.radien.ms.openid.client.exception;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class NotFoundExceptionTestCase {

    @Test
    public void testBadRequestException(){
        NotFoundException exception = new NotFoundException("message");
        assertNotNull(exception);
        assertEquals("message", exception.getMessage());
    }
}
