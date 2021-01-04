package io.radien.ms.usermanagement.client.exceptions;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ErrorCodeMessageTest {

    @Test
    public void testToString() {
        assertEquals("{\"code\":101, \"key\":\"error.duplicated.field\", \"message\":\"There is more than one resource with the same value for the field: %s\"}", ErrorCodeMessage.DUPLICATED_FIELD.toString());
        assertEquals("{\"code\":101, \"key\":\"error.duplicated.field\", \"message\":\"There is more than one resource with the same value for the field: Email Address\"}", ErrorCodeMessage.DUPLICATED_FIELD.toString("Email Address"));
    }
}
