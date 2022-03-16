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
import io.radien.api.service.ecm.model.ContentType;
import io.radien.api.service.ecm.model.ContentVersion;
import io.radien.api.service.ecm.model.EnterpriseContent;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.GenericErrorMessagesToResponseMapper;
import io.radien.ms.ecm.client.controller.ContentResource;
import io.radien.ms.ecm.client.entities.DeleteContentFilter;
import io.radien.ms.ecm.config.ConfigHandler;
import io.radien.ms.openid.entities.Authenticated;
import javax.ws.rs.core.Response;

import javax.inject.Inject;
import java.util.List;
import software.amazon.awssdk.utils.StringUtils;

@Authenticated
public class ContentResourceClient implements ContentResource {

    private static final long serialVersionUID = -8196891572077112658L;

    @Inject
    private ContentServiceAccess contentServiceAccess;
    @Inject
    private ConfigHandler configHandler;

    public Response getContentFile(String jcrPath) {
        return Response.ok(contentServiceAccess.loadFile(jcrPath))
                .build();
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
        return Response.ok(contentServiceAccess.getFolderContents(path))
                .build();
    }

    public Response getOrCreateDocumentsPath(String client, String path) {
        if(StringUtils.isEmpty(client)) {
            client = configHandler.getDefaultClient();
        }
        return Response.ok(contentServiceAccess.getOrCreateDocumentsPath(client, path))
                .build();
    }

    public Response getContentVersions(String path) {
        return Response.ok(contentServiceAccess.getContentVersions(path))
                .build();
    }

    public Response deleteVersionable(String path, ContentVersion contentVersion) {
            contentServiceAccess.deleteVersion(path, contentVersion);
            return Response.ok().build();
    }

    public Response saveContent(String client, EnterpriseContent content) {
        if(StringUtils.isEmpty(client)) {
            client = configHandler.getDefaultClient();
        }
        contentServiceAccess.save(client, content);
        return Response.ok().build();
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
        List<EnterpriseContent> byViewIdLanguage = contentServiceAccess.getByViewIdLanguage(viewId,true,language);
        for (EnterpriseContent obj : byViewIdLanguage) {
            contentServiceAccess.delete(obj);
        }
        return Response.ok().build();
    }

    private Response deleteContentByJcrPath(String path) {
        contentServiceAccess.delete(path);
        return Response.ok().build();
    }

}
