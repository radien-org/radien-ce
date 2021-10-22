package io.radien.ms.ecm.client.providers;

import io.radien.ms.ecm.client.entities.I18NProperty;
import io.radien.ms.ecm.client.entities.LabelTypeEnum;
import junit.framework.TestCase;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class I18NPropertyMessageBodyReaderTest extends TestCase {
    @InjectMocks
    private I18NPropertyMessageBodyReader reader;

    public I18NPropertyMessageBodyReaderTest() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testIsReadable() {
        assertTrue(reader.isReadable(I18NProperty.class, null, null, null));
    }

    @Test
    public void testReadFrom() throws IOException {
        String object = "{ " +
                "\"key\": \"test\", " +
                "\"type\": \"MESSAGE\", " +
                "\"translations\": [ " +
                "{ \"language\": \"en\", \"description\": \"test description\" } " +
                "] }";
        InputStream in = new ByteArrayInputStream(object.getBytes());
        I18NProperty read = reader.readFrom(null, null, null, null, null, in);

        assertEquals(read.getKey(), "test");
        assertEquals(read.getType(), LabelTypeEnum.MESSAGE);
        assertEquals(read.getTranslations().size(), 1);
    }
}
