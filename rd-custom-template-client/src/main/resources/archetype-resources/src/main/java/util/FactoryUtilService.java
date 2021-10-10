/*
 * Copyright (c) 2021-present radien GmbH & its legal owners. All rights reserved.
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
package ${package}.util;

import javax.json.*;
import java.io.Serializable;

public class FactoryUtilService implements Serializable {

    private static final long serialVersionUID = 7731959741864165435L;

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
