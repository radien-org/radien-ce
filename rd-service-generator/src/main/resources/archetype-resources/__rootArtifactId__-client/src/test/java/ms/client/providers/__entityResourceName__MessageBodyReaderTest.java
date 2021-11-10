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
package ${package}.ms.client.providers;

import ${package}.ms.client.entities.${entityResourceName};
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class ${entityResourceName}MessageBodyReaderTest {

    ${entityResourceName}MessageBodyReader target;

    @Before
    public void before() {
        target = new ${entityResourceName}MessageBodyReader();
    }

    @Test
    public void testIsReadable() {
        assertTrue(target.isReadable(${entityResourceName}.class,null,null,null));
    }

    @Test
    public void testReadFrom() throws IOException {
        String result = "{\"" +
                "id\":null," +
                "\"name\":\"name\"" +
                "}";

        InputStream in = new ByteArrayInputStream(result.getBytes());

        ${entityResourceName} ${entityResourceName.toLowerCase()} = target.readFrom(null,null,null,null,null, in);
        assertNull(${entityResourceName.toLowerCase()}.getId());
        assertEquals("name",${entityResourceName.toLowerCase()}.getName());
    }
}
