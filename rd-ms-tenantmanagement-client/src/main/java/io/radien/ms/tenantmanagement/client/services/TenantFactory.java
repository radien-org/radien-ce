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
import io.radien.ms.tenantmanagement.client.entities.Tenant;
import io.radien.ms.tenantmanagement.client.util.FactoryUtilService;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Bruno Gama
 */
public class TenantFactory {

    private final static String DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";

    /**
     * Create a tenant with already predefine fields.
     *
     * @param name tenant name
     * @param createdUser the user which has created the contract
     * @return a Contract object to be used
     */
    public static Tenant create(String name, Long createdUser){
        Tenant tenant = new Tenant();
        tenant.setName(name);

        tenant.setCreateUser(createdUser);
        tenant.setLastUpdateUser(createdUser);
        Date now = new Date();
        tenant.setLastUpdate(now);
        tenant.setCreateDate(now);
        return tenant;
    }

    /**
     * Converts a JSONObject to a SystemUser object Used by the Application
     * DataInit to seed Data in the database
     *
     * @param jsonContract the JSONObject to convert
     * @return the Contract Object
     */
    public static Tenant convert(JsonObject jsonContract) throws ParseException {
        Long id = FactoryUtilService.getLongFromJson("id", jsonContract);
        String name = FactoryUtilService.getStringFromJson("name", jsonContract);
        Long createUser = FactoryUtilService.getLongFromJson("createUser", jsonContract);
        Long lastUpdateUser = FactoryUtilService.getLongFromJson("lastUpdateUser", jsonContract);
        String createDate = FactoryUtilService.getStringFromJson("createDate", jsonContract);
        String lastUpdate = FactoryUtilService.getStringFromJson("lastUpdate", jsonContract);
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        Tenant tenant = create(name,createUser);
        tenant.setId(id);
        tenant.setLastUpdateUser(lastUpdateUser);
        if(createDate != null) {
            tenant.setCreateDate(formatter.parse(createDate));
        } else {
          tenant.setCreateDate(null);
        }
        if(lastUpdate != null) {
            tenant.setLastUpdate(formatter.parse(lastUpdate));
        } else {
            tenant.setLastUpdate(null);
        }
        return tenant;
    }

    /**
     * Converts a System user to a Json Object
     *
     * @param tenant system user to be converted to json
     * @return json object with keys and values constructed
     */
    public static JsonObject convertToJsonObject(Tenant tenant) {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        FactoryUtilService.addValueLong(builder, "id", tenant.getId());
        FactoryUtilService.addValue(builder, "name", tenant.getName());
        FactoryUtilService.addValueLong(builder, "createUser", tenant.getCreateUser());
        FactoryUtilService.addValueLong(builder, "lastUpdateUser", tenant.getLastUpdateUser());
        FactoryUtilService.addValue(builder, "createDate", tenant.getCreateDate());
        FactoryUtilService.addValue(builder, "lastUpdate", tenant.getLastUpdate());

        return  builder.build();
    }


    public static List<Tenant> convert(JsonArray jsonArray) throws ParseException{
        List<Tenant> list = new ArrayList<>();
        for (JsonValue i : jsonArray) {
            Tenant convert = convert(i.asJsonObject());
            list.add(convert);
        }
        return list;
    }

    /**
     * Converts a JsonObject into a Permission Page object
     * @param page the JsonObject to convert
     * @return the Page encapsulating information regarding permissions
     */
    public static Page<Tenant> convertJsonToPage(JsonObject page) throws ParseException {
        int currentPage = io.radien.api.util.FactoryUtilService.getIntFromJson("currentPage", page);
        JsonArray results = io.radien.api.util.FactoryUtilService.getArrayFromJson("results", page);
        int totalPages = io.radien.api.util.FactoryUtilService.getIntFromJson("totalPages", page);
        int totalResults = io.radien.api.util.FactoryUtilService.getIntFromJson("totalResults", page);

        ArrayList<Tenant> pageResults = new ArrayList();

        if(results != null){
            for(int i = 0;i<results.size();i++){
                pageResults.add(convert(results.getJsonObject(i)));
            }
        }
        return new Page<>(pageResults,currentPage,totalResults,totalPages);
    }

}
