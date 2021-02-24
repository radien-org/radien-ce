package io.radien.ms.ecm.client.util;

import io.radien.ms.ecm.client.entities.I18NProperty;
import io.radien.ms.ecm.client.entities.LabelTypeEnum;

import javax.json.*;
import java.io.InputStream;
import java.util.List;

public class I18NPropertyMapper {

    public static JsonObject map(I18NProperty model) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        addValue(builder, "key", model.getKey());
        addValue(builder, "type", model.getType());
        addValue(builder, "translations", TranslationMapper.getJsonArrayFromTranslations(model.getTranslations()));
        return builder.build();
    }

    private static void addValue(JsonObjectBuilder builder, String key, Object value) {
        if (value != null) {
            builder.add(key, value.toString());
        } else {
            builder.addNull(key);
        }
    }

    public static JsonArray map(List<I18NProperty> models) {
        final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        models.forEach(model -> {
            JsonObject jsonObject = map(model);
            arrayBuilder.add(jsonObject);
        });
        return arrayBuilder.build();
    }

    public static I18NProperty map(InputStream is) {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonObject jsonObject = jsonReader.readObject();
            I18NProperty model = new I18NProperty();
            model.setKey(getStringFromJson("key", jsonObject));
            model.setType(LabelTypeEnum.valueOf(getStringFromJson("type", jsonObject)));
            model.setTranslations(TranslationMapper.getTranslationFromJson("translations", jsonObject));
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
}
