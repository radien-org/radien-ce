package io.radien.ms.tenantmanagement.util;

import java.io.InputStream;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonString;

import io.radien.ms.tenantmanagement.model.RadienModel;

/**
 * @author mawe
 *
 */
public class RadienModelMapper {

    public static JsonObject map(RadienModel model) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        addValue(builder, "id", model.getId());
        addValue(builder, "message", model.getMessage());
        return builder.build();
    }

    private static void addValue(JsonObjectBuilder builder, String key, Object value) {
        if (value != null) {
            builder.add(key, value.toString());
        } else {
            builder.addNull(key);
        }
    }

    public static JsonArray map(List<RadienModel> models) {
        final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        models.forEach(model -> {
            JsonObject jsonObject = map(model);
            arrayBuilder.add(jsonObject);
        });
        return arrayBuilder.build();
    }

    public static RadienModel map(InputStream is) {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonObject jsonObject = jsonReader.readObject();
            RadienModel model = new RadienModel();
            model.setId(getStringFromJson("id", jsonObject));
            model.setMessage(getStringFromJson("message", jsonObject));
            return model;
        }
    }

    private static String getStringFromJson(String key, JsonObject json) {
        String returnedString = null;
        if (json.containsKey(key)) {
            JsonString value = json.getJsonString(key);
            if (value != null) {
                returnedString = value.getString();
            }
        }
        return returnedString;
    }

    private static Integer getIntFromJson(String key, JsonObject json) {
        Integer returnedValue = null;
        if (json.containsKey(key)) {
            JsonNumber value = json.getJsonNumber(key);
            if (value != null) {
                returnedValue = value.intValue();
            }
        }
        return returnedValue;
    }
}