/*
 * Copyright (c) 2021-present radien GmbH. All rights reserved.
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

import ${package}.ms.service.entities.${entityResourceName};
import ${package}.ms.service.legacy.${entityResourceName}Factory;

import junit.framework.TestCase;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;

public class ${entityResourceName}ModelListMessageBodyWriterTest extends TestCase {
    ${entityResourceName}ModelListMessageBodyWriter target = new ${entityResourceName}ModelListMessageBodyWriter();

    @Test
    public void testIsWriteable() {
        assertTrue(target.isWriteable(null,null,null,null));
    }

    @Test
    public void testGetSize() {
        assertEquals(0,target.getSize(null,null,null,null,null));
    }

    @Test
    public void testWriteTo() throws IOException {
        String result = "[{" +
                "\"id\":null," +
                "\"name\":\"name\"" +
                "}]";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ${entityResourceName} u = ${entityResourceName}Factory.create("name");
        target.writeTo(Collections.singletonList(u),null,null,null,null,null,baos);
        assertEquals(result,baos.toString());
    }
}
