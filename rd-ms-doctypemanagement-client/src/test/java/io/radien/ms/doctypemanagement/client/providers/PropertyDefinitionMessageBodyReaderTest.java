package io.radien.ms.doctypemanagement.client.providers;

import io.radien.ms.doctypemanagement.client.entities.PropertyDefinition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import javax.ws.rs.WebApplicationException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class PropertyDefinitionMessageBodyReaderTest {

    @InjectMocks
    PropertyDefinitionMessageBodyReader target;

    @Test
    public void testIsReadable() {assertTrue(target.isReadable(PropertyDefinition.class, null, null, null));}

    @Test
    public void testReadFrom() {
        String result = "{\"" +
                "id\":null," +
                "\"name\":\"nameValue\"," +
                "\"mandatory\":\"true\", " +
                "\"protected\":\"true\", " +
                "\"propertyType\":\"true\", " +
                "\"multiple\":\"false\", " +
                "\"requiredType\":1," +
                "\"multiple\":\"false\" " +
                "}";

        InputStream in = new ByteArrayInputStream(result.getBytes());
        PropertyDefinition propertyDefinition = target.readFrom(null,null,null,null,null,in);

        assertNull(propertyDefinition.getId());
        assertEquals("nameValue", propertyDefinition.getName());
    }

    @Test(expected = WebApplicationException.class)
    public void testReadFromException() {
        String result = "{\"" +
                "id\":null," +
                "\"name\":\"nameValue\"," +
                "\"mandatory\":true, " +
                "\"protected\":true, " +
                "\"propertyType\":true, " +
                "\"multiple\":false, " +
                "\"requiredType\":1," +
                "\"multiple\":false, " +
                "\"createDate\":\"null\","+
                "\"lastUpdate\":null,"+
                "\"createUser\":null,"+
                "\"lastUpdateUser\":null"+
                "}";

        InputStream in = new ByteArrayInputStream(result.getBytes());
        PropertyDefinition propertyDefinition = target.readFrom(null,null,null,null,null,in);
    }
}