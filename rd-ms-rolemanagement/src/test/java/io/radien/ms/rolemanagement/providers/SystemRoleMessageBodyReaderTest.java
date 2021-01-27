package io.radien.ms.rolemanagement.providers;

import io.radien.api.model.role.SystemRole;
import io.radien.ms.rolemanagement.entities.Role;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;


public class SystemRoleMessageBodyReaderTest extends TestCase {

    @Test
    public void testIsReadable() {
        SystemRoleMessageBodyReader target = new SystemRoleMessageBodyReader();
        assertTrue(target.isReadable(Role.class, null, null, null));
    }

    @Test
    public void testReadFrom() {
        String result = "{\"" +
                "id\":null," +
                "\"name\":\"nameValue\"," +
                "\"description\":\"descriptionValue\"" +
                "}";

        SystemRoleMessageBodyReader target = new SystemRoleMessageBodyReader();
        InputStream in = new ByteArrayInputStream(result.getBytes());

        SystemRole role = target.readFrom(null, null, null, null, null, in);

        assertNull(role.getId());
        assertEquals("nameValue", role.getName());
        assertEquals("descriptionValue", role.getDescription());
    }
}