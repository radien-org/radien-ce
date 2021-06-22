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
package io.radien.ms.tenantmanagement.entities;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Active Tenant test class {@link io.radien.ms.tenantmanagement.entities.ActiveTenant}
 *
 * @author Bruno Gama
 */
public class ActiveTenantTest {
    ActiveTenant activeTenant;
    ActiveTenant activeTenant1;

    /**
     * Active tenant entity constructor test
     */
    public ActiveTenantTest(){
        activeTenant1 = new ActiveTenant(new io.radien.ms.tenantmanagement.client.entities.ActiveTenant());
        activeTenant = new ActiveTenant();
        activeTenant.setUserId(1L);
        activeTenant.setTenantId(1L);
        activeTenant.setId(1L);

        activeTenant1.setUserId(2L);
        activeTenant1.setTenantId(2L);
        activeTenant1.setId(2L);
    }

    /**
     * Active tenant entity id getter test
     */
    @Test
    public void getId() {
        assertEquals((Long) 1L, activeTenant.getId());
        assertEquals((Long) 2L, activeTenant1.getId());
    }

    /**
     * Active tenant entity user id getter test
     */
    @Test
    public void getUserId() {
        assertEquals((Long) 1L, activeTenant.getUserId());
        assertEquals((Long) 2L, activeTenant1.getUserId());
    }

    /**
     * Active tenant entity tenant id getter test
     */
    @Test
    public void getTenantId() {
        assertEquals((Long) 1L, activeTenant.getTenantId());
        assertEquals((Long) 2L, activeTenant1.getTenantId());
    }

    /**
     * Active tenant entity constructor plus getter test
     */
    @Test
    public void testTenantTest() {
        io.radien.ms.tenantmanagement.client.entities.ActiveTenant t =
                new io.radien.ms.tenantmanagement.client.entities.ActiveTenant(2L, 2L, 2L, null, false);

        assertNotNull(t.getId());
        assertNotNull(t.getUserId());
        assertNotNull(t.getTenantId());
    }
}