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
package io.radien.ms.permissionmanagement.util;

import io.radien.ms.permissionmanagement.model.Permission;
import io.radien.ms.permissionmanagement.legacy.PermissionFactory;
import junit.framework.TestCase;
import org.junit.Test;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;

/**
 * @author Nuno Santana
 * @author Bruno Gama
 */
public class PermissionModelMapperTest extends TestCase {

    @Test
    public void testMapInputStream() {
        String example = "{\n" +
                "\"id\": 28,\n" +
                "\"name\": \"b\"\n" +
                "}";
        InputStream in = new ByteArrayInputStream(example.getBytes());
        Permission p = PermissionModelMapper.map(in);
        assertEquals("b",p.getName());
        assertEquals((Long) 28L, p.getId());

    }

    @Test
    public void testMapJsonObject() {
        String name = "aa";
        Long createdUser = 2L;
        Permission p = PermissionFactory.create(name, createdUser);
        JsonObject jsonObject = PermissionModelMapper.map(p);

        assertEquals(p.getName(),jsonObject.getString("name"));

    }

    @Test
    public void testMapList() {
        String firstName = "aa";
        Long createdUser = 2L;
        Permission p = PermissionFactory.create(firstName, createdUser);
        JsonArray jsonArray = PermissionModelMapper.map(Collections.singletonList(p));
        assertEquals(1,jsonArray.size());
        JsonObject jsonObject = jsonArray.getJsonObject(0);

        assertEquals(p.getName(),jsonObject.getString("name"));
    }
}
