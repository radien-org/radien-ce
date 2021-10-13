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

import io.radien.ms.rolemanagement.client.entities.TenantRoleUser;
import org.junit.Test;

import javax.ws.rs.WebApplicationException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.Assert.*;

/**
 * @author Newton Carvalho
 */
public class TenantRoleUserMessageBodyReaderTest {

    @Test
    public void testIsReadable() {
        TenantRoleUserMessageBodyReader target = new TenantRoleUserMessageBodyReader();
        assertTrue(target.isReadable(TenantRoleUser.class, null, null, null));
    }

    @Test
    public void testReadFrom() {
        String result = "{\"" +
                "id\":23," +
                "\"tenantRoleId\":67," +
                "\"userId\":45," +
                "\"createUser\":null," +
                "\"lastUpdateUser\":null" +
                "}";
        TenantRoleUserMessageBodyReader target = new TenantRoleUserMessageBodyReader();
        InputStream in = new ByteArrayInputStream(result.getBytes());
        TenantRoleUser tenantRoleUser = target.readFrom(null,null,null,null,null, in);
        assertEquals((Long) 23L, tenantRoleUser.getId());
        assertEquals((Long) 45L, tenantRoleUser.getUserId());
        assertEquals((Long) 67L, tenantRoleUser.getTenantRoleId());
        assertNull(tenantRoleUser.getCreateUser());
        assertNull(tenantRoleUser.getLastUpdateUser());
    }

    @Test(expected = WebApplicationException.class)
    public void testReadFromException() {
        String result = "{\"id\":null, \"tenantRoleId\": 11, \"userId\": \"a\" \"}";
        TenantRoleUserMessageBodyReader target = new TenantRoleUserMessageBodyReader();
        InputStream in = new ByteArrayInputStream(result.getBytes());
        TenantRoleUser tenantRolePermission = target.readFrom(null,null,null,null,null, in);
    }
}