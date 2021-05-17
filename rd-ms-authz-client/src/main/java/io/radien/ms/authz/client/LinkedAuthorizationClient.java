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

    @GET
    @Path("/exists")
    Response existsSpecificAssociation(@QueryParam("tenantId") Long tenantId,
                                       @QueryParam("permissionId") Long permissionId,
                                       @QueryParam("roleId") Long roleId,
                                       @QueryParam("userId") Long userId,
                                       @DefaultValue("true") @QueryParam("isLogicalConjunction") boolean isLogicalConjunction);

    @GET
    @Path("/exists/role")
    Response isRoleExistentForUser(@QueryParam("userId") Long userId,
                                   @QueryParam("roleName") String roleName,
                                   @QueryParam("tenantId") Long tenantId);

    @GET
    @Path("/exists/checkPermissions")
    Response checkPermissions(@QueryParam("userId") Long userId,
                                   @QueryParam("roleName") List<String> roleName,
                                   @QueryParam("tenantId") Long tenantId);
}
