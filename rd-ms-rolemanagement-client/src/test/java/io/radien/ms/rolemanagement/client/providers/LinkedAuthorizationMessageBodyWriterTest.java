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

import io.radien.ms.rolemanagement.client.entities.LinkedAuthorization;
import io.radien.ms.rolemanagement.client.services.LinkedAuthorizationFactory;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.ByteArrayOutputStream;

/**
 * @author Bruno Gama
 */
public class LinkedAuthorizationMessageBodyWriterTest extends TestCase {

    @Test
    public void testGetSize() {
        LinkedAuthorizationMessageBodyWriter target = new LinkedAuthorizationMessageBodyWriter();
        assertEquals(0L,target.getSize(null,null,null,null,null));
    }

    @Test
    public void testIsWriteable() {
        LinkedAuthorizationMessageBodyWriter target = new LinkedAuthorizationMessageBodyWriter();
        assertTrue(target.isWriteable(LinkedAuthorization.class,null,null,null));
    }

    @Test
    public void testWriteTo() {
        String result = "{\"" +
                "id\":null," +
                "\"tenantId\":2," +
                "\"permissionId\":2," +
                "\"roleId\":2," +
                "\"userId\":2," +
                "\"createUser\":2," +
                "\"lastUpdateUser\":null" +
                "}";

        LinkedAuthorizationMessageBodyWriter target = new LinkedAuthorizationMessageBodyWriter();

        LinkedAuthorization linkedAuthorization = LinkedAuthorizationFactory.create(2L, 2L, 2L, 2L, 2L);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        target.writeTo(linkedAuthorization,null,null,null, null,null, baos);

        assertEquals(result,baos.toString());
    }
}