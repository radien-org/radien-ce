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
package io.radien.ms.permissionmanagement.client.providers;

import junit.framework.TestCase;
import org.junit.Test;

import io.radien.ms.permissionmanagement.client.entities.Permission;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PermissionMessageBodyWriterTest extends TestCase {

    @Test
    public void testIsWriteable() {
        PermissionMessageBodyWriter target = new PermissionMessageBodyWriter();
        assertTrue(target.isWriteable(Permission.class,null,null,null));
    }

    @Test
    public void testGetSize() {
        PermissionMessageBodyWriter target = new PermissionMessageBodyWriter();
        assertEquals(0L,target.getSize(null,null,null,null,null));
    }

    @Test
    public void testWriteTo() throws IOException {
        String result = "{\"" +
                "id\":null," +
                "\"name\":\"a\"," +
                "\"createUser\":null," +
                "\"lastUpdateUser\":null" +
                "}";
        PermissionMessageBodyWriter target = new PermissionMessageBodyWriter();
        Permission permission = new Permission();
        permission.setName("a");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        target.writeTo(permission,null,null,null, null,null, baos);

        assertEquals(result,baos.toString());
    }
}