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
import io.radien.ms.tenantmanagement.client.entities.Tenant;
import io.radien.ms.tenantmanagement.client.entities.TenantType;
import org.apache.cxf.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Tenant factory for mapping and converting given information
 *
 * @author Bruno Gama
 */
public class TenantFactory {

    private static final String DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";

    private static Logger log = LoggerFactory.getLogger(TenantFactory.class);

    /**
     * Create a tenant with already predefine fields.
     * @param name of the tenant
     * @param key value of the tenant
     * @param type of the tenant
     * @param start date of the tenant
     * @param end date of the tenant
     * @param clientAddress in case of type being a client tenant client address
     * @param clientZipCode in case of type being a client tenant client address zip code
     * @param clientCity in case of type being a client tenant client city
     * @param clientCountry in case of type being a client tenant client country
     * @param clientPhoneNumber in case of type being a client tenant client phone number
     * @param clientEmail in case of type being a client tenant client email address
     * @param parentId tenant id to whom this tenant is bellow
     * @param clientId tenant id of the client tenant this tenant belongs to
     * @param createdUser user which has given the command to create the tenant
     */
    public static Tenant create(String name, String key, TenantType type, LocalDate start, LocalDate end,
                                String clientAddress, String clientZipCode, String clientCity, String clientCountry,
                                Long clientPhoneNumber, String clientEmail, Long parentId, Long clientId, Long createdUser){
        Tenant tenant = new Tenant();
        tenant.setName(name);
        tenant.setTenantKey(key);
        tenant.setTenantType(type);
        tenant.setTenantStart(start);
        tenant.setTenantEnd(end);

        tenant.setClientAddress(clientAddress);
        tenant.setClientZipCode(clientZipCode);
        tenant.setClientCity(clientCity);
        tenant.setClientCountry(clientCountry);
        tenant.setClientPhoneNumber(clientPhoneNumber);
        tenant.setClientEmail(clientEmail);

        tenant.setParentId(parentId);
        tenant.setClientId(clientId);

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
     * @param jsonTenant the JSONObject to convert
     * @return the Contract Object
     * @throws ParseException in case of any issue while parsing the JSON
     */
    public static Tenant convert(JsonObject jsonTenant) throws ParseException {
        Long id = FactoryUtilService.getLongFromJson("id", jsonTenant);
        String name = FactoryUtilService.getStringFromJson("name", jsonTenant);
        String key = FactoryUtilService.getStringFromJson("tenantKey", jsonTenant);
        String start = FactoryUtilService.getStringFromJson("tenantStart", jsonTenant);
        String end = FactoryUtilService.getStringFromJson("tenantEnd", jsonTenant);

        TenantType type = getTypeFromJson(jsonTenant);

        String clientAddress = FactoryUtilService.getStringFromJson("clientAddress", jsonTenant);
        String clientZipCode = FactoryUtilService.getStringFromJson("clientZipCode", jsonTenant);
        String clientCity = FactoryUtilService.getStringFromJson("clientCity", jsonTenant);
        String clientCountry = FactoryUtilService.getStringFromJson("clientCountry", jsonTenant);
        Long clientPhoneNumber = FactoryUtilService.getLongFromJson("clientPhoneNumber", jsonTenant);
        String email = FactoryUtilService.getStringFromJson("clientEmail", jsonTenant);

        Long parent = FactoryUtilService.getLongFromJson("parentId", jsonTenant);
        Long clientId = FactoryUtilService.getLongFromJson("clientId", jsonTenant);

        Long createUser = FactoryUtilService.getLongFromJson("createUser", jsonTenant);
        Long lastUpdateUser = FactoryUtilService.getLongFromJson("lastUpdateUser", jsonTenant);
        String createDate = FactoryUtilService.getStringFromJson("createDate", jsonTenant);
        String lastUpdate = FactoryUtilService.getStringFromJson("lastUpdate", jsonTenant);

        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);

        Tenant tenant = create(name, key, type, null, null, clientAddress, clientZipCode, clientCity, clientCountry, clientPhoneNumber, email, parent, clientId, createUser);

        tenant.setId(id);

        if(start != null) {
            tenant.setTenantStart(LocalDate.parse(start));
        } else {
            tenant.setTenantStart(null);
        }

        if(end != null) {
            tenant.setTenantEnd(LocalDate.parse(end));
        } else {
            tenant.setTenantEnd(null);
        }

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
     * Extract and obtains SystemTenantType from Json
     * @param jsonTenant information to be set
     * @return a correct tenant type
     */
    private static TenantType getTypeFromJson(JsonObject jsonTenant) {
        String typeAsString = FactoryUtilService.getStringFromJson("tenantType", jsonTenant);
        if (StringUtils.isEmpty(typeAsString)) {
            throw new IllegalStateException("Field type is mandatory");
        }
        TenantType tenantType = TenantType.getByName(typeAsString);
        // TODO @Newton: Necessary to understand why TenantMessageBodyWriter is not being invoked
        if (tenantType == null) {
            log.error("No tenant type found for " + typeAsString);
            try {
                tenantType = TenantType.valueOf(typeAsString);
            } catch (IllegalArgumentException i) {
                log.error("No tenant type found for " + typeAsString + " via valueOf");
            }
        }
        if (tenantType == null) {
            throw new IllegalStateException("No tenant type could be found");
        }
        return tenantType;
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
        FactoryUtilService.addValue(builder, "tenantKey", tenant.getTenantKey());
        FactoryUtilService.addValue(builder, "tenantType", tenant.getTenantType().getName());
        FactoryUtilService.addValue(builder, "tenantStart", tenant.getTenantStart());
        FactoryUtilService.addValue(builder, "tenantEnd", tenant.getTenantEnd());

        FactoryUtilService.addValue(builder, "clientAddress", tenant.getClientAddress());
        FactoryUtilService.addValue(builder, "clientZipCode", tenant.getClientZipCode());
        FactoryUtilService.addValue(builder, "clientCity", tenant.getClientCity());
        FactoryUtilService.addValue(builder, "clientCountry", tenant.getClientCountry());
        FactoryUtilService.addValueLong(builder, "clientPhoneNumber", tenant.getClientPhoneNumber());
        FactoryUtilService.addValue(builder, "clientEmail", tenant.getClientEmail());

        FactoryUtilService.addValueLong(builder, "parentId", tenant.getParentId());
        FactoryUtilService.addValueLong(builder, "clientId", tenant.getClientId());

        FactoryUtilService.addValueLong(builder, "createUser", tenant.getCreateUser());
        FactoryUtilService.addValueLong(builder, "lastUpdateUser", tenant.getLastUpdateUser());
        FactoryUtilService.addValue(builder, "createDate", tenant.getCreateDate());
        FactoryUtilService.addValue(builder, "lastUpdate", tenant.getLastUpdate());

        return  builder.build();
    }

    /**
     * Will convert a JSON Array into a List of Tenants
     * @param jsonArray to be converted
     * @return the list of tenants
     * @throws ParseException in case of any issue while parsing the JSON
     */
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
     * @throws ParseException in case of any issue while parsing the JSON
     */
    public static Page<Tenant> convertJsonToPage(JsonObject page) throws ParseException {
        int currentPage = io.radien.api.util.FactoryUtilService.getIntFromJson("currentPage", page);
        JsonArray results = io.radien.api.util.FactoryUtilService.getArrayFromJson("results", page);
        int totalPages = io.radien.api.util.FactoryUtilService.getIntFromJson("totalPages", page);
        int totalResults = io.radien.api.util.FactoryUtilService.getIntFromJson("totalResults", page);

        ArrayList<Tenant> pageResults = new ArrayList<>();

        if(results != null){
            for(int i = 0;i<results.size();i++){
                pageResults.add(convert(results.getJsonObject(i)));
            }
        }
        return new Page<>(pageResults,currentPage,totalResults,totalPages);
    }

}
