/*
 * Copyright (c) 2021-present radien GmbH. All rights reserved.
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

import io.radien.ms.rolemanagement.client.entities.TenantRole;
import org.junit.Test;

import javax.ws.rs.WebApplicationException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.Assert.*;

/**
 * @author Newton Carvalho
 */
public class TenantRoleMessageBodyReaderTest {

    @Test
    public void testIsReadable() {
        TenantRoleMessageBodyReader target = new TenantRoleMessageBodyReader();
        assertTrue(target.isReadable(TenantRole.class, null, null, null));
    }

    @Test
    public void testReadFrom() {
        String result = "{\"" +
                "id\":22," +
                "\"roleId\":44," +
                "\"tenantId\":66," +
                "\"createUser\":null," +
                "\"lastUpdateUser\":null" +
                "}";
        TenantRoleMessageBodyReader target = new TenantRoleMessageBodyReader();
        InputStream in = new ByteArrayInputStream(result.getBytes());
        TenantRole tenantRole = target.readFrom(null,null,null,null,null, in);
        assertEquals((Long) 22L, tenantRole.getId());
        assertEquals((Long) 44L, tenantRole.getRoleId());
        assertEquals((Long) 66L, tenantRole.getTenantId());
        assertNull(tenantRole.getCreateUser());
        assertNull(tenantRole.getLastUpdateUser());
    }

    @Test(expected = WebApplicationException.class)
    public void testReadFromException() {
        String result = "{\"" +
                "id\":null," +
                "\"roleId\":\"22\"," +
                "\"tenantId\":\"44\"," +
                "\"createUser\":null," +
                "\"lastUpdateUser\":null" +
                "}";
        TenantRoleMessageBodyReader target = new TenantRoleMessageBodyReader();
        InputStream in = new ByteArrayInputStream(result.getBytes());
        TenantRole tenantRole = target.readFrom(null,null,null,null,null, in);
    }
}