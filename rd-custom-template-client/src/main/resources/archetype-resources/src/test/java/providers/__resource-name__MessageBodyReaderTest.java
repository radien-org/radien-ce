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
package ${package}.providers;

import ${package}.entities.${resource-name};

import junit.framework.TestCase;
import org.junit.Test;

import javax.ws.rs.WebApplicationException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Rajesh Gavvala
 * @author Santana
 */
public class ${resource-name}MessageBodyReaderTest extends TestCase {

    @Test
    public void testIsReadable() {
        ${resource-name}MessageBodyReader target = new ${resource-name}MessageBodyReader();
        assertTrue(target.isReadable(${resource-name}.class,null,null,null));
    }

    @Test
    public void testReadFrom() throws IOException {
        String read = "{\n" +
                "        \"id\": 3,\n" +
                "        \"message\": \"testMessage\"" +
                "    }";
        ${resource-name}MessageBodyReader target = new ${resource-name}MessageBodyReader();
        InputStream inputStream = new ByteArrayInputStream(read.getBytes());
        ${resource-name} ${resource-name-variable} =target.readFrom(null,null,null,null,null, inputStream);
        assertEquals((Long)3L, ${resource-name-variable}.getId());
        assertEquals("testMessage",${resource-name-variable}.getMessage());
    }
}