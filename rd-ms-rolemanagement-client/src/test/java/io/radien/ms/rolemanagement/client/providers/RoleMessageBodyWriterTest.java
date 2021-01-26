package io.radien.ms.rolemanagement.client.providers;

import io.radien.ms.rolemanagement.client.entities.Role;
import io.radien.ms.rolemanagement.client.services.RoleFactory;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.ByteArrayOutputStream;

import static org.junit.Assert.*;

public class RoleMessageBodyWriterTest extends TestCase {

    @Test
    public void testGetSize() {
        RoleMessageBodyWriter target = new RoleMessageBodyWriter();
        assertEquals(0L,target.getSize(null,null,null,null,null));
    }

    @Test
    public void testIsWriteable() {
        RoleMessageBodyWriter target = new RoleMessageBodyWriter();
        assertTrue(target.isWriteable(Role.class,null,null,null));
    }

    @Test
    public void testWriteTo() {
        String result = "{\"" +
                "id\":null," +
                "\"name\":\"nameValue\"," +
                "\"description\":\"descriptionValue\"," +
                "\"createUser\":2," +
                "\"lastUpdateUser\":null" +
                "}";
        RoleMessageBodyWriter target = new RoleMessageBodyWriter();
        Role user = RoleFactory.create("nameValue","descriptionValue",2L);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        target.writeTo(user,null,null,null, null,null, baos);

        assertEquals(result,baos.toString());
    }
}