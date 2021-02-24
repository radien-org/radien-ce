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

import io.radien.api.model.linked.authorization.SystemLinkedAuthorization;
import io.radien.ms.rolemanagement.client.entities.LinkedAuthorization;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author Bruno Gama
 */
public class LinkedAuthorizationMessageBodyReaderTest extends TestCase {

    @Test
    public void testIsReadable() {
        LinkedAuthorizationMessageBodyReader target = new LinkedAuthorizationMessageBodyReader();
        assertTrue(target.isReadable(LinkedAuthorization.class, null, null, null));
    }

    @Test
    public void testReadFrom() {
        String result = "{\"" +
                "id\":null," +
                "\"tenantId\":2," +
                "\"permissionId\":2," +
                "\"roleId\":2," +
                "\"createdUser\":2" +
                "}";

        LinkedAuthorizationMessageBodyReader target = new LinkedAuthorizationMessageBodyReader();
        InputStream in = new ByteArrayInputStream(result.getBytes());

        SystemLinkedAuthorization linkedAuthorization = target.readFrom(null, null, null, null, null, in);

        assertNull(linkedAuthorization.getId());
        assertEquals((Long) 2L, linkedAuthorization.getTenantId());
        assertEquals((Long) 2L, linkedAuthorization.getPermissionId());
        assertEquals((Long) 2L, linkedAuthorization.getRoleId());
    }
}