package io.radien.ms.authz.service;

import io.radien.ms.openid.entities.Principal;
import io.radien.ms.openid.service.PrincipalFactory;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import static org.junit.Assert.assertEquals;

public class PrincipalFactoryTest {

    Principal principal;
    JsonObject json;

    /**
     * Constructor class method were we are going to create the JSON and the user for
     * testing purposes.
     */
    public PrincipalFactoryTest() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("preferred_username", "logonTest");
        builder.add("email", "emailtest@emailtest.pt");
        builder.add("sub","sub");
        builder.add("given_name", "testFirstName");
        builder.add("family_name", "testLastname");

        json = builder.build();

        principal = PrincipalFactory.create("testFirstName", "testLastname",
                "logonTest", "sub", "emailtest@emailtest.pt", 2L);
    }

    /**
     * Test method to validate the creation of a User using a Json
     */
    @Test
    public void create() {
        PrincipalFactory userFactory = new PrincipalFactory();
        Principal constructedNewPrincipal = userFactory.create("testFirstName", "testLastname", "logonTest", "sub", "emailtest@emailtest.pt", 2L);

        assertEquals(principal.getId(), constructedNewPrincipal.getId());
        assertEquals(principal.getLogon(), constructedNewPrincipal.getLogon());
        assertEquals(principal.getUserEmail(), constructedNewPrincipal.getUserEmail());
        assertEquals(principal.getCreateUser(), constructedNewPrincipal.getCreateUser());
        assertEquals(principal.getLastUpdateUser(), constructedNewPrincipal.getLastUpdateUser());
        assertEquals(principal.getSub(), constructedNewPrincipal.getSub());
        assertEquals(principal.getFirstname(), constructedNewPrincipal.getFirstname());
        assertEquals(principal.getLastname(), constructedNewPrincipal.getLastname());
    }

    /**
     * Test method to validate the conversion of a User using a Json
     */
    @Test
    public void convert() {
        Principal constructedNewUser = PrincipalFactory.convert(json);

        assertEquals(principal.getId(), constructedNewUser.getId());
        assertEquals(principal.getLogon(), constructedNewUser.getLogon());
        assertEquals(principal.getUserEmail(), constructedNewUser.getUserEmail());
        assertEquals(principal.getLastUpdateUser(), constructedNewUser.getLastUpdateUser());
        assertEquals(principal.getSub(), constructedNewUser.getSub());
        assertEquals(principal.getFirstname(), constructedNewUser.getFirstname());
        assertEquals(principal.getLastname(), constructedNewUser.getLastname());
    }

}
