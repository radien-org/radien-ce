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
package io.radien.ms.rolemanagement.entities;

import io.radien.ms.rolemanagement.factory.LinkedAuthorizationFactory;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.Date;

/**
 * @author Bruno Gama
 */
public class LinkedAuthorizationTest extends TestCase {

    LinkedAuthorization linkedAuthorization;
    Date now = new Date();

    public LinkedAuthorizationTest() {
        linkedAuthorization = LinkedAuthorizationFactory.create(2L, 2L, 2L, 2L);
        linkedAuthorization.setId(1L);
        linkedAuthorization.setCreateUser(3L);
        linkedAuthorization.setCreateDate(now);
        linkedAuthorization.setLastUpdate(now);
        linkedAuthorization.setLastUpdateUser(3L);
    }

    @Test
    public void testConstructor() {
        LinkedAuthorization newAuthorization = new LinkedAuthorization(io.radien.ms.rolemanagement.client.services.LinkedAuthorizationFactory.create(3L, 3L, 3L, 3L));

        assertNull(newAuthorization.getId());
        assertEquals((Long) 3L, newAuthorization.getTenantId());
        assertEquals((Long) 3L, newAuthorization.getPermissionId());
        assertEquals((Long) 3L, newAuthorization.getRoleId());
    }

    @Test
    public void testGetId() {
        assertNotNull(linkedAuthorization.getId());
        assertEquals((Long) 1L, linkedAuthorization.getId());
    }

    @Test
    public void testSetId() {
        linkedAuthorization.setId(4L);

        assertNotNull(linkedAuthorization.getId());
        assertEquals((Long) 4L, linkedAuthorization.getId());
    }

    @Test
    public void testGetTenantId() {
        assertNotNull(linkedAuthorization.getTenantId());
        assertEquals((Long) 2L, linkedAuthorization.getTenantId());
    }

    @Test
    public void testSetTenantId() {
        linkedAuthorization.setTenantId(4L);

        assertNotNull(linkedAuthorization.getTenantId());
        assertEquals((Long) 4L, linkedAuthorization.getTenantId());
    }

    @Test
    public void testGetPermissionId() {
        assertNotNull(linkedAuthorization.getPermissionId());
        assertEquals((Long) 2L, linkedAuthorization.getPermissionId());
    }

    @Test
    public void testSetPermissionId() {
        linkedAuthorization.setPermissionId(4L);

        assertNotNull(linkedAuthorization.getPermissionId());
        assertEquals((Long) 4L, linkedAuthorization.getPermissionId());
    }

    @Test
    public void testGetRoleId() {
        assertNotNull(linkedAuthorization.getRoleId());
        assertEquals((Long) 2L, linkedAuthorization.getRoleId());
    }

    @Test
    public void testSetRoleId() {
        linkedAuthorization.setRoleId(4L);

        assertNotNull(linkedAuthorization.getRoleId());
        assertEquals((Long) 4L, linkedAuthorization.getRoleId());
    }

    @Test
    public void testGetCreateDate() {
        assertNotNull(linkedAuthorization.getCreateDate());
        assertEquals(now, linkedAuthorization.getCreateDate());
    }

    @Test
    public void testSetCreateDate() {
        Date dateRefactored = new Date();
        linkedAuthorization.setCreateDate(dateRefactored);

        assertNotNull(linkedAuthorization.getCreateDate());
        assertEquals(dateRefactored, linkedAuthorization.getCreateDate());
    }

    @Test
    public void testGetLastUpdate() {
        assertNotNull(linkedAuthorization.getLastUpdate());
        assertEquals(now, linkedAuthorization.getLastUpdate());
    }

    @Test
    public void testSetLastUpdate() {
        Date dateRefactored = new Date();
        linkedAuthorization.setLastUpdate(dateRefactored);

        assertNotNull(linkedAuthorization.getLastUpdate());
        assertEquals(dateRefactored, linkedAuthorization.getLastUpdate());
    }

    @Test
    public void testGetCreateUser() {
        assertNotNull(linkedAuthorization.getCreateUser());
        assertEquals((Long) 3L, linkedAuthorization.getCreateUser());
    }

    @Test
    public void testSetCreateUser() {
        linkedAuthorization.setCreateUser(4L);

        assertNotNull(linkedAuthorization.getCreateUser());
        assertEquals((Long) 4L, linkedAuthorization.getCreateUser());
    }

    @Test
    public void testGetLastUpdateUser() {
        assertNotNull(linkedAuthorization.getLastUpdateUser());
        assertEquals((Long) 3L, linkedAuthorization.getLastUpdateUser());
    }

    @Test
    public void testSetLastUpdateUser() {
        linkedAuthorization.setLastUpdateUser(4L);

        assertNotNull(linkedAuthorization.getLastUpdateUser());
        assertEquals((Long) 4L, linkedAuthorization.getLastUpdateUser());
    }
}