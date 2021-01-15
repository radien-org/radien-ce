package io.radien.ms.usermanagement.client.util;

import io.radien.api.entity.Page;
import io.radien.api.model.user.SystemUser;
import io.radien.ms.usermanagement.client.entities.User;
import io.radien.ms.usermanagement.client.services.PageFactory;
import io.radien.ms.usermanagement.client.services.UserFactory;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;
import java.io.InputStream;
import java.util.List;

public class ListUserModelMapper {
    public static List<? extends SystemUser> map(InputStream is) {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonArray jsonArray = jsonReader.readArray();
            return UserFactory.convert(jsonArray);
        }
    }
}
