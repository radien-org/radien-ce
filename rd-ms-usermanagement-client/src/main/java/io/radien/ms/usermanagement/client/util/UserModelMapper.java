package io.radien.ms.usermanagement.client.util;


import io.radien.ms.usermanagement.client.entities.User;
import io.radien.ms.usermanagement.client.services.UserFactory;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.InputStream;
import java.util.List;


/**
 * @author Marco Weiland 
 *
 */
public class UserModelMapper {


    public static JsonObject map(User model) {
        return UserFactory.convertToJsonObject(model);
    }

    public static JsonArray map(List<User> models) {
        final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        models.forEach(model -> {
            JsonObject jsonObject = map(model);
            arrayBuilder.add(jsonObject);
        });
        return arrayBuilder.build();
    }

    public static User map(InputStream is) {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonObject jsonObject = jsonReader.readObject();

            return UserFactory.convert(jsonObject);
        }
    }
}
