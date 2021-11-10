/*
 * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
