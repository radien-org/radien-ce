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

package io.radien.ms.ecm.seed;

import io.radien.api.model.i18n.SystemI18NProperty;
import io.radien.api.service.ecm.ContentServiceAccess;
import io.radien.api.service.ecm.model.*;
import io.radien.api.service.i18n.I18NServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.ecm.ContentRepository;
import io.radien.ms.ecm.config.ConfigHandler;
import io.radien.ms.ecm.constants.CmsProperties;
import io.radien.ms.ecm.domain.ContentDataProvider;
import io.radien.ms.ecm.domain.TranslationDataProvider;
import io.radien.ms.ecm.event.ApplicationInitializedEvent;
import java.util.Arrays;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.annotation.PostConstruct;
import javax.ejb.Asynchronous;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.jcr.RepositoryException;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public @ApplicationScoped class ECMSeeder {
    private static final Logger log = LoggerFactory.getLogger(ECMSeeder.class);

    @Inject
    private ConfigHandler configHandler;
    @Inject
    private ContentRepository repository;
    @Inject
    private ContentServiceAccess contentService;
    @Inject
    private ContentDataProvider contentDataProvider;
    @Inject
    private I18NServiceAccess i18NServiceAccess;

    private String[] supportedClients;
    private String supportedLanguages;
    private String defaultLanguage;

    @PostConstruct
    public void init() {
        log.info("Initializing CMS");
        if (Boolean.parseBoolean(configHandler.getSeedContent())) {
            supportedClients = configHandler.getSupportedClients().split(",");
            supportedLanguages = configHandler.getSupportedLanguages();
            defaultLanguage = configHandler.getDefaultLanguage();
            boolean insertOnly = Boolean.parseBoolean(configHandler.getSeedInsertOnly());
            initStructure(supportedClients);
            initContent(insertOnly);
            initI18NProperties();
        }
    }

    private void initI18NProperties() {
        TranslationDataProvider translationDataProvider = new TranslationDataProvider(supportedLanguages, defaultLanguage);
        List<SystemI18NProperty> allProperties = translationDataProvider.getAllProperties();
        for (SystemI18NProperty allProperty : allProperties) {
            try {
                i18NServiceAccess.save(allProperty);
            } catch (SystemException e) {
                log.warn("Error saving {} for {}", allProperty.getKey(), allProperty.getApplication(), e);
            }
        }
    }

    private void initStructure(String[] supportedClients) {
        try {
            repository.registerCNDNodeTypes(CmsProperties.OAF_NODE_TYPES.propKey());

            for(String client : supportedClients) {
                EnterpriseContent rootNode;
                EnterpriseContent oafPropertiesContent = null;
                EnterpriseContent oafHTMLContent = null;

                rootNode = initRootNode(client);
                initPropertiesContent(client, rootNode, oafPropertiesContent);
                initHTMLContentNode(client, rootNode, oafHTMLContent);
                initDocumentsNode(client, rootNode);
            }

        } catch (Exception e) {
            log.error("Error processing new content", e);
        }
    }

    @NotNull
    private EnterpriseContent initRootNode(String client) throws RepositoryException {
        EnterpriseContent rootNode = null;
        List<EnterpriseContent> byViewIdLanguage = contentService.getByViewIdLanguage(configHandler.getRootNode(client), false, null);

        if (byViewIdLanguage != null && !byViewIdLanguage.isEmpty()) {
            rootNode = byViewIdLanguage.get(0);
        }
        if (rootNode == null || rootNode.getContentType().equals(ContentType.ERROR)) {

            log.info("[CMS] : ENABLED : Content Repository Root Node initialization");
            rootNode = new Folder(configHandler.getRootNode(client));
            rootNode.setParentPath(repository.getRootNodePath());
            rootNode.setViewId(rootNode.getName());

            contentService.save(client, rootNode);

            log.info("[CMS] : ROOT NODE : INITIALIZED {}", rootNode);

        } else {
            log.info("[CMS] : ENABLED : Content Repository Root Node already initialized");

        }
        return rootNode;
    }

    private void initDocumentsNode(String client, EnterpriseContent rootNode) throws RepositoryException {
        EnterpriseContent documentsNode = contentService.getFirstByViewIdLanguage(configHandler.getDocumentsNode(client), false, null);
        if (documentsNode == null || documentsNode.getContentType().equals(ContentType.ERROR)) {
            log.info("[CMS] : ENABLED : Content Repository Documents Node initialization");

            documentsNode = new Folder(configHandler.getDocumentsNode(client));
            documentsNode.setParentPath(rootNode.getJcrPath());
            documentsNode.setViewId(documentsNode.getName());

            contentService.save(client, documentsNode);

            log.info("[CMS] : DOCUMENTS NODE : INITIALIZED {}", documentsNode);
        } else {
            log.info("[CMS] : ENABLED : Content Repository Documents Node already initialized; Checking for updated locales.");
            repository.updateFolderSupportedLanguages(client, documentsNode.getParentPath(), configHandler.getDocumentsNode(client));
        }
        autoCreateDocumentFolders(client, documentsNode);
    }

    private void autoCreateDocumentFolders(String client, EnterpriseContent documentsNode) {
        String[] folderNames = configHandler.getAutoCreateNodes().split(",");
        List<String> children = contentService.getChildrenFiles(documentsNode.getViewId())
                .stream().filter(ec -> ec.getContentType() == ContentType.FOLDER).map(EnterpriseContent::getName).collect(Collectors.toList());
        for (String folderName : folderNames) {
            if (!children.contains(folderName)) {
                EnterpriseContent folder = new Folder(folderName);
                folder.setParentPath(documentsNode.getJcrPath());
                folder.setViewId(folderName + UUID.randomUUID());
                contentService.save(client, folder);
                log.info("[CMS] folder created: {}", folder);
            }
        }
    }

    private void initPropertiesContent(String client, EnterpriseContent rootNode, EnterpriseContent oafPropertiesContent) {
        List<EnterpriseContent> tmpContent = contentService.getByViewIdLanguage(configHandler.getPropertiesNode(), false, "");

        if (tmpContent != null && !tmpContent.isEmpty()) {
            oafPropertiesContent = tmpContent.get(0);
        }

        if (oafPropertiesContent == null || oafPropertiesContent.getContentType().equals(ContentType.ERROR)) {
            log.info("[CMS] : ENABLED : Content Repository Properties Node initialization");

            oafPropertiesContent = new Folder(configHandler.getPropertiesNode());
            oafPropertiesContent.setParentPath(rootNode.getJcrPath());
            oafPropertiesContent.setViewId(oafPropertiesContent.getName());

            contentService.save(client, oafPropertiesContent);
            log.info("[CMS] : PROPERTIES NODE : INITIALIZED {}", oafPropertiesContent);
        } else {
            log.info("[CMS] : ENABLED : Content Repository PROPERTIES Node already initialized;.");
        }
    }

    private void initHTMLContentNode(String client, EnterpriseContent rootNode, EnterpriseContent oafHTMLContent) throws RepositoryException {
        List<EnterpriseContent> tmpContent = contentService.getByViewIdLanguage(configHandler.getHtmlNode(client), false, "");

        if (tmpContent != null && !tmpContent.isEmpty()) {
            oafHTMLContent = tmpContent.get(0);
        }

        if (oafHTMLContent == null || oafHTMLContent.getContentType().equals(ContentType.ERROR)) {
            log.info("[CMS] : ENABLED : Content Repository HTML Node initialization");

            oafHTMLContent = new Folder(configHandler.getHtmlNode(client));
            oafHTMLContent.setParentPath(rootNode.getJcrPath());
            oafHTMLContent.setViewId(oafHTMLContent.getName());

            contentService.save(client, oafHTMLContent);
            log.info("[CMS] : HTML NODE : INITIALIZED {}", oafHTMLContent);
        } else {
            log.info("[CMS] : ENABLED : Content Repository HTML Node already initialized; Checking for updated locales.");
            repository.updateFolderSupportedLanguages(client, oafHTMLContent.getParentPath(), configHandler.getHtmlNode(client));
        }
    }

    /**
     * Seeds all the content present in this project json files
     */
    private void initContent(boolean insertOnly) {
        for (EnterpriseContent content : contentDataProvider.getContents()) {
            String viewId = content.getViewId();
            String language = content.getLanguage();
            String client = content.getParentPath() != null ? content.getParentPath().split("/")[1] : configHandler.getDefaultClient();
            if(Arrays.stream(supportedClients).noneMatch(c -> c.equals(client))) {
                log.warn("Skipped content for client {}, not supported", client);
                continue;
            }
            try {
                content.setLastEditDate(new Date());
                content.setAuthor("System");
                List<EnterpriseContent> exists = contentService.getByViewIdLanguage(content.getViewId(), false, language);
                if (exists != null && !exists.isEmpty() && content.getLanguage().equals(exists.get(exists.size() - 1).getLanguage())) {
                    EnterpriseContent rootVersion = exists.get(exists.size() - 1);
                    if (shouldUpdateContent(content, rootVersion)) {
                        contentService.save(client, content);
                        log.info("Updated content with viewID {}", content.getViewId());
                    }
                } else {
                    contentService.save(client, content);
                    log.info("Saved new content with viewID {}", content.getViewId());
                }
            } catch (Exception e) {
                log.error("Error seeding viewId: {} with language: {}",viewId,language, e);
            }
        }
    }

    private boolean shouldUpdateContent(EnterpriseContent content, EnterpriseContent rootVersion) {
        if (rootVersion == null || rootVersion.getContentType().equals(ContentType.ERROR)
                || (rootVersion.getContentType() != ContentType.TAG && !rootVersion.getParentPath().equals(content.getParentPath()))) {
            return true;
        }
        else {
            if (content instanceof SystemVersionableEnterpriseContent) {
                return ((SystemVersionableEnterpriseContent) content).getVersion().compareTo(((SystemVersionableEnterpriseContent) rootVersion).getVersion()) > 0;
            } else {
                return true;
            }
        }
    }

    @Asynchronous
    public void init(@Observes ApplicationInitializedEvent event) {
        log.info("{} - received", event.getClass().getSimpleName());
    }

}
