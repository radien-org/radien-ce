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
package io.radien.ms.tenantmanagement.client.util;

import io.radien.api.entity.Page;
import io.radien.ms.tenantmanagement.client.entities.Tenant;
import io.radien.ms.tenantmanagement.client.services.TenantFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;

/**
 * Mapper from a given information into a JSON or a contract
 * @author Santana
 */
public class TenantModelMapper {

    protected static final Logger log = LoggerFactory.getLogger(TenantModelMapper.class);

    /**
     * Maps into a Json Object a Tenant
     * @param model tenant that has the information to be converted
     * @return a json object created based the tenant
     */
    public static JsonObject map(Tenant model) {
        return TenantFactory.convertToJsonObject(model);
    }

    /**
     * Creates a list of tenants based a received inputted information
     * @param is inputted information to be converted into the object
     * @return a list of tenant object based in the received information
     */
    public static List<? extends Tenant> mapList(InputStream is) throws ParseException {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonArray jsonArray = jsonReader.readArray();
            return TenantFactory.convert(jsonArray);
        }
    }

    /**
     * Creates a tenant based a received inputted information
     * @param is inputted information to be converted into the object
     * @return a tenant object based in the received information
     */
    public static Tenant map(InputStream is) throws ParseException {
        try(JsonReader jsonReader = Json.createReader(is)) {
            return TenantFactory.convert(jsonReader.readObject());
        }
    }

    /**
     * Creates a tenants based a received inputted information
     * @param is inputted information to be converted into the object
     * @return a page of tenants object based in the received information
     */
    public static Page<Tenant> mapToPage(InputStream is) {
        Page<Tenant> page = null;
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonObject jsonObject = jsonReader.readObject();

            page = TenantFactory.convertJsonToPage(jsonObject);
        } catch (ParseException e) {
            log.error(e.getMessage(),e);
        }
        return page;
    }
}
