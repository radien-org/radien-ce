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

import io.radien.ms.permissionmanagement.client.entities.Action;
import io.radien.ms.permissionmanagement.client.services.ActionFactory;
import org.junit.Test;

import io.radien.ms.permissionmanagement.client.entities.Permission;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class PermissionMessageBodyWriterTest {

    @InjectMocks
    PermissionMessageBodyWriter target;

    @Test
    public void testIsWriteable() {
        assertTrue(target.isWriteable(Permission.class,null,null,null));
    }

    @Test
    public void testGetSize() {
        assertEquals(0L,target.getSize(null,null,null,null,null));
    }

    @Test
    public void testWriteTo() throws IOException {
        String result = "{\"" +
                "id\":null," +
                "\"name\":\"a\"," +
                "\"createUser\":null," +
                "\"lastUpdateUser\":null," +
                "\"actionId\":null" +
                "}";

        Permission permission = new Permission();
        permission.setName("a");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        target.writeTo(permission,null,null,
                null, null,null, out);

        assertEquals(result, out.toString());
    }

    @Test
    public void test2WriteTo() throws IOException {
        String action = "{\"id\":11,\"name\":\"Update-Radien-User\",\"createUser\":28,\"lastUpdateUser\":null}";
        String result = "{\"" +
                "id\":1," +
                "\"name\":\"permission-a\"," +
                "\"createUser\":28," +
                "\"lastUpdateUser\":100," +
                "\"actionId\":11}";

        Permission permission = new Permission();
        permission.setName("permission-a");
        permission.setId(1L);
        permission.setCreateUser(28L);
        permission.setLastUpdateUser(100L);

        Action a = ActionFactory.create("Update-Radien-User", 28L);
        a.setId(11L);
        permission.setActionId(a.getId());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        target.writeTo(permission,null,null,
                null, null,null, out);

        assertEquals(result, out.toString());
    }
}