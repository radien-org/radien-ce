package io.radien.ms.usermanagement.client.util;

import javax.json.*;
import java.io.Serializable;

public class FactoryUtilService implements Serializable {

    private static final long serialVersionUID = 7731959741864165435L;

    /**
     * Retrieves the String value from the Json Object
     * @param key of the value to be retrieved
     * @param json object with the values to be retrieved
     * @return String value
     */
    public static String getStringFromJson(String key, JsonObject json) {
        String returnedString = null;
        // case where key is present with value null
        if (isValueNotNull(key, json)) {
            JsonString value = json.getJsonString(key);
            if (value != null) {
                returnedString = value.getString();
            }
        }
        return returnedString;
    }

    /**
     * Retrieves the Integer value from the Json Object
     * @param key of the value to be retrieved
     * @param json object with the values to be retrieved
     * @return Integer value
     */
    public static Integer getIntFromJson(String key, JsonObject json) {
        Integer returnedValue = null;
        // case where key is present with value null
        if (isValueNotNull(key, json)) {
            JsonNumber value = json.getJsonNumber(key);
            if (value != null) {
                returnedValue = value.intValue();
            }
        }
        return returnedValue;
    }

    /**
     * Retrieves the Long value from the Json Object
     * @param key of the value to be retrieved
     * @param json object with the values to be retrieved
     * @return Long value
     */
    public static Long getLongFromJson(String key, JsonObject json) {
        Long returnedValue = null;
        // case where key is present with value null
        if (isValueNotNull(key, json)) {
            JsonNumber value = json.getJsonNumber(key);
            if (value != null) {
                returnedValue = value.longValue();
            }
        }
        return returnedValue;
    }

    /**
     * Retrieves the JsonArray from the Json Object
     * @param key of the value to be retrieved
     * @param json object with the values to be retrieved
     * @return Json Array value
     */
    public static JsonArray getArrayFromJson(String key, JsonObject json){
        JsonArray returnedValue = null;
        if (isValueNotNull(key, json)) {
            return json.getJsonArray(key);
        }
        return returnedValue;
    }

    /**
     * Validates if the present key has a null value or not.
     * @param key of the value to be retrieved
     * @param json object with the values to be retrieved
     * @return true case the value is not null and false if it is
     */
    private static boolean isValueNotNull(String key, JsonObject json) {
        JsonValue val = json.get(key);
        if (val != null && !val.toString().equalsIgnoreCase("null")) {
            return true;
        }
        return false;
    }

    /**
     * Adds the values String to the designated keys in a json object
     *
     * @param builder Json Object builder that it is being used
     * @param key value of the json field
     * @param value value of the field to be added
     */
    public static void addValue(JsonObjectBuilder builder, String key, Object value) {
        if (value != null) {
            builder.add(key, value.toString());
        } else {
            builder.addNull(key);
        }
    }

    /**
     * Adds the values Long to the designated keys in a json object
     *
     * @param builder Json Object builder that it is being used
     * @param key value of the json field
     * @param value value of the field to be added
     */
    public static void addValueLong(JsonObjectBuilder builder, String key, Object value) {
        if (value != null) {
            builder.add(key, (Long) value);
        } else {
            builder.addNull(key);
        }
    }

    /**
     * Adds the values Long to the designated keys in a json object
     *
     * @param builder Json Object builder that it is being used
     * @param key value of the json field
     * @param value value of the field to be added
     */
    public static void addValueInt(JsonObjectBuilder builder, String key, Object value) {
        if (value != null) {
            builder.add(key, (Integer) value);
        } else {
            builder.addNull(key);
        }
    }

    public static void addValueArray(JsonObjectBuilder builder, String key, Object value) {
        if (value != null) {
            builder.add(key, (JsonValue) value);
        } else {
            builder.addNull(key);
        }
    }

}
