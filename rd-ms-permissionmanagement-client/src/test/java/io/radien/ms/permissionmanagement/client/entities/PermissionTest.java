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
package io.radien.ms.permissionmanagement.client.entities;

import org.junit.Test;


import java.util.Date;

import static org.junit.Assert.*;

public class PermissionTest {

    private Permission permission = new Permission();
    private Date terminationDate = new Date();

    public PermissionTest() {
        permission.setId(2L);
        permission.setName("permission-test");
    }

    @Test
    public void getId() {
        assertSame(2L, permission.getId());
    }

    @Test
    public void setId() {
        permission.setId(3L);
        assertSame(3L, permission.getId());
    }

    @Test
    public void getName() {
        assertEquals("permission-test", permission.getName());
    }

    @Test
    public void setName() {
        permission.setName("permission-test");
        assertEquals("permission-test", permission.getName());
    }

    @Test
    public void testConstruct() {
        Action originalAction = new Action();
        originalAction.setId(11L);
        originalAction.setName("originalAction");

        Permission originalPermission = new Permission();
        originalPermission.setActionId(originalAction.getId());
        originalPermission.setResourceId(111111L);
        originalPermission.setId(111L);
        originalPermission.setName("originalPermission");

        Permission newPermission = new Permission(originalPermission);

        assertNotNull(newPermission.getActionId());
        assertNotNull(newPermission.getResourceId());
        assertNotNull(newPermission.getName());
        assertNotNull(newPermission.getId());

        assertEquals(newPermission.getId(), originalPermission.getId());
        assertEquals(newPermission.getName(), originalPermission.getName());
        assertEquals(newPermission.getActionId(), originalPermission.getActionId());
        assertEquals(newPermission.getResourceId(), originalPermission.getResourceId());
    }
}
