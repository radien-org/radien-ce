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
package io.radien.ms.rolemanagement.client.services;

import io.radien.api.entity.Page;
import io.radien.api.util.FactoryUtilService;
import io.radien.ms.rolemanagement.client.entities.TenantRolePermission;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for creating Tenant Role Permission associations (and read from/convert to json)
 *
 * @author Newton Carvalho
 */
public class TenantRolePermissionFactory {

    /**
     * Create a Tenant Role Permission Association with already predefined fields.
     *
     * @param tenantRoleId tenant role id
     * @param permissionId permission id
     * @param createdUser user which has created the association
     * @return Tenant Role Permission Association object to be used.
     */
    public static TenantRolePermission create(Long tenantRoleId, Long permissionId, Long createdUser) {
        TenantRolePermission tenantRole = new TenantRolePermission();
        tenantRole.setPermissionId(permissionId);
        tenantRole.setTenantRoleId(tenantRoleId);
        tenantRole.setCreateUser(createdUser);
        Date now = new Date();
        tenantRole.setLastUpdate(now);
        tenantRole.setCreateDate(now);

        return tenantRole;
    }

    /**
     * Converts a JSONObject to a Tenant Role Permission Association object that will be used by the Application.
     *
     * @param jsonRole receives a json object with all the information.
     * @return a Tenant Role Permission Association object constructed by the given json.
     */
    public static TenantRolePermission convert(JsonObject jsonRole) {
        Long id = FactoryUtilService.getLongFromJson("id", jsonRole);
        Long tenantRoleId = FactoryUtilService.getLongFromJson("tenantRoleId", jsonRole);
        Long permissionId = FactoryUtilService.getLongFromJson("permissionId", jsonRole);
        Long createUser = FactoryUtilService.getLongFromJson("createUser", jsonRole);
        Long lastUpdateUser = FactoryUtilService.getLongFromJson("lastUpdateUser", jsonRole);

        TenantRolePermission tenantRolePermission = new TenantRolePermission();
        tenantRolePermission.setId(id);
        tenantRolePermission.setTenantRoleId(tenantRoleId);
        tenantRolePermission.setPermissionId(permissionId);
        tenantRolePermission.setCreateDate(new Date());
        tenantRolePermission.setLastUpdate(new Date());
        tenantRolePermission.setCreateUser(createUser);
        tenantRolePermission.setLastUpdateUser(lastUpdateUser);

        return tenantRolePermission;
    }

    /**
     * Converts a System Tenant Role Permission Association to a Json Object.
     * @param tenantRolePermission system Tenant Role Permission Association to be converted to json.
     * @return json object with the keys and values constructed by the given object.
     */
    public static JsonObject convertToJsonObject(TenantRolePermission tenantRolePermission) {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        FactoryUtilService.addValueLong(builder, "id", tenantRolePermission.getId());
        FactoryUtilService.addValueLong(builder, "tenantRoleId", tenantRolePermission.getTenantRoleId());
        FactoryUtilService.addValueLong(builder, "permissionId", tenantRolePermission.getPermissionId());
        FactoryUtilService.addValueLong(builder, "createUser", tenantRolePermission.getCreateUser());
        FactoryUtilService.addValueLong(builder, "lastUpdateUser", tenantRolePermission.getLastUpdateUser());
        FactoryUtilService.addValue(builder, "createDate", tenantRolePermission.getCreateDate());
        FactoryUtilService.addValue(builder, "lastUpdate", tenantRolePermission.getLastUpdate());
        return builder.build();
    }

    /**
     * Converts a json array into a list of Tenant Role Permission Associations to be used.
     * @param jsonArray from the request that should be read and used
     * @return a list of tenant user associations
     */
    public static List<TenantRolePermission> convert(JsonArray jsonArray) {
        return jsonArray.stream().map(i->convert(i.asJsonObject())).collect(Collectors.toList());
    }

    /**
     * Converts a JsonObject into a Tenant Role Permission Associations Page object
     * @param page the JsonObject to convert
     * @return the Page encapsulating information regarding Tenant User Associations
     */
    public static Page<TenantRolePermission> convertJsonToPage(JsonObject page) {
        int currentPage = FactoryUtilService.getIntFromJson("currentPage", page);
        JsonArray results = FactoryUtilService.getArrayFromJson("results", page);
        int totalPages = FactoryUtilService.getIntFromJson("totalPages", page);
        int totalResults = FactoryUtilService.getIntFromJson("totalResults", page);

        ArrayList<TenantRolePermission> pageResults = new ArrayList<>();

        if(results != null){
            for(int i = 0;i<results.size();i++){
                pageResults.add(convert(results.getJsonObject(i)));
            }
        }
        return new Page<>(pageResults,currentPage,totalResults,totalPages);
    }
}
