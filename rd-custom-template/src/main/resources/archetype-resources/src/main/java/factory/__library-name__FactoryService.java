package ${package}.factory;

import javax.json.*;
import java.io.Serializable;

/**
 * @author Rajesh Gavvala
 *
 */

public class ${library-name}FactoryService implements Serializable {
    public static String getStringFromJson(String key, JsonObject json) {
        String returnedString = null;
        if (isValueNotNull(key, json)) {
            JsonString value = json.getJsonString(key);
            if (value != null) {
                returnedString = value.getString();
            }
        }
        return returnedString;
    }

    public static Long getLongFromJson(String key, JsonObject json) {
        Long returnedValue = null;

        if (isValueNotNull(key, json)) {
            JsonNumber value = json.getJsonNumber(key);
            if (value != null) {
                returnedValue = value.longValue();
            }
        }
        return returnedValue;
    }

    private static boolean isValueNotNull(String key, JsonObject json) {
        JsonValue val = json.get(key);
        if (val != null && !val.toString().equalsIgnoreCase("null")) {
            return true;
        }
        return false;
    }

    public static void addValue(JsonObjectBuilder builder, String key, Object value) {
        if (value != null) {
            builder.add(key, value.toString());
        } else {
            builder.addNull(key);
        }
    }

    public static void addValueLong(JsonObjectBuilder builder, String key, Object value) {
        if (value != null) {
            builder.add(key, (Long) value);
        } else {
            builder.addNull(key);
        }
    }
}
