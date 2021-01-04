package io.radien.ms.usermanagement.client.util;

import io.radien.ms.usermanagement.client.entities.Page;
import io.radien.ms.usermanagement.client.entities.User;
import io.radien.ms.usermanagement.client.services.PageFactory;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.InputStream;



/**
 * @author Marco Weiland 
 *
 */
public class PageModelMapper {

    public static Page<User> map(InputStream is) {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonObject jsonObject = jsonReader.readObject();

            return PageFactory.convert(jsonObject);
        }
    }
}
