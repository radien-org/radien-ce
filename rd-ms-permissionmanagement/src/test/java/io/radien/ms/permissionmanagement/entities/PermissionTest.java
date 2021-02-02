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

import io.radien.ms.permissionmanagement.legacy.PermissionFactory;
import io.radien.ms.permissionmanagement.model.Permission;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.Date;

public class PermissionTest extends TestCase {

    Permission user;
    private final Date terminationDate = new Date();

    public PermissionTest() {
        user = PermissionFactory.create("testPermissionName", 3L);
        user.setId(2L);
    }

    @Test
    public void testGetId() {
        assertNotNull(user.getId());
        assertEquals((Long) 2L, user.getId());
    }

    @Test
    public void testSetId() {
        user.setId(4L);

        assertNotNull(user.getId());
        assertEquals((Long) 4L, user.getId());
    }

    @Test
    public void testGetName() {
        assertNotNull(user.getName());
        assertEquals("testPermissionName", user.getName());
    }

    @Test
    public void testSetName() {
        user.setName("permTestRefactored");
        assertNotNull(user.getName());
        assertEquals("permTestRefactored", user.getName());
    }

}