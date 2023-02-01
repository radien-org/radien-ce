package io.radien.ms.doctypemanagement.client.providers;


import io.radien.ms.doctypemanagement.client.entities.PropertyDefinition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayOutputStream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class PropertyDefinitionMessageBodyWriterTest {

    @InjectMocks
    PropertyDefinitionMessageBodyWriter target;

    @Test
    public void testGetSize() {assertEquals(0L, target.getSize(null,null,null,null,null));}

    @Test
    public void testIsWriteable() {assertTrue(target.isWriteable(PropertyDefinition.class,null,null,null));}

    @Test
    public void testWriteTo() {
        String result = "{\"" +
                "id\":null," +
                "\"name\":\"nameValue\"," +
                "\"mandatory\":true," +
                "\"protected\":false," +
                "\"requiredType\":1," +
                "\"multiple\":false," +
                "\"createDate\":null,"+
                "\"lastUpdate\":null,"+
                "\"createUser\":null,"+
                "\"lastUpdateUser\":null"+
                "}";

        PropertyDefinition property = new PropertyDefinition();
        property.setName("nameValue");
        property.setRequiredType(1);
        property.setMandatory(true);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        target.writeTo(property,null,null,null,null,null,out);

        assertEquals(result,out.toString());
    }
}