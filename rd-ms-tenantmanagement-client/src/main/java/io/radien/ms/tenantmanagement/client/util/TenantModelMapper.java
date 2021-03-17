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
package io.radien.ms.tenantmanagement.client.util;

import io.radien.api.entity.Page;
import io.radien.api.model.tenant.SystemTenant;
import io.radien.ms.tenantmanagement.client.entities.Tenant;
import io.radien.ms.tenantmanagement.client.services.TenantFactory;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;

/**
 * @author Santana
 *
 */
public class TenantModelMapper {


    public static JsonObject map(Tenant model) {
        return TenantFactory.convertToJsonObject(model);
    }

    public static List<? extends SystemTenant> mapList(InputStream is) throws ParseException {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonArray jsonArray = jsonReader.readArray();
            return TenantFactory.convert(jsonArray);
        }
    }

    public static SystemTenant map(InputStream is) throws ParseException {
        try(JsonReader jsonReader = Json.createReader(is)) {
            return TenantFactory.convert(jsonReader.readObject());
        }
    }

    /**
     * Obtains a Permission Page from a Json input stream
     * @param is
     * @return
     */
    public static Page<Tenant> mapToPage(InputStream is) {
        Page<Tenant> page = null;
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonObject jsonObject = jsonReader.readObject();

            page = TenantFactory.convertJsonToPage(jsonObject);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return page;
    }
}
