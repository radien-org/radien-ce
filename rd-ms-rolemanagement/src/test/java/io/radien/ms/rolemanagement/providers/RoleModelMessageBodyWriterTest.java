package io.radien.ms.rolemanagement.providers;

import io.radien.ms.rolemanagement.entities.Role;
import io.radien.ms.rolemanagement.factory.RoleFactory;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class RoleModelMessageBodyWriterTest extends TestCase {

    @Test
    public void testIsWriteable() {
        RoleModelMessageBodyWriter target = new RoleModelMessageBodyWriter();
        assertTrue(target.isWriteable(Role.class,null,null,null));
    }

    @Test
    public void testGetSize() {
        RoleModelMessageBodyWriter target = new RoleModelMessageBodyWriter();
        assertEquals(0L,target.getSize(null,null,null,null,null));
    }

    @Test
    public void testWriteTo() throws IOException {
        String result = "{\"" +
                "id\":null," +
                "\"name\":\"nameValue\"," +
                "\"description\":\"descriptionValue\"," +
                "\"createUser\":2," +
                "\"lastUpdateUser\":null" +
                "}";
        RoleModelMessageBodyWriter target = new RoleModelMessageBodyWriter();
        Role user = RoleFactory.create("nameValue","descriptionValue",2L);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        target.writeTo(user,null,null,null, null,null, baos);

        assertEquals(result,baos.toString());
    }
}