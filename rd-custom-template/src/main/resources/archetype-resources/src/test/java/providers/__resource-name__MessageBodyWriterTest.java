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
package ${package}.providers;

import ${client-packageName}.services.${resource-name}Factory;
import ${package}.entities.${resource-name};
import junit.framework.TestCase;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * @author Rajesh Gavvala
 * @author Santana
 */

public class ${resource-name}MessageBodyWriterTest extends TestCase {

    @Test
    public void testIsWriteable() {
        ${resource-name}MessageBodyWriter target = new ${resource-name}MessageBodyWriter();
        assertTrue(target.isWriteable(${resource-name}.class,null,null,null));
    }

    @Test
    public void testGetSize() {
        ${resource-name}MessageBodyWriter target = new ${resource-name}MessageBodyWriter();
        assertEquals(0L,target.getSize(null,null,null,null,null));
    }

    @Test
    public void testWriteTo() throws IOException {
        String result = "{" +
                "\"id\":3," +
                "\"message\":\"testMessage\"" +
                "}";

        ${resource-name}MessageBodyWriter target = new ${resource-name}MessageBodyWriter();
        ${resource-name} ${resource-name-variable} = new ${resource-name}(${resource-name}Factory.create("testMessage"));
        ${resource-name-variable}.setId(3L);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        target.writeTo(${resource-name-variable},null,null,null, null,null, baos);

        assertEquals(result,baos.toString());
    }
}