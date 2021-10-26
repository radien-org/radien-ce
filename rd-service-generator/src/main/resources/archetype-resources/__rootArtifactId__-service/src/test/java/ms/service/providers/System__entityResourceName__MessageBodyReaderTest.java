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
package ${package}.ms.service.providers;

import ${package}.api.model.System${entityResourceName};
import ${package}.ms.service.entities.${entityResourceName};

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class System${entityResourceName}MessageBodyReaderTest {

    @Test
    public void testIsReadable() {
        System${entityResourceName}MessageBodyReader target = new System${entityResourceName}MessageBodyReader();
        assertTrue(target.isReadable(${entityResourceName}.class,null,null,null));
    }

    @Test
    public void testReadFrom() throws IOException {
        String read = "{\"" +
                "id\":null," +
                "\"name\":\"name\" " +
                "}";
        System${entityResourceName}MessageBodyReader target = new System${entityResourceName}MessageBodyReader();
        InputStream inputStream = new ByteArrayInputStream(read.getBytes());
        System${entityResourceName} system${entityResourceName} =target.readFrom(null,null,null,null,null, inputStream);
        assertNull(system${entityResourceName}.getId());
        assertEquals("name",system${entityResourceName}.getName());
    }
}
