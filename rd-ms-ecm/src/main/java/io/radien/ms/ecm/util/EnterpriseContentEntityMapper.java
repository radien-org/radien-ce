package io.radien.ms.ecm.util;

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
import io.radien.api.service.ecm.model.EnterpriseContent;
import io.radien.ms.ecm.factory.ContentFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;


@ApplicationScoped
public class EnterpriseContentEntityMapper {

    @Inject
    private ContentFactory contentFactory;

    /**
     * Maps one Action into a Json Object
     * @param model to be converted into Json
     * @return the converted json object
     */

    //public static JsonObject map(EnterpriseContent model) {
    //    return contentFactory..convertToJsonObject(model);
    //}

    /**
     * Maps an Action Collection into a Json Array
     * @param models list of actions to be converted and sent
     * @return json array with all the given action information
     */
    /*public static JsonArray map(List<Action> models) {
        final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        models.forEach(model -> {
            JsonObject jsonObject = map(model);
            arrayBuilder.add(jsonObject);
        });
        return arrayBuilder.build();
    }*/

    /**
     * Maps a Json stream into a Action
     * @param is input stream to be converted into action
     * @return action object retrieved from the input stream
     */
    public EnterpriseContent map(InputStream is) {
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject)jsonParser.parse(
                    new InputStreamReader(is, StandardCharsets.UTF_8));
            return contentFactory.convertJSONObject(jsonObject);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Obtains a Action Page from a Json input stream
     * @param is to be mapped
     * @return a page of actions mapped from the input stream
     */
//    public static Page<Action> mapToPage(InputStream is) {
//        try(JsonReader jsonReader = Json.createReader(is)) {
//            JsonObject jsonObject = jsonReader.readObject();
//
//            return ActionFactory.convertJsonToPage(jsonObject);
//        }
//    }

}
