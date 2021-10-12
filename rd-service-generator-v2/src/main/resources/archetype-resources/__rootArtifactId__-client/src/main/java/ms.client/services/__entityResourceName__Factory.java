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
package ${package}.ms.client.services;

import io.radien.api.entity.Page;
import io.radien.api.util.FactoryUtilService;

import ${package}.ms.client.entities.${entityResourceName};

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * ${entityResourceName} Management object factory.
 * Converts and maps information from Json or
 * to Json into a ${entityResourceName} or
 * from a ${entityResourceName} object
 *
 * @author Rajesh Gavvala
 */
public class ${entityResourceName}Factory {
    private static final Logger log = LoggerFactory.getLogger(${entityResourceName}Factory.class);
    /**
     * Create a ${entityResourceName.toLowerCase()} with already predefined fields.
     *
     * @param name of an ${entityResourceName}.
     * @return a ${entityResourceName} object to be used.
     */
    public static ${entityResourceName} create(String name) {
        ${entityResourceName} ${entityResourceName.toLowerCase()} = new ${entityResourceName}();

        ${entityResourceName.toLowerCase()}.setName(name);

        Date now = new Date();
        ${entityResourceName.toLowerCase()}.setLastUpdate(now);
        ${entityResourceName.toLowerCase()}.setCreateDate(now);

        log.info("Client will begin to create a new ${entityResourceName} object with the specific values" +
                " Name: {} ", name);

        return ${entityResourceName.toLowerCase()};
    }

    /**
     * Converts a JSONObject to a ${entityResourceName} object that will be used by the Application.
     *
     * @param json${entityResourceName} receives a json object with all the information.
     * @return a ${entityResourceName} object constructed by the given json.
     */
    public static ${entityResourceName} convert(JsonObject json${entityResourceName}) {
        Long id = FactoryUtilService.getLongFromJson("id", json${entityResourceName});
        String name = FactoryUtilService.getStringFromJson("name", json${entityResourceName});

        ${entityResourceName} ${entityResourceName.toLowerCase()} = new ${entityResourceName}();
        ${entityResourceName.toLowerCase()}.setId(id);
        ${entityResourceName.toLowerCase()}.setName(name);
        ${entityResourceName.toLowerCase()}.setCreateDate(new Date());
        ${entityResourceName.toLowerCase()}.setLastUpdate(new Date());

        log.info("Client will begin to create a new ${entityResourceName} " +
                "object with the specific values received in the json" +
                " ID: {}, Name: {}", id, name);

        return ${entityResourceName.toLowerCase()};
    }

    /**
     * Converts a System ${entityResourceName} to a Json Object.
     * @param ${entityResourceName.toLowerCase()} system${entityResourceName} to be converted to json.
     * @return json object with the keys and values constructed by the given object.
     */
    public static JsonObject convertToJsonObject(${entityResourceName} ${entityResourceName.toLowerCase()}) {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        FactoryUtilService.addValueLong(builder, "id", ${entityResourceName.toLowerCase()}.getId());
        FactoryUtilService.addValue(builder, "name", ${entityResourceName.toLowerCase()}.getName());

        log.info("Will begin to create a new json object with the specific values received in the give " +
                "${entityResourceName.toLowerCase()}" +
                " ID: {}, Name: {}", ${entityResourceName.toLowerCase()}.getId(),
                ${entityResourceName.toLowerCase()}.getName());

        return builder.build();
    }

    /**
     * Converts a JsonObject into a ${entityResourceName} Page object
     * @param jsonObject${entityResourceName} to convert
     * @return the Page encapsulating information regarding permissions
     */
    public static Page<${entityResourceName}> convertJsonToPage(JsonObject jsonObject${entityResourceName}) {
        int currentPage = FactoryUtilService.getIntFromJson("currentPage", jsonObject${entityResourceName});
        JsonArray results = FactoryUtilService.getArrayFromJson("results", jsonObject${entityResourceName});
        int totalPages = FactoryUtilService.getIntFromJson("totalPages", jsonObject${entityResourceName});
        int totalResults = FactoryUtilService.getIntFromJson("totalResults", jsonObject${entityResourceName});

        ArrayList<${entityResourceName}> pageResults = new ArrayList<>();

        if(results != null){
            for(int i = 0;i<results.size();i++){
                pageResults.add(convert(results.getJsonObject(i)));
            }
        }
        return new Page<>(pageResults,currentPage,totalResults,totalPages);
    }

    /**
     * Method to convert a given json array with relevant information
     * into ${entityResourceName.toLowerCase()} objects that will be stored into a list
     * @param jsonArrayjsonObject${entityResourceName} to be mapped
     * @return a list of all the ${entityResourceName.toLowerCase()} information
     */
    public static List<${entityResourceName}> convert(JsonArray jsonArrayjsonObject${entityResourceName}) {
        return jsonArrayjsonObject${entityResourceName}.stream().map(i->convert(i.asJsonObject())).collect(Collectors.toList());
    }
}
