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

package io.radien.ms.tenantmanagement.client.entities;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;

/**
 * Active Tenant test class {@link io.radien.ms.tenantmanagement.client.entities.ActiveTenant}
 *
 * @author Bruno Gama
 */
public class ActiveTenantTest {

    ActiveTenant activeTenant;

    /**
     * Active tenant entity constructor test
     */
    public ActiveTenantTest(){
        activeTenant = new ActiveTenant(2L, 2L, 2L, "test", true);
    }

    /**
     * Test Method to validate the three existent constructors
     */
    @Test
    public void testActiveTenantConstructor(){
        ActiveTenant activeTenant2 = new ActiveTenant(2L, 2L, 2L, "test", true);

        assertEquals((Long) 2L, activeTenant2.getId());
        assertEquals((Long) 2L, activeTenant2.getUserId());
        assertEquals((Long) 2L, activeTenant2.getTenantId());
        assertEquals("test", activeTenant2.getTenantName());
        assertTrue(activeTenant2.getIsTenantActive());

        ActiveTenant activeTenant3 = new ActiveTenant(activeTenant);

        assertEquals((Long) 2L, activeTenant3.getId());
        assertEquals((Long) 2L, activeTenant3.getUserId());
        assertEquals((Long) 2L, activeTenant3.getTenantId());
        assertEquals("test", activeTenant3.getTenantName());
        assertTrue(activeTenant3.getIsTenantActive());

        ActiveTenant activeTenant4 = new ActiveTenant();

        assertNull(activeTenant4.getId());
        assertNull(activeTenant4.getUserId());
        assertNull(activeTenant4.getTenantId());
        assertNull(activeTenant4.getTenantName());
    }
}