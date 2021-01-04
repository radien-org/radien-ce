package io.radien.ms.usermanagement.client.providers;

import io.radien.ms.usermanagement.client.entities.User;
import io.radien.ms.usermanagement.client.services.UserFactory;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class UserMessageBodyWriterTest extends TestCase {

    @Test
    public void testIsWriteable() {
        UserMessageBodyWriter target = new UserMessageBodyWriter();
        assertTrue(target.isWriteable(User.class,null,null,null));
    }

    @Test
    public void testGetSize() {
        UserMessageBodyWriter target = new UserMessageBodyWriter();
        assertEquals(0L,target.getSize(null,null,null,null,null));
    }

    @Test
    public void testWriteTo() throws IOException {
        String result = "{\"" +
                "id\":null," +
                "\"logon\":\"logon\"," +
                "\"userEmail\":\"email@server.pt\"," +
                "\"createUser\":null," +
                "\"lastUpdateUser\":null," +
                "\"sub\":\"sub\"," +
                "\"firstname\":\"a\"," +
                "\"lastname\":\"b\"" +
                "}";
        UserMessageBodyWriter target = new UserMessageBodyWriter();
        User user = UserFactory.create("a","b","logon","sub","email@server.pt", null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        target.writeTo(user,null,null,null, null,null, baos);

        assertEquals(result,baos.toString());
    }
}