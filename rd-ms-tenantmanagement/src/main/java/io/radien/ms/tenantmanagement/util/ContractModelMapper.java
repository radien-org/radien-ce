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
package io.radien.ms.tenantmanagement.util;

import io.radien.ms.tenantmanagement.client.services.ContractFactory;
import io.radien.ms.tenantmanagement.entities.ContractEntity;

import java.io.InputStream;
import java.text.ParseException;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;

/**
 * Mapper from a given information into a JSON or a contract
 * @author Santana
 */
public class ContractModelMapper {

    /**
     * Maps into a Json Object a Contract
     * @param model contract that has the information to be converted
     * @return a json object created based the contract
     */
    public static JsonObject map(ContractEntity model) {
        return ContractFactory.convertToJsonObject(model);
    }

    /**
     * Maps into a Json Object array based on a contract array list
     * @param models contract that have the information to be converted
     * @return a json array created based on the multiple contracts
     */
    public static JsonArray map(List<ContractEntity> models) {
        final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        models.forEach(model -> {
            JsonObject jsonObject = map(model);
            arrayBuilder.add(jsonObject);
        });
        return arrayBuilder.build();
    }

    /**
     * Creates a contract based a received inputted information
     * @param is inputted information to be converted into the object
     * @return a contract object based in the received information
     */
    public static ContractEntity map(InputStream is) throws ParseException{
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonObject jsonObject = jsonReader.readObject();

            return new ContractEntity(ContractFactory.convert(jsonObject));
        }
    }
}
