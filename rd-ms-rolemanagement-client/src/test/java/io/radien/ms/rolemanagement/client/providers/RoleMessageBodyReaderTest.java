package io.radien.ms.rolemanagement.client.providers;

import io.radien.ms.rolemanagement.client.entities.Role;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class RoleMessageBodyReaderTest extends TestCase {

    @Test
    public void testIsReadable() {
        RoleMessageBodyReader target = new RoleMessageBodyReader();
        assertTrue(target.isReadable(Role.class, null, null, null));
    }

    @Test
    public void testReadFrom() {
        String result = "{\"" +
                "id\":null," +
                "\"name\":\"nameValue\"," +
                "\"description\":\"descriptionValue\"" +
                "}";

        RoleMessageBodyReader target = new RoleMessageBodyReader();
        InputStream in = new ByteArrayInputStream(result.getBytes());

        Role role = target.readFrom(null, null, null, null, null, in);

        assertNull(role.getId());
        assertEquals("nameValue", role.getName());
        assertEquals("descriptionValue", role.getDescription());
    }
}