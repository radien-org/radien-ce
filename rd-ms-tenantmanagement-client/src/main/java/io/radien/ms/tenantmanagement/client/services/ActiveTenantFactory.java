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
package io.radien.ms.tenantmanagement.client.services;

import io.radien.api.entity.Page;
import io.radien.api.util.FactoryUtilService;
import io.radien.ms.tenantmanagement.client.entities.ActiveTenant;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonValue;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Active Tenant factory for mapping and converting given information
 *
 * @author Bruno Gama
 */
public class ActiveTenantFactory {

    /**
     * Create a tenant with already predefine fields.
     * @param userId user id to whom this active tenant belongs
     * @param tenantId tenant id to whom this active tenant belongs and has active
     * @param tenantName tenant name to whom the active tenant belongs
     * @param isTenantActive if the selected record is active for the selected user
     */
    public static ActiveTenant create(Long userId, Long tenantId, String tenantName, boolean isTenantActive){
        ActiveTenant activeTenant = new ActiveTenant();
        activeTenant.setUserId(userId);
        activeTenant.setTenantId(tenantId);
        activeTenant.setTenantName(tenantName);
        activeTenant.setIsTenantActive(isTenantActive);

        return activeTenant;
    }

    /**
     * Converts a JSONObject to a SystemActiveTenant object Used by the Application
     * DataInit to seed Data in the database
     *
     * @param jsonTenant the JSONObject to convert
     * @return the Active Tenant Object
     * @throws ParseException in case of any issue while parsing the JSON
     */
    public static ActiveTenant convert(JsonObject jsonTenant) throws ParseException {
        Long id = FactoryUtilService.getLongFromJson("id", jsonTenant);
        Long userId = FactoryUtilService.getLongFromJson("userId", jsonTenant);
        Long tenantId = FactoryUtilService.getLongFromJson("tenantId", jsonTenant);
        String tenantName = FactoryUtilService.getStringFromJson("tenantName", jsonTenant);
        boolean isTenantActive = FactoryUtilService.getBooleanFromJson("isTenantActive", jsonTenant);

        ActiveTenant activeTenant = create(userId, tenantId, tenantName, isTenantActive);
        activeTenant.setId(id);

        return activeTenant;
    }

    /**
     * Converts a System active tenant to a Json Object
     *
     * @param activeTenant system active tenant to be converted to json
     * @return json object with keys and values constructed
     */
    public static JsonObject convertToJsonObject(ActiveTenant activeTenant) {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        FactoryUtilService.addValueLong(builder, "id", activeTenant.getId());
        FactoryUtilService.addValueLong(builder, "userId", activeTenant.getUserId());
        FactoryUtilService.addValueLong(builder, "tenantId", activeTenant.getTenantId());
        FactoryUtilService.addValue(builder, "tenantName", activeTenant.getTenantName());
        FactoryUtilService.addValueBoolean(builder, "isTenantActive", activeTenant.getIsTenantActive());

        return  builder.build();
    }

    /**
     * Will convert a JSON Array into a List of active Tenants
     * @param jsonArray to be converted
     * @return the list of active tenants
     * @throws ParseException in case of any issue while parsing the JSON
     */
    public static List<ActiveTenant> convert(JsonArray jsonArray) throws ParseException{
        List<ActiveTenant> list = new ArrayList<>();
        for (JsonValue i : jsonArray) {
            ActiveTenant convert = convert(i.asJsonObject());
            list.add(convert);
        }
        return list;
    }

    /**
     * Converts a JsonObject into a active tenant Page object
     * @param page the JsonObject to convert
     * @return the Page encapsulating information regarding permissions
     * @throws ParseException in case of any issue while parsing the JSON
     */
    public static Page<ActiveTenant> convertJsonToPage(JsonObject page) throws ParseException {
        int currentPage = io.radien.api.util.FactoryUtilService.getIntFromJson("currentPage", page);
        JsonArray results = io.radien.api.util.FactoryUtilService.getArrayFromJson("results", page);
        int totalPages = io.radien.api.util.FactoryUtilService.getIntFromJson("totalPages", page);
        int totalResults = io.radien.api.util.FactoryUtilService.getIntFromJson("totalResults", page);

        ArrayList<ActiveTenant> pageResults = new ArrayList<>();

        if(results != null){
            for(int i = 0;i<results.size();i++){
                pageResults.add(convert(results.getJsonObject(i)));
            }
        }
        return new Page<>(pageResults,currentPage,totalResults,totalPages);
    }

}
