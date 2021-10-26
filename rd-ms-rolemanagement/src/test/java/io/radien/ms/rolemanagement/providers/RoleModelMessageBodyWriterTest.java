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
package io.radien.ms.rolemanagement.providers;

import io.radien.ms.rolemanagement.entities.RoleEntity;
import io.radien.ms.rolemanagement.factory.RoleFactory;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;

/**
 * @author Bruno Gama
 */
public class RoleModelMessageBodyWriterTest {

    @Test
    public void testIsWriteable() {
        RoleModelMessageBodyWriter target = new RoleModelMessageBodyWriter();
        assertTrue(target.isWriteable(RoleEntity.class,null,null,null));
    }

    @Test
    public void testGetSize() {
        RoleModelMessageBodyWriter target = new RoleModelMessageBodyWriter();
        assertEquals(0L,target.getSize(null,null,null,null,null));
    }

    @Test
    public void testWriteTo() {
        String result = "{\"" +
                "id\":null," +
                "\"name\":\"nameValue\"," +
                "\"description\":\"descriptionValue\"," +
                "\"createUser\":2," +
                "\"lastUpdateUser\":null" +
                "}";
        RoleModelMessageBodyWriter target = new RoleModelMessageBodyWriter();
        RoleEntity user = RoleFactory.create("nameValue","descriptionValue",2L);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        target.writeTo(user,null,null,null, null,null, baos);

        assertEquals(result,baos.toString());
    }
}