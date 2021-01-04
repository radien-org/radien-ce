package io.radien.ms.usermanagement.client.providers;

import io.radien.ms.usermanagement.client.entities.Page;
import io.radien.ms.usermanagement.client.entities.User;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class PageMessageBodyReaderTest extends TestCase {

    @Test
    public void testIsReadable() {
        PageMessageBodyReader target = new PageMessageBodyReader();
        assertTrue(target.isReadable(Page.class,null,null,null));
    }

    @Test
    public void testReadFrom() throws IOException {
        String example = "{\n" +
                "\"currentPage\": -1,\n" +
                "\"results\": [\n" +
                "{\n" +
                "\"enabled\": false,\n" +
                "\"firstname\": \"a\",\n" +
                "\"id\": 28,\n" +
                "\"lastname\": \"b\",\n" +
                "\"logon\": \"aa34433@email.tt\",\n" +
                "\"userEmail\": \"aa234433@email.tt\"\n" +
                "}\n" +
                "],\n" +
                "\"totalPages\": 1,\n" +
                "\"totalResults\": 4\n" +
                "}";
        PageMessageBodyReader target = new PageMessageBodyReader();
        InputStream in = new ByteArrayInputStream(example.getBytes());
        Page<User> page = target.readFrom(null,null,null,null,null,in);
        assertEquals(1,page.getTotalPages());
        assertEquals(4,page.getTotalResults());
        assertEquals(-1,page.getCurrentPage());
        assertEquals(1,page.getResults().size());
        User u = page.getResults().get(0);
        assertEquals("a",u.getFirstname());
        assertEquals(28L,(long) u.getId());
        assertFalse(u.isEnabled());
    }
}