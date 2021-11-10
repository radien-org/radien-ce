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
package io.radien.ms.rolemanagement.client.providers;

import io.radien.ms.rolemanagement.client.entities.Role;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class RoleMessageBodyReaderTest extends TestCase {

    @Test
    public void testIsReadable() {
        RoleMessageBodyReader target = new RoleMessageBodyReader();
        assertTrue(target.isReadable(Role.class, null, null, null));
    }

    @Test
    public void testReadFrom() {
        String result = "{\"" +
                "id\":null," +
                "\"name\":\"nameValue\"," +
                "\"description\":\"descriptionValue\"" +
                "}";

        RoleMessageBodyReader target = new RoleMessageBodyReader();
        InputStream in = new ByteArrayInputStream(result.getBytes());

        Role role = target.readFrom(null, null, null, null, null, in);

        assertNull(role.getId());
        assertEquals("nameValue", role.getName());
        assertEquals("descriptionValue", role.getDescription());
    }
}