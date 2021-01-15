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
package io.radien.ms.usermanagement.util;

import io.radien.ms.usermanagement.entities.User;
import io.radien.ms.usermanagement.legacy.UserFactory;
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
public class UserModelMapperTest extends TestCase {

    @Test
    public void testMapInputStream() {
        String example = "{\n" +
                "\"enabled\": false,\n" +
                "\"firstname\": \"a\",\n" +
                "\"id\": 28,\n" +
                "\"lastname\": \"b\",\n" +
                "\"logon\": \"aa34433\",\n" +
                "\"userEmail\": \"aa234433@email.tt\",\n" +
                "\"sub\": \"89c760fc-2962-4fe8-ad4d-02e02bc48593\"\n" +
                "}";
        InputStream in = new ByteArrayInputStream(example.getBytes());
        User user = UserModelMapper.map(in);
        assertEquals("a",user.getFirstname());
        assertEquals("b",user.getLastname());
        assertEquals((Long) 28L, user.getId());
        assertFalse(user.isEnabled());
        assertEquals("aa34433",user.getLogon());
        assertEquals("aa234433@email.tt", user.getUserEmail());
        assertEquals("89c760fc-2962-4fe8-ad4d-02e02bc48593", user.getSub());

    }

    @Test
    public void testMapJsonObject() {
        String firstName = "aa";
        String lastName = "bb";
        String logon = "logonAA";
        String email = "a@b.pt";
        String sub = "e8b54882-97e4-46e5-97c3-a2abd9c3d951";
        Long createdUser = 2L;
        User user = UserFactory.create(firstName,lastName,logon,sub,email, createdUser);
        JsonObject jsonObject = UserModelMapper.map(user);

        assertEquals(user.getFirstname(),jsonObject.getString("firstname"));
        assertEquals(user.getLastname(),jsonObject.getString("lastname"));
        assertEquals(user.getLogon(),jsonObject.getString("logon"));
        assertEquals(user.getSub(),jsonObject.getString("sub"));
        assertEquals(user.getUserEmail(),jsonObject.getString("userEmail"));

    }

    @Test
    public void testMapList() {
        String firstName = "aa";
        String lastName = "bb";
        String logon = "logonAA";
        String sub = "uuidReallyUnique";
        String email = "a@b.pt";
        Long createdUser = 2L;
        User user = UserFactory.create(firstName,lastName,logon,sub,email, createdUser);
        JsonArray jsonArray = UserModelMapper.map(Collections.singletonList(user));
        assertEquals(1,jsonArray.size());
        JsonObject jsonObject = jsonArray.getJsonObject(0);

        assertEquals(user.getFirstname(),jsonObject.getString("firstname"));
        assertEquals(user.getLastname(),jsonObject.getString("lastname"));
        assertEquals(user.getLogon(),jsonObject.getString("logon"));
        assertEquals(user.getSub(),jsonObject.getString("sub"));
        assertEquals(user.getUserEmail(),jsonObject.getString("userEmail"));
    }
}
