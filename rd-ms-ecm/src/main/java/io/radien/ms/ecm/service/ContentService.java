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

import io.radien.api.service.ecm.ContentServiceAccess;
import io.radien.api.service.ecm.exception.ContentException;
import io.radien.api.service.ecm.exception.ContentNotAvailableException;
import io.radien.api.service.ecm.exception.ElementNotFoundException;
import io.radien.api.service.ecm.exception.InvalidClientException;
import io.radien.api.service.ecm.exception.NameNotValidException;
import io.radien.api.service.ecm.model.ContentType;
import io.radien.api.service.ecm.model.EnterpriseContent;
import io.radien.api.service.ecm.model.GenericEnterpriseContent;
import io.radien.api.service.ecm.model.SystemContentVersion;
import io.radien.api.service.mail.model.MailType;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.ms.ecm.config.ConfigHandler;
import io.radien.ms.ecm.ContentRepository;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.jcr.RepositoryException;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.Date;

/**
 * Default implementation of the ContentService using JackRabbit
 *
 * @author Bruno Gama
 */
@Stateless
public class ContentService implements ContentServiceAccess {

    private static final Logger log = LoggerFactory.getLogger(ContentService.class);
    private static final long serialVersionUID = 8354030307902734111L;

    @Inject
    private ContentRepository contentRepository;

    @Inject
    private ConfigHandler configHandler;

    private String defaultLanguage;
    private String[] availableClients;

    @PostConstruct
    public void init() {
        defaultLanguage = configHandler.getDefaultLanguage();
        availableClients = configHandler.getSupportedClients().split(",");
    }

    public List<EnterpriseContent> getChildrenFiles(String viewId) {
        try {
            return new ArrayList<>(contentRepository.getChildren(viewId));
        } catch (RepositoryException | ElementNotFoundException e) {
            throw new ContentException("Error getting children files", e);
        }
    }

    @Override
    public EnterpriseContent loadFile(String jcrPath) {
        try {
            return contentRepository.loadFile(jcrPath);
        } catch (RepositoryException e) {
            throw new ContentNotAvailableException("Error loading EnterpriseContent File", e,
                    Response.Status.INTERNAL_SERVER_ERROR);
        } catch (IOException e) {
            throw new ContentException("Error loading file", e,
                    Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<EnterpriseContent> getFolderContents(String path) {
        try {
            return contentRepository.getFolderContents(path);
        } catch(RepositoryException e) {
            throw new ContentNotAvailableException("Could not retrieve folder contents for " + path, e);
        }
    }

    @Override
    public List<EnterpriseContent> getContentVersions(String path) {
        try {
            return contentRepository.getContentVersions(path);
        } catch(RepositoryException e) {
            throw new ContentException("Could not retrieve content versions", e,
                    Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void deleteVersion(String path, SystemContentVersion version) {
        try {
            int records = contentRepository.deleteVersion(path, version);
            if(records == 0) {
                throw new ContentNotAvailableException(MessageFormat.format("Version {0} not found in {1}", version.getVersion(), path),
                        Response.Status.NOT_FOUND);
            }
        } catch (RepositoryException e) {
            throw new ContentException(MessageFormat.format("Could not delete version {0} in path {1}", version.getVersion(), path), e);        }
    }

    @Override
    public String getOrCreateDocumentsPath(String client, String path) {
        if(validateClient(client)) {
            try {
                return contentRepository.getOrCreateDocumentsPath(client, path);
            } catch (RepositoryException e) {
                throw new ContentException("Could not generate path provided", e,
                        Response.Status.INTERNAL_SERVER_ERROR);
            }
        } else {
            throw new InvalidClientException("Provided Client " + client + " is not valid.",
                    Response.Status.BAD_REQUEST);
        }
    }

    public void save(String client, EnterpriseContent obj) {
        if(validateClient(client)) {
            try {
                contentRepository.save(client, obj);
            } catch (RepositoryException e) {
                throw new ContentNotAvailableException("Error saving object", e, Response.Status.INTERNAL_SERVER_ERROR);
            }
        } else {
            throw new InvalidClientException("Provided Client " + client + " is not valid.",
                    Response.Status.BAD_REQUEST);
        }
    }

    public void delete(EnterpriseContent obj) {
       delete(obj.getJcrPath());
    }

    public void delete(String path) {
        try {
            contentRepository.delete(path);
        } catch (RepositoryException e) {
            throw new ContentNotAvailableException(MessageFormat.format("Could not delete enterprise content in path {0}", path), e,
                    Response.Status.INTERNAL_SERVER_ERROR);
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
        } else if(type == MailType.REQUEST_GDPR_DATA_OPT_IN) {
            return "email-7";
        }else if(type == MailType.DATA_MANIPULATION){
            return "email-11";
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
        } catch (RepositoryException e) {
            throw new ContentNotAvailableException(
                    GenericErrorCodeMessage.NOT_FOUND_VIEWID_LANGUAGE.toString(viewId, language),
                    e,
                    Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean validateClient(String client) {
        return Arrays.asList(availableClients).contains(client);
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
}
