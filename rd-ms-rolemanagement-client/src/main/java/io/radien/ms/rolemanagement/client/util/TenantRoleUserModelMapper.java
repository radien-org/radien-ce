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
package io.radien.ms.rolemanagement.client.util;

import io.radien.api.entity.Page;
import io.radien.ms.rolemanagement.client.entities.TenantRoleUser;
import io.radien.ms.rolemanagement.client.services.TenantRoleUserFactory;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;

/**
 * Mapper utility class that allows to convert Tenant Role User association beans
 * into JSON objects and vice-versa
 * @author Newton Carvalho
 */
public class TenantRoleUserModelMapper {

    /**
     * Obtains JSON object from Tenant Role User association
     * @param model
     * @return
     */
    public static JsonObject map(TenantRoleUser model) {
        return TenantRoleUserFactory.convertToJsonObject(model);
    }

    /**
     * Converts Input Stream (JSON array) into Tenant Role User List
     * @param is
     * @return
     * @throws ParseException
     */
    public static List<? extends TenantRoleUser> mapList(InputStream is) {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonArray jsonArray = jsonReader.readArray();
            return TenantRoleUserFactory.convert(jsonArray);
        }
    }

    /**
     * Converts Input Stream (JSON object) into Tenant Role User bean
     * @param is
     * @return
     * @throws ParseException
     */
    public static TenantRoleUser map(InputStream is) throws ParseException {
        try(JsonReader jsonReader = Json.createReader(is)) {
            return TenantRoleUserFactory.convert(jsonReader.readObject());
        }
    }

    /**
     * Obtains a TenantRoleUser Page from a Json input stream
     * @param is
     * @return
     */
    public static Page<TenantRoleUser> mapToPage(InputStream is) {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonObject jsonObject = jsonReader.readObject();
            return TenantRoleUserFactory.convertJsonToPage(jsonObject);
        }
    }
}
