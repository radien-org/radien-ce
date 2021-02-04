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
package io.radien.ms.permissionmanagement.entities;

import io.radien.api.model.permission.SystemAction;
import io.radien.api.model.permission.SystemPermission;
import io.radien.ms.permissionmanagement.client.entities.ActionType;
import io.radien.ms.permissionmanagement.legacy.ActionFactory;
import io.radien.ms.permissionmanagement.legacy.PermissionFactory;
import io.radien.ms.permissionmanagement.model.Action;
import io.radien.ms.permissionmanagement.model.Permission;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class PermissionTest extends TestCase {

    Permission permission;
    Action action;
    private final Date terminationDate = new Date();

    public PermissionTest() {
        action = ActionFactory.create("Create Contract", ActionType.EXECUTION, 3l);
        action.setId(33L);
        permission = PermissionFactory.create("testPermissionName", action, 3L);
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
        assertNotNull(permission.getAction());
        assertEquals(permission.getAction().getName(), "Create Contract");
        assertEquals(permission.getAction().getType(), ActionType.EXECUTION);
    }

    @Test
    public void testSetAction() {
        SystemAction newAction = ActionFactory.create("Update Contract", ActionType.READ, null);
        permission.setAction(newAction);
        assertNotNull(permission.getAction());
        assertEquals(permission.getAction().getName(), "Update Contract");
        assertEquals(permission.getAction().getType(), ActionType.READ);
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
        systemAction.setType(ActionType.READ);
        systemPermission.setAction(systemAction);

        SystemPermission permission2 = new Permission(systemPermission);

        assertEquals(permission2.getId(), systemPermission.getId());
        assertEquals(permission2.getAction().getId(), systemPermission.getAction().getId());
        assertEquals(permission2.getAction().getName(), systemPermission.getAction().getName());
        assertEquals(permission2.getAction().getType(), systemPermission.getAction().getType());
    }

}