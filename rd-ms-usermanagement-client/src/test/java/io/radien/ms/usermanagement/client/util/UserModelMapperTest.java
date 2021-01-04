package io.radien.ms.usermanagement.client.util;

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
}