/*
 * Copyright (c) 2021-present radien GmbH. All rights reserved.
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
package ${package}.ms.client.util;

import io.radien.api.entity.Page;

import io.radien.api.model.${entityResourceName.toLowerCase()}.System${entityResourceName};

import ${package}.ms.client.entities.${entityResourceName};
import ${package}.ms.client.services.${entityResourceName}Factory;

import java.io.InputStream;
import java.text.ParseException;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;
/**
 * Mapper from a given information into a JSON or a ${entityResourceName}
 *
 * @author Bruno Gama
 */
public class ${entityResourceName}ModelMapper {

    private ${entityResourceName}ModelMapper() {
        // empty constructor
    }

    /**
     * Maps into a Json Object a ${entityResourceName}
     * @param model ${entityResourceName.toLowerCase()} that has the information to be converted
     * @return a json object created based the ${entityResourceName.toLowerCase()}
     */
    public static JsonObject map(${entityResourceName} model) {
        return ${entityResourceName}Factory.convertToJsonObject(model);
    }

    /**
     * Maps into a Json Object array based on a ${entityResourceName.toLowerCase()} array list
     * @param models ${entityResourceName.toLowerCase()} that have the information to be converted
     * @return json array created based on the multiple ${entityResourceName.toLowerCase()}s
     */
    public static JsonArray map(List<${entityResourceName}> models) {
        final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        models.forEach(model -> {
            JsonObject jsonObject = map(model);
            arrayBuilder.add(jsonObject);
        });
        return arrayBuilder.build();
    }

    /**
     * Creates a ${entityResourceName.toLowerCase()} based a received inputted information
     * @param is inputted information to be converted into the object
     * @return a ${entityResourceName.toLowerCase()} object based in the received information
     */
    public static ${entityResourceName} map(InputStream is) {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonObject jsonObject = jsonReader.readObject();
            return ${entityResourceName}Factory.convert(jsonObject);
        }
    }

    /**
     * Obtains a ${entityResourceName} Page from a Json input stream
     * @param is inputted information to be converted into the object
     * @return a page of ${entityResourceName.toLowerCase()}s with the requested information
     */
    public static Page<${entityResourceName}> mapToPage(InputStream is) {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonObject jsonObject = jsonReader.readObject();

            return ${entityResourceName}Factory.convertJsonToPage(jsonObject);
        }
    }

    /**
     * Converts Input Stream (JSON array) into List
     * @param is inputted information to be converted into the object List
     * @return a list of ${entityResourceName.toLowerCase()}s with the requested information
     */
    public static List<? extends System${entityResourceName}> mapList(InputStream is) throws ParseException {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonArray jsonArray = jsonReader.readArray();
            return ${entityResourceName}Factory.convert(jsonArray);
        }
    }
}
