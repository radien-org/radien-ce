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
package io.radien.ms.ecm.service;

import com.fasterxml.jackson.core.TreeNode;
import io.radien.api.service.ecm.ContentServiceAccess;
import io.radien.api.service.ecm.exception.ContentRepositoryNotAvailableException;
import io.radien.api.service.ecm.exception.ElementNotFoundException;
import io.radien.api.service.ecm.model.ContentType;
import io.radien.api.service.ecm.model.EnterpriseContent;
import io.radien.api.service.mail.model.MailType;
import io.radien.ms.ecm.factory.ContentFactory;
import io.radien.ms.ecm.ContentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of the ContentService using JackRabbit
 *
 * @author Bruno Gama
 */
public @RequestScoped
class ContentService implements ContentServiceAccess {

    private static final Logger log = LoggerFactory.getLogger(ContentService.class);
    private static final long serialVersionUID = 8354030307902734111L;

    @Inject
    private ContentFactory factory;

    @Inject
    private ContentRepository contentRepository;

    public List<EnterpriseContent> getChildrenFiles(String viewId) {
        List<EnterpriseContent> results = new ArrayList<>();
        try {
            results.addAll(contentRepository.getChildren(viewId));
        } catch (ContentRepositoryNotAvailableException | ElementNotFoundException e) {
            log.error("Error getting children files", e);
        }

        return results;
    }

    @Override
    public String getNotificationIdByTypeAndLanguage(MailType type, String languageCode) {
        return null;
    }

    @Override
    public String getAppDesc(String app, String language) {
        return null;
    }

    @Override
    public int countByTagName(String name) {
        return 0;
    }

    @Override
    public String getAppInfoId(String content, String app, String language) {
        return null;
    }

    @Override
    public EnterpriseContent loadFile(String jcrPath) throws ElementNotFoundException, ContentRepositoryNotAvailableException {
        return null;
    }

    @Override
    public Map<String, String> getAppDescriptions(String language) {
        return null;
    }

    @Override
    public List<EnterpriseContent> getFolderContents(String path) {
        return null;
    }

    @Override
    public List<EnterpriseContent> getContentVersions(String path) {
        return null;
    }

    @Override
    public String getOrCreateDocumentsPath(String path) {
        return null;
    }

    @Override
    public EnterpriseContent getByViewId(String viewId, boolean activeOnly) {
        EnterpriseContent content = null;
        try {
            content = contentRepository.getByViewId(viewId, activeOnly);
        } catch (ContentRepositoryNotAvailableException | ElementNotFoundException e) {
            // log.error("Error getting view by id", e);
        }
        return content;
    }

    public void save(EnterpriseContent obj) {
        try {
            contentRepository.save(obj);
        } catch (Exception e) {
            throw e;
        }
    }

    public EnterpriseContent loadFile(EnterpriseContent content)
            throws ElementNotFoundException, ContentRepositoryNotAvailableException {
        return contentRepository.loadFile(content);
    }

    public void delete(EnterpriseContent obj) {
        try {
            contentRepository.delete(obj);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public List<EnterpriseContent> getByContentType(ContentType contentType, String language) {
        return null;
    }

    public List<EnterpriseContent> search(int pageSize, int pageNumber, String searchTerm) {
        if (pageNumber == 0) {
            pageNumber = 1;
        }
        if (pageSize == 0) {
            pageSize = 10;
        }

        List<EnterpriseContent> searchContent = new ArrayList<>();
        try {
            searchContent = contentRepository.searchContent(pageSize, pageNumber, searchTerm, true);
        } catch (ContentRepositoryNotAvailableException e) {
            log.error("Error searching", e);
        }

        return searchContent;
    }

    public List<EnterpriseContent> getByContentType(ContentType contentType) {
        return getByContentType(contentType, false);
    }

    public List<EnterpriseContent> getByContentType(ContentType contentType, boolean includeSystemContent) {
        List<EnterpriseContent> results = new ArrayList<>();
        try {
            results.addAll(contentRepository.getByContentType(contentType, true, includeSystemContent));
        } catch (ContentRepositoryNotAvailableException e) {
            log.error("Error calling getByContentType", e);
        }

        return results;
    }

    public TreeNode getDocumentTreeModel() {
        return contentRepository.getDocumentsTreeModel();
    }

    public ContentFactory getFactory() {
        return factory;
    }

    public String getNotificationIdByType(MailType type) {
        if (type == MailType.CONFIRMATION) {
            return "email-1";
        } else if (type == MailType.RESET_PASSWORD) {
            return "email-2";
        } else if (type == MailType.CHANGE_EMAIL) {
            return "email-3";
        } else if (type == MailType.REQUEST_CONTEXT) {
            return "email-4";
        } else if (type == MailType.REQUEST_CONTEXT_ACCEPT) {
            return "email-5";
        } else if (type == MailType.REQUEST_CONTEXT_DENIED) {
            return "email-6";
        }
        return null;
    }

    @Override
    public List<EnterpriseContent> getByViewIdLanguage(String viewId, boolean activeOnly, String language) {
        return null;
    }
}
