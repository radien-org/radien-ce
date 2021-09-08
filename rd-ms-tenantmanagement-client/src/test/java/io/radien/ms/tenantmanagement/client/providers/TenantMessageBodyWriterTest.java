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
import io.radien.ms.tenantmanagement.client.services.TenantFactory;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Bruno Gama
 */
public class TenantMessageBodyWriterTest extends TestCase {

    @Test
    public void testIsWriteable() {
        TenantMessageBodyWriter target = new TenantMessageBodyWriter();
        assertTrue(target.isWriteable(Tenant.class,null,null,null));
    }

    @Test
    public void testGetSize() {
        TenantMessageBodyWriter target = new TenantMessageBodyWriter();
        assertEquals(0L,target.getSize(null,null,null,null,null));
    }

    @Test
    public void testWriteTo() throws IOException {
        String result = "{\"id\":null," +
                "\"name\":\"name\"," +
                "\"tenantKey\":\"tenantKey\"," +
                "\"tenantType\":\"ROOT\"," +
                "\"tenantStart\":null," +
                "\"tenantEnd\":null," +
                "\"clientAddress\":null," +
                "\"clientZipCode\":null," +
                "\"clientCity\":null," +
                "\"clientCountry\":null," +
                "\"clientPhoneNumber\":null," +
                "\"clientEmail\":null," +
                "\"parentId\":null," +
                "\"clientId\":null," +
                "\"createUser\":null," +
                "\"lastUpdateUser\":null}";
        TenantMessageBodyWriter target = new TenantMessageBodyWriter();
        Tenant tenant = TenantFactory.create("name","tenantKey", TenantType.ROOT_TENANT.getName(),null, null, null, null, null, null, null, null, null, null, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        target.writeTo(tenant,null,null,null, null,null, baos);

        assertEquals(result,baos.toString().substring(0, baos.toString().indexOf("createDate")-2).trim() + "}");
    }
}