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

import io.radien.api.service.ecm.model.EnterpriseContent;
import io.radien.ms.ecm.client.factory.ContentFactory;
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

public class EnterpriseContentMapper {
    private EnterpriseContentMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static EnterpriseContent map(InputStream is) throws IOException, ParseException, java.text.ParseException {
        JSONParser jsonParser = new JSONParser();
        JSONObject obj = (JSONObject) jsonParser.parse(new InputStreamReader(is, StandardCharsets.UTF_8));

        return ContentFactory.convertJSONObject(obj);
    }

    public static List<EnterpriseContent> mapArray(InputStream is) throws IOException, ParseException, java.text.ParseException {
        List<EnterpriseContent> result = new ArrayList<>();
        JSONParser jsonParser = new JSONParser();
        JSONArray array = (JSONArray) jsonParser.parse(new InputStreamReader(is, StandardCharsets.UTF_8));

        for(Object entity : array) {
            result.add(ContentFactory.convertJSONObject((JSONObject) entity));
        }

        return result;
    }
}
