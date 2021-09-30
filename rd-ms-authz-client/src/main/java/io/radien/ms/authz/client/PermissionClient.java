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
package io.radien.ms.authz.client;

import io.radien.ms.authz.client.exception.ExceptionMapper;
import io.radien.ms.openid.entities.GlobalHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.io.Serializable;

/**
 * REST client created to decouple the current subproject
 * in relation of user management (client module)
 *
 * @author Newton Carvalho
 */
@RegisterRestClient
@RegisterProvider(ExceptionMapper.class)
@RegisterClientHeaders(GlobalHeaders.class)
@Path("/permission")
public interface PermissionClient extends Serializable {

    /**
     * Retrieve the permission Id using the combination of resource and action as parameters
     * @param resource resource name (Mandatory)
     * @param action action name (Mandatory)
     * @return Response OK (200) with the retrieved id (if exists). If not exist will return 404 status.
     * In case of insufficient params (tenant or role not informed) It will return 400 status.
     * For any other kind of (unpredictable) error this endpoint will return status 500
     */
    @GET
    @Path("/id")
    public Response getIdByResourceAndAction(@QueryParam("resource") String resource, @QueryParam("action") String action);
}
