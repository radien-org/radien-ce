package io.radien.ms.usermanagement.client.providers;

import io.radien.ms.usermanagement.client.entities.User;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class UserMessageBodyReaderTest extends TestCase {

    @Test
    public void testIsReadable() {
        UserMessageBodyReader target = new UserMessageBodyReader();
        assertTrue(target.isReadable(User.class,null,null,null));
    }

    @Test
    public void testReadFrom() throws IOException {
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
        UserMessageBodyReader target = new UserMessageBodyReader();
        InputStream in = new ByteArrayInputStream(result.getBytes());
        User user = target.readFrom(null,null,null,null,null, in);
        assertNull(user.getId());
        assertEquals("logon",user.getLogon());
        assertEquals("email@server.pt",user.getUserEmail());
        assertNull(user.getCreateUser());
        assertNull(user.getLastUpdateUser());
        assertEquals("sub",user.getSub());
        assertEquals("a",user.getFirstname());
        assertEquals("b",user.getLastname());

    }
}