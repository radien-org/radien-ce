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
import junit.framework.TestCase;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Active Tenant Message Body Reader test
 * {@link io.radien.ms.tenantmanagement.client.providers.ActiveTenantMessageBodyReader}
 *
 * @author Bruno Gama
 */
public class ActiveTenantMessageBodyReaderTest extends TestCase {

    /**
     * Tests if the object is readable
     */
    @Test
    public void testIsReadable() {
        ActiveTenantMessageBodyReader target = new ActiveTenantMessageBodyReader();
        assertTrue(target.isReadable(ActiveTenant.class, null, null, null));
    }

    /**
     * Test to read the given object to map him after
     */
    @Test
    public void testReadFrom() {
        String result = "{\"" +
                "id\":null," +
                "\"userId\": 2," +
                "\"tenantId\": 2" +
                "}";
        ActiveTenantMessageBodyReader target = new ActiveTenantMessageBodyReader();
        InputStream in = new ByteArrayInputStream(result.getBytes());
        ActiveTenant activeTenant = target.readFrom(null,null,null,null,null, in);
        assertNull(activeTenant.getId());
        assertEquals((Long) 2L,activeTenant.getUserId());
        assertEquals((Long) 2L,activeTenant.getTenantId());
    }

    /**
     * Tests the read method but with the throw of the exception
     */
    @Test
    public void testReadFromException() {
        String result = "{\"" +
                "id\":null," +
                "\"userId\": 2," +
                "\"tenantId\": 2" +
                "}";
        ActiveTenantMessageBodyReader target = new ActiveTenantMessageBodyReader();
        InputStream in = new ByteArrayInputStream(result.getBytes());

        ActiveTenant activeTenant = target.readFrom(null,null,null,null,null, in);
        assertNotNull(activeTenant);
    }
}