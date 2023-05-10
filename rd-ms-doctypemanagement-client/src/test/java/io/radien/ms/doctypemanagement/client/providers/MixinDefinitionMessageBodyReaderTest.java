package io.radien.ms.doctypemanagement.client.providers;


import io.radien.ms.doctypemanagement.client.entities.MixinDefinitionDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import javax.ws.rs.WebApplicationException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class MixinDefinitionMessageBodyReaderTest{

    @InjectMocks
    MixinDefinitionMessageBodyReader target;

    @Test
    public void testIsReadable() {assertTrue(target.isReadable(MixinDefinitionDTO.class,null,null,null));}

    @Test
    public void testReadFrom() {
        String result = "{\"" +
                "id\":null," +
                "\"name\":\"nameValue\"," +
                "\"namespace\":\"nameSpace\"," +
                "\"propertyDefinitions\":[],"+
                "\"abstract\":false,"+
                "\"queryable\":false,"+
                "\"mixin\":true,"+
                "\"createDate\":null,"+
                "\"lastUpdate\":null,"+
                "\"createUser\":null,"+
                "\"lastUpdateUser\":null"+
                "}";

        InputStream in = new ByteArrayInputStream(result.getBytes());
        MixinDefinitionDTO mixinDefinitionDTO = target.readFrom(null,null,null,null,null,in);

        assertNull(mixinDefinitionDTO.getId());
        assertEquals("nameValue", mixinDefinitionDTO.getName());
        assertEquals("nameSpace", mixinDefinitionDTO.getNamespace());
    }

    @Test(expected = WebApplicationException.class)
    public void testReadFromException() {
        String result = "{\"" +
                "id\":null," +
                "\"name\":\"nameValue\"," +
                "\"namespace\":\"nameSpace\"," +
                "\"propertyDefinitions\":[],"+
                "\"abstract\":false,"+
                "\"queryable\":false,"+
                "\"mixin\":true,"+
                "\"createDate\":\"null\","+
                "\"lastUpdate\":null,"+
                "\"createUser\":null,"+
                "\"lastUpdateUser\":null"+
                "}";

        InputStream in = new ByteArrayInputStream(result.getBytes());
        MixinDefinitionDTO mixinDefinitionDTO = target.readFrom(null,null,null,null,null,in);
    }
}