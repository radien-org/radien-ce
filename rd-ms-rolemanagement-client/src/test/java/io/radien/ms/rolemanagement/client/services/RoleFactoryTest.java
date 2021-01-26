package io.radien.ms.rolemanagement.client.services;

import io.radien.ms.rolemanagement.client.entities.Role;
import junit.framework.TestCase;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class RoleFactoryTest extends TestCase {

    Role role = new Role();
    JsonObject json;

    public RoleFactoryTest() {
        role.setName("nameValue");
        role.setDescription("descriptionValue");
        role.setCreateUser(2L);
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

        json = builder.build();
        Role newJsonRole = RoleFactory.convert(json);

        assertEquals(role.getName(), newJsonRole.getName());
        assertEquals(role.getDescription(), newJsonRole.getDescription());
        assertEquals(role.getCreateUser(), newJsonRole.getCreateUser());
    }

    @Test
    public void testConvertToJsonObject() {

    }
}