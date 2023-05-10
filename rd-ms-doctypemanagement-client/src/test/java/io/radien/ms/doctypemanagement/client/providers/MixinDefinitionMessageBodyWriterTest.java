package io.radien.ms.doctypemanagement.client.providers;

import io.radien.ms.doctypemanagement.client.entities.MixinDefinitionDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;


import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class MixinDefinitionMessageBodyWriterTest {

    @InjectMocks
    MixinDefinitionMessageBodyWriter target;

    @Test
    public void testGetSize() {assertEquals(0L, target.getSize(null,null,null,null,null));}

    @Test
    public void testIsWriteable() {assertTrue(target.isWriteable(MixinDefinitionDTO.class,null,null,null));}

    @Test
    public void testWriteTo() {
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

        MixinDefinitionDTO mixin = new MixinDefinitionDTO();
        mixin.setName("nameValue");
        mixin.setNamespace("nameSpace");
        mixin.setPropertyDefinitions(new ArrayList<>());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        target.writeTo(mixin,null,null,null,null,null,out);

        assertEquals(result,out.toString());
    }
}