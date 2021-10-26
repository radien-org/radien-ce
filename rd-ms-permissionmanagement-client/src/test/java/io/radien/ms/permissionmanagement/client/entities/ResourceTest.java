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
package io.radien.ms.permissionmanagement.client.entities;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class ResourceTest {

    private Resource resource = new Resource();
    private Date terminationDate = new Date();

    public ResourceTest() {
        resource.setId(2L);
        resource.setName("resource-test");
    }

    @Test
    public void getId() {
        assertSame(2L, resource.getId());
    }

    @Test
    public void setId() {
        resource.setId(3L);
        assertSame(3L, resource.getId());
    }

    @Test
    public void getName() {
        assertEquals("resource-test", resource.getName());
    }

    @Test
    public void setName() {
        resource.setName("resource-test2");
        assertEquals("resource-test2", resource.getName());
    }


    @Test
    public void testConstructor() {
        Resource original = new Resource();
        original.setId(11L);
        original.setName("original");

        Resource newResource = new Resource(original);
        assertNotNull(newResource.getId());
        assertNotNull(newResource.getName());

        assertEquals(newResource.getId(), original.getId());
        assertEquals(newResource.getName(), original.getName());
    }
}
