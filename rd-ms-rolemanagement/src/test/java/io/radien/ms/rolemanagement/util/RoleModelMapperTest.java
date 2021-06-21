package io.radien.ms.rolemanagement.util;

import io.radien.ms.rolemanagement.entities.Role;
import io.radien.ms.rolemanagement.factory.RoleFactory;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;;
import static org.junit.jupiter.api.Assertions.assertNull;

public class RoleModelMapperTest {

    @Test
    public void testMapRole() {
        Role role = RoleFactory.create("nameValue", "descriptionValue", 2L);

        JsonObject jsonObject = RoleModelMapper.map(role);
        assertEquals(role.getName(),jsonObject.getString("name"));
        assertEquals(role.getDescription(),jsonObject.getString("description"));
    }

    @Test
    public void testMapList() {
        Role role = RoleFactory.create("nameValue", "descriptionValue", 2L);

        JsonArray jsonArray = RoleModelMapper.map(Collections.singletonList(role));
        assertEquals(1,jsonArray.size());
        JsonObject jsonObject = jsonArray.getJsonObject(0);

        assertEquals(role.getName(),jsonObject.getString("name"));
        assertEquals(role.getDescription(),jsonObject.getString("description"));
    }

    @Test
    public void testMapInputStream() {
        String result = "{\"" +
                "id\":null," +
                "\"name\":\"nameValue\"," +
                "\"description\":\"descriptionValue\"" +
                "}";

        InputStream in = new ByteArrayInputStream(result.getBytes());
        Role role = RoleModelMapper.map(in);

        assertNull(role.getId());
        assertEquals("nameValue", role.getName());
        assertEquals("descriptionValue", role.getDescription());
    }
}