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
package io.radien.ms.usermanagement.client.services;

import io.radien.ms.usermanagement.client.entities.User;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import static org.junit.Assert.*;

public class UserFactoryTest {

    User user;
    JsonObject json;

    /**
     * Constructor class method were we are going to create the JSON and the user for
     * testing purposes.
     */
    public UserFactoryTest() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.addNull("id");
        builder.add("logon", "logonTest");
        builder.add("userEmail", "emailtest@emailtest.pt");
        builder.add("createUser", 2L);
        builder.addNull("lastUpdateUser");
        builder.add("sub","sub");
        builder.add("firstname", "testFirstName");
        builder.add("lastname", "testLastname");
        builder.add("delegatedCreation", false);
        builder.add("enabled", true);

        json = builder.build();

        user = UserFactory.create("testFirstName", "testLastname", "logonTest", "sub", "emailtest@emailtest.pt", 2L);
    }

    /**
     * Test method to validate the creation of a User using a Json
     */
    @Test
    public void create() {
        UserFactory userFactory = new UserFactory();
        User constructedNewUser = userFactory.create("testFirstName", "testLastname", "logonTest", "sub", "emailtest@emailtest.pt", 2L);

        assertEquals(user.getId(), constructedNewUser.getId());
        assertEquals(user.getLogon(), constructedNewUser.getLogon());
        assertEquals(user.getUserEmail(), constructedNewUser.getUserEmail());
        assertEquals(user.getCreateUser(), constructedNewUser.getCreateUser());
        assertEquals(user.getLastUpdateUser(), constructedNewUser.getLastUpdateUser());
        assertEquals(user.getSub(), constructedNewUser.getSub());
        assertEquals(user.getFirstname(), constructedNewUser.getFirstname());
        assertEquals(user.getLastname(), constructedNewUser.getLastname());
    }

    /**
     * Test method to validate the conversion of a User using a Json
     */
    @Test
    public void convert() {
        User constructedNewUser = UserFactory.convert(json);

        assertEquals(user.getId(), constructedNewUser.getId());
        assertEquals(user.getLogon(), constructedNewUser.getLogon());
        assertEquals(user.getUserEmail(), constructedNewUser.getUserEmail());
        assertEquals(user.getCreateUser(), constructedNewUser.getCreateUser());
        assertEquals(user.getLastUpdateUser(), constructedNewUser.getLastUpdateUser());
        assertEquals(user.getSub(), constructedNewUser.getSub());
        assertEquals(user.getFirstname(), constructedNewUser.getFirstname());
        assertEquals(user.getLastname(), constructedNewUser.getLastname());
    }

    /**
     * Test method to validate the conversion of a User using a Json
     */
    @Test
    public void convertEnable() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.addNull("id");
        builder.add("logon", "logonTest");
        builder.add("userEmail", "emailtest@emailtest.pt");
        builder.add("createUser", 2L);
        builder.addNull("lastUpdateUser");
        builder.add("sub","sub");
        builder.add("enabled", true);
        builder.add("delegatedCreation", true);
        builder.add("firstname", "testFirstName");
        builder.add("lastname", "testLastname");

        JsonObject json2 = builder.build();

        user = UserFactory.create("testFirstName", "testLastname", "logonTest", "sub", "emailtest@emailtest.pt", 2L);

        User constructedNewUser = UserFactory.convert(json2);

        assertEquals(user.getId(), constructedNewUser.getId());
        assertEquals(user.getLogon(), constructedNewUser.getLogon());
        assertEquals(user.getUserEmail(), constructedNewUser.getUserEmail());
        assertEquals(user.getCreateUser(), constructedNewUser.getCreateUser());
        assertEquals(user.getLastUpdateUser(), constructedNewUser.getLastUpdateUser());
        assertEquals(user.getSub(), constructedNewUser.getSub());
        assertEquals(user.getFirstname(), constructedNewUser.getFirstname());
        assertEquals(user.getLastname(), constructedNewUser.getLastname());
    }

    @Test
    public void convertToJson() {
        JsonObject constructedNewUser = UserFactory.convertToJsonObject(user);

        assertEquals(json, constructedNewUser);
    }
}