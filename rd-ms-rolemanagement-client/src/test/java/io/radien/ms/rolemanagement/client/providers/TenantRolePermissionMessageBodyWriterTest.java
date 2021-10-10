/*
 * Copyright (c) 2021-present radien GmbH & its legal owners. All rights reserved.
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

import io.radien.ms.rolemanagement.client.entities.TenantRolePermission;
import io.radien.ms.rolemanagement.client.services.TenantRolePermissionFactory;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Newton Carvalho
 */
public class TenantRolePermissionMessageBodyWriterTest {

    @Test
    public void testIsWriteable() {
        TenantRolePermissionMessageBodyWriter target = new TenantRolePermissionMessageBodyWriter();
        assertTrue(target.isWriteable(TenantRolePermission.class,null,null,null));
    }

    @Test
    public void testGetSize() {
        TenantRolePermissionMessageBodyWriter target = new TenantRolePermissionMessageBodyWriter();
        assertEquals(0L,target.getSize(null,null,null,null,null));
    }

    @Test
    public void testWriteTo() throws IOException {
        String result = "{\"id\":44," +
                "\"tenantRoleId\":56," +
                "\"permissionId\":68," +
                "\"createUser\":33," +
                "\"lastUpdateUser\":null}";
        TenantRolePermissionMessageBodyWriter target = new TenantRolePermissionMessageBodyWriter();
        TenantRolePermission tenantRole = TenantRolePermissionFactory.create(56L,68L, 33L);
        tenantRole.setId(44L);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        target.writeTo(tenantRole,null,null,null, null,null, baos);
        assertEquals(result,baos.toString().substring(0, baos.toString().indexOf("createDate")-2).trim() + "}");
    }
}