/*
 * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
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
package ${package}.services;

import ${package}.entities.${resource-name};
import ${package}.util.FactoryUtilService;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 * @author Rajesh Gavvala
 *
 */

public class ${resource-name}Factory {

    public static ${resource-name} create(String message){
        ${resource-name} ${resource-name-variable} = new ${resource-name}();
        ${resource-name-variable}.setMessage(message);
        return ${resource-name-variable};
    }

    public static ${resource-name} convert(JsonObject jsonObject) {
        Long id = FactoryUtilService.getLongFromJson("id", jsonObject);
        String message = FactoryUtilService.getStringFromJson("message", jsonObject);

        ${resource-name} ${resource-name-variable} = new ${resource-name}();
        ${resource-name-variable}.setId(id);
        ${resource-name-variable}.setMessage(message);
        return ${resource-name-variable};
    }

    public static JsonObject convertToJsonObject(${resource-name} template) {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        FactoryUtilService.addValueLong(builder, "id", template.getId());
        FactoryUtilService.addValue(builder, "message", template.getMessage());
        return  builder.build();
    }
}
