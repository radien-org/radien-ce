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
package io.radien.ms.rolemanagement.util;

import io.radien.ms.rolemanagement.entities.RoleEntity;
import io.radien.ms.rolemanagement.factory.RoleFactory;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;;
import static org.junit.jupiter.api.Assertions.assertNull;

public class RoleModelMapperTest {

    @Test
    public void testMapRole() {
        RoleEntity role = RoleFactory.create("nameValue", "descriptionValue", 2L);

        JsonObject jsonObject = RoleModelMapper.map(role);
        assertEquals(role.getName(),jsonObject.getString("name"));
        assertEquals(role.getDescription(),jsonObject.getString("description"));
    }

    @Test
    public void testMapList() {
        RoleEntity role = RoleFactory.create("nameValue", "descriptionValue", 2L);

        JsonArray jsonArray = RoleModelMapper.map(Collections.singletonList(role));
        assertEquals(1,jsonArray.size());
        JsonObject jsonObject = jsonArray.getJsonObject(0);

        assertEquals(role.getName(),jsonObject.getString("name"));
        assertEquals(role.getDescription(),jsonObject.getString("description"));
    }

    @Test
    public void testMapInputStream() {
        String result = "{\"" +
                "id\":null," +
                "\"name\":\"nameValue\"," +
                "\"description\":\"descriptionValue\"" +
                "}";

        InputStream in = new ByteArrayInputStream(result.getBytes());
        RoleEntity role = RoleModelMapper.map(in);

        assertNull(role.getId());
        assertEquals("nameValue", role.getName());
        assertEquals("descriptionValue", role.getDescription());
    }
}