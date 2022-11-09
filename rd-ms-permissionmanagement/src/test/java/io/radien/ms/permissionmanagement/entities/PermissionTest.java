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
package io.radien.ms.permissionmanagement.entities;

import io.radien.api.model.permission.SystemAction;
import io.radien.api.model.permission.SystemPermission;
import io.radien.ms.permissionmanagement.legacy.ActionFactory;
import io.radien.ms.permissionmanagement.legacy.PermissionFactory;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.Date;

public class PermissionTest extends TestCase {

    PermissionEntity permission;
    ActionEntity action;
    private final Date terminationDate = new Date();

    public PermissionTest() {
        action = ActionFactory.create("Create Contract", 3l);
        action.setId(33L);
        permission = PermissionFactory.create("testPermissionName", action.getId(), 3L);
        permission.setId(2L);
    }

    @Test
    public void testGetId() {
        assertNotNull(permission.getId());
        assertEquals((Long) 2L, permission.getId());
    }

    @Test
    public void testSetId() {
        permission.setId(4L);

        assertNotNull(permission.getId());
        assertEquals((Long) 4L, permission.getId());
    }

    @Test
    public void testGetName() {
        assertNotNull(permission.getName());
        assertEquals("testPermissionName", permission.getName());
    }

    @Test
    public void testSetName() {
        permission.setName("permTestRefactored");
        assertNotNull(permission.getName());
        assertEquals("permTestRefactored", permission.getName());
    }

    @Test
    public void testGetAction() {
        assertNotNull(permission.getActionId());
    }

    @Test
    public void testSetAction() {
        SystemAction newAction = ActionFactory.create("Update Contract", null);
        newAction.setId(111111L);
        permission.setActionId(newAction.getId());
        assertNotNull(permission.getActionId());
        assertEquals(permission.getActionId(), newAction.getId());
    }

    @Test
    public void testConstructor() {
        io.radien.ms.permissionmanagement.client.entities.Permission systemPermission =
                new io.radien.ms.permissionmanagement.client.entities.Permission();

        systemPermission.setId(11L);
        systemPermission.setName("test");

        SystemAction systemAction =
                new io.radien.ms.permissionmanagement.client.entities.Action();

        systemAction.setId(22L);
        systemAction.setName("actionTest");

        SystemPermission permission2 = new PermissionEntity(systemPermission);

        assertEquals(permission2.getId(), systemPermission.getId());
        assertEquals(permission2.getActionId(), systemPermission.getActionId());
    }

}