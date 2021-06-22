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
import io.radien.ms.tenantmanagement.client.entities.ActiveTenant;
import io.radien.ms.tenantmanagement.client.services.ActiveTenantFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.InputStream;
import java.text.ParseException;

/**
 * Mapper from a given information into a JSON or a Active tenant
 * @author Bruno Gama
 */
public class ActiveTenantModelMapper {

    protected final static Logger log = LoggerFactory.getLogger(ActiveTenantModelMapper.class);

    /**
     * Maps into a Json Object a active Tenant
     * @param model active tenant that has the information to be converted
     * @return a json object created based the active tenant
     */
    public static JsonObject map(ActiveTenant model) {
        return ActiveTenantFactory.convertToJsonObject(model);
    }

    /**
     * Creates a active tenant based a received inputted information
     * @param is inputted information to be converted into the object
     * @return a active tenant object based in the received information
     */
    public static ActiveTenant map(InputStream is) throws ParseException {
        try(JsonReader jsonReader = Json.createReader(is)) {
            return ActiveTenantFactory.convert(jsonReader.readObject());
        }
    }

    /**
     * Creates a active tenants based a received inputted information
     * @param is inputted information to be converted into the object
     * @return a page of active tenants object based in the received information
     */
    public static Page<ActiveTenant> mapToPage(InputStream is) {
        Page<ActiveTenant> page = null;
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonObject jsonObject = jsonReader.readObject();

            page = ActiveTenantFactory.convertJsonToPage(jsonObject);
        } catch (ParseException e) {
            log.error(e.getMessage(),e);
        }
        return page;
    }
}
