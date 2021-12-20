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
import io.radien.ms.ecm.client.util.LegalDocumentTypeMapper;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.json.JsonObject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Class test for {@link LegalDocumentTypeMessageBodyWriter}
 * @author Newton Carvalho
 */
public class LegalDocumentTypeMessageBodyWriterTest {

    @InjectMocks
    private LegalDocumentTypeMessageBodyWriter writer;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test for method {@link LegalDocumentTypeMessageBodyWriter#getSize(LegalDocumentType, Class, Type, Annotation[], MediaType)}
     */
    @Test
    public void testGetSize() {
        assertEquals(0L,writer.getSize(null,null,null,null,null));
    }

    /**
     * Test for method {@link LegalDocumentTypeMessageBodyWriter#isWriteable(Class, Type, Annotation[], MediaType)}
     */
    @Test
    public void testIsWriteable() {
        assertTrue(writer.isWriteable(LegalDocumentType.class,null,null,null));
    }

    /**
     * Test for method {@link LegalDocumentTypeMessageBodyWriter#writeTo(LegalDocumentType, Class, Type, Annotation[], MediaType, MultivaluedMap, OutputStream)} 
     */
    @Test
    public void testWriteTo() {
        Long id = 1L, tenantId = 2L;
        String name = "ToU";

        LegalDocumentType type = new LegalDocumentType();
        type.setId(id);
        type.setTenantId(tenantId);
        type.setName(name);

        JsonObject expectedJsonObject = LegalDocumentTypeMapper.map(type);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        writer.writeTo(type, null, null, null, null,
                null, outputStream);

        byte[] bytes = outputStream.toByteArray();
        String resultJsonAsString = new String(bytes);
        assertEquals(expectedJsonObject.toString(), resultJsonAsString);
    }

}
