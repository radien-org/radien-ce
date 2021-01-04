package io.radien.ms.usermanagement.providers;


import io.radien.ms.usermanagement.entities.User;
import io.radien.ms.usermanagement.legacy.UserFactory;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;


public class UserModelListMessageBodyWriterTest extends TestCase {

    @Test
    public void testIsWriteable() {
        UserModelListMessageBodyWriter target = new UserModelListMessageBodyWriter();
        assertTrue(target.isWriteable(null,null,null,null));
    }

    @Test
    public void testGetSize() {
        UserModelListMessageBodyWriter target = new UserModelListMessageBodyWriter();
        assertEquals(0,target.getSize(null,null,null,null,null));
    }

    @Test
    public void testWriteTo() throws IOException {
        String result = "[{" +
                            "\"id\":null," +
                            "\"logon\":\"logon\"," +
                            "\"userEmail\":\"email@server.pt\"," +
                            "\"createUser\":null," +
                            "\"lastUpdateUser\":null," +
                            "\"sub\":\"sub\"," +
                            "\"firstname\":\"a\"," +
                            "\"lastname\":\"b\"" +
                        "}]";
        UserModelListMessageBodyWriter target = new UserModelListMessageBodyWriter();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        User u = UserFactory.create("a","b","logon","sub","email@server.pt", null);
        target.writeTo(Collections.singletonList(u),null,null,null,null,null,baos);
        assertEquals(result,baos.toString());
    }
}