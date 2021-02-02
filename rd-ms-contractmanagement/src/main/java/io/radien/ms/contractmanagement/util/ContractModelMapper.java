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
package io.radien.ms.contractmanagement.util;


import io.radien.ms.contractmanagement.client.services.ContractFactory;
import io.radien.ms.contractmanagement.entities.Contract;

import java.io.InputStream;
import java.text.ParseException;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;


/**
 * @author Santana
 *
 */
public class ContractModelMapper {


    public static JsonObject map(Contract model) {
        return ContractFactory.convertToJsonObject(model);
    }

    public static JsonArray map(List<Contract> models) {
        final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        models.forEach(model -> {
            JsonObject jsonObject = map(model);
            arrayBuilder.add(jsonObject);
        });
        return arrayBuilder.build();
    }

    public static Contract map(InputStream is) {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonObject jsonObject = jsonReader.readObject();

            return new Contract(ContractFactory.convert(jsonObject));
        } catch (ParseException e) {
            e.printStackTrace();
            //TODO : improve error handling
            return null;
        }
    }
}
