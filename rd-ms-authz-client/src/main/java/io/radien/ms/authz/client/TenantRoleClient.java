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
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 * REST client created to decouple the current subproject
 * in relation of role management (client module)
 *
 * @author Newton Carvalho
 */
@RegisterRestClient
@RegisterClientHeaders(GlobalHeaders.class)
@RegisterProvider(ExceptionMapper.class)
@Path("/tenantrole")
public interface TenantRoleClient {

    /**
     * Check if Role exists for a User (Optionally under a Tenant)
     * @param userId User identifier
     * @param roleName Role name identifier
     * @param tenantId Tenant identifier (Optional)
     * @return Response OK containing a boolean value (true if role is associated to an User, otherwise false).
     * Response 404 in case of absence of parameter like user identifier or role name.
     * Response 500 in case of any error
     */
    @GET
    @Path("/exists/role")
    Response isRoleExistentForUser(@QueryParam("userId") Long userId,
                                   @QueryParam("roleName") String roleName,
                                   @QueryParam("tenantId") Long tenantId);

    /**
     * Check if Permission exists for a User (Optionally under a Tenant)
     * @param userId User identifier
     * @param permissionId Permission identifier
     * @param tenantId Tenant identifier (Optional)
     * @return Response OK containing a boolean value (true if permission is associated to an User, otherwise false).
     * Response 404 in case of absence of parameter like user identifier or permission identifier.
     * Response 500 in case of any error
     */
    @GET
    @Path("/exists/permission")
    Response isPermissionExistentForUser(@QueryParam("userId") Long userId,
                                         @QueryParam("permissionId") Long permissionId,
                                         @QueryParam("tenantId") Long tenantId);

}
