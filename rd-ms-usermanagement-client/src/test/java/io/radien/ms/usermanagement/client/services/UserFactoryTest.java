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


}
