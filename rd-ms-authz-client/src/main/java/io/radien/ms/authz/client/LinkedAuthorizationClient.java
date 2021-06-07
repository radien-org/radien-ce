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
package io.radien.ms.authz.client;

import io.radien.ms.authz.client.exception.ExceptionMapper;
import io.radien.ms.openid.entities.GlobalHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.QueryParam;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * REST client created to decouple the current subproject
 * in relation of role management (client module)
 *
 * @author Newton Carvalho
 */
@RegisterRestClient
@RegisterClientHeaders(GlobalHeaders.class)
@RegisterProvider(ExceptionMapper.class)
@Path("/linkedauthorization")
public interface LinkedAuthorizationClient {

    /**
     * Rest request for validating if a specific association in the linked authorization does exist or not
     * by a given filtered parameters
     * @param tenantId tenant id in the linked authorizations
     * @param permissionId permission id in the linked authorizations
     * @param roleId role id in the linked authorizations
     * @param userId user id in the linked authorizations
     * @param isLogicalConjunction if the filtered values should be found as an and or a or. if true then it should be
     *                             found with and clause
     * @return the http request response
     */
    @GET
    @Path("/exists")
    Response existsSpecificAssociation(@QueryParam("tenantId") Long tenantId,
                                       @QueryParam("permissionId") Long permissionId,
                                       @QueryParam("roleId") Long roleId,
                                       @QueryParam("userId") Long userId,
                                       @DefaultValue("true") @QueryParam("isLogicalConjunction") boolean isLogicalConjunction);

    /**
     * Rest request to validate if a specific role does exist for a specific user
     * @param userId to be found
     * @param roleName to be found
     * @param tenantId for the specific tenant
     * @return the http request response
     */
    @GET
    @Path("/exists/role")
    Response isRoleExistentForUser(@QueryParam("userId") Long userId,
                                   @QueryParam("roleName") String roleName,
                                   @QueryParam("tenantId") Long tenantId);

    /**
     * Rest request to check if any permission does exist for a given parameters as for a specific user with a
     * specific role in a specific tenant
     * @param userId to be found
     * @param roleName to be found
     * @param tenantId to be found
     * @return the http request response
     */
    @GET
    @Path("/exists/checkPermissions")
    Response checkPermissions(@QueryParam("userId") Long userId,
                                   @QueryParam("roleName") List<String> roleName,
                                   @QueryParam("tenantId") Long tenantId);
}
