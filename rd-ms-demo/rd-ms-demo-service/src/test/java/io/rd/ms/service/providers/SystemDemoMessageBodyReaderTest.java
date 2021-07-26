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
 */
package io.rd.ms.service.providers;

import io.rd.api.model.SystemDemo;
import io.rd.ms.service.entities.Demo;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class SystemDemoMessageBodyReaderTest {

    @Test
    public void testIsReadable() {
        SystemDemoMessageBodyReader target = new SystemDemoMessageBodyReader();
        assertTrue(target.isReadable(Demo.class,null,null,null));
    }

    @Test
    public void testReadFrom() throws IOException {
        String read = "{\"" +
                "id\":null," +
                "\"name\":\"name\" " +
                "}";
        SystemDemoMessageBodyReader target = new SystemDemoMessageBodyReader();
        InputStream inputStream = new ByteArrayInputStream(read.getBytes());
        SystemDemo systemDemo =target.readFrom(null,null,null,null,null, inputStream);
        assertNull(systemDemo.getId());
        assertEquals("name",systemDemo.getName());
    }
}
