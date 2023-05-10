package io.radien.ms.notificationmanagement.client.exception;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InvalidURIExceptionTest {

    @Test
    public void ConstructorTest(){
        String exceptionText = ("Test exception");
        InvalidURIException e = new InvalidURIException(exceptionText);
        assertEquals(e.getMessage(), exceptionText);
    }

}