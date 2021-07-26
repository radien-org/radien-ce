package io.radien.ms.rolemanagement.providers;

import io.radien.api.model.role.SystemRole;
import io.radien.ms.rolemanagement.entities.RoleEntity;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SystemRoleMessageBodyReaderTest {

    @Test
    public void testIsReadable() {
        SystemRoleMessageBodyReader target = new SystemRoleMessageBodyReader();
        assertTrue(target.isReadable(RoleEntity.class, null, null, null));
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