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
package io.radien.ms.rolemanagement.client.services;

import io.radien.api.entity.Page;
import io.radien.ms.rolemanagement.client.entities.Role;
import io.radien.ms.rolemanagement.client.util.RoleModelMapper;
import junit.framework.TestCase;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @author Bruno Gama
 */
public class RoleFactoryTest extends TestCase {

    Role role = new Role();
    JsonObject json;

    public RoleFactoryTest() throws ParseException {
        role.setName("nameValue");
        role.setDescription("descriptionValue");
        role.setCreateUser(2L);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        role.setTerminationDate(format.parse("2021-04-30T12:00:00"));
    }

    @Test
    public void testCreate() {
        RoleFactory roleFactory = new RoleFactory();
        Role newRoleConstructed = roleFactory.create("nameValue", "descriptionValue", 2L);

        assertEquals(role.getName(), newRoleConstructed.getName());
        assertEquals(role.getDescription(), newRoleConstructed.getDescription());
        assertEquals(role.getCreateUser(), newRoleConstructed.getCreateUser());
    }

    @Test
    public void testConvert() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.addNull("id");
        builder.add("name", "nameValue");
        builder.add("description", "descriptionValue");
        builder.add("createUser", 2L);
        builder.add("terminationDate", "2021-04-30T12:00:00+0100");

        json = builder.build();
        Role newJsonRole = RoleFactory.convert(json);

        assertEquals(role.getName(), newJsonRole.getName());
        assertEquals(role.getDescription(), newJsonRole.getDescription());
        assertEquals(role.getCreateUser(), newJsonRole.getCreateUser());
        assertEquals(role.getTerminationDate(), newJsonRole.getTerminationDate());
    }

    @Test
    public void testConvertToJsonObject() {
        JsonObject constructedNewJson = RoleFactory.convertToJsonObject(role);

        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.addNull("id");
        builder.add("name", "nameValue");
        builder.add("description", "descriptionValue");
        builder.add("createUser", 2L);
        builder.addNull("lastUpdateUser");
        builder.add("terminationDate", "2021-04-30T12:00:00");

        json = builder.build();

        assertEquals(json.toString(), constructedNewJson.toString());
    }

    @Test
    public void testConvertWithFailure() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.addNull("id");
        builder.add("name", "nameValue");
        builder.add("description", "descriptionValue");
        builder.add("createUser", 2L);
        builder.addNull("lastUpdateUser");
        builder.add("terminationDate", "Wed 2021-04-30");

        boolean foundException = false;
        try {
            RoleFactory.convert(builder.build());
        } catch (Exception e) {
            foundException = true;
        }
        assertTrue(foundException);
    }

    @Test
    public void testMapInputStreamToPage() {
        String example = "{\n" +
                "\"currentPage\": 0,\n" +
                "\"results\": [\n" +
                "{\n" +
                "\"name\": \"a\",\n" +
                "\"description\": \"test\"\n" +
                "}\n" +
                "],\n" +
                "\"totalPages\": 1,\n" +
                "\"totalResults\": 4\n" +
                "}";
        InputStream in = new ByteArrayInputStream(example.getBytes());
        Page<Role> role = RoleModelMapper.mapToPage(in);
        assertEquals(0, role.getCurrentPage());
        assertEquals(1, role.getTotalPages());
        assertEquals(4, role.getTotalResults());
    }
}
