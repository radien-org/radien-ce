/*
 * Copyright (c) 2021-present radien GmbH & its legal owners. All rights reserved.
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
import io.radien.ms.ecm.client.util.I18NPropertyMapper;
import junit.framework.TestCase;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class I18NPropertyMessageBodyWriterTest extends TestCase {
    @InjectMocks
    private I18NPropertyMessageBodyWriter writer;

    public I18NPropertyMessageBodyWriterTest() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetSize() {
        assertEquals(0L,writer.getSize(null,null,null,null,null));
    }

    @Test
    public void testIsWriteable() {
        assertTrue(writer.isWriteable(I18NProperty.class,null,null,null));
    }

    @Test
    public void testWriteTo() throws IOException {
        String object = "{" +
                "\"key\":\"test\"," +
                "\"type\":\"MESSAGE\"," +
                "\"translations\":[" +
                "{\"language\":\"en\",\"description\":\"test description\"} " +
                "]}";
        String result = "{" +
                "\"key\":\"test\"," +
                "\"type\":\"MESSAGE\"," +
                "\"translations\":\"[" +
                "{\\\"description\\\":\\\"test description\\\",\\\"language\\\":\\\"en\\\"}" +
                "]\"}";
        I18NProperty property = I18NPropertyMapper.map(new ByteArrayInputStream(object.getBytes()));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writer.writeTo(property,null,null,null, null,null, baos);

        assertEquals(result,baos.toString());
    }
}
