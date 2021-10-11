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
package ${package}.ms.client.services;

import ${package}.api.util.FactoryUtilService;
import ${package}.api.entity.Page;
import ${package}.ms.client.entities.${entityResourceName};

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Rajesh Gavvala
 */
public class ${entityResourceName}Factory {
    private static final long serialVersionUID = 6812608123262000045L;

    public static ${entityResourceName} create(String firstname){
        ${entityResourceName} ${entityResourceName.toLowerCase()} = new ${entityResourceName}();
        ${entityResourceName.toLowerCase()}.setName(firstname);
        return ${entityResourceName.toLowerCase()};
    }

    public static ${entityResourceName} convert(JsonObject person) {
        Long id = FactoryUtilService.getLongFromJson("id", person);
        String logon = FactoryUtilService.getStringFromJson("name", person);

        ${entityResourceName} ${entityResourceName.toLowerCase()} = new ${entityResourceName}();
        ${entityResourceName.toLowerCase()}.setId(id);
        ${entityResourceName.toLowerCase()}.setName(logon);

        return ${entityResourceName.toLowerCase()};
    }

    public static JsonObject convertToJsonObject(${entityResourceName} person) {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        FactoryUtilService.addValueLong(builder, "id", person.getId());
        FactoryUtilService.addValue(builder, "name", person.getName());

        return  builder.build();
    }

    public static List<${entityResourceName}> convert(JsonArray jsonArray) {
        return jsonArray.stream().map(i->convert(i.asJsonObject())).collect(Collectors.toList());
    }

    public static Page<${entityResourceName}> convertJsonToPage(JsonObject page) {
        int currentPage = FactoryUtilService.getIntFromJson("currentPage", page);
        JsonArray results = FactoryUtilService.getArrayFromJson("results", page);
        int totalPages = FactoryUtilService.getIntFromJson("totalPages", page);
        int totalResults = FactoryUtilService.getIntFromJson("totalResults", page);

        ArrayList<${entityResourceName}> pageResults = new ArrayList();

        if(results != null){
            for(int i = 0;i<results.size();i++){
                pageResults.add(convert(results.getJsonObject(i)));
            }
        }
        return new Page<>(pageResults, currentPage, totalResults, totalPages);
    }
}
