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
package ${package}.util;

import ${client-packageName}.services.${resource-name}Factory;
import ${package}.entities.${resource-name};

import java.io.InputStream;
import java.text.ParseException;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;


/**
 * @ Rajesh Gavvala
 */
public class ${resource-name}ModelMapper {

    public static JsonObject map(${resource-name} model) {
        return ${resource-name}Factory.convertToJsonObject(model);
    }

    public static ${resource-name} map(InputStream is) throws ParseException {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonObject jsonObject = jsonReader.readObject();
            return new ${resource-name}(${resource-name}Factory.convert(jsonObject));
        }
    }

}
