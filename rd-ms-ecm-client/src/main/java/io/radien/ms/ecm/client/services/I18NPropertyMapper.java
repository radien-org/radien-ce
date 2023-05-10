/*
 *
 *  * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
 *  * <p>
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  * <p>
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  * <p>
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package io.radien.ms.ecm.client.services;

import io.radien.api.entity.Page;
import io.radien.api.model.i18n.SystemI18NProperty;
import io.radien.ms.ecm.client.factory.I18NFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class I18NPropertyMapper {
    private I18NPropertyMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static SystemI18NProperty map(InputStream is) throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        JSONObject obj = (JSONObject) jsonParser.parse(new InputStreamReader(is, StandardCharsets.UTF_8));

        return I18NFactory.convertJSONObject(obj);
    }

    public static Page<SystemI18NProperty> mapToPage(InputStream is) throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        JSONObject obj = (JSONObject) jsonParser.parse(new InputStreamReader(is, StandardCharsets.UTF_8));

        return I18NFactory.convertJsonToPage(obj);
    }

    public static List<SystemI18NProperty> mapArray(InputStream is) throws IOException, ParseException {
        List<SystemI18NProperty> result = new ArrayList<>();
        JSONParser jsonParser = new JSONParser();
        JSONArray array = (JSONArray) jsonParser.parse(new InputStreamReader(is, StandardCharsets.UTF_8));

        for(Object entity : array) {
            result.add(I18NFactory.convertJSONObject((JSONObject) entity));
        }

        return result;
    }
}
