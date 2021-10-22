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
package io.radien.ms.rolemanagement.client.util;

import io.radien.api.entity.Page;
import io.radien.ms.rolemanagement.client.entities.TenantRole;
import io.radien.ms.rolemanagement.client.services.TenantRoleFactory;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.InputStream;
import java.util.List;

/**
 * Mapper utility class that allows to convert Tenant Role association beans
 * into JSON objects and vice-versa
 *
 * @author Newton Carvalho
 */
public class TenantRoleModelMapper {

    private TenantRoleModelMapper() {
        // empty constructor
    }

    /**
     * Obtains JSON object from Tenant Role association
     * @param model with the information to be converted into a JSON
     * @return a constructed JSON on the given information
     */
    public static JsonObject map(TenantRole model) {
        return TenantRoleFactory.convertToJsonObject(model);
    }

    /**
     * Converts Input Stream (JSON array) into Tenant Role List
     * @param is inputted information to be converted into the object
     * @return a list of tenant role object based in the received information
     */
    public static List<? extends TenantRole> mapList(InputStream is) {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonArray jsonArray = jsonReader.readArray();
            return TenantRoleFactory.convert(jsonArray);
        }
    }

    /**
     * Converts Input Stream (JSON object) into Tenant Role bean
     * @param is inputted information to be converted into the object
     * @return a tenant role object based in the received information
     */
    public static TenantRole map(InputStream is) {
        try(JsonReader jsonReader = Json.createReader(is)) {
            return TenantRoleFactory.convert(jsonReader.readObject());
        }
    }

    /**
     * Obtains a TenantRole Page from a Json input stream
     * @param is inputted information to be converted into the object
     * @return a page of tenant role object based in the received information
     */
    public static Page<TenantRole> mapToPage(InputStream is) {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonObject jsonObject = jsonReader.readObject();
            return TenantRoleFactory.convertJsonToPage(jsonObject);
        }
    }
}
