/*
 * Copyright (c) 2016-present openappframe.org & its legal owners. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.radien.api.util;

import javax.json.JsonString;
import javax.json.JsonObject;
import javax.json.JsonNumber;
import javax.json.JsonValue;
import javax.json.JsonObjectBuilder;
import javax.json.JsonArray;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Factory Util service for converting objects into json ones or vice versa
 *
 * @author Nuno Santana
 */
public class FactoryUtilService implements Serializable {
    private final static String DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";

    private static final long serialVersionUID = -6560813510881461316L;

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
     * Retrieves the Double value from the Json Object
     * @param key of the value to be retrieved
     * @param json object with the values to be retrieved
     * @return Double value
     */
    public static Double getDoubleFromJson(String key, JsonObject json) {
        Double returnedValue = null;
        // case where key is present with value null
        if (isValueNotNull(key, json)) {
            JsonNumber value = json.getJsonNumber(key);
            if (value != null) {
                returnedValue = value.doubleValue();
            }
        }
        return returnedValue;
    }

    /**
     * Retrieves the Boolean value from the Json Object
     * @param key of the value to be retrieved
     * @param json object with the values to be retrieved
     * @return Boolean value
     */
    public static Boolean getBooleanFromJson(String key, JsonObject json) {
        Boolean returnedBool = null;
        // case where key is present with value null
        if (isValueNotNull(key, json)) {
            JsonValue value = json.get(key);
            if (value != null) {
                returnedBool = Boolean.parseBoolean(value.toString());
            }
        }
        return returnedBool;
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
     * Adds the values String to the designated keys in a json object
     *
     * @param builder Json Object builder that it is being used
     * @param key value of the json field
     * @param value value of the field to be added
     */
    public static void addValueBoolean(JsonObjectBuilder builder, String key, Boolean value) {
        if (value != null) {
            builder.add(key, value);
        } else {
            builder.addNull(key);
        }
    }

    /**
     * Adds the values Date to the designated keys in a json object
     *
     * @param builder Json Object builder that it is being used
     * @param key value of the json field
     * @param value value of the field to be added
     */
    public static void addValue(JsonObjectBuilder builder, String key, Date value) {
        if (value != null) {
            DateFormat df = new SimpleDateFormat(DATE_FORMAT);
            builder.add(key, df.format(value));
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
     * Adds the values Double to the designated keys in a json object
     *
     * @param builder Json Object builder that it is being used
     * @param key value of the json field
     * @param value value of the field to be added
     */
    public static void addValueDouble(JsonObjectBuilder builder, String key, Object value) {
        if (value != null) {
            builder.add(key, (Double) value);
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

    /**
     * Adds the value given object into an json array
     * @param builder Json Object builder that it is being used
     * @param key value of the json field
     * @param value value of the field to be addedwebappDataModelEnum
     */
    public static void addValueArray(JsonObjectBuilder builder, String key, Object value) {
        if (value != null) {
            builder.add(key, (JsonValue) value);
        } else {
            builder.addNull(key);
        }
    }

    /**
     * Retrieves the LocalDateTime value from the Json Object
     * @param key of the value to be retrieved
     * @param json object with the values to be retrieved
     * @return LocalDateTime value
     */
    public static LocalDateTime getLocalDateTimeFromJson(String key, JsonObject json) {
        String returnedString = null;
        // case where key is present with value null
        if (isValueNotNull(key, json)) {
            JsonString value = json.getJsonString(key);
            if (value != null) {
                returnedString = value.getString();
            }
        }
        if(returnedString != null){
            return LocalDateTime.parse(returnedString);
        }
        return null;
    }
}