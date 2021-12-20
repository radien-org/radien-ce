/*
 * Copyright (c) 2016-present openappframe.org & its legal owners. All rights reserved.
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
 *
 */
package io.radien.ms.ecm.client.providers;

import io.radien.ms.ecm.client.entities.legal.LegalDocumentType;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Class test for {@link LegalDocumentTypeMessageBodyReader}
 * @author Newton Carvalho
 */
public class LegalDocumentTypeMessageBodyReaderTest {
    @InjectMocks
    private LegalDocumentTypeMessageBodyReader reader;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test for method {@link LegalDocumentTypeMessageBodyReader#isReadable(Class, Type, Annotation[], MediaType)}
     */
    @Test
    public void testIsReadable() {
        assertTrue(reader.isReadable(LegalDocumentType.class, null, null, null));
    }

    /**
     * Test for method {@link LegalDocumentTypeMessageBodyReader#readFrom(Class, Type, Annotation[], MediaType, MultivaluedMap, InputStream)}
     */
    @Test
    public void testReadFrom() {
        Long id = 1L, tenantId = 2L;
        String name = "ToU";

        final String jsonPattern = "{\"id\": %d, \"tenantId\": %d, " +
                "\"name\": \"%s\", \"toBeShown\": %b, " +
                "\"toBeAccepted\": %b}";
        String jsonAsString = String.format(jsonPattern, id, tenantId, name, true, true);

        InputStream in = new ByteArrayInputStream(jsonAsString.getBytes());
        LegalDocumentType legalDocumentType = reader.readFrom(null, null,
                null, null, null, in);

        assertEquals(id, legalDocumentType.getId());
        assertEquals(tenantId, legalDocumentType.getTenantId());
        assertEquals(name, legalDocumentType.getName());
    }
}
