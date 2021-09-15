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
package io.radien.ms.tenantmanagement.client.providers;

import io.radien.ms.tenantmanagement.client.entities.Tenant;
import io.radien.ms.tenantmanagement.client.entities.TenantType;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author Bruno Gama
 */
public class TenantMessageBodyReaderTest extends TestCase {

    @Test
    public void testIsReadable() {
        TenantMessageBodyReader target = new TenantMessageBodyReader();
        assertTrue(target.isReadable(Tenant.class, null, null, null));
    }

    @Test
    public void testReadFrom() {
        String result = "{\"" +
                "id\":null," +
                "\"name\":\"name\"," +
                "\"tenantKey\":\"tenantKey\"," +
                "\"createUser\":null," +
                "\"lastUpdateUser\":null," +
                "\"tenantType\":\"root\"" +
                "}";
        TenantMessageBodyReader target = new TenantMessageBodyReader();
        InputStream in = new ByteArrayInputStream(result.getBytes());
        Tenant tenant = target.readFrom(null,null,null,null,null, in);
        assertNull(tenant.getId());
        assertEquals("name",tenant.getName());
        assertEquals("tenantKey",tenant.getTenantKey());
        assertNull(tenant.getCreateUser());
        assertNull(tenant.getLastUpdateUser());
        assertEquals(TenantType.ROOT_TENANT.getDescription(),tenant.getTenantType());
    }

    @Test
    public void testReadFromException() {
        String result = "{\"" +
                "id\":null," +
                "\"name\":\"name\"," +
                "\"tenantKey\":\"tenantKey\"," +
                "\"createDate\": \"a\",\n" +
                "\"createUser\":null," +
                "\"lastUpdateUser\":null," +
                "\"tenantType\":\"root\"" +
                "}";
        boolean success = false;
        try {
            TenantMessageBodyReader target = new TenantMessageBodyReader();
            InputStream in = new ByteArrayInputStream(result.getBytes());

            Tenant tenant = target.readFrom(null, null, null, null, null, in);
        } catch (Exception e) {
            success = true;
        }
        assertTrue(success);
    }
}