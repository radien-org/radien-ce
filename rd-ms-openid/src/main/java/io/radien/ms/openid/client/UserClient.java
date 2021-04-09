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
package io.radien.ms.openid.client;

import io.radien.ms.openid.entities.GlobalHeaders;
import io.radien.ms.openid.client.exception.ExceptionMapper;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 * REST client created to decouple the current subproject
 * in relation of user management (client module)
 *
 * @author Newton Carvalho
 */
@RegisterRestClient
@RegisterProvider(ExceptionMapper.class)
@RegisterClientHeaders(GlobalHeaders.class)
@Path("/user")
public interface UserClient {

    @GET
    @Path("/sub/{sub}")
    public Response getUserIdBySub(@PathParam("sub") String sub);
}
