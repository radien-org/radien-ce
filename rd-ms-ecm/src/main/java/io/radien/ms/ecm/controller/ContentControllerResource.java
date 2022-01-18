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

package io.radien.ms.ecm.controller;

import io.radien.api.service.ecm.ContentServiceAccess;
import io.radien.api.service.ecm.exception.ContentRepositoryNotAvailableException;
import io.radien.api.service.ecm.exception.ElementNotFoundException;
import io.radien.api.service.ecm.model.EnterpriseContent;
import io.radien.exception.GenericErrorMessagesToResponseMapper;
import io.radien.exception.SystemException;
import io.radien.ms.ecm.client.controller.ContentController;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

public class ContentControllerResource implements ContentController {

    private static final Logger log = LoggerFactory.getLogger(ContentControllerResource.class);

    private static final long serialVersionUID = -8196891572077112658L;

    @Inject
    public ContentServiceAccess contentServiceAccess;

    public Response getContentFile(String jcrPath) {
        try {
            return Response.ok(contentServiceAccess.loadFile(jcrPath))
                    .build();
        } catch (ElementNotFoundException e) {
            log.error("Element not found", e);
            return GenericErrorMessagesToResponseMapper.getResourceNotFoundException();
        } catch (ContentRepositoryNotAvailableException e) {
            log.error("JCR not available", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("JCR Not available; " + e.getMessage())
                    .build();
        }
    }

    public Response getContentByViewIdLanguage(String viewId, String language) {
        return Response.ok(contentServiceAccess.getByViewIdLanguage(viewId, true, language).get(0))
                .build();
    }

    public Response getFolderContents(String path) {
        try {
            return Response.ok(contentServiceAccess.getFolderContents(path))
                    .build();
        } catch (Exception e) {
            log.error("Error retrieving folder contents", e);
            return Response.ok(Response.Status.INTERNAL_SERVER_ERROR).entity("Error retrieving folder contents; " + e.getMessage())
                    .build();
        }
    }

    public Response getOrCreateDocumentsPath(String path) {
        return Response.ok(contentServiceAccess.getOrCreateDocumentsPath(path))
                .build();
    }

    public Response saveContent(EnterpriseContent content) {
        try {
            contentServiceAccess.save(content);
            return Response.ok().build();
        } catch (ContentRepositoryNotAvailableException e) {
            log.error("JCR not available", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("JCR Not available; " + e.getMessage())
                    .build();
        }
    }

    public Response deleteByPath(String path) {
        try {
            contentServiceAccess.delete(path);
            return Response.ok().build();
        } catch (SystemException e) {
            log.error("Could not delete by path", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Could not delete by path; " + e.getMessage())
                    .build();
        }
    }

    public Response deleteContent(String viewId, String language) {
        try {
            List<EnterpriseContent> byViewIdLanguage = contentServiceAccess.getByViewIdLanguage(viewId,true,language);
            for (EnterpriseContent obj : byViewIdLanguage) {
                contentServiceAccess.delete(obj);
            }
            return Response.ok().build();
        } catch (ContentRepositoryNotAvailableException e) {
            log.error("JCR not available", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("JCR Not available; " + e.getMessage())
                    .build();
        } catch (SystemException e) {
            log.error("Could not delete documents by viewId and Language {} -- {}", viewId, language, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Could not delete documents by viewId and Language; " + e.getMessage())
                    .build();
        }
    }



}
