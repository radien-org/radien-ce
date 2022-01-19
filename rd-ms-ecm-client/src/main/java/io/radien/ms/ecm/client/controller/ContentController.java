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

import io.radien.api.service.ecm.model.EnterpriseContent;
import io.radien.ms.ecm.client.entities.GlobalHeaders;
import java.io.Serializable;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;

@Path("cms/content")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RegisterClientHeaders(GlobalHeaders.class)
public interface ContentController extends Serializable {

    @GET
    Response getContentFile(@QueryParam("path") String jcrPath);

    @POST
    Response saveContent(EnterpriseContent content);

    @DELETE
    Response deleteByPath(@QueryParam("path") String path);

    @GET
    @Path(value = "/metadata")
    Response getContentByViewIdLanguage(@QueryParam("viewId") String viewId,
                                        @QueryParam("lang") String language);

    @GET
    @Path(value = "/metadata/childNodes")
    Response getFolderContents(@QueryParam("path") String path);

    @GET
    @Path(value = "/path")
    Response getOrCreateDocumentsPath(@QueryParam("path") String path);

    @DELETE
    @Path(value = "/content")
    //TODO CHECK BACK
    Response deleteContent(@QueryParam("viewId") String viewId,
                           @QueryParam("lang") String lang);


}
