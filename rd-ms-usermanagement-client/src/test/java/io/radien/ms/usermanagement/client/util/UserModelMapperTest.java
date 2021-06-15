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
package io.radien.ms.usermanagement.client.util;

import io.radien.api.entity.Page;
import io.radien.ms.usermanagement.client.entities.User;
import io.radien.ms.usermanagement.client.services.UserFactory;
import junit.framework.TestCase;
import org.junit.Test;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;

public class UserModelMapperTest extends TestCase {

    @Test
    public void testMapInputStream() {
        String example = "{\n" +
                "\"enabled\": false,\n" +
                "\"firstname\": \"a\",\n" +
                "\"id\": 28,\n" +
                "\"lastname\": \"b\",\n" +
                "\"logon\": \"aa34433\",\n" +
                "\"userEmail\": \"aa234433@email.tt\"\n" +
                "}";
        InputStream in = new ByteArrayInputStream(example.getBytes());
        User user = UserModelMapper.map(in);
        assertEquals("a",user.getFirstname());
        assertEquals("b",user.getLastname());
        assertEquals(28L,(long)user.getId());
        assertFalse(user.isEnabled());
        assertEquals("aa34433",user.getLogon());
        assertEquals("aa234433@email.tt", user.getUserEmail());

    }

    @Test
    public void testMapJsonObject() {
        String firstName = "aa";
        String lastName = "bb";
        String logon = "logonAA";
        String sub = "uuidReallyUnique";
        String email = "a@b.pt";
        User user = UserFactory.create(firstName,lastName,logon,sub,email, null);
        JsonObject jsonObject = UserModelMapper.map(user);
        validateUserJsonObject(user,jsonObject);
    }

    @Test
    public void testMapList() {
        String firstName = "aa";
        String lastName = "bb";
        String logon = "logonAA";
        String sub = "uuidReallyUnique";
        String email = "a@b.pt";
        User user = UserFactory.create(firstName,lastName,logon,sub,email, null);
        JsonArray jsonArray = UserModelMapper.map(Collections.singletonList(user));
        assertEquals(1,jsonArray.size());
        JsonObject jsonObject = jsonArray.getJsonObject(0);
        validateUserJsonObject(user,jsonObject);
    }

    private void validateUserJsonObject(User user,JsonObject jsonObject){
        assertEquals(user.getFirstname(),jsonObject.getString("firstname"));
        assertEquals(user.getLastname(),jsonObject.getString("lastname"));
        assertEquals(user.getLogon(),jsonObject.getString("logon"));
        assertEquals(user.getSub(),jsonObject.getString("sub"));
        assertEquals(user.getUserEmail(),jsonObject.getString("userEmail"));
    }

    @Test
    public void testMapInputStreamToPage() {
        String example = "{\n" +
                "\"currentPage\": 0,\n" +
                "\"results\": [\n" +
                "{\n" +
                "\"name\": \"a\",\n" +
                "\"start\": \"2021-01-29T13:10:19.396\",\n" +
                "\"end\": \"2021-01-29T13:10:20.396\"\n" +
                "}\n" +
                "],\n" +
                "\"totalPages\": 1,\n" +
                "\"totalResults\": 4\n" +
                "}";
        InputStream in = new ByteArrayInputStream(example.getBytes());
        Page<User> user = UserModelMapper.mapToPage(in);
        assertEquals(0, user.getCurrentPage());
        assertEquals(1, user.getTotalPages());
        assertEquals(4, user.getTotalResults());
    }
}