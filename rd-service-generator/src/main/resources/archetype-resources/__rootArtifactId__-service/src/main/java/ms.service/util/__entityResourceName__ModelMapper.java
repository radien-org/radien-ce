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
package ${package}.ms.service.util;

import java.io.InputStream;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;

import ${package}.ms.service.entities.${entityResourceName};
import ${package}.ms.service.legacy.${entityResourceName}Factory;

/**
 * @author Marco Weiland 
 *
 */
public class ${entityResourceName}ModelMapper {
    private static final long serialVersionUID = 6812608123262000070L;
    public static JsonObject map(${entityResourceName} model) {
        return ${entityResourceName}Factory.convertToJsonObject(model);
    }

    public static JsonArray map(List<${entityResourceName}> models) {
        final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        models.forEach(model -> {
            JsonObject jsonObject = map(model);
            arrayBuilder.add(jsonObject);
        });
        return arrayBuilder.build();
    }

    public static ${entityResourceName} map(InputStream is) {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonObject jsonObject = jsonReader.readObject();

            return ${entityResourceName}Factory.convert(jsonObject);
        }
    }
}
