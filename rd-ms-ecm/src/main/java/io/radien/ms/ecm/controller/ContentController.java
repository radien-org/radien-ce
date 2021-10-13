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
 *
 */

package io.radien.ms.ecm.controller;

import io.radien.api.service.ecm.ContentServiceAccess;
import io.radien.api.service.ecm.exception.ContentRepositoryNotAvailableException;
import io.radien.api.service.ecm.exception.ElementNotFoundException;
import io.radien.api.service.ecm.model.EnterpriseContent;
import io.radien.ms.ecm.client.entities.GlobalHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("cms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RegisterClientHeaders(GlobalHeaders.class)
public class ContentController {

    private static final Logger log = LoggerFactory.getLogger(ContentController.class);

    @Inject
    public ContentServiceAccess contentServiceAccess;

    @GET
    @Path("/content/files")
    public EnterpriseContent getContentFile(@QueryParam("path") String jcrPath) {
        try {
            return contentServiceAccess.loadFile(jcrPath);
        } catch (ElementNotFoundException e) {
            log.error("Element not found", e);
        } catch (ContentRepositoryNotAvailableException e) {
            log.error("JCR not available", e);
        }
        return null;//Afterwards replace for proper message
    }



}
