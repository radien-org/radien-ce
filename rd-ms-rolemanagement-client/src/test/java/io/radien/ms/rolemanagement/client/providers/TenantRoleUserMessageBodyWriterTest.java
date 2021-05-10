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
package io.radien.ms.rolemanagement.client.providers;

import io.radien.ms.rolemanagement.client.entities.TenantRoleUser;
import io.radien.ms.rolemanagement.client.services.TenantRoleUserFactory;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Newton Carvalho
 */
public class TenantRoleUserMessageBodyWriterTest {

    @Test
    public void testIsWriteable() {
        TenantRoleUserMessageBodyWriter target = new TenantRoleUserMessageBodyWriter();
        assertTrue(target.isWriteable(TenantRoleUser.class,null,null,null));
    }

    @Test
    public void testGetSize() {
        TenantRoleUserMessageBodyWriter target = new TenantRoleUserMessageBodyWriter();
        assertEquals(0L,target.getSize(null,null,null,null,null));
    }

    @Test
    public void testWriteTo() throws IOException {
        String result = "{\"id\":44," +
                "\"tenantRoleId\":56," +
                "\"userId\":68," +
                "\"createUser\":33," +
                "\"lastUpdateUser\":null}";
        TenantRoleUserMessageBodyWriter target = new TenantRoleUserMessageBodyWriter();
        TenantRoleUser tenantRole = TenantRoleUserFactory.create(56L,68L, 33L);
        tenantRole.setId(44L);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        target.writeTo(tenantRole,null,null,null, null,null, baos);
        assertEquals(result,baos.toString().substring(0, baos.toString().indexOf("createDate")-2).trim() + "}");
    }
}