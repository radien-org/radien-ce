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
 *
 */
package io.radien.ms.ecm.service;

import com.fasterxml.jackson.core.TreeNode;
import io.radien.api.service.ecm.ContentServiceAccess;
import io.radien.api.service.ecm.exception.ContentNotAvailableException;
import io.radien.api.service.ecm.exception.ContentRepositoryNotAvailableException;
import io.radien.api.service.ecm.exception.ElementNotFoundException;
import io.radien.api.service.ecm.exception.NameNotValidException;
import io.radien.api.service.ecm.model.ContentType;
import io.radien.api.service.ecm.model.ContentVersion;
import io.radien.api.service.ecm.model.EnterpriseContent;
import io.radien.api.service.ecm.model.GenericEnterpriseContent;
import io.radien.api.service.ecm.model.SystemContentVersion;
import io.radien.api.service.mail.model.MailType;
import io.radien.exception.SystemException;
import io.radien.ms.ecm.util.ContentMappingUtils;
import io.radien.ms.ecm.ContentRepository;
import java.text.MessageFormat;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.jcr.RepositoryException;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Date;

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
    private ContentMappingUtils factory;

    @Inject
    private ContentRepository contentRepository;

    @Inject
    @ConfigProperty(name = "system.default.language")
    private String defaultLanguage;

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
    public EnterpriseContent loadFile(String jcrPath) throws ContentRepositoryNotAvailableException {
        return contentRepository.loadFile(jcrPath);
    }

    @Override
    public List<EnterpriseContent> getFolderContents(String path) throws ContentRepositoryNotAvailableException, ContentNotAvailableException {
        try {
            return contentRepository.getFolderContents(path);
        } catch(RepositoryException e) {
            throw new ContentNotAvailableException("Could not retriede folder contents", e);
        }
    }

    @Override
    public List<EnterpriseContent> getContentVersions(String path) throws ContentRepositoryNotAvailableException, ContentNotAvailableException {
        try {
            return contentRepository.getContentVersions(path);
        } catch(RepositoryException e) {
            throw new ContentNotAvailableException("Could not retrieve content versions", e);
        }
    }

    @Override
    public void deleteVersion(String path, SystemContentVersion version) throws ContentRepositoryNotAvailableException, ContentNotAvailableException {
        try {
            contentRepository.deleteVersion(path, version);
        } catch (RepositoryException e) {
            throw new IllegalStateException(MessageFormat.format("Could not delete version {0} in path {1}", version.getVersion(), path), e);        }
    }

    @Override
    public String getOrCreateDocumentsPath(String path) throws ContentRepositoryNotAvailableException, ContentNotAvailableException{
        try {
            return contentRepository.getOrCreateDocumentsPath(path);
        } catch (RepositoryException e) {
            throw new ContentNotAvailableException("Error on getOrCreateDocumentsPath", e);
        }
    }

    public void save(EnterpriseContent obj) throws ContentRepositoryNotAvailableException, ContentNotAvailableException {
        try {
            contentRepository.save(obj);
        } catch (RepositoryException e) {
            throw new ContentNotAvailableException("Error saving object", e);
        }
    }

    public void delete(EnterpriseContent obj) throws ContentRepositoryNotAvailableException, ContentNotAvailableException {
       delete(obj.getJcrPath());
    }

    public void delete(String path) throws ContentRepositoryNotAvailableException, ContentNotAvailableException {
        try {
            contentRepository.delete(path);
        } catch (RepositoryException e) {
            throw new ContentNotAvailableException(MessageFormat.format("Could not delete enterprise content in path {0}", path), e);
        }
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
        Optional<List<EnterpriseContent>> contentList;
        try {
            contentList = contentRepository.getByViewIdLanguage(viewId, activeOnly, language);

            if (contentList.isPresent() && !contentList.get().isEmpty()) {
                return contentList.get();
            } else {
                log.warn("[ContentService] : Error on retrieving content with viewId: {} and language {} - we are trying to get that content with default language: {}",viewId, language, defaultLanguage);
                contentList = contentRepository.getByViewIdLanguage(viewId, activeOnly, defaultLanguage);

                if (contentList.isPresent() && !contentList.get().isEmpty()) {
                    return contentList.get();
                } else {
                    log.error("[ContentService] : Error on retrieving default content with viewId: {} and language {}",viewId, defaultLanguage);

                    List<EnterpriseContent> list = new ArrayList<>();
                    list.add(createErrorContent(viewId, language));
                    return list;
                }
            }
        } catch (ContentRepositoryNotAvailableException e) {
            log.error("CMS not available!", e);
        }

        List<EnterpriseContent> list = new ArrayList<>();
        list.add(createErrorContent(viewId, language));
        return list;
    }

    private EnterpriseContent createErrorContent(String viewId, String language) {
        log.error("content not found: viewId={} in {}", viewId, language);

        EnterpriseContent content = null;
        try {
            content = new GenericEnterpriseContent("<ERROR> Content not found");
            content.setContentType(ContentType.ERROR);
            content.setViewId(UUID.randomUUID().toString());
            content.setHtmlContent("Oooops! Something went wrong. The elemt which should be displayed is not found, and there is not fallbackelement. The Support has been informed about that matter!");
            content.setCreateDate(new Date());
        } catch (NameNotValidException e) {
            log.error("Could not create ERROR Content Type", e);
        }
        return content;
    }

    //region Methods not implemented
    @Override
    public String getNotificationIdByTypeAndLanguage(MailType type, String languageCode) {
        //TODO Define Method
        return null;
    }

    @Override
    public String getAppDesc(String app, String language) {
        //TODO Check if needed
        return null;
    }

    @Override
    public int countByTagName(String name) {
        //TODO Check if needed
        return 0;
    }

    public TreeNode getDocumentTreeModel() {
        //TODO Check if needed
        return contentRepository.getDocumentsTreeModel();
    }

    @Override
    public List<EnterpriseContent> getByContentType(ContentType contentType, String language) {
        //TODO Define Method
        return null;
    }

    public List<EnterpriseContent> search(int pageSize, int pageNumber, String searchTerm) {
        //TODO Check if needed
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
    //endregion
}
