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

import io.radien.api.util.FactoryUtilService;
import io.radien.ms.rolemanagement.client.entities.LinkedAuthorization;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Bruno Gama
 */
public class LinkedAuthorizationFactory {

    /**
     * Create a Linked Authorization with already predefined fields.
     *
     * @param tenantId tenant id
     * @param permissionId permission id
     * @param roleId role id
     * @param userId identifier that associates a radien user with a user and a permission
     * @param createdUser user which has created the association
     * @return Linked Authorization object to be used.
     */
    public static LinkedAuthorization create(Long tenantId, Long permissionId, Long roleId, Long userId, Long createdUser) {
        LinkedAuthorization tenancyCtrl = new LinkedAuthorization();

        tenancyCtrl.setTenantId(tenantId);
        tenancyCtrl.setPermissionId(permissionId);
        tenancyCtrl.setRoleId(roleId);
        tenancyCtrl.setUserId(userId);
        tenancyCtrl.setCreateUser(createdUser);
        Date now = new Date();
        tenancyCtrl.setLastUpdate(now);
        tenancyCtrl.setCreateDate(now);

        return tenancyCtrl;
    }

    /**
     * Converts a JSONObject to a Linked Authorization object that will be used by the Application.
     *
     * @param jsonRole receives a json object with all the information.
     * @return a Linked Authorization object constructed by the given json.
     */
    public static LinkedAuthorization convert(JsonObject jsonRole) {
        Long id = FactoryUtilService.getLongFromJson("id", jsonRole);
        Long tenantId = FactoryUtilService.getLongFromJson("tenantId", jsonRole);
        Long permissionId = FactoryUtilService.getLongFromJson("permissionId", jsonRole);
        Long roleId = FactoryUtilService.getLongFromJson("roleId", jsonRole);
        Long userId = FactoryUtilService.getLongFromJson("userId", jsonRole);

        LinkedAuthorization tenancyCtrl = new LinkedAuthorization();
        tenancyCtrl.setId(id);
        tenancyCtrl.setTenantId(tenantId);
        tenancyCtrl.setPermissionId(permissionId);
        tenancyCtrl.setRoleId(roleId);
        tenancyCtrl.setUserId(userId);
        tenancyCtrl.setCreateDate(new Date());
        tenancyCtrl.setLastUpdate(new Date());

        return tenancyCtrl;
    }

    /**
     * Converts a System Linked Authorization to a Json Object.
     *
     * @param tenancyCtrl system Linked Authorization to be converted to json.
     * @return json object with the keys and values constructed by the given object.
     */
    public static JsonObject convertToJsonObject(LinkedAuthorization tenancyCtrl) {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        FactoryUtilService.addValue(builder, "id", tenancyCtrl.getId());
        FactoryUtilService.addValueLong(builder, "tenantId", tenancyCtrl.getTenantId());
        FactoryUtilService.addValueLong(builder, "permissionId", tenancyCtrl.getPermissionId());
        FactoryUtilService.addValueLong(builder, "roleId", tenancyCtrl.getRoleId());
        FactoryUtilService.addValueLong(builder, "userId", tenancyCtrl.getUserId());
        FactoryUtilService.addValueLong(builder, "createUser", tenancyCtrl.getCreateUser());
        FactoryUtilService.addValueLong(builder, "lastUpdateUser", tenancyCtrl.getLastUpdateUser());

        return builder.build();
    }

    /**
     * Converts a json array into a list of Linked Authorizations to be used.
     * @param jsonArray from the request that should be read and used
     * @return a list of linked authorizations
     */
    public static List<LinkedAuthorization> convert(JsonArray jsonArray) {
        return jsonArray.stream().map(i->convert(i.asJsonObject())).collect(Collectors.toList());
    }
}
