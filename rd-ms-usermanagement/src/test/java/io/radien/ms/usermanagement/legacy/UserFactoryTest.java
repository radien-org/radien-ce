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
package io.radien.ms.usermanagement.legacy;

import io.radien.ms.usermanagement.entities.User;
import junit.framework.TestCase;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 * @author Bruno Gama
 */
public class UserFactoryTest extends TestCase {

    JsonObject json;
    User user;

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
        builder.addNull("sub");
        builder.add("firstname", "testFirstName");
        builder.add("lastname", "testLastname");
        builder.add("delegatedCreation",false);
        builder.add("enabled",true);
        json = builder.build();

        user = UserFactory.create("testFirstName", "testLastname", "logonTest", null, "emailtest@emailtest.pt", 2L);
    }

    /**
     * Test method to validate the creation of a User using a Json
     */
    @Test
    public void testCreate() {
        assertEquals("logonTest", user.getLogon());
        assertEquals("emailtest@emailtest.pt", user.getUserEmail());
        assertEquals((Long) 2L, user.getCreateUser());
        assertNull(user.getLastUpdateUser());
        assertNull(user.getSub());
        assertEquals("testFirstName", user.getFirstname());
        assertEquals("testLastname", user.getLastname());
    }

    /**
     * Test method to validate the conversion of a User using a Json
     */
    @Test
    public void testConvert() {
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
     * Test method to validate the conversion of a Json Object using a User
     */
    @Test
    public void testConvertToJsonObject() {
        JsonObject constructedNewJson = UserFactory.convertToJsonObject(user);

        assertEquals(json.toString(), constructedNewJson.toString());
    }
}
