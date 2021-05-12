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
package io.radien.ms.rolemanagement.factory;

import io.radien.api.model.linked.authorization.SystemLinkedAuthorization;
import io.radien.api.util.FactoryUtilService;
import io.radien.ms.rolemanagement.entities.LinkedAuthorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.Serializable;
import java.util.Date;

/**
 * Linked Authorization Factory where we can construct a linked authorization object
 * by a given set of elements
 *
 * @author Bruno Gama
 */
public class LinkedAuthorizationFactory implements Serializable {

    private static final long serialVersionUID = 2027649289777530394L;

    private static final Logger log = LoggerFactory.getLogger(LinkedAuthorizationFactory.class);

    /**
     * Create a association with already predefined fields.
     *
     * @param tenantId tenant id value
     * @param permissionId permission id value
     * @param roleId role id value
     * @param createdUser created user value
     * @return a LinkedAuthorization object to be used.
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

        log.info("Will begin to create a new Linked Authorization object with the specific values" +
                " Tenant Id: {}, Permission Id: {}, Role Id: {}" +
                " User Id: {}" + tenantId, permissionId, roleId, userId);

        return tenancyCtrl;
    }

    /**
     *  Converts a JSONObject to a LinkedAuthorization object that will be used by the Application.
     * @param jsonRole receives a json object with all the information.
     * @return a LinkedAuthorization object constructed by the given json.
     */
    public static LinkedAuthorization convert(JsonObject jsonRole) {
        Long id = FactoryUtilService.getLongFromJson("id", jsonRole);
        Long tenantId = FactoryUtilService.getLongFromJson("tenantId", jsonRole);
        Long permissionId = FactoryUtilService.getLongFromJson("permissionId", jsonRole);
        Long roleId = FactoryUtilService.getLongFromJson("roleId", jsonRole);
        Long userId = FactoryUtilService.getLongFromJson("userId", jsonRole);
        Long createUser = FactoryUtilService.getLongFromJson("createdUser", jsonRole);

        LinkedAuthorization tenancyCtrl = new LinkedAuthorization();
        tenancyCtrl.setId(id);
        tenancyCtrl.setRoleId(roleId);
        tenancyCtrl.setPermissionId(permissionId);
        tenancyCtrl.setTenantId(tenantId);
        tenancyCtrl.setUserId(userId);
        tenancyCtrl.setCreateUser(createUser);
        Date date = new Date();
        tenancyCtrl.setCreateDate(date);
        tenancyCtrl.setLastUpdate(date);

        log.info("Will begin to create a new Linked Authorization object with the specific values" +
                " Id: {}, Tenant Id {}, Permission Id: {}, Role Id: {}" +
                " User Id: {}" + id, tenantId, permissionId, roleId, userId);

        return tenancyCtrl;
    }

    /**
     * Converts a System LinkedAuthorization to a Json Object.
     *
     * @param systemTenancyCtrl system linked authorization to be converted to json.
     * @return json object with the keys and values constructed by the given object.
     */
    public static JsonObject convertToJsonObject(SystemLinkedAuthorization systemTenancyCtrl) {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        FactoryUtilService.addValueLong(builder, "id", systemTenancyCtrl.getId());
        FactoryUtilService.addValueLong(builder, "tenantId", systemTenancyCtrl.getTenantId());
        FactoryUtilService.addValueLong(builder, "permissionId", systemTenancyCtrl.getPermissionId());
        FactoryUtilService.addValueLong(builder, "roleId", systemTenancyCtrl.getRoleId());
        FactoryUtilService.addValueLong(builder, "userId", systemTenancyCtrl.getUserId());
        FactoryUtilService.addValueLong(builder, "createdUser", systemTenancyCtrl.getCreateUser());

        return builder.build();
    }
}
