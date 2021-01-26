package io.radien.ms.rolemanagement.client.exception;

import junit.framework.TestCase;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class RoleErrorCodeMessageTest extends TestCase {

    @Test
    public void testToString() {
        assertEquals("{\"code\":101, \"key\":\"error.duplicated.field\", \"message\":\"There is more than one role with the same value for the field: %s\"}", RoleErrorCodeMessage.DUPLICATED_FIELD.toString());
        assertEquals("{\"code\":101, \"key\":\"error.duplicated.field\", \"message\":\"There is more than one role with the same value for the field: Email Address\"}", RoleErrorCodeMessage.DUPLICATED_FIELD.toString("Email Address"));
    }
}