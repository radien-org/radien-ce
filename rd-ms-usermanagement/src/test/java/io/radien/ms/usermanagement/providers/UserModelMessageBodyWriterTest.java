package io.radien.ms.usermanagement.providers;


import io.radien.ms.usermanagement.entities.User;
import io.radien.ms.usermanagement.legacy.UserFactory;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class UserModelMessageBodyWriterTest extends TestCase {

    @Test
    public void testIsWriteable() {
        UserModelMessageBodyWriter target = new UserModelMessageBodyWriter();
        assertTrue(target.isWriteable(User.class,null,null,null));
    }

    @Test
    public void testGetSize() {
        UserModelMessageBodyWriter target = new UserModelMessageBodyWriter();
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
        UserModelMessageBodyWriter target = new UserModelMessageBodyWriter();
        User user = UserFactory.create("a","b","logon","sub","email@server.pt", null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        target.writeTo(user,null,null,null, null,null, baos);

        assertEquals(result,baos.toString());
    }
}