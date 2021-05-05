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
package io.radien.ms.rolemanagement.client.services;

import io.radien.api.entity.Page;
import io.radien.api.util.FactoryUtilService;
import io.radien.ms.rolemanagement.client.entities.TenantRole;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for creating Tenant Role associations (and read from/convert to json)
 * @author Newton Carvalho
 */
public class TenantRoleFactory {

    /**
     * Create a Tenant Role Association with already predefined fields.
     *
     * @param tenantId tenant id
     * @param roleId role id
     * @param createdUser user which has created the association
     * @return Tenant Role Association object to be used.
     */
    public static TenantRole create(Long tenantId, Long roleId, Long createdUser) {
        TenantRole tenantRole = new TenantRole();

        tenantRole.setTenantId(tenantId);
        tenantRole.setRoleId(roleId);
        tenantRole.setCreateUser(createdUser);
        Date now = new Date();
        tenantRole.setLastUpdate(now);
        tenantRole.setCreateDate(now);

        return tenantRole;
    }

    /**
     * Converts a JSONObject to a Tenant Role Association object that will be used by the Application.
     *
     * @param jsonObject receives a json object with all the information.
     * @return a Tenant Role Association object constructed by the given json.
     */
    public static TenantRole convert(JsonObject jsonObject) {
        Long id = FactoryUtilService.getLongFromJson("id", jsonObject);
        Long tenantId = FactoryUtilService.getLongFromJson("tenantId", jsonObject);
        Long roleId = FactoryUtilService.getLongFromJson("roleId", jsonObject);
        Long createUser = FactoryUtilService.getLongFromJson("createUser", jsonObject);
        Long lastUpdateUser = FactoryUtilService.getLongFromJson("lastUpdateUser", jsonObject);
        TenantRole tenantRole = new TenantRole();
        tenantRole.setId(id);
        tenantRole.setTenantId(tenantId);
        tenantRole.setRoleId(roleId);
        tenantRole.setCreateDate(new Date());
        tenantRole.setLastUpdate(new Date());
        tenantRole.setCreateUser(createUser);
        tenantRole.setLastUpdateUser(lastUpdateUser);

        return tenantRole;
    }

    /**
     * Converts a System Tenant Role Association to a Json Object.
     * @param tenantRole system Tenant Role Association to be converted to json.
     * @return json object with the keys and values constructed by the given object.
     */
    public static JsonObject convertToJsonObject(TenantRole tenantRole) {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        FactoryUtilService.addValueLong(builder, "id", tenantRole.getId());
        FactoryUtilService.addValueLong(builder, "tenantId", tenantRole.getTenantId());
        FactoryUtilService.addValueLong(builder, "roleId", tenantRole.getRoleId());
        FactoryUtilService.addValueLong(builder, "createUser", tenantRole.getCreateUser());
        FactoryUtilService.addValueLong(builder, "lastUpdateUser", tenantRole.getLastUpdateUser());
        FactoryUtilService.addValue(builder, "createDate", tenantRole.getCreateDate());
        FactoryUtilService.addValue(builder, "lastUpdate", tenantRole.getLastUpdate());

        return builder.build();
    }

    /**
     * Converts a json array into a list of Tenant Role Associations to be used.
     * @param jsonArray from the request that should be read and used
     * @return a list of tenant user associations
     */
    public static List<TenantRole> convert(JsonArray jsonArray) {
        return jsonArray.stream().map(i->convert(i.asJsonObject())).collect(Collectors.toList());
    }

    /**
     * Converts a JsonObject into a Tenant Role Associations Page object
     * @param page the JsonObject to convert
     * @return the Page encapsulating information regarding Tenant User Associations
     */
    public static Page<TenantRole> convertJsonToPage(JsonObject page) {
        int currentPage = FactoryUtilService.getIntFromJson("currentPage", page);
        JsonArray results = FactoryUtilService.getArrayFromJson("results", page);
        int totalPages = FactoryUtilService.getIntFromJson("totalPages", page);
        int totalResults = FactoryUtilService.getIntFromJson("totalResults", page);

        ArrayList<TenantRole> pageResults = new ArrayList();

        if(results != null){
            for(int i = 0;i<results.size();i++){
                pageResults.add(convert(results.getJsonObject(i)));
            }
        }
        return new Page<>(pageResults,currentPage,totalResults,totalPages);
    }
}
