/*
 *
 *  * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
 *  * <p>
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  * <p>
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  * <p>
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package io.radien.ms.ecm.client.controller;

import io.radien.api.model.i18n.SystemI18NProperty;
import io.radien.ms.ecm.client.entities.i18n.DeletePropertyFilter;
import java.io.Serializable;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;

import io.radien.ms.ecm.client.entities.GlobalHeaders;

@Path("i18n")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RegisterClientHeaders(GlobalHeaders.class)
public interface I18NResource extends Serializable{

    @GET
    Response getAll(@QueryParam("application") String application,
                    @DefaultValue("1") @QueryParam("pageNo") int pageNo,
                    @DefaultValue("10") @QueryParam("pageSize") int pageSize,
                    @QueryParam("sortBy") List<String> sortBy,
                    @DefaultValue("true") @QueryParam("asc") boolean isAscending);

    @GET
    @Path("/property")
    Response findByKeyAndApplication(@QueryParam("key") String key,
                                     @QueryParam("application") String application);

    @GET
    @Path("/properties")
    Response findAllByApplication(@QueryParam("application") String application);

    @GET
    @Path("/properties/{language}")
    Response findAllByApplicationAndLanguage(@QueryParam("application") String application,
                                             @PathParam("language") String language);

    @DELETE
    Response deleteProperties(DeletePropertyFilter filter);

    @POST
    Response saveProperty(SystemI18NProperty property);

    @GET
    @Path("/translation")
    Response getMessage(@QueryParam("key") String key,
             @QueryParam("application") String application,
             @QueryParam("language") String language);


}
