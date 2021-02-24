package io.radien.ms.ecm.client.util;

import io.radien.ms.ecm.client.entities.Translation;

import javax.json.*;
import java.util.ArrayList;
import java.util.List;

public class TranslationMapper {
    public static List<Translation> getTranslationFromJson(String key, JsonObject json) {
        List<Translation> returnedString = new ArrayList<>();
        if (json.containsKey(key)) {
            JsonArray value = json.getJsonArray(key);
            if (value != null) {
                for(int i = 0; i < value.size(); i++) {
                    JsonObject obj = value.getJsonObject(i);
                    Translation t = new Translation();
                    t.setDescription(getStringFromJson("description", obj));
                    t.setLanguage(getStringFromJson("language", obj));
                    returnedString.add(t);
                }
            }
        }
        return returnedString;
    }

    public static JsonArray getJsonArrayFromTranslations(List<Translation> translations) {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        translations.forEach(t -> {
            JsonObjectBuilder objBuilder = Json.createObjectBuilder();
            addValue(objBuilder, "description", t.getDescription());
            addValue(objBuilder, "language", t.getLanguage());
            builder.add(objBuilder.build());
        });
        return builder.build();
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

    private static void addValue(JsonObjectBuilder builder, String key, Object value) {
        if (value != null) {
            builder.add(key, value.toString());
        } else {
            builder.addNull(key);
        }
    }
}
