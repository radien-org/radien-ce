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
import io.radien.api.service.ecm.exception.ContentNotAvailableException;
import io.radien.api.service.ecm.exception.ContentRepositoryNotAvailableException;
import io.radien.api.service.ecm.exception.ElementNotFoundException;
import io.radien.api.service.ecm.model.ContentType;
import io.radien.api.service.ecm.model.EnterpriseContent;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.GenericErrorMessagesToResponseMapper;
import io.radien.exception.SystemException;
import io.radien.ms.ecm.client.controller.ContentResource;
import io.radien.ms.ecm.client.entities.DeleteContentFilter;
import java.text.MessageFormat;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;
import software.amazon.awssdk.utils.StringUtils;

public class ContentResourceClient implements ContentResource {

    private static final Logger log = LoggerFactory.getLogger(ContentResourceClient.class);

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
            log.error(GenericErrorCodeMessage.REPOSITORY_NOT_AVAILABLE.toString(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(GenericErrorCodeMessage.REPOSITORY_NOT_AVAILABLE + e.getMessage())
                    .build();
        }
    }

    public Response getContentByViewIdLanguage(String viewId, String language) {
        List<EnterpriseContent> resultList = contentServiceAccess.getByViewIdLanguage(viewId, true, language);
        if(!resultList.isEmpty()) {
            EnterpriseContent result = resultList.get(0);
            if(result.getContentType().equals(ContentType.ERROR)) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(GenericErrorCodeMessage.NOT_FOUND_VIEWID_LANGUAGE.toString(viewId, language))
                        .build();
            }
            return Response.ok(result).build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(GenericErrorCodeMessage.NOT_FOUND_VIEWID_LANGUAGE.toString(viewId, language))
                .build();
    }

    public Response getFolderContents(String path) {
        try {
            return Response.ok(contentServiceAccess.getFolderContents(path))
                    .build();
        } catch (ContentRepositoryNotAvailableException e) {
            log.error(GenericErrorCodeMessage.REPOSITORY_NOT_AVAILABLE.toString(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(GenericErrorCodeMessage.REPOSITORY_NOT_AVAILABLE + e.getMessage())
                    .build();
        } catch(ContentNotAvailableException e) {
            log.error(GenericErrorCodeMessage.ERROR_RETRIEVING_FOLDER_CONTENTS.toString(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(GenericErrorCodeMessage.ERROR_RETRIEVING_FOLDER_CONTENTS + e.getMessage())
                    .build();
        }
    }

    public Response getOrCreateDocumentsPath(String path) {
        try {
            return Response.ok(contentServiceAccess.getOrCreateDocumentsPath(path))
                    .build();
        } catch (ContentRepositoryNotAvailableException e) {
            log.error(GenericErrorCodeMessage.REPOSITORY_NOT_AVAILABLE.toString(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(GenericErrorCodeMessage.REPOSITORY_NOT_AVAILABLE + e.getMessage())
                    .build();
        } catch (ContentNotAvailableException e) {
            log.error(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage())
                    .build();
        }
    }

    public Response saveContent(EnterpriseContent content) {
        try {
            contentServiceAccess.save(content);
            return Response.ok().build();
        } catch (ContentRepositoryNotAvailableException e) {
            log.error(GenericErrorCodeMessage.REPOSITORY_NOT_AVAILABLE.toString(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(GenericErrorCodeMessage.REPOSITORY_NOT_AVAILABLE + e.getMessage())
                    .build();
        } catch (ContentNotAvailableException e) {
            log.error(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage())
                    .build();
        }
    }

    public Response deleteContent(DeleteContentFilter deleteContentFilter) {
        if(!StringUtils.isEmpty(deleteContentFilter.getViewId()) && !StringUtils.isEmpty(deleteContentFilter.getAbsoluteJcrPath())) {
            return GenericErrorMessagesToResponseMapper.getInvalidRequestResponse(GenericErrorCodeMessage.ERROR_INVALID_CMS_FILTER_OBJECT.toString());
        }

        if(!StringUtils.isEmpty(deleteContentFilter.getViewId())) {
            return deleteContentByViewIdLanguage(deleteContentFilter.getViewId(), deleteContentFilter.getLanguage());
        } else if(!StringUtils.isEmpty(deleteContentFilter.getAbsoluteJcrPath())) {
            return deleteContentByJcrPath(deleteContentFilter.getAbsoluteJcrPath());
        } else {
            return GenericErrorMessagesToResponseMapper.getInvalidRequestResponse(GenericErrorCodeMessage.ERROR_INVALID_CMS_FILTER_OBJECT.toString());
        }
    }

    private Response deleteContentByViewIdLanguage(String viewId, String language) {
        try {
            List<EnterpriseContent> byViewIdLanguage = contentServiceAccess.getByViewIdLanguage(viewId,true,language);
            for (EnterpriseContent obj : byViewIdLanguage) {
                contentServiceAccess.delete(obj);
            }
            return Response.ok().build();
        } catch (ContentRepositoryNotAvailableException e) {
            log.error(GenericErrorCodeMessage.REPOSITORY_NOT_AVAILABLE.toString(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(GenericErrorCodeMessage.REPOSITORY_NOT_AVAILABLE + e.getMessage())
                    .build();
        } catch (ContentNotAvailableException e) {
            log.error(GenericErrorCodeMessage.ERROR_DELETE_VIEWID_LANGUAGE.toString(viewId, language), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(GenericErrorCodeMessage.ERROR_DELETE_VIEWID_LANGUAGE.toString(viewId, language) + e.getMessage())
                    .build();
        }
    }

    private Response deleteContentByJcrPath(String path) {
        try {
            contentServiceAccess.delete(path);
            return Response.ok().build();
        } catch (SystemException e) {
            log.error(e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(GenericErrorCodeMessage.ERROR_DELETE_PATH.toString(path) + e.getMessage())
                    .build();
        }
    }

}
