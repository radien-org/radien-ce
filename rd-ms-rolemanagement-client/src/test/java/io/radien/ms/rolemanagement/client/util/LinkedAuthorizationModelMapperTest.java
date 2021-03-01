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
package io.radien.ms.rolemanagement.client.util;

import io.radien.ms.rolemanagement.client.entities.LinkedAuthorization;
import io.radien.ms.rolemanagement.client.services.LinkedAuthorizationFactory;
import junit.framework.TestCase;
import org.junit.Test;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;

/**
 * @author Bruno Gama
 */
public class LinkedAuthorizationModelMapperTest extends TestCase {

    @Test
    public void testMapRole() {
        LinkedAuthorization linkedAuthorization = LinkedAuthorizationFactory.create(2L, 2L, 2L, 2L, 2L);

        JsonObject jsonObject = LinkedAuthorizationModelMapper.map(linkedAuthorization);
        assertEquals(linkedAuthorization.getTenantId().toString(),jsonObject.get("tenantId").toString());
        assertEquals(linkedAuthorization.getPermissionId().toString(),jsonObject.get("permissionId").toString());
        assertEquals(linkedAuthorization.getRoleId().toString(),jsonObject.get("roleId").toString());
    }

    @Test
    public void testMapList() {
        LinkedAuthorization linkedAuthorization = LinkedAuthorizationFactory.create(2L, 2L, 2L, 2L, 2L);

        JsonArray jsonArray = LinkedAuthorizationModelMapper.map(Collections.singletonList(linkedAuthorization));
        assertEquals(1,jsonArray.size());
        JsonObject jsonObject = jsonArray.getJsonObject(0);

        assertEquals(linkedAuthorization.getTenantId().toString(),jsonObject.get("tenantId").toString());
        assertEquals(linkedAuthorization.getPermissionId().toString(),jsonObject.get("permissionId").toString());
        assertEquals(linkedAuthorization.getRoleId().toString(),jsonObject.get("roleId").toString());
    }

    @Test
    public void testMapInputStream() {
        String result = "{\"" +
                "id\":null," +
                "\"tenantId\":2," +
                "\"permissionId\":2," +
                "\"roleId\":2," +
                "\"createdUser\":2" +
                "}";

        InputStream in = new ByteArrayInputStream(result.getBytes());
        LinkedAuthorization linkedAuthorization = LinkedAuthorizationModelMapper.map(in);

        assertNull(linkedAuthorization.getId());
        assertEquals((Long) 2L,linkedAuthorization.getTenantId());
        assertEquals((Long) 2L,linkedAuthorization.getPermissionId());
        assertEquals((Long) 2L,linkedAuthorization.getRoleId());
    }
}
