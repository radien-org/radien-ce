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
package io.radien.ms.usermanagement.client.services;

import io.radien.ms.usermanagement.client.entities.GlobalHeaders;
import io.radien.ms.usermanagement.client.entities.JobshopItem;
import io.radien.ms.usermanagement.client.entities.StudentIdentity;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;

/**
 * User Management REST Requests and command curls
 *
 * @author Nuno Santana
 * @author Bruno Gama
 */
@Path("jobshop")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RegisterClientHeaders(GlobalHeaders.class)
public interface JobshopResourceClient {

    @GET
    @Path("/studentIdentity")
    Response getAllIdentities(@QueryParam("nameFilter") String nameFilter,
                    @DefaultValue("1")  @QueryParam("pageNo") int pageNo,
                    @DefaultValue("10") @QueryParam("pageSize") int pageSize);


    @POST
    @Path("/studentIdentity")
    Response saveIdentity(StudentIdentity studentIdentity);

    @GET
    @Path("/item")
    Response getAllItems();

    @POST
    @Path("/item")
    Response saveItem(JobshopItem jobshopItem);

    @POST
    @Path("/item/setup")
    Response saveItems(List<JobshopItem> jobshopItems);

}
