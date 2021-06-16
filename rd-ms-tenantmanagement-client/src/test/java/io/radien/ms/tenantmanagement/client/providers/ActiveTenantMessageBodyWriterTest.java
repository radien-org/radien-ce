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
package io.radien.ms.tenantmanagement.client.providers;

import io.radien.ms.tenantmanagement.client.entities.ActiveTenant;
import io.radien.ms.tenantmanagement.client.services.ActiveTenantFactory;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Active Tenant Message Body Writer
 * {@link io.radien.ms.tenantmanagement.client.providers.ActiveTenantMessageBodyWriter}
 *
 * @author Bruno Gama
 */
public class ActiveTenantMessageBodyWriterTest extends TestCase {

    /**
     * Tests if the given object is writable
     */
    @Test
    public void testIsWriteable() {
        ActiveTenantMessageBodyWriter target = new ActiveTenantMessageBodyWriter();
        assertTrue(target.isWriteable(ActiveTenant.class,null,null,null));
    }

    /**
     * Tests the getSize method
     */
    @Test
    public void testGetSize() {
        ActiveTenantMessageBodyWriter target = new ActiveTenantMessageBodyWriter();
        assertEquals(0L,target.getSize(null,null,null,null,null));
    }

    /**
     * Tests the write method and if can write the given active tenant object
     * @throws IOException in case a issue while parsing the json
     */
    @Test
    public void testWriteTo() throws IOException {
        String result = "{\"" +
                "id\":null," +
                "\"userId\":2," +
                "\"tenantId\":2" +
                "}";
        ActiveTenantMessageBodyWriter target = new ActiveTenantMessageBodyWriter();
        ActiveTenant activeTenant = ActiveTenantFactory.create(2L, 2L);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        target.writeTo(activeTenant,null,null,null, null,null, baos);

        assertEquals(result,baos.toString());
    }
}