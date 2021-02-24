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
package io.radien.ms.permissionmanagement.client.entities;

import io.radien.ms.permissionmanagement.client.services.ResourceFactory;
import io.radien.ms.permissionmanagement.client.util.ResourceModelMapper;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ResourceFactoryTest {

    Resource resource;
    JsonObject json;

    /**
     * Constructor class method were we are going to create the JSON and the permission for
     * testing purposes.
     */
    public ResourceFactoryTest() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.addNull("id");
        builder.add("name", "resource-bbb");
        builder.add("createUser", 2L);
        builder.addNull("lastUpdateUser");
        json = builder.build();
        resource = ResourceFactory.create("resource-bbb", 2L);
    }

    /**
     * Test method to validate the creation of a Permission using a Json
     */
    @Test
    public void create() {
        Resource newtResource = ResourceFactory.create("permission-bbb", 2L);

        assertEquals(resource.getId(), newtResource.getId());
        assertEquals(resource.getCreateUser(), newtResource.getCreateUser());
        assertEquals(resource.getLastUpdateUser(), newtResource.getLastUpdateUser());
    }

    /**
     * Test method to validate the conversion of a Permission using a Json
     */
    @Test
    public void convert() {
        Resource newAct = ResourceFactory.convert(json);

        assertEquals(resource.getId(), newAct.getId());
        assertEquals(resource.getCreateUser(), newAct.getCreateUser());
        assertEquals(resource.getLastUpdateUser(), newAct.getLastUpdateUser());
        assertEquals(resource.getName(), newAct.getName());
    }

    @Test
    public void testConvertToJsonObject() {
        JsonObject constructedNewJson = ResourceFactory.convertToJsonObject(resource);
        assertEquals(json.toString(), constructedNewJson.toString());
    }

    @Test
    public void testConvertingArray() {

        List<Resource> originalCollection = new ArrayList<>();
        originalCollection.add(ResourceFactory.create("perm1", 0L));
        originalCollection.add(ResourceFactory.create("perm2", 0L));
        originalCollection.add(ResourceFactory.create("perm3", 0L));

        JsonArray array = ResourceModelMapper.map(originalCollection);

        List<Resource> rebuild = ResourceFactory.convert(array);
        assertNotNull(rebuild);
        assertFalse(rebuild.isEmpty());
        assertEquals(originalCollection.size(), rebuild.size());
    }
}