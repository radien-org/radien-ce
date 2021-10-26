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
package io.radien.ms.tenantmanagement.entities;

import io.radien.ms.tenantmanagement.client.entities.TenantType;
import org.junit.Test;

import static org.junit.Assert.*;

public class TenantTest {
    TenantEntity tenant;
    TenantEntity tenant2;

    public TenantTest(){
        tenant2 = new TenantEntity(new io.radien.ms.tenantmanagement.client.entities.Tenant());
        tenant = new TenantEntity();
        tenant.setName("a");
        tenant.setId(1L);
        tenant2.setName("b");
        tenant2.setId(2L);
    }

    @Test
    public void getId() {
        assertEquals((Long) 1L,tenant.getId());
        assertEquals((Long) 2L,tenant2.getId());
    }

    @Test
    public void getName() {
        assertEquals("a",tenant.getName());
        assertEquals("b",tenant2.getName());
    }

    @Test
    public void testTenantTest() {
        io.radien.ms.tenantmanagement.client.entities.Tenant t = new io.radien.ms.tenantmanagement.client.entities.Tenant(1L, "name", "tenantKey", TenantType.ROOT, null, null, "address", "zip", "city", "country", 123L, "email", 1L, 1L);

        assertNull(t.getClientAddress());
        assertNull(t.getClientZipCode());
        assertNull(t.getClientCity());
        assertNull(t.getClientCountry());
    }
}