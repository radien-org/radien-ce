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
package io.radien.ms.rolemanagement.client.services;

import io.radien.api.entity.Page;
import io.radien.api.util.FactoryUtilService;
import io.radien.ms.rolemanagement.client.entities.TenantRoleUser;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for creating Tenant Role User associations (and read from/convert to json)
 *
 * @author Newton Carvalho
 */
public class TenantRoleUserFactory {

    /**
     * Create a Tenant Role User Association with already predefined fields.
     *
     * @param tenantRoleId tenant role id
     * @param userId user id
     * @param createdUser user which has created the association
     * @return Tenant Role User Association object to be used.
     */
    public static TenantRoleUser create(Long tenantRoleId, Long userId, Long createdUser) {
        TenantRoleUser tenantRoleUser = new TenantRoleUser();
        tenantRoleUser.setUserId(userId);
        tenantRoleUser.setTenantRoleId(tenantRoleId);
        tenantRoleUser.setCreateUser(createdUser);
        Date now = new Date();
        tenantRoleUser.setLastUpdate(now);
        tenantRoleUser.setCreateDate(now);

        return tenantRoleUser;
    }

    /**
     * Converts a JSONObject to a Tenant Role User Association object that will be used by the Application.
     *
     * @param jsonRole receives a json object with all the information.
     * @return a Tenant Role User Association object constructed by the given json.
     */
    public static TenantRoleUser convert(JsonObject jsonRole) {
        Long id = FactoryUtilService.getLongFromJson("id", jsonRole);
        Long tenantRoleId = FactoryUtilService.getLongFromJson("tenantRoleId", jsonRole);
        Long userId = FactoryUtilService.getLongFromJson("userId", jsonRole);
        Long createUser = FactoryUtilService.getLongFromJson("createUser", jsonRole);
        Long lastUpdateUser = FactoryUtilService.getLongFromJson("lastUpdateUser", jsonRole);

        TenantRoleUser tenantRoleUser = new TenantRoleUser();
        tenantRoleUser.setId(id);
        tenantRoleUser.setTenantRoleId(tenantRoleId);
        tenantRoleUser.setUserId(userId);
        tenantRoleUser.setCreateDate(new Date());
        tenantRoleUser.setLastUpdate(new Date());
        tenantRoleUser.setCreateUser(createUser);
        tenantRoleUser.setLastUpdateUser(lastUpdateUser);
        return tenantRoleUser;
    }

    /**
     * Converts a System Tenant Role User Association to a Json Object.
     * @param tenantRoleUser system Tenant Role User Association to be converted to json.
     * @return json object with the keys and values constructed by the given object.
     */
    public static JsonObject convertToJsonObject(TenantRoleUser tenantRoleUser) {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        FactoryUtilService.addValueLong(builder, "id", tenantRoleUser.getId());
        FactoryUtilService.addValueLong(builder, "tenantRoleId", tenantRoleUser.getTenantRoleId());
        FactoryUtilService.addValueLong(builder, "userId", tenantRoleUser.getUserId());
        FactoryUtilService.addValueLong(builder, "createUser", tenantRoleUser.getCreateUser());
        FactoryUtilService.addValueLong(builder, "lastUpdateUser", tenantRoleUser.getLastUpdateUser());
        FactoryUtilService.addValue(builder, "createDate", tenantRoleUser.getCreateDate());
        FactoryUtilService.addValue(builder, "lastUpdate", tenantRoleUser.getLastUpdate());
        return builder.build();
    }

    /**
     * Converts a json array into a list of Tenant Role User Associations to be used.
     * @param jsonArray from the request that should be read and used
     * @return a list of tenant user associations
     */
    public static List<TenantRoleUser> convert(JsonArray jsonArray) {
        return jsonArray.stream().map(i->convert(i.asJsonObject())).collect(Collectors.toList());
    }

    /**
     * Converts a JsonObject into a Tenant Role User Associations Page object
     * @param page the JsonObject to convert
     * @return the Page encapsulating information regarding Tenant User Associations
     */
    public static Page<TenantRoleUser> convertJsonToPage(JsonObject page) {
        int currentPage = FactoryUtilService.getIntFromJson("currentPage", page);
        JsonArray results = FactoryUtilService.getArrayFromJson("results", page);
        int totalPages = FactoryUtilService.getIntFromJson("totalPages", page);
        int totalResults = FactoryUtilService.getIntFromJson("totalResults", page);

        ArrayList<TenantRoleUser> pageResults = new ArrayList<>();

        if(results != null){
            for(int i = 0;i<results.size();i++){
                pageResults.add(convert(results.getJsonObject(i)));
            }
        }
        return new Page<>(pageResults,currentPage,totalResults,totalPages);
    }
}
