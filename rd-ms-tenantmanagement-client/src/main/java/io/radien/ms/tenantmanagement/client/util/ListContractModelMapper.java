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

package io.radien.ms.tenantmanagement.client.util;

import io.radien.api.model.tenant.SystemContract;
import io.radien.ms.tenantmanagement.client.services.ContractFactory;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;

/**
 * Contract list mapper class
 *
 * @author Bruno Gama
 */
public class ListContractModelMapper {

    /**
     * Coverts a given input into a list of system contracts
     * @param is response or request requested input stream
     * @return a list of system contracts that have been gather and converted from the inputted stream
     * @throws ParseException in case of issue while parsing the inputted stream
     */
    public static List<? extends SystemContract> map(InputStream is) throws ParseException {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonArray jsonArray = jsonReader.readArray();
            return ContractFactory.convert(jsonArray);
        }
    }
}
